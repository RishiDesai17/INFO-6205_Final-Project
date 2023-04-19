package info6205.Graph.Utility;

import info6205.Graph.Edge;
import info6205.Graph.Node;
import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.KeyLatLongId;
import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.LatLongId;
import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.NodeALG;
import info6205.Graph.UndirectedEdgeWeighedListGraph;

public class Utility {

    public static Double calculateWeight(UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph) {
        Double pathWeight = 0D;
        for (Node<String, LatLongId> node : graph.getNodes()) {
            for (Edge<Node<String, LatLongId>, Double> edge : graph.getNeighbours(node.getKey())) {
                pathWeight += edge.getEdgeWeight();
            }
        }
        return pathWeight/2;
    }

    public static Node<String, LatLongId> createNode(Double latitude, Double longitude, String value) {
        return new NodeALG(new KeyLatLongId(new LatLongId(latitude, longitude, value)), value);
    }
}
