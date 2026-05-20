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
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmField;
    @FXML
    private Label errorLabel;

    @FXML
    private void register() {
        String username = this.usernameField.getText().trim();
        String email = this.emailField.getText().trim();
        String password = this.passwordField.getText();
        String confirm = this.confirmField.getText();
        if (!username.isEmpty() && !password.isEmpty()) {
            if (username.length() < 3) {
                this.errorLabel.setText("Username minimal 3 karakter.");
            } else if (!password.equals(confirm)) {
                this.errorLabel.setText("Password tidak cocok!");
            } else if (password.length() < 6) {
                this.errorLabel.setText("Password minimal 6 karakter.");
            } else {
                boolean ok = DatabaseHelper.getInstance().register(username, password, email);
                if (ok) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Registrasi Berhasil");
                    alert.setHeaderText((String)null);
                    alert.setContentText("Akun berhasil dibuat! Silakan login.");
                    alert.showAndWait();
                    this.backToLogin();
                } else {
                    this.errorLabel.setText("Username sudah digunakan, coba yang lain.");
                }

            }
        } else {
            this.errorLabel.setText("Username dan password wajib diisi!");
        }
    }

    @FXML
    private void backToLogin() {
        try {
            Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("/com/lizardcontact/fxml/Login.fxml"));
            Stage stage = MainApp.getPrimaryStage();
            stage.setScene(new Scene(root));
            stage.setWidth((double)400.0F);
            stage.setHeight((double)400.0F);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
