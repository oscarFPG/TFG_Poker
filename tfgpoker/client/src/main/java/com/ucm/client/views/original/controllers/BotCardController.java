package com.ucm.client.views.original.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class BotCardController {

    @FXML
    private Spinner<Integer> spinnerBot1;
    @FXML
    private StackPane cardInfoBot1;
    @FXML
    private VBox cardFrontBot1;
    @FXML
    private StackPane cardBackBot1;
    private boolean isFlippedBot1 = false;
}
