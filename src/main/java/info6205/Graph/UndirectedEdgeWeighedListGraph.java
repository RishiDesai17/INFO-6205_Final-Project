package info6205.Graph;
import info6205.Graph.Utils.IndexMinPQ;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This is an Undirected graph, which extends EdgeWeightedListGraph
 * @param <NodeValue> Type of value the Node or vertices hold for this graph
 * @param <NodeKeyValue> Type of unique ID for a node or vertices for this graph
 * @param <EdgeWeight> Type of Edge Weight the edge will represent for this graph, this needs to implement Comparable
 */
public class UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight extends Comparable<EdgeWeight>>
        extends EdgeWeightedListGraph<NodeValue, NodeKeyValue, EdgeWeight> {

    /**
     * This contains the implementation of the Edge Creator for edge creation
     */
    private final EdgeCreator<NodeValue, NodeKeyValue, EdgeWeight> edgeCreator;

    /**
     *
     * @param edgeCreator Edge creator used for Edge Creations
     */
    public UndirectedEdgeWeighedListGraph(EdgeCreator<NodeValue, NodeKeyValue, EdgeWeight> edgeCreator) {
        super();
        this.edgeCreator = edgeCreator;
    }

    @Override
    public Map<Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight>, Integer> getEdges() {
        return edges;
    }

    /**
     * Adds Undirected edges between given nodes with the given weight
     * @param node1 first node of the edge
     * @param node2 second node of the edge
     * @param edgeWeight weight between these nodes
     * @return return true if created or false in not
     */
    @Override
    public boolean addEdge(Node<NodeValue, NodeKeyValue> node1, Node<NodeValue, NodeKeyValue> node2, EdgeWeight edgeWeight) {
        try {
            Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edge = edgeCreator.createEdge(node1, node2, edgeWeight);
            Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edgeReverse = edgeCreator.createEdge(node2, node1, edgeWeight);
            ///todo mehul to add reverse node edge condition

            if (edges.containsKey(edge) || edges.containsKey(edgeReverse)) {
                Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> existingEdgeDirection =
                        edges.containsKey(edge) ? edge : edgeReverse;

                edges.put(existingEdgeDirection, edges.get(existingEdgeDirection) + 1);
            } else {
                edges.put(edge, 1);
            }
            addNeighbour(node1, node2, edgeWeight);

        return true;
        } catch (Exception e) {
            System.out.println("Error while adding edge e: " + e);
            return false;
        }
    }

    /**
     * add node1 and node2 as each others neighbour with the given weight similar to addEdge
     * this method should be removed and added to addEdge
     * @param nodeValue1 first node
     * @param nodeValue2 second node
     * @param edgeWeight edge weight between these provided nodes
     * @return return true if added or false in not
     */
    //how to stop this method being called if weighted Edge is used
    @Override
    protected boolean addNeighbour(Node<NodeValue, NodeKeyValue> nodeValue1, Node<NodeValue, NodeKeyValue> nodeValue2,
                                   EdgeWeight edgeWeight) {
        try {
            if (!nodes.containsKey(nodeValue1.getKey())) {
                addNode(nodeValue1);
            }

            if (!nodes.containsKey(nodeValue2.getKey())) {
                addNode(nodeValue2);
            }
            neighbourMap.get(nodeValue1.getKey()).add(edgeCreator.createEdge(nodeValue1, nodeValue2, edgeWeight));
            neighbourMap.get(nodeValue2.getKey()).add(edgeCreator.createEdge(nodeValue2, nodeValue1, edgeWeight));
            return true;

        } catch (Exception e) {
            System.out.println("Error while adding neighbour e: " + e);
            return false;
        }
    }


    public void test() {
        Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edge = this.neighbourMap.get(getNodes().get(0).getKey()).get(0);
        if (this.edges.containsKey(edge)) {
            System.out.println("Hash code working fine");
        }
    }


    /**
     * This creates MST by Eager Prims algorithm, it also uses IndexMinPQ to store object of SingleNodeEdgeWeight
     * for an index.
     * <a href="https://algs4.cs.princeton.edu/24pq">Source of IndexMinPQ</a>
     * @return returns ans UndirectedEdgeWeighedListGraph object similar to this class
     */
    public UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> getMSTByPrims() {
        try {

            UndirectedEdgeWeighedListGraph<NodeValue, NodeKeyValue, EdgeWeight> graphMst =
                    new UndirectedEdgeWeighedListGraph<>(edgeCreator);

            //Creating indices of Nodes and shortedEdgesNeighbour to reach those nodes
            // also weight to reach that node;
            Map<Key<NodeKeyValue>, Integer> nodeIndexMap = new HashMap<>();
            Map<Integer, Node<NodeValue, NodeKeyValue>> indexNodeMap = new HashMap<>();
            Set<Integer> visited = new HashSet<>();
            Set<Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight>> goodEdges = new HashSet<>();
            //Map<Integer, Integer> edgeNeighbourNode = new HashMap<>();
            int count = 0;
            Key<NodeKeyValue> startingKey = null;
            IndexMinPQ<SingleNodeEdgeWeight<EdgeWeight, NodeValue, NodeKeyValue>> indexedMinEdgePQ
                    = new IndexMinPQ<>(this.nodes.size());


            for (Key<NodeKeyValue> nodeKey : this.nodes.keySet()) {
                if (startingKey == null) {
                    startingKey = nodeKey;
                }
                //edgeNeighbourNode.put(, null);
                nodeIndexMap.put(nodeKey, count++);
                indexNodeMap.put(count - 1, this.nodes.get(nodeKey));
            }

            //initializing with first node
            visited.add(nodeIndexMap.get(startingKey));
            for (Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edge : this.neighbourMap.get(startingKey)) {

                Node<NodeValue, NodeKeyValue> neighbour = edge.getSecondNode();

                EdgeWeight edgeWeight = edge.getEdgeWeight();

                int neighbourIndex = nodeIndexMap.get(neighbour.getKey());

                indexedMinEdgePQ.insert(neighbourIndex, new SingleNodeEdgeWeight<>(edgeWeight,
                        this.nodes.get(startingKey)));
            }

            //creating edges now

            while (visited.size() != this.nodes.size()) {

                int indexOfCurrentMinEdge = indexedMinEdgePQ.minIndex();

                //checking if the shortest edge has nodes already present in mst
                if (visited.contains(indexOfCurrentMinEdge)) {
                    indexedMinEdgePQ.delMin();
                    continue;
                }

                SingleNodeEdgeWeight<EdgeWeight, NodeValue, NodeKeyValue>
                        currMinEdge = indexedMinEdgePQ.minKey();

                goodEdges.add(edgeCreator.createEdge(
                        currMinEdge.neighbour, indexNodeMap.get(indexOfCurrentMinEdge), currMinEdge.edgeWeight));

                indexedMinEdgePQ.delMin();

                visited.add(indexOfCurrentMinEdge);

                startingKey = indexNodeMap.get(indexOfCurrentMinEdge).getKey();

                for (Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edge : this.neighbourMap.get(startingKey)) {

                    Node<NodeValue, NodeKeyValue> neighbour = edge.getSecondNode();

                    int neighbourIndex = nodeIndexMap.get(neighbour.getKey());

                    EdgeWeight edgeWeight = edge.getEdgeWeight();

                    if (visited.contains(neighbourIndex)) {
                        continue;
                    }

                    if (indexedMinEdgePQ.contains(neighbourIndex)) {
                        if (indexedMinEdgePQ.keyOf(neighbourIndex)
                                .edgeWeight.compareTo(edgeWeight) > 0) {
                            indexedMinEdgePQ.changeKey(neighbourIndex, new SingleNodeEdgeWeight<>(edgeWeight,
                                    this.nodes.get(startingKey)));
                        }
                    } else {
                        indexedMinEdgePQ.insert(neighbourIndex, new SingleNodeEdgeWeight<>(edgeWeight,
                                this.nodes.get(startingKey)));
                    }
                }
            }

            //adding edges in MSt by goodEdges

            for (Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edge : goodEdges) {
                graphMst.addEdge(edge.getFirstNode(), edge.getSecondNode(), edge.getEdgeWeight());
            }

            //PointPlotter pointPlotter = new PointPlotter();

            //pointPlotter.plotGraph((UndirectedEdgeWeighedListGraph<String, LatLongId, Double>) graphMst);


            return graphMst;
        } catch (Exception e) {
            System.out.println("error while creating MST from Prim's e" + e);
            throw e;
        }
    }

    /**
     * Not used should not be used
     * @param edge
     */
    @Deprecated
    public void deleteEdge(Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edge) {
        try {

            Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edgeToBeRemoved =
                    edges.containsKey(edge) ? edge : edge.createReverseEdge();

            if (edges.get(edgeToBeRemoved) == 1) {
                this.edges.remove(edgeToBeRemoved);
            } else {
                this.edges.put(edge, edges.get(edgeToBeRemoved) - 1);
            }

            neighbourMap.get(edgeToBeRemoved.getFirstNode().getKey()).remove(edgeToBeRemoved);
            Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> edgeReverse = edgeToBeRemoved.createReverseEdge();
            neighbourMap.get(edgeReverse.getFirstNode().getKey()).remove(edgeReverse);

        } catch (Exception e) {
            System.out.println("error while deleting edge" + Arrays.stream(e.getStackTrace()).map(p -> p.toString()).collect(Collectors.joining(", ")));
            throw e;
        }

    }

    /**
     * This is a class Edge Weight and a Node used in getMSTByPrims
     * @param <EdgeWeight> type of Edge Weight the edge will represent for this graph, this needs to implement Comparable
     * @param <NodeValue> value contained in the node present in this object
     * @param <NodeKeyValue> Type of unique ID for a node or vertices present in this object
     */
    static class SingleNodeEdgeWeight<EdgeWeight extends Comparable<EdgeWeight>, NodeValue, NodeKeyValue>
            implements Comparable<SingleNodeEdgeWeight<EdgeWeight, NodeValue, NodeKeyValue>> {

        EdgeWeight edgeWeight;

        Node<NodeValue, NodeKeyValue> neighbour;

        public SingleNodeEdgeWeight(EdgeWeight edgeWeight, Node<NodeValue, NodeKeyValue> neighbour) {
            this.edgeWeight = edgeWeight;
            this.neighbour = neighbour;
        }

        @Override
        public int compareTo(SingleNodeEdgeWeight o) {
            return this.edgeWeight.compareTo((EdgeWeight) o.edgeWeight);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UndirectedEdgeWeighedListGraph<?, ?, ?> that)) return false;
        if (!super.equals(o)) return false;
        return ((UndirectedEdgeWeighedListGraph<?, ?, ?>) o).edgeCreator != null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), edgeCreator);
    }
}
