import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpForm extends Application {

    // Database connection details
    private static final String PGHOST = "ep-cool-sea-a2dj5p9s.eu-central-1.aws.neon.tech";
    private static final String PGDATABASE = "europort2";
    private static final String PGUSER = "kris.kajtna";
    private static final String PGPASSWORD = "gk3F9qeiwtXD";
    private static final String URL = "jdbc:postgresql://" + PGHOST + "/" + PGDATABASE;

    @Override
    public void start(Stage primaryStage) {
        // Create labels and fields
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        // Create a button
        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(e -> {
            // Get user input
            String name = nameField.getText();
            String lastName = lastNameField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();

            // Insert user into the database
            try {
                // Connect to the database
                Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);

                // Create a prepared statement with parameters to prevent SQL injection
                String query = "INSERT INTO userji (ime, priimek, uporabniskoime, geslo, email) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, username);
                preparedStatement.setString(4, password);
                preparedStatement.setString(5, email);

                // Execute the query
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("User signed up successfully!");
                    // Close the sign-up form
                    primaryStage.close();
                    // Launch the main class
                    Main.launch(Main.class);
                    // Exit the JavaFX application
                    Platform.exit();
                } else {
                    System.out.println("Failed to sign up user.");
                }

                // Close the connection and statement
                preparedStatement.close();
                connection.close();
            } catch (SQLException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        });

        // Create a grid pane and add labels, fields, and button
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameField, 1, 0);
        gridPane.add(lastNameLabel, 0, 1);
        gridPane.add(lastNameField, 1, 1);
        gridPane.add(usernameLabel, 0, 2);
        gridPane.add(usernameField, 1, 2);
        gridPane.add(passwordLabel, 0, 3);
        gridPane.add(passwordField, 1, 3);
        gridPane.add(emailLabel, 0, 4);
        gridPane.add(emailField, 1, 4);
        gridPane.add(signUpButton, 1, 5);

        // Create a scene and set it on the stage
        Scene scene = new Scene(gridPane, 400, 250);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sign Up Form");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
