package info6205.Graph.Problems.TravellingSalesMan.Algorithm.TwoOpt;

import info6205.Graph.Node;
import info6205.Graph.Utils.Pair;

import java.util.*;
import java.util.concurrent.CompletableFuture;


/**
 * The `TwoOpt` class is an implementation of the 2-Opt algorithm, it has methods for parallel simulated annealing and normal 2 opt.
 * It uses a parallelized version of the simulated annealing algorithm to find an optimal or near-optimal solution for the TSP.
 * The `TwoOpt` class takes generic type parameters for NodeValue, NodeKeyValue, and EdgeWeight, which represent the values associated with nodes in the TSP graph,
 * the keys associated with nodes in the TSP graph, and the weights of the edges between nodes, respectively.
 *
 * @param <NodeValue> Type of value the Node or vertices hold for the input graph
 * @param <NodeKeyValue> Type of unique ID for a node or vertices of the input graph
 * @param <EdgeWeight> Type of Edge Weight the edge will represent for the graph, this needs to implement Comparable
 */

public class TwoOpt<NodeValue, NodeKeyValue, EdgeWeight extends Comparable<EdgeWeight>> {

    /**
     * A map that stores the weights of the edges between nodes in the TSP graph.
     * The keys of the map are pairs of nodes, and the values are the Edge weights of the edge between those nodes.
     * this should also contain node1 -> node2 and node2 -> node1 for undirected graph
     */
    Map<Pair<Node<NodeValue, NodeKeyValue>, Node<NodeValue, NodeKeyValue>>, EdgeWeight> edgeWeights;

    /**
     * A list of all the possible indices pairs for an two opt operation.
     * Indices are the indices of the node present in the order
     */
    List<int[]> segments;

    /**
     * Constructs a `TwoOpt` object with the given edge weights and the size of the TSP graph.
     *
     * @param edgeWeights  A map that stores the weights of the edges between nodes in the TSP graph.
     * @param size         The size of the TSP graph.
     */
    public TwoOpt(Map<Pair<Node<NodeValue, NodeKeyValue>, Node<NodeValue, NodeKeyValue>>, EdgeWeight> edgeWeights, int size) {
        this.edgeWeights = edgeWeights;
        segments = allSegments(size);
    }



    /**
     * This Parallelizes the simulated annealing 2-Opt algorithm.
     * The algorithm is run in parallel using the specified number of threads.
     *
     *
     * @param parallelism The number of threads to use for parallel execution of the algorithm.
     * @param tour The initial tour for the TSP.
     * @param graphWeight The initial weight of the TSP tour.
     * @param temp The initial temperature for the simulated annealing algorithm.
     * @param coolingRate The cooling rate for the simulated annealing algorithm.
     * @param maxIteration The maximum number of iterations for the simulated annealing algorithm.
     * @param equilibriumCountForTemp The number of iterations to wait for equilibrium before reducing the temperature.
     * @param equilibriumIncrease The increase factor for the equilibrium count.
     * @param returnMinimum Whether to return only the minimum tour found by the algorithm and replace current tour with minimum tour
     * @param benchMarking Whether to enable benchmarking mode for the algorithm.
     * @return A list of pairs, where each pair contains a tour and its corresponding weight. If returnMinimum is true, the list will contain only one pair with the minimum tour and its weight.
     */
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


    /**
    *
    * Runs the simulated annealing algorithm to find the minimum weight Hamiltonian path in the given graph.
     * @param order a list of nodes representing the TSP path
     * @param graphWeight the weight of the initial TSP path
     * @param temp the starting temperature
     * @param coolingRate the cooling rate by which the temperature is dropped
     * @param maxIteration the maximum number of iterations to run
     * @param equilibriumCountForTemp the number of times to iterate at each temperature before cooling
     * @param equilibriumIncrease the amount to increase the equilibrium count for each temperature
     * @param edgeWeights a map of edge weights in the graph
     * @return the minimum weight Hamiltonian path found by the algorithm
     * @throws Exception if an error occurs while running the algorithm
     */
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
    /**

     * Performs the 2-opt algorithm to improve the given TSP tour order. it searches through every possible indices
     * @param order a List of Nodes representing the TSP tour order
     * @param tsp the total weight of the TSP tour
     * @return the updated total weight of the TSP tour after performing 2-opt algorithm
     * @throws Exception if any error occurs while performing 2-opt algorithm
     */
    public Double performTwoOpt(List<Node<NodeValue, NodeKeyValue>> order, Double tsp) {
        try {
            long iteration = 0;
            Random random = new Random();
            while (true) {
                Double delta = 0D;
                for (int a = 1; a < order.size() - 1; a++) {
                    for (int b = a + 2; b < order.size(); b++) {
                        if (random.nextDouble() < 0.6) {
                        //    continue;
                        }
                        Double temp = twoOpt(order, a, b, tsp);
                        delta += temp;
                        //System.out.println("\nDelta : " + temp + ", Tsp: " + tsp);
                        tsp += temp;
                        //System.out.println("Tsp weight : " + tsp + ", iteration = " + iteration++);
                    }
                }
                if (delta >= 0) {
                    break;
                }
                iteration++;
            }
            System.out.println("iterations" + iteration);
            return tsp;
        } catch (Exception e) {
            System.out.println("Exception While Performing TwoOpt");
            throw e;
        }
    }



