package org.docheinstein.mp3doctor.commons.adt;

import java.io.File;

/** Entity that wraps a file and is able to provide it. */
public interface FileWrapper {

    /**
     * Returns the file this entity wraps.
     * @return the wrapped file.
     */
    File getWrappedFile();
}
