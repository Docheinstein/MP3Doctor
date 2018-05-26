package org.docheinstein.mp3doctor.ui.groups.artists;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.docheinstein.mp3doctor.artist.base.MusicArtist;
import org.docheinstein.mp3doctor.artist.base.MusicGroup;
import org.docheinstein.mp3doctor.artist.base.Person;
import org.docheinstein.mp3doctor.artist.base.SoloMusicArtist;
import org.docheinstein.mp3doctor.artist.impl.MusicArtistImpl;
import org.docheinstein.mp3doctor.artist.impl.MusicGroupImpl;
import org.docheinstein.mp3doctor.artist.impl.SoloMusicArtistImpl;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.cache.CacheHierarchy;
import org.docheinstein.mp3doctor.commons.hierarchy.DEntry;
import org.docheinstein.mp3doctor.commons.hierarchy.Entry;
import org.docheinstein.mp3doctor.commons.hierarchy.FEntry;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.commons.utils.CollectionsUtil;
import org.docheinstein.mp3doctor.commons.utils.FXUtil;
import org.docheinstein.mp3doctor.commons.utils.StringUtil;
import org.docheinstein.mp3doctor.artist.ArtistsManager;
import org.docheinstein.mp3doctor.artist.*;
import org.docheinstein.mp3doctor.ui.commons.alert.AlertInstance;
import org.docheinstein.mp3doctor.ui.commons.controller.base.InstantiableReinitializableControllerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a controller that handle an artist's details; shows and let
 * them to be saved to the disk.
 */
public class ArtistDetailsController implements InstantiableReinitializableControllerView {

    protected static final Logger L =
        Logger.createForClass(ArtistDetailsController.class);

    @FXML
    private Label uiHeader;

    @FXML
    private Label uiSoloButton;

    @FXML
    private Label uiGroupButton;

    // Artist details

    @FXML
    private TextField uiStageName;

    @FXML
    private TextArea uiBiography;

    @FXML
    private DatePicker uiDebutDate;

    // Music artist details

    @FXML
    private TextField uiFanPage;

    @FXML
    private VBox uiPreferredGenresContainer;

    @FXML
    private Button uiPreferredGenresButton;

    @FXML
    private VBox uiDiscographyContainer;

    @FXML
    private Button uiDiscographyButton;

    // Person details

    @FXML
    private Node uiPersonDetails;

    @FXML
    private TextField uiName;

    @FXML
    private TextField uiSurname;

    @FXML
    private TextField uiNationality;

    @FXML
    private DatePicker uiBirthDate;

    @FXML
    private DatePicker uiDeathDate;

    // Group details

    @FXML
    private Node uiGroupDetails;

    @FXML
    private VBox uiMembersContainer;

    @FXML
    private Button uiMembersButton;

    @FXML
    private DatePicker uiCreationDate;

    @FXML
    private DatePicker uiDisbandDate;

    /**
     * The underlying artist this controller is handling.
     * The reason why a {@link MusicArtistObservableFileWrapper} wrapper
     * is needed instead of a simple artist is that in this way the instance
     * of artist can be changed (e.g. from {@link SoloMusicArtist} to
     * {@link MusicGroup}) and the wrapper is able to notify the change in order
     * to let the listeners to be consistent with the update.
     */
    private MusicArtistObservableFileWrapper mArtist;

    /** Current selected type of artist (reflects the UI). */
    private MusicArtistFactory.MusicArtistType mCurrentArtistType;

    /**
     * Creates a new artist details controller for an artist with the
     * given identifier (name).
     * @param artistIdentifier the artist's name
     */
    public ArtistDetailsController(String artistIdentifier) {
        setArtist(artistIdentifier);
    }

    @Override
    public String getFXMLAsset() {
        return "artist_details.fxml";
    }

    @FXML
    private void initialize() {
        uiSoloButton.setOnMouseClicked(e ->
            handleArtistTypeChanged(MusicArtistFactory.MusicArtistType.Solo));
        uiGroupButton.setOnMouseClicked(e ->
            handleArtistTypeChanged(MusicArtistFactory.MusicArtistType.Group));

        uiPersonDetails.managedProperty().bind(uiPersonDetails.visibleProperty());
        uiGroupDetails.managedProperty().bind(uiGroupDetails.visibleProperty());

        uiPreferredGenresButton.setOnMouseClicked(
            e -> addSingleTextRow(uiPreferredGenresContainer, null));
        uiMembersButton.setOnMouseClicked(
            e -> addDoubleTextRow(uiMembersContainer, null, null));
        uiDiscographyButton.setOnMouseClicked(
            e -> addDoubleTextRow(uiDiscographyContainer, null, null));

        reinit();
    }

    @Override
    public void reinit() {
        handleArtistTypeChanged(mCurrentArtistType);
        updateArtistDetails();
    }

