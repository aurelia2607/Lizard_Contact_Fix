package com.lizardcontact.controller;

import com.lizardcontact.model.Contact;
import com.lizardcontact.model.ContactManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Optional;

public class KontakController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterKategori;
    @FXML private ComboBox<String> filterTipe;
    @FXML private TableView<Contact> contactTable;
    @FXML private TableColumn<Contact, String> colNo;
    @FXML private TableColumn<Contact, String> colNama;
    @FXML private TableColumn<Contact, String> colTelepon;
    @FXML private TableColumn<Contact, String> colEmail;
    @FXML private TableColumn<Contact, String> colKategori;
    @FXML private TableColumn<Contact, String> colTipe;
    @FXML private TableColumn<Contact, Void> colAksi;
    @FXML private Label infoLabel;
    @FXML private Label favInfo;

    private ContactManager cm;

    private static final String BTN =
            "-fx-background-color:#d4d0c8;-fx-border-color:#ffffff #808080 #808080 #ffffff;" +
                    "-fx-border-width:2;-fx-font-size:10;-fx-cursor:hand;-fx-padding:2 8;";

    @FXML
    public void initialize() {
        cm = MainController.getContactManager();
        filterKategori.getItems().add("Semua");
        filterKategori.getItems().addAll(
                com.lizardcontact.model.ContactCategory.allDisplayNames());
        filterKategori.setValue("Semua");

        filterTipe.getItems().addAll("Semua", "Personal", "Bisnis");
        filterTipe.setValue("Semua");

        setupColumns();
        loadTable(cm.getContacts());
    }

    private void setupColumns() {
        colNo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || getIndex() < 0) ? null : String.valueOf(getIndex() + 1));
            }
        });

        colNo.setSortable(false);
        colNama.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        colNama.setSortable(true);
        colTelepon.setCellValueFactory(d -> new SimpleStringProperty(nvl(d.getValue().getPhoneNumber())));
        colTelepon.setSortable(true);
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(nvl(d.getValue().getEmail())));
        colEmail.setSortable(true);
        colKategori.setCellValueFactory(d -> new SimpleStringProperty(nvl(d.getValue().getCategoryName())));
        colKategori.setSortable(true);
        colTipe.setCellValueFactory(d -> new SimpleStringProperty(nvl(d.getValue().getContactType())));
        colTipe.setSortable(true);
        colNama.setSortType(TableColumn.SortType.ASCENDING);
        contactTable.getSortOrder().add(colNama);

        colAksi.setSortable(false);
        colAksi.setCellFactory(col -> new TableCell<>() {
            final Button editBtn = new Button("Edit");
            final Button delBtn  = new Button("Hapus");
            final Button favBtn  = new Button();
            final HBox box = new HBox(3, editBtn, delBtn, favBtn);
            {
                editBtn.setStyle(BTN);
                delBtn.setStyle(BTN);
                box.setAlignment(Pos.CENTER);
                editBtn.setOnAction(e -> openForm(getTableView().getItems().get(getIndex())));
                delBtn.setOnAction(e  -> konfirmasiHapus(getTableView().getItems().get(getIndex())));
                favBtn.setOnAction(e  -> {
                    cm.toggleFavorite(getTableView().getItems().get(getIndex()));
                    refreshTable();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }
                Contact c = getTableView().getItems().get(getIndex());
                favBtn.setText(c.isFavorite() ? "★" : "☆");
                favBtn.setStyle(BTN + (c.isFavorite()
                        ? "-fx-text-fill:black;-fx-font-size:13;"
                        : "-fx-text-fill:#888;-fx-font-size:13;"));
                setGraphic(box);
            }
        });
    }

    private String nvl(String s) { return s != null ? s : ""; }

    private void loadTable(List<Contact> list) {
        contactTable.getItems().setAll(list);
        contactTable.sort();
        long fav = cm.getContacts().stream().filter(Contact::isFavorite).count();
        infoLabel.setText("Menampilkan " + list.size() + " dari " + cm.getContacts().size() + " kontak");
        favInfo.setText("Favorit: " + fav);
    }

    @FXML private void doSearch() {
        loadTable(cm.search(searchField.getText().trim(),
                filterKategori.getValue(), filterTipe.getValue()));
    }

    @FXML private void resetSearch() {
        searchField.clear();
        filterKategori.setValue("Semua");
        filterTipe.setValue("Semua");
        loadTable(cm.getContacts());
    }

    @FXML private void tambahKontak() { openForm(null); }

    private void openForm(Contact c) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/lizardcontact/fxml/ContactForm.fxml"));
            Node form = loader.load();
            ContactFormController ctrl = loader.getController();
            if (c != null) ctrl.setContact(c);
            ctrl.setOnSaveCallback(this::refreshTable);
            StackPane content = (StackPane) contactTable.getScene().lookup("#contentArea");
            if (content != null) content.getChildren().setAll(form);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void konfirmasiHapus(Contact c) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Hapus Kontak");
        a.setHeaderText(null);
        a.setContentText("Hapus kontak \"" + c.getName() + "\"?");
        Optional<ButtonType> r = a.showAndWait();
        if (r.isPresent() && r.get() == ButtonType.OK) {
            cm.deleteContact(c);
            refreshTable();
        }
    }

    private void refreshTable() { loadTable(cm.getContacts()); }
}
