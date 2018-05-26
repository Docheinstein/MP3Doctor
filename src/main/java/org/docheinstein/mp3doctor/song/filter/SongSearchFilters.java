package org.docheinstein.mp3doctor.song.filter;

import org.docheinstein.mp3doctor.commons.utils.StringUtil;
import org.docheinstein.mp3doctor.song.Song;


/** Contains all the filters that operate on {@link Song}. */
public class SongSearchFilters {

    /** {@link SongSearchFilter} that operates on {@link Song#getTitle()}. */
    public static class Title extends SongSearchFilterImpl<String> {

        /** Creates a filter that evaluates a song over the given field. */
        public Title(String filterValue) { super(filterValue); }

        /**
         * Returns whether the given song contains the field this filter
         * has been created with in {@link Song#getTitle()}.
         * @param song the song to check
         * @return whether the song contains a part of the field of this filter
         */
        @Override
        public boolean check(Song song) {
            return
                !StringUtil.isValid(mFilterValue) ||
                StringUtil.isValid(song.getTitle()) &&
                StringUtil.containsIgnoreCase(song.getTitle(), mFilterValue);
        }
    }

    /** {@link SongSearchFilter} that operates on {@link Song#getArtist()}. */
    public static class Artist extends SongSearchFilterImpl<String> {

        /** Creates a filter that evaluates a song over the given field. */
        public Artist(String filterValue) { super(filterValue); }

        /**
         * Returns whether the given song contains the field this filter
         * has been created with in {@link Song#getArtist()}.
         * @param song the song to check
         * @return whether the song contains a part of the field of this filter
         */
        @Override
        public boolean check(Song song) {
            return
                !StringUtil.isValid(mFilterValue) ||
                StringUtil.isValid(song.getArtist()) &&
                StringUtil.containsIgnoreCase(song.getArtist(), mFilterValue);
        }
    }

    /** {@link SongSearchFilter} that operates on {@link Song#getAlbum()}. */
    public static class Album extends SongSearchFilterImpl<String> {

        /** Creates a filter that evaluates a song over the given field. */
        public Album(String filterValue) { super(filterValue); }

        /**
         * Returns whether the given song contains the field this filter
         * has been created with in {@link Song#getAlbum()}.
         * @param song the song to check
         * @return whether the song contains a part of the field of this filter
         */
        @Override
        public boolean check(Song song) {
            return
                !StringUtil.isValid(mFilterValue) ||
                StringUtil.isValid(song.getAlbum()) &&
                StringUtil.containsIgnoreCase(song.getAlbum(), mFilterValue);
        }
    }

    /** {@link SongSearchFilter} that operate on {@link Song#getYear()}. */
    public static class Year extends SongSearchFilterImpl<String> {

        /** Creates a filter that evaluates a song over the given field. */
        public Year(String filterValue) { super(filterValue); }

        /**
         * Returns whether the given song contains the field this filter
         * has been created with in {@link Song#getYear()}.
         * @param song the song to check
         * @return whether the song contains a part of the field of this filter
         */
        @Override
        public boolean check(Song song) {
            return
                !StringUtil.isValid(mFilterValue) ||
                StringUtil.isValid(song.getYear()) &&
                StringUtil.containsIgnoreCase(song.getYear(), mFilterValue);
        }
    }

    /** {@link SongSearchFilter} that operate on {@link Song#getGenre()}. */
    public static class Genre extends SongSearchFilterImpl<String> {

        /** Creates a filter that evaluates a song over the given field. */
        public Genre(String filterValue) { super(filterValue); }

        /**
         * Returns whether the given song contains the field this filter
         * has been created with in {@link Song#getGenre()}.
         * @param song the song to check
         * @return whether the song contains a part of the field of this filter
         */
        @Override
        public boolean check(Song song) {
            return
                !StringUtil.isValid(mFilterValue) ||
                StringUtil.isValid(song.getGenre()) &&
                StringUtil.containsIgnoreCase(song.getGenre(), mFilterValue);
        }
    }

    /** {@link SongSearchFilter} that operate on {@link Song#getLyrics()}. */
    public static class Lyrics extends SongSearchFilterImpl<Boolean> {
        public Lyrics(Boolean filterValue) { super(filterValue); }


        /**
         * Returns whether the given song has valid lyrics if this filter
         * has been created with value true, or whether the given song has
         * invalid lyrics if this filter has been created with value false.
         * @param song the song to check
         * @return whether the song has/hasn't lyrics based on this filter value
         */
        @Override
        public boolean check(Song song) {
            return mFilterValue == StringUtil.isValid(song.getLyrics());
        }
    }

    /** {@link SongSearchFilter} that operate on {@link Song#getCover()}. */
    public static class Cover extends SongSearchFilterImpl<Boolean> {
        public Cover(Boolean filterValue) { super(filterValue); }

        /**
         * Returns whether the given song has a valid cover if this filter
         * has been created with value true, or whether the given song has
         * an invalid cover if this filter has been created with value false.
         * @param song the song to check
         * @return whether the song has/has not a cover based on this filter value
         */
        @Override
        public boolean check(Song song) {
            return mFilterValue == (song.getCover() != null);
        }
    }
}
