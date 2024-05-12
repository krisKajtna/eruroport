import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class LetalskeDruzbeInfo extends Application {
    private static final String PGHOST = "ep-cool-sea-a2dj5p9s.eu-central-1.aws.neon.tech";
    private static final String PGDATABASE = "europort2";
    private static final String PGUSER = "kris.kajtna";
    private static final String PGPASSWORD = "gk3F9qeiwtXD";
    private static final String URL = "jdbc:postgresql://" + PGHOST + "/" + PGDATABASE;
    private TableView<String[]> table;
    private ObservableList<String[]> data;

    public static void main(String[] args) {
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
        this.table.getColumns().addAll(new TableColumn[]{imeColumn, kraticaColumn, letaliscaIdColumn, userIdColumn});

        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://ep-cool-sea-a2dj5p9s.eu-central-1.aws.neon.tech/europort2", "kris.kajtna", "gk3F9qeiwtXD");
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

        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");
        Button airportsButton = new Button("Letalisca");
        airportsButton.setOnAction((event) -> {
            // Dodaj kodo za obdelavo dogodka za gumb za letalisca
        });

        HBox buttonBox = new HBox(10.0, insertButton, updateButton, deleteButton, airportsButton);
        buttonBox.setPadding(new javafx.geometry.Insets(10.0));
        HBox.setHgrow(airportsButton, Priority.ALWAYS);

        VBox layout = new VBox(10.0, this.table, buttonBox);
        layout.setPadding(new javafx.geometry.Insets(10.0));
        Scene scene = new Scene(layout, 600.0, 400.0);
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
