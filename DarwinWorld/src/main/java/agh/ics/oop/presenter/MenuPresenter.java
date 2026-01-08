package agh.ics.oop.presenter;

import agh.ics.oop.OptionsParser;
import agh.ics.oop.Simulation;
import agh.ics.oop.model.GrassField;
import agh.ics.oop.model.MoveDirection;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.WorldMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MenuPresenter {
    @FXML
    private Label infoLabel;
    @FXML
    private TextField moveInput;

    public void onSimulationStartClicked() {
        try {
            final var loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
            final Parent root = loader.load();
            final SimulationPresenter simulationController = loader.getController();
            final var simulationStage = new Stage();
            simulationStage.setTitle("Symulacja");
            simulationStage.setScene(new Scene(root));
            simulationStage.show();

            final var map = new WorldMap(5);
            simulationController.setWorldMap(map);
            final var positions = List.of(
                    new Vector2d(6, 7),
                    new Vector2d(1, 1),
                    new Vector2d(5, 3)
            );
            final var textDir = moveInput.getText().strip().split(" ");
            final var directions = (!(textDir.length == 1 && textDir[0].equals("")))
                    ? OptionsParser.parse(textDir)
                    : new ArrayList<MoveDirection>();
            final var sim = new Simulation(map, positions, directions);
            final var engine = SimulationEngine.getGlobalEngine();
            engine.addSimulation(sim);
            engine.runAsyncInThreadPool();
        } catch (Exception e) {
            final var msg = "Uruchomienie symulacji nieudane: " + e;
            System.err.println(msg);
            infoLabel.setText(msg);
        }
    }
}
