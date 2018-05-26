package org.docheinstein.mp3doctor.artist;

import javafx.util.Pair;
import org.docheinstein.mp3doctor.artist.base.*;
import org.docheinstein.mp3doctor.artist.impl.MusicArtistImpl;
import org.docheinstein.mp3doctor.artist.impl.MusicGroupImpl;
import org.docheinstein.mp3doctor.artist.impl.SoloMusicArtistImpl;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.commons.utils.CollectionsUtil;
import org.docheinstein.mp3doctor.commons.utils.FileUtil;
import org.docheinstein.mp3doctor.commons.utils.StringUtil;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Contains handy method for create music artist artists,
 * loading the details from the disk and for save those to the disk.
 */
public class MusicArtistFactory {

    /** Type of music artist. */
    public enum MusicArtistType {
        Solo("solo"),
        Group("group")
        ;

        /**
         * Creates an artist type for the given identifier.
         * @param identifier the identifier of this artist type
         */
        MusicArtistType(String identifier) {
            mIdentifier = identifier;
        }

        /** A string that uniquely identify this type of artist. */
        private String mIdentifier;

        /**
         * Returns a string that uniquely identify this type of artist.
         * @return the identifier ot this artist type
         */
        public String getIdentifier() {
            return mIdentifier;
        }

        /**
         * Returns a type based on the type of given instance of artist.
         * @param artist the artist
         * @return the type associated with the artist
         */
        public static MusicArtistType fromArtist(MusicArtist artist) {
            if (artist instanceof Person)
                return Solo;
            if (artist instanceof org.docheinstein.mp3doctor.artist.base.Group)
                return Group;
            return null;
        }

        /**
         * Return the type associated with the given identifier.
         * @param identifier the identifier
         * @return the type associated with the identifier
         */
        public static MusicArtistType fromIdentifier(String identifier) {
            if (identifier == null)
                return null;
            if (identifier.equals("solo"))
                return Solo;
            if (identifier.equals("group"))
                return Group;
            return null;
        }
    }

    // Keys that are used for save and load the artist' details from the disk

    private static final String ARTIST_TYPE = "artist_type";
    private static final String STAGE_NAME = "stage_name";
    private static final String BIOGRAPHY = "biography";
    private static final String DEBUT_DATE = "debut_date";

    private static final String FAN_PAGE = "fan_page";
    private static final String PREFERRED_GENRES = "preferred_genres";
    private static final String DISCOGRAPHY = "discography";

    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String NATIONALITY = "nationality";
    private static final String BIRTH_DATE = "birth_date";
    private static final String DEATH_DATE = "death_date";

    private static final String CREATION_DATE = "creation_date";
    private static final String DISBAND_DATE = "disband_date";
    private static final String MEMBERS = "members";

    /** Marks the begin of the value for a key. */
    private static final String CONTENT_START = "{{";
    private static final String CONTENT_START_REGEX = "\\{\\{";

    /** Marks the begin of the value for a key. */
    private static final String CONTENT_END = "}}";
    private static final String CONTENT_END_REGEX = "}}";

    /** Separate the tuples. */
    private static final String LIST_SEPARATOR = ",";

    /** Separate the fields of a tuple with multiple field. */
    private static final String TUPLE_SEPARATOR = ":";

    private static final Logger L = Logger.createForClass(MusicArtistFactory.class);

    /**
     * Creates an artist parsing the given file.
     * @param artistFile the file that contains the artist details
     *                   saved via {@link #saveArtist(MusicArtist, File)}
     * @return the artist created by parsing the file
     */
    public static MusicArtist createArtist(File artistFile) {
        return Reader.loadFromFile(artistFile);
    }

    /**
     * Saves the given artist to the given file so that i can be loaded via
     * {@link #createArtist(File)}.
     * @param artist the artist to save
     * @param file the file in which save the artist details
     */
    public static void saveArtist(MusicArtist artist, File file) {
        Writer.saveToFile(artist, file);
    }

    /**
     * The class responsible for read the artist details from file,
     * parse those and create a valid artist.
     */
    private static class Reader {

        /**
         * Creates an artist parsing the given file.
         * @param f the file that contains the artist details
         *          saved via {@link #saveArtist(MusicArtist, File)}
         * @return the artist created by parsing the file
         */
        private static MusicArtist loadFromFile(File f) {
            L.info("Loading MusicArtist from file " + f.getPath());

            Asserts.assertNotNull(f, "Can't create an artist for a null file");

            if (!f.exists()) {
                L.error("Can't create an artist for a non existing file");
                return null;
            }

            Scanner scanner = FileUtil.getScanner(f);

            if (scanner == null) {
                L.error("Can't retrieve a scanner for file: " + f.getAbsolutePath());
                return null;
            }

            // Scans the file and put the values for the keys inside the map

            HashMap<String, String> keyvals = new HashMap<>();

            L.debug("Going parse file for create a MusicArtist");
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                L.verbose("Scanning line: "  + line);
                Pair<String, String> keyval = getKeyValueForLine(line);
                if (keyval != null) {
                    L.verbose("Found (K ; V) := (" + keyval.getKey() + " ; " + keyval.getValue() + ")");
                    keyvals.put(keyval.getKey(), keyval.getValue());
                }
            }

