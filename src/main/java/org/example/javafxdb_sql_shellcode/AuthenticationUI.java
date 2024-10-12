package org.example.javafxdb_sql_shellcode;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * The AuthenticationUI class provides static methods for creating and displaying
 * login and registration user interfaces.
 */
public class AuthenticationUI {

    /**
     * Displays the authentication UI with login and registration tabs.
     *
     * @param primaryStage   The primary stage of the application.
     * @param onLoginSuccess A Runnable to be executed upon successful login.
     */
    public static void show(Stage primaryStage, Runnable onLoginSuccess) {
        TabPane tabPane = new TabPane();

        Tab loginTab = new Tab("Login", createLoginForm(onLoginSuccess));
        Tab registerTab = new Tab("Register", createRegistrationForm());

        tabPane.getTabs().addAll(loginTab, registerTab);

        Scene scene = new Scene(tabPane, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login/Register");
        primaryStage.show();
    }

    /**
     * Creates and returns a GridPane containing the login form.
     *
     * @param onLoginSuccess A Runnable to be executed upon successful login.
     * @return A GridPane containing the login form elements.
     */
    public static GridPane createLoginForm(Runnable onLoginSuccess) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            onLoginSuccess.run();
        });

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(loginButton, 1, 2);

        return grid;
    }

    /**
     * Creates and returns a GridPane containing the registration form.
     *
     * @return A GridPane containing the registration form elements.
     */
    public static GridPane createRegistrationForm() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        Button registerButton = new Button("Register");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        grid.add(registerButton, 1, 3);

        registerButton.setOnAction(e -> {
            if (passwordField.getText().equals(confirmPasswordField.getText())) {
                showAlert(Alert.AlertType.INFORMATION, "Registration", "Registration successful!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Error", "Passwords do not match!");
            }
        });

        return grid;
    }

    /**
     * Displays an alert dialog with the specified type, title, and content.
     *
     * @param alertType The type of the alert (e.g., INFORMATION, ERROR).
     * @param title     The title of the alert dialog.
     * @param content   The content message of the alert dialog.
     */
    private static void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}