import java.sql.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LetalskeDruzbeInfo extends Application {
    private static final String PGHOST = "ep-cool-sea-a2dj5p9s.eu-central-1.aws.neon.tech";
    private static final String PGDATABASE = "europort2";
    private static final String PGUSER = "kris.kajtna";
    private static final String PGPASSWORD = "gk3F9qeiwtXD";
    private static final String URL = "jdbc:postgresql://" + PGHOST + "/" + PGDATABASE;
    private TableView<String[]> table;
    private ObservableList<String[]> data;

    public static void main(String[] args) {
        LetalskeDruzbeInfo letalskeDruzbeInfo = new LetalskeDruzbeInfo();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.table = new TableView();
        this.data = FXCollections.observableArrayList();
        TableColumn<String[], String> imeColumn = new TableColumn("Ime");
        imeColumn.setCellValueFactory((data) -> {
            return new SimpleStringProperty(((String[])data.getValue())[0]);
        });
        TableColumn<String[], String> kraticaColumn = new TableColumn("Kratica");
        kraticaColumn.setCellValueFactory((data) -> {
            return new SimpleStringProperty(((String[])data.getValue())[1]);
        });
        TableColumn<String[], String> letaliscaIdColumn = new TableColumn("Letalisca ID");
        letaliscaIdColumn.setCellValueFactory((data) -> {
            return new SimpleStringProperty(((String[])data.getValue())[2]);
        });
        TableColumn<String[], String> userIdColumn = new TableColumn("User ID");
        userIdColumn.setCellValueFactory((data) -> {
            return new SimpleStringProperty(((String[])data.getValue())[3]);
        });

        // Enable editing of table cells
        imeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        imeColumn.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            ((String[]) event.getTableView().getItems().get(event.getTablePosition().getRow()))[0] = newValue;
        });

        kraticaColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        kraticaColumn.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            ((String[]) event.getTableView().getItems().get(event.getTablePosition().getRow()))[1] = newValue;
        });

        letaliscaIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        letaliscaIdColumn.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            ((String[]) event.getTableView().getItems().get(event.getTablePosition().getRow()))[2] = newValue;
        });

        userIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userIdColumn.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            ((String[]) event.getTableView().getItems().get(event.getTablePosition().getRow()))[3] = newValue;
        });

        // Add update and delete buttons to each row
        TableColumn<String[], Void> updateColumn = new TableColumn<>("Update");
        updateColumn.setCellFactory(param -> new TableCell<String[], Void>() {
            private final Button updateButton = new Button("Update");

            {
                // Inside the updateColumn setCellFactory method
                updateButton.setOnAction(event -> {
                    String[] rowData = getTableView().getItems().get(getIndex());
                    int airlineId = getAirlineId(rowData[0], rowData[1]); // Assuming the first two elements represent the name and acronym
                    updateRow(rowData, airlineId);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(updateButton);
                }
            }

        });

        TableColumn<String[], Void> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setCellFactory(param -> new TableCell<String[], Void>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    String[] rowData = getTableView().getItems().get(getIndex());
                    deleteRow(rowData);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        this.table.getColumns().addAll(imeColumn, kraticaColumn, letaliscaIdColumn, userIdColumn, updateColumn, deleteColumn);

        try {
            Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM letalskedruzbe");

            while(resultSet.next()) {
                String[] row = new String[]{resultSet.getString("ime"), resultSet.getString("kratica"), resultSet.getString("letalisca_id"), resultSet.getString("user_id")};
                this.data.add(row);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException var13) {
            var13.printStackTrace();
        }

        this.table.setItems(this.data);
        this.table.setEditable(true);

        Button insertButton = new Button("Insert");
        insertButton.setOnAction(event -> insertRow());


        Button airportsButton = new Button("Letalisca");
        airportsButton.setOnAction((event) -> {
            // Add code for handling airports button click event
        });

        HBox buttonBox = new HBox(10.0, insertButton, airportsButton);
        buttonBox.setPadding(new javafx.geometry.Insets(10.0));
        HBox.setHgrow(airportsButton, Priority.ALWAYS);

        VBox layout = new VBox(10.0, this.table, buttonBox);
        layout.setPadding(new javafx.geometry.Insets(10.0));
        Scene scene = new Scene(layout, 700.0, 450.0);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Izpis Tabele Letalskih Družb");
        primaryStage.show();
    }

    private void insertRow() {
        Platform.runLater(() -> {
            Stage insertStage = new Stage();
            insertLetalskeDruzbe insertLetalskeDruzbe = new insertLetalskeDruzbe();
            insertLetalskeDruzbe.setLetalskeDruzbeInfo(this);
            insertLetalskeDruzbe.start(insertStage);
        });
    }

    private int getAirlineId(String ime, String kratica) {
        int id = -1; // Default value if no airline is found
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD)) {
            String sql = "SELECT id FROM letalskedruzbe WHERE ime=? AND kratica=?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, ime);
                statement.setString(2, kratica);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    id = resultSet.getInt("id");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return id;
    }


    private void updateRow(String[] rowData, int airlineId) {
        Stage updateStage = new Stage();
        updateLetalskeDruzbe updateLetalskeDruzbe = new updateLetalskeDruzbe(rowData, airlineId, this); // Pass 'this' as the third parameter
        updateLetalskeDruzbe.setOnUpdateListener(updatedData -> {
            refreshTable();
            updateStage.close();
        });
        updateLetalskeDruzbe.start(updateStage);
    }


    private void deleteRow(String[] rowData) {
        int airlineId = getAirlineId(rowData[0], rowData[1]); // Get the airline ID using the name and acronym
        if (airlineId != -1) { // Check if airline ID is valid
            try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD)) {
                String deleteSql = "DELETE FROM letalskedruzbe WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteSql)) {
                    statement.setInt(1, airlineId);
                    int affectedRows = statement.executeUpdate();
                    if (affectedRows == 1) {
                        System.out.println("Delete successful.");
                        refreshTable();
                    } else {
                        System.out.println("Delete failed.");
                    }
                }
            } catch (SQLException ex) {
                System.out.println("Error deleting row: " + ex.getMessage());
            }
        } else {
            System.out.println("Invalid airline ID. Delete failed.");
        }
    }


    public void refreshTable() {
        this.data.clear();

        try {
            Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM letalskedruzbe");

            while(resultSet.next()) {
                String[] row = new String[]{resultSet.getString("ime"), resultSet.getString("kratica"), resultSet.getString("letalisca_id"), resultSet.getString("user_id")};
                this.data.add(row);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException var5) {
            var5.printStackTrace();
        }

    }
}
