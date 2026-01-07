package agh.ics.oop;

import agh.ics.oop.presenter.MenuPresenter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class SimulationApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        final var loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("menu.fxml"));

        final BorderPane viewRoot = loader.load();
        final MenuPresenter presenter = loader.getController();

        final var scene = new Scene(viewRoot);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Simulation app");
        primaryStage.minWidthProperty().bind(viewRoot.minWidthProperty());
        primaryStage.minHeightProperty().bind(viewRoot.minHeightProperty());
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        final var engine = SimulationEngine.getGlobalEngine();
        engine.setTimeout(1, TimeUnit.SECONDS);
        engine.awaitSimulationEnd();
        super.stop();
    }
}
