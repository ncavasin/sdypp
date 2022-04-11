package shared;

/**
 * Defines a generic task of generic type T to interface between the TaskProcessor and the work that needs
 * to be done. It does not extend Remote so it will be exposed via the
 * @param <T> generic type
 */
public interface Task<T> {
    T execute();
}
