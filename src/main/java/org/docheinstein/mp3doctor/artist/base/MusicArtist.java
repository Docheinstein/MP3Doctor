package org.docheinstein.mp3doctor.artist.base;

import javafx.util.Pair;

/** Represents an artist specialized in music. */
public interface MusicArtist extends Artist {

    /** Represents a disk, which is a pair of disk's name and its producer. */
    class Disk extends Pair<String, String> {

        /**
         * Creates a disk.
         * @param diskName the name of the disk
         * @param producer the producer of the disk
         */
        public Disk(String diskName, String producer) {
            super(diskName, producer);
        }

        /**
         * Returns the name of the disk.
         * @return the name of the disk
         */
        public String getDiskName() {
            return getKey();
        }

        /**
         * Returns the name of the producer of the disk.
         * @return the name of the producer of the disk
         */
        public String getProducer() {
            return getValue();
        }
    }

    /**
     * Returns the genres preferred by the artist
     * @return the preferred genres
     */
    String[] getPreferredGenres();

    /**
     * Returns the discography of the artist, which is a bunch of pair
     * of disk name and the producer of those
     * @return the discography
     */
    Disk[] getDiscography();

    /**
     * Returns the artist's fan page (should be an URL)
     * @return the fan page
     */
    String getFanPage();
}
