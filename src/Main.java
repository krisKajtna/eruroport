import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends Application {

    // Global variable to store user ID
    private static int loggedInUserID;

    // Method to get the logged-in user ID
    public static int getLoggedInUserID() {
        return loggedInUserID;
    }

    @Override
    public void start(Stage primaryStage) {
        // Create labels and fields
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        // Create buttons
        Button loginButton = new Button("Login");
        Button signUpButton = new Button("Sign Up");

        // Event handler for Sign Up button
        signUpButton.setOnAction(e -> {
            // Open SignUpForm window
            SignUpForm signUpForm = new SignUpForm();
            Stage signUpStage = new Stage();
            signUpForm.start(signUpStage);
        });

        // Event handler for Login button
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Check if username and password are correct
            if (authenticate(username, password)) {
                // Save the logged-in user ID
                loggedInUserID = getUserID(username);

                // Open LetaliscaInfo window
                LetaliscaInfo letaliscaInfo = new LetaliscaInfo();
                Stage letaliscaInfoStage = new Stage();
                letaliscaInfo.start(letaliscaInfoStage);

                // Close the login window
                primaryStage.close();
            } else {
                // Show error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username or password!");
                alert.showAndWait();
            }
        });

        // Create a grid pane and add labels, fields, and buttons
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);

        // Create an HBox for buttons
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(loginButton, signUpButton);

        // Add the button box to the grid pane
        gridPane.add(buttonBox, 1, 2);

        // Create a scene and set it on the stage
        Scene scene = new Scene(gridPane, 300, 150);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Form");
        primaryStage.show();
    }

    private boolean authenticate(String username, String password) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM userji WHERE uporabniskoime = ? AND geslo = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // True if a matching record is found
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of exception
        }
    }

    // Method to retrieve user ID based on username
    private int getUserID(String username) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "SELECT id FROM userji WHERE uporabniskoime = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if user ID not found
    }

    public static void main(String[] args) {
        launch(args);
    }
}
