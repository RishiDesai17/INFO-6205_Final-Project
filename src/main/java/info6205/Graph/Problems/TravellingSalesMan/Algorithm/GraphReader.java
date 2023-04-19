package info6205.Graph.Problems.TravellingSalesMan.Algorithm;

import info6205.Graph.EdgeCreator;
import info6205.Graph.Node;
import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.KeyLatLongId;
import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.LatLongId;
import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.NodeALG;
import info6205.Graph.UndirectedEdgeWeighedListGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;


/**
 * GraphReader is a utility class that reads data from a file and creates a graph
 * representation of the data for the Traveling Salesman Problem algorithm.
 * It provides a method to read graph data from a CSV file, create nodes and edges
 * from the data, and return a graph object.
 * The graph data is expected to be in a specific format, where each line in the CSV
 * file represents a node with its associated latitude, longitude, and ID. The CSV file
 * This class uses the EdgeCreator interface to create edges for the graph
 * This class provides error handling for file I/O and data parsing, and avoids creating
 * duplicate nodes in the graph based on latitude and longitude values.
 */
public class GraphReader {


    /**
     * Reads graph data from a CSV file, creates nodes from the data, and
     * returns a graph object.
     *
     * @param fileName     the name of the CSV file to read
     * @param edgeCreator  the EdgeCreator object to create edges for the graph
     * @return an UndirectedEdgeWeighedListGraph object representing the graph or null if there was an error during graph creation
     */
    public static UndirectedEdgeWeighedListGraph<String, LatLongId, Double> getGraphFromFile(String fileName,
                                                                                             EdgeCreator<String, LatLongId, Double> edgeCreator) {
        try {
            String path = new File(".").getCanonicalPath();

            File file = new File(path + "/csvData/" + fileName);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph = new UndirectedEdgeWeighedListGraph<>(edgeCreator);
            System.out.println(bufferedReader.readLine());
            HashSet<String> set = new HashSet<>();
            while (true) {
                Node<String, LatLongId> node = createNodeFromData(bufferedReader);
                if (node == null) {
                    break;
                } else {

                    String key = String.valueOf(node.getKey().getValue().getLatitude()) +
                            String.valueOf(node.getKey().getValue().getLongitude());
                    if (!set.contains(key)) {
                        //System.out.println("Duplicate not found");
                        graph.addNode(node);
                    }
                    set.add(key);
                }
            }
            //graph.createEdgesFromPresentNodes();
            HashSet<String> set1 = new HashSet<>();
           return graph;


        } catch (Exception e) {
            System.out.println("Error while creating graph : " + e);
            return null;
        }
    }

    /**
     * Creates a Node object from data read from a BufferedReader.
     *
     * @param br  the BufferedReader object to read data from
     * @return    a Node object created from the data, or null if the data is null
     * @throws IOException  if there was an error reading data from the BufferedReader
     */

    private static Node<String, LatLongId> createNodeFromData(BufferedReader br) throws IOException{
        String nodeData = br.readLine();
        if (nodeData != null) {
            String[] idLongLat = nodeData.split(",");
            return new NodeALG(new KeyLatLongId(
                    new LatLongId(Double.valueOf(idLongLat[2]), Double.valueOf(idLongLat[1]), idLongLat[0])),
                    idLongLat[0]);

        }
        return null;
    }
}
