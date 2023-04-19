package info6205.Graph;

/**
 * This class is meant for containing a unique ID for the Node Class
 * @param <T> The Object Withich is unique to a node on the graph
 */
public interface Key<T> {

    /**
     * fetches the value of the Unique Identifier encapsulated by this Object
     * @return
     */
    T getValue();

}
