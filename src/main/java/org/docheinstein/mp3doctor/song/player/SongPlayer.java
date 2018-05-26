package org.docheinstein.mp3doctor.song.player;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.song.Song;
import org.docheinstein.mp3doctor.ui.commons.alert.AlertInstance;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Represents an entity that is able to play songs, and allow basic
 * operations such as play/pause the song.
 * <p>
 * Furthermore this player allow to play a queue of song and thus allow some
 * advanced operations such as go to the next or go to the previous song.
 * <p>
 * It is even possible to listen to the state of the song this player is handling
 * via the {@link SongPlayerListener} interface.
 */
public class SongPlayer {

    /**
     * Interface that let listeners to observe the state of this player and
     * the state of song it is handling at the moment.
     */
    public interface SongPlayerListener {

        /**
         * Called when a new song is started via {@link #play(Song)} or
         * an analog method.
         * @param song the song that has been started
         */
        void onSongStart(Song song);

        /**
         * Called each second when a song is playing, exports details
         * about the current elapsed time and the total duration of the song.
         * @param song the song that is playing
         * @param current the amount of millis elapsed from the start of the song
         * @param duration the total duration of the song in millis
         */
        void onSongProgress(Song song, double current, double duration);

        /**
         * Called when the song is stopped via {@link #stop()} or {@link #pause()}.
         * @param song the song that has been stopped
         */
        void onSongStopped(Song song);

        /**
         * Called when the song is finished (because all the song has been
         * played, not because it has been stopped).
         * @param song the song that has finished to be played
         */
        void onSongFinished(Song song);
    }

    private static final Logger L = Logger.createForClass(SongPlayer.class);

    /** Unique instance of this class. */
    private static final SongPlayer INSTANCE = new SongPlayer();

    /**
     * Returns the unique instance of this class.
     * @return the unique instance of this class
     */
    public static SongPlayer instance() {
        return INSTANCE;
    }

    /** Listeners that listen to the changes to the state of the player. */
    private final Set<SongPlayerListener> mListeners = new CopyOnWriteArraySet<>();

    /** Scheduler that handles the progress task for the song that is playing. */
    private final ScheduledExecutorService mSongProgressScheduler =
        Executors.newSingleThreadScheduledExecutor();

    /** Task of the progress of the song. */
    // This is kept since it might be stopped when the song is pause, stopped,
    // finished or whatever
    private ScheduledFuture mSongProgressTask;

    /** Underlying media player, gently taken from JavaFX. */
    private MediaPlayer mPlayer;

    /** Queue of the songs that have to be reproduced. */
    private List<Song> mSongQueue;

    /** Current index of the song in play within {@link #mSongQueue}. */
    private int mCurrentSongIndex = -1;

    /** The song in execution. */
    // This actually is not needed since with the index and the queue the
    // song in execution can be retrieved at any time, but is kept
    // just for the convenience of not call mSongQueue.get(mCurrentSongIndex)
    // each time (e.g. in the onSongProgress callback).
    private Song mCurrentSong;

    /** Duration in millis of the song in execution. */
    // As mCurrentSong this is not needed but kept for convenience
    private double mSongDuration;

    /** Creates a new SongPlayer, */
    private SongPlayer() {}

    /**
     * Adds a listener that will be notified when the state of the player change.
     * @param listener the listener that will listen to the state of the player
     */
    public void addListener(SongPlayerListener listener) {
        if (listener != null)
            mListeners.add(listener);
    }

    /**
     * Removes a previously added listener from the listener set.
     * @param listener the listener that won't listen to the state of
     *                 the player anymore
     */
    public void removeListener(SongPlayerListener listener) {
        mListeners.remove(listener);
    }

    /**
     * Plays the given song without any play queue (i.e. when the song finishes
     * nothing is player and next/prev is not available).
     * @param song the song to play
     *
     * @see #play(List, Song)
     * @see #play(List, int)
     */
    public void play(Song song) {
        Asserts.assertNotNull(song, "Can't play a null song", L);

        List<Song> songQueue = new ArrayList<>();
        songQueue.add(song);
        play(songQueue, 0);
    }

    /**
     * Plays the given song using the given play queue.
     * <p>
     * Note that the song MUST be within the elements of the song queue,
     * otherwise this method aborts.
     * @param songQueue the play queue
     * @param song the first song to play of the play queue
     */
    public void play(List<Song> songQueue, Song song) {
        Asserts.assertNotNull(songQueue, "Can't play a null song queue", L);
        Asserts.assertNotNull(song, "Can't play a null song", L);
        play(songQueue, songQueue.indexOf(song));
    }

