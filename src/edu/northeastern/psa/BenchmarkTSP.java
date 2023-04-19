package edu.northeastern.psa;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkTSP {
    public static void main(String[] args) {
//        benchmarkAllAndWriteToFile("./csv_results/50ants_200gen_results.csv", 50, 100);
          specificBenchmarkAndWriteToFile(1.5,5,0.3, "./csv_results/50ants_500gen_results.csv", 50, 500);
    }

    public static void benchmarkAllAndWriteToFile(String fileName, int antCount, int genCount) {
        List<String[]> rows = new ArrayList<>();
        int count = 0;
        for (int alpha = 10; alpha <= 20; alpha += 5) {
            for (int beta = 5; beta <= 9; beta += 2) {
                for(int evapRate = 1; evapRate <= 3; evapRate += 1) {
                    AntColony a = new AntColony("src/edu/northeastern/psa/file.csv");
                    double d_alpha = (1.0*alpha)/10;
                    double d_evapRate = (1.0*evapRate)/10;
                    a.setAlpha(d_alpha);
                    a.setBeta(beta);
                    a.setEvaporationRate(d_evapRate);
                    // Record the raw time it takes to run a.travelingSalesman()
                    long startTime = System.currentTimeMillis();
                    double minCost_in_km = a.travellingSalesman(antCount,genCount, new ArrayList<>());
                    long endTime = System.currentTimeMillis();
                    long elapsedTime_in_ms = endTime - startTime;

                    String[] row = {String.valueOf(d_alpha), String.valueOf(beta), String.valueOf(d_evapRate), String.valueOf(minCost_in_km), String.valueOf(elapsedTime_in_ms)};
                    rows.add(row);
                    System.out.println("count = " + (++count) + ", alpha = " + d_alpha + ", beta = " + beta + ", evap = " + d_evapRate);
                }
            }
        }
        try {
            FileWriter csvWriter = new FileWriter(fileName);
            csvWriter.append("alpha,beta,gamma,minCost_in_km,elapsedTime_in_ms\n");
            for (String[] row : rows) {
                csvWriter.append(String.join(",", row));
                csvWriter.append("\n");
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void specificBenchmarkAndWriteToFile(double alpha, double beta, double evapRate, String fileName, int antCount, int genCount){
        AntColony a = new AntColony("src/edu/northeastern/psa/file.csv");
        a.setAlpha(alpha);
        a.setBeta(beta);
        a.setEvaporationRate(evapRate);

        List<String> generationWiseStats = new ArrayList<>();
        // Record the raw time it takes to run a.travelingSalesman()
        long startTime = System.currentTimeMillis();
        double minCost_in_km = a.travellingSalesman(antCount,genCount, generationWiseStats);
        long endTime = System.currentTimeMillis();
        long elapsedTime_in_ms = endTime - startTime;

        String[] row = {String.valueOf(alpha), String.valueOf(beta), String.valueOf(evapRate), String.valueOf(minCost_in_km), String.valueOf(elapsedTime_in_ms)};
        try {
            FileWriter csvWriter = new FileWriter(fileName);
            csvWriter.append("alpha,beta,evapRate,minCost_in_km_gens_total,elapsedTime_in_ms\n");
                csvWriter.append(String.join(",", row));
                csvWriter.append("\n\n");
            csvWriter.append("gen,minCost_in_km_per_gen\n");
            for (String generationStat : generationWiseStats) {
                String generation = generationStat.split(",")[0];
                String generationMinCost = generationStat.split(",")[1];
                csvWriter.append(generation);
                csvWriter.append(",");
                csvWriter.append(generationMinCost);
                csvWriter.append("\n");
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("genCount = " + genCount + ", antCount = " + antCount + ", alpha = " + alpha + ", beta = " + beta + ", evap = " + evapRate);
        System.out.println("File written to = " + fileName);
    }
}