            scanner.close();

            MusicArtistType artistType =
                MusicArtistType.fromIdentifier(keyvals.get(ARTIST_TYPE));

            if (artistType == null) {
                L.error("Can't create an artist since the type is not found" +
                    " in the file or is of an unknown type");
                return null;
            }

            L.debug("Creating MusicArtist of type: " + artistType);

            MusicArtist ma = null;

            if (artistType == MusicArtistType.Solo)
                ma = new SoloMusicArtistImpl();
            else if (artistType == MusicArtistType.Group)
                ma = new MusicGroupImpl();

            if (ma == null) {
                L.error("Can't create an artist since the type is unknown");
                return null;
            }

            // For each (key, val) found set the associated properties
            // of the artist

            for (Map.Entry<String, String> kv : keyvals.entrySet()) {
                if (artistType == MusicArtistType.Solo)
                    assignSoloArtistPropertyForKeyValue(
                        (SoloMusicArtistImpl) ma, kv.getKey(), kv.getValue());
                else assignGroupArtistPropertyForKeyValue(
                    (MusicGroupImpl) ma, kv.getKey(), kv.getValue());
            }

            return ma;
        }

        /**
         * Returns a pair that is the value for the key found in the given line.
         * @param line the line to parse
         * @return a pair (K,V) if the line is valid
         */
        private static Pair<String, String> getKeyValueForLine(String line) {
            String[] lineComponents = line.split(CONTENT_START_REGEX);
            if (lineComponents.length < 2) {
                L.verbose("--No key found in line");
                return null;
            }

            String key = lineComponents[0];
            String value = lineComponents[1].split(CONTENT_END_REGEX)[0];

            // L.verbose("\tFound key and value");
            return new Pair<>(
                key != null ? key.trim() : null,
                value !=  null ? value.trim() : null);
        }

        /**
         * Sets the value of the property associated with the given key for
         * a given {@link MusicArtist}.
         * @param ma the artist
         * @param key the key
         * @param val the value
         */
        private static void assignMusicArtistPropertyForKeyValue(
            MusicArtistImpl ma, String key, String val) {

            if (ma == null || key == null || val == null)
                return;

            switch (key) {
                case STAGE_NAME: ma.setStageName(val); break;
                case BIOGRAPHY: ma.setBiography(val); break;
                case DEBUT_DATE: ma.setDebutDate(LocalDate.parse(val)); break;
                case FAN_PAGE: ma.setFanPage(val); break;
                case PREFERRED_GENRES:
                    // Split the genres with the separator
                    if (StringUtil.isValid(val)) {
                        String[] genres = val.split(LIST_SEPARATOR);
                        ma.setPreferredGenres(genres);
                    }
                    break;
                case DISCOGRAPHY:
                    if (StringUtil.isValid(val)) {
                        String[] disksStrings = val.split(LIST_SEPARATOR);
                        List<MusicArtist.Disk> disks = new ArrayList<>();
                        for (String diskString : disksStrings) {
                            String[] diskComponents = diskString.split(TUPLE_SEPARATOR);
                            if (diskComponents.length != 2) {
                                L.warn("The key or the value of the pair is invalid; aborting assignment");
                                continue;
                            }
                            disks.add(new MusicArtist.Disk(diskComponents[0], diskComponents[1]));
                        }
                        ma.setDiscography(disks.toArray(new MusicArtist.Disk[0]));
                    }
                    break;
            }
        }

        /**
         * Sets the value of the property associated with the given key for
         * a given {@link SoloMusicArtist}.
         * This method also handles the property that belongs to {@link MusicArtist} by calling
         * {@link #assignMusicArtistPropertyForKeyValue(MusicArtistImpl, String, String)}.
         * @param sma the solo music artist
         * @param key the key
         * @param val the value
         */
        private static void assignSoloArtistPropertyForKeyValue(
            SoloMusicArtistImpl sma, String key, String val) {
            if (sma == null || key == null || val == null)
                return;

            assignMusicArtistPropertyForKeyValue(sma, key, val);

            switch (key) {
                case NAME: sma.setName(val); break;
                case SURNAME: sma.setSurname(val); break;
                case NATIONALITY: sma.setNationality(val); break;
                case BIRTH_DATE: sma.setBirthDate(LocalDate.parse(val)); break;
                case DEATH_DATE: sma.setDeathDate(LocalDate.parse(val)); break;
            }
        }

