/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packages;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author vivek
 */
public class Point {

    public static DecimalFormat df;
    int number;
    int label;
    float[] dimension;
    float splValue;

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public float getSplValue() {
        return splValue;
    }

    public void setSplValue(float splValue) {
        this.splValue = splValue;
    }

    public Point(String s, int number) {
        List<String> d1 = Arrays.asList(s.split(","));
        df = new DecimalFormat("#.#####");
        dimension = new float[d1.size() - 1];
        Iterator iter = d1.iterator();
        label = Integer.parseInt((String) iter.next());
        int counter = 0;
        while (iter.hasNext()) {
            dimension[counter] = Float.parseFloat((String) iter.next());
            counter++;
        }
        this.number = number;
    }

    public int length() {
        return dimension.length;
    }

    public float get(int x) {
        return dimension[x];
    }
}
