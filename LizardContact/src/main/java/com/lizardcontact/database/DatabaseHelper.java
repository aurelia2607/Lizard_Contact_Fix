package com.lizardcontact.database;

import com.lizardcontact.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.lizardcontact.model.ContactFactory;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:contacts.db";
    private static DatabaseHelper instance;
    private Connection connection;

    private DatabaseHelper() {
        connect();
        createTables();
        insertSampleData();
    }

    public static DatabaseHelper getInstance() {
        if (instance == null) instance = new DatabaseHelper();
        return instance;
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() { return connection; }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    userID INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    passwordHash TEXT NOT NULL,
                    email TEXT
                )""");
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS contacts (
                    contactID INTEGER PRIMARY KEY AUTOINCREMENT,
                    userID INTEGER,
                    contactType TEXT NOT NULL,
                    name TEXT NOT NULL,
                    phoneNumber TEXT,
                    email TEXT,
                    address TEXT,
                    category TEXT,
                    favorite INTEGER DEFAULT 0,
                    createdAt TEXT,
                    FOREIGN KEY(userID) REFERENCES users(userID)
                )""");
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS personalContactDetails (
                    detailID INTEGER PRIMARY KEY AUTOINCREMENT,
                    contactID INTEGER UNIQUE,
                    nickname TEXT,
                    birthdate TEXT,
                    relationship TEXT,
                    FOREIGN KEY(contactID) REFERENCES contacts(contactID)
                )""");
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS businessContactDetails (
                    detailID INTEGER PRIMARY KEY AUTOINCREMENT,
                    contactID INTEGER UNIQUE,
                    company TEXT,
                    jobTitle TEXT,
                    website TEXT,
                    FOREIGN KEY(contactID) REFERENCES contacts(contactID)
                )""");
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS activity_logs (
                    logID INTEGER PRIMARY KEY AUTOINCREMENT,
                    userID INTEGER,
                    action TEXT,
                    contactName TEXT,
                    description TEXT,
                    timestamp TEXT,
                    FOREIGN KEY(userID) REFERENCES users(userID)
                )""");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertSampleData() {
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) > 0) return;

            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (username, passwordHash, email) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, "samuel");
            ps.setString(2, hashPassword("samuel123"));
            ps.setString(3, "samuel@email.com");
            ps.executeUpdate();
            ResultSet genKeys = ps.getGeneratedKeys();
            int uid = genKeys.next() ? genKeys.getInt(1) : 1;

            String[][] contacts = {
                    {"Personal","Budi Santoso","081234567890","budi@email.com","Jl. Mawar 1","Teman","0"},
                    {"Bisnis","PT. Maju Bersama","0211234567","info@maju.co.id","Jl. Sudirman 10","Kolega","0"},
                    {"Personal","Siti Rahayu","085678901234","siti@gmail.com","Jl. Kenanga 5","Keluarga","1"},
                    {"Bisnis","CV. Karya Mandiri","0311234568","cv@karya.com","Jl. Diponegoro 3","Kolega","0"},
                    {"Personal","Rudi Hermawan","087890123456","rudi@yahoo.com","Jl. Anggrek 7","Teman","1"},
                    {"Personal","Rina Wijaya","082345678901","rina@outlook.com","Jl. Melati 2","Lainnya","0"},
                    {"Personal","Agus Santoso","081122334455","agus@gmail.com","Jl. Cempaka 9","Keluarga","1"},
                    {"Bisnis","PT. Sejahtera","0212345678","info@sejahtera.co.id","Jl. Thamrin 15","Kolega","1"},
                    {"Personal","Dewi Rahayu","089987654321","dewi@email.com","Jl. Flamboyan 4","Teman","0"},
                    {"Personal","Hendra Kusuma","082233445566","hendra@gmail.com","Jl. Dahlia 8","Keluarga","0"},
                    {"Bisnis","CV. Maju Jaya","0314567890","cv@majujaya.com","Jl. Veteran 6","Kolega","0"},
                    {"Personal","Lina Susanti","081567890123","lina@email.com","Jl. Marigold 11","Teman","1"},
            };

            for (String[] c : contacts) {
                PreparedStatement cps = connection.prepareStatement(
                        "INSERT INTO contacts (userID,contactType,name,phoneNumber,email,address,category,favorite,createdAt) VALUES (?,?,?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                cps.setInt(1, uid); cps.setString(2, c[0]); cps.setString(3, c[1]);
                cps.setString(4, c[2]); cps.setString(5, c[3]); cps.setString(6, c[4]);
                cps.setString(7, c[5]); cps.setInt(8, Integer.parseInt(c[6]));
                cps.setString(9, LocalDateTime.now().minusDays((long)(Math.random()*30)).toString());
                cps.executeUpdate();
                ResultSet cKeys = cps.getGeneratedKeys();
                int cid = cKeys.next() ? cKeys.getInt(1) : -1;

                if (c[0].equals("Personal")) {
                    PreparedStatement pps = connection.prepareStatement(
                            "INSERT INTO personalContactDetails (contactID,nickname,birthdate,relationship) VALUES (?,?,?,?)");
                    pps.setInt(1, cid); pps.setString(2, c[1].split(" ")[0]);
                    pps.setString(3, "1990-01-01"); pps.setString(4, "Sahabat");
                    pps.executeUpdate();
                } else {
                    PreparedStatement bps = connection.prepareStatement(
                            "INSERT INTO businessContactDetails (contactID,company,jobTitle,website) VALUES (?,?,?,?)");
                    bps.setInt(1, cid); bps.setString(2, c[1]);
                    bps.setString(3, "Manager"); bps.setString(4, "www.example.com");
                    bps.executeUpdate();
                }
            }

            String[][] logs = {
                    {"TAMBAH","Rina Wijaya","Kontak personal baru ditambahkan"},
                    {"EDIT","Budi Santoso","Nomor telepon diperbarui"},
                    {"FAVORIT","Rudi Hermawan","Ditandai sebagai favorit"},
                    {"HAPUS","Kontak Lama","Kontak dihapus dari sistem"},
                    {"TAMBAH","PT. Maju Bersama","Kontak bisnis baru ditambahkan"},
                    {"EDIT","Siti Rahayu","Kategori diubah ke Keluarga"},
            };
            for (String[] l : logs) {
                PreparedStatement lps = connection.prepareStatement(
                        "INSERT INTO activity_logs (userID,action,contactName,description,timestamp) VALUES (?,?,?,?,?)");
                lps.setInt(1, uid); lps.setString(2, l[0]); lps.setString(3, l[1]);
                lps.setString(4, l[2]);
                lps.setString(5, LocalDateTime.now().minusHours((long)(Math.random()*72)).toString());
                lps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return password;
        }
    }

    public User login(String username, String password) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM users WHERE username=? AND passwordHash=?");
            ps.setString(1, username);
            ps.setString(2, hashPassword(password));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setUserID(rs.getInt("userID"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                return u;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean register(String username, String password, String email) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (username,passwordHash,email) VALUES (?,?,?)");
            ps.setString(1, username);
            ps.setString(2, hashPassword(password));
            ps.setString(3, email);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public List<Contact> getAllContacts(int userID) {
        List<Contact> list = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM contacts WHERE userID=? ORDER BY name");
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(buildContact(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Contact buildContact(ResultSet rs) throws SQLException {
        String type = rs.getString("contactType");
        Contact c;
        int cid = rs.getInt("contactID");

        c = ContactFactory.create(type.toLowerCase());

        if (c instanceof PersonalContact pc) {
            try {
                PreparedStatement dps = connection.prepareStatement(
                        "SELECT * FROM personalContactDetails WHERE contactID=?");
                dps.setInt(1, cid);
                ResultSet drs = dps.executeQuery();
                if (drs.next()) {
                    pc.setNickname(drs.getString("nickname"));
                    String bd = drs.getString("birthdate");
                    if (bd != null && !bd.isEmpty()) pc.setBirthdate(LocalDate.parse(bd));
                    pc.setRelationship(drs.getString("relationship"));
                }
            } catch (Exception ignored) {}
        } else if (c instanceof BusinessContact bc) {
            try {
                PreparedStatement dps = connection.prepareStatement(
                        "SELECT * FROM businessContactDetails WHERE contactID=?");
                dps.setInt(1, cid);
                ResultSet drs = dps.executeQuery();
                if (drs.next()) {
                    bc.setCompany(drs.getString("company"));
                    bc.setJobTitle(drs.getString("jobTitle"));
                    bc.setWebsite(drs.getString("website"));
                }
            } catch (Exception ignored) {}
        }

        c.setContactID(cid);
        c.setUserID(rs.getInt("userID"));
        c.setContactType(type);
        c.setName(rs.getString("name"));
        c.setPhoneNumber(rs.getString("phoneNumber"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        c.setCategoryFromString(rs.getString("category"));
        c.setFavorite(rs.getInt("favorite") == 1);
        try {
            String ca = rs.getString("createdAt");
            if (ca != null) c.setCreatedAt(LocalDateTime.parse(ca));
        } catch (Exception ignored) {}
        return c;
    }

    public int saveContact(Contact c, int userID) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO contacts (userID,contactType,name,phoneNumber,email,address,category,favorite,createdAt) VALUES (?,?,?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userID); ps.setString(2, c.getContactType()); ps.setString(3, c.getName());
            ps.setString(4, c.getPhoneNumber()); ps.setString(5, c.getEmail()); ps.setString(6, c.getAddress());
            ps.setString(7, c.getCategoryName()); ps.setInt(8, c.isFavorite() ? 1 : 0);
            ps.setString(9, LocalDateTime.now().toString());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            int id = keys.next() ? keys.getInt(1) : -1;

            if (c instanceof PersonalContact pc) {
                PreparedStatement dps = connection.prepareStatement(
                        "INSERT INTO personalContactDetails (contactID,nickname,birthdate,relationship) VALUES (?,?,?,?)");
                dps.setInt(1, id); dps.setString(2, pc.getNickname());
                dps.setString(3, pc.getBirthdate() != null ? pc.getBirthdate().toString() : "");
                dps.setString(4, pc.getRelationship());
                dps.executeUpdate();
            } else if (c instanceof BusinessContact bc) {
                PreparedStatement dps = connection.prepareStatement(
                        "INSERT INTO businessContactDetails (contactID,company,jobTitle,website) VALUES (?,?,?,?)");
                dps.setInt(1, id); dps.setString(2, bc.getCompany());
                dps.setString(3, bc.getJobTitle()); dps.setString(4, bc.getWebsite());
                dps.executeUpdate();
            }
            return id;
        } catch (SQLException e) { e.printStackTrace(); return -1; }
    }

    public void updateContact(Contact c) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE contacts SET name=?,phoneNumber=?,email=?,address=?,category=?,favorite=? WHERE contactID=?");
            ps.setString(1, c.getName()); ps.setString(2, c.getPhoneNumber());
            ps.setString(3, c.getEmail()); ps.setString(4, c.getAddress());
            ps.setString(5, c.getCategoryName()); ps.setInt(6, c.isFavorite() ? 1 : 0);
            ps.setInt(7, c.getContactID());
            ps.executeUpdate();

            if (c instanceof PersonalContact pc) {
                PreparedStatement dps = connection.prepareStatement(
                        "UPDATE personalContactDetails SET nickname=?,birthdate=?,relationship=? WHERE contactID=?");
                dps.setString(1, pc.getNickname());
                dps.setString(2, pc.getBirthdate() != null ? pc.getBirthdate().toString() : "");
                dps.setString(3, pc.getRelationship()); dps.setInt(4, c.getContactID());
                dps.executeUpdate();
            } else if (c instanceof BusinessContact bc) {
                PreparedStatement dps = connection.prepareStatement(
                        "UPDATE businessContactDetails SET company=?,jobTitle=?,website=? WHERE contactID=?");
                dps.setString(1, bc.getCompany()); dps.setString(2, bc.getJobTitle());
                dps.setString(3, bc.getWebsite()); dps.setInt(4, c.getContactID());
                dps.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public void deleteContact(int contactID) {
        try {
            PreparedStatement ps1 = connection.prepareStatement(
                    "DELETE FROM personalContactDetails WHERE contactID=?");
            ps1.setInt(1, contactID);
            ps1.executeUpdate();

            PreparedStatement ps2 = connection.prepareStatement(
                    "DELETE FROM businessContactDetails WHERE contactID=?");
            ps2.setInt(1, contactID);
            ps2.executeUpdate();

            PreparedStatement ps3 = connection.prepareStatement(
                    "DELETE FROM contacts WHERE contactID=?");
            ps3.setInt(1, contactID);
            ps3.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void toggleFavorite(int contactID, boolean fav) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE contacts SET favorite=? WHERE contactID=?");
            ps.setInt(1, fav ? 1 : 0);
            ps.setInt(2, contactID);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public ContactStatistics getStatistics(int userID) {
        ContactStatistics stats = new ContactStatistics();
        try {
            PreparedStatement ps;
            ResultSet rs;

            ps = connection.prepareStatement("SELECT COUNT(*) FROM contacts WHERE userID=?");
            ps.setInt(1, userID); rs = ps.executeQuery();
            stats.setTotalContacts(rs.next() ? rs.getInt(1) : 0);

            ps = connection.prepareStatement("SELECT COUNT(*) FROM contacts WHERE userID=? AND favorite=1");
            ps.setInt(1, userID); rs = ps.executeQuery();
            stats.setFavoriteCount(rs.next() ? rs.getInt(1) : 0);

            ps = connection.prepareStatement("SELECT COUNT(*) FROM contacts WHERE userID=? AND contactType='Personal'");
            ps.setInt(1, userID); rs = ps.executeQuery();
            stats.setPersonalCount(rs.next() ? rs.getInt(1) : 0);

            ps = connection.prepareStatement("SELECT COUNT(*) FROM contacts WHERE userID=? AND contactType='Bisnis'");
            ps.setInt(1, userID); rs = ps.executeQuery();
            stats.setBusinessCount(rs.next() ? rs.getInt(1) : 0);

            String thisMonth = LocalDateTime.now().getYear() + "-" +
                    String.format("%02d", LocalDateTime.now().getMonthValue());
            ps = connection.prepareStatement(
                    "SELECT COUNT(*) FROM contacts WHERE userID=? AND createdAt LIKE ?");
            ps.setInt(1, userID);
            ps.setString(2, thisMonth + "%");
            rs = ps.executeQuery();
            stats.setNewThisMonth(rs.next() ? rs.getInt(1) : 0);

            ps = connection.prepareStatement(
                    "SELECT category, COUNT(*) as cnt FROM contacts WHERE userID=? GROUP BY category ORDER BY cnt DESC");
            ps.setInt(1, userID); rs = ps.executeQuery();
            Map<String, Integer> dist = new LinkedHashMap<>();
            String top = null; int topCnt = 0;
            while (rs.next()) {
                String cat = rs.getString("category");
                int cnt    = rs.getInt("cnt");
                dist.put(cat, cnt);
                if (top == null || cnt > topCnt) { top = cat; topCnt = cnt; }
            }
            stats.setCategoryDistribution(dist);
            stats.setTopCategory(top);
        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }

    public void addLog(int userID, String action, String contactName, String description) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO activity_logs (userID,action,contactName,description,timestamp) VALUES (?,?,?,?,?)");
            ps.setInt(1, userID); ps.setString(2, action); ps.setString(3, contactName);
            ps.setString(4, description); ps.setString(5, LocalDateTime.now().toString());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public List<ActivityLog> getLogs(int userID, String filterAction, String fromDate, String toDate) {
        List<ActivityLog> list = new ArrayList<>();
        try {
            // Build query dengan placeholder ? untuk tiap kondisi opsional
            StringBuilder sql = new StringBuilder(
                    "SELECT * FROM activity_logs WHERE userID=?");

            boolean filterByAction = filterAction != null && !filterAction.equals("Semua");
            boolean filterByFrom   = fromDate != null && !fromDate.isEmpty();
            boolean filterByTo     = toDate   != null && !toDate.isEmpty();

            if (filterByAction) sql.append(" AND action=?");
            if (filterByFrom)   sql.append(" AND timestamp >= ?");
            if (filterByTo)     sql.append(" AND timestamp <= ?");
            sql.append(" ORDER BY timestamp DESC");

            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int idx = 1;
            ps.setInt(idx++, userID);
            if (filterByAction) ps.setString(idx++, filterAction);
            if (filterByFrom)   ps.setString(idx++, fromDate + "T00:00:00");
            if (filterByTo)     ps.setString(idx++, toDate + "T23:59:59");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setLogID(rs.getInt("logID"));
                log.setAction(rs.getString("action"));
                log.setContactName(rs.getString("contactName"));
                log.setDescription(rs.getString("description"));
                try { log.setTimestamp(LocalDateTime.parse(rs.getString("timestamp"))); } catch (Exception ignored) {}
                log.setUserID(rs.getInt("userID"));
                list.add(log);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void clearAllLogs(int userID) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM activity_logs WHERE userID=?");
            ps.setInt(1, userID);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
