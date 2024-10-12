package org.example.javafxdb_sql_shellcode;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.example.javafxdb_sql_shellcode.db.ConnDbOps;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

/**
 * The main application class for the Database Management Application.
 * This class sets up the user interface, handles user interactions,
 * and manages the connection to the database.
 */
public class App extends Application {

    private static Scene scene;
    private static ConnDbOps cdbop;
    private TableView<Object[]> tableView;
    private boolean isDarkMode = false;

    private TextArea outputArea;
    private TextField nameField;
    private TextField emailField;
    private TextField phoneField;
    private TextField addressField;

    private ImageView profilePicView;
    private static final String DEFAULT_PROFILE_PIC = "/profile.png";


    /**
     * The main entry point for the JavaFX application.
     * This method sets up the primary stage and initiates the application flow.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        showMainApplication(primaryStage);
        SplashScreen.show(primaryStage, () -> {
            Platform.runLater(() -> {
                try {
                    showAuthenticationUI(primaryStage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    /**
     * Displays the authentication UI for user login or registration.
     *
     * @param primaryStage The primary stage of the application.
     * @throws IOException If there's an error loading the UI components.
     */
    private void showAuthenticationUI(Stage primaryStage) throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(primaryStage);
        dialog.setTitle("Login/Register");

        TabPane tabPane = new TabPane();

        Tab loginTab = new Tab("Login");
        GridPane loginForm = AuthenticationUI.createLoginForm(() -> {
            dialog.setResult(ButtonType.OK);
            dialog.close();
        });
        loginTab.setContent(loginForm);

        Tab registerTab = new Tab("Register");
        GridPane registerForm = AuthenticationUI.createRegistrationForm();
        registerTab.setContent(registerForm);

        tabPane.getTabs().addAll(loginTab, registerTab);

