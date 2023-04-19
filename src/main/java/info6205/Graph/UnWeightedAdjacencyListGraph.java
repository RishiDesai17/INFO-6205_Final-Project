package info6205.Graph;

import java.util.List;

/**
 * Structure for Implementing an Un weighted Adjacency List Graph
 * @param <NodeValue> Type of value the Node or vertices hold for this graph
 * @param <NodeKeyValue> Type of unique ID for a node or vertices for this graph
 */
public interface UnWeightedAdjacencyListGraph<NodeValue, NodeKeyValue> {

    /**
     * return size of the graph in terms of nodes
     * @return return an int
     */
    int getSize();

    /**
     * Adds node to the graph
     * @param node type of Node to be added
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
     * add node1 and node2 as each others neighbour
     * @param nodeValue1 first node
     * @param nodeValue2 second node
     * @return return true if added or false in not
     */
    boolean addNeighbour(Node<NodeValue, NodeKeyValue> nodeValue1, Node<NodeValue, NodeKeyValue> nodeValue2);

}
