package com.lizardcontact.controller;

import com.lizardcontact.model.ContactManager;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

    public static ContactManager contactManager = new ContactManager();
    private Button activeBtn;

    private static final String STYLE_NORMAL =
            "-fx-background-color:#d4d0c8;-fx-border-color:transparent;-fx-padding:6 12;-fx-font-size:12;-fx-cursor:hand;-fx-alignment:CENTER_LEFT;";
    private static final String STYLE_ACTIVE =
            "-fx-background-color:#000080;-fx-text-fill:white;-fx-border-color:transparent;-fx-padding:6 12;-fx-font-size:12;-fx-cursor:hand;-fx-alignment:CENTER_LEFT;-fx-font-weight:bold;";

    @FXML
    public void initialize() {
        contactManager.loadContacts();
        showDashboard();
    }

    private void setActive(Button btn) {
        List.of(btnDashboard, btnKontak, btnFavorit, btnStatistik, btnRiwayat)
                .forEach(b -> b.setStyle(STYLE_NORMAL));
        btn.setStyle(STYLE_ACTIVE);
        activeBtn = btn;
    }

    private void loadPage(String fxml) {
        try {
            Node page = FXMLLoader.load(getClass().getResource(fxml));
            contentArea.getChildren().setAll(page);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static ContactManager getContactManager() {
        return contactManager;
    }
}
