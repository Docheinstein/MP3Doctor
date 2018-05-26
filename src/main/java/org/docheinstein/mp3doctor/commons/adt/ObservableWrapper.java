package org.docheinstein.mp3doctor.commons.adt;

import javafx.beans.Observable;

/**
 * Represents an {@link Wrapper} of a an object whose changes can be
 * listened via the {@link Observable} interface, which this interface extends.
 * this interface extends.
 * @param <E> the type of the wrapped object
 */
public interface ObservableWrapper<E> extends Wrapper<E>, Observable {}
