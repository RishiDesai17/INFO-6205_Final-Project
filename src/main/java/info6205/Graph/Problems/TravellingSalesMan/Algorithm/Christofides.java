package info6205.Graph.Problems.TravellingSalesMan.Algorithm;import info6205.Graph.*;import info6205.Graph.Problems.TravellingSalesMan.Algorithm.ThreeOpt.ThreeOpt;import info6205.Graph.Problems.TravellingSalesMan.Algorithm.TwoOpt.TwoOpt;import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.LatLongId;import info6205.Graph.Utils.Pair;import info6205.Graph.Utils.PointPlotter;import java.awt.*;import java.util.*;import java.util.List;import java.util.concurrent.ForkJoinPool;import java.util.stream.Collectors;/** * This applies Christofides algorithm and also optimizations based on the variable processOfOptimization * @param <NodeValue> Type of value the Node or vertices hold for the graph given * @param <NodeKeyValue> Type of unique ID for a node or vertices for the graph given * @param <EdgeWeight> Type of Edge Weight the edge will represent for the graph given, this needs to implement Comparable */public class Christofides<NodeValue, NodeKeyValue, EdgeWeight extends Comparable<EdgeWeight>> {    private UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> graph;    private Map<Pair<Node<NodeValue, NodeKeyValue>, Node<NodeValue, NodeKeyValue>>, EdgeWeight> edgeWeights;    private final EdgeCreator<NodeValue, NodeKeyValue, EdgeWeight> edgeCreator;    private static final double EARTH_RADIUS = 6371.0;    //private final SimulatedAnnealing<NodeValue, NodeKeyValue, EdgeWeight> simulatedAnnealing;    /**     *     * @param graph Graph for which christofidies is to be applied     * @param edgeCreator The edge creater implementation     */    public Christofides(UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> graph,                        EdgeCreator<NodeValue, NodeKeyValue, EdgeWeight> edgeCreator) {        this.graph = graph;        this.edgeWeights = new HashMap<>(graph.getSize() * graph.getSize() * 2);        this.edgeCreator = edgeCreator;    }    /**     * This is the core method it first performs the christofidies algorithm and then does the optimization     */    public void runChristofides() {        try {            addEdgesBetweenNodes();            graph.test();            PointPlotter.initializePointPlotter();            //System.out.println("hello hello");            final long startTime = System.currentTimeMillis();            UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> mst = graph.getMSTByPrims();            double mstWeight = calculateGraphWeight(mst);            List<Node<NodeValue, NodeKeyValue>> oddEdgedNodes = getOddEdgesNodes(mst);            //for multimatch check            List<Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight>> edges = createMinimumWeightPerfectMatchingBruteForce(oddEdgedNodes);            optimizeMinimumWeightPerfectMatching(edges);            createMultiGraphFromMstAndEdges(mst, edges);            UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> multiGraphEulerian = mst;            //here in createEulerianTourFromMinWeightMultiMatchGraphStarter using external removal for edges and not using graphs method            List<Node<NodeValue, NodeKeyValue>> eulerianTour = createEulerianTourFromMinWeightMultiMatchGraphStarterTwo(multiGraphEulerian);            //without traversing multimatch            //List<Node<NodeValue, NodeKeyValue>> eulerianTour = createOrderFromDFSStarter(mst);            UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> christoTsp = createGraphWithSameNodes(this.graph);            generateTspTourFromEulerianTourStarter(eulerianTour, christoTsp);            //PointPlotter pointPlotter = new PointPlotter();            double tspWeight = calculateGraphWeight(christoTsp);            long christoTime = System.currentTimeMillis();            System.out.println("Christofidies Time : " + (christoTime - startTime)/1000);            System.out.println("TSP After Christofidies : " + tspWeight);            List<Node<NodeValue, NodeKeyValue>> order = createOrderFromDFSStarter(christoTsp);            //end of christofidies--------------------------------------------------------------------------            int processOfOptimization = 1;            if (processOfOptimization == 1) {                System.out.println("Degree of parallelism: " + ForkJoinPool.getCommonPoolParallelism());                // 2 OPT with simulated annealing                for (int i = 0; i < 1; i++) {                    System.out.println("Iteration: " + i);                    long time1 = System.currentTimeMillis();//                    ArrayList<Node<NodeValue, NodeKeyValue>> newList = new ArrayList<>(order);                    TwoOpt<NodeValue, NodeKeyValue, EdgeWeight> twoOpt = new TwoOpt<>(edgeWeights, order.size());                    tspWeight =                            twoOpt.parallelizeSimulatedAnnealingTwoOpt(12, order, calculateGraphWeight(christoTsp),                                            1, 0.0001, 26000, 20, 1, true,                                            false)                                    .get(0).getSecond();                    System.out.println("TSP weight after 2 Opt: " + tspWeight);                    // 3 OPT                    //ThreeOpt<NodeValue, NodeKeyValue, EdgeWeight> threeOpt = new ThreeOpt<>(edgeWeights, newList.size());                   //threeOpt.performThreeOpt(newList, tspWeight);                    //generating final graph                    christoTsp = createGraphWithSameNodes(this.graph);                    generateTspTourFromOrderStarter(order, christoTsp);//                    tspWeight = calculateGraphWeight(christoTsp);                    long time2 = System.currentTimeMillis();                    System.out.println("Mst weight :" + mstWeight);                    System.out.println("TSP weight :" + tspWeight);                    System.out.println("Time Taken seconds :" + (time2 - time1) / 1000);                    System.out.println("Ratio" + tspWeight/mstWeight + "\n\n\n");                }            } else if (processOfOptimization == 2) {                // Three Opt                ThreeOpt<NodeValue, NodeKeyValue, EdgeWeight> threeOpt = new ThreeOpt<>(edgeWeights, order.size());                tspWeight = threeOpt.performThreeOpt(order, tspWeight);                System.out.println("TSP After three opt : "+ tspWeight);                //tspWeight = threeOpt.runSimulatedAnnealing(order, tspWeight, 1, 0.0000001,                //        0, 10, 0);                // 2 OPT with simulated annealing                /*                SimulatedAnnealing<NodeValue, NodeKeyValue, EdgeWeight> simulatedAnnealing = new SimulatedAnnealing<>(edgeWeights);                tspWeight =                        simulatedAnnealing.parallelizeSimulatedAnnealingTwoOpt(12, order, tspWeight,                                        1, 0.001, 10000, 55, 1, true,                                        false)                                .get(0).getSecond();                System.out.println("TSP After two opt parallel : "+ tspWeight);                 */                //creating graph                christoTsp = createGraphWithSameNodes(this.graph);                generateTspTourFromOrderStarter(order, christoTsp);            } else if (processOfOptimization == 3) {                SimulatedAnnealing<NodeValue, NodeKeyValue, EdgeWeight> simulatedAnnealing = new SimulatedAnnealing<>(edgeWeights);                FileWriter fileWriter = new FileWriter();                fileWriter.start2optWriting();                for (int maxIteration = 25000; maxIteration <= 40000; maxIteration += 1000) {                    for (double coolingRate = 0.0001; coolingRate <= 0.0001; coolingRate *= 10) {                        for (int equliburmCountTemperature = 20; equliburmCountTemperature <= 20; equliburmCountTemperature += 5) {                            ArrayList<Node<NodeValue, NodeKeyValue>> list = new ArrayList<>(order);                            long time1 = System.currentTimeMillis();                            List<Pair<List<Node<NodeValue, NodeKeyValue>>, Double>> listOfTours =                                    simulatedAnnealing.parallelizeSimulatedAnnealingTwoOpt(12, list, tspWeight,                                            1, coolingRate, maxIteration, equliburmCountTemperature, 1, false,                                            true);                            long time2 = System.currentTimeMillis();                            fileWriter.writeLine(12, listOfTours.stream().map(p -> p.getSecond())                                            .collect(Collectors.toList()), tspWeight, 1, coolingRate, maxIteration,                                    equliburmCountTemperature, 1, (time2 - time1));                        }                    }                }            } else if (processOfOptimization == 4) {                //plain two opt                TwoOpt<NodeValue, NodeKeyValue, EdgeWeight> twoOpt = new TwoOpt<>(edgeWeights, order.size());                tspWeight = twoOpt.performTwoOpt(order, tspWeight);                System.out.println("Tsp weight after basic Two opt : " + tspWeight);                //plain three Opt                //parallelize this three opt                //check simulated annealing for three opt                //optimize thsi three opt                ThreeOpt<NodeValue, NodeKeyValue, EdgeWeight> threeOpt = new ThreeOpt<>(edgeWeights, order.size());                tspWeight = threeOpt.performThreeOpt(order, tspWeight);                System.out.println("Tsp weight after basic Three opt : " + tspWeight);                christoTsp = createGraphWithSameNodes(this.graph);                generateTspTourFromOrderStarter(order, christoTsp);            } else if (processOfOptimization == 5)  {                ThreeOpt<NodeValue, NodeKeyValue, EdgeWeight> threeOpt = new ThreeOpt<>(edgeWeights, order.size());                tspWeight = threeOpt.runSimulatedAnnealing(order, tspWeight, 1, 0.01, 12000,                        20, 1);                christoTsp = createGraphWithSameNodes(this.graph);                generateTspTourFromOrderStarter(order, christoTsp);            } else if (processOfOptimization == 6) {                ThreeOpt<NodeValue, NodeKeyValue, EdgeWeight> threeOpt = new ThreeOpt<>(edgeWeights, order.size());                tspWeight = threeOpt.runSimulatedAnnealing(order, tspWeight, 1, 0.01, 12000,                        20, 1);                System.out.println("TSP weight after 3 Opt: " + tspWeight);                SimulatedAnnealing<NodeValue, NodeKeyValue, EdgeWeight> simulatedAnnealing = new SimulatedAnnealing<>(edgeWeights);                tspWeight =                        simulatedAnnealing.parallelizeSimulatedAnnealingTwoOpt(12, order, tspWeight,                                        1, 0.001, 26000, 20, 1, true,                                        false)                                .get(0).getSecond();                System.out.println("TSP weight after 2 Opt: " + tspWeight);                christoTsp = createGraphWithSameNodes(this.graph);                generateTspTourFromOrderStarter(order, christoTsp);            } else {                SimulatedAnnealing<NodeValue, NodeKeyValue, EdgeWeight> simulatedAnnealing = new SimulatedAnnealing<>(edgeWeights);                FileWriter fileWriter = new FileWriter();                fileWriter.start2optWriting();                for (int maxIteration = 10000; maxIteration <= 30000; maxIteration += 5000) {                    for (double coolingRate = 0.01; coolingRate >= 0.0001; coolingRate = coolingRate/10) {                        for (int equliburmCountTemperature = 30; equliburmCountTemperature <= 30; equliburmCountTemperature += 5) {                            ArrayList<Node<NodeValue, NodeKeyValue>> list = new ArrayList<>(order);                            long time1 = System.currentTimeMillis();                            ThreeOpt<NodeValue, NodeKeyValue, EdgeWeight> threeOpt = new ThreeOpt<>(this.edgeWeights, order.size());                            List<Pair<List<Node<NodeValue, NodeKeyValue>>, Double>> listOfTours =                                    threeOpt.parallelizeSimulatedAnnealingThreeOpt(12, list, tspWeight,                                            1, coolingRate, maxIteration, equliburmCountTemperature, 1, false,                                            true);                            long time2 = System.currentTimeMillis();                            fileWriter.writeLine(12, listOfTours.stream().map(p -> p.getSecond())                                            .collect(Collectors.toList()), tspWeight, 1, coolingRate, maxIteration,                                    equliburmCountTemperature, 1, (time2 - time1));                        }                    }                }            }            System.out.println("here 2");            plotGraph(christoTsp);            tspWeight = calculateGraphWeight(christoTsp);            final long endTime = System.currentTimeMillis();            System.out.println("Total execution time: " + (endTime - startTime)/1000);            System.out.println("Mst weight :" + mstWeight);            System.out.println("TSP weight :" + tspWeight);            System.out.println("Ratio" + tspWeight/mstWeight);        } catch (Exception e) {            System.out.println("exception while performing christofedies : " +  Arrays.stream(e.getStackTrace()).map(p -> p.toString()).collect(Collectors.joining(", ")));        }    }    /**     * This adds all the edges to the graph object to create a complete graph in the christofidies and also populates the edge Weights     */    private void addEdgesBetweenNodes() {        List<Node<NodeValue, NodeKeyValue>> nodeList = graph.getNodes();        for (int i = 0; i < nodeList.size() - 1; i++) {            for (int j = i + 1; j < nodeList.size(); j++) {                EdgeWeight edgeWeight =  (EdgeWeight) getDistanceBetweenNodes((Node<String, LatLongId>) nodeList.get(i),                        (Node<String, LatLongId>) nodeList.get(j));                System.out.println();                graph.addEdge(nodeList.get(i), nodeList.get(j), edgeWeight);                edgeWeights.put(new Pair<>(nodeList.get(i), nodeList.get(j)), edgeWeight);                edgeWeights.put(new Pair<>(nodeList.get(j), nodeList.get(i)), edgeWeight);            }        }    }    /**     * It creates a graph with same nodes give by the input graph     * @param graph object of type UndirectedEdgeWeighedListGraph which needs to be copied     * @return an graph with same nodes in a new graph object     */    private UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> createGraphWithSameNodes(            UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> graph) {        UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> graphCopy = new UndirectedEdgeWeighedListGraph<>(edgeCreator);        for (int i = 0; i < graph.getNodes().size(); i++) {            graphCopy.addNode(graph.getNodes().get(i));        }        return graphCopy;    }    /**     * This calculates distance between two nodes by the latitude and longitude logic     * @param node1 Node object one     * @param node2 Node Object Two     * @return Returns distance between two nodes in Double     */    private Double getDistanceBetweenNodes(Node<String, LatLongId> node1, Node<String, LatLongId> node2) {/*        System.out.print("Lat 1 = " + node1.getKey().getValue().getLatitude() + "Long 1: " +                node1.getKey().getValue().getLongitude());        System.out.print("--> Lat 2 = " + node2.getKey().getValue().getLatitude() + "Long 2: " +                node2.getKey().getValue().getLongitude());        System.out.println(); */        double lat1Rad = Math.toRadians(node1.getKey().getValue().getLatitude());        double lon1Rad = Math.toRadians(node1.getKey().getValue().getLongitude());        double lat2Rad = Math.toRadians(node2.getKey().getValue().getLatitude());        double lon2Rad = Math.toRadians(node2.getKey().getValue().getLongitude());        double dlon = lon2Rad - lon1Rad;        double dlat = lat2Rad - lat1Rad;        double a = Math.sin(dlat/2) * Math.sin(dlat/2) +                Math.cos(lat1Rad) * Math.cos(lat2Rad) *                        Math.sin(dlon/2) * Math.sin(dlon/2);        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));        double distance = EARTH_RADIUS * c;        //System.out.println("distance : " + distance);        return distance;//roundToDecimalPlaces(distance, 6);    }    /*    // Round a double value to a specified number of decimal places    private static double roundToDecimalPlaces(double value, int decimalPlaces) {        BigDecimal bd = new BigDecimal(Double.toString(value));        bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);        return bd.doubleValue();    }     */    /**     Returns a list of nodes in the given graph that have an odd number of edges.     @param graph the undirected weighted graph to search for odd edges nodes in     @return a list of nodes that have an odd number of edges     */    private List<Node<NodeValue, NodeKeyValue>> getOddEdgesNodes(UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> graph) {        List<Node<NodeValue, NodeKeyValue>> oddNodes = new ArrayList<>();        for (Node<NodeValue, NodeKeyValue> node : graph.getNodes()) {            if (graph.getNeighbours(node.getKey()).size() % 2 != 0) {                oddNodes.add(node);            }        }        return oddNodes;    }    /**     Creates a minimum weight perfect matching for a given list of odd nodes in a graph using a brute force approach.     @param listOfOddNode a list of odd nodes in the graph to create the matching for     @return a list of edges representing the minimum weight perfect matching for the given list of odd nodes     @throws Exception if an error occurs while creating the edges     */    private List<Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight>> createMinimumWeightPerfectMatchingBruteForce(            List<Node<NodeValue, NodeKeyValue>> listOfOddNode) {        try {            Set<Key<NodeKeyValue>> setOfNodesDone = new HashSet<>();            List<Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight>> listOfEdges = new LinkedList<>();            for (int i = 0; i < listOfOddNode.size() - 1; i++) {                if (setOfNodesDone.contains(listOfOddNode.get(i).getKey())) {                    continue;                }                //int minIndex = -1;                int jMin = -1;                EdgeWeight minEdgeWeight = null;                for (int j = i + 1; j < listOfOddNode.size(); j++) {                    if (setOfNodesDone.contains(listOfOddNode.get(j).getKey())) {                        continue;                    }                    if (minEdgeWeight == null                     || minEdgeWeight.compareTo(edgeWeights.get(new Pair<>(listOfOddNode.get(i), listOfOddNode.get(j)))) > 0) {                        jMin = j;                        minEdgeWeight = edgeWeights.get(new Pair<>(listOfOddNode.get(i), listOfOddNode.get(j)));                    }                }                //System.out.println("Jmin " + jMin + " i " + i);                listOfEdges.add(edgeCreator.createEdge(listOfOddNode.get(i), listOfOddNode.get(jMin), minEdgeWeight));                setOfNodesDone.add(listOfOddNode.get(i).getKey());                setOfNodesDone.add(listOfOddNode.get(jMin).getKey());            }            return listOfEdges;        } catch (Exception e) {            System.out.println("Exception while creating edges " + Arrays.stream(e.getStackTrace()).map(p -> p.toString()).collect(Collectors.joining(", ")));            throw e;        }    }    /**     * Optimizes the minimum weight perfect matching algorithm. by checking if better edge exist between four nodes     * @param listOfEdges a list of edges.     * @return void     */    private void optimizeMinimumWeightPerfectMatching(            List<Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight>> listOfEdges) {        int iterations = 0;        Double delta = 0.0;        while (iterations < 2) {            iterations++;            delta = 0.0;            for (int i = 0; i < listOfEdges.size(); i++) {                for (int j = i + 1; j < listOfEdges.size(); j++) {                    delta += optimizeMinimumWeightPerfectMatchingHelper(listOfEdges, i, j);                }            }            if (delta >= 0) {                //break;            }        }    }    /**     * Helper method for optimizing the weight of minimum weight perfect matching.     * @param edges List of edges     * @param index1 Index of first edge     * @param index2 Index of second edge     * @return Returns the difference in weight of the old edges and the new edges     *     */    private Double optimizeMinimumWeightPerfectMatchingHelper(            List<Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight>> edges, int index1, int index2) {        Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edge1 = edges.get(index1);        Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edge2 = edges.get(index2);        Double currentWeight = (Double) edge1.getEdgeWeight() + (Double) edge2.getEdgeWeight();        Double newEdgeOneLength = (Double) edgeWeights.get(new Pair<>(edge1.getFirstNode(), edge2.getFirstNode())) +                (Double) edgeWeights.get(new Pair<>(edge1.getSecondNode(), edge2.getSecondNode()));        Double newEdgeTwoLength = (Double) edgeWeights.get(new Pair<>(edge1.getFirstNode(), edge2.getSecondNode())) +                (Double) edgeWeights.get(new Pair<>(edge1.getSecondNode(), edge2.getFirstNode()));        if (newEdgeOneLength < newEdgeTwoLength) {            if (newEdgeOneLength < currentWeight) {                Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> newEdge1 = edgeCreator.createEdge(edge1.getFirstNode(),                        edge2.getFirstNode(), edgeWeights.get(new Pair<>(edge1.getFirstNode(), edge2.getFirstNode())));                Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> newEdge2 = edgeCreator.createEdge(edge1.getSecondNode(),                        edge2.getSecondNode(), edgeWeights.get(new Pair<>(edge1.getSecondNode(), edge2.getSecondNode())));                edges.set(index1, newEdge1);                edges.set(index2, newEdge2);                return newEdgeOneLength - currentWeight;            }        } else {            if (newEdgeTwoLength < currentWeight) {                Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> newEdge1 = edgeCreator.createEdge(edge1.getFirstNode(),                        edge2.getSecondNode(), edgeWeights.get(new Pair<>(edge1.getFirstNode(), edge2.getSecondNode())));                Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> newEdge2 = edgeCreator.createEdge(edge1.getSecondNode(),                        edge2.getFirstNode(), edgeWeights.get(new Pair<>(edge1.getSecondNode(), edge2.getFirstNode())));                edges.set(index1, newEdge1);                edges.set(index2, newEdge2);                return newEdgeTwoLength - currentWeight;            }        }        return 0.0;    }    private List<Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight>> createEdgesForOddNodesBlossoms(List<Node<NodeValue, NodeKeyValue>> listOfOddNode) {        return null;    }    /**     Creates a multi-graph by adding all edges from the given list which is minimum weight perfect matching to the given MST.     @param mst the minimum spanning tree (MST) to add the edges to.     @param edges the list of edges to add to the MST.     */    public void createMultiGraphFromMstAndEdges(UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> mst,                                    List<Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight>> edges) {        for (Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edge : edges) {            mst.addEdge(edge.getFirstNode(), edge.getSecondNode(), edge.getEdgeWeight());        }    }    /**     * Creates a node order list using a Depth-First-Search algorithm starting from the first node in the graph.     * @param graph the graph to create the order list from     * @return the list of nodes in the order visited by the DFS algorithm     */    public List<Node<NodeValue, NodeKeyValue>> createOrderFromDFSStarter(            UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> graph) {        Set<Key<NodeKeyValue>> visited = new HashSet<>();        Set<Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight>> edgeSet = new HashSet<>();        List<Node<NodeValue, NodeKeyValue>> orderList = new LinkedList<>();        createOrderFromDFS(visited, graph, graph.getNodes().get(0), orderList);        return orderList;    }    /**     *     * Recursively creates an order of nodes in the graph using Depth-First Search (DFS) starting from the given node.     * @param visited a set of visited nodes to keep track of visited nodes during the DFS traversal     * @param graph the graph to traverse     * @param node the starting node for DFS traversal     * @param orderList a list to store the order of nodes in the graph     */    public void createOrderFromDFS(Set<Key<NodeKeyValue>> visited,                                                                UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> graph,                                                                Node<NodeValue, NodeKeyValue> node,                                                                List<Node<NodeValue, NodeKeyValue>> orderList) {        if (visited.contains(node.getKey())) {            return;        }        visited.add(node.getKey());        orderList.add(node);        for (Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edge : graph.getNeighbours(node.getKey())) {            createOrderFromDFS(visited, graph, edge.getSecondNode(), orderList);        }    }    /**     *     * Creates an Eulerian tour from a minimum weight multi-match graph     * @param graph an undirected weighted graph of NodeValue and NodeKeyValue nodes and EdgeWeight edges     * @return a list of nodes representing the order of the Eulerian tour     */    public List<Node<NodeValue, NodeKeyValue>> createEulerianTourFromMinWeightMultiMatchGraphStarterTwo(            UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> graph) {        Set<Key<NodeKeyValue>> visited = new HashSet<>();        List<Node<NodeValue, NodeKeyValue>> orderList = new LinkedList<>();        createEulerianTourFromMinWeightMultiMatchGraphTwo(visited, graph, graph.getNodes().get(0), orderList);        return orderList;    }    /**     *     * Creates an Eulerian tour from a minimum weight multi-match graph starting from a specified node.     * @param visited a set of visited nodes     * @param graph an undirected weighted graph of NodeValue and NodeKeyValue nodes and EdgeWeight edges     * @param node the starting node for the Eulerian tour     * @param orderList a list of nodes representing the order of the Eulerian tour     */    public void createEulerianTourFromMinWeightMultiMatchGraphTwo(Set<Key<NodeKeyValue>> visited,                                                                UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> graph,                                                                Node<NodeValue, NodeKeyValue> node,                                                                List<Node<NodeValue, NodeKeyValue>> orderList) {        if (visited.contains(node.getKey())) {            return;        }        visited.add(node.getKey());        orderList.add(node);        for (Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edge : graph.getNeighbours(node.getKey())) {            createEulerianTourFromMinWeightMultiMatchGraphTwo(visited, graph, edge.getSecondNode(), orderList);        }    }    /**     *     * Generates a TSP tour from an order of nodes     * @param eulerianTour a list of nodes representing the order of an tour     * @param graph an undirected weighted graph of NodeValue and NodeKeyValue nodes and EdgeWeight edges     */    public void generateTspTourFromOrderStarter(List<Node<NodeValue, NodeKeyValue>> eulerianTour,                                                        UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> graph) {        Set<Node<NodeValue, NodeKeyValue>> visitedNodes = new HashSet<>();        Node<NodeValue, NodeKeyValue> previousNode = eulerianTour.get(0);        int currentNodeIndex = 1;        int lastNodeUsed = -1;        visitedNodes.add(previousNode);        while (visitedNodes.size() != graph.getSize()) {            if (visitedNodes.contains(eulerianTour.get(currentNodeIndex))) {                currentNodeIndex++;                continue;            }            graph.addEdge(previousNode, eulerianTour.get(currentNodeIndex),                    edgeWeights.get(new Pair<>(previousNode, eulerianTour.get(currentNodeIndex))));            lastNodeUsed = currentNodeIndex;            previousNode = eulerianTour.get(currentNodeIndex);            visitedNodes.add(previousNode);            currentNodeIndex++;        }        graph.addEdge(eulerianTour.get(0), eulerianTour.get(lastNodeUsed),                edgeWeights.get(new Pair<>(eulerianTour.get(0), eulerianTour.get(lastNodeUsed))));    }    /**     *     * Generates a TSP tour from an order,     * starting at a random node in the order.     * @param eulerianTour a list of nodes representing the order of an order of nodes     * @param graph an undirected weighted graph of NodeValue and NodeKeyValue nodes and EdgeWeight edges     */    public void generateTspTourFromEulerianTourStarter(List<Node<NodeValue, NodeKeyValue>> eulerianTour,                                                 UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> graph) {        Set<Node<NodeValue, NodeKeyValue>> visitedNodes = new HashSet<>();        int startIndex = new Random().nextInt(graph.getSize() - 1);        Node<NodeValue, NodeKeyValue> previousNode = eulerianTour.get(startIndex);        int currentNodeIndex = startIndex + 1;        int lastNodeUsed = -1;        visitedNodes.add(previousNode);        while (visitedNodes.size() != graph.getSize()) {            //System.out.println("max value = " + (graph.getSize() - 1) +  "currentIndex = " + currentNodeIndex);            if (visitedNodes.contains(eulerianTour.get(currentNodeIndex))) {                currentNodeIndex = (currentNodeIndex + 1) % graph.getSize();                continue;            }            graph.addEdge(previousNode, eulerianTour.get(currentNodeIndex),                    edgeWeights.get(new Pair<>(previousNode, eulerianTour.get(currentNodeIndex))));            lastNodeUsed = currentNodeIndex;            previousNode = eulerianTour.get(currentNodeIndex);            visitedNodes.add(previousNode);            currentNodeIndex = (currentNodeIndex + 1) % graph.getSize();        }        graph.addEdge(eulerianTour.get(startIndex), eulerianTour.get(lastNodeUsed),                edgeWeights.get(new Pair<>(eulerianTour.get(startIndex), eulerianTour.get(lastNodeUsed))));    }    public void plotEdges(java.util.List<Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight>> edgeList) {        //java.util.List<Edge<Node<String, LatLongId>, Double>> edgeList =        for (Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edge : edgeList) {            PointPlotter.addLineOuter(((LatLongId) edge.getFirstNode().getKey().getValue()).getLatitude(),                    ((LatLongId)edge.getFirstNode().getKey().getValue()).getLongitude(),                    ((LatLongId)edge.getSecondNode().getKey().getValue()).getLatitude(),                    ((LatLongId)edge.getSecondNode().getKey().getValue()).getLongitude(),                    Color.GREEN);        }    }    public void plotGraph(UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> graph) {        for (Node<NodeValue, NodeKeyValue> node : graph.getNodes()) {            PointPlotter.addPointOuter(((LatLongId)node.getKey().getValue()).getLatitude(),                    ((LatLongId)node.getKey().getValue()).getLongitude(), Color.RED);        }        for (Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edge : graph.getEdges().keySet()) {            PointPlotter.addLineOuter(((LatLongId) edge.getFirstNode().getKey().getValue()).getLatitude(),                    ((LatLongId)edge.getFirstNode().getKey().getValue()).getLongitude(),                    ((LatLongId)edge.getSecondNode().getKey().getValue()).getLatitude(),                    ((LatLongId)edge.getSecondNode().getKey().getValue()).getLongitude(),                    Color.BLUE);        }    }    public Double calculateGraphWeight(UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> graph) {        Double ans = 0D;        for (Node<NodeValue, NodeKeyValue> node : graph.getNodes()) {            for (Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edge : graph.getNeighbours(node.getKey())) {                ans = ans +  (Double) edge.getEdgeWeight();                //System.out.println("Edge weight : " + (Double) edge.getEdgeWeight());            }        }        return ans/2;    }    public Map<Pair<Node<NodeValue, NodeKeyValue>, Node<NodeValue, NodeKeyValue>>, EdgeWeight> getEdgeWeights() {        return edgeWeights;    }    public void setEdgeWeights(Map<Pair<Node<NodeValue, NodeKeyValue>, Node<NodeValue, NodeKeyValue>>, EdgeWeight> edgeWeights) {        this.edgeWeights = edgeWeights;    }}