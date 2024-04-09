import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {

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

    public static void main(String[] args) {
        launch(args);
    }
}
