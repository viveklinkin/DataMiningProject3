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

	static String testFile = "/home/vaidy083/workspace/DMAssignment3/project_3/rep2/mnist_test.csv";
	static String trainFile = "/home/vaidy083/workspace/DMAssignment3/project_3/rep2/mnist_train.csv";
	static String valFile = "/home/vaidy083/workspace/DMAssignment3/project_3/rep2/mnist_validation.csv";
	static String outputFile = "/home/vaidy083/workspace/DMAssignment3/output/regressionrep2classes.csv";
	static String options = "/home/vaidy083/workspace/DMAssignment3/output/regressionrep2weights.csv";

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
		System.out.println("Test Set: (" + testSet.length + ", "
				+ (testSet[0].length - 1) + ")");
		
		double[][] trainSet = FileOps
				.getNormalisedCSVContentAsMatrix(trainFile);
		System.out.println("Train Set: (" + trainSet.length + ", "
				+ (trainSet[0].length - 1) + ")");
		
		double[][] valSet = FileOps.getNormalisedCSVContentAsMatrix(valFile);
		System.out.println("Validation Set: (" + valSet.length + ", "
				+ (valSet[0].length - 1) + ")");

		double[][] trainingvals = CSR.dropColumn(trainSet, 0);
		int[] order = permute(trainingvals[0].length);

		CSR X = CSR.toCSR(trainingvals);
		double weights[][][] = new double[lambdas.length][10][trainingvals[0].length];

		System.out.println("Preprocessing");
		
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

		System.out.println("Training");
		for (int i = 0; i < lambdas.length; i++) {
			for (int Cl = 0; Cl < 10; Cl++) {

				WorkerThread task = new WorkerThread(X, weights[i][Cl],
						lambdas[i], Cl, getYVals(Cl, trainSet), xi, l2, order);
				executor.execute(task);
			}
		}
		executor.shutdown();
		while (!executor.isTerminated()) {/*.... wait ... .*/   }

		double bestacc = -1;
		int bestlambda = -1;
		for (int i = 0; i < lambdas.length; i++) {
			double temp = validate(weights[i], valSet);
			if (temp > bestacc) {
				bestacc = temp;
				bestlambda = i;
			}
		}

		System.out.println("BESTLAMBDA: " + lambdas[bestlambda]);
		System.out.println("TRAINACC: " + bestacc);
		System.out.println("time: "
				+ ((double) (System.currentTimeMillis() - starting)) / 1000.0
				+ " ms");

		double bestL = lambdas[bestlambda];
		double[][] combinedWeights1 = new double[10][trainingvals[0].length];
		double[][] combinedWeights2 = new double[10][trainingvals[0].length];

		for (int i = 0; i < combinedWeights1.length; i++) {
			combinedWeights1[i] = fillrands(combinedWeights1[i]);
			combinedWeights2[i] = fillrands(combinedWeights2[i]);
		}

		double[][] combinedSet = new double[valSet.length + trainSet.length][trainSet[0].length];

		for (int i = 0; i < trainSet.length; i++) {
			combinedSet[i] = trainSet[i];
		}
		int iter1 = trainSet.length;
		for (int i = 0; i < valSet.length; i++, iter1++) {
			combinedSet[iter1] = valSet[i];
		}

		double[][] combinedvals = CSR.dropColumn(combinedSet, 0);
		X = CSR.toCSR(combinedvals);
		xi = getxis(combinedvals);
		l2 = new double[xi.length];
		order = permute(combinedvals[0].length);

		for (int i = 0; i < xi.length; i++) {
			l2[i] = CSR.vectormult(xi[i], xi[i]);
		}

		System.out.println("Training with train + validation set and lambda = "
				+ bestL + " and " + bestL * 2);
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		for (int Cl = 0; Cl < 10; Cl++) {
			WorkerThread task1 = new WorkerThread(X, combinedWeights1[Cl],
					bestL, Cl, getYVals(Cl, combinedSet), xi, l2, order);

			WorkerThread task2 = new WorkerThread(X, combinedWeights2[Cl],
					bestL * 2, Cl, getYVals(Cl, combinedSet), xi, l2, order);

			executor.execute(task1);
			executor.execute(task2);
		}
		executor.shutdown();

		while (!executor.isTerminated()) {
		}

		double temp1 = validate(combinedWeights1, testSet);
		double temp2 = validate(combinedWeights2, testSet);

		double bestWeights[][];
		if (temp1 >= temp2) {
			bestWeights = combinedWeights1;
		} else {
			bestWeights = combinedWeights2;
			bestL *= 2;
		}

		double finalAcc = validate(bestWeights, testSet);
		System.out.println("lambda :" + bestL);

		System.out.println("ACCURACY: " + finalAcc);

		List<String> outputweights = new ArrayList<>();
		for (int i = 0; i < bestWeights.length; i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < bestWeights[i].length; j++) {
				sb.append(bestWeights[i][j] + ",");
			}
			sb.deleteCharAt(sb.length() - 1);
			outputweights.add(sb.toString());
		}

		List<String> outputclasses = new ArrayList<String>();

		for (int i = 0; i < testSet.length; i++) {
			outputclasses.add(""
					+ argmax(CSR.dropColumn(testSet[i], 0), bestWeights));
		}

		FileOps.writeFile(options, outputweights);
		FileOps.writeFile(outputFile, outputclasses);

	}

	static double validate(double[][] w, double[][] valSet) {
		double[][] validationarray = CSR.dropColumn(valSet, 0);
		double score = 0;
		for (int j = 0; j < validationarray.length; j++) {
			if (argmax(validationarray[j], w) == valSet[j][0]) {
				score++;
			}
		}
		return score / (double) valSet.length;
	}

	static int argmax(double[] currentarr, double[][] w) {
		int argmax = -1;
		double max = -1;
		for (int i = 0; i < 10; i++) {
			if (CSR.vectormult(currentarr, w[i]) > max) {
				max = CSR.vectormult(currentarr, w[i]);
				argmax = i;
			}
		}
		return argmax;

	}

	static double[] norm(double[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = (a[i] >= 0) ? 1 : -1;
		}
		return a;
	}

	static double geterr(CSR X, double[] w, double[] y, double lambda) {
		double[] ans = CSR.matsub(y, norm(CSR.multiply(X, CSR.toVector(w))));
		double res = CSR.vectormult(ans, ans)
				+ (lambda * (CSR.vectormult(w, w)));

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
			f[i] = rand.nextDouble() * ((rand.nextInt(1) == 0) ? 1 : -1);
					/// 1000.0;
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
