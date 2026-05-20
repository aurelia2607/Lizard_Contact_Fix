//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lizardcontact.controller;

import com.lizardcontact.MainApp;
import com.lizardcontact.model.ContactManager;
import com.lizardcontact.util.SessionManager;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
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
    private static final String STYLE_NORMAL = "-fx-background-color:#d4d0c8;-fx-border-color:transparent;-fx-padding:6 12;-fx-font-size:12;-fx-cursor:hand;-fx-alignment:CENTER_LEFT;";
    private static final String STYLE_ACTIVE = "-fx-background-color:#000080;-fx-text-fill:white;-fx-border-color:transparent;-fx-padding:6 12;-fx-font-size:12;-fx-cursor:hand;-fx-alignment:CENTER_LEFT;-fx-font-weight:bold;";

    @FXML
    public void initialize() {
        contactManager.loadContacts();
        this.showDashboard();
    }

    private void setActive(Button btn) {
        List.of(this.btnDashboard, this.btnKontak, this.btnFavorit, this.btnStatistik, this.btnRiwayat).forEach((b) -> b.setStyle("-fx-background-color:#d4d0c8;-fx-border-color:transparent;-fx-padding:6 12;-fx-font-size:12;-fx-cursor:hand;-fx-alignment:CENTER_LEFT;"));
        btn.setStyle("-fx-background-color:#000080;-fx-text-fill:white;-fx-border-color:transparent;-fx-padding:6 12;-fx-font-size:12;-fx-cursor:hand;-fx-alignment:CENTER_LEFT;-fx-font-weight:bold;");
        this.activeBtn = btn;
    }

    @FXML
    public void showDashboard() {
        this.loadPage("/com/lizardcontact/fxml/Dashboard.fxml");
        this.setActive(this.btnDashboard);
    }

    @FXML
    public void showRiwayat() {
        this.loadPage("/com/lizardcontact/fxml/Riwayat.fxml");
        this.setActive(this.btnRiwayat);
    }

    private void loadPage(String fxml) {
        try {
            Node page = (Node)FXMLLoader.load(this.getClass().getResource(fxml));
            this.contentArea.getChildren().setAll(new Node[]{page});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Keluar");
        alert.setHeaderText((String)null);
        alert.setContentText("Yakin ingin keluar?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            SessionManager.getInstance().logout();

            try {
                MainApp.showLogin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static ContactManager getContactManager() {
        return contactManager;
    }
}