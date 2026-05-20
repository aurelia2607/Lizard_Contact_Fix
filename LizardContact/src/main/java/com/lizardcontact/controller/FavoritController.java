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
import java.util.stream.Collectors;

public class FavoritController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortBox;
    @FXML private TableView<Contact> favTable;
    @FXML private TableColumn<Contact, String> colNo;
    @FXML private TableColumn<Contact, String> colNama;
    @FXML private TableColumn<Contact, String> colTelepon;
    @FXML private TableColumn<Contact, String> colKategori;
    @FXML private TableColumn<Contact, String> colTipe;
    @FXML private TableColumn<Contact, Void>   colAksi;
    @FXML private Label infoLabel;
    @FXML private Label totalFavLabel;

    private ContactManager cm;

    private static final String BTN =
        "-fx-background-color:#d4d0c8;-fx-border-color:#ffffff #808080 #808080 #ffffff;" +
        "-fx-border-width:2;-fx-font-size:10;-fx-cursor:hand;-fx-padding:2 8;";

    @FXML
    public void initialize() {
        cm = MainController.getContactManager();
        sortBox.getItems().addAll("Nama A-Z","Nama Z-A","Kategori");
        sortBox.setValue("Nama A-Z");
        sortBox.setOnAction(e -> doSearch());
        setupColumns();
        loadTable(cm.getFavorites());
    }

    private void setupColumns() {
        colNo.setCellValueFactory(d ->
            new SimpleStringProperty(String.valueOf(favTable.getItems().indexOf(d.getValue())+1)));
        colNama.setCellValueFactory(d ->
            new SimpleStringProperty("★ " + d.getValue().getName()));
        colTelepon.setCellValueFactory(d ->
            new SimpleStringProperty(nvl(d.getValue().getPhoneNumber())));
        colKategori.setCellValueFactory(d ->
            new SimpleStringProperty(nvl(d.getValue().getCategory())));
        colTipe.setCellValueFactory(d ->
            new SimpleStringProperty(nvl(d.getValue().getContactType())));

        // Cuma Edit + Hapus (hapus dari favorit)
        colAksi.setCellFactory(col -> new TableCell<>() {
            final Button editBtn  = new Button("Edit");
            final Button hapusBtn = new Button("Hapus ★");
            final HBox box = new HBox(5, editBtn, hapusBtn);
            {
                editBtn.setStyle(BTN);
                hapusBtn.setStyle(BTN);
                box.setAlignment(Pos.CENTER);
                editBtn.setOnAction(e -> openEditForm(getTableView().getItems().get(getIndex())));
                hapusBtn.setOnAction(e -> {
                    Contact c = getTableView().getItems().get(getIndex());
                    cm.toggleFavorite(c); // unfav
                    doSearch();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private String nvl(String s) { return s != null ? s : ""; }

    private void loadTable(List<Contact> list) {
        String sort = sortBox.getValue();
        if ("Nama A-Z".equals(sort))
            list = list.stream().sorted((a,b)->a.getName().compareToIgnoreCase(b.getName())).collect(Collectors.toList());
        else if ("Nama Z-A".equals(sort))
            list = list.stream().sorted((a,b)->b.getName().compareToIgnoreCase(a.getName())).collect(Collectors.toList());
        else if ("Kategori".equals(sort))
            list = list.stream().sorted((a,b)->nvl(a.getCategory()).compareToIgnoreCase(nvl(b.getCategory()))).collect(Collectors.toList());

        favTable.getItems().setAll(list);
        int total = cm.getFavorites().size();
        infoLabel.setText("Menampilkan " + list.size() + " dari " + total + " favorit");
        totalFavLabel.setText("Total Favorit: " + total + " kontak");
    }

    @FXML private void doSearch() {
        String kw = searchField.getText().trim().toLowerCase();
        List<Contact> favs = cm.getFavorites();
        if (!kw.isEmpty())
            favs = favs.stream()
                .filter(c -> c.getName().toLowerCase().contains(kw)
                    || nvl(c.getPhoneNumber()).contains(kw))
                .collect(Collectors.toList());
        loadTable(favs);
    }

    @FXML private void resetSearch() { searchField.clear(); loadTable(cm.getFavorites()); }

    private void openEditForm(Contact c) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lizardcontact/fxml/ContactForm.fxml"));
            Node form = loader.load();
            ContactFormController ctrl = loader.getController();
            ctrl.setContact(c);
            ctrl.setOnSaveCallback(() -> loadTable(cm.getFavorites()));
            StackPane content = (StackPane) favTable.getScene().lookup("#contentArea");
            if (content != null) content.getChildren().setAll(form);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