        dialog.getDialogPane().setContent(tabPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            showMainApplication(primaryStage);
        } else {
            Platform.exit();
        }
    }

    /**
     * Sets up and displays the main application window.
     *
     * @param primaryStage The primary stage of the application.
     */
    private void showMainApplication(Stage primaryStage) {
        cdbop = new ConnDbOps();
        BorderPane mainLayout = new BorderPane();

        // Create MenuBar
        MenuBar menuBar = createMenuBar();
        mainLayout.setTop(menuBar);

        // Create left panel (for profile picture)
        VBox leftPanel = createLeftPanel();
        mainLayout.setLeft(leftPanel);

        // Create center panel (for user details form)
        VBox centerPanel = createCenterPanel();
        mainLayout.setCenter(centerPanel);

        // Create right panel (for table view)
        tableView = createTableView();
        mainLayout.setRight(tableView);

        // Create bottom panel
        HBox bottomPanel = new HBox();
        bottomPanel.getStyleClass().add("bottom_pane");
        bottomPanel.setPrefHeight(32);
        mainLayout.setBottom(bottomPanel);

        // Create scene
        scene = new Scene(mainLayout, 807, 535);

        // Apply initial theme
        scene.getStylesheets().add(getClass().getResource("/light-theme.css").toExternalForm());

        // Set the scene
        primaryStage.setScene(scene);
        primaryStage.setTitle("Database Management Application");
        primaryStage.show();

        // Connect to the database and refresh the table view
        connectToDb();
        refreshTableView();
    }

    /**
     * Creates and returns the menu bar for the application.
     *
     * @return A MenuBar object containing all application menus.
     */
    private MenuBar createMenuBar() {

        MenuBar menuBar = new MenuBar();

        Menu helpMenu = new Menu("Help");
        MenuItem challengesItem = new MenuItem("Challenges");
        challengesItem.setOnAction(e -> showChallenges());
        challengesItem.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));
        helpMenu.getItems().add(challengesItem);

        Menu fileMenu = new Menu("File");
        MenuItem closeItem = new MenuItem("Close");
        closeItem.setOnAction(e -> System.exit(0));
        closeItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        fileMenu.getItems().add(closeItem);

        Menu dbMenu = new Menu("Database");
        MenuItem connectItem = new MenuItem("Connect to DB");
        connectItem.setOnAction(e -> connectToDb());
        connectItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        dbMenu.getItems().add(connectItem);

        Menu viewMenu = new Menu("View");
        MenuItem toggleThemeItem = new MenuItem("Toggle Theme");
        toggleThemeItem.setOnAction(e -> switchTheme());
        toggleThemeItem.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));
        viewMenu.getItems().add(toggleThemeItem);

        menuBar.getMenus().addAll(fileMenu, dbMenu, viewMenu, helpMenu);
        return menuBar;
    }

    /**
     * Clears all input fields in the user form.
     */
    private void clearForm() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
    }

    /**
     * Creates and returns the left panel of the application, which contains the profile picture and related buttons.
     *
     * @return A VBox containing the profile picture view and associated buttons.
     */
    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.getStyleClass().add("left_pane");
        leftPanel.setPrefWidth(116);

        // Load the default profile picture
        Image defaultImage = new Image(getClass().getResourceAsStream(DEFAULT_PROFILE_PIC));
        profilePicView = new ImageView(defaultImage);
        profilePicView.setFitHeight(103);
        profilePicView.setFitWidth(85);
        profilePicView.getStyleClass().add("profile_pic");

        Button addImageButton = new Button("Add Image");
        addImageButton.setOnAction(e -> addProfilePicture());

        Button deleteImageButton = new Button("Delete Image");
        deleteImageButton.setOnAction(e -> deleteProfilePicture());

        leftPanel.getChildren().addAll(profilePicView, addImageButton, deleteImageButton);
        return leftPanel;
    }

    /**
     * Handles the process of adding a profile picture for a user.
     */
    private void addProfilePicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                byte[] fileContent = Files.readAllBytes(selectedFile.toPath());

                TextInputDialog nameDialog = new TextInputDialog();
                nameDialog.setTitle("Assign Profile Picture");
                nameDialog.setHeaderText("Enter the name of the user");
                nameDialog.setContentText("User Name:");

                Optional<String> nameResult = nameDialog.showAndWait();
                nameResult.ifPresent(userName -> {
                    String result = cdbop.uploadProfilePicture(userName, fileContent);
                    outputArea.appendText(result + "\n");
                    updateProfilePicture(userName);
                });
            } catch (IOException e) {
                outputArea.appendText("Error reading file: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        } else {
            outputArea.appendText("No file selected.\n");
        }
    }

    /**
     * Handles the process of deleting a profile picture for the currently selected user.
     */
    private void deleteProfilePicture() {
        String name = nameField.getText();
        if (name.isEmpty()) {
            outputArea.appendText("Please select a user first.\n");
            return;
        }

        String result = cdbop.deleteProfilePicture(name);
        outputArea.appendText(result + "\n");
        updateProfilePicture(name);
    }

    /**
     * Creates and returns the center panel of the application, which contains the user input form and action buttons.
     *
     * @return A VBox containing text fields for user information and action buttons.
     */
    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(5);
        centerPanel.setPadding(new Insets(5, 10, 10, 10));

        outputArea = new TextArea();
        outputArea.setEditable(false);

        nameField = new TextField();
        nameField.setPromptText("Name");

        emailField = new TextField();
        emailField.setPromptText("Email");

        phoneField = new TextField();
        phoneField.setPromptText("Phone");

        addressField = new TextField();
        addressField.setPromptText("Address");


        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("nav_btn");
        clearButton.setOnAction(e -> clearForm());

        Button addButton = new Button("Add");
        addButton.getStyleClass().add("nav_btn");
        addButton.setOnAction(e -> insertUser());

        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("nav_btn");
        editButton.setOnAction(e -> editUser());

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("nav_btn");
        deleteButton.setOnAction(e -> deleteUser());

        centerPanel.getChildren().addAll(nameField, emailField, phoneField, addressField, clearButton, addButton, editButton, deleteButton, outputArea);

        return centerPanel;
    }

    /**
     * Refreshes the TableView with the latest user data from the database.
     */
    private void refreshTableView() {
        List<Object[]> users = cdbop.getAllUsers();
        tableView.getItems().clear(); // Clear existing items
        tableView.getItems().addAll(users); // Add all users
    }

    /**
     * Creates and returns the TableView that displays user information.
     *
     * @return A TableView configured to display user data.
     */
    private TableView<Object[]> createTableView() {
        TableView<Object[]> tableView = new TableView<>();
        tableView.setPrefWidth(507);

        TableColumn<Object[], String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue()[0]));

        TableColumn<Object[], String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue()[1]));

        TableColumn<Object[], String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue()[2]));

        TableColumn<Object[], String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue()[3]));

        double columnWidth = tableView.getPrefWidth() / 4;
        nameColumn.setPrefWidth(columnWidth);
        emailColumn.setPrefWidth(columnWidth);
        phoneColumn.setPrefWidth(columnWidth);
        addressColumn.setPrefWidth(columnWidth);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String name = (String) newSelection[0];
                String email = (String) newSelection[1];
                String phone = (String) newSelection[2];
                String address = (String) newSelection[3];

                nameField.setText(name);
                emailField.setText(email);
                phoneField.setText(phone);
                addressField.setText(address);

                updateProfilePicture(name);
            }
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Object[] selectedUser = tableView.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    nameField.setText((String) selectedUser[0]);
                    emailField.setText((String) selectedUser[1]);
                    phoneField.setText((String) selectedUser[2]);
                    addressField.setText((String) selectedUser[3]);
                }
            }
        });

        tableView.getColumns().addAll(nameColumn, emailColumn, phoneColumn, addressColumn);

        this.tableView = tableView;

        return tableView;
    }

    /**
     * Toggles between light and dark themes for the application.
     */
    private void switchTheme() {
        isDarkMode = !isDarkMode;
        applyTheme(scene);
    }

    /**
     * Applies the current theme (light or dark) to the given scene.
     *
     * @param scene The scene to which the theme should be applied.
     */
    private void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        if (isDarkMode) {
            scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
        } else {
            scene.getStylesheets().add(getClass().getResource("/light-theme.css").toExternalForm());
        }
    }

    /**
     * Applies the current theme to a dialog.
     *
     * @param dialog The dialog to which the theme should be applied.
     */
    private void applyThemeToDialog(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        Scene dialogScene = dialogPane.getScene();
        applyTheme(dialogScene);
    }

    /**
     * Handles the process of uploading a profile picture for the currently selected user.
     */
    private void uploadProfilePicture() {
        String name = nameField.getText();
        if (name.isEmpty()) {
            outputArea.appendText("Please select a user first or enter a name.\n");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
                outputArea.appendText("File size: " + fileContent.length + " bytes\n");

                // First, ensure the profile_picture column exists
                cdbop.addProfilePictureColumn();

                // Then, upload the picture
                String result = cdbop.uploadProfilePicture(name, fileContent);
                outputArea.appendText(result + "\n");

                // Update the displayed profile picture
                updateProfilePicture(name);
            } catch (IOException e) {
                outputArea.appendText("Error reading file: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        } else {
            outputArea.appendText("No file selected.\n");
        }
    }

    /**
     * Updates the profile picture display for the specified user.
     *
     * @param userName The name of the user whose profile picture should be updated.
     */
    private void updateProfilePicture(String userName) {
        byte[] imageData = cdbop.getProfilePicture(userName);
        if (imageData != null && imageData.length > 0) {
            Image image = new Image(new ByteArrayInputStream(imageData));
            profilePicView.setImage(image);
        } else {
            Image defaultImage = new Image(getClass().getResourceAsStream(DEFAULT_PROFILE_PIC));
            profilePicView.setImage(defaultImage);
        }
    }

    /**
     * Displays the profile picture for a user specified through a dialog.
     */
    private void displayProfilePicture() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Display Profile Picture");
        dialog.setHeaderText("Enter the name of the user");
        dialog.setContentText("User Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(userName -> {
            byte[] imageData = cdbop.getProfilePicture(userName);
            Image image;
            if (imageData != null && imageData.length > 0) {
                image = new Image(new ByteArrayInputStream(imageData));
            } else {
                image = new Image(getClass().getResourceAsStream(DEFAULT_PROFILE_PIC));
            }
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);

            Dialog<Void> imageDialog = new Dialog<>();
            imageDialog.setTitle("Profile Picture for " + userName);
            imageDialog.getDialogPane().setContent(imageView);
            imageDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            imageDialog.showAndWait();
        });
    }

    /**
     * Sets the root of the scene to a new FXML layout.
     *
     * @param fxml The name of the FXML file to load.
     * @throws IOException If there's an error loading the FXML file.
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Loads an FXML file and returns the root element.
     *
     * @param fxml The name of the FXML file to load.
     * @return The root element of the loaded FXML file.
     * @throws IOException If there's an error loading the FXML file.
     */
    private static javafx.scene.Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Establishes a connection to the database.
     */
    private void connectToDb() {
        boolean connected = cdbop.connectToDatabase();
        if (connected) {
            outputArea.appendText("Connected to database.\n");
            cdbop.addProfilePictureColumn();
            cdbop.allowNullPasswords(); // Add this line
        } else {
            outputArea.appendText("Failed to connect to database.\n");
        }
    }

    /**
     * Retrieves and displays a list of all users in the database.
     */
    private void listAllUsers() {
        String result = cdbop.listAllUsers();
        outputArea.appendText("Listing all users:\n" + result + "\n");
    }

    /**
     * Inserts a new user into the database based on the current form input.
     */
    private void insertUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();

        if (name.isEmpty() || email.isEmpty()) {
            outputArea.appendText("Name and Email are required fields.\n");
            return;
        }

        String result = cdbop.insertUser(name, email, phone, address);
        outputArea.appendText(result + "\n");
        refreshTableView();
        clearForm();
    }

    /**
     * Updates the information of an existing user based on the current form input.
     */
    private void editUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();

        if (name.isEmpty()) {
            outputArea.appendText("Name is required for editing.\n");
            return;
        }

        String result = cdbop.editUserByName(name, email, phone, address);
        outputArea.appendText(result + "\n");
        refreshTableView();
        clearForm();
    }

    /**
     * Deletes the currently selected user from the database.
     */
    private void deleteUser() {
        String name = nameField.getText();

        if (name.isEmpty()) {
            outputArea.appendText("Name is required for deletion.\n");
            return;
        }

        String result = cdbop.deleteUserByName(name);
        outputArea.appendText(result + "\n");
        refreshTableView();
        clearForm();
    }

    /**
     * Displays an alert dialog showing the challenges faced during development and their solutions.
     */
    private void showChallenges() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Integration Challenges");
        alert.setHeaderText("Challenges Faced and Solutions");

        String challengesText =
                "1. Database Connection Issues:\n" +
                        "   Challenge: Initially faced difficulties connecting to the Azure MySQL database.\n" +
                        "   Solution: Verified connection string, updated firewall rules, and ensured proper SSL configuration.\n\n" +
                        "2. JavaFX and FXML Integration:\n" +
                        "   Challenge: Aligning JavaFX code with FXML structure.\n" +
                        "   Solution: Refactored code to use programmatic JavaFX instead of FXML for better control.\n\n" +
                        "3. Theme Switching:\n" +
                        "   Challenge: Implementing a smooth dark/light theme switch.\n" +
                        "   Solution: Created separate CSS files and implemented a theme toggle method.\n\n";

        alert.setContentText(challengesText);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        applyThemeToDialog(alert);
        alert.showAndWait();
    }

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        launch(args);
    }
}