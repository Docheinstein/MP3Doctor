package org.docheinstein.mp3doctor.commons.hierarchy;

import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.commons.utils.CollectionsUtil;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

/**
 * Represents a directory entry.
 * @see Entry
 * @see DEntry
 */
public class DEntry extends Entry {

    private static final Logger L = Logger.createForClass(DEntry.class);

    /** Nested entries. */
    private HashMap<String, Entry> mChildren = new HashMap<>();

    /**
     * Creates a dentry for the given entry name using the given system
     * path as base directory and initializes it with the given entries.
     * <p>
     * The directory is normalized: every entry is recursively initialized
     * consistently with the given system path.
     * @param systemPath the relative or absolute system path that proceed the entry name
     * @param entryName the entry name
     * @param entries the entries this dentry is initialized inside it
     * @return a dentry already normalized for the given paths.
     *
     * @see #DEntry(String, Entry...)
     * @see #normalizeHierarchy()
     */
    public static DEntry createNormalizedHierarchyForPath(
        String systemPath, String entryName, Entry... entries) {
        DEntry dEntry = new DEntry(entryName, entries);
        dEntry.initForParent(new File(systemPath));
        dEntry.normalizeHierarchy();
        dEntry.loadChildren();
        return dEntry;
    }

    /**
     * Creates a file entry for the given file.
     * <p>
     * This can be used if the hierarchy is already built and there is need
     * to load an already known file.
     * @param underlyingFile the file
     *
     * @see #DEntry(String)
     * @see #initForParent(File)
     */
    public DEntry(File underlyingFile) {
        super(underlyingFile);
    }

    /**
     * Creates a directory entry for the given entry name without children.
     * @param entryName the directory name (not the path, just the name)
     *
     * @see #DEntry(String, Entry...)
     * @see #createNormalizedHierarchyForPath(String, String, Entry...)
     */
    public DEntry(String entryName) {
        this(entryName, new Entry[0]);
    }

    /**
     * Creates a directory entry for the given entry name initialized
     * with the given children.
     * <p>
     * Note that the added entries are not automatically normalized, but
     * this must be done manually invoking {@link #normalizeHierarchy()}.
     * @param entryName the directory name (not the path, just the name)
     * @param entries the entries this directory entry is initialized within it
     *
     * @see #createNormalizedHierarchyForPath(String, String, Entry...)
     */
    public DEntry(String entryName, Entry... entries) {
        super(entryName);
        if (CollectionsUtil.isFilled(entries))
            for (Entry entry : entries)
                addEntry(entry, false);
    }

    /**
     * Adds an entry to the entries inside this directory and normalizes it.
     * @param entry the entry to name
     * @see #addEntry(Entry, boolean)
     */
    public void addEntry(Entry entry) {
        addEntry(entry, true);
    }

    /**
     * Adds an entry to the entries inside this directory entry and
     * optionally normalize the nested entries using this directory entry as
     * parent for make those consistent.
     * @param entry the entry to name
     * @param andNormalize whether the entry should be normalized against
     *                     this directory entry as it parent
     *
     * @see #addEntry(Entry)
     */
    public void addEntry(Entry entry, boolean andNormalize) {
        mChildren.putIfAbsent(entry.getName(), entry);
        if (andNormalize)
            normalizeHierarchy();
    }

    /**
     * Returns the children of this directory entry.
     * @return the entries of this entry.
     */
    public Collection<Entry> getEntries() {
        return mChildren.values();
    }

    /**
     * Returns an (the) entry that matches the given entry name.
     * @param entryName the entry name to find
     * @return  the entry that matches the given entry name if exists,
     *          or null otherwise
     *
     * @see #getFEntry(String)
     * @see #getDEntry(String)
     */
    public Entry getEntry(String entryName) {
        return mChildren.get(entryName);
    }

    /**
     * Returns an (the) entry that matches the given entry name and that is
     * an FEntry. If an entry exists but is not an FEntry, null is returned.
     * @param entryName the entry name to find
     * @return  the entry that matches the given entry name if exists,
     *          or null otherwise
     *
     * @see #getEntry(String)
     * @see #getDEntry(String)
     */
    public FEntry getFEntry(String entryName) {
        Entry e = getEntry(entryName);
        return e instanceof FEntry ? (FEntry) e : null;
    }

    /**
     * Returns an (the) entry that matches the given entry name and that is
     * an DEntry. If an entry exists but is not an DEntry, null is returned.
     * @param entryName the entry name to find
     * @return  the entry that matches the given entry name if exists,
     *          or null otherwise
     *
     * @see #getEntry(String)
     * @see #getFEntry(String)
     */
    public DEntry getDEntry(String entryName) {
        Entry e = getEntry(entryName);
        return e instanceof DEntry ? (DEntry) e : null;
    }

    /**
     * Returns whether the entry with the given entry name exists within
     * this entry children.
     * @param entryName the entry name to find
     * @return whether the entry exists
     */
    public boolean containsEntry(String entryName) {
        return getEntry(entryName) != null;
    }

    /**
     * Normalizes this directory entry and every nested entry inside it recursively.
     * <p>
     * The normalization process essentially initializes the underlying file
     * of the entry using the parent entry as parent directory for figure out
     * its path.
     * <p>
     * Note that for make the process work, the root entry this method is called
     * from must have a valid system path as entry name.
     */
    public void normalizeHierarchy() {
        L.debug("Normalizing hierarchy for D-Entry: " + mEntryName);

        for (Entry entry : mChildren.values()) {
            // Initialize the child entry using this entry as parent
            entry.initForParent(mFile);

            // Recursively normalize cache
            if (entry instanceof DEntry)
                ((DEntry) entry).normalizeHierarchy();
        }
    }

    @Override
    public boolean create() {
        L.debug("Creating underlying directory for D-Entry: " + mEntryName);

        Asserts.assertNotNull(mFile,
            "Entry's underlying file should not be null when invoking create()", L);

        boolean creationResult = mFile.exists() || mFile.mkdirs();

        if (!creationResult) {
            L.error("Directory creation has failed");
            return false;
        }

        for (Entry entry : mChildren.values()) {
            creationResult &= entry.create();
        }

        return creationResult;
    }

    /**
     * Loads the nested files of this directory into the children list and
     * continues the process recursively.
     * <p>
     * This entry should be normalized with {@link #normalizeHierarchy()}
     * before using this method.
     */
    public void loadChildren() {
        Asserts.assertNotNull(mFile,
            "Entry's underlying file should not be null when invoking create()", L);

        L.debug("Loading children for D-Entry: " + mFile.getPath());

        File[] nestedFiles = mFile.listFiles();

        if (CollectionsUtil.isFilled(nestedFiles)) {

            // Add the files to the children if these do not exist yet

            for (File f : nestedFiles) {
                L.debug("Loading child: " + f.getName());
                addEntry(
                    f.isFile() ?
                        new FEntry(f) :
                        new DEntry(f),
                    false
                );
            }

            // Loads the child recursively for the children

            for (Entry entry : mChildren.values()) {
                if (entry instanceof DEntry)
                    ((DEntry) entry).loadChildren();
            }
        }
    }
}
