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
						CSR.matsub(y, norm(CSR.multiply(X, w))));
				double denom = l2[currentW] + lambda;
				w[currentW] = numer / denom;
			}

			double currerr = geterr(X, w, y, lambda);
			System.out.println("Cl:" + Cl + "\t lambda:" + lambda
					+ "\t error: " + currerr);

			if (err != -1) {
				if ((err - currerr) / err <= stopping || err == 0) {
					break;
				}
			}
			err = currerr;
		}
		System.out.println("Converged for: " + Cl + "," + lambda);
	}

	static double geterr(CSR X, double[] w, double[] y, double lambda) {
		double[] ans = CSR.matsub(y, norm(CSR.multiply(X, CSR.toVector(w))));
		double res = CSR.vectormult(ans, ans)
				+ (lambda * (CSR.vectormult(w, w)));

		return res;
	}

	static double[] norm(double[] a) {
		// for (int i = 0; i < a.length; i++) {
		// a[i] = (a[i] >= 0.5) ? a[i] : 0;
		// }
		return a;
	}
}
