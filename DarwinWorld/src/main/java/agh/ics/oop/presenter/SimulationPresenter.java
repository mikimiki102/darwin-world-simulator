package agh.ics.oop.presenter;

import agh.ics.oop.model.WorldElement;
import agh.ics.oop.model.WorldMap;
import agh.ics.oop.simulation.Simulation;
import agh.ics.oop.simulation.SimulationChangeListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class SimulationPresenter implements SimulationChangeListener {
    private static final int BORDER_WIDTH = 3;
    private static final int CELL_SIZE = 40 + BORDER_WIDTH / 2;
    private static final int FONT_SIZE = CELL_SIZE / 2;

    private Simulation simulation;
    @FXML
    private Canvas canvas;
    @FXML
    private Button pauseButton;

    private static void clearCanvas(GraphicsContext context, Canvas canvas, int width, int height) {
        canvas.setWidth((width + 1) * CELL_SIZE);
        canvas.setHeight((height + 1) * CELL_SIZE);
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private static void configureFont(GraphicsContext context, int size, Color color) {
        context.setTextAlign(TextAlignment.CENTER);
        context.setTextBaseline(VPos.CENTER);
        context.setFont(new Font("Monospace Regular", size));
        context.setFill(color);
    }

    private static void drawOnGrid(GraphicsContext context, String text, int x, int y) {
        if (x < -1 || y < -1) {
            System.err.println("Cannot draw outside the grid. x = " + x + ", y = " + y);
            return;
        }
        x += 1;
        y += 1;
        context.fillText(text, x * CELL_SIZE + CELL_SIZE / 2, y * CELL_SIZE + CELL_SIZE / 2);
    }

    private static void drawOnGrid(GraphicsContext context, int num, int x, int y) {
        drawOnGrid(context, Integer.toString(num), x, y);
    }

    private static void drawOnGrid(GraphicsContext context, WorldElement element) {
        final var position = element.getPosition();
        drawOnGrid(context, element.toString(), position.x(), position.y());
    }

    private static void drawGrid(GraphicsContext context, int width, int height) {
        context.setStroke(Color.BLACK);
        context.setLineWidth(BORDER_WIDTH);

        context.strokeLine(0, 0, 0, (height + 1) * CELL_SIZE);
        context.strokeLine(0, 0, (width + 1) * CELL_SIZE, 0);
        drawOnGrid(context, "y\\x", -1, -1);

        for (int x = 0; x < width; x++) {
            context.strokeLine((x + 1) * CELL_SIZE, 0, (x + 1) * CELL_SIZE, (height + 1) * CELL_SIZE);
            drawOnGrid(context, x, x, -1);
        }
        context.strokeLine((width + 1) * CELL_SIZE, 0, (width + 1) * CELL_SIZE, (height + 1) * CELL_SIZE);

        for (int y = 0; y < height; y++) {
            context.strokeLine(0, (y + 1) * CELL_SIZE, (width + 1) * CELL_SIZE, (y + 1) * CELL_SIZE);
            drawOnGrid(context, height - 1 - y, -1, y);
        }
        context.strokeLine(0, (height + 1) * CELL_SIZE, (width + 1) * CELL_SIZE, (height + 1) * CELL_SIZE);

    }

    private static void drawElements(GraphicsContext context, WorldMap map) {
        map.getAnimalsGroupedNSorted().forEach(cell -> {
            final var position = cell.first();
            final int numOfAnimals = cell.second().size();
            if (numOfAnimals > 1) {
                drawOnGrid(context, numOfAnimals, position.x(), position.y());
            } else {
                drawOnGrid(context, cell.second().getFirst());
            }
        });
        map.getPlants().forEach(plant -> {
            if (!map.isAnimalAt(plant.getPosition())) {
                drawOnGrid(context, plant);
            }
        });
    }

    @FXML
    private void onPauseButtonClicked() {
        if (simulation.isRunning()) {
            simulation.stop();
            pauseButton.setText("Wznów");
        } else {
            simulation.start();
            pauseButton.setText("Wstrzymaj");
        }
    }

    private void drawMap(WorldMap map) {
        final var context = canvas.getGraphicsContext2D();
        final int width = map.getWidth();
        final int height = map.getHeight();
        clearCanvas(context, canvas, width, height);
        configureFont(context, FONT_SIZE, Color.BLACK);
        drawGrid(context, width, height);
        drawElements(context, map);
    }

    @Override
    public void onSimulationChanged(Simulation simulation) {
        Platform.runLater(() -> {
            drawMap(simulation.getMap());
        });
    }

    public void startSimulation(Simulation sim) {
        this.simulation = sim;
        sim.addListener(this);

        sim.init();
        sim.start();
    }

    public void stopSimulation(Simulation sim) {
        sim.stop();
    }
}
