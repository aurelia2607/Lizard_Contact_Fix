package com.lizardcontact.controller;

import com.lizardcontact.database.DatabaseHelper;
import com.lizardcontact.model.ActivityLog;
import com.lizardcontact.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class RiwayatController {

    @FXML private ComboBox<String> filterAksi;
    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;
    @FXML private TableView<ActivityLog> logTable;
    @FXML private TableColumn<ActivityLog, String> colNo;
    @FXML private TableColumn<ActivityLog, String> colWaktu;
    @FXML private TableColumn<ActivityLog, Void>   colAksi;
    @FXML private TableColumn<ActivityLog, String> colNama;
    @FXML private TableColumn<ActivityLog, String> colKet;
    @FXML private Label totalLogLabel;
    @FXML private Label summaryLabel;

    private final DatabaseHelper db = DatabaseHelper.getInstance();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        filterAksi.getItems().addAll("Semua","TAMBAH","EDIT","HAPUS","FAVORIT");
        filterAksi.setValue("Semua");
        setupColumns();
        loadLogs(null, null, null);
    }

    private void setupColumns() {
        colNo.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(logTable.getItems().indexOf(d.getValue())+1)));

        colWaktu.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getTimestamp() != null ? d.getValue().getTimestamp().format(dtf) : "-"));

        colAksi.setCellFactory(col -> new TableCell<>() {
            final Label lbl = new Label();
            final HBox box  = new HBox(lbl);
            { box.setAlignment(Pos.CENTER); }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null); return;
                }
                ActivityLog log = getTableView().getItems().get(getIndex());
                String action = log.getAction();
                lbl.setText("[" + action + "]");

                String bg;
                switch (action) {
                    case "TAMBAH"  -> bg = "#4a7c4e";
                    case "EDIT"    -> bg = "#1a237e";
                    case "FAVORIT" -> bg = "#827717";
                    case "HAPUS"   -> bg = "#7b1a1a";
                    default        -> bg = "#555555";
                }
                lbl.setStyle(
                        "-fx-background-color:" + bg + ";" +
                                "-fx-text-fill:white;" +
                                "-fx-font-size:10;" +
                                "-fx-font-weight:bold;" +
                                "-fx-padding:2 5 2 5;" +
                                "-fx-background-radius:2;"
                );
                setGraphic(box);
            }
        });

        colNama.setCellValueFactory(d -> new SimpleStringProperty(nvl(d.getValue().getContactName())));
        colKet.setCellValueFactory(d  -> new SimpleStringProperty(nvl(d.getValue().getDescription())));
    }

    private String nvl(String s) { return s != null ? s : ""; }

    private void loadLogs(String action, String from, String to) {
        int uid = SessionManager.getInstance().getCurrentUser().getUserID();
        List<ActivityLog> logs = db.getLogs(uid, action, from, to);
        logTable.getItems().setAll(logs);

        long tambah  = logs.stream().filter(l -> "TAMBAH".equals(l.getAction())).count();
        long edit    = logs.stream().filter(l -> "EDIT".equals(l.getAction())).count();
        long hapus   = logs.stream().filter(l -> "HAPUS".equals(l.getAction())).count();
        long favorit = logs.stream().filter(l -> "FAVORIT".equals(l.getAction())).count();

        totalLogLabel.setText("Total log: " + logs.size());
        summaryLabel.setText("Tambah: "+tambah+" | Edit: "+edit+" | Hapus: "+hapus+" | Favorit: "+favorit);
    }

    @FXML private void doFilter() {
        String action = "Semua".equals(filterAksi.getValue()) ? null : filterAksi.getValue();
        String from = fromDate.getValue() != null ? fromDate.getValue().toString() : null;
        String to   = toDate.getValue()   != null ? toDate.getValue().toString()   : null;
        loadLogs(action, from, to);
    }

    @FXML private void resetFilter() {
        filterAksi.setValue("Semua");
        fromDate.setValue(null);
        toDate.setValue(null);
        loadLogs(null, null, null);
    }

    @FXML private void hapusLog() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Hapus Log"); a.setHeaderText(null);
        a.setContentText("Hapus semua riwayat aktivitas?");
        Optional<ButtonType> r = a.showAndWait();
        if (r.isPresent() && r.get() == ButtonType.OK) {
            db.clearAllLogs(SessionManager.getInstance().getCurrentUser().getUserID());
            loadLogs(null, null, null);
        }
    }
}
