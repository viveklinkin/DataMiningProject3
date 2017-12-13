/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packages;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vivek
 */
public class FileOps {

    public static void writeFile(String filePath, List<String> data) {
        File f = new File(filePath);
        BufferedWriter bw;

        try {
            bw = new BufferedWriter(new FileWriter(f));
            for (String x : data) {
                bw.write(x + "\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            System.out.println("Error creating file");
        }

    }

    public static List<String> openCSV(String path) {
        File f = new File(path);
        List<String> res = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    res.add(line);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading file");
        }
        return res;
    }

    public static List<Point> getCSVContentAsPointList(String path) {
        List<String> input = openCSV(path);
        List<Point> output = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            output.add(new Point(input.get(i), i));
        }
        return output;
    }

    public static double[][] getNormalisedCSVContentAsMatrix(String path) {
        List<String> input = openCSV(path);
        double[][] output = new double[input.size()][input.get(0).split(",").length];
        for (int i = 0; i < input.size(); i++) {
            String[] words = input.get(i).split(",");
            double sum = 0;
            for (int j = 0; j < words.length; j++) {
                output[i][j] = Double.parseDouble(words[j]);
                if (j != 0) {
                    sum += output[i][j] * output[i][j];
                }
            }
            sum = Math.sqrt(sum);
            for (int j = 1; j < words.length; j++) {
                output[i][j] /= sum;
            }
        }

        return output;
    }
}
