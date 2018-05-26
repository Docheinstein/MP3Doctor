package org.docheinstein.mp3doctor.commons.hierarchy;

import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.commons.utils.StringUtil;
import org.docheinstein.mp3doctor.commons.adt.FileWrapper;

import java.io.File;

/**
 * Represents an abstract system entry (i.e. a file or a directory).
 * This abstraction can be use for build cache of files, retrieve
 * the underlying file of those, and create the underlying file or directory.
 */
public abstract class Entry implements FileWrapper {

    private static final Logger L = Logger.createForClass(Entry.class);

    /**
     * Name of the entry.
     * <p>
     * This is not the absolute path of the file; it's just the file name
     * or the directory name of the entry.
     * <p>
     * The entry name can't be changed after the initialization of the entry.
     */
    protected String mEntryName;

    /**
     * Underlying file this entry wraps.
     * This must be initialized with {@link #initForParent(File)}.
     */
    protected File mFile;

    /**
     * Creates an entry for the given entry name.
     * <p>
     * This can be used if the hierarchy have to be built and avoids the
     * create a file for each entry.
     * @param entryName the file/directory name (not the path, just the name)
     *
     * @see #Entry(File)
     */
    public Entry(String entryName) {
        mEntryName = entryName;
    }

    /**
     * Creates an entry for the given file.
     * <p>
     * This can be used if the hierarchy is already built and there is need
     * to load an already known file.
     * @param underlyingFile the file
     *
     * @see #Entry(String)
     * @see #initForParent(File)
     */
    public Entry(File underlyingFile) {
        mFile = underlyingFile;
    }

    /**
     * Returns the entry name.
     * @return the entry name
     */
    public String getEntryName() {
        return mEntryName;
    }

    /**
     * Returns the name of the entry which may be the entry name if this
     * as been built using {@link #Entry(String)} or the file name if this
     * has been built using {@link #Entry(File)}.
     * @return the name of the entry, not null if this has been properly initialized
     */
    public String getName() {
        if (mEntryName != null)
            return mEntryName;
        if (mFile != null)
            return mFile.getName();
        return null;
    }

    /**
     * Returns the underlying file this entry wraps.
     * @return the underlying file this entry wraps
     */
    @Override
    public File getWrappedFile() {
        return mFile;
    }

    /**
     * Initialize the underlying file for the given parent file.
     * <p>
     * Note that this is different from {@link #Entry(File)} since this method
     * init the underlying file using both the parent file and the name entry
     * specified when built with {@link #Entry(String)}, while the constructor
     * that accepts the file directly initializes the underlying file.
     * @param parentFile the parent directory that contains this entry
     */
    void initForParent(File parentFile) {
        Asserts.assertNotNull(parentFile, "Entry's parent file must be not null", L);
        L.debug("Initializing entry for parent file: " + parentFile.getAbsolutePath());
        mFile = new File(
            StringUtil.ensureTrailing(
                parentFile.getAbsolutePath(),
                File.separator
            ) +  mEntryName);
        L.verbose("Entry initialized; underlying file is: " + mFile.getAbsolutePath());

    }

    /**
     * Creates the underlying file this entry wraps.
     * @return whether the creation has been successful.
     */
    public abstract boolean create();
}
