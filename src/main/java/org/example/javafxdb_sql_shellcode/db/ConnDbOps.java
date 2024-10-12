package org.example.javafxdb_sql_shellcode.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

/**
 * This class handles all database operations for the application.
 * It provides methods for connecting to the database, querying, inserting, updating, and deleting users,
 * as well as handling profile picture operations.
 */
public class ConnDbOps {
    final String MYSQL_SERVER_URL = "jdbc:mysql://csc311jeanserver.mysql.database.azure.com/";
    final String DB_URL = "jdbc:mysql://csc311jeanserver.mysql.database.azure.com/DBname";
    final String USERNAME = "philippejean0429";
    final String PASSWORD = "Cscserver0429";

    /**
     * Connects to the database and creates necessary tables if they don't exist.
     *
     * @return true if there are registered users in the database, false otherwise.
     */
    public boolean connectToDatabase() {
        boolean hasRegistredUsers = false;

        try {
            // First, connect to MYSQL server and create the database if not created
            Connection conn = DriverManager.getConnection(MYSQL_SERVER_URL, USERNAME, PASSWORD);
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS DBname");
            statement.close();
            conn.close();

            // Second, connect to the database and create the table "users" if not created
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT( 10 ) NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                    + "name VARCHAR(200) NOT NULL,"
                    + "email VARCHAR(200) NOT NULL UNIQUE,"
                    + "phone VARCHAR(200),"
                    + "address VARCHAR(200)"
                    + ")";
            statement.executeUpdate(sql);

            // Check if we have users in the table users
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");

            if (resultSet.next()) {
                int numUsers = resultSet.getInt(1);
                if (numUsers > 0) {
                    hasRegistredUsers = true;
                }
            }

            statement.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasRegistredUsers;
    }

    /**
     * Lists all users in the database.
     *
     * @return A string containing all users' details or an error message.
     */
    public String listAllUsers() {
        StringBuilder result = new StringBuilder();

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM users";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String address = resultSet.getString("address");
                result.append(String.format("Name: %s, Email: %s, Phone: %s, Address: %s\n",
                        name, email, phone, address));
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error listing users: " + e.getMessage();
        }

        return result.length() > 0 ? result.toString() : "No users found in the database.";
    }


    /**
     * Inserts a new user into the database.
     *
     * @param name    The name of the new user.
     * @param email   The email of the new user.
     * @param phone   The phone number of the new user.
     * @param address The address of the new user.
     * @return A string indicating success or failure of the operation.
     */
    public String insertUser(String name, String email, String phone, String address) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO users (name, email, phone, address) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);

            int row = preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();

            if (row > 0) {
                return "A new user was inserted successfully.";
            } else {
                return "Failed to insert the new user.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error inserting user: " + e.getMessage();
        }
    }

    /**
     * Modifies the password column to allow NULL values.
     */
    public void allowNullPasswords() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = conn.createStatement();
            String sql = "ALTER TABLE users MODIFY COLUMN password VARCHAR(200) NULL";
            statement.executeUpdate(sql);
            statement.close();
            conn.close();
            System.out.println("Password column modified to allow NULL values.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error modifying password column: " + e.getMessage());
        }
    }

    /**
     * Edits an existing user's information.
     *
     * @param name       The name of the user to edit.
     * @param newEmail   The new email for the user.
     * @param newPhone   The new phone number for the user.
     * @param newAddress The new address for the user.
     * @return A string indicating success or failure of the operation.
     */
    public String editUserByName(String name, String newEmail, String newPhone, String newAddress) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "UPDATE users SET email = COALESCE(?, email), " +
                    "phone = COALESCE(?, phone), address = COALESCE(?, address) WHERE name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, newEmail.isEmpty() ? null : newEmail);
            preparedStatement.setString(2, newPhone.isEmpty() ? null : newPhone);
            preparedStatement.setString(3, newAddress.isEmpty() ? null : newAddress);
            preparedStatement.setString(4, name);

            int rowsAffected = preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();

            if (rowsAffected > 0) {
                return "User updated successfully.";
            } else {
                return "No user found with the name: " + name;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error updating user: " + e.getMessage();
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param name The name of the user to delete.
     * @return A string indicating success or failure of the operation.
     */
    public String deleteUserByName(String name) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "DELETE FROM users WHERE name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);

            int rowsAffected = preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();

            if (rowsAffected > 0) {
                return "User deleted successfully.";
            } else {
                return "No user found with the name: " + name;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting user: " + e.getMessage();
        }
    }

    /**
     * Checks if a column exists in the users table.
     *
     * @param columnName The name of the column to check.
     * @return true if the column exists, false otherwise.
     */
    public boolean columnExists(String columnName) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getColumns(null, null, "users", columnName);
            boolean exists = rs.next();
            rs.close();
            conn.close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of Object arrays, each representing a user.
     */
    public List<Object[]> getAllUsers() {
        List<Object[]> users = new ArrayList<>();
        String query = "SELECT name, email, phone, address FROM users ORDER BY id";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] user = new Object[]{
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                };
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Deletes the profile picture of a user.
     *
     * @param userName The name of the user whose profile picture should be deleted.
     * @return A string indicating success or failure of the operation.
     */
    public String deleteProfilePicture(String userName) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "UPDATE users SET profile_picture = NULL WHERE name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);

            int rowsAffected = preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();

            if (rowsAffected > 0) {
                return "Profile picture deleted successfully for " + userName + ".";
            } else {
                return "No user found with the name: " + userName;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting profile picture: " + e.getMessage();
        }
    }

    /**
     * Uploads a profile picture for a user.
     *
     * @param userName    The name of the user.
     * @param pictureData The byte array of the picture data.
     * @return A string indicating success or failure of the operation.
     */
    public String uploadProfilePicture(String userName, byte[] pictureData) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            // First, check if the user exists
            String checkUserSql = "SELECT COUNT(*) FROM users WHERE name = ?";
            PreparedStatement checkUserStmt = conn.prepareStatement(checkUserSql);
            checkUserStmt.setString(1, userName);
            ResultSet rs = checkUserStmt.executeQuery();
            rs.next();
            int userCount = rs.getInt(1);
            if (userCount == 0) {
                return "User " + userName + " does not exist.";
            }

            String sql = "UPDATE users SET profile_picture = ? WHERE name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setBytes(1, pictureData);
            preparedStatement.setString(2, userName);

            int rowsAffected = preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();

            if (rowsAffected > 0) {
                return "Profile picture uploaded successfully for " + userName + ".";
            } else {
                return "Failed to upload profile picture. No rows were updated.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "SQL Error: " + e.getMessage();
        }
    }

    /**
     * Retrieves the profile picture of a user.
     *
     * @param userName The name of the user.
     * @return A byte array of the profile picture data, or null if not found.
     */
    public byte[] getProfilePicture(String userName) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT profile_picture FROM users WHERE name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                byte[] imageData = resultSet.getBytes("profile_picture");
                preparedStatement.close();
                conn.close();
                return imageData;
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a profile picture column to the users table if it doesn't exist.
     */
    public void addProfilePictureColumn() {
        try {
            if (!columnExists("profile_picture")) {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                Statement statement = conn.createStatement();
                String sql = "ALTER TABLE users ADD COLUMN profile_picture LONGBLOB";
                statement.executeUpdate(sql);
                statement.close();
                conn.close();
                System.out.println("Profile picture column added successfully.");
            } else {
                System.out.println("Profile picture column already exists.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding profile picture column: " + e.getMessage());
        }
    }
}
