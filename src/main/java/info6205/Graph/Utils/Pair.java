package info6205.Graph.Utils;

import java.util.Objects;


/**
 * Represents a generic pair of values.
 * This class allows you to store and retrieve a pair of values, where the type
 * of the first value is denoted by 'X' and the type of the second value is
 * denoted by 'Y'.
 *
 * @param <X> The type of the first value.
 * @param <Y> The type of the second value.
 */
public class Pair<X, Y> {

    private X first;
    private Y second;

    /**
     * Creates a new Pair object with the given first and second values.
     *
     * @param first  The first value.
     * @param second The second value.
     */
    public Pair(X first, Y second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Checks if this Pair is equal to the given object.
     * Two Pair objects are considered equal if their first and second values are equal.
     *
     * @param o The object to compare.
     * @return True if this Pair is equal to the given object, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair<?, ?> pair)) return false;
        return first.equals(pair.first) && second.equals(pair.second);
    }

    /**
     * Retrieves the first value of this Pair.
     *
     * @return The first value of this Pair.
     */
    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    /**
     * Retrieves the first value of this Pair.
     *
     * @return The first value of this Pair.
     */
    public X getFirst() {
        return first;
    }

    /**
     * Retrieves the second value of this Pair.
     *
     * @return The second value of this Pair.
     */
    public Y getSecond() {
        return second;
    }
}
