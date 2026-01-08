package agh.ics.oop.presenter;

import agh.ics.oop.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class SimulationPresenter implements MapChangeListener {
    private static final int BORDER_WIDTH = 3;
    private static final int CELL_SIZE = 40 + BORDER_WIDTH / 2;
    private static final int FONT_SIZE = CELL_SIZE / 2;

    private WorldMap worldMap;
    @FXML
    private Label moveInfoLabel;
    @FXML
    private Canvas canvas;

    public void setWorldMap(WorldMap map) {
        if (worldMap != null) worldMap.unregisterListener(this);
        worldMap = map;
        if (worldMap != null) worldMap.registerListener(this);
    }

    private static void clearCanvas(GraphicsContext context, Canvas canvas, Boundary bounds) {
        canvas.setWidth((bounds.width() + 1) * CELL_SIZE);
        canvas.setHeight((bounds.height() + 1) * CELL_SIZE);
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
        // grid is offset by -1 (-1 is for axis)
        x += 1;
        y += 1;
        context.fillText(text, x * CELL_SIZE + CELL_SIZE / 2, y * CELL_SIZE + CELL_SIZE / 2);
    }

    private static void drawOnGrid(GraphicsContext context, int num, int x, int y) {
        drawOnGrid(context, Integer.toString(num), x, y);
    }

    private static void drawOnGrid(GraphicsContext context, WorldElement element, Vector2d gridPosition) {
        drawOnGrid(context, element.toString(), gridPosition.x(), gridPosition.y());
    }

    private static void drawGrid(GraphicsContext context, Boundary bounds) {
        context.setStroke(Color.BLACK);
        context.setLineWidth(BORDER_WIDTH);

        context.strokeLine(0, 0, 0, (bounds.height() + 1) * CELL_SIZE);
        context.strokeLine(0, 0, (bounds.width() + 1) * CELL_SIZE, 0);
        drawOnGrid(context, "y\\x", -1, -1);

        final var width = bounds.width();
        final var height = bounds.height();

        for (int x = 0; x < width; x++) {
            context.strokeLine((x + 1) * CELL_SIZE, 0, (x + 1) * CELL_SIZE, (height + 1) * CELL_SIZE);
            drawOnGrid(context, bounds.lowerLeft().x() + x, x, -1);
        }
        context.strokeLine((width + 1) * CELL_SIZE, 0, (width + 1) * CELL_SIZE, (height + 1) * CELL_SIZE);

        for (int y = 0; y < bounds.height(); y++) {
            context.strokeLine(0, (y + 1) * CELL_SIZE, (bounds.width() + 1) * CELL_SIZE, (y + 1) * CELL_SIZE);
            drawOnGrid(context, bounds.upperRight().y() - y, -1, y);
        }
        context.strokeLine(0, (height + 1) * CELL_SIZE, (width + 1) * CELL_SIZE, (height + 1) * CELL_SIZE);

    }

    private static void drawElements(GraphicsContext context, WorldMap map) {
        final var bounds = map.getCurrentBounds();
        for (final var e : map.getElements()) {
            final var worldPos = e.getPosition();

            // Draw only elements at the "top" (not grass if animal is also present)
            if (e.equals(map.objectAt(worldPos))) {
                final var gridPos = worldPos.toGridPosition(bounds);
                drawOnGrid(context, e, gridPos);
            }
        }
    }

    private void drawMap(WorldMap map) {
        final var context = canvas.getGraphicsContext2D();
        clearCanvas(context, canvas, map.getCurrentBounds());
        configureFont(context, FONT_SIZE, Color.BLACK);
        drawGrid(context, map.getCurrentBounds());
        drawElements(context, map);
    }

    @Override
    public void mapChanged(WorldMap map, String message) {
        Platform.runLater(() -> {
            drawMap(map);
            moveInfoLabel.setText(message);
        });
    }
}
