package com.lizardcontact;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.setTitle("Lizard Contact");
        stage.setResizable(true);
        showLogin();
        stage.show();
    }

    public static void showLogin() throws Exception {
        Parent root = FXMLLoader.load(MainApp.class.getResource("/com/lizardcontact/fxml/Login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(430);
        primaryStage.setHeight(360);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
    }

    public static void showMain() throws Exception {
        Parent root = FXMLLoader.load(MainApp.class.getResource("/com/lizardcontact/fxml/Main.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(920);
        primaryStage.setHeight(660);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
    }

    public static Stage getPrimaryStage() { return primaryStage; }

    public static void main(String[] args) { launch(args); }
}
