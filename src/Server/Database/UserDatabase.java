package Server.Database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

import Server.Database.Database;
import Server.Server;
import Shared.*;

public class UserDatabase extends Database {

    private ResultSet results;

    public UserDatabase(Properties properties) {
        super(properties);
    }

    public User getUser(String value) throws Throwable {
        super.startConnection();
        User returnValue = new User();

        try {
            String sqlSelect = "SELECT * FROM users WHERE userName = \"" + value + "\"";
            results = super.runSelectQuery(sqlSelect);
            results.next();
            returnValue = new User(results.getBytes("userObject"));
            results.close();
        } catch (SQLException e) {}
        super.closeConnection();
        return returnValue;
    }

    public ArrayList<User> getUsers() throws Throwable {
        super.startConnection();
        ArrayList<User> allUser = new ArrayList<>();

        try {
            String sqlSelect = "SELECT * FROM users";
            results = super.runSelectQuery(sqlSelect);

            while(results.next()) {
                User user = new User(results.getBytes("userObject"));
                allUser.add(user);
            }
            results.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        super.closeConnection();
        return allUser;
   }

    public boolean isInTable(User user) {
        boolean returnValue = false;
        if(user != null) {
            try {
                String sqlSelect = "select userID from users where userName = \"" + user.getUserName() + "\"";
                results = super.runSelectQuery(sqlSelect);
                returnValue = results.next();
                results.close();
            } catch (SQLException e) { } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return returnValue;
    }

    public void updateDatabase(User user) throws Throwable {
        if(!super.getConnectionStatus()) {
            super.startConnection();
        }
        if (isInTable(user)) {
            String sqlUpdate = "UPDATE users SET userObject = ? where userName = ?";
            Object[] parameters = new Object[]{user, user.getUserName()};
            super.runInsertUpdateQuery(sqlUpdate, parameters, "UPDATE");
        }
        super.closeConnection();
    }

    public void addToDatabase(User user) throws Throwable {
        super.startConnection();
        if (!isInTable(user)) {
            String sqlInsert = "INSERT INTO users (userName, userObject) VALUES (?, ?)";
            Object[] parameters = new Object[]{user.getUserName(), user};
            super.runInsertUpdateQuery(sqlInsert, parameters, "INSERT");
        }

        updateUserID(user);
        if(super.getConnectionStatus()) {
            super.closeConnection();
        }
    }

    public void updateUserID(User user) throws Throwable {
        String sqlSelect = "SELECT userID FROM users WHERE userName = \"" + user.getUserName() + "\"";
        ResultSet result = super.runSelectQuery(sqlSelect);

        try {
            result.next();
            user.setUserID(result.getInt("userID"));
            result.close();
            updateDatabase(user);
        } catch (SQLException e) {}

        if(super.getConnectionStatus()) {
            super.closeConnection();
        }
    }
}