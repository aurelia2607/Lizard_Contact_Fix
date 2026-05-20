package com.lizardcontact.controller;

import com.lizardcontact.database.DatabaseHelper;
import com.lizardcontact.model.ContactStatistics;
import com.lizardcontact.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.util.Map;

public class StatistikController {

    @FXML private Label totalLabel, topLabel, avgLabel, bulanLabel, pvbLabel;
    @FXML private PieChart pieChart;
    @FXML private BarChart<String, Number> barChart;
    @FXML private TableView<Map.Entry<String, Integer>> rincianTable;
    @FXML private TableColumn<Map.Entry<String, Integer>, String> colKat, colJml, colPct;
    @FXML private TableColumn<Map.Entry<String, Integer>, Void> colBar;

    @FXML
    public void initialize() {
        int uid = SessionManager.getInstance().getCurrentUser().getUserID();
        ContactStatistics stats = DatabaseHelper.getInstance().getStatistics(uid);

        totalLabel.setText(String.valueOf(stats.getTotalContacts()));
        topLabel.setText(stats.getTopCategory() != null ? stats.getTopCategory() : "-");
        avgLabel.setText(String.format("%.1f", stats.getAveragePerCategory()));
        bulanLabel.setText(String.valueOf(stats.getNewThisMonth()));
        pvbLabel.setText(stats.getPersonalVsBusinessRatio());

        Map<String, Integer> dist = stats.getCategoryDistribution();

        pieChart.getData().clear();
        if (dist != null) dist.forEach((k,v) -> pieChart.getData().add(new PieChart.Data(k+" "+v, v)));
        pieChart.setLabelsVisible(true);

        barChart.getData().clear();
        XYChart.Series<String,Number> series = new XYChart.Series<>();
        series.setName("Jumlah");
        if (dist != null) dist.forEach((k,v) -> series.getData().add(new XYChart.Data<>(k,v)));
        barChart.getData().add(series);

        int total = stats.getTotalContacts();
        colKat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getKey()));
        colJml.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getValue())));
        colPct.setCellValueFactory(d -> {
            double pct = total == 0 ? 0 : (double)d.getValue().getValue()/total*100;
            return new SimpleStringProperty(String.format("%.1f%%", pct));
        });

        colBar.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null); return;
                }
                Map.Entry<String,Integer> e = getTableView().getItems().get(getIndex());
                double ratio = total == 0 ? 0 : (double)e.getValue() / total;

                // Total lebar container ikut kolom, bar navy isi ratio, sisa light gray
                double totalW = colBar.getWidth() - 12; // sedikit margin
                if (totalW < 10) totalW = 150;
                double navyW = ratio * totalW;
                double grayW = totalW - navyW;

                Region navyBar = new Region();
                navyBar.setPrefWidth(navyW);
                navyBar.setPrefHeight(12);
                navyBar.setMinWidth(navyW);
                navyBar.setStyle("-fx-background-color:#1a237e;"); // navy sesuai screenshot

                Region grayBar = new Region();
                grayBar.setPrefWidth(grayW);
                grayBar.setPrefHeight(12);
                grayBar.setMinWidth(grayW);
                grayBar.setStyle("-fx-background-color:#c8c8c8;");

                HBox container = new HBox(navyBar, grayBar);
                container.setStyle("-fx-alignment:CENTER_LEFT;");
                setGraphic(container);
            }
        });

        // Re-render bar when column width changes
        colBar.widthProperty().addListener((obs, o, n) -> rincianTable.refresh());

        if (dist != null) rincianTable.getItems().setAll(dist.entrySet());
    }
}
