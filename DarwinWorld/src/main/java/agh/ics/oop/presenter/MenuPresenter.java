package agh.ics.oop.presenter;

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
    @FXML private TextField widthField,
            heightField,
            startPlantsField,
            startAnimalsField,
            startEnergyField,
            startPlantCountField,
            plantsPerDayField,
            startAnimalCountField,
            startAnimalEnergyField,
            energyToReproduceField,
            energyLossPerDayField,
            energyToChildField,
            minMutationsField,
            maxMutationsField,
            plantEnergyField,
            genomeLengthField;

    @FXML private CheckBox fireEnabledCheckbox;
    @FXML private GridPane fireOptionsPane;
    @FXML private TextField fireChanceField, fireDurationField, fireLossField;

    private int getInt(TextField field) {
        return Integer.parseInt(field.getText().trim());
    }

    private Simulation.Config createSimConfig() {
        return new Simulation.Config(
                getInt(widthField),
                getInt(heightField),
                getInt(startPlantCountField),
                getInt(plantsPerDayField),
                getInt(startAnimalCountField),
                getInt(startAnimalEnergyField),
                getInt(energyToReproduceField),
                getInt(energyLossPerDayField),
                getInt(energyToChildField),
                getInt(minMutationsField),
                getInt(maxMutationsField),
                getInt(plantEnergyField),
                getInt(genomeLengthField)
        );
    }

    private FireSimulation.Config createFireSimConfig() {
        return new FireSimulation.Config(
                createSimConfig(),
                getInt(fireChanceField),
                getInt(fireDurationField),
                getInt(fireLossField)
        );
    }

    private Simulation createSimulation() {
        if (fireEnabledCheckbox.isSelected())
            return new FireSimulation(createFireSimConfig());
        return new Simulation(createSimConfig());
    }

    public void onStartSimulation() {
        try {
            final var sim = createSimulation();
            final var loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
            final Parent root = loader.load();
            final SimulationPresenter presenter = loader.getController();
            final var simulationStage = new Stage();
            simulationStage.setTitle("Symulacja");
            simulationStage.setScene(new Scene(root));
            simulationStage.show();
            simulationStage.setOnCloseRequest(event -> {
                presenter.stopSimulation(sim);
            });
            presenter.startSimulation(sim);
        } catch (Exception e) {
            final var msg = "Uruchomienie symulacji nieudane: " + e;
            System.err.println(msg);
            infoLabel.setText(msg);
        }
    }
}
