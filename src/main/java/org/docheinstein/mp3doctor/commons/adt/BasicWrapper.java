package org.docheinstein.mp3doctor.commons.adt;

/**
 * Basic implementation of {@link Wrapper} that wraps an object and
 * provides getter and setter for it.
 */
public class BasicWrapper<E> implements Wrapper<E> {
    /** Wrapped value. */
    private E mValue;

    /**
     * Creates a new wrapper.
     */
    public BasicWrapper() {}

    /**
     * Creates a new wrapper and set the underlying object.
     * @param e the wrapped object
     */
    public BasicWrapper(E e) {
        set(e);
    }

    @Override
    public E get() {
        return mValue;
    }

    @Override
    public void set(E e) {
        mValue = e;
    }
}
