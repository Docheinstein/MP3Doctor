package org.docheinstein.mp3doctor.commons.adt;

import javafx.beans.Observable;


/**
 * Represents an {@link ObservableWrapper} of a an object whose changes can be
 * listened via the {@link Observable} interface, which this interface extends.
 * <p>
 * Furthermore, this interface represents a wrapper of a file and thus is able
 * to retrieve the wrapped file via {@link FileWrapper#getWrappedFile()}.
 * <p>
 * This interface is handy when there is an object in memory which should remain
 * consistent with its representation on the disk.
 * @param <E> the type of the wrapped object
 */
public interface ObservableFileWrapper<E> extends ObservableWrapper<E>, FileWrapper {}
