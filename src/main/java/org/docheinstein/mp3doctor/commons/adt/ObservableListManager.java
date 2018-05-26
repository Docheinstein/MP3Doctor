package org.docheinstein.mp3doctor.commons.adt;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.docheinstein.mp3doctor.commons.logger.Logger;


import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Represents a manager that is responsible for notify listeners about
 * the changes to the underlying list.
 * <p>
 * This manager implements itself the {@link ObservableListListener} interface
 * so that it can handle the changes to the items (in addition to callback the
 * listeners). Anyhow, actually this implementation does nothing but notify the
 * listeners.
 * @param <E>
 */
public class ObservableListManager<E extends Observable>
    implements ObservableListListener<E> {

    private static final Logger L =
        Logger.createForClass(ObservableListManager.class);

    /** Underlying list. */
    protected final ObservableList<E> mList =
        // The list is an observable list of observable items, in this
        // way we can receive the callbacks from the items when they
        // call invalidated()
        FXCollections.observableArrayList(s -> new Observable[] { s } );

    /** Listeners that listen to the list's changes. */
    protected final Set<ObservableListListener<E>> mListeners = new CopyOnWriteArraySet<>();
    
    /**
     *  Listener of the list.
     *  <p>
     *  This is kept so that the list can be listened and unlistened at any time.
     */
    
    protected final ListChangeListener<E> mListChangeListener = c -> {
        while (c.next()) {
            // L.debug("Detected observable list change");
            if (c.wasAdded()) {
                L.verbose("Detected list change of ADDED elements, notifying listeners");
                mListeners.forEach(l -> l.onElementsAdded(c.getAddedSubList()));
            }
            else if (c.wasRemoved()) {
                L.verbose("Detected list change of REMOVED elements, notifying listeners");
                mListeners.forEach(l -> l.onElementsRemoved(c.getRemoved()));
            }
            else if (c.wasUpdated()) {
                L.verbose("Detected list change of UPDATED elements in list, notifying listeners");
                mListeners.forEach(l -> l.onElementsChanged(
                    c.getList().subList(c.getFrom(), c.getTo())));
            }
        }
    };

    /**
     * Creates a new list manager and initializes the underlying list.
     */
    public ObservableListManager() {
        this(true);
    }

    /**
     * Creates a new list manager and optionally initializes the underlying list
     * by adding a listener to it that detect the changes.
     * <p>
     * This constructor may be useful in case the elements of the list
     * have to be loaded before make the list observable (this avoid to fire
     * the callback to {@link #onElementsAdded(List)} which may be unneeded the
     * for the first load).
     * @param makeListObservable whether this entity listen to the changes
     *                           to the underlying list and thus reports those
     */
    public ObservableListManager(boolean makeListObservable) {
        addListener(this);

        if (makeListObservable)
            startListObservation();
    }

    /**
     * Adds a listener that will be notified when changes to the
     * list are made.
     * @param listener the listener that will listen to the list's changes
     */
    public void addListener(ObservableListListener<E> listener) {
        if (listener != null)
            mListeners.add(listener);
    }

    /**
     * Removes a previously added listener from the listener set.
     * @param listener the listener that won't listen to the list's changes anymore
     */
    public void removeListener(ObservableListListener<E> listener) {
        mListeners.remove(listener);
    }

    /**
     * Returns the underlying observable list.
     * @return the list
     */
    public ObservableList<E> getList() {
        return mList;
    }

    // This manager itself listens to the changes but do nothing.
    // Classes that extend this class may override those method
    // for perform some actions

    @Override
    public void onElementsAdded(List<? extends E> es) {}

    @Override
    public void onElementsRemoved(List<? extends E> es) {}

    @Override
    public void onElementsChanged(List<? extends E> es) {}

    /**
     * Makes the underlying list observable by adding a listener to it,
     * detecting changes and reporting those to the listeners of this class.
     */
    protected void startListObservation() {
        L.debug("Starting observing the underlying list");
        mList.addListener(mListChangeListener);
    }

    /**
     * Stops to listen to the underluing list changes.
     */
    protected void stopListObservation() {
        L.debug("Stopping observing the underlying list");
        mList.removeListener(mListChangeListener);
    }

}
