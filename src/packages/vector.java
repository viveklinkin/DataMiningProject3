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
public class vector {

    double[] val;
    int[] col_ind;

    vector(int i) {
        val = new double[i];
        col_ind = new int[i];
    }

    int getSize() {
        return val.length;
    }
}
