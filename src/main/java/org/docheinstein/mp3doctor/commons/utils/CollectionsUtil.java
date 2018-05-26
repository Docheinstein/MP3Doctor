package org.docheinstein.mp3doctor.commons.utils;

import org.docheinstein.mp3doctor.commons.logger.Logger;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/** Contains utility methods for collections. */
public class CollectionsUtil {
    
    private static final Logger L = Logger.createForClass(CollectionsUtil.class);

    /**
     * Returns whether the given array is neither null nor empty.
     * @param objects the array to check
     * @return whether the array is not null and not empty
     */
    public static boolean isFilled(Object[] objects) {
        return objects != null && objects.length > 0;
    }

    /**
     * Returns whether the given collection is neither null nor empty.
     * @param collection the collection to check
     * @return whether the collection is not null and not empty
     */
    public static boolean isFilled(Collection collection) {
        return collection != null && collection.size() > 0;
    }
    
    /**
     * Adds an element to the collection only if it is not null.
     * @param collection the collection
     * @param element the element
     * @param <E> the type of the collection
     */
    public static <E> void addIfNotNull(Collection<E> collection,
                                        E element) {
        if (collection != null && element != null)
            collection.add(element);
    }

    /**
     * Adds a collection to the collection only if it is not null.
     * @param collection the 'parent' collection
     * @param collectionToAdd the 'child' collection
     * @param <E> the type of the collection
     */
    public static <E> void addIfNotNull(Collection<E> collection,
                                        Collection<? extends E> collectionToAdd) {
        if (collection != null && isFilled(collectionToAdd))
            collection.addAll(collectionToAdd);
    }

    /**
     * Scans an iterable collection and returns the first item that satisfy
     * the predicate or null if such element doesn't exist.
     * @param collection the collection
     * @param checker the predicate that checks if the scanned item satisfy
     *                the requirment
     * @param <E> the type of the collection
     * @return the firs item that satisfy the given checker.
     */
    public static <E> E find(Collection<E> collection, Predicate<E> checker) {
        if (!isFilled(collection))
            return null;

        for (E e : collection) {
            if (checker.test(e))
                return e;
        }

        return null;
    }

    /**
     * Removes the duplicates in the list using the given comparator
     * for evaluate the equality of the objects.
     * <p>
     * Actually this method performs a sort before check for duplicate;
     * this implies that the comparator must be a valid comparator that can
     * correctly evaluate for the object's order.
     * <p>
     * Furthermore, due the implementation of the {@link List#sort(Comparator)}
     * the cost of the sort step for consecutive call to this method is derisory.
     * <p>
     * The cost of the method is cost(sort) + o(n) = o(n * log(n)).
     *
     * @param list the list to check
     * @param comparator the comparator to use for evaluate the equality of the objects
     * @param <E> the type of the collection
     */
    public static <E> void sortAndRemoveDuplicates(List<E> list,
                                                   Comparator<E> comparator) {


        if (!isFilled(list))
            return;

        // Sort before find duplicates; this makes the cost be o(n*log(n)) + o(n)
        // instead of o(n^2)
        list.sort(comparator);

        // Idea: begin from the last item and remove the consecutive streaks of
        //       equals items
        for (int i = list.size() - 1; i >= 0; i--) {
            L.verbose("Searching duplicate at index i [" + i + "] for item [" + list.get(i) + "]");
            int j = i - 1;

            // Scan the list in reverse order till the [j] item is equals to the [i]
            while (j >= 0 && comparator.compare(list.get(i), list.get(j)) == 0) {
                L.debug("Found duplicated item: " + list.get(j));
                j--;
            }

            // Increment j so that it points the the first item to remove
            // and not the different one
            j++;

            // Remove if there is a duplicate
            if (j < i) {
                L.verbose("Removing duplicate (inclusive) \n  " + "" +
                    "\nFROM\t@ " + list.get(j) +
                    "\nTO\t@  " + list.get(i - 1));
                // Clears the list from j to i as of subList() javadoc
                list.subList(j, i).clear();
                // L.verbose("Decrementing i by: " + (i - j));
                i = j; // Continue from the last invalid item, so that
                       // the for condition (i--) will make this point to the
                       // first valid item
            }
            // else := Nothing to remove
        }
    }
}
