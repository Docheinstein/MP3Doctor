package org.docheinstein.mp3doctor.commons.utils;

import javafx.css.Styleable;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.docheinstein.mp3doctor.commons.logger.Logger;

import java.awt.image.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

/** Contains utility methods for Java FX. */
public class FXUtil {

    private static final Logger L = Logger.createForTag("{FX_UTIL}");

    /**
     * Adds a stylesheet to the given node.
     * @param node a node
     * @param stylesheet the stylesheet to add to the given node
     */
    public static void addStylesheet(Parent node, URL stylesheet) {
        node.getStylesheets().add(stylesheet.toExternalForm());
    }

    /**
     * Adds the style class for the given {@link Styleable}.
     * @param obj the styleable object
     * @param styleClass the style class
     *
     * @see #setClass(Styleable, String)
     */
    public static void addClass(Styleable obj, String styleClass) {
        if (!obj.getStyleClass().contains(styleClass))
            obj.getStyleClass().add(styleClass);
    }

    /**
     * Removes every style class from the given {@link Styleable}.
     * @param obj the styleable object
     */
    public static void clearClasses(Styleable obj) {
        obj.getStyleClass().clear();
    }

    /**
     * Sets the style class for the given {@link Styleable} and removes
     * any already existing class.
     * @param obj the styleable object
     * @param styleClass the style class
     *
     * @see #addClass(Styleable, String)
     */
    public static void setClass(Styleable obj, String styleClass) {
        clearClasses(obj);
        addClass(obj, styleClass);
    }

    /**
     * Converts a {@link BufferedImage} to an {@link Image}.
     * @param bufferedImage the awt image
     * @return the java fx image
     */
    public static Image awtBufferedImageToImage(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    /**
     * Creates a {@link Node} for the given FXML asset and binds it with
     * the given controller.
     * @param controller the controller bound with the node
     * @param fxml the .fxml asset
     * @return the node associated with the given asset, bound with the given controller
     */
    public static Node createNode(Object controller, URL fxml) {
        Asserts.assertNotNull(controller, "Can't bound to null controller");

        L.debug("Creating node for FXML: " + fxml + " bound to controller " +
            controller.getClass().getSimpleName());

        FXMLLoader loader = new FXMLLoader(fxml);
        loader.setController(controller);
        Node node = null;
        try {
            node = loader.load();
        } catch (IOException e) {
            L.error("Error occurred while loading FXML for URL: " + fxml, e);
            return null;
        }

        Asserts.assertNotNull(node, "Error occurred while loading FXML for URL: " + fxml);

        return node;
    }

    /**
     * Creates an {@link Image} for the given resource name.
     * <p>
     * The resource name must be the filename of the resource,
     * without any leading path.
     * @param clazz the class to use for retrieve the class loader that loads the image
     * @param resourceName the filename of the image to create
     * @return an image for the given resource name
     *
     * @see #createImage(String)
     */
    public static Image createImage(Class clazz, String resourceName) {
        return new Image(ResourceUtil.getImageStream(clazz, resourceName));
    }

    /**
     * Creates an {@link Image} for the given resource name.
     * <p>
     * The resource name must be the filename of the resource,
     * without any leading path.
     * @param resourceName the filename of the image to create
     * @return an image for the given resource name
     *
     * @see #createImage(Class, String)
     */
    public static Image createImage(String resourceName) {
        return new Image(ResourceUtil.getImageStream(resourceName));
    }

    /**
     * Attaches a node to an anchor pane using 0 as anchor value for all
     * the directions and removes the already attached nodes.
     * @param parent the parent anchor pane
     * @param child the child node
     *
     * @see #attachToAnchorPane(AnchorPane, Node, double, double, double, double, boolean)
     */
    public static void attachToAnchorPane(AnchorPane parent, Node child) {
        attachToAnchorPane(parent, child, 0);
    }

    /**
     * Attaches a node to an anchor pane using the given anchor value
     * for all the directions and removes the already attached nodes.
     * @param parent the parent anchor pane
     * @param child the child node
     * @param anchor the anchor value to use for all the directions
     *
     * @see #attachToAnchorPane(AnchorPane, Node, double, double, double, double, boolean)
     */
    public static void attachToAnchorPane(AnchorPane parent, Node child,
                                          double anchor) {
        attachToAnchorPane(parent, child, anchor, true);
    }


    /**
     * Attaches a node to an anchor pane using the given anchor value
     * for all the directions.
     * @param parent the parent anchor pane
     * @param anchor the anchor value to use for all the directions
     * @param removeAttachedNodes whether remove the already attached nodes from
     *                            the anchor pane before attach the new child
     *
     * @see #attachToAnchorPane(AnchorPane, Node, double, double, double, double, boolean)
     */
    public static void attachToAnchorPane(AnchorPane parent, Node child,
                                          double anchor, boolean removeAttachedNodes) {
        attachToAnchorPane(
            parent, child, anchor, anchor, anchor, anchor, removeAttachedNodes);
    }


    /**
     * Attaches a node to an anchor pane  using the given anchor values.
     * @param parent the parent anchor pane
     * @param child the child node
     * @param l the left anchor value
     * @param t the top anchor value
     * @param r the right anchor value
     * @param b the bottom anchor value
     * @param removeAttachedNodes whether remove the already attached nodes from
     *                            the anchor pane before attach the new child
     */
    public static void attachToAnchorPane(AnchorPane parent, Node child,
                                          double l, double t, double r, double b,
                                          boolean removeAttachedNodes) {
        AnchorPane.setLeftAnchor(child, l);
        AnchorPane.setRightAnchor(child, r);
        AnchorPane.setTopAnchor(child, t);
        AnchorPane.setBottomAnchor(child, b);

        if (removeAttachedNodes)
            parent.getChildren().clear();

        parent.getChildren().add(child);
    }

    /**
     * Sets the same text for all the given {@link TextInputControl}.
     * @param text the string
     * @param textFields the text controls that must be set with the given string
     */
    public static void setTextFields(String text, TextInputControl... textFields) {
        if (CollectionsUtil.isFilled(textFields))
            for (TextInputControl tf : textFields)
                if (tf != null)
                    tf.setText(text);
    }

    /**
     * Sets the same date for all the given {@link DatePicker}.
     * @param date the date
     * @param datePickers the date pickers that must be set with the given string
     */
    public static void setDatePickers(LocalDate date, DatePicker... datePickers) {
        if (CollectionsUtil.isFilled(datePickers))
            for (DatePicker dp : datePickers)
                if (dp != null)
                    dp.setValue(date);
    }

    public static void clearPanes(Pane... panes) {
        if (CollectionsUtil.isFilled(panes))
            for (Pane p : panes)
                p.getChildren().clear();
    }
}
