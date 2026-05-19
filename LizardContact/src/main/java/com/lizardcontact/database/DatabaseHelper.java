package com.lizardcontact.database;

import com.lizardcontact.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            // Check if data exists
            ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) > 0) return;

            // Insert default user
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (username, passwordHash, email) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, "samuel");
            ps.setString(2, hashPassword("samuel123"));
            ps.setString(3, "samuel@email.com");
            ps.executeUpdate();
            ResultSet genKeys = ps.getGeneratedKeys();
            int uid = genKeys.next() ? genKeys.getInt(1) : 1;

            // Sample contacts
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
                cps.setInt(1, uid);
                cps.setString(2, c[0]);
                cps.setString(3, c[1]);
                cps.setString(4, c[2]);
                cps.setString(5, c[3]);
                cps.setString(6, c[4]);
                cps.setString(7, c[5]);
                cps.setInt(8, Integer.parseInt(c[6]));
                cps.setString(9, LocalDateTime.now().minusDays((long)(Math.random()*30)).toString());
                cps.executeUpdate();
                ResultSet cKeys = cps.getGeneratedKeys();
                int cid = cKeys.next() ? cKeys.getInt(1) : -1;

                if (c[0].equals("Personal")) {
                    PreparedStatement pps = connection.prepareStatement(
                            "INSERT INTO personalContactDetails (contactID,nickname,birthdate,relationship) VALUES (?,?,?,?)");
                    pps.setInt(1, cid);
                    pps.setString(2, c[1].split(" ")[0]);
                    pps.setString(3, "1990-01-01");
                    pps.setString(4, "Sahabat");
                    pps.executeUpdate();
                } else {
                    PreparedStatement bps = connection.prepareStatement(
                            "INSERT INTO businessContactDetails (contactID,company,jobTitle,website) VALUES (?,?,?,?)");
                    bps.setInt(1, cid);
                    bps.setString(2, c[1]);
                    bps.setString(3, "Manager");
                    bps.setString(4, "www.example.com");
                    bps.executeUpdate();
                }
            }

            // Sample activity logs
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
                lps.setInt(1, uid);
                lps.setString(2, l[0]);
                lps.setString(3, l[1]);
                lps.setString(4, l[2]);
                lps.setString(5, LocalDateTime.now().minusHours((long)(Math.random()*72)).toString());
                lps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String hashPassword(String password) {
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

    // --- USER ---
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

    // --- CONTACTS ---
    public List<Contact> getAllContacts(int userID) {
        List<Contact> list = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM contacts WHERE userID=? ORDER BY name");
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Contact c = buildContact(rs);
                list.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Contact buildContact(ResultSet rs) throws SQLException {
        String type = rs.getString("contactType");
        Contact c;
        int cid = rs.getInt("contactID");

        if ("Personal".equals(type)) {
            PersonalContact pc = new PersonalContact();
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
            c = pc;
        } else {
            BusinessContact bc = new BusinessContact();
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
            c = bc;
        }

        c.setContactID(cid);
        c.setUserID(rs.getInt("userID"));
        c.setContactType(type);
        c.setName(rs.getString("name"));
        c.setPhoneNumber(rs.getString("phoneNumber"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        c.setCategory(rs.getString("category"));
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
            ps.setInt(1, userID);
            ps.setString(2, c.getContactType());
            ps.setString(3, c.getName());
            ps.setString(4, c.getPhoneNumber());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getAddress());
            ps.setString(7, c.getCategory());
            ps.setInt(8, c.isFavorite() ? 1 : 0);
            ps.setString(9, LocalDateTime.now().toString());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            int id = keys.next() ? keys.getInt(1) : -1;

            if (c instanceof PersonalContact pc) {
                PreparedStatement dps = connection.prepareStatement(
                        "INSERT INTO personalContactDetails (contactID,nickname,birthdate,relationship) VALUES (?,?,?,?)");
                dps.setInt(1, id);
                dps.setString(2, pc.getNickname());
                dps.setString(3, pc.getBirthdate() != null ? pc.getBirthdate().toString() : "");
                dps.setString(4, pc.getRelationship());
                dps.executeUpdate();
            } else if (c instanceof BusinessContact bc) {
                PreparedStatement dps = connection.prepareStatement(
                        "INSERT INTO businessContactDetails (contactID,company,jobTitle,website) VALUES (?,?,?,?)");
                dps.setInt(1, id);
                dps.setString(2, bc.getCompany());
                dps.setString(3, bc.getJobTitle());
                dps.setString(4, bc.getWebsite());
                dps.executeUpdate();
            }
            return id;
        } catch (SQLException e) { e.printStackTrace(); return -1; }
    }

    public void updateContact(Contact c) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE contacts SET name=?,phoneNumber=?,email=?,address=?,category=?,favorite=? WHERE contactID=?");
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhoneNumber());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getAddress());
            ps.setString(5, c.getCategory());
            ps.setInt(6, c.isFavorite() ? 1 : 0);
            ps.setInt(7, c.getContactID());
            ps.executeUpdate();

            if (c instanceof PersonalContact pc) {
                PreparedStatement dps = connection.prepareStatement(
                        "UPDATE personalContactDetails SET nickname=?,birthdate=?,relationship=? WHERE contactID=?");
                dps.setString(1, pc.getNickname());
                dps.setString(2, pc.getBirthdate() != null ? pc.getBirthdate().toString() : "");
                dps.setString(3, pc.getRelationship());
                dps.setInt(4, c.getContactID());
                dps.executeUpdate();
            } else if (c instanceof BusinessContact bc) {
                PreparedStatement dps = connection.prepareStatement(
                        "UPDATE businessContactDetails SET company=?,jobTitle=?,website=? WHERE contactID=?");
                dps.setString(1, bc.getCompany());
                dps.setString(2, bc.getJobTitle());
                dps.setString(3, bc.getWebsite());
                dps.setInt(4, c.getContactID());
                dps.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteContact(int contactID) {
        try {
            connection.createStatement().execute("DELETE FROM personalContactDetails WHERE contactID=" + contactID);
            connection.createStatement().execute("DELETE FROM businessContactDetails WHERE contactID=" + contactID);
            connection.createStatement().execute("DELETE FROM contacts WHERE contactID=" + contactID);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void toggleFavorite(int contactID, boolean fav) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE contacts SET favorite=? WHERE contactID=?");
            ps.setInt(1, fav ? 1 : 0);
            ps.setInt(2, contactID);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- STATISTICS ---
    public ContactStatistics getStatistics(int userID) {
        ContactStatistics stats = new ContactStatistics();
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM contacts WHERE userID=" + userID);
            stats.setTotalContacts(rs.next() ? rs.getInt(1) : 0);

            rs = connection.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM contacts WHERE userID=" + userID + " AND favorite=1");
            stats.setFavoriteCount(rs.next() ? rs.getInt(1) : 0);

            rs = connection.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM contacts WHERE userID=" + userID + " AND contactType='Personal'");
            stats.setPersonalCount(rs.next() ? rs.getInt(1) : 0);

            rs = connection.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM contacts WHERE userID=" + userID + " AND contactType='Bisnis'");
            stats.setBusinessCount(rs.next() ? rs.getInt(1) : 0);

            String thisMonth = LocalDateTime.now().getYear() + "-" +
                    String.format("%02d", LocalDateTime.now().getMonthValue());
            rs = connection.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM contacts WHERE userID=" + userID + " AND createdAt LIKE '" + thisMonth + "%'");
            stats.setNewThisMonth(rs.next() ? rs.getInt(1) : 0);

            rs = connection.createStatement().executeQuery(
                    "SELECT category, COUNT(*) as cnt FROM contacts WHERE userID=" + userID +
                            " GROUP BY category ORDER BY cnt DESC");
            Map<String, Integer> dist = new LinkedHashMap<>();
            String top = null;
            int topCnt = 0;
            while (rs.next()) {
                String cat = rs.getString("category");
                int cnt = rs.getInt("cnt");
                dist.put(cat, cnt);
                if (top == null || cnt > topCnt) { top = cat; topCnt = cnt; }
            }
            stats.setCategoryDistribution(dist);
            stats.setTopCategory(top);
        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }

    // --- ACTIVITY LOGS ---
    public void addLog(int userID, String action, String contactName, String description) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO activity_logs (userID,action,contactName,description,timestamp) VALUES (?,?,?,?,?)");
            ps.setInt(1, userID);
            ps.setString(2, action);
            ps.setString(3, contactName);
            ps.setString(4, description);
            ps.setString(5, LocalDateTime.now().toString());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<ActivityLog> getLogs(int userID, String filterAction, String fromDate, String toDate) {
        List<ActivityLog> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder(
                    "SELECT * FROM activity_logs WHERE userID=" + userID);
            if (filterAction != null && !filterAction.equals("Semua"))
                sql.append(" AND action='").append(filterAction).append("'");
            if (fromDate != null && !fromDate.isEmpty())
                sql.append(" AND timestamp >= '").append(fromDate).append("'");
            if (toDate != null && !toDate.isEmpty())
                sql.append(" AND timestamp <= '").append(toDate).append(" 23:59:59'");
            sql.append(" ORDER BY timestamp DESC");

            ResultSet rs = connection.createStatement().executeQuery(sql.toString());
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
            connection.createStatement().execute("DELETE FROM activity_logs WHERE userID=" + userID);
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
