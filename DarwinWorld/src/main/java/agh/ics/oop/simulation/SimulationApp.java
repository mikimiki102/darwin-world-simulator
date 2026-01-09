package agh.ics.oop.simulation;

import agh.ics.oop.presenter.MenuPresenter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class SimulationApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        final var loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("menu.fxml"));

        final VBox viewRoot = loader.load();
        final MenuPresenter presenter = loader.getController();

        final var scene = new Scene(viewRoot);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation app");
        primaryStage.minWidthProperty().bind(viewRoot.minWidthProperty());
        primaryStage.minHeightProperty().bind(viewRoot.minHeightProperty());
        primaryStage.show();
    }
}
