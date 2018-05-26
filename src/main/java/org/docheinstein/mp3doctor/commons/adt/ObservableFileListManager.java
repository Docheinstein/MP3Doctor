package org.docheinstein.mp3doctor.commons.adt;

import javafx.beans.Observable;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.utils.Asserts;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a manager that is responsible for notify listeners about
 * the changes to the underlying list.
 * <p>
 * Furthermore, this class is responsible for load the list's elements
 * from the disk when is initialized (the directory and the way the new
 * items are initialized must be specified by the classes that implement
 * this abstraction) and for delete the file wrapped by an element when
 * it is removed from the list.
 * @param <E>
 */
public abstract class ObservableFileListManager
    <E extends FileWrapper & Observable> extends ObservableListManager<E> {

    private static final Logger L =
        Logger.createForClass(ObservableFileListManager.class);

    /**
     * Creates a new list manager and load the list from the disk to memory.
     */
    public ObservableFileListManager() {
        // Observe the underlying list after the list has been loaded
        super(false);
        load();
        startListObservation();
    }

    /**
     * Returns the directory from which this list have to load and
     * remove the files.
     * @return the directory used for load and remove items
     */
    public abstract File getBaseDirectory();

    /**
     * Returns a new object for a file read by {@link #load()}.
     * <p>
     * The initialization of the object (probably) should be depended from
     * the content or the name of the file.
     * @param f the file
     * @return an new object of type {@link E} for the given file
     */
    public abstract E newFileWrapper(File f);

    @Override
    public void onElementsRemoved(List<? extends E> fs) {
        fs.forEach(this::remove);
    }

    /** Loads the elements from the disk to the underlying list. */
    protected void load() {
        L.info("Loading list from cache");

        File wrappedFilesDirectory = getBaseDirectory();

        Asserts.assertTrue(
            wrappedFilesDirectory != null && wrappedFilesDirectory.isDirectory(),
            "Can't load list elements: invalid directory");

        File[] wrappedFiles = wrappedFilesDirectory.listFiles();

        if (wrappedFiles == null) {
            L.warn("Found null file array, list can't be loaded");
            return;
        }

        // Sort alphabetically before add
        Arrays.sort(wrappedFiles);

        List<E> elements = new ArrayList<>();

        for (File f : wrappedFiles) {
            elements.add(newFileWrapper(f));
        }

        // The songs are added at once with addAll() in order to avoid
        // to callback the listeners for each song
        mList.addAll(elements);
    }

    /**
     * Removes the file wrapped by the given {@link FileWrapper} object.
     * @param f the {@link FileWrapper} whose wrapped file should be deleted
     */
    protected void remove(E f) {
        if (f == null)
            return;

        L.debug("Removing file from cache for element " + f.toString());

        File wrappedFile = f.getWrappedFile();

        if (wrappedFile == null) {
            L.warn("Can't operate on a null file");
            return;
        }

        if (!wrappedFile.exists()) {
            L.debug("File doesn't exists yet");
            return;
        }

        if (wrappedFile.delete()) {
            L.debug("File has been removed successfully");
        } else {
            L.warn("Error occurred while deleting wrapped file");
        }
    }
}
