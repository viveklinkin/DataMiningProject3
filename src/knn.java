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

	static String testFile = "project_3/rep2/mnist_test.csv";
	static String trainFile = "project_3/rep2/mnist_train.csv";
	static String valFile = "project_3/rep2/mnist_validation.csv";
	static String outputFile = "output/knnoutput.csv";

	// 0:train-file, 1:validation-file, 2:test-file, 3:output-file //
	// NA: 4:[options]
	static void setVals(String args[]) {
		trainFile = args[0];
		valFile = args[1];
		testFile = args[2];
		outputFile = args[3];
	}

	public static void main(String[] args) {

		if (args.length != 0) {
			setVals(args);
		}

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
			System.out.print(i + 1 + ":" + accuracy[i] / (float) valSet.size()
					+ ", ");
		}
		System.out.print("\n");

		System.out.println("K = " + maxk);
		System.out.println("Testing");

		trainSet.addAll(valSet);

		float totAcc = 0;
		List<String> output = new ArrayList<String>();
		for (Point p : testSet) {
			List<Point> kClosest = findKNN(maxk, p, trainSet);
			output.add("" + mostOccurring(maxk, kClosest));
			if (p.getLabel() == mostOccurring(maxk, kClosest)) {
				totAcc++;
			}
		}

		System.out.println("Writing to file\n\n");
		FileOps.writeFile(outputFile, output);

		System.out.println("ACCURACY: " + totAcc / (float) testSet.size());

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
			currentPoint.setSplValue(jDistance(currentPoint, p));
			splinsert(output, k, currentPoint);
		}
		return output;
	}

	static List<Point> splinsert(List<Point> mylist, int k, Point p) {
		if (mylist.size() == k
				&& mylist.get(k - 1).getSplValue() > p.getSplValue()) {
			return mylist;
		}
		if (mylist.isEmpty()) {
			mylist.add(p);
			return mylist;
		}

		int i;
		for (i = mylist.size() - 1; i >= 0; i--) {
			if (mylist.get(i).getSplValue() > p.getSplValue()) {
				break;
			}
		}
		i++;
		mylist.add(i, p);
		if (mylist.size() > k) {
			mylist.remove(k);
		}

		return mylist;

	}

	public static float cDistance(Point a, Point b) {
		float nume = 0, deno1 = 0, deno2 = 0;
		for (int i = 0; i < a.length(); i++) {
			nume += a.get(i) * b.get(i);
			deno1 += a.get(i) * a.get(i);
			deno2 += b.get(i) * b.get(i);
		}
		return (float) (nume / (Math.sqrt(deno1) * Math.sqrt(deno2)));
	}

	public static float jDistance(Point a, Point b) {
		float nume = 0, deno1 = 0, deno2 = 0;
		for (int i = 0; i < a.length(); i++) {
			nume += a.get(i) * b.get(i);
			deno1 += a.get(i) * a.get(i);
			deno2 += b.get(i) * b.get(i);
		}
		return (float) nume / (deno1 + deno2 - nume);
	}

	public static float eDistance(Point a, Point b) {
		double distance = 0;
		for (int i = 0; i < a.length(); i++) {
			distance += (a.get(i) - b.get(i)) * (a.get(i) - b.get(i));
		}
		return (float) Math.sqrt(distance);
	}
}
