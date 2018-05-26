package org.docheinstein.mp3doctor.commons.adt;

/**
 * Represents a wrapper of an object.
 * @param <E> the type of the wrapped object
 */
public interface Wrapper<E> {
    /**
     * Returns the wrapped object.
     * @return the object
     */
    E get();

    /**
     * Sets the wrapped object.
     * @param e the object
     */
    void set(E e);
}
