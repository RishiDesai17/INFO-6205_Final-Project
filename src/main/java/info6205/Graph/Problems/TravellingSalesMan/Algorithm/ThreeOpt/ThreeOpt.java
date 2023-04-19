package info6205.Graph.Problems.TravellingSalesMan.Algorithm.ThreeOpt;

import info6205.Graph.Node;
import info6205.Graph.Utils.Pair;

import java.util.*;
import java.util.concurrent.CompletableFuture;


/**

 * The ThreeOpt class is used to implement the 3-opt algorithm..
 * This algorithm takes a set of nodes as input and iteratively optimizes the tour using three different kinds of exchanges
 * between three edges in the tour until no further improvement can be made.
 * @param <NodeValue> the node value type
 * @param <NodeKeyValue> the node key value type
 * @param <EdgeWeight> the edge weight type which extends Comparable
 */
public class ThreeOpt<NodeValue, NodeKeyValue, EdgeWeight extends Comparable<EdgeWeight>> {

    Map<Pair<Node<NodeValue, NodeKeyValue>, Node<NodeValue, NodeKeyValue>>, EdgeWeight> edgeWeights;

    List<int[]> segments;


    /**
     * Takes in Edge weights which is basically all the edges of a complete graph
     * @param edgeWeights all the edges of a complete graph
     * @param size number of nodes in same
     */
    public ThreeOpt(Map<Pair<Node<NodeValue, NodeKeyValue>, Node<NodeValue, NodeKeyValue>>, EdgeWeight> edgeWeights, int size) {
        this.edgeWeights = edgeWeights;
        segments = allSegments(size);
    }


