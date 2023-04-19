package info6205.Graph;

import info6205.Graph.Problems.TravellingSalesMan.Algorithm.Christofides;
import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.EdgeALG;
import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.LatLongId;
import info6205.Graph.Utils.Pair;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static info6205.Graph.Utility.Utility.createNode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChristofidesTest {

    @Test
    public void testAddEdgesBetweenNodes() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = new UndirectedEdgeWeighedListGraph<>(EdgeALG::new);
        Node<String, LatLongId> node1 = createNode(1.0, 1.0, "1");
        Node<String, LatLongId> node2 = createNode(2.0, 2.0, "2");
        Node<String, LatLongId> node3 = createNode(3.0, 3.0, "3");
        Node<String, LatLongId> node4 = createNode(4.0, 4.0, "4");
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        Method addEdgesBetweenNodesMethod = getMethodByName("addEdgesBetweenNodes");
        Christofides<String, LatLongId, Double> christofides = new Christofides<>(graph, EdgeALG::new);
        addEdgesBetweenNodesMethod.invoke(christofides);
        assertEquals(6, graph.getEdges().size());
        assertEquals(3, graph.getNeighbours(node1.getKey()).size());
        assertEquals(3, graph.getNeighbours(node2.getKey()).size());
        assertEquals(3, graph.getNeighbours(node3.getKey()).size());
        assertEquals(3, graph.getNeighbours(node4.getKey()).size());
    }

    @Test
    public void testCreateGraphWithSameNodes() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = new UndirectedEdgeWeighedListGraph<>(EdgeALG::new);
        Node<String, LatLongId> node1 = createNode(1.0, 1.0, "1");
        Node<String, LatLongId> node2 = createNode(2.0, 2.0, "2");
        Node<String, LatLongId> node3 = createNode(3.0, 3.0, "3");
        Node<String, LatLongId> node4 = createNode(4.0, 4.0, "4");
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        Christofides<String, LatLongId, Double> christofides = new Christofides<>(graph, EdgeALG::new);
        Method createGraphWithSameNodesMethod = getMethodByName("createGraphWithSameNodes");
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> copyGraph = (UndirectedEdgeWeighedListGraph<String, LatLongId, Double>) createGraphWithSameNodesMethod.invoke(christofides, graph);
        assertTrue(graph.equals(copyGraph));
    }

    @Test
    public void testGetOddEdgesNodes() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = new UndirectedEdgeWeighedListGraph<>(EdgeALG::new);
        Node<String, LatLongId> node1 = createNode(1.0, 1.0, "1");
        Node<String, LatLongId> node2 = createNode(2.0, 2.0, "2");
        Node<String, LatLongId> node3 = createNode(3.0, 3.0, "3");
        Node<String, LatLongId> node4 = createNode(4.0, 4.0, "4");
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        graph.addEdge(node1, node2, 2D);
        graph.addEdge(node1, node3, 2D);
        graph.addEdge(node3, node4, 2D);
        HashSet<Node<String, LatLongId>> oddEdgesNodeSet = new HashSet<>();
        oddEdgesNodeSet.add(node2);
        oddEdgesNodeSet.add(node4);
        Christofides<String, LatLongId, Double> christofides = new Christofides<>(graph, EdgeALG::new);
        Method getOddEdgesNodesMethod = getMethodByName("getOddEdgesNodes");
        List<Node<String, LatLongId>> oddEdgeNodeListToBeTested = (List<Node<String, LatLongId>>) getOddEdgesNodesMethod.invoke(christofides, graph);
        assertTrue(oddEdgesNodeSet.containsAll(oddEdgeNodeListToBeTested));
    }

    @Test
    public void testCreateMinimumWeightPerfectMatchingBruteForce() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = new UndirectedEdgeWeighedListGraph<>(EdgeALG::new);
        Node<String, LatLongId> node1 = createNode(1.0, 1.0, "1");
        Node<String, LatLongId> node2 = createNode(2.0, 2.0, "2");
        Node<String, LatLongId> node3 = createNode(3.0, 3.0, "3");
        Node<String, LatLongId> node4 = createNode(4.0, 4.0, "4");
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        graph.addEdge(node1, node2, 2D);
        graph.addEdge(node1, node3, 2D);
        graph.addEdge(node3, node4, 2D);
        Map<Pair<Node<String, LatLongId>, Node<String, LatLongId>>, Double> edgeWeights = new HashMap<>();

        edgeWeights.put(new Pair<>(node1, node2), 1D);
        edgeWeights.put(new Pair<>(node2, node1), 1D);
        edgeWeights.put(new Pair<>(node1, node3), 2D);
        edgeWeights.put(new Pair<>(node3, node1), 2D);
        edgeWeights.put(new Pair<>(node3, node4), 4D);
        edgeWeights.put(new Pair<>(node4, node3), 4D);
        edgeWeights.put(new Pair<>(node4, node2), 2D);
        edgeWeights.put(new Pair<>(node2, node4), 2D);
        edgeWeights.put(new Pair<>(node1, node4), 3D);
        edgeWeights.put(new Pair<>(node4, node1), 3D);
        edgeWeights.put(new Pair<>(node2, node3), 3D);
        edgeWeights.put(new Pair<>(node3, node2), 3D);

        List<Node<String, LatLongId>> oddEdgesNodeList = new ArrayList<>();
        oddEdgesNodeList.add(node2);
        oddEdgesNodeList.add(node4);
        Christofides<String, LatLongId, Double> christofides = new Christofides<>(graph, EdgeALG::new);
        christofides.setEdgeWeights(edgeWeights);
        Method createMinimumWeightPerfectMatchingBruteForceMethod = getMethodByName("createMinimumWeightPerfectMatchingBruteForce");
        List<Edge<Node<String, LatLongId>, Double>> multiMatchEdges = (List<Edge<Node<String, LatLongId>, Double>>) createMinimumWeightPerfectMatchingBruteForceMethod
                .invoke(christofides, graph.getNodes());
        assertEquals(2, multiMatchEdges.size());
    }


    @Test
    public void testOptimizeMinimumWeightPerfectMatching() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = new UndirectedEdgeWeighedListGraph<>(EdgeALG::new);
        Node<String, LatLongId> node1 = createNode(1.0, 1.0, "1");
        Node<String, LatLongId> node2 = createNode(2.0, 2.0, "2");
        Node<String, LatLongId> node3 = createNode(3.0, 3.0, "3");
        Node<String, LatLongId> node4 = createNode(4.0, 4.0, "4");

        Map<Pair<Node<String, LatLongId>, Node<String, LatLongId>>, Double> edgeWeights = new HashMap<>();

        edgeWeights.put(new Pair<>(node1, node2), 1D);
        edgeWeights.put(new Pair<>(node2, node1), 1D);
        edgeWeights.put(new Pair<>(node1, node3), 2D);
        edgeWeights.put(new Pair<>(node3, node1), 2D);
        edgeWeights.put(new Pair<>(node3, node4), 5D);
        edgeWeights.put(new Pair<>(node4, node3), 5D);
        edgeWeights.put(new Pair<>(node4, node2), 2D);
        edgeWeights.put(new Pair<>(node2, node4), 2D);
        edgeWeights.put(new Pair<>(node1, node4), 3D);
        edgeWeights.put(new Pair<>(node4, node1), 3D);
        edgeWeights.put(new Pair<>(node3, node2), 3D);
        edgeWeights.put(new Pair<>(node2, node3), 3D);


        List<Edge<Node<String, LatLongId>, Double>> edgeWeightList = new ArrayList<>();

        edgeWeightList.add(new EdgeALG(node1, node2, 1D));
        edgeWeightList.add(new EdgeALG(node3, node4, 5D));

        Christofides<String, LatLongId, Double> christofides = new Christofides<>(graph, EdgeALG::new);
        christofides.setEdgeWeights(edgeWeights);
        Method optimizeMinimumWeightPerfectMatchingMethod = getMethodByName("optimizeMinimumWeightPerfectMatching");
        optimizeMinimumWeightPerfectMatchingMethod.invoke(christofides, edgeWeightList);
        assertEquals(4.0, edgeWeightList.stream().map(p -> p.getEdgeWeight()).mapToDouble(Double::doubleValue) // Convert Stream<Double> to DoubleStream
                .sum(), 0);
    }

    @Test
    public void testCreateMultiGraphFromMstAndEdges() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = new UndirectedEdgeWeighedListGraph<>(EdgeALG::new);
        Node<String, LatLongId> node1 = createNode(1.0, 1.0, "1");
        Node<String, LatLongId> node2 = createNode(2.0, 2.0, "2");
        Node<String, LatLongId> node3 = createNode(3.0, 3.0, "3");
        Node<String, LatLongId> node4 = createNode(4.0, 4.0, "4");
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        graph.addEdge(node1, node3, 2D);
        graph.addEdge(node1, node2, 1D);
        graph.addEdge(node2, node4, 2D);

        Map<Pair<Node<String, LatLongId>, Node<String, LatLongId>>, Double> edgeWeights = new HashMap<>();

        edgeWeights.put(new Pair<>(node1, node2), 1D);
        edgeWeights.put(new Pair<>(node2, node1), 1D);
        edgeWeights.put(new Pair<>(node1, node3), 2D);
        edgeWeights.put(new Pair<>(node3, node1), 2D);
        edgeWeights.put(new Pair<>(node3, node4), 5D);
        edgeWeights.put(new Pair<>(node4, node3), 5D);
        edgeWeights.put(new Pair<>(node4, node2), 2D);
        edgeWeights.put(new Pair<>(node2, node4), 2D);
        edgeWeights.put(new Pair<>(node1, node4), 3D);
        edgeWeights.put(new Pair<>(node4, node1), 3D);
        edgeWeights.put(new Pair<>(node3, node2), 3D);
        edgeWeights.put(new Pair<>(node2, node3), 3D);


        List<Edge<Node<String, LatLongId>, Double>> edgeWeightList = new ArrayList<>();

        edgeWeightList.add(new EdgeALG(node3, node4, 5D));

        Christofides<String, LatLongId, Double> christofides = new Christofides<>(graph, EdgeALG::new);
        christofides.setEdgeWeights(edgeWeights);
        Method createGraphWithSameNodesMethod = getMethodByName("createMultiGraphFromMstAndEdges");
        createGraphWithSameNodesMethod.invoke(christofides, graph, edgeWeightList);
        for (Node<String, LatLongId> node : graph.getNodes()) {
            assertEquals(0, graph.getNeighbours(node.getKey()).size() % 2, 0);
        }

    }

    //createOrderFromDFSStarter

    @Test
    public void testCreateOrderFromDFSStarter() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = new UndirectedEdgeWeighedListGraph<>(EdgeALG::new);
        Node<String, LatLongId> node1 = createNode(1.0, 1.0, "1");
        Node<String, LatLongId> node2 = createNode(2.0, 2.0, "2");
        Node<String, LatLongId> node3 = createNode(3.0, 3.0, "3");
        Node<String, LatLongId> node4 = createNode(4.0, 4.0, "4");
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        graph.addEdge(node1, node3, 2D);
        graph.addEdge(node1, node2, 1D);
        graph.addEdge(node2, node4, 2D);
        graph.addEdge(node3, node4, 2D);

        Christofides<String, LatLongId, Double> christofides = new Christofides<>(graph, EdgeALG::new);
        Method createOrderFromDFSStarterMethod = getMethodByName("createOrderFromDFSStarter");
        List<Node<String, LatLongId>> order = (List<Node<String, LatLongId>>) createOrderFromDFSStarterMethod.invoke(christofides, graph);

        HashSet<String> set = new HashSet<>();
        set.add("1,3,4,2,");set.add("2,4,3,1,");
        set.add("2,1,3,4,");set.add("4,3,1,2,");
        set.add("4,2,1,3,");set.add("3,1,2,4,");
        set.add("3,4,2,1,");set.add("1,2,4,3,");

        String orderString = "";
        for (int i = 0; i < order.size(); i++) {
            orderString += order.get(i).getValue() + ",";
        }
        assertTrue(set.contains(orderString));
    }

    //createEulerianTourFromMinWeightMultiMatchGraphStarterTwo

    @Test
    public void testCreateEulerianTourFromMinWeightMultiMatchGraphStarterTwo() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = new UndirectedEdgeWeighedListGraph<>(EdgeALG::new);
        Node<String, LatLongId> node1 = createNode(1.0, 1.0, "1");
        Node<String, LatLongId> node2 = createNode(2.0, 2.0, "2");
        Node<String, LatLongId> node3 = createNode(3.0, 3.0, "3");
        Node<String, LatLongId> node4 = createNode(4.0, 4.0, "4");
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        graph.addEdge(node1, node3, 2D);
        graph.addEdge(node1, node2, 1D);
        graph.addEdge(node2, node4, 2D);
        graph.addEdge(node3, node4, 2D);

        Christofides<String, LatLongId, Double> christofides = new Christofides<>(graph, EdgeALG::new);
        Method createEulerianTourFromMinWeightMultiMatchGraphStarterTwoMethod = getMethodByName("createEulerianTourFromMinWeightMultiMatchGraphStarterTwo");
        List<Node<String, LatLongId>> order = (List<Node<String, LatLongId>>) createEulerianTourFromMinWeightMultiMatchGraphStarterTwoMethod
                .invoke(christofides, graph);

        HashSet<Node<String, LatLongId>> set = new HashSet<>();
        set.add(node1);set.add(node2);
        set.add(node3);set.add(node4);

        assertTrue(set.containsAll(order));
    }

    //generateTspTourFromOrderStarter

    @Test
    public void testGenerateTspTourFromOrderStarter() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = new UndirectedEdgeWeighedListGraph<>(EdgeALG::new);
        Node<String, LatLongId> node1 = createNode(1.0, 1.0, "1");
        Node<String, LatLongId> node2 = createNode(2.0, 2.0, "2");
        Node<String, LatLongId> node3 = createNode(3.0, 3.0, "3");
        Node<String, LatLongId> node4 = createNode(4.0, 4.0, "4");
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        List<Node<String, LatLongId>> order = new ArrayList<>();
        order.add(node1);
        order.add(node2);
        order.add(node3);
        order.add(node4);

        Map<Pair<Node<String, LatLongId>, Node<String, LatLongId>>, Double> edgeWeights = new HashMap<>();

        edgeWeights.put(new Pair<>(node1, node2), 1D);
        edgeWeights.put(new Pair<>(node2, node1), 1D);

        edgeWeights.put(new Pair<>(node1, node4), 2D);
        edgeWeights.put(new Pair<>(node4, node1), 2D);

        edgeWeights.put(new Pair<>(node3, node4), 5D);
        edgeWeights.put(new Pair<>(node4, node3), 5D);

        edgeWeights.put(new Pair<>(node3, node2), 2D);
        edgeWeights.put(new Pair<>(node2, node3), 2D);


        Christofides<String, LatLongId, Double> christofides = new Christofides<>(graph, EdgeALG::new);
        christofides.setEdgeWeights(edgeWeights);

        Method createEulerianTourFromMinWeightMultiMatchGraphStarterTwoMethod = getMethodByName("generateTspTourFromOrderStarter");
        createEulerianTourFromMinWeightMultiMatchGraphStarterTwoMethod
                .invoke(christofides, order,graph);

        assertEquals(2, graph.getNeighbours(node1.getKey()).size());
        assertEquals(2, graph.getNeighbours(node2.getKey()).size());
        assertEquals(2, graph.getNeighbours(node3.getKey()).size());
        assertEquals(2, graph.getNeighbours(node4.getKey()).size());
        Set<Node<String, LatLongId>> set = new HashSet<>();
        set.add(node2);
        set.add(node4);
        assertTrue(set.containsAll(graph.getNeighbours(node1.getKey()).stream().map(p -> p.getSecondNode()).collect(Collectors.toList())));

        set.clear();
        set.add(node1);
        set.add(node3);
        assertTrue(set.containsAll(graph.getNeighbours(node2.getKey()).stream().map(p -> p.getSecondNode()).collect(Collectors.toList())));

        set.clear();
        set.add(node2);
        set.add(node4);
        assertTrue(set.containsAll(graph.getNeighbours(node3.getKey()).stream().map(p -> p.getSecondNode()).collect(Collectors.toList())));

        set.clear();
        set.add(node1);
        set.add(node3);
        assertTrue(set.containsAll(graph.getNeighbours(node4.getKey()).stream().map(p -> p.getSecondNode()).collect(Collectors.toList())));

    }

    //generateTspTourFromEulerianTourStarter
    @Test
    public void testGenerateTspTourFromEulerianTourStarter() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = new UndirectedEdgeWeighedListGraph<>(EdgeALG::new);
        Node<String, LatLongId> node1 = createNode(1.0, 1.0, "1");
        Node<String, LatLongId> node2 = createNode(2.0, 2.0, "2");
        Node<String, LatLongId> node3 = createNode(3.0, 3.0, "3");
        Node<String, LatLongId> node4 = createNode(4.0, 4.0, "4");
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        List<Node<String, LatLongId>> order = new ArrayList<>();
        order.add(node1);
        order.add(node2);
        order.add(node3);
        order.add(node4);

        Map<Pair<Node<String, LatLongId>, Node<String, LatLongId>>, Double> edgeWeights = new HashMap<>();

        edgeWeights.put(new Pair<>(node1, node2), 1D);
        edgeWeights.put(new Pair<>(node2, node1), 1D);

        edgeWeights.put(new Pair<>(node1, node4), 2D);
        edgeWeights.put(new Pair<>(node4, node1), 2D);

        edgeWeights.put(new Pair<>(node3, node4), 5D);
        edgeWeights.put(new Pair<>(node4, node3), 5D);

        edgeWeights.put(new Pair<>(node3, node2), 2D);
        edgeWeights.put(new Pair<>(node2, node3), 2D);


        Christofides<String, LatLongId, Double> christofides = new Christofides<>(graph, EdgeALG::new);
        christofides.setEdgeWeights(edgeWeights);

        Method createEulerianTourFromMinWeightMultiMatchGraphStarterTwoMethod = getMethodByName("generateTspTourFromEulerianTourStarter");
        createEulerianTourFromMinWeightMultiMatchGraphStarterTwoMethod
                .invoke(christofides, order,graph);

        assertEquals(2, graph.getNeighbours(node1.getKey()).size());
        assertEquals(2, graph.getNeighbours(node2.getKey()).size());
        assertEquals(2, graph.getNeighbours(node3.getKey()).size());
        assertEquals(2, graph.getNeighbours(node4.getKey()).size());
        Set<Node<String, LatLongId>> set = new HashSet<>();
        set.add(node2);
        set.add(node4);
        assertTrue(set.containsAll(graph.getNeighbours(node1.getKey()).stream().map(p -> p.getSecondNode()).collect(Collectors.toList())));

        set.clear();
        set.add(node1);
        set.add(node3);
        assertTrue(set.containsAll(graph.getNeighbours(node2.getKey()).stream().map(p -> p.getSecondNode()).collect(Collectors.toList())));

        set.clear();
        set.add(node2);
        set.add(node4);
        assertTrue(set.containsAll(graph.getNeighbours(node3.getKey()).stream().map(p -> p.getSecondNode()).collect(Collectors.toList())));

        set.clear();
        set.add(node1);
        set.add(node3);
        assertTrue(set.containsAll(graph.getNeighbours(node4.getKey()).stream().map(p -> p.getSecondNode()).collect(Collectors.toList())));

    }


    private Method getMethodByName(String methodName) throws NoSuchMethodException {
        Method[] methods = Christofides.class.getDeclaredMethods();
        Method privateMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                privateMethod = method;
                privateMethod.setAccessible(true); // Set the method accessible
                break;
            }
        }
        return privateMethod;
    }
}
