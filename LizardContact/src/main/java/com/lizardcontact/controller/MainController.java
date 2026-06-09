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

    @FXML private StackPane contentArea;
    @FXML private Button btnDashboard;
    @FXML private Button btnKontak;
    @FXML private Button btnFavorit;
    @FXML private Button btnStatistik;
    @FXML private Button btnRiwayat;

    private static ContactManager contactManager = new ContactManager();
    private static MainController instance;

    private Button activeBtn;

    private static final String STYLE_NORMAL =
            "-fx-background-color:#d4d0c8;-fx-border-color:transparent;" +
                    "-fx-padding:6 12;-fx-font-size:12;-fx-cursor:hand;-fx-alignment:CENTER_LEFT;";
    private static final String STYLE_ACTIVE =
            "-fx-background-color:#000080;-fx-text-fill:white;-fx-border-color:transparent;" +
                    "-fx-padding:6 12;-fx-font-size:12;-fx-cursor:hand;-fx-alignment:CENTER_LEFT;-fx-font-weight:bold;";

    @FXML
    public void initialize() {
        instance = this;
        contactManager = new ContactManager();
        contactManager.loadContacts();
        showDashboard();
    }

    private void setActive(Button btn) {
        List.of(btnDashboard, btnKontak, btnFavorit, btnStatistik, btnRiwayat)
                .forEach(b -> b.setStyle(STYLE_NORMAL));
        btn.setStyle(STYLE_ACTIVE);
        this.activeBtn = btn;
    }

    @FXML public void showDashboard()  { loadPage("/com/lizardcontact/fxml/Dashboard.fxml");  setActive(btnDashboard);  }
    @FXML public void showKontak()     { loadPage("/com/lizardcontact/fxml/Kontak.fxml");      setActive(btnKontak);     }
    @FXML public void showFavorit()    { loadPage("/com/lizardcontact/fxml/Favorit.fxml");     setActive(btnFavorit);    }
    @FXML public void showStatistik()  { loadPage("/com/lizardcontact/fxml/Statistik.fxml");   setActive(btnStatistik);  }
    @FXML public void showRiwayat()    { loadPage("/com/lizardcontact/fxml/Riwayat.fxml");     setActive(btnRiwayat);    }

    private void loadPage(String fxml) {
        try {
            Node page = FXMLLoader.load(getClass().getResource(fxml));
            contentArea.getChildren().setAll(page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Keluar");
        alert.setHeaderText(null);
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

    public static MainController getInstance() {
        return instance;
    }
}
