package org.docheinstein.mp3doctor.commons.adt;

import javafx.beans.Observable;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Represents an entity that listen to changes either to a list structure
 * (added and removed elements) and to the elements via the {@link Observable}
 * interface which must be implemented by the elements.
 * @param <E> the type of the elements of the list
 */
public interface ObservableListListener<E extends Observable> extends ListListener<E> {

    /**
     * Called when elements are updated in the list.
     * <p>
     * The changes to the list's elements are detected via the
     * {@link Observable} interface which is implemented by the elements.
     * <p>
     * If multiple items are updated at the same time
     * (e.g. with {@link List#replaceAll(UnaryOperator)}
     * this method is called just once for all the elements.
     * @param es the changed elements
     */
    void onElementsChanged(List<? extends E> es);
}

