package com.lizardcontact.controller;

import com.lizardcontact.MainApp;
import com.lizardcontact.database.DatabaseHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private Label errorLabel;

    private static final String EMAIL_REGEX = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

    @FXML
    private void register() {
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username dan password wajib diisi!");
            return;
        }
        if (username.length() < 3) {
            errorLabel.setText("Username minimal 3 karakter.");
            return;
        }
        if (!email.isEmpty() && !email.matches(EMAIL_REGEX)) {
            errorLabel.setText("Format email tidak valid! (contoh: nama@email.com)");
            return;
        }
        if (!password.equals(confirm)) {
            errorLabel.setText("Password tidak cocok!");
            return;
        }
        if (password.length() < 6) {
            errorLabel.setText("Password minimal 6 karakter.");
            return;
        }

        boolean ok = DatabaseHelper.getInstance().register(username, password, email);
        if (ok) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Registrasi Berhasil");
            alert.setHeaderText(null);
            alert.setContentText("Akun berhasil dibuat! Silakan login.");
            alert.showAndWait();
            backToLogin();
        } else {
            errorLabel.setText("Username sudah digunakan, coba yang lain.");
        }
    }

    @FXML
    private void backToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/lizardcontact/fxml/Login.fxml"));
            Stage stage = MainApp.getPrimaryStage();
            stage.setScene(new Scene(root));
            stage.setWidth(400);
            stage.setHeight(400);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
