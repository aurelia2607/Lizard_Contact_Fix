package com.lizardcontact.controller;

import com.lizardcontact.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class ContactFormController {

    @FXML private Label formTitle;
    @FXML private RadioButton rbPersonal;
    @FXML private RadioButton rbBisnis;
    @FXML private ToggleGroup tipeGroup;

    @FXML private TextField nameField;
    @FXML private ComboBox<String> kodeNegaraBox;
    @FXML private RadioButton rbHP;
    @FXML private RadioButton rbLokal;
    @FXML private ToggleGroup phoneTypeGroup;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> kategoriBox;
    @FXML private CheckBox favCheck;
    @FXML private VBox personalSection;
    @FXML private TextField nicknameField;
    @FXML private DatePicker birthdatePicker;
    @FXML private ComboBox<String> relasiBox;
    @FXML private VBox bisnisSection;
    @FXML private TextField companyField;
    @FXML private TextField jobTitleField;
    @FXML private TextField websiteField;

    @FXML private Label errorLabel;

    private Contact editContact;
    private Runnable onSaveCallback;

    @FXML
    public void initialize() {
        kategoriBox.getItems().addAll(ContactCategory.allDisplayNames());
        kategoriBox.setValue(ContactCategory.TEMAN.getDisplayName());

        relasiBox.getItems().addAll("Ayah", "Ibu", "Saudara", "Pasangan", "Sahabat", "Teman", "Lainnya");
        relasiBox.setValue("Teman");

        kodeNegaraBox.getItems().addAll(
                "+62 (Indonesia)", "+1 (USA)", "+60 (Malaysia)", "+65 (Singapura)",
                "+81 (Jepang)", "+82 (Korea)", "+86 (Tiongkok)", "+91 (India)",
                "+44 (Inggris)", "+61 (Australia)"
        );
        kodeNegaraBox.setValue("+62 (Indonesia)");

        phoneTypeGroup.selectedToggleProperty().addListener((obs, old, nw) -> {
            phoneField.setPromptText(rbHP.isSelected() ? "81234567890" : "211234567");
            phoneField.clear();
        });

        tipeGroup.selectedToggleProperty().addListener((obs, old, nw) -> {
            boolean isPersonal = rbPersonal.isSelected();
            personalSection.setVisible(isPersonal);
            personalSection.setManaged(isPersonal);
            bisnisSection.setVisible(!isPersonal);
            bisnisSection.setManaged(!isPersonal);
        });
    }

    private String getKodeNegara() {
        String selected = kodeNegaraBox.getValue();
        if (selected == null) return "+62";
        return selected.split(" ")[0];
    }

    public void setContact(Contact c) {
        this.editContact = c;
        formTitle.setText("Edit Kontak");
        rbPersonal.setDisable(true);
        rbBisnis.setDisable(true);

        nameField.setText(c.getName());

        String fullPhone = c.getPhoneNumber() != null ? c.getPhoneNumber() : "";
        if (fullPhone.startsWith("+")) {
            int spaceIdx = fullPhone.indexOf(' ');
            if (spaceIdx > 0) {
                String kode  = fullPhone.substring(0, spaceIdx);
                String nomor = fullPhone.substring(spaceIdx + 1);
                for (String item : kodeNegaraBox.getItems()) {
                    if (item.startsWith(kode + " ")) { kodeNegaraBox.setValue(item); break; }
                }
                rbHP.setSelected(nomor.startsWith("8"));
                rbLokal.setSelected(!rbHP.isSelected());
                phoneField.setText(nomor);
            } else {
                phoneField.setText(fullPhone);
            }
        } else {
            phoneField.setText(fullPhone);
        }

        emailField.setText(c.getEmail() != null ? c.getEmail() : "");
        addressField.setText(c.getAddress() != null ? c.getAddress() : "");
        kategoriBox.setValue(c.getCategoryName());
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

    public void setOnSaveCallback(Runnable cb) { this.onSaveCallback = cb; }

    @FXML
    private void save() {
        String name       = nameField.getText().trim();
        String phoneInput = phoneField.getText().trim();
        String kodeNegara = getKodeNegara();
        boolean isHP      = rbHP.isSelected();

        if (name.isEmpty())       { errorLabel.setText("Nama wajib diisi!"); return; }
        if (phoneInput.isEmpty()) { errorLabel.setText("Nomor telepon wajib diisi!"); return; }
        if (!phoneInput.matches("\\d+")) {
            errorLabel.setText("Nomor telepon hanya boleh berisi angka!"); return;
        }
        if (isHP) {
            if (phoneInput.length() < 9 || phoneInput.length() > 12) {
                errorLabel.setText("Nomor HP harus 9-12 digit tanpa 0 di depan! (contoh: 81234567890)"); return;
            }
            if (!phoneInput.startsWith("8")) {
                errorLabel.setText("Nomor HP harus diawali angka 8!"); return;
            }
        } else {
            if (phoneInput.length() < 6 || phoneInput.length() > 11) {
                errorLabel.setText("Nomor lokal harus 6-11 digit!"); return;
            }
        }

        String phone = kodeNegara + " " + phoneInput;
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            errorLabel.setText("Format email tidak valid!"); return;
        }

        ContactManager cm = MainController.getContactManager();
        int excludeID = (editContact != null) ? editContact.getContactID() : -1;
        if (cm.isPhoneDuplicate(phone, excludeID)) {
            Optional<Contact> existing = cm.findByPhone(phone);
            String namaExisting = existing.map(Contact::getName).orElse("kontak lain");
            errorLabel.setText("Nomor ini sudah dipakai oleh \"" + namaExisting + "\"!");
            return;
        }
        ContactCategory kategori = ContactCategory.fromString(kategoriBox.getValue());

        if (editContact != null) {
            editContact.setName(name);
            editContact.setPhoneNumber(phone);
            editContact.setEmail(email);
            editContact.setAddress(addressField.getText().trim());
            editContact.setCategory(kategori);
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
            newContact.setEmail(email);
            newContact.setAddress(addressField.getText().trim());
            newContact.setCategory(kategori);
            newContact.setFavorite(favCheck.isSelected());
            cm.addContact(newContact);
        }

        if (onSaveCallback != null) onSaveCallback.run();
        goBack();
    }

    @FXML private void cancel() { goBack(); }

    private void goBack() {
        try {
            StackPane content = (StackPane) nameField.getScene().lookup("#contentArea");
            if (content != null) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/lizardcontact/fxml/Kontak.fxml"));
                Node page = loader.load();
                content.getChildren().setAll(page);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
