package info6205.Graph.Problems.TravellingSalesMan.Algorithm;

import info6205.Graph.Node;
import info6205.Graph.Utils.Pair;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SimulatedAnnealing<NodeValue, NodeKeyValue, EdgeWeight extends Comparable<EdgeWeight>> {

    private final Map<Pair<Node<NodeValue, NodeKeyValue>, Node<NodeValue, NodeKeyValue>>, EdgeWeight> edgeWeights;

    private final int equilibriumCountForTemp = 10;


    public SimulatedAnnealing(Map<Pair<Node<NodeValue, NodeKeyValue>, Node<NodeValue, NodeKeyValue>>, EdgeWeight> edgeWeights) throws FileNotFoundException {
        this.edgeWeights = edgeWeights;
    }



    public List<Pair<List<Node<NodeValue, NodeKeyValue>>, Double>>
    parallelizeSimulatedAnnealingTwoOpt(int parallelism, List<Node<NodeValue, NodeKeyValue>> tour,
                                        Double graphWeight, long temp, double coolingRate,
                                        long maxIteration, int equilibriumCountForTemp, int equilibriumIncrease,
                                        boolean returnMinimum, boolean benchMarking) {
        try {
            if (parallelism > 14) {
                parallelism = 14;
            }

            CompletableFuture<Pair<List<Node<NodeValue, NodeKeyValue>>, Double>>[] simulatedAnnealings = new CompletableFuture[parallelism];

            for (int i = 0; i < parallelism; i++) {
                ArrayList<Node<NodeValue, NodeKeyValue>> tourParallel = new ArrayList<>(tour);
                simulatedAnnealings[i] = parallelSimulatedAnnealingHelper(tourParallel, graphWeight, temp, coolingRate,
                        maxIteration, equilibriumCountForTemp, equilibriumIncrease);
            }
            List<Node<NodeValue, NodeKeyValue>> minTour = new ArrayList<>();
            Double minTourValue = Double.MAX_VALUE;
            List<Pair<List<Node<NodeValue, NodeKeyValue>>, Double>> tourValues = new LinkedList<>();
            for (int i = 0; i < simulatedAnnealings.length; i++) {
                Pair<List<Node<NodeValue, NodeKeyValue>>, Double> tourValue = simulatedAnnealings[i].join();
                if (returnMinimum && minTourValue > tourValue.getSecond()) {
                    minTour = tourValue.getFirst();
                    minTourValue = tourValue.getSecond();
                }
                tourValues.add(tourValue);
            }
            System.out.println("Final 2Opts parallel min Tour value : " + minTourValue);
            if (returnMinimum) {
                tour.clear();
                tour.addAll(minTour);
            }
            return returnMinimum ? Collections.singletonList(new Pair<>(minTour, minTourValue)) : tourValues;
        } catch (Exception e) {
            System.out.println("Exception in Parallelized 2 opt sim : " + e);
            throw e;
        }
    }


    public CompletableFuture<Pair<List<Node<NodeValue, NodeKeyValue>>, Double>> parallelSimulatedAnnealingHelper(List<Node<NodeValue, NodeKeyValue>> tour,
                                                                                                   Double graphWeight,
                                                                                                   long temp, double coolingRate,
                                                                                                   long maxIteration, int equilibriumCountForTemp,
                                                                                                   int equilibriumIncrease) {
        return CompletableFuture.supplyAsync(
                () -> {
                    return runSimulatedAnnealingWrapper(tour, graphWeight, temp, coolingRate, maxIteration, equilibriumCountForTemp
                    , equilibriumIncrease);
                }
        );
    }


    public Pair<List<Node<NodeValue, NodeKeyValue>>, Double>
    runSimulatedAnnealingWrapper(List<Node<NodeValue, NodeKeyValue>> order, Double graphWeight, long temp, double coolingRate,
                                 long maxIteration, int equilibriumCountForTemp, int equilibriumIncrease) {
       return new Pair<>(order, runSimulatedAnnealing(order, graphWeight, temp, coolingRate, maxIteration,
                equilibriumCountForTemp, equilibriumIncrease, this.edgeWeights));
    }


    public Double
    runSimulatedAnnealing(List<Node<NodeValue, NodeKeyValue>> order, Double graphWeight, double temp, double coolingRate,
                          long maxIteration, int equilibriumCountForTemp, int equilibriumIncrease,
                          Map<Pair<Node<NodeValue, NodeKeyValue>, Node<NodeValue, NodeKeyValue>>, EdgeWeight> edgeWeights) {
        try {
            //Node<NodeValue, NodeKeyValue>[] orderArray = order.toArray((Node<NodeValue, NodeKeyValue>[]) new Node[order.size()]);
            Random random = new Random();
            //double temp = 1;
            //double coolingRate = 0.1;
            boolean twoOptNew = true;
            double min = Double.MAX_VALUE;
            int graphV = order.size();
            int minConstCount = 0;
            //int equilibriumCountForTemp = 25;
            int iteration = 0;
            while (temp > 0 && iteration < maxIteration) {
                iteration = iteration + 1;
                int tempEquilibriumCount = 0;

                while (tempEquilibriumCount != equilibriumCountForTemp) {

                    int indexOfNodeOne = random.nextInt(graphV - 1);
                    int indexOfNodeTwo = indexOfNodeOne;

                    while (indexOfNodeOne == indexOfNodeTwo || Math.absExact(indexOfNodeTwo - indexOfNodeOne) < 2) {
                        indexOfNodeTwo = random.nextInt(graphV - 1);
                    }

                    Double newGraphWeight = null;

                    newGraphWeight = twoOpt(order, indexOfNodeOne,
                                indexOfNodeTwo, temp, graphWeight);

                    if (newGraphWeight == null) {
                        //tempEquilibriumCount++;
                    } else {
                        if (newGraphWeight < min) {
                            min = newGraphWeight;
                            minConstCount = 0;
                        }
                        //tempEquilibriumCount = 0;
                        graphWeight = newGraphWeight;
                    }
                    minConstCount++;
                    tempEquilibriumCount++;
                    //System.out.println("New Graph Weight : " + iteration + "graph weight");
                }
                temp *= 1 - coolingRate;
                if (equilibriumCountForTemp < 5000) {
                    equilibriumCountForTemp += equilibriumIncrease;
                }
                System.out.println("temp : " + min + " Equilibrium count" + equilibriumCountForTemp);
                if (minConstCount > order.size() * order.size() && temp == 0) {
                    //temp += 5;
                    break;
                }

            }
            System.out.println("hello min graph weight = " + min);
            return min;
        } catch (Exception e) {
            System.out.println("Error while running simulated Annealing by order : " + e);
            throw e;
        }
    }


    private Double twoOpt(List<Node<NodeValue, NodeKeyValue>> order, int smallerIndex, int largerIndex,
                          double temperature, Double currentWeightOfGraph) {
        try {
            if (smallerIndex > largerIndex) {
                int temp = largerIndex;
                largerIndex = smallerIndex;
                smallerIndex = temp;
            }

            double nodeOneEdgesLost = 0;
            double nodeTwoEdgesLost = 0;
            double nodeOneEdgesGain = 0;
            double nodeTwoEdgesGain = 0;

            nodeOneEdgesLost = (Double) edgeWeights.get(new Pair<>(order.get(smallerIndex), order.get(smallerIndex + 1)));
            nodeTwoEdgesLost = (Double) edgeWeights.get(new Pair<>(order.get(largerIndex), order.get(largerIndex + 1)));

            nodeOneEdgesGain = (Double) edgeWeights.get(new Pair<>(order.get(smallerIndex), order.get(largerIndex)));
            nodeTwoEdgesGain = (Double) edgeWeights.get(new Pair<>(order.get(smallerIndex + 1), order.get(largerIndex + 1)));

            Double newWeight = currentWeightOfGraph + nodeTwoEdgesGain + nodeOneEdgesGain - nodeOneEdgesLost - nodeTwoEdgesLost;
            Random random = new Random();

            //System.out.println("Random value:" + Math.exp((currentWeightOfGraph - edgeMatchingWeight) / temperature));

            if (newWeight < currentWeightOfGraph || Math.exp((currentWeightOfGraph - newWeight) / temperature) > random.nextDouble()) {
                Collections.reverse(order.subList(smallerIndex + 1, largerIndex + 1));
                return newWeight;
            }

            return null;
        } catch (Exception e) {
            System.out.println("Exception in new Two opt + e" + e);
            throw e;
        }
    }

}