    /**
     * Sets the artist this controller handles.
     * @param artistIdentifier the artist's name
     */
    public void setArtist(String artistIdentifier) {
        L.debug("Setting details for artist: " + artistIdentifier);

        mArtist = CollectionsUtil.find(
            ArtistsManager.instance().getList(),
            a -> a.getIdentifier().equals(artistIdentifier)
        );

        // Create a new artist file and add it to the manager if doesn't exist yet
        if (mArtist == null) {

            DEntry artistsEntry = CacheHierarchy.instance().getArtistsEntry();
            Entry artistEntry = artistsEntry.getEntry(artistIdentifier);

            if (artistEntry == null) {
                artistEntry = new FEntry(artistIdentifier);
                artistsEntry.addEntry(artistEntry);
            }

            File artistFile = artistEntry.getWrappedFile();

            L.debug("Going to use artist file: " + artistFile);

            mArtist = new MusicArtistObservableFileWrapper(
                MusicArtistFactory.createArtist(artistFile), artistFile);

            ArtistsManager.instance().getList().add(mArtist);
        }

        MusicArtistFactory.MusicArtistType safeType =
            MusicArtistFactory.MusicArtistType.fromArtist(mArtist.get());

        mCurrentArtistType =
            safeType != null ?
                safeType :
                MusicArtistFactory.MusicArtistType.Solo;
    }

    /** Saves the artist details from the UI to the disk. */
    public void saveArtistToCache() {
        L.info("Saving details for artist " + mArtist.getIdentifier() + " to cache");

        MusicArtistImpl a = null;

        // Solo details
        if (mCurrentArtistType == MusicArtistFactory.MusicArtistType.Solo) {
            a = new SoloMusicArtistImpl();
            SoloMusicArtistImpl soloArtist = (SoloMusicArtistImpl) a;

            soloArtist.setName(uiName.getText());
            soloArtist.setSurname(uiSurname.getText());
            soloArtist.setBirthDate(uiBirthDate.getValue());
            soloArtist.setDeathDate(uiDeathDate.getValue());
            soloArtist.setNationality(uiNationality.getText());
        }

        // Group details
        else if (mCurrentArtistType == MusicArtistFactory.MusicArtistType.Group) {
            a = new MusicGroupImpl();
            MusicGroupImpl groupArtist = (MusicGroupImpl) a;

            groupArtist.setCreationDate(uiCreationDate.getValue());
            groupArtist.setDisbandDate(uiDisbandDate.getValue());

            List<SoloMusicArtistImpl> members = new ArrayList<>();
            for (Node node : uiMembersContainer.getChildren()) {
                Asserts.assertTrue(node instanceof Pane,
                    "Member node must be instance of Pane", L);
                Pane box = (Pane) node;
                Asserts.assertTrue(box.getChildren().size() == 2,
                    "Member node must have two child", L);

                String name = ((TextField) box.getChildren().get(0)).getText();
                String surname = ((TextField) box.getChildren().get(1)).getText();

                if (StringUtil.areValid(name, surname)) {
                    SoloMusicArtistImpl sma = new SoloMusicArtistImpl();
                    sma.setName(name);
                    sma.setSurname(surname);
                    members.add(sma);
                }
            }
            groupArtist.setMembers(members.toArray(new SoloMusicArtistImpl[0]));
        }

        Asserts.assertNotNull(a, "Artist can't be null at this point", L);

        a.setFanPage(uiFanPage.getText());
        a.setStageName(uiStageName.getText());
        a.setBiography(uiBiography.getText());
        a.setDebutDate(uiDebutDate.getValue());

        // Genres

        List<String> genres = new ArrayList<>();
        for (Node node : uiPreferredGenresContainer.getChildren()) {
            if (node instanceof TextInputControl) {
                String genre = ((TextInputControl) node).getText();
                if (StringUtil.isValid(genre))
                    genres.add(genre);
            }
        }
        a.setPreferredGenres(genres.toArray(new String[0]));

        // Disks

        List<MusicArtist.Disk> disks = new ArrayList<>();
        for (Node node : uiDiscographyContainer.getChildren()) {
            Asserts.assertTrue(node instanceof Pane,
                "Disk node must be instance of Pane", L);
            Pane box = (Pane) node;
            Asserts.assertTrue(box.getChildren().size() == 2,
                "Disk node must have two child", L);
            String disk = ((TextField) box.getChildren().get(0)).getText();
            String prod = ((TextField) box.getChildren().get(1)).getText();

            if (StringUtil.isValid(disk) /* producer may be null */)
                disks.add(new MusicArtist.Disk(disk, prod));

        }
        a.setDiscography(disks.toArray(new MusicArtist.Disk[0]));

        // Replace the underlying artist of the wrapper so that it
        // can notify the change
        mArtist.set(a);

        AlertInstance.ArtistSaved.show(mArtist.getIdentifier());
    }


