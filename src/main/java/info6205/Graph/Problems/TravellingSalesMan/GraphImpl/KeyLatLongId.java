package info6205.Graph.Problems.TravellingSalesMan.GraphImpl;

import java.util.Objects;


/**
 * Represents a key for a "LatLongId" object, which can be used as a unique identifier for a Node.
 * Implements the "Key" interface for generic key types in a graph.
 */
public class KeyLatLongId implements info6205.Graph.Key<LatLongId> {

    private final LatLongId latLongId;

    /**
     * Creates a new KeyLatLongId object with the given "LatLongId" object as the key.
     *
     * @param latLongId The "LatLongId" object to be used as the key.
     */
    public KeyLatLongId(LatLongId latLongId) {
        this.latLongId = latLongId;
    }

    /**
     * Retrieves the "LatLongId" object associated with this key.
     *
     * @return The "LatLongId" object associated with this key.
     */
    @Override
    public LatLongId getValue() {
        return latLongId;
    }

    /**
     * Checks if this KeyLatLongId object is equal to the given object.
     * Two KeyLatLongId objects are considered equal if their "LatLongId" objects are equal.
     *
     * @param o The object to compare.
     * @return True if this KeyLatLongId object is equal to the given object, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyLatLongId that)) return false;
        return latLongId.equals(that.latLongId);
    }

    /**
     * Computes the hash code of this KeyLatLongId object.
     * The hash code is computed based on the hash code of the "LatLongId" object.
     *
     * @return The hash code of this KeyLatLongId object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(latLongId);
    }
}
