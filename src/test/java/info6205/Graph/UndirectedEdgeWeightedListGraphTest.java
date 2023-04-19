package info6205.Graph;

import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.EdgeALG;
import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.KeyLatLongId;
import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.LatLongId;
import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.NodeALG;
import org.junit.Test;

import java.util.Optional;

import static info6205.Graph.Utility.Utility.calculateWeight;
import static info6205.Graph.Utility.Utility.createNode;
import static org.junit.Assert.assertEquals;

public class UndirectedEdgeWeightedListGraphTest {


    @Test
    public void testAddNode() {
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = new UndirectedEdgeWeighedListGraph<>(EdgeALG::new);
        graph.addNode(createNode(1.0, 1.0, "1"));
        graph.addNode(createNode(2.0, 2.0, "2"));
        assertEquals(2, graph.nodes.size());
    }


    @Test
    public void testAddNeighbour() {
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = new UndirectedEdgeWeighedListGraph<>(EdgeALG::new);
        Node<String, LatLongId> node1 = createNode(1.0, 1.0, "1");
        Node<String, LatLongId> node2 = createNode(2.0, 2.0, "2");
        Node<String, LatLongId> node3 = createNode(3.0, 3.0, "3");
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNeighbour(node1, node2, 1D);
        graph.addNeighbour(node1, node3, 2D);
        graph.addNeighbour(node2, node3, 2D);
        assertEquals(2, graph.getNeighbours(node1.getKey()).size());
        assertEquals(2, graph.getNeighbours(node2.getKey()).size());
        assertEquals(2, graph.getNeighbours(node3.getKey()).size());
    }

    @Test
    public void testAddEdge() {
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = new UndirectedEdgeWeighedListGraph<>(EdgeALG::new);
        Node<String, LatLongId> node1 = createNode(1.0, 1.0, "1");
        Node<String, LatLongId> node2 = createNode(2.0, 2.0, "2");
        Node<String, LatLongId> node3 = createNode(3.0, 3.0, "3");
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addEdge(node1, node2, 1D);
        graph.addEdge(node1, node3, 2D);
        graph.addEdge(node2, node3, 3D);
        assertEquals(3, graph.getEdges().size());
        assertEquals(Optional.of(1).get(), graph.getEdges().get(new EdgeALG(node1, node2, 1D)));
        assertEquals(null, graph.getEdges().get(new EdgeALG(node1, node2, 2D)));
        assertEquals(Optional.of(1).get(), graph.getEdges().get(new EdgeALG(node1, node3, 2D)));
        assertEquals(Optional.of(1).get(), graph.getEdges().get(new EdgeALG(node2, node3, 3D)));
    }

    @Test
    public void testGetPrimsMst() {
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = new UndirectedEdgeWeighedListGraph<>(EdgeALG::new);
        Node<String, LatLongId> node1 = createNode(1.0, 1.0, "1");
        Node<String, LatLongId> node2 = createNode(2.0, 2.0, "2");
        Node<String, LatLongId> node3 = createNode(3.0, 3.0, "3");
        Node<String, LatLongId> node4 = createNode(4.0, 4.0, "4");
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        graph.addEdge(node1, node2, 1D);
        graph.addEdge(node1, node3, 2D);
        graph.addEdge(node2, node3, 3D);
        graph.addEdge(node4, node2, 2D);
        graph.addEdge(node4, node3, 4D);
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> mst = graph.getMSTByPrims();
        assertEquals(Optional.of(5.0).get(), calculateWeight(mst));
        assertEquals(3, mst.getEdges().size());
        assertEquals(2, mst.getNeighbours(node1.getKey()).size());
        assertEquals(2, mst.getNeighbours(node2.getKey()).size());
        assertEquals(1, mst.getNeighbours(node3.getKey()).size());
        assertEquals(1, mst.getNeighbours(node4.getKey()).size());
    }
}
