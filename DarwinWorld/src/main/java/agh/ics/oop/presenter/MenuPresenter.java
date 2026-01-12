package agh.ics.oop.presenter;

import agh.ics.oop.simulation.ConsoleDebugListener;
import agh.ics.oop.simulation.ConsoleStatsListener;
import agh.ics.oop.simulation.FireSimulation;
import agh.ics.oop.simulation.Simulation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MenuPresenter {
    @FXML private Label infoLabel;

    @FXML private TextField widthField;
    @FXML private TextField heightField;
    @FXML private TextField startPlantCountField;
    @FXML private TextField plantsPerDayField;
    @FXML private TextField startAnimalCountField;
    @FXML private TextField startAnimalEnergyField;
    @FXML private TextField energyToReproduceField;
    @FXML private TextField energyLossPerDayField;
    @FXML private TextField energyToChildField;
    @FXML private TextField minMutationsField;
    @FXML private TextField maxMutationsField;
    @FXML private TextField plantEnergyField;
    @FXML private TextField genomeLengthField;

    @FXML private CheckBox fireEnabledCheckbox;
    @FXML private GridPane fireOptionsPane;
    @FXML private TextField fireChanceField;
    @FXML private TextField fireDurationField;
    @FXML private TextField fireLossField;

    private int getInt(TextField field, int def) {
        try { return Integer.parseInt(field.getText().trim()); }
        catch (Exception e) { return def; }
    }

    private Simulation.Config createSimConfig() {
        return new Simulation.Config(
                getInt(widthField, 50),
                getInt(heightField, 30),
                getInt(startPlantCountField, 120),
                getInt(plantsPerDayField, 10),
                getInt(startAnimalCountField, 20),
                getInt(startAnimalEnergyField, 20),
                getInt(energyToReproduceField, 30),
                getInt(energyLossPerDayField, 1),
                getInt(energyToChildField, 20),
                getInt(minMutationsField, 0),
                getInt(maxMutationsField, 0),
                getInt(plantEnergyField, 10),
                getInt(genomeLengthField, 8)
        );
    }

    private FireSimulation.Config createFireSimConfig() {
        return new FireSimulation.Config(
                createSimConfig(),
                getInt(fireChanceField, 5),
                getInt(fireDurationField, 3),
                getInt(fireLossField, 2)
        );
    }

    private Simulation createSimulation() {
        if (fireEnabledCheckbox.isSelected()) {
            return new FireSimulation(createFireSimConfig());
        }
        return new Simulation(createSimConfig());
    }

    @FXML
    private void initialize() {
        fireOptionsPane.setDisable(!fireEnabledCheckbox.isSelected());
        fireEnabledCheckbox.selectedProperty().addListener((obs, oldV, enabled) ->
                fireOptionsPane.setDisable(!enabled)
        );
    }

    public void onStartSimulation() {
        try {
            final var sim = createSimulation();

            sim.addListener(new ConsoleStatsListener());
            sim.addListener(new ConsoleDebugListener());

            final var loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
            final Parent root = loader.load();
            final SimulationPresenter presenter = loader.getController();

            final var stage = new Stage();
            stage.setTitle("Simulation");
            stage.setScene(new Scene(root));
            stage.show();

            stage.setOnCloseRequest(ev -> presenter.stopSimulation(sim));
            presenter.startSimulation(sim);
        } catch (Exception e) {
            final var msg = "Start simulation not done: " + e;
            System.err.println(msg);
            infoLabel.setText(msg);
        }
    }
}
