import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class updateLetalisca {
    interface UpdateListener {
        void onUpdate(String[] updatedData);
    }

    private UpdateListener listener;
    private String[] rowData;
    private static final String PGHOST = "ep-cool-sea-a2dj5p9s.eu-central-1.aws.neon.tech";
    private static final String PGDATABASE = "europort2";
    private static final String PGUSER = "kris.kajtna";
    private static final String PGPASSWORD = "gk3F9qeiwtXD";
    private static final String URL = "jdbc:postgresql://" + PGHOST + "/" + PGDATABASE;
    private LetaliscaInfo letaliscaInfo;

    public updateLetalisca(String[] rowData, LetaliscaInfo letaliscaInfo) {
        this.rowData = rowData;
        this.letaliscaInfo = letaliscaInfo;
    }

    public void start(Stage updateStage) {
        updateStage.setTitle("Update Letalisca");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label imeLabel = new Label("Ime:");
        GridPane.setConstraints(imeLabel, 0, 0);
        TextField imeField = new TextField(rowData[0]);
        GridPane.setConstraints(imeField, 1, 0);

        Label kapacitetaPotnikovLabel = new Label("Kapaciteta Potnikov:");
        GridPane.setConstraints(kapacitetaPotnikovLabel, 0, 1);
        TextField kapacitetaPotnikovField = new TextField(rowData[1]);
        GridPane.setConstraints(kapacitetaPotnikovField, 1, 1);

        Label kapacitetaTovoraLabel = new Label("Kapaciteta Tovora:");
        GridPane.setConstraints(kapacitetaTovoraLabel, 0, 2);
        TextField kapacitetaTovoraField = new TextField(rowData[2]);
        GridPane.setConstraints(kapacitetaTovoraField, 1, 2);

        Label letalskaPovezavaLabel = new Label("Letalska Povezava:");
        GridPane.setConstraints(letalskaPovezavaLabel, 0, 3);
        TextField letalskaPovezavaField = new TextField(rowData[3]);
        GridPane.setConstraints(letalskaPovezavaField, 1, 3);

        Label krajIdLabel = new Label("Kraj:");
        GridPane.setConstraints(krajIdLabel, 0, 4);
        ComboBox<String> krajIdComboBox = new ComboBox<>();
        GridPane.setConstraints(krajIdComboBox, 1, 4);

        loadKraji(krajIdComboBox);

        Button updateButton = new Button("Update");
        GridPane.setConstraints(updateButton, 1, 5);
        updateButton.setOnAction(e -> {
            // Update the rowData array with the new values
            rowData[0] = imeField.getText();
            rowData[1] = kapacitetaPotnikovField.getText();
            rowData[2] = kapacitetaTovoraField.getText();
            rowData[3] = letalskaPovezavaField.getText();
            rowData[4] = krajIdComboBox.getValue().split(":")[0]; // Extract Kraj ID from selected value

            // Invoke the listener to notify the main application with updated data
            if (listener != null) {
                listener.onUpdate(rowData);
            }

            // Update the data in the database
            updateDatabase(rowData);
        });

        grid.getChildren().addAll(
                imeLabel, imeField,
                kapacitetaPotnikovLabel, kapacitetaPotnikovField,
                kapacitetaTovoraLabel, kapacitetaTovoraField,
                letalskaPovezavaLabel, letalskaPovezavaField,
                krajIdLabel, krajIdComboBox,
                updateButton);

        Scene scene = new Scene(grid, 400, 250);
        updateStage.setScene(scene);
        updateStage.show();
    }

    public void setOnUpdateListener(UpdateListener listener) {
        this.listener = listener;
    }

    private void updateDatabase(String[] rowData) {
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD)) {
            String sql = "UPDATE letalisca SET ime=?, kapacitetapotnikov=?, kapacitetatovora=?, letalskapovezava=?, kraj_id=? WHERE id=?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, rowData[0]); // Name
                statement.setInt(2, Integer.parseInt(rowData[1])); // Convert to integer
                statement.setInt(3, Integer.parseInt(rowData[2])); // Convert to integer
                statement.setString(4, rowData[3]); // Letalska Povezava
                statement.setInt(5, Integer.parseInt(rowData[4])); // Convert to integer
                statement.setInt(6, Integer.parseInt(getIdAsString(rowData[0], Integer.parseInt(rowData[1]), Integer.parseInt(rowData[2])))); // Convert to integer
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 1) {
                    System.out.println("Update successful.");
                    letaliscaInfo.refreshTable();
                } else {
                    System.out.println("Update failed.");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error updating database: " + ex.getMessage());
        }
    }

    private String getIdAsString(String ime, int kapacitetaPotnikov, int kapacitetaTovora) {
        int id = letaliscaInfo.getId(ime, kapacitetaPotnikov, kapacitetaTovora);
        return String.valueOf(id);
    }

    private void loadKraji(ComboBox<String> krajIdComboBox) {
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD)) {
            String sql = "SELECT id, ime FROM kraji";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                ObservableList<String> krajiList = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String ime = resultSet.getString("ime");
                    krajiList.add(id + ": " + ime);
                }
                krajIdComboBox.setItems(krajiList);
                // Set default value based on existing data
                for (String item : krajiList) {
                    if (item.startsWith(rowData[4] + ":")) {
                        krajIdComboBox.setValue(item);
                        break;
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error loading kraji: " + ex.getMessage());
        }
    }
}
