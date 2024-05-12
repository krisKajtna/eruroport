import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class updateLetalisca {
    // Define the UpdateListener interface
    interface UpdateListener {
        void onUpdate();
    }

    private UpdateListener listener;
    private String[] rowData;

    public updateLetalisca(String[] rowData) {
        this.rowData = rowData;
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

        Label krajIdLabel = new Label("Kraj ID:");
        GridPane.setConstraints(krajIdLabel, 0, 4);
        TextField krajIdField = new TextField(rowData[4]);
        GridPane.setConstraints(krajIdField, 1, 4);

        Button updateButton = new Button("Update");
        GridPane.setConstraints(updateButton, 1, 5);
        updateButton.setOnAction(e -> {
            // Update the rowData array with the new values
            rowData[0] = imeField.getText();
            rowData[1] = kapacitetaPotnikovField.getText();
            rowData[2] = kapacitetaTovoraField.getText();
            rowData[3] = letalskaPovezavaField.getText();
            rowData[4] = krajIdField.getText();

            // Invoke the listener to notify the main application
            invokeListener();
        });

        grid.getChildren().addAll(
                imeLabel, imeField,
                kapacitetaPotnikovLabel, kapacitetaPotnikovField,
                kapacitetaTovoraLabel, kapacitetaTovoraField,
                letalskaPovezavaLabel, letalskaPovezavaField,
                krajIdLabel, krajIdField,
                updateButton);

        Scene scene = new Scene(grid, 400, 250);
        updateStage.setScene(scene);
        updateStage.show();
    }

    public void setOnUpdateListener(UpdateListener listener) {
        this.listener = listener;
    }

    private void invokeListener() {
        if (listener != null) {
            listener.onUpdate();
        }
    }
}