/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packages;

/**
 *
 * @author vivek
 */
public class CSR {

    vector[] v;

    public CSR(int i) {
        v = new vector[i];
    }

    public int size() {
        return v.length;
    }

    public static CSR toCSR(double[][] a) {
        CSR res = new CSR(a.length);

        for (int i = 0; i < a.length; i++) {
            res.v[i] = toVector(a[i]);
        }

        return res;
    }

    public static vector toVector(double[] a) {
        int nn0 = 0;
        for (double x : a) {
            if (x != 0) {
                nn0++;
            }
        }
        vector res = new vector(nn0);
        int ind = 0;
        for (int i = 0; i < a.length; i++) {
            if (ind == nn0) {
                break;
            }
            if (a[i] != 0) {
                res.col_ind[ind] = i;
                res.val[ind] = a[i];
                ind++;
            }

        }
        return res;
    }

    public static double vectormult(double[] a, double[] b) {
        double res = 0;
        for (int i = 0; i < a.length; i++) {
            res += a[i] * b[i];
        }
        return res;
    }

    public static double vectormult(vector a, double[] b) {
        double res = 0;
        for (int i = 0; i < a.getSize(); i++) {
            res += b[a.col_ind[i]] * a.val[i];
        }
        return res;
    }

    public static double[] multiply(CSR a, double[] b) {
        double[] res = new double[a.size()];

        for (int i = 0; i < a.size(); i++) {
            res[i] = vectormult(a.v[i], b);
        }

        return res;
    }

    public static double[] multiply(CSR a, vector b) {
        double[] res = new double[a.size()];

        for (int i = 0; i < a.size(); i++) {
            res[i] = vectormult(a.v[i], b);
        }

        return res;
    }

    public static double vectormult(vector a, vector b) {
        double res = 0;
        int walkera = 0;
        int walkerb = 0;

        while (walkera < a.getSize() && walkerb < b.getSize()) {
            if (a.col_ind[walkera] == b.col_ind[walkerb]) {
                res += a.val[walkera] * b.val[walkerb];
                walkera++;
                walkerb++;
            } else if (a.col_ind[walkera] < b.col_ind[walkerb]) {
                walkera++;
            } else {
                walkerb++;
            }
        }
        return res;
    }

    public static double[][] getTranspose(double[][] a) {
        double[][] b = new double[a[0].length][a.length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                b[j][i] = a[i][j];
            }
        }
        return b;
    }

    public static double[] matsub(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new RuntimeException("MATSUB: length of the vectors are not the same");
        }
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    public static double[] getColumn(double[][] a, int col) {
        double[] b = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i][col];
        }
        return b;
    }

    public static double[][] dropColumn(double[][] a, int col) {
        double[][] b = new double[a.length][a[0].length - 1];
        for (int i = 0; i < a.length; i++) {
            int ind = 0;
            for (int j = 0; j < a[i].length; j++) {
                if (j == col) {
                    continue;
                }
                b[i][ind] = a[i][j];
                ind++;
            }
        }
        return b;
    }

    public static double[] dropColumn(double[] a, int col) {
        double[] b = new double[a.length - 1];
        int ind = 0;
        for (int j = 0; j < a.length - 1; j++) {
            if (j == col) {
                continue;
            }
            b[ind] = a[j];
            ind++;
        }
        return b;
    }
}
