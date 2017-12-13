import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import packages.CSR;
import packages.FileOps;
import packages.WorkerThread;
import packages.vector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * 
 * @author vivek
 */
public class regression {

	static String testFile = "project_3/rep2/mnist_test.csv";
	static String trainFile = "project_3/rep2/mnist_train.csv";
	static String valFile = "project_3/rep2/mnist_validation.csv";
	static String outputFile = "";
	static String options = "";

	static double[] lambdas = new double[] { 0.01, 0.05, 0.1, 0.5, 1.0, 2.0,
			5.0 };

	static double stopping = 0.0001;

	// 0:train-file, 1:validation-file, 2:test-file, 3:output-file 4:[options]
	static void setVals(String args[]) {
		trainFile = args[0];
		valFile = args[1];
		testFile = args[2];
		outputFile = args[3];
		options = args[4];
	}

	public static void main(String[] args) {

		if (args.length != 0) {
			setVals(args);
		}

		long starting = System.currentTimeMillis();
		System.out.println("Fetching");
		double[][] testSet = FileOps.getNormalisedCSVContentAsMatrix(testFile);
		double[][] trainSet = FileOps.getNormalisedCSVContentAsMatrix(trainFile);
		double[][] valSet = FileOps.getNormalisedCSVContentAsMatrix(valFile);

		double[][] trainingvals = CSR.dropColumn(trainSet, 0);
		int[] order = permute(trainingvals[0].length);

		CSR X = CSR.toCSR(trainingvals);
		double weights[][][] = new double[lambdas.length][10][trainingvals[0].length];

		vector[] xi = getxis(trainingvals);
		double l2[] = new double[xi.length];

		for (int i = 0; i < xi.length; i++) {
			l2[i] = CSR.vectormult(xi[i], xi[i]);
		}

		for (int i = 0; i < lambdas.length; i++) {
			for (int j = 0; j < 10; j++) {
				weights[i][j] = fillrands(weights[i][j]);
			}
		}
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
				.newCachedThreadPool();

		for (int i = 0; i < lambdas.length; i++) {
			for (int Cl = 0; Cl < 10; Cl++) {

				WorkerThread task = new WorkerThread(X, weights[i][Cl],
						lambdas[i], Cl, getYVals(Cl, trainSet), xi, l2, order);
				executor.execute(task);
			}
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}

		double bestacc = -1;
		int bestlambda = -1;
		for (int i = 0; i < lambdas.length; i++) {
			double temp = validate(weights[i], valSet);
			if (temp > bestacc) {
				bestacc = temp;
				bestlambda = i;
			}
		}

		System.out.println("BESTLambda: " + lambdas[bestlambda]);
		System.out.println("BESTACC: " + bestacc);
		System.out.println("time: "
				+ ((double) (System.currentTimeMillis() - starting)) / 1000.0
				+ " ms");

		List<String> outputweights = new ArrayList<>();
		for (int i = 0; i < weights[bestlambda].length; i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < weights[bestlambda][i].length; j++) {
				sb.append(weights[bestlambda][i][j] + ",");
			}
			sb.deleteCharAt(sb.length() - 1);
			outputweights.add(sb.toString());
		}

		for (int i = 0; i < weights[bestlambda].length; i++) {
			for (int j = 0; j < weights[bestlambda][i].length; j++) {
				// System.out.print(weights[bestlambda][i][j] + ",");
			}
			// System.out.println("\n");
		}

		FileOps.writeFile(options, outputweights);

	}

	static double validate(double[][] w, double[][] valSet) {
		double[][] validationarray = CSR.dropColumn(valSet, 0);
		double score = 0;
		for (int j = 0; j < validationarray.length; j++) {
			double[] currentarr = validationarray[j];
			double argmax = -1;
			double max = -1;
			for (int i = 0; i < 10; i++) {
				if (CSR.vectormult(currentarr, w[i]) > max) {
					max = CSR.vectormult(currentarr, w[i]);
					argmax = i;
				}
			}
			if (argmax == valSet[j][0]) {
				score++;
			}
		}
		return score / (double) valSet.length;
	}

	static double[] norm(double[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = (a[i] >= 0) ? 1 : -1;
		}
		return a;
	}

	static double geterr(CSR X, double[] w, double[] y) {
		double[] ans = CSR.matsub(y, norm(CSR.multiply(X, CSR.toVector(w))));
		double res = 0;
		for (double x : ans) {
			res += x * x;
		}
		return res;
	}

	static int[] permute(int length) {
		int[] output = new int[length];
		List<Integer> picker = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			picker.add(i);
		}
		Random r = new Random();
		for (int i = 0; i < length; i++) {
			int x = r.nextInt(picker.size());
			int y = picker.get(x);
			picker.remove(x);
			output[i] = y;
		}
		return output;
	}

	static CSR[] getXminuses(double[][] vals) {
		CSR[] res = new CSR[vals[0].length];

		for (int i = 0; i < vals[0].length; i++) {
			res[i] = CSR.toCSR(CSR.dropColumn(vals, i));
		}
		return res;
	}

	static double[] fillrands(double[] f) {
		Random rand = new Random();
		for (int i = 0; i < f.length; i++) {
			f[i] = rand.nextDouble() * ((rand.nextInt(1) == 0) ? 1 : -1)
					/ 1000.0;
		}
		return f;
	}

	static final double[] getYVals(int a, double[][] b) {
		double[] res = new double[b.length];
		for (int i = 0; i < b.length; i++) {
			res[i] = (b[i][0] == a) ? 1 : -1;
		}
		return res;
	}

	static vector[] getxis(double[][] a) {
		vector[] output = new vector[a[0].length];
		for (int j = 0; j < a[0].length; j++) {
			double[] line = new double[a.length];
			for (int i = 0; i < a.length; i++) {
				line[i] = a[i][j];
			}
			output[j] = CSR.toVector(line);
		}
		return output;
	}
}
