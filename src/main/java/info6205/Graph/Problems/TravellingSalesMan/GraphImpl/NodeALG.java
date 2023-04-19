package info6205.Graph.Problems.TravellingSalesMan.GraphImpl;

import info6205.Graph.Node;
import java.util.*;
import info6205.Graph.Key;
/**
 * Represents a node in a graph.
 * This class implements the Node interface, where the key is of type Key<LatLongId> and the value is of type String.
 */

public class NodeALG implements Node<String, LatLongId> {

    private final Key<LatLongId> key;
    private final String value;

    /**
     * Creates a new NodeALG object with the given key and value.
     *
     * @param key   The key of the node, which is of type Key<LatLongId>.
     * @param value The value of the node, which is of type String.
     */
    public NodeALG(Key<LatLongId> key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Retrieves the key of this node.
     *
     * @return The key of this node.
     */
    @Override
    public Key<LatLongId> getKey() {
        return key;
    }

    /**
     * Retrieves the value of this node.
     *
     * @return The value of this node.
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * Returns a string representation of this NodeALG object.
     * The string representation includes the key and value of the node.
     *
     * @return A string representation of this NodeALG object.
     */
    @Override
    public String toString() {
        return "NodeALG {" +
                "key=" + key.getValue() +
                ", value='" + value + '\'' +
                '}';
    }


    /**
     * Checks if this NodeALG is equal to the given object.
     * Two NodeALG objects are considered equal if their keys and values are equal.
     *
     * @param o The object to compare.
     * @return True if this NodeALG is equal to the given object, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeALG)) return false;
        NodeALG nodeALG = (NodeALG) o;
        return getKey().equals(nodeALG.getKey()) && getValue().equals(nodeALG.getValue());
    }

    /**
     * Computes the hash code of this NodeALG object.
     * The hash code is computed based on the hash codes of the key and value.
     *
     * @return The hash code of this NodeALG object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue());
    }
}
