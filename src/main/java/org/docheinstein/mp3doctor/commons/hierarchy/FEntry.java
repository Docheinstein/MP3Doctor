package org.docheinstein.mp3doctor.commons.hierarchy;

import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.utils.Asserts;

import java.io.File;
import java.io.IOException;

/**
 * Represents a file entry.
 * @see Entry
 * @see DEntry
 */
public class FEntry extends Entry {

    private static final Logger L = Logger.createForClass(FEntry.class);

    /**
     * Creates an file entry for the given entry name.
     * <p>
     * This can be used if the hierarchy have to be built and avoids the
     * create a file for each entry.
     * @param entryName the file/directory name (not the path, just the name)
     *
     * @see #FEntry(File)
     */
    public FEntry(String entryName) {
        super(entryName);
    }

    /**
     * Creates a file entry for the given file.
     * <p>
     * This can be used if the hierarchy is already built and there is need
     * to load an already known file.
     * @param underlyingFile the file
     *
     * @see #FEntry(String)
     * @see #initForParent(File)
     */
    public FEntry(File underlyingFile) {
        super(underlyingFile);
    }

    @Override
    public boolean create() {
        try {
            Asserts.assertNotNull(mFile,
                "Entry's underlying file should not be null when invoking create()", L);

            L.debug("Creating underlying file for F-Entry: " + mFile.getPath());

            return mFile.exists() || mFile.createNewFile();
        } catch (IOException e) {
            L.error("File creation has failed for F-Entry: " + mEntryName);
            return false;
        }
    }
}
