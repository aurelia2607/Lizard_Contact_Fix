package com.lizardcontact.util;

import com.lizardcontact.model.User;

public class SessionManager {
        private static com.lizardcontact.util.SessionManager instance;
        private User currentUser;

        private SessionManager() {
        }

        public static com.lizardcontact.util.SessionManager getInstance() {
            if (instance == null) {
                instance = new com.lizardcontact.util.SessionManager();
            }

            return instance;
        }

        public User getCurrentUser() {
            return this.currentUser;
        }

        public void setCurrentUser(User user) {
            this.currentUser = user;
        }

        public void logout() {
            this.currentUser = null;
        }

        public boolean isLoggedIn() {
            return this.currentUser != null;
        }
}