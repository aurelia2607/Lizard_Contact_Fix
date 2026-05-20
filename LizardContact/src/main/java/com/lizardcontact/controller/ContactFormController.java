package com.lizardcontact.controller;

import com.lizardcontact.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class ContactFormController {

    @FXML private Label formTitle;
    @FXML private RadioButton rbPersonal;
    @FXML private RadioButton rbBisnis;
    @FXML private ToggleGroup tipeGroup;

    // Common fields
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> kategoriBox;
    @FXML private CheckBox favCheck;

    // Personal fields
    @FXML private VBox personalSection;
    @FXML private TextField nicknameField;
    @FXML private DatePicker birthdatePicker;
    @FXML private ComboBox<String> relasiBox;

    // Business fields
    @FXML private VBox bisnisSection;
    @FXML private TextField companyField;
    @FXML private TextField jobTitleField;
    @FXML private TextField websiteField;

    @FXML private Label errorLabel;

    private Contact editContact;
    private Runnable onSaveCallback;

    @FXML
    public void initialize() {
        kategoriBox.getItems().addAll("Teman", "Keluarga", "Kolega", "Lainnya");
        kategoriBox.setValue("Teman");
        relasiBox.getItems().addAll("Ayah", "Ibu", "Saudara", "Pasangan", "Sahabat", "Teman", "Lainnya");
        relasiBox.setValue("Teman");

        // Toggle personal/bisnis sections
        tipeGroup.selectedToggleProperty().addListener((obs, old, nw) -> {
            boolean isPersonal = rbPersonal.isSelected();
            personalSection.setVisible(isPersonal);
            personalSection.setManaged(isPersonal);
            bisnisSection.setVisible(!isPersonal);
            bisnisSection.setManaged(!isPersonal);
        });
    }

    public void setContact(Contact c) {
        this.editContact = c;
        formTitle.setText("Edit Kontak");

        nameField.setText(c.getName());
        phoneField.setText(c.getPhoneNumber());
        emailField.setText(c.getEmail() != null ? c.getEmail() : "");
        addressField.setText(c.getAddress() != null ? c.getAddress() : "");
        kategoriBox.setValue(c.getCategory());
        favCheck.setSelected(c.isFavorite());

        if (c instanceof PersonalContact pc) {
            rbPersonal.setSelected(true);
            nicknameField.setText(pc.getNickname() != null ? pc.getNickname() : "");
            birthdatePicker.setValue(pc.getBirthdate());
            if (pc.getRelationship() != null) relasiBox.setValue(pc.getRelationship());
        } else if (c instanceof BusinessContact bc) {
            rbBisnis.setSelected(true);
            companyField.setText(bc.getCompany() != null ? bc.getCompany() : "");
            jobTitleField.setText(bc.getJobTitle() != null ? bc.getJobTitle() : "");
            websiteField.setText(bc.getWebsite() != null ? bc.getWebsite() : "");
        }
    }

    public void setOnSaveCallback(Runnable cb) {
        this.onSaveCallback = cb;
    }

    @FXML
    private void save() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty()) { errorLabel.setText("Nama wajib diisi!"); return; }
        if (phone.isEmpty()) { errorLabel.setText("Nomor telepon wajib diisi!"); return; }

        ContactManager cm = MainController.getContactManager();

        if (editContact != null) {
            // Update existing
            editContact.setName(name);
            editContact.setPhoneNumber(phone);
            editContact.setEmail(emailField.getText().trim());
            editContact.setAddress(addressField.getText().trim());
            editContact.setCategory(kategoriBox.getValue());
            editContact.setFavorite(favCheck.isSelected());

            if (editContact instanceof PersonalContact pc) {
                pc.setNickname(nicknameField.getText().trim());
                pc.setBirthdate(birthdatePicker.getValue());
                pc.setRelationship(relasiBox.getValue());
            } else if (editContact instanceof BusinessContact bc) {
                bc.setCompany(companyField.getText().trim());
                bc.setJobTitle(jobTitleField.getText().trim());
                bc.setWebsite(websiteField.getText().trim());
            }
            cm.updateContact(editContact);
        } else {
            // Create new
            Contact newContact;
            if (rbPersonal.isSelected()) {
                PersonalContact pc = new PersonalContact();
                pc.setNickname(nicknameField.getText().trim());
                pc.setBirthdate(birthdatePicker.getValue());
                pc.setRelationship(relasiBox.getValue());
                newContact = pc;
            } else {
                BusinessContact bc = new BusinessContact();
                bc.setCompany(companyField.getText().trim());
                bc.setJobTitle(jobTitleField.getText().trim());
                bc.setWebsite(websiteField.getText().trim());
                newContact = bc;
            }
            newContact.setName(name);
            newContact.setPhoneNumber(phone);
            newContact.setEmail(emailField.getText().trim());
            newContact.setAddress(addressField.getText().trim());
            newContact.setCategory(kategoriBox.getValue());
            newContact.setFavorite(favCheck.isSelected());
            cm.addContact(newContact);
        }

        if (onSaveCallback != null) onSaveCallback.run();
        goBack();
    }

    @FXML
    private void cancel() {
        goBack();
    }

    private void goBack() {
        try {
            StackPane content = (StackPane) nameField.getScene().lookup("#contentArea");
            if (content != null) {
                java.io.InputStream is = getClass().getResourceAsStream("/com/lizardcontact/fxml/Kontak.fxml");
                if (is != null) {
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                            getClass().getResource("/com/lizardcontact/fxml/Kontak.fxml"));
                    javafx.scene.Node page = loader.load();
                    content.getChildren().setAll(page);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
