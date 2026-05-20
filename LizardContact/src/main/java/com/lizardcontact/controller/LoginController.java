package com.lizardcontact.controller;

import com.lizardcontact.MainApp;
import com.lizardcontact.database.DatabaseHelper;
import com.lizardcontact.model.User;
import com.lizardcontact.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisible;
    @FXML private Button toggleBtn;
    @FXML private Label errorLabel;

    private boolean showingPassword = false;

    @FXML
    public void initialize() {
        passwordVisible.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    private void togglePassword() {
        showingPassword = !showingPassword;
        passwordField.setVisible(!showingPassword);
        passwordField.setManaged(!showingPassword);
        passwordVisible.setVisible(showingPassword);
        passwordVisible.setManaged(showingPassword);
        toggleBtn.setText(showingPassword ? "Sembunyikan" : "Lihat");
    }

    @FXML
    private void login() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username dan password tidak boleh kosong!");
            return;
        }

        User user = DatabaseHelper.getInstance().login(username, password);
        if (user != null) {
            SessionManager.getInstance().setCurrentUser(user);
            try {
                MainApp.showMain();
            } catch (Exception e) {
                e.printStackTrace();
                // Tampilkan pesan error yang lebih detail
                String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                if (e.getCause() != null && e.getCause().getMessage() != null)
                    msg = e.getCause().getMessage();
                errorLabel.setText("Error: " + msg);
            }
        } else {
            errorLabel.setText("Username atau password salah!");
            passwordField.clear();
            passwordVisible.clear();
        }
    }

    @FXML
    private void showRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/lizardcontact/fxml/Register.fxml"));
            Stage stage = MainApp.getPrimaryStage();
            stage.setScene(new Scene(root));
            stage.setWidth(430);
            stage.setHeight(430);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error: " + e.getMessage());
        }
    }
}
