
import java.util.*;
import packages.CSR;
import packages.FileOps;
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

    static String testFile = "project_3/rep1/mnist_test.csv";
    static String trainFile = "project_3/rep1/mnist_train.csv";
    static String valFile = "project_3/rep1/mnist_validation.csv";

    static double[] lambdas = new double[]{0.01, 0.05, 0.1, 0.5, 1.0, 2.0, 5.0};
    static double stopping = 0.001;

    public static void main(String[] args) {
        System.out.println("Fetching");
        double[][] testSet = FileOps.getCSVContentAsMatrix(testFile);
        double[][] trainSet = FileOps.getCSVContentAsMatrix(trainFile);
        double[][] valSet = FileOps.getCSVContentAsMatrix(valFile);

        double[][] trainingvals = CSR.dropColumn(trainSet, 0);
        int[] order = permute(trainingvals[0].length);

        CSR X = CSR.toCSR(trainingvals);
        double weights[][] = new double[10][trainingvals[0].length];

        vector[] xi = getxis(trainingvals);

        double bestlambda = -1;
        double bestacc = -1;
        for (double lambda : lambdas) {
            System.out.println("training for lambda = " + lambda);

            for (int Cl = 0; Cl < 10; Cl++) {
                weights[Cl] = fillrands(weights[Cl]);
                System.out.println(Cl);
                final double[] y = getYVals(Cl, trainSet);
                double err = -1;
                while (true) {
                    for (int currentW : order) {
                        double temp = weights[Cl][currentW];
                        weights[Cl][currentW] = 0;
                        double numer = CSR.vectormult(xi[currentW],
                                CSR.matsub(y, CSR.multiply(X, weights[Cl])));
                        double denom = CSR.vectormult(xi[currentW], xi[currentW]) + lambda;
                        weights[Cl][currentW] = numer / denom;
                    }

                    double currerr = geterr(X, weights[Cl], y);
                    System.out.println("error: " + currerr);

                    if (err != -1) {
                        if ((err - currerr) / err <= stopping) {
                            break;
                        }
                    }
                    err = currerr;
                }
            }
            System.out.println("validating");
            System.out.println(validate(weights, valSet));
        }
        System.out.println();
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

//    static double[] norm(double[] a) {
//        for (int i = 0; i < a.length; i++) {
//            a[i] = (a[i] / 100) - 2;
//        }
//        return a;
//    }
    static double geterr(CSR X, double[] w, double[] y) {
        double[] ans = CSR.matsub(y, CSR.multiply(X, CSR.toVector(w)));
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
            f[i] = rand.nextDouble() * ((rand.nextInt(1) == 0) ? 1 : -1) / 1000.0;
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
