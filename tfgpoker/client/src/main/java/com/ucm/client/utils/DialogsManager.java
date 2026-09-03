
package com.ucm.client.utils;

import javafx.application.Platform;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
/**
 * class that manages the dialogs of the application
 */
public abstract class DialogsManager {
    /**
     * Main stage of the application. It is used to set the owner of the dialogs.
     */
    protected static Stage stage;
    /**
     * Path to the CSS file used in the dialogs.
     */
    protected static final String CSS_PATH = Messages.GLOBAL_CSS_PATH;
    /**
     * Initializes the DialogsManager with the main stage of the application.
     * @param s
     */
    public static void init(Stage s) {
        stage = s;
    }
    /**
     * Applies the CSS file to the given root region.
     * If the CSS file cannot be loaded, an error message is printed to the console.
     * @param root
     */
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
    /**
     * Executes the given task on the JavaFX Application Thread.
     * If the current thread is the JavaFX Application Thread, the task is executed immediately.
     * @param task
     */
    protected static void runSafe(Runnable task) {
        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            Platform.runLater(task);
        }
    }
}