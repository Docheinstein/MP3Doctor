package org.docheinstein.mp3doctor.artist.base;

import java.time.LocalDate;

/** Represents a natural person. */
public interface Person {
    /**
     * Returns the first name of the person.
     * @return the name
     */
    String getName();

    /**
     * Returns the last name of the person.
     * @return the surname
     */
    String getSurname();

    /**
     * Returns the birth date of the person.
     * @return the birth date
     */
    LocalDate getBirthDate();

    /**
     * Returns the death date of the person.
     * @return the death date
     */
    LocalDate getDeathDate();

    /**
     * Returns the nationality of the person.
     * @return the nationality
     */
    String getNationality();
}
