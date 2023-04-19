package info6205.Graph.Problems.TravellingSalesMan.GraphImpl;

import java.util.Objects;


/**
 * Represents a geographical location identified by its latitude, longitude, and an additional identifier.
 * This is a key for a Node which will become its unique Identifier
 */
public class LatLongId {

    private final Double latitude;

    private final Double longitude;

    private final String id;


    /**
     * Creates a new LatLongId object with the given latitude, longitude, and identifier.
     *
     * @param latitude  The latitude of the location.
     * @param longitude The longitude of the location.
     * @param id        The identifier of the location.
     */
    public LatLongId(Double latitude, Double longitude, String id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }

    /**
     * Retrieves the latitude of this location.
     *
     * @return The latitude of this location.
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Retrieves the longitude of this location.
     *
     * @return The longitude of this location.
     */
    public Double getLongitude() {
        return longitude;
    }


    /**
     * Returns a string representation of this LatLongId object.
     * The string representation includes the latitude and longitude of the location.
     *
     * @return A string representation of this LatLongId object.
     */
    @Override
    public String toString() {
        return "LatLongId {" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }


    /**
     * Checks if this LatLongId object is equal to the given object.
     * Two LatLongId objects are considered equal if their latitude, longitude, and identifier are equal.
     *
     * @param o The object to compare.
     * @return True if this LatLongId object is equal to the given object, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LatLongId latLongId)) return false;
        return getLatitude().equals(latLongId.getLatitude()) && getLongitude().equals(latLongId.getLongitude()) && id.equals(latLongId.id);
    }

    /**
     * Computes the hash code of this LatLongId object.
     * The hash code is computed based on the hash codes of the latitude, longitude, and identifier.
     *
     * @return The hash code of this LatLongId object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getLatitude(), getLongitude(), id);
    }
}
