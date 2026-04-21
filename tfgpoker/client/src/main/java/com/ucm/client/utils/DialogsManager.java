
package com.ucm.client.utils;

import javafx.application.Platform;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public abstract class DialogsManager {
    protected static Stage stage;
    protected static final String CSS_PATH = Messages.GLOBAL_CSS_PATH;

    public static void init(Stage s) {
        stage = s;
    }

   
    protected static void applyCSS(Region root) {
        try {
            var resource = DialogsManager.class.getResource(CSS_PATH);
            if (resource != null) {
                root.getStylesheets().add(resource.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Error cargando CSS: " + CSS_PATH);
        }
    }

   
    protected static void runSafe(Runnable task) {
        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            Platform.runLater(task);
        }
    }
}