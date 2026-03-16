package com.ucm.client.views.template;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class AppTemplateCopy extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("templateStyleCopy.fxml"));
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("templateStyleCopy.css").toExternalForm());

        stage.setTitle("Test");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
