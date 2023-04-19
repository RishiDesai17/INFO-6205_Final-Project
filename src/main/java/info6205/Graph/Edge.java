package info6205.Graph;

/**
 * This class represent edges of Graphs
 * @param <Node> type of Vertices or nodes which this edge is connecting
 * @param <Weight> Type of weight representation of this edge
 */
public interface Edge<Node, Weight> {

    /**
     * In Directed graph this represents from Node
     * @return returs first node
     */
    Node getFirstNode();

    /**
     * In Directed graph this represents to Node
     * @return returns second node
     */
    Node getSecondNode();

    /**
     *
     * @return return Edge Weight represented by this edge
     */
    Weight getEdgeWeight();

    /**
     * Creates Reverse edge meant for directed graphs
     * @return returns reverse Edge
     */
    Edge<Node, Weight> createReverseEdge();

}
