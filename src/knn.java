/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.*;
import packages.FileOps;
import packages.Point;

/**
 *
 * @author vivek
 */
public class knn {

    static String testFile = "project_3/rep3/mnist_test.csv";
    static String trainFile = "project_3/rep3/mnist_train.csv";
    static String valFile = "project_3/rep3/mnist_validation.csv";

    public static void main(String[] args) {
        System.out.println("Reading");
        List<Point> testSet = FileOps.getCSVContentAsPointList(testFile);
        List<Point> trainSet = FileOps.getCSVContentAsPointList(trainFile);
        List<Point> valSet = FileOps.getCSVContentAsPointList(valFile);

        System.out.println("Training");
        float[] accuracy = new float[20];
        for (int i = 0; i < 20; i++) {
            accuracy[i] = 0;
        }

        for (Point p : valSet) {
            List<Point> kClosest = findKNN(20, p, trainSet);
            for (int i = 1; i < 21; i++) {
                if (p.getLabel() == mostOccurring(i, kClosest)) {
                    accuracy[i - 1]++;
                }
            }
        }

        int maxk = -1;
        float maxacc = -1;
        for (int i = 0; i < 20; i++) {
            if (accuracy[i] > maxacc) {
                maxk = i + 1;
                maxacc = accuracy[i];
            }
        }

        for (int i = 0; i < 20; i++) {
            System.out.print(i + 1 + ":" + accuracy[i] / (float) valSet.size() + ", ");
        }
        System.out.print("\n");

        System.out.println("K = " + maxk);
        System.out.println("Testing");

        trainSet.addAll(valSet);

        float totAcc = 0;

        for (Point p : testSet) {
            List<Point> kClosest = findKNN(maxk, p, trainSet);
            if (p.getLabel() == mostOccurring(maxk, kClosest)) {
                totAcc++;
            }
        }

        System.out.println(totAcc / (float) testSet.size());

    }

    static int mostOccurring(int length, List<Point> closest) {
        int[] freq = new int[10];
        for (int i = 0; i < 10; i++) {
            freq[i] = 0;
        }
        for (int i = 0; i < length; i++) {
            freq[closest.get(i).getLabel()]++;
        }
        int maxind = -1;
        int maxfreq = -1;
        for (int i = 0; i < 10; i++) {
            if (freq[i] > maxfreq) {
                maxind = i;
                maxfreq = freq[i];
            }
        }
        return maxind;
    }

    static List<Point> findKNN(int k, Point p, List<Point> trainingSet) {
        List<Point> output = new ArrayList<>();
        for (Point currentPoint : trainingSet) {
            currentPoint.setSplValue(eDistance(currentPoint, p));
            splinsert(output, k, currentPoint);
        }
        return output;
    }

    static List<Point> splinsert(List<Point> mylist, int k, Point p) {
        if (mylist.size() == k && mylist.get(k - 1).getSplValue() < p.getSplValue()) {
            return mylist;
        }
        if (mylist.isEmpty()) {
            mylist.add(p);
            return mylist;
        }

        int i;
        for (i = mylist.size() - 1; i >= 0; i--) {
            if (mylist.get(i).getSplValue() < p.getSplValue()) {
                i++;
                break;
            }
        }
        if (i == -1) {
            i = 0;
        }
        mylist.add(i, p);
        if (mylist.size() > k) {
            mylist.remove(k);
        }

        return mylist;

    }

    public static float eDistance(Point a, Point b) {
        double distance = 0;
        for (int i = 0; i < a.length(); i++) {
            distance += (a.get(i) - b.get(i)) * (a.get(i) - b.get(i));
        }
        return (float) Math.sqrt(distance);
    }
}
