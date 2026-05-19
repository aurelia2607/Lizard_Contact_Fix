package com.lizardcontact.model;

import com.lizardcontact.database.DatabaseHelper;
import com.lizardcontact.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ContactManager {
    private List<Contact> contacts = new ArrayList<>();
    private final DatabaseHelper db = DatabaseHelper.getInstance();

    public void loadContacts(){
        int uid = SessionManager.getInstance().getCurrentUser().getUserID();
        contacts = db.getAllContacts(uid);
    }

    public List<Contact> getContacts() { return contacts; }

    public List<Contact> search(String keyword, String category, String type) {
        return contacts.stream()
                .filter(c -> {
                    boolean matchKw = keyword == null || keyword.isEmpty()
                            || c.getName().toLowerCase().contains(keyword.toLowerCase())
                            || (c.getPhoneNumber() != null && c.getPhoneNumber().contains(keyword))
                            || (c.getEmail() != null && c.getEmail().toLowerCase().contains(keyword.toLowerCase()));
                    boolean matchCat = category == null || category.equals("Semua") || category.equals(c.getCategory());
                    boolean matchType = type == null || type.equals("Semua") || type.equals(c.getContactType());
                    return matchKw && matchCat && matchType;
                })
                .collect(Collectors.toList());
    }

    public List<Contact> getFavorites() {
        return contacts.stream().filter(Contact::isFavorite).collect(Collectors.toList());
    }

    public void addContact(Contact c) {
        int uid = SessionManager.getInstance().getCurrentUser().getUserID();
        int id = db.saveContact(c, uid);
        c.setContactID(id);
        c.setUserID(uid);
        contacts.add(c);
        db.addLog(uid, "TAMBAH", c.getName(), "Kontak " + c.getContactType().toLowerCase() + " baru ditambahkan");
    }

    public void updateContact(Contact c) {
        db.updateContact(c);
        int uid = SessionManager.getInstance().getCurrentUser().getUserID();
        db.addLog(uid, "EDIT", c.getName(), "Data kontak diperbarui");
    }

    public void deleteContact(Contact c) {
        db.deleteContact(c.getContactID());
        contacts.remove(c);
        int uid = SessionManager.getInstance().getCurrentUser().getUserID();
        db.addLog(uid, "HAPUS", c.getName(), "Kontak dihapus dari sistem");
    }

    public void toggleFavorite(Contact c) {
        boolean newFav = !c.isFavorite();
        c.setFavorite(newFav);
        db.toggleFavorite(c.getContactID(), newFav);
        int uid = SessionManager.getInstance().getCurrentUser().getUserID();
        db.addLog(uid, "FAVORIT", c.getName(), newFav ? "Ditandai sebagai favorit" : "Dihapus dari favorit");
    }
}