    /**
     *
     * @param parallelism number of parallel threads
     * @param tour TSP Tour, list of nodes
     * @param graphWeight current weight of the TSP tour
     * @param temp The initial temperature for the simulated annealing algorithm.
     * @param coolingRate The cooling rate for the simulated annealing algorithm.
     * @param maxIteration The maximum number of iterations for the simulated annealing algorithm.
     * @param equilibriumCountForTemp The number of iterations to wait for equilibrium before reducing the temperature.
     * @param equilibriumIncrease The increase factor for the equilibrium count.
     * @param returnMinimum Whether to return only the minimum tour found by the algorithm and replace current tour with minimum tour
     * @param benchMarking Whether to enable benchmarking mode for the algorithm.
     */
    public List<Pair<List<Node<NodeValue, NodeKeyValue>>, Double>>
    parallelizeSimulatedAnnealingThreeOpt(int parallelism, List<Node<NodeValue, NodeKeyValue>> tour,
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
            List<Node<NodeValue, NodeKeyValue>> minTour = null;
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
            System.out.println("Final 3Opts parallel min Tour value : " + minTourValue);
            if (returnMinimum) {
                tour.clear();
                tour.addAll(minTour);
            }
            return returnMinimum ? Collections.singletonList(new Pair<>(minTour, minTourValue)) : tourValues;
        } catch (Exception e) {
            System.out.println("Exception in Parallelized 3 opt sim : " + e);
            throw e;
        }
    }


    /**
     * Helper for Parallel simulated annealing
     * @param tour TSP Tour, list of nodes
     * @param graphWeight current weight of the TSP tour
     * @param temp The initial temperature for the simulated annealing algorithm.
     * @param coolingRate The cooling rate for the simulated annealing algorithm.
     * @param maxIteration The maximum number of iterations for the simulated annealing algorithm.
     * @param equilibriumCountForTemp The number of iterations to wait for equilibrium before reducing the temperature.
     * @param equilibriumIncrease The increase factor for the equilibrium count.
     * @return
     */
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
                equilibriumCountForTemp, equilibriumIncrease));
    }

    /**
     *
     * Runs the simulated annealing algorithm to find the minimum weight Hamiltonian path in the given graph.
     * @param order a list of nodes representing the graph
     * @param graphWeight the weight of the initial TSP path
     * @param temp the starting temperature
     * @param coolingRate the cooling rate by which the temp is decreased
     * @param maxIteration the maximum number of iterations to run
     * @param equilibriumCountForTemp the number of times to iterate at each temperature before cooling
     * @param equilibriumIncrease the amount to increase the equilibrium count for each temperature
     * @return the minimum weight Hamiltonian path found by the algorithm
     * @throws Exception if an error occurs while running the algorithm
     */
    public Double
    runSimulatedAnnealing(List<Node<NodeValue, NodeKeyValue>> order, Double graphWeight, double temp, double coolingRate,
                             long maxIteration, int equilibriumCountForTemp, int equilibriumIncrease) {
        try {
            Random random = new Random();
            double min = Double.MAX_VALUE;
            int graphV = order.size();
            int minConstCount = 0;
            //int equilibriumCountForTemp = 25;
            int iteration = 0;
            while (temp > 0 && iteration < maxIteration) {
                iteration = iteration + 1;
                int tempEquilibriumCount = 0;

                while (tempEquilibriumCount != equilibriumCountForTemp) {

                    int segmentIndex = random.nextInt(this.segments.size());

                    int[] segment = this.segments.get(segmentIndex);
                    Double delta = null;

                    delta = threeOptPure(order, segment[0],
                            segment[1], segment[2], temp);

                    if (delta == 0D) {
                        //tempEquilibriumCount++;
                    } else {
                        if (delta + graphWeight < min) {
                            min = graphWeight + delta;
                            minConstCount = 0;
                        }
                        //tempEquilibriumCount = 0;
                        graphWeight = delta + graphWeight;
                    }
                    minConstCount++;
                    tempEquilibriumCount++;
                    //System.out.println("New Graph Weight : " + iteration + "graph weight");
                }
                temp *= 1 - coolingRate;
                if (equilibriumCountForTemp < 5000) {
                    equilibriumCountForTemp += equilibriumIncrease;
                }
                System.out.println("Three Opt min : " + min + ", iteration: " + iteration);
                if (minConstCount > order.size() * order.size() && temp == 0) {
                    //temp += 5;
                    //break;
                }

            }
            System.out.println("hello min graph weight = " + iteration);
            return min;
        } catch (Exception e) {
            System.out.println("Error while running simulated Annealing by order : " + e);
            throw e;
        }
    }


    /**

     * Performs the 3-opt algorithm to improve the given TSP tour order. it searches through every possible indices
     * @param order a List of Nodes representing the TSP tour order
     * @param tsp the total weight of the TSP tour
     * @return the updated total weight of the TSP tour after performing 2-opt algorithm
     * @throws Exception if any error occurs while performing 2-opt algorithm
     */
    public Double performThreeOpt(List<Node<NodeValue, NodeKeyValue>> order, Double tsp) {
        try {
            long iteration = 0;
            while (true) {
                Double delta = 0D;
                for (int a = 1; a < order.size(); a++) {
                    for (int b = a + 2; b < order.size(); b++) {
                        for (int c = b + 2; c < order.size() + (a > 0 ? 1 : 0); c++) {

                            Double temp = threeOptPure(order, a, b, c);
                            delta += temp;
                            //System.out.println("\nDelta : " + temp + ", Tsp: " + tsp);
                            tsp += temp;
                            System.out.println("Tsp weight : " + tsp + ", iteration = " + iteration++);
                        }
                    }
                }
                if (delta >= 0) {
                    break;
                }
            }
            return tsp;
        } catch (Exception e) {
            System.out.println("Exception While Performing ThreeOpt");
            throw e;
        }
    }


    public static List<int[]> allSegments(int n) {
        List<int[]> segments = new ArrayList<>();
        for (int a = 1; a < n; a++) {
            for (int b = a + 2; b < n; b++) {
                for (int c = b + 2; c < n + (a > 0 ? 1 : 0); c++) {
                    segments.add(new int[] { a, b, c });
                }
            }
        }
        return segments;
    }

    /**
     * Applies the 3-opt heuristic algorithm to a given list of nodes with node indices 3 indices.
     * Applies # opt which is present in the wiki with temperature
     * @param order a List of Nodes representing the TSP tour order
     * @param firstIndex the index of the first node
     * @param secondIndex the index of the second node
     * @param thirdIndex the total weight of the TSP tour before performing the 3-opt algorithm
     * @param temperature the current temperature of the simulated annealing algorithm
     * @return the change in weight of the TSP tour after performing 3-opt between the specified nodes
     * @throws Exception if any error occurs while performing 3-opt algorithm
     */
    public Double threeOpt(List<Node<NodeValue, NodeKeyValue>> order, int firstIndex, int secondIndex, int thirdIndex, Double temperature) {
        try {
            Node<NodeValue, NodeKeyValue> nodeA = order.get(firstIndex - 1);
            Node<NodeValue, NodeKeyValue> nodeB = order.get(firstIndex);
            Node<NodeValue, NodeKeyValue> nodeC = order.get(secondIndex - 1);
            Node<NodeValue, NodeKeyValue> nodeD = order.get(secondIndex);
            Node<NodeValue, NodeKeyValue> nodeE = order.get(thirdIndex - 1);
            Node<NodeValue, NodeKeyValue> nodeF = order.get(thirdIndex % order.size());

            Double d0 = (Double) edgeWeights.get(new Pair<>(nodeA, nodeB)) + (Double) edgeWeights.get(new Pair<>(nodeC, nodeD)) +
                    (Double) edgeWeights.get(new Pair<>(nodeE, nodeF));
            Double d1 = (Double) edgeWeights.get(new Pair<>(nodeA, nodeC)) + (Double) edgeWeights.get(new Pair<>(nodeB, nodeD)) +
                    (Double) edgeWeights.get(new Pair<>(nodeE, nodeF));
            Double d2 = (Double) edgeWeights.get(new Pair<>(nodeA, nodeB)) + (Double) edgeWeights.get(new Pair<>(nodeC, nodeE)) +
                    (Double) edgeWeights.get(new Pair<>(nodeD, nodeF));
            Double d3 = (Double) edgeWeights.get(new Pair<>(nodeA, nodeD)) + (Double) edgeWeights.get(new Pair<>(nodeE, nodeB)) +
                    (Double) edgeWeights.get(new Pair<>(nodeC, nodeF));
            Double d4 = (Double) edgeWeights.get(new Pair<>(nodeF, nodeB)) + (Double) edgeWeights.get(new Pair<>(nodeC, nodeD)) +
                    (Double) edgeWeights.get(new Pair<>(nodeE, nodeA));
            Random random = new Random();

            if (d0 > d1) {
                Collections.reverse(order.subList(firstIndex, secondIndex));
                return -d0 + d1;
            } else if (d0 > d2) {
                Collections.reverse(order.subList(secondIndex, thirdIndex));
                return -d0 + d2;
            } else if (d0 > d4) {
                Collections.reverse(order.subList(firstIndex, thirdIndex));
                return -d0 + d4;
            } else if (d0 > d3) {

                //Collections.reverse(order.subList(firstIndex, secondIndex));
                List<Node<NodeValue, NodeKeyValue>> tmp = new ArrayList<>(order.subList(secondIndex, thirdIndex));
                tmp.addAll(order.subList(firstIndex, secondIndex));
                // Replace the sublist from i to k (exclusive) with the concatenated sublist
                order.subList(firstIndex, thirdIndex).clear();
                order.addAll(firstIndex, tmp);
                return -d0 + d3;
            }

            if (Math.exp((d0 - d1) / temperature) > random.nextDouble()) {
                Collections.reverse(order.subList(firstIndex, secondIndex));
                return -d0 + d1;
            } else if (Math.exp((d0 - d2) / temperature) > random.nextDouble()) {
                Collections.reverse(order.subList(secondIndex, thirdIndex));
                return -d0 + d2;
            } else if (Math.exp((d0 - d4) / temperature) > random.nextDouble()) {
                Collections.reverse(order.subList(firstIndex, thirdIndex));
                return -d0 + d4;
            } else if (Math.exp((d0 - d3) / temperature) > random.nextDouble()) {
                List<Node<NodeValue, NodeKeyValue>> tmp = new ArrayList<>(order.subList(secondIndex, thirdIndex));
                tmp.addAll(order.subList(firstIndex, secondIndex));
                // Replace the sublist from i to k (exclusive) with the concatenated sublist
                order.subList(firstIndex, thirdIndex).clear();
                order.addAll(firstIndex, tmp);
                return -d0 + d3;
            }

            return 0D;

        } catch (Exception e) {
            System.out.println("Exception While Performing ThreeOpt e: " + e);
            throw e;
        }
    }


    /**
     * Applies the 3-opt heuristic algorithm to a given list of nodes with node indices 3 indices.
     * applies True 3 opt with temperature
     * @param order a List of Nodes representing the TSP tour order
     * @param firstIndex the index of the first node
     * @param secondIndex the index of the second node
     * @param thirdIndex the total weight of the TSP tour before performing the 3-opt algorithm
     * @param temperature the current temperature of the simulated annealing algorithm
     * @return the change in weight of the TSP tour after performing 3-opt between the specified nodes
     * @throws Exception if any error occurs while performing 3-opt algorithm
     */
    public Double threeOptPure(List<Node<NodeValue, NodeKeyValue>> order, int firstIndex, int secondIndex, int thirdIndex, Double temperature) {
        try {
            Node<NodeValue, NodeKeyValue> nodeA = order.get(firstIndex - 1);
            Node<NodeValue, NodeKeyValue> nodeB = order.get(firstIndex);
            Node<NodeValue, NodeKeyValue> nodeC = order.get(secondIndex - 1);
            Node<NodeValue, NodeKeyValue> nodeD = order.get(secondIndex);
            Node<NodeValue, NodeKeyValue> nodeE = order.get(thirdIndex - 1);
            Node<NodeValue, NodeKeyValue> nodeF = order.get(thirdIndex % order.size());

            Double d0 = (Double) edgeWeights.get(new Pair<>(nodeA, nodeB)) + (Double) edgeWeights.get(new Pair<>(nodeC, nodeD)) +
                    (Double) edgeWeights.get(new Pair<>(nodeE, nodeF));

            Double d3 = (Double) edgeWeights.get(new Pair<>(nodeA, nodeD)) + (Double) edgeWeights.get(new Pair<>(nodeE, nodeB)) +
                    (Double) edgeWeights.get(new Pair<>(nodeC, nodeF));

            Double d4 = (Double) edgeWeights.get(new Pair<>(nodeD, nodeB)) + (Double) edgeWeights.get(new Pair<>(nodeC, nodeF)) +
                    (Double) edgeWeights.get(new Pair<>(nodeE, nodeA));

            if (d0 > d3) {
                List<Node<NodeValue, NodeKeyValue>> tmp = new ArrayList<>(order.subList(secondIndex, thirdIndex));
                tmp.addAll(order.subList(firstIndex, secondIndex));
                order.subList(firstIndex, thirdIndex).clear();
                order.addAll(firstIndex, tmp);
                return -d0 + d3;

            } else if (d0 > d4) {
                List<Node<NodeValue, NodeKeyValue>> tmp = new ArrayList<>(order.subList(firstIndex, secondIndex));
                tmp.addAll(order.subList(thirdIndex, order.size()));
                tmp.addAll(order.subList(0, firstIndex));
                Collections.reverse(order.subList(secondIndex, thirdIndex));
                tmp.addAll(order.subList(secondIndex, thirdIndex));
                order.clear();
                order.addAll(tmp);
                return -d0 + d4;
            }


            Random random = new Random();
            if (Math.exp((d0 - d4) / temperature) > random.nextDouble()) {
                List<Node<NodeValue, NodeKeyValue>> tmp = new ArrayList<>(order.subList(firstIndex, secondIndex));
                tmp.addAll(order.subList(thirdIndex, order.size()));
                tmp.addAll(order.subList(0, firstIndex));
                Collections.reverse(order.subList(secondIndex, thirdIndex));
                tmp.addAll(order.subList(secondIndex, thirdIndex));
                order.clear();
                order.addAll(tmp);
                return -d0 + d4;
            } else if (Math.exp((d0 - d3) / temperature) > random.nextDouble()) {
                List<Node<NodeValue, NodeKeyValue>> tmp = new ArrayList<>(order.subList(secondIndex, thirdIndex));
                tmp.addAll(order.subList(firstIndex, secondIndex));
                // Replace the sublist from i to k (exclusive) with the concatenated sublist
                order.subList(firstIndex, thirdIndex).clear();
                order.addAll(firstIndex, tmp);
                return -d0 + d3;
            }

            return 0D;

        } catch (Exception e) {
            System.out.println("Exception While Performing ThreeOpt e: " + e);
            throw e;
        }
    }


    /**
     * Applies the 3-opt heuristic algorithm to a given list of nodes with node indices 3 indices.
     * applies True 3 opt with temperature
     * @param order a List of Nodes representing the TSP tour order
     * @param firstIndex the index of the first node
     * @param secondIndex the index of the second node
     * @param thirdIndex the total weight of the TSP tour before performing the 3-opt algorithm
     * @return the change in weight of the TSP tour after performing 3-opt between the specified nodes
     * @throws Exception if any error occurs while performing 3-opt algorithm
     */
    public Double threeOptPure(List<Node<NodeValue, NodeKeyValue>> order, int firstIndex, int secondIndex, int thirdIndex) {
        try {
            Node<NodeValue, NodeKeyValue> nodeA = order.get(firstIndex - 1);
            Node<NodeValue, NodeKeyValue> nodeB = order.get(firstIndex);
            Node<NodeValue, NodeKeyValue> nodeC = order.get(secondIndex - 1);
            Node<NodeValue, NodeKeyValue> nodeD = order.get(secondIndex);
            Node<NodeValue, NodeKeyValue> nodeE = order.get(thirdIndex - 1);
            Node<NodeValue, NodeKeyValue> nodeF = order.get(thirdIndex % order.size());

            Double d0 = (Double) edgeWeights.get(new Pair<>(nodeA, nodeB)) + (Double) edgeWeights.get(new Pair<>(nodeC, nodeD)) +
                    (Double) edgeWeights.get(new Pair<>(nodeE, nodeF));

            Double d3 = (Double) edgeWeights.get(new Pair<>(nodeA, nodeD)) + (Double) edgeWeights.get(new Pair<>(nodeE, nodeB)) +
                    (Double) edgeWeights.get(new Pair<>(nodeC, nodeF));

            Double d4 = (Double) edgeWeights.get(new Pair<>(nodeD, nodeB)) + (Double) edgeWeights.get(new Pair<>(nodeC, nodeF)) +
                    (Double) edgeWeights.get(new Pair<>(nodeE, nodeA));

            if (d0 > d3) {
                List<Node<NodeValue, NodeKeyValue>> tmp = new ArrayList<>(order.subList(secondIndex, thirdIndex));
                tmp.addAll(order.subList(firstIndex, secondIndex));
                order.subList(firstIndex, thirdIndex).clear();
                order.addAll(firstIndex, tmp);
                return -d0 + d3;

            } else if (d0 > d4) {
                List<Node<NodeValue, NodeKeyValue>> tmp = new ArrayList<>(order.subList(firstIndex, secondIndex));
                tmp.addAll(order.subList(thirdIndex, order.size()));
                tmp.addAll(order.subList(0, firstIndex));
                Collections.reverse(order.subList(secondIndex, thirdIndex));
                tmp.addAll(order.subList(secondIndex, thirdIndex));
                order.clear();
                order.addAll(tmp);
                return -d0 + d4;
            }
            return 0D;

        } catch (Exception e) {
            System.out.println("Exception While Performing ThreeOpt e: " + e);
            throw e;
        }
    }


    /**
     * Applies the 3-opt heuristic algorithm to a given list of nodes with node indices 3 indices.
     * applies 3 opt as given on wiki with temperature
     * @param order a List of Nodes representing the TSP tour order
     * @param firstIndex the index of the first node
     * @param secondIndex the index of the second node
     * @param thirdIndex the total weight of the TSP tour before performing the 3-opt algorithm
     * @return the change in weight of the TSP tour after performing 3-opt between the specified nodes
     * @throws Exception if any error occurs while performing 3-opt algorithm
     */
    public Double threeOpt(List<Node<NodeValue, NodeKeyValue>> order, int firstIndex, int secondIndex, int thirdIndex) {
        try {
            Node<NodeValue, NodeKeyValue> nodeA = order.get(firstIndex - 1);
            Node<NodeValue, NodeKeyValue> nodeB = order.get(firstIndex);
            Node<NodeValue, NodeKeyValue> nodeC = order.get(secondIndex - 1);
            Node<NodeValue, NodeKeyValue> nodeD = order.get(secondIndex);
            Node<NodeValue, NodeKeyValue> nodeE = order.get(thirdIndex - 1);
            Node<NodeValue, NodeKeyValue> nodeF = order.get(thirdIndex % order.size());

            Double d0 = (Double) edgeWeights.get(new Pair<>(nodeA, nodeB)) + (Double) edgeWeights.get(new Pair<>(nodeC, nodeD)) +
                    (Double) edgeWeights.get(new Pair<>(nodeE, nodeF));

            Double d1 = (Double) edgeWeights.get(new Pair<>(nodeA, nodeC)) + (Double) edgeWeights.get(new Pair<>(nodeB, nodeD)) +
                    (Double) edgeWeights.get(new Pair<>(nodeE, nodeF));
            Double d2 = (Double) edgeWeights.get(new Pair<>(nodeA, nodeB)) + (Double) edgeWeights.get(new Pair<>(nodeC, nodeE)) +
                    (Double) edgeWeights.get(new Pair<>(nodeD, nodeF));

            Double d3 = (Double) edgeWeights.get(new Pair<>(nodeA, nodeD)) + (Double) edgeWeights.get(new Pair<>(nodeE, nodeB)) +
                    (Double) edgeWeights.get(new Pair<>(nodeC, nodeF));

            Double d4 = (Double) edgeWeights.get(new Pair<>(nodeF, nodeB)) + (Double) edgeWeights.get(new Pair<>(nodeC, nodeD)) +
                    (Double) edgeWeights.get(new Pair<>(nodeE, nodeA));

            if (d0 > d1) {
                Collections.reverse(order.subList(firstIndex, secondIndex));
                return -d0 + d1;
            } else if (d0 > d2) {
                Collections.reverse(order.subList(secondIndex, thirdIndex));
                return -d0 + d2;
            } else if (d0 > d4) {
                Collections.reverse(order.subList(firstIndex, thirdIndex));
                return -d0 + d4;
            } else if (d0 > d3) {
                //Collections.reverse(order.subList(firstIndex, secondIndex));
                List<Node<NodeValue, NodeKeyValue>> tmp = new ArrayList<>(order.subList(secondIndex, thirdIndex));
                tmp.addAll(order.subList(firstIndex, secondIndex));
                // Replace the sublist from i to k (exclusive) with the concatenated sublist
                order.subList(firstIndex, thirdIndex).clear();
                order.addAll(firstIndex, tmp);
                return -d0 + d3;
            }

            return 0D;

        } catch (Exception e) {
            System.out.println("Exception While Performing ThreeOpt e: " + e);
            throw e;
        }
    }

}