    /**
     * Performs the required action for the change of the type of artist
     * selected in the UI from the user.
     * @param artistType the new artist type
     */
    private void handleArtistTypeChanged(MusicArtistFactory.MusicArtistType artistType) {
        // Sets the type
        mCurrentArtistType = artistType;

        // Shows only the categories for the selected type
        uiPersonDetails.setVisible(artistType == MusicArtistFactory.MusicArtistType.Solo);
        uiGroupDetails.setVisible(artistType == MusicArtistFactory.MusicArtistType.Group);

        // Highlights the button of the selected type
        FXUtil.clearClasses(uiSoloButton);
        FXUtil.clearClasses(uiGroupButton);
        FXUtil.setClass(
            artistType == MusicArtistFactory.MusicArtistType.Solo ?
                uiSoloButton :
                uiGroupButton,
            "midnight-more-3");
    }

    /** Updates the UI with the current artist details. */
    private void updateArtistDetails() {
        L.debug("Loading details for artist " + mArtist.getIdentifier());
        MusicArtist ma = mArtist.get();

        if (ma == null) {
            L.verbose("Nothing to load, null artist (clearing fields)");
            FXUtil.setTextFields(null,
                uiName, uiSurname, uiNationality,
                uiStageName, uiBiography, uiFanPage);
            FXUtil.setDatePickers(null,
                uiBirthDate, uiDeathDate, uiCreationDate,
                uiDisbandDate, uiDebutDate);
            FXUtil.clearPanes(
                uiPreferredGenresContainer,
                uiDiscographyContainer,
                uiMembersContainer);
            return;
        }

        // Solo fields
        if (mCurrentArtistType == MusicArtistFactory.MusicArtistType.Solo) {
            SoloMusicArtist sma = (SoloMusicArtist) ma;

            uiName.setText(sma.getName());
            uiSurname.setText(sma.getSurname());
            uiBirthDate.setValue(sma.getBirthDate());
            uiDeathDate.setValue(sma.getDeathDate());
            uiNationality.setText(sma.getNationality());
        }

        // Group fields
        else if (mCurrentArtistType == MusicArtistFactory.MusicArtistType.Group) {
            MusicGroup mg = (MusicGroup) ma;
            uiCreationDate.setValue(mg.getCreationDate());
            uiDisbandDate.setValue(mg.getDisbandDate());

            FXUtil.clearPanes(uiMembersContainer);

            Person[] members = ((MusicGroup) ma).getMembers();
            if (CollectionsUtil.isFilled(members))
                for (Person member : members)
                    addDoubleTextRow(
                        uiMembersContainer,
                        member.getName(),
                        member.getSurname());
        }

        // Common fields

        uiFanPage.setText(ma.getFanPage());
        uiStageName.setText(ma.getStageName());
        uiBiography.setText(ma.getBiography());
        uiDebutDate.setValue(ma.getDebutDate());

        // Genres
        FXUtil.clearPanes(uiPreferredGenresContainer);

        String[] prefGenres = ma.getPreferredGenres();
        if (CollectionsUtil.isFilled(prefGenres))
            for (String genre : prefGenres)
                addSingleTextRow(
                    uiPreferredGenresContainer,
                    genre);

        // Disks
        FXUtil.clearPanes(uiDiscographyContainer);

        MusicArtist.Disk[] disks = ma.getDiscography();
        if (CollectionsUtil.isFilled(disks))
            for (MusicArtist.Disk disk : disks)
                addDoubleTextRow(
                    uiDiscographyContainer,
                    disk.getDiskName(),
                    disk.getProducer());
    }

    /**
     * Adds a single text field row to th given pane.
     * @param pane the pane which the row will be added to
     * @param text the text of the text field
     *
     * @see #addDoubleTextRow(Pane, String, String)
     */
    private static void addSingleTextRow(Pane pane, String text) {
        if (pane != null)
            pane.getChildren().add(new TextField(text));
    }

    /**
     * Adds a double text field row to the given pane.
     * @param pane the pane which the row will be added to
     * @param text1 the text of the first text field
     * @param text2 the text of the second text field
     *
     * @see #addSingleTextRow(Pane, String)
     */
    private static void addDoubleTextRow(Pane pane, String text1, String text2) {
        if (pane != null) {
            HBox hBox = new HBox();
            hBox.setSpacing(10);

            TextField t1 = new TextField(text1);
            TextField t2 = new TextField(text2);

            HBox.setHgrow(t1, Priority.ALWAYS);
            HBox.setHgrow(t2, Priority.ALWAYS);

            hBox.getChildren().add(t1);
            hBox.getChildren().add(t2);

            pane.getChildren().add(hBox);
        }
    }
}
