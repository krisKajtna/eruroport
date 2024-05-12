import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class insertLetalskeDruzbe extends Application {

    private static final String PGHOST = "ep-cool-sea-a2dj5p9s.eu-central-1.aws.neon.tech";
    private static final String PGDATABASE = "europort2";
    private static final String PGUSER = "kris.kajtna";
    private static final String PGPASSWORD = "gk3F9qeiwtXD";
    private static final String URL = "jdbc:postgresql://" + PGHOST + "/" + PGDATABASE;

    private ObservableList<String> letalisca = FXCollections.observableArrayList();
    private LetalskeDruzbeInfo letalskeDruzbeInfo;
    private Stage stage;

    // This method assumes you have a way to get the logged in user ID
    private int getLoggedInUserId() {
        // Replace this with your logic to get the logged in user ID
        return 123; // Just a placeholder value, replace with actual implementation
    }

    public void setLetalskeDruzbeInfo(LetalskeDruzbeInfo letalskeDruzbeInfo) {
        this.letalskeDruzbeInfo = letalskeDruzbeInfo;
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage; // Assign the stage

        primaryStage.setTitle("Insert Letalske Druzbe");

        // Create form elements
        Label imeLabel = new Label("Ime:");
        TextField imeField = new TextField();

        Label kraticaLabel = new Label("Kratica:");
        TextField kraticaField = new TextField();

        Label letaliscaIdLabel = new Label("Letalisca ID:");
        ComboBox<String> letaliscaComboBox = new ComboBox<>();
        letaliscaComboBox.setItems(letalisca);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            String ime = imeField.getText();
            String kratica = kraticaField.getText();
            String selectedLetalisce = letaliscaComboBox.getValue();

            if (selectedLetalisce != null && !selectedLetalisce.isEmpty()) {
                String letaliscaId = getLetaliscaId(selectedLetalisce);
                insertLetalskaDruzba(ime, kratica, letaliscaId);

                // Clear the fields after submission
                imeField.clear();
                kraticaField.clear();
                letaliscaComboBox.getSelectionModel().clearSelection();
            } else {
                System.out.println("Please select a letalisce.");
            }
        });

        // Layout setup
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        GridPane.setConstraints(imeLabel, 0, 0);
        GridPane.setConstraints(imeField, 1, 0);

        GridPane.setConstraints(kraticaLabel, 0, 1);
        GridPane.setConstraints(kraticaField, 1, 1);

        GridPane.setConstraints(letaliscaIdLabel, 0, 2);
        GridPane.setConstraints(letaliscaComboBox, 1, 2);

        GridPane.setConstraints(submitButton, 1, 3);

        grid.getChildren().addAll(
                imeLabel, imeField,
                kraticaLabel, kraticaField,
                letaliscaIdLabel, letaliscaComboBox,
                submitButton
        );

        VBox layout = new VBox(10);
        layout.getChildren().addAll(grid);
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        fetchLetaliscaFromDatabase();
    }

    private void fetchLetaliscaFromDatabase() {
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM letalisca")) {

            while (resultSet.next()) {
                letalisca.add(resultSet.getString("ime"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getLetaliscaId(String letalisceName) {
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM letalisca WHERE ime = ?")) {
            preparedStatement.setString(1, letalisceName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void insertLetalskaDruzba(String ime, String kratica, String letaliscaId) {
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO letalskedruzbe (ime, kratica, letalisca_id, user_id) " +
                             "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, ime);
            preparedStatement.setString(2, kratica);
            preparedStatement.setInt(3, Integer.parseInt(letaliscaId)); // Parse as integer
            preparedStatement.setInt(4, getLoggedInUserId()); // Add the logged in user ID
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Letalska druzba inserted successfully!");
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    letalskeDruzbeInfo.refreshTable(); // Update the table in LetalskeDruzbeInfo window
                    stage.close(); // Close the insertLetalskaDruzba window
                }
            } else {
                System.out.println("Failed to insert letalska druzba.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
