import java.sql.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class updateLetalskeDruzbe {
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
    private LetalskeDruzbeInfo letalskeDruzbeInfo;

    private int airlineId;

    public updateLetalskeDruzbe(String[] rowData, int airlineId, LetalskeDruzbeInfo letalskeDruzbeInfo) {
        this.rowData = rowData;
        this.airlineId = airlineId;
        this.letalskeDruzbeInfo = letalskeDruzbeInfo; // Assign the parameter to the field
    }

    public void start(Stage updateStage) {
        updateStage.setTitle("Update Letalske Družbe");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label imeLabel = new Label("Ime:");
        GridPane.setConstraints(imeLabel, 0, 0);
        TextField imeField = new TextField(rowData[0]);
        GridPane.setConstraints(imeField, 1, 0);

        Label kraticaLabel = new Label("Kratica:");
        GridPane.setConstraints(kraticaLabel, 0, 1);
        TextField kraticaField = new TextField(rowData[1]);
        GridPane.setConstraints(kraticaField, 1, 1);

        Label letaliscaIdLabel = new Label("Letalisca ID:");
        GridPane.setConstraints(letaliscaIdLabel, 0, 2);
        ComboBox<String> letaliscaIdDropdown = new ComboBox<>();
        // Pridobitev letališč iz baze in dodajanje v padajoči seznam
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD)) {
            String sql = "SELECT id, ime FROM letalisca";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    letaliscaIdDropdown.getItems().add(resultSet.getString("id") + ": " + resultSet.getString("ime"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error fetching airports: " + ex.getMessage());
        }
        GridPane.setConstraints(letaliscaIdDropdown, 1, 2);

        Button updateButton = new Button("Update");
        GridPane.setConstraints(updateButton, 1, 4);
        updateButton.setOnAction(e -> {
            // Update the rowData array with the new values
            rowData[0] = imeField.getText();
            rowData[1] = kraticaField.getText();
            // Pridobitev id-ja iz izbranega letališča v ComboBoxu
            String selectedAirport = letaliscaIdDropdown.getValue();
            String[] parts = selectedAirport.split(":");
            if (parts.length == 2) {
                rowData[2] = parts[0].trim(); // Id letališča
            }
            // Invoke the listener to notify the main application with updated data
            if (listener != null) {
                listener.onUpdate(rowData);
            }
            // Update the data in the database
            updateDatabase(rowData);
        });

        grid.getChildren().addAll(
                imeLabel, imeField,
                kraticaLabel, kraticaField,
                letaliscaIdLabel, letaliscaIdDropdown,
                updateButton);

        Scene scene = new Scene(grid, 400, 250);
        updateStage.setScene(scene);
        updateStage.show();
    }

    private void updateDatabase(String[] rowData) {
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD)) {
            // Posodobitev letalske družbe
            String updateSql = "UPDATE letalskedruzbe SET ime=?, kratica=?, letalisca_id=? WHERE id=?";
            try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
                statement.setString(1, rowData[0]); // Ime
                statement.setString(2, rowData[1]); // Kratica
                statement.setInt(3, Integer.parseInt(rowData[2])); // Letalisce ID (pretvorba v celo število)
                statement.setInt(4, airlineId); // ID letalske družbe
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 1) {
                    System.out.println("Update successful.");
                    letalskeDruzbeInfo.refreshTable();

                } else {
                    System.out.println("Update failed.");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error updating database: " + ex.getMessage());
        }
    }

    public void setOnUpdateListener(UpdateListener listener) {
        this.listener = listener;
    }
}
