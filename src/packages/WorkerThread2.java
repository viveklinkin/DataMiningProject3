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
public class WorkerThread2 implements Runnable {

    double w[];
    double lambda;
    double[] y;
    vector[] xi;
    double[] l2;
    int[] order;
    CSR X;
    int Cl;
    static final double stopping = 0.0001;

    public WorkerThread2(CSR X, double[] w, double lambda, int Cl, double[] y,
            vector[] xi, double[] l2, int[] order) {
        this.X = X;
        this.w = w;
        this.lambda = lambda;
        this.y = y;
        this.xi = xi;
        this.l2 = l2;
        this.order = order;
        this.Cl = Cl;
    }

    public void run() {
        double err = -1;
        while (true) {
            for (int currentW : order) {
                double temp = w[currentW];
                w[currentW] = 0;
                double numer = CSR.vectormult(xi[currentW],
                        CSR.matsub(norm(CSR.multiply(X, w)), y));
                double denom = l2[currentW] + lambda;
                w[currentW] = numer / denom;
            }

            double currerr = geterr(X, w, y);
            System.out.println("Cl:" + Cl + " lambda:" + lambda + " error: "
                    + currerr);

            if (err != -1) {
                if (Math.abs(err - currerr) / err <= stopping) {
                    break;
                }
            }
            err = currerr;
        }
    }

    private void processmessage() {

    }

    static double geterr(CSR X, double[] w, double[] y) {
        double[] ans = CSR.matsub(y, norm(CSR.multiply(X, CSR.toVector(w))));
        double res = 0;
        for (double x : ans) {
            res += x * x;
        }
        return res;
    }

    static double[] norm(double[] a) {
        for (int i = 0; i < a.length; i++) {
            a[i] = (a[i] >= 1) ? a[i] : 0;
        }
        return a;
    }
}
