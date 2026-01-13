package agh.ics.oop.presenter;

import javafx.scene.control.TextField;

public class InputParser {
    public static int parseOrDefault(TextField field, int defaultValue) {
        if (field == null || field.getText().trim().isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
