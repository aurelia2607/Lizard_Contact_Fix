package com.lizardcontact.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class MainController {
    @FXML
    private StackPane contentArea;
    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnKontak;
    @FXML
    private Button btnFavorit;
    @FXML
    private Button btnStatistik;
    @FXML
    private Button btnRiwayat;

    private Button activeBtn;

    private static final String STYLE_NORMAL =
            "-fx-background-color:#d4d0c8;-fx-border-color:transparent;-fx-padding:6 12;-fx-font-size:12;-fx-cursor:hand;-fx-alignment:CENTER_LEFT;";
    private static final String STYLE_ACTIVE =
            "-fx-background-color:#000080;-fx-text-fill:white;-fx-border-color:transparent;-fx-padding:6 12;-fx-font-size:12;-fx-cursor:hand;-fx-alignment:CENTER_LEFT;-fx-font-weight:bold;";
}

