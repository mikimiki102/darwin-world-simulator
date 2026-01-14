package agh.ics.oop.presenter;

import agh.ics.oop.model.WorldMap;
import agh.ics.oop.simulation.FireSimulation;
import agh.ics.oop.simulation.Simulation;
import agh.ics.oop.simulation.SimulationChangeListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SimulationPresenter implements SimulationChangeListener {
    private static final int BORDER_WIDTH = 2;
    private static final int MIN_CELL = 10;
    private static final int MAX_CELL = 50;
    private static final int MAX_POINTS = 300;

    private Simulation simulation;

    @FXML private Canvas canvas;

    @FXML private Button pauseButton;
    @FXML private Button stepBackButton;
    @FXML private Button stepForwardButton;

    @FXML private Label dayLabel;
    @FXML private Label animalsLabel;
    @FXML private Label plantsLabel;
    @FXML private Label freeFieldsLabel;
    @FXML private Label avgEnergyLabel;
    @FXML private Label avgLifeLabel;
    @FXML private Label avgKidsLabel;
    @FXML private Label popularGenotypesLabel;
    @FXML private Label fireLabel;

    @FXML private ComboBox<String> chartChoice;
    @FXML private LineChart<Number, Number> statsChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;

    private final XYChart.Series<Number, Number> series = new XYChart.Series<>();
    private String currentMetric = "avgEnergy";

    @FXML
    public void initialize() {
        if (chartChoice != null) {
            chartChoice.getItems().addAll(
                    "Śr. energia (żywe)",
                    "Śr. długość życia (martwe)",
                    "Liczba zwierzaków",
                    "Liczba roślin",
                    "Wolne pola",
                    "Śr. dzieci (żywe)"
            );
            chartChoice.getSelectionModel().select(0);
            chartChoice.setOnAction(e -> {
                int idx = chartChoice.getSelectionModel().getSelectedIndex();
                currentMetric = switch (idx) {
                    case 1 -> "avgLife";
                    case 2 -> "animals";
                    case 3 -> "plants";
                    case 4 -> "free";
                    case 5 -> "avgKids";
                    default -> "avgEnergy";
                };
                series.getData().clear();
            });
        }

        if (statsChart != null) {
            statsChart.getData().clear();
            series.setName("statystyka");
            statsChart.getData().add(series);
        }
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

    @FXML
    private void onStepForwardClicked() {
        if (simulation.isRunning()) simulation.stop();
        pauseButton.setText("Wznów");
        simulation.stepForward();
    }

    @FXML
    private void onStepBackClicked() {
        if (simulation.isRunning()) simulation.stop();
        pauseButton.setText("Wznów");
        simulation.stepBack();
    }

    private int cellSize(WorldMap map) {
        int maxDim = Math.max(map.getWidth(), map.getHeight());
        int s = (int) Math.round(800.0 / (maxDim + 1));
        if (s < MIN_CELL) s = MIN_CELL;
        if (s > MAX_CELL) s = MAX_CELL;
        return s;
    }

    private int fontSize(int cell) {
        return Math.max(8, cell / 2);
    }

    private static void configureFont(GraphicsContext context, int size, Color color) {
        context.setTextAlign(TextAlignment.CENTER);
        context.setTextBaseline(VPos.CENTER);
        context.setFont(new Font("Monospace Regular", size));
        context.setFill(color);
    }

    private void clearCanvas(GraphicsContext context, Canvas canvas, int width, int height, int cell) {
        canvas.setWidth((width + 1) * cell);
        canvas.setHeight((height + 1) * cell);
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawOnGrid(GraphicsContext context, String text, int x, int y, int cell) {
        x += 1;
        y += 1;
        context.fillText(text, x * cell + cell / 2.0, y * cell + cell / 2.0);
    }

    private void drawGrid(GraphicsContext context, int width, int height, int cell) {
        context.setStroke(Color.BLACK);
        context.setLineWidth(BORDER_WIDTH);

        context.strokeLine(0, 0, 0, (height + 1) * cell);
        context.strokeLine(0, 0, (width + 1) * cell, 0);
        drawOnGrid(context, "y\\x", -1, -1, cell);

        for (int x = 0; x < width; x++) {
            context.strokeLine((x + 1) * cell, 0, (x + 1) * cell, (height + 1) * cell);
            drawOnGrid(context, Integer.toString(x), x, -1, cell);
        }
        context.strokeLine((width + 1) * cell, 0, (width + 1) * cell, (height + 1) * cell);

        for (int y = 0; y < height; y++) {
            context.strokeLine(0, (y + 1) * cell, (width + 1) * cell, (y + 1) * cell);
            drawOnGrid(context, Integer.toString(height - 1 - y), -1, y, cell);
        }
        context.strokeLine(0, (height + 1) * cell, (width + 1) * cell, (height + 1) * cell);
    }

    private void drawEnergyBar(GraphicsContext ctx, int x, int y, int cell, int energy, int maxRef) {
        double ratio = maxRef <= 0 ? 0.0 : Math.min(1.0, Math.max(0.0, (double) energy / maxRef));
        double barH = Math.max(3, cell * 0.12);
        double pad = 2;

        double x0 = (x + 1) * cell + pad;
        double y0 = (y + 1) * cell + cell - barH - pad;

        ctx.setFill(Color.DARKGREEN);
        ctx.fillRect(x0, y0, (cell - 2 * pad) * ratio, barH);
        ctx.setStroke(Color.BLACK);
        ctx.strokeRect((x + 1) * cell + pad, (y + 1) * cell + cell - barH - pad, cell - 2 * pad, barH);
    }

    private void drawElements(GraphicsContext context, WorldMap map, int cell) {
        int maxE = map.getAnimalsFlat().stream().mapToInt(a -> a.getEnergy()).max().orElse(1);

        map.getAnimalsGroupedNSorted().forEach(cellEntry -> {
            final var pos = cellEntry.first();
            final var animals = cellEntry.second();
            final var strongest = animals.get(0);

            drawEnergyBar(context, pos.x(), pos.y(), cell, strongest.getEnergy(), maxE);

            if (animals.size() > 1) {
                drawOnGrid(context, Integer.toString(animals.size()), pos.x(), pos.y(), cell);
            } else {
                drawOnGrid(context, strongest.toString(), pos.x(), pos.y(), cell);
            }
        });

        map.getPlants().forEach(plant -> {
            if (!map.isAnimalAt(plant.getPosition())) {
                drawOnGrid(context, plant.toString(), plant.getPosition().x(), plant.getPosition().y(), cell);
            }
        });
    }

    private void drawMap(WorldMap map) {
        final var context = canvas.getGraphicsContext2D();
        int cell = cellSize(map);
        int font = fontSize(cell);

        clearCanvas(context, canvas, map.getWidth(), map.getHeight(), cell);
        configureFont(context, font, Color.BLACK);
        drawGrid(context, map.getWidth(), map.getHeight(), cell);
        drawElements(context, map, cell);
    }

    private void updateStats(Simulation sim) {
        var s = sim.snapshot();

        dayLabel.setText(Integer.toString(s.day()));
        animalsLabel.setText(Integer.toString(s.animals()));
        plantsLabel.setText(Integer.toString(s.plants()));
        freeFieldsLabel.setText(Integer.toString(s.freeFields()));
        avgEnergyLabel.setText(String.format("%.2f", s.avgEnergyOfLiving()));
        avgLifeLabel.setText(String.format("%.2f", s.avgLifespanOfDead()));
        avgKidsLabel.setText(String.format("%.2f", s.avgChildrenOfLiving()));

        String genos = s.mostPopularGenotypes().isEmpty()
                ? "-"
                : s.mostPopularGenotypes().stream()
                .map(arr -> Arrays.stream(arr).mapToObj(Integer::toString).collect(Collectors.joining("")))
                .collect(Collectors.joining(" | "));
        popularGenotypesLabel.setText(genos);

        if (sim instanceof FireSimulation fs) {
            fireLabel.setText("płonące rośliny=" + fs.getBurningPlantsCount() + ", płonące zwierzaki=" + fs.getBurningAnimalsCount());
        } else {
            fireLabel.setText("-");
        }
    }

    private void updateChart(Simulation sim) {
        var s = sim.snapshot();
        double v = switch (currentMetric) {
            case "avgLife" -> s.avgLifespanOfDead();
            case "animals" -> s.animals();
            case "plants" -> s.plants();
            case "free" -> s.freeFields();
            case "avgKids" -> s.avgChildrenOfLiving();
            default -> s.avgEnergyOfLiving();
        };

        series.getData().add(new XYChart.Data<>(s.day(), v));
        if (series.getData().size() > MAX_POINTS) {
            series.getData().remove(0);
        }
    }

    @Override
    public void onSimulationChanged(Simulation simulation) {
        Platform.runLater(() -> {
            drawMap(simulation.getMap());
            updateStats(simulation);
            updateChart(simulation);
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
