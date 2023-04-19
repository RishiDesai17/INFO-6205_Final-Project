package info6205.Graph;

/**
 * This Interface is meant to be implemented by Edge Creators. To generify edge Creation
 * @param <NodeValue> Type of value the Node or vertices hold
 * @param <NodeKeyValue> Type of ID for a node or vertices
 * @param <EdgeWeight> Type of Edge Weight the edge will represent
 */
public interface EdgeCreator<NodeValue, NodeKeyValue, EdgeWeight> {

    /**
     *
     * @param node1 first Node, in case of Directed Graph this represents from Node
     * @param node2 second Node, in case of Directed Graph this represents to Node
     * @param edgeWeight weight of the edge between these two nodes
     * @return
     */
    Edge<Node<NodeValue, NodeKeyValue>, EdgeWeight> createEdge(Node<NodeValue, NodeKeyValue> node1,
                                                                      Node<NodeValue, NodeKeyValue> node2,
                                                                      EdgeWeight edgeWeight);
}
