package info6205.Graph;

import java.util.*;


/**
 * This abstract class represents Weighted Edge Graph, whether this is a directed or undirected graphs depends on its
 * implementation
 * @param <NodeValue> Type of value the Node or vertices hold for this graph
 * @param <NodeKeyValue> Type of unique ID for a node or vertices for this graph
 * @param <EdgeWeight> Type of Edge Weight the edge will represent for this graph, this needs to implement Comparable
 */
public abstract class EdgeWeightedListGraph<NodeValue, NodeKeyValue, EdgeWeight extends Comparable<EdgeWeight>>
        implements WeightedAdjacencyListGraph<NodeValue, NodeKeyValue, EdgeWeight> {


    /**
     * Nodes present in the graph
     */
    protected final Map<Key<NodeKeyValue>, Node<NodeValue, NodeKeyValue>> nodes;
    /**
     * this have list of edges of a particular node
     */
    protected final Map<Key<NodeKeyValue>, List<Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight>>> neighbourMap;

    //now every thing depends on the implementation of adding weighted edge of directed and undirected
    /**
     * All the edges of the graph if two nodes have same weight multiple edge this have the number of edges as well
     */
    protected final Map<Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight>, Integer> edges;


    public EdgeWeightedListGraph() {
        this.nodes = new HashMap<>();
        this.neighbourMap = new HashMap<>();
        this.edges = new HashMap<>();
    }

    /**
     * return size of the graph in terms of nodes
     * @return returns an int
     */
    public int getSize() {
        return neighbourMap.size();
    }

    /**
     * Check if this node is present in the graph by Nodes key or Unique ID
     * @param key Node's Unique Identifier
     * @return return Node type if present, otherwise null
     */
    @Override
    public Node<NodeValue, NodeKeyValue> hasNode(Key<NodeKeyValue> key) {
        return nodes.getOrDefault(key, null);
    }

    /**
     * Fetches all nodes of the graph in the form of list
     * @return list of all the nodes present in the graph in the form of Node object in a List
     */
    @Override
    public List<Node<NodeValue, NodeKeyValue>> getNodes() {
        return new LinkedList<>(this.nodes.values());
    }


    /**
     * Adds node to the graph
     * @param node node to be added
     * @return return true if successfully added otherwise false
     */
    @Override
    public boolean addNode(Node<NodeValue, NodeKeyValue> node) {
        try {
            nodes.put(node.getKey(), node);
            neighbourMap.put(node.getKey(), new LinkedList<>());
            return true;
        } catch (Exception e) {
            System.out.println("Exception while saving a Node : " + e);
            return false;
        }
    }

    /**
     * Returns List of edges related to a node
     * @param key Unique ID or key for a node
     * @return list of Edges from this node to Neighbour Node
     */
    public List<Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight>> getNeighbours(Key<NodeKeyValue> key) {
        return this.neighbourMap.getOrDefault(key, null);
    }

    /**
     * Add edges between given nodes with the given weight
     * @param node1 first node of the edge, in case of directed from node
     * @param node2 second node of the edge, in case of directed to node
     * @param edgeWeight weight between these nodes
     * @return return true if created or false in not
     */
    public abstract boolean addEdge(Node<NodeValue, NodeKeyValue> node1, Node<NodeValue, NodeKeyValue> node2, EdgeWeight edgeWeight);

    /**
     * add node1 and node2 as each others neighbour with the given weight similar to addEdge
     * @param nodeValue1 first node
     * @param nodeValue2 second node
     * @param edgeWeight edge weight between these provided nodes
     * @return return true if added or false in not
     */
    protected abstract boolean addNeighbour(Node<NodeValue, NodeKeyValue> nodeValue1, Node<NodeValue, NodeKeyValue> nodeValue2,
                                            EdgeWeight edgeWeight);

    /**
     * fetches all the edges present in this graph
     * @return map of edges and their count
     */
    public abstract Map<Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight>, Integer> getEdges();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EdgeWeightedListGraph<?, ?, ?> that)) return false;
        return getNodes().equals(that.getNodes()) && neighbourMap.equals(that.neighbourMap) && getEdges().equals(that.getEdges());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodes(), neighbourMap, getEdges());
    }
}
