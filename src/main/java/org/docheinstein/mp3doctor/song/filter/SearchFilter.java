package org.docheinstein.mp3doctor.song.filter;

/**
 * Represents a generic filter that evaluate a predicate against an
 * instance of a given type.
 * @param <T> the type of the object to check.
 */
public interface SearchFilter<T> {

    /**
     * Returns whether the given instance satisfy the predicate in a sense
     * that depends on the use of this interface.
     * @param t the instance to check
     * @return whether the instance satisfy the filter condition
     */
    boolean check(T t);
}
