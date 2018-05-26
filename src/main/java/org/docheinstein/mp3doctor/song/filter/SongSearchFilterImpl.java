package org.docheinstein.mp3doctor.song.filter;

/**
 * Represents a basic implementation of {@link SongSearchFilter} that
 * takes an object of a generic type which can be used for the evaluation
 * of the filter through {@link #check(Object)}.
 * @param <V> the type of object to use for the evaluation of the filter
 */
public abstract class SongSearchFilterImpl<V> implements SongSearchFilter {

    /** A value that can be used in {@link #check(Object)} to evaluate the filter. */
    protected V mFilterValue;

    /**
     * Creates a {@link SongSearchFilter} with the given value to used
     * for check the predicate in a way that is decided by the implementation
     * of this abstract entity.
     * @param filterValue
     */
    protected SongSearchFilterImpl(V filterValue) {
        mFilterValue = filterValue;
    }
}
