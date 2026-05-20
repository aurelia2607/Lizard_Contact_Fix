package com.lizardcontact.controller;

import com.lizardcontact.database.DatabaseHelper;
import com.lizardcontact.model.Contact;
import com.lizardcontact.model.ContactStatistics;
import com.lizardcontact.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

import java.util.Map;

public class DashboardController {

    @FXML private Label greetingLabel;
    @FXML private PieChart pieChart;
    @FXML private ListView<String> legendList;

    @FXML
    public void initialize() {
        String username = SessionManager.getInstance().getCurrentUser().getUsername();
        greetingLabel.setText("Selamat datang, " + cap(username) + "!");

        int uid = SessionManager.getInstance().getCurrentUser().getUserID();
        ContactStatistics stats = DatabaseHelper.getInstance().getStatistics(uid);

        pieChart.getData().clear();
        Map<String, Integer> dist = stats.getCategoryDistribution();
        int total = stats.getTotalContacts();

        legendList.getItems().clear();
        if (dist != null && total > 0) {
            for (Map.Entry<String, Integer> e : dist.entrySet()) {
                pieChart.getData().add(new PieChart.Data(e.getKey(), e.getValue()));
                double pct = (double) e.getValue() / total * 100;
                legendList.getItems().add(String.format("■ %-10s %d (%.1f%%)", e.getKey(), e.getValue(), pct));
            }
        }
        pieChart.setLegendVisible(false);
        pieChart.setLabelsVisible(false);
    }

    @FXML
    private void tambahKontak() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lizardcontact/fxml/ContactForm.fxml"));
            Node form = loader.load();
            ContactFormController ctrl = loader.getController();
            ctrl.setOnSaveCallback(() -> {});
            StackPane content = (StackPane) greetingLabel.getScene().lookup("#contentArea");
            if (content != null) content.getChildren().setAll(form);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String cap(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
