package org.docheinstein.mp3doctor.commons.adt;

import java.util.Collection;
import java.util.List;

/**
 * Represents an entity that listen to changes to a list.
 * @param <E> the type of the elements of the list
 */
public interface ListListener<E> {

    /**
     * Called when elements are added to the list.
     * <p>
     * If multiple items are added at the same time
     * (e.g. with {@link List#addAll(int, Collection)}
     * this method is called just once for all the elements.
     * @param es the added elements
     */
    void onElementsAdded(List<? extends E> es);

    /**
     * Called when elements are removed from the list.
     * <p>
     * If multiple items are removed at the same time
     * (e.g. with {@link List#removeAll(Collection)}
     * this method is called just once for all the elements.
     * @param es the removed elements
     */
    void onElementsRemoved(List<? extends E> es);
}