        /**
         * Sets the value of the property associated with the given key for
         * a given {@link MusicGroupImpl}.
         * This method also handles the property that belongs to {@link MusicArtist} by calling
         * {@link #assignMusicArtistPropertyForKeyValue(MusicArtistImpl, String, String)}.
         * @param mg the music group
         * @param key the key
         * @param val the value
         */
        private static void assignGroupArtistPropertyForKeyValue(
            MusicGroupImpl mg, String key, String val) {

            if (mg == null || key == null || val == null)
                return;

            assignMusicArtistPropertyForKeyValue(mg, key, val);


            switch (key) {
                case CREATION_DATE: mg.setCreationDate(LocalDate.parse(val)); break;
                case DISBAND_DATE: mg.setDisbandDate(LocalDate.parse(val)); break;
                case MEMBERS:
                    if (StringUtil.isValid(val)) {
                        String[] membersStrings = val.split(LIST_SEPARATOR);
                        List<Person> members = new ArrayList<>();
                        for (String membersString : membersStrings) {
                            String[] memberComponents = membersString.split(TUPLE_SEPARATOR);
                            if (memberComponents.length != 2) {
                                L.warn("The key or the value of the pair is invalid; aborting assignment");
                                continue;
                            }
                            SoloMusicArtistImpl sma = new SoloMusicArtistImpl();
                            sma.setName(memberComponents[0]);
                            sma.setSurname(memberComponents[1]);
                            members.add(sma);
                        }
                        mg.setMembers(members.toArray(new Person[0]));
                    }
                    break;
            }
        }
    }

    /**
     * The class responsible for write the artist details to file.
     */
    private static class Writer {

        /**
         * Saves the given artist to the given file so that i can be loaded via
         * {@link #createArtist(File)}.
         * @param musicArtist the artist to save
         * @param artistFile the file in which save the artist details
         */
        private static void saveToFile(MusicArtist musicArtist, File artistFile) {
            L.info("Saving artist " + artistFile + " to disk");

            Asserts.assertNotNull(artistFile, "Can't save an artist to a null file");

            PrintWriter writer = FileUtil.getWriter(artistFile);

            if (writer == null) {
                L.error("Can't retrieve a print writer for file " + artistFile.getAbsolutePath());
                return;
            }

            MusicArtistType artistType = MusicArtistType.fromArtist(musicArtist);

            if (artistType == null) {
                L.error("Unknown artist type, aborting save");
                return;
            }

            writeString(writer, ARTIST_TYPE, artistType.getIdentifier());
            writeString(writer, STAGE_NAME, musicArtist.getStageName());
            writeString(writer, BIOGRAPHY, musicArtist.getBiography());
            writeDate(writer, DEBUT_DATE, musicArtist.getDebutDate());

            writeString(writer, FAN_PAGE, musicArtist.getFanPage());
            writeArray(writer, PREFERRED_GENRES, musicArtist.getPreferredGenres(), s -> s);

            writeArray(writer, DISCOGRAPHY, musicArtist.getDiscography(),
                disk ->
                    disk.getDiskName() + TUPLE_SEPARATOR +
                    disk.getProducer());

            if (artistType == MusicArtistType.Solo) {
                SoloMusicArtist sma = (SoloMusicArtist) musicArtist;
                writeString(writer, NAME, sma.getName());
                writeString(writer, SURNAME, sma.getSurname());
                writeString(writer, NATIONALITY, sma.getNationality());
                writeDate(writer, BIRTH_DATE, sma.getBirthDate());
                writeDate(writer, DEATH_DATE, sma.getDeathDate());
            }
            else if (artistType == MusicArtistType.Group) {
                MusicGroup mg = (MusicGroup) musicArtist;
                writeDate(writer, CREATION_DATE, mg.getCreationDate());
                writeDate(writer, DISBAND_DATE, mg.getDisbandDate());
                writeArray(writer, MEMBERS, ((MusicGroup) musicArtist).getMembers(),
                    art -> art.getName() + TUPLE_SEPARATOR + art.getSurname());
            }

            writer.close();
        }

        /**
         * Writes a value of type string for the given key on the writer.
         * @param writer the writer
         * @param key the key
         * @param value the string
         */
        private static void writeString(PrintWriter writer, String key, String value) {
            if (!StringUtil.isValid(value))
                return;

            L.verbose("Writing to file (K ; V) := (" + key + " ; " + value + ")");
            writer.println(key + " " + CONTENT_START  + value + CONTENT_END);
            writer.println();
        }

        /**
         * Writes a value of type date for the given key on the writer.
         * @param writer the writer
         * @param key the key
         * @param date the date
         */
        private static void writeDate(PrintWriter writer, String key, LocalDate date) {
            if (date != null)
                writeString(writer, key, date.toString());
        }

        /**
         * Writes an array for the given key on the writer.
         * @param writer the writer
         * @param key the key
         * @param elements the array
         * @param mappingFunction a function that map every object of the array
         *                        to its string representation
         * @param <E> the type of the elements of the array
         */
        private static <E> void writeArray(PrintWriter writer,
                                           String key,
                                           E[] elements,
                                           Function<? super E, ? extends String> mappingFunction) {
            if (CollectionsUtil.isFilled(elements))
                writeString(
                    writer,
                    key,
                    // Concat the string representation of the objects with ", "
                    Arrays
                        .stream(elements)
                        .map(mappingFunction)
                        .collect(Collectors.joining(LIST_SEPARATOR))
                );
        }
    }
}
