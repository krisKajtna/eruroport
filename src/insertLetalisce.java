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

public class insertLetalisce extends Application {

    private static final String PGHOST = "ep-cool-sea-a2dj5p9s.eu-central-1.aws.neon.tech";
    private static final String PGDATABASE = "europort2";
    private static final String PGUSER = "kris.kajtna";
    private static final String PGPASSWORD = "gk3F9qeiwtXD";
    private static final String URL = "jdbc:postgresql://" + PGHOST + "/" + PGDATABASE;

    private ObservableList<String> kraji = FXCollections.observableArrayList();
    private LetaliscaInfo letaliscaInfo;
    private Stage stage;

    public void setLetaliscaInfo(LetaliscaInfo letaliscaInfo) {
        this.letaliscaInfo = letaliscaInfo;
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage; // Assign the stage

        primaryStage.setTitle("Insert Letalisce");

        // Create form elements
        Label imeLabel = new Label("Ime:");
        TextField imeField = new TextField();

        Label kapacitetapotnikovLabel = new Label("Kapaciteta Potnikov:");
        TextField kapacitetapotnikovField = new TextField();

        Label kapacitetatovoraLabel = new Label("Kapaciteta Tovora:");
        TextField kapacitetatovoraField = new TextField();

        Label letalskapovezavaLabel = new Label("Letalska Povezava:");
        TextField letalskapovezavaField = new TextField();

        Label krajIdLabel = new Label("Kraj ID:");
        ComboBox<String> krajComboBox = new ComboBox<>();
        krajComboBox.setItems(kraji);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            String ime = imeField.getText();
            String kapacitetapotnikov = kapacitetapotnikovField.getText();
            String kapacitetatovora = kapacitetatovoraField.getText();
            String letalskapovezava = letalskapovezavaField.getText();
            String selectedKraj = krajComboBox.getValue();

            if (selectedKraj != null && !selectedKraj.isEmpty()) {
                String krajId = getKrajId(selectedKraj);
                insertLetalisce(ime, kapacitetapotnikov, kapacitetatovora, letalskapovezava, krajId);

                // Clear the fields after submission
                imeField.clear();
                kapacitetapotnikovField.clear();
                kapacitetatovoraField.clear();
                letalskapovezavaField.clear();
                krajComboBox.getSelectionModel().clearSelection();
            } else {
                System.out.println("Please select a kraj.");
            }
        });

        // Layout setup
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        GridPane.setConstraints(imeLabel, 0, 0);
        GridPane.setConstraints(imeField, 1, 0);

        GridPane.setConstraints(kapacitetapotnikovLabel, 0, 1);
        GridPane.setConstraints(kapacitetapotnikovField, 1, 1);

        GridPane.setConstraints(kapacitetatovoraLabel, 0, 2);
        GridPane.setConstraints(kapacitetatovoraField, 1, 2);

        GridPane.setConstraints(letalskapovezavaLabel, 0, 3);
        GridPane.setConstraints(letalskapovezavaField, 1, 3);

        GridPane.setConstraints(krajIdLabel, 0, 4);
        GridPane.setConstraints(krajComboBox, 1, 4);

        GridPane.setConstraints(submitButton, 1, 5);

        grid.getChildren().addAll(
                imeLabel, imeField,
                kapacitetapotnikovLabel, kapacitetapotnikovField,
                kapacitetatovoraLabel, kapacitetatovoraField,
                letalskapovezavaLabel, letalskapovezavaField,
                krajIdLabel, krajComboBox,
                submitButton
        );

        VBox layout = new VBox(10);
        layout.getChildren().addAll(grid);
        Scene scene = new Scene(layout, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();

        fetchKrajiFromDatabase();
    }

    private void fetchKrajiFromDatabase() {
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM kraji")) {

            while (resultSet.next()) {
                kraji.add(resultSet.getString("ime"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private String getKrajId(String krajName) {
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM kraji WHERE ime = ?")) {
            preparedStatement.setString(1, krajName);
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

    private void insertLetalisce(String ime, String kapacitetapotnikov, String kapacitetatovora, String letalskapovezava, String krajId) {
        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement statement = connection.prepareCall("{ ? = call insert_airport(?, ?, ?, ?, ?) }")) {

            statement.registerOutParameter(1, Types.INTEGER);
            statement.setString(2, ime);
            statement.setInt(3, Integer.parseInt(kapacitetapotnikov)); // Parse as integer
            statement.setInt(4, Integer.parseInt(kapacitetatovora)); // Parse as integer
            statement.setString(5, letalskapovezava);
            statement.setInt(6, Integer.parseInt(krajId)); // Parse as integer
            statement.execute();

            int airportId = statement.getInt(1);
            if (airportId > 0) {
                System.out.println("Letalisce inserted successfully!");
                // Close the insertLetalisce window
                stage.close();
                // Open the LetaliscaInfo window
                LetaliscaInfo letaliscaInfo = new LetaliscaInfo();
                letaliscaInfo.start(new Stage());
            } else {
                System.out.println("Failed to insert letalisce.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}
