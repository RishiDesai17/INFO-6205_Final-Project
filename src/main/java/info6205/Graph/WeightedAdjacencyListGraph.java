package info6205.Graph;

import java.util.List;

/**
 * Structure for implementing  Weighted Adjacency List Graph, implementation could be directed or undirected graph
 * @param <NodeValue> Type of value the Node or vertices hold for this graph
 * @param <NodeKeyValue> Type of unique ID for a node or vertices for this graph
 * @param <EdgeWeight> Type of Edge Weight the edge will represent for this graph, this needs to implement Comparable
 */
//should we break the method of adding edge and neighbour from weighted and unweighted interface??
public interface WeightedAdjacencyListGraph<NodeValue, NodeKeyValue, EdgeWeight extends Comparable<EdgeWeight>> {

    /**
     * return size of the graph in terms of nodes
     * @return returns an int
     */
    int getSize();

    /**
     * Adds node to the graph
     * @param node node to be added
     * @return return true if successfully added otherwise false
     */
    boolean addNode(Node<NodeValue, NodeKeyValue> node);

    /**
     * Check if this node is present in the graph by Nodes key or Unique ID
     * @param key Node's Unique Identifier
     * @return return Node type if present, otherwise null
     */
    Node<NodeValue, NodeKeyValue> hasNode(Key<NodeKeyValue> key);

    /**
     * Fetches all nodes of the graph in the form of list
     * @return list of all the nodes present in the graph in the form of Node object in a List
     */
    List<Node<NodeValue, NodeKeyValue>> getNodes();

    /**
     * Add edges between given nodes with the given weight
     * @param node1 first node of the edge, in case of directed from node
     * @param node2 second node of the edge, in case of directed to node
     * @param weight weight between these nodes
     * @return return true if created or false in not
     */
    boolean addEdge(Node<NodeValue, NodeKeyValue> node1, Node<NodeValue, NodeKeyValue> node2, EdgeWeight weight);
}
