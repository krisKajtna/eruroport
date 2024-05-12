import java.sql.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LetaliscaInfo extends Application {
    private static final String PGHOST = "ep-cool-sea-a2dj5p9s.eu-central-1.aws.neon.tech";
    private static final String PGDATABASE = "europort2";
    private static final String PGUSER = "kris.kajtna";
    private static final String PGPASSWORD = "gk3F9qeiwtXD";
    private static final String URL = "jdbc:postgresql://" + PGHOST + "/" + PGDATABASE;
    private TableView<String[]> table;
    private ObservableList<String[]> data;

    public LetaliscaInfo() {
    }

    public void start(Stage primaryStage) {
        this.table = new TableView<>();
        this.data = FXCollections.observableArrayList();
        TableColumn<String[], String> imeColumn = new TableColumn<>("Ime");
        imeColumn.setCellValueFactory((data) -> {
            return new SimpleStringProperty(((String[])data.getValue())[0]);
        });
        TableColumn<String[], String> kapacitetaPotnikovColumn = new TableColumn<>("Kapaciteta Potnikov");
        kapacitetaPotnikovColumn.setCellValueFactory((data) -> {
            return new SimpleStringProperty(((String[])data.getValue())[1]);
        });
        TableColumn<String[], String> kapacitetaTovoraColumn = new TableColumn<>("Kapaciteta Tovora");
        kapacitetaTovoraColumn.setCellValueFactory((data) -> {
            return new SimpleStringProperty(((String[])data.getValue())[2]);
        });
        TableColumn<String[], String> letalskaPovezavaColumn = new TableColumn<>("Letalska Povezava");
        letalskaPovezavaColumn.setCellValueFactory((data) -> {
            return new SimpleStringProperty(((String[])data.getValue())[3]);
        });
        TableColumn<String[], String> krajIdColumn = new TableColumn<>("Kraj ID");
        krajIdColumn.setCellValueFactory((data) -> {
            return new SimpleStringProperty(((String[])data.getValue())[4]);
        });
        this.table.getColumns().addAll(imeColumn, kapacitetaPotnikovColumn, kapacitetaTovoraColumn, letalskaPovezavaColumn, krajIdColumn);

        try {
            Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM letalisca");

            while(resultSet.next()) {
                String[] row = new String[]{resultSet.getString("ime"), resultSet.getString("kapacitetapotnikov"), resultSet.getString("kapacitetatovora"), resultSet.getString("letalskapovezava"), resultSet.getString("kraj_id")};
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
        Button airlinesButton = new Button("Letalska Družba");
        airlinesButton.setOnAction((event) -> {
            LetalskeDruzbeInfo letalskeDruzbeInfo = new LetalskeDruzbeInfo();
            Stage stage = new Stage();
            letalskeDruzbeInfo.start(stage);
        });
        HBox buttonBox = new HBox(10.0, insertButton, updateButton, deleteButton, airlinesButton);
        buttonBox.setPadding(new Insets(10.0));
        HBox.setHgrow(airlinesButton, Priority.ALWAYS);
        VBox layout = new VBox(10.0, this.table, buttonBox);
        layout.setPadding(new Insets(10.0));
        Scene scene = new Scene(layout, 600.0, 400.0);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Izpis Tabele Letališč");
        primaryStage.show();
    }

    private void insertRow() {
        Platform.runLater(() -> {
            Stage insertStage = new Stage();
            insertLetalisce insertLetalisce = new insertLetalisce();
            insertLetalisce.setLetaliscaInfo(this);
            insertLetalisce.start(insertStage);
        });
    }

    private void updateRow() {
    }

    private void deleteRow() {
    }

    public void refreshTable() {
        this.data.clear();

        try {
            Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM letalisca");

            while(resultSet.next()) {
                String[] row = new String[]{resultSet.getString("ime"), resultSet.getString("kapacitetapotnikov"), resultSet.getString("kapacitetatovora"), resultSet.getString("letalskapovezava"), resultSet.getString("kraj_id")};
                this.data.add(row);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException var5) {
            var5.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
