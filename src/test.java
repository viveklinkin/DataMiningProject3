
import packages.CSR;
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
public class test {

    static double[] mydouble = new double[]{1, 0, 1, 0, 0, 0, 0, 0, 0, 3};
    static double[] mydouble2 = new double[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 2};

    public static void main(String args[]) {
        double res = CSR.vectormult(CSR.toVector(mydouble), CSR.toVector(mydouble2));

        System.out.println(res);
    }
}
