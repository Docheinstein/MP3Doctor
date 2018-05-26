package org.docheinstein.mp3doctor.ui.commons.controller.base;

import javafx.stage.Stage;

/** Represents an entity that is aware and thus provide a {@link Stage}. */
public interface StageAware {

    /**
     * Returns the stage this entity is aware of.
     * @return the stage
     */
    Stage getStage();
}
