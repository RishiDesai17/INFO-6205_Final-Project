package info6205.Graph;


/**
 * Represents a Node of Vertices in a Graph
 * @param <T> Value which will be present in the node
 * @param <G> Unique Identifier for this node
 */
public interface Node<T, G> {

    /**
     * Gets the Unique Identifier Encapsulated in a Key
     * @return Key encapsulated unique Identifier
     */
    Key<G> getKey();

    /**
     * Returs the value contained in the Node
     * @return Value contained in the node
     */
    T getValue();

    //List<Node<T, G>> getNeighbours();

    //Node<T, G> getNeighbour(KeyLatLongId<G> key);

    //boolean addNeighbour(Node<T, G> neighbour);
}
