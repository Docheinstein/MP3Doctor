package org.docheinstein.mp3doctor.artist.base;

import javafx.beans.Observable;

import java.time.LocalDate;

/** Represents a generic artist. */
public interface Artist extends Observable {

    /**
     * Returns the stage name, i.e. an invented name.
     * @return the stage name
     */
    String getStageName();

    /**
     * Returns the biography of the artist.
     * @return the biography
     */
    String getBiography();

    /**
     * Returns the date of the first debut of the artist in sense
     * that depends on the implementation of this interface.
     * @return the debut date
     */
    LocalDate getDebutDate();
}