    /**
     * Plays the song at the given index within the given play queue.
     * @param songQueue the play queue
     * @param firstSongIndex the index of the first song to play of the play queue
     */
    public void play(List<Song> songQueue, int firstSongIndex) {
        Asserts.assertNotNull(songQueue, "Can't play a null song queue", L);

        mSongQueue = songQueue;
        mCurrentSongIndex = firstSongIndex;

        if (mCurrentSongIndex < 0 || mCurrentSongIndex >= mSongQueue.size()) {
            L.warn("Can't play a song with index out of queue's bounds");
            return;
        }

        mCurrentSong = mSongQueue.get(mCurrentSongIndex);

        L.info("Playing song: " + mCurrentSong);
        L.debug("Song queue size: " + mSongQueue.size());

        if (isInitialized()) {
            L.debug("Stopping previous instance of media player");
            stop();
        }

        File songFile = mCurrentSong.getWrappedFile();

        if (!songFile.exists()) {
            L.error("Can't play a non existing song");
            AlertInstance.SongFileNotExist.show(mCurrentSong);
            return;
        }

        mPlayer = new MediaPlayer(new Media(songFile.toURI().toString()));

        mPlayer.setOnPlaying(this::onSongStarted);
        mPlayer.setOnStopped(this::onSongStopped);
        mPlayer.setOnPaused(this::onSongStopped);
        mPlayer.setOnEndOfMedia(this::onSongFinished);

        mPlayer.play();
    }

    /**
     * Pauses the song that is currently playing.
     * <p>
     * This does nothing if no song is playing at the moment.
     */
    public void pause() {
        if (!isPlaying()) {
            L.warn("Can't pause: no song is going on");
            return;
        }

        L.info("Pausing song" + mCurrentSong);
        mPlayer.pause();
    }

    /**
     * Resumes the songs previously paused via {@link #pause()}.
     * <p>
     * This does nothing if the current song is not in pause.
     */
    public void resume() {
        if (!isPaused()) {
            L.warn("Can't resume: no song is going on");
            return;
        }

        L.info("Resuming song " + mCurrentSong);
        mPlayer.play();
    }

    /**
     * Stops the song that is currently playing.
     * <p>
     * This does nothing if no song is playing at the moment.
     */
    public void stop() {
        if (!isInitialized()) {
            L.warn("Can't stop: player is not even initialized");
            return;
        }

        L.info("Stopping song");
        mPlayer.stop();
    }

    /** Plays the next song in the play queue, if possible. */
    public void next() {
        L.info("Playing next song");
        mCurrentSongIndex++;
        play(mSongQueue, mCurrentSongIndex);
    }

    /** Plays the previous song in the play queue, if possible. */
    public void prev() {
        L.info("Playing previous song");
        mCurrentSongIndex--;
        play(mSongQueue, mCurrentSongIndex);
    }

    /**
     * Returns whether the underlying media player has been initialized.
     * <p>
     * The media player is actually initialized if at least a song has been
     * played through this player with {@link #play(List, Song)} or an analog
     * method.
     * @return whether the media player is initialized
     */
    public boolean isInitialized() {
        return mPlayer != null;
    }

    /**
     * Returns whether the current song is playing.
     * @return whether the current song is playing
     */
    public boolean isPlaying() {
        return  isInitialized() &&
                mPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    /**
     * Returns whether the current song is paused.
     * @return whether the current song is paused
     */
    public boolean isPaused() {
        return  isInitialized() &&
                mPlayer.getStatus() == MediaPlayer.Status.PAUSED;
    }

    /**
     * Handles the start of the song; start the periodic progress task
     * and notifies the listeners about the state change.
     */
    private void onSongStarted() {
        Asserts.assertNotNull(mCurrentSong, "Current song must not be null", L);

        L.debug("Song is actually started");
        L.verbose("Going to notify listeners about song start");

        mSongDuration = mPlayer.getTotalDuration().toMillis();

        startSongProgressTask();

        mListeners.forEach(l -> l.onSongStart(mCurrentSong));
    }

    /**
     * Handles the stop of the song; stops the periodic progress task
     * and notifies the listeners about the state change.
     */
    private void onSongStopped() {
        Asserts.assertNotNull(mCurrentSong, "Current song must not be null", L);

        L.debug("Song has actually been stopped (pause / stop)");
        L.verbose("Going to notify listeners about song stop");

        stopSongProgressTask();

        mListeners.forEach(l -> l.onSongStopped(mCurrentSong));
    }

    /**
     * Handles the finish of the song; stops the periodic progress task
     * and notifies the listeners about the state change.
     * <p>
     * Furthermore, this starts the next song, if possible.
     */
    private void onSongFinished() {
        Asserts.assertNotNull(mCurrentSong, "Current song must not be null", L);

        L.debug("Song is actually finished");
        L.verbose("Going to notify listeners about song end");

        stopSongProgressTask();

        mListeners.forEach(l -> l.onSongFinished(mCurrentSong));

        next();
    }

    /**
     * Starts the periodic progress task that notifies the listeners
     * about the progress of the song every second.
     * <p>
     * The started task can be stopped via {@link #stopSongProgressTask()}.
     */
    private void startSongProgressTask() {
        L.verbose("Song duration: " + mSongDuration / 1000 + " seconds");

        mSongProgressTask = mSongProgressScheduler.scheduleAtFixedRate(() -> {
            if (!isPlaying()) {
                L.warn("Player is not playing but progress task is going on. " +
                    "This task should be cancelled before the player is stopped");
                return;
            }

            mListeners.forEach(l -> l.onSongProgress(
                mCurrentSong,
                mPlayer.getCurrentTime().toMillis(),
                mSongDuration)
            );
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    /** Stops the periodic progress task. */
    private void stopSongProgressTask() {
        if (mSongProgressTask != null) {
            mSongProgressTask.cancel(true);
            mSongProgressTask = null;
        }
    }
}