    /**
     * Performs the 2-opt algorithm between two nodes in the given TSP tour order and returns the change in weight caused
     * by the algorithm.
     * @param order a List of Nodes representing the TSP tour order
     * @param smallerIndex the index of the smaller node
     * @param largerIndex the index of the larger node
     * @param currentWeightOfGraph the total weight of the TSP tour before performing the 2-opt algorithm
     * @return the change in weight of the TSP tour after performing 2-opt between the specified nodes
     * @throws Exception if any error occurs while performing 2-opt algorithm
     */
    private Double twoOpt(List<Node<NodeValue, NodeKeyValue>> order, int smallerIndex, int largerIndex,
                              Double currentWeightOfGraph) {
        try {
            if (smallerIndex > largerIndex) {
                int temp = largerIndex;
                largerIndex = smallerIndex;
                smallerIndex = temp;
            }
            largerIndex = largerIndex % order.size();

            double nodeOneEdgesLost = 0;
            double nodeTwoEdgesLost = 0;
            double nodeOneEdgesGain = 0;
            double nodeTwoEdgesGain = 0;

            nodeOneEdgesLost = (Double) edgeWeights.get(new Pair<>(order.get(smallerIndex), order.get(smallerIndex + 1)));
            nodeTwoEdgesLost = (Double) edgeWeights.get(new Pair<>(order.get(largerIndex), order.get((largerIndex + 1) % order.size())));

            nodeOneEdgesGain = (Double) edgeWeights.get(new Pair<>(order.get(smallerIndex), order.get(largerIndex)));
            nodeTwoEdgesGain = (Double) edgeWeights.get(new Pair<>(order.get(smallerIndex + 1), order.get((largerIndex + 1) % order.size())));

            Double newWeight = currentWeightOfGraph + nodeTwoEdgesGain + nodeOneEdgesGain - nodeOneEdgesLost - nodeTwoEdgesLost;
            Random random = new Random();


            if (newWeight < currentWeightOfGraph) {
                Collections.reverse(order.subList(smallerIndex + 1, largerIndex + 1));
                return newWeight - currentWeightOfGraph;
            }

            return 0.0;
        } catch (Exception e) {
            System.out.println("Exception in new Two opt + e" + e);
            throw e;
        }
    }


    /**

     Applies the 2-opt heuristic algorithm to a given list of nodes with node indices "smallerIndex" and "largerIndex".
     Calculates the change in weight of the graph after swapping the edges between nodes "smallerIndex" and "largerIndex".
     If the new weight is less than the current weight of the graph, then it reverses the nodes between "smallerIndex+1" and "largerIndex"
     in the list and returns the new weight.
     If the new weight is greater than or equal to the current weight of the graph, it calculates a probability of accepting the new
     solution based on the current temperature and a random number generated by the algorithm. If the probability is greater than the
     random number, then it reverses the nodes and returns the new weight, else it returns null.
     @param order the list of nodes
     @param smallerIndex the index of the first node to be swapped
     @param largerIndex the index of the second node to be swapped
     @param temperature the current temperature of the simulated annealing algorithm
     @param currentWeightOfGraph the current weight of the graph
     @return the new weight of the graph after applying 2-opt heuristic, or null if the new solution is rejected
     @throws Exception if there is an error while performing 2-opt
     */
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

    /**
     * Returns a list of all possible segments of a given size n, with segment size of 2.
     * A segment is defined as two indices a and b, where a and b are integers between 0 and n-1, inclusive,
     * and a < b - 2.
     * @param n the size of the segment.
     * @return a list of all possible segments of size 2.
     */
    public static List<int[]> allSegments(int n) {
        List<int[]> segments = new ArrayList<>();
        for (int a = 0; a < n - 4; a++) {
            for (int b = a + 2; b < n - 1; b++) {
                segments.add(new int[] {a, b});

            }
        }
        return segments;
    }
}
