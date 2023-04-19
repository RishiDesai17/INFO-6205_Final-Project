package info6205;

import info6205.Graph.Node;
import info6205.Graph.Problems.TravellingSalesMan.Algorithm.SimulatedAnnealing;
import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.EdgeALG;
import info6205.Graph.UnWeightedAdjacencyListGraph;
import info6205.Graph.Problems.TravellingSalesMan.Algorithm.Christofides;
import info6205.Graph.Problems.TravellingSalesMan.Algorithm.GraphReader;
import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.LatLongId;
import info6205.Graph.UndirectedEdgeWeighedListGraph;

public class Runner {

    public static void main(String[] args) {
        UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = GraphReader
                .getGraphFromFile("final.csv", EdgeALG::new);
        Christofides<String, LatLongId, Double> christofidesRunner = new Christofides<>(graph, (Node<String, LatLongId> node1
                , Node<String, LatLongId> node2, Double c)
                -> new EdgeALG(node1, node2, c));
        christofidesRunner.runChristofides();
    }
}
