package info6205.Graph.Problems.TravellingSalesMan.Algorithm;

import info6205.Graph.Node;
import info6205.Graph.Utils.Pair;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Objects;

public class FileWriter<NodeValue, NodeKeyValue> {

    FileOutputStream fis = null;
    OutputStreamWriter isr = null;
    BufferedWriter bw = null;


    public void start2optWriting() {
        try {
            fis = new FileOutputStream("./csvOutput/" + "3Opt_bechmarking" + ".csv");
            isr = new OutputStreamWriter(fis);
            bw = new BufferedWriter(isr);

            bw.write("Parallelism, temp, coolingRate, maxIteration, equilibriumCountForTemp, equilibriumIncrease, AverageTSP," +
                    "MinTSP, MaxTSP, TimeTaken, Starting TSP\n");
        } catch (Exception e) {
            System.out.println("Exception while starting 2Opt: " + e);
        }
    }


    public void writeLine(int parallelism, List<Double> tours,
                          Double startingTsp, long temp, double coolingRate,
                          long maxIteration, int equilibriumCountForTemp, int equilibriumIncrease,
                          long timeTaken) {
        try {
            Double averageTsp = 0D;
            Double minTsp = Double.MAX_VALUE;
            Double maxTsp = Double.MIN_VALUE;

            for (Double tour : tours) {
                averageTsp += tour;
                if (minTsp > tour) {
                    minTsp = tour;
                }
                if (maxTsp < tour) {
                    maxTsp = tour;
                }
            }
            averageTsp = averageTsp/ tours.size();

            bw.write(parallelism + "," + temp + "," + coolingRate + "," + maxIteration + "," + equilibriumCountForTemp
                    + "," + equilibriumIncrease + "," + averageTsp + "," + minTsp + "," + maxTsp + "," + timeTaken
                    + "," + startingTsp + "\n");

            System.out.println("\naverage:"+averageTsp+", min:" + minTsp + " max:" + maxTsp + ", time taken:" + timeTaken);
            System.out.println("coolingRate:"+coolingRate+", equilibriumCountForTemp:" + equilibriumCountForTemp
                    + " equilibriumIncrease:" + equilibriumIncrease);
            bw.flush();
        } catch (Exception e) {
            System.out.println("Exception while writing for values 2Opt: " + e);
        }

    }




    public void stop2optWriting() {
        try {
            bw.flush();
            bw.close();
            isr.flush();
            isr.close();
            fis.flush();
            fis.close();
        } catch (Exception e) {
            System.out.println("Exception while stoping writing 2Opt: " + e);
        }
    }

}
