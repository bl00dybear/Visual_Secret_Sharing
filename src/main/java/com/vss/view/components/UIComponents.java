package main.java.com.vss.view.components;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class UIComponents {
    private static final String DEFAULT_FONT = "DejaVu Sans Mono";
    private static final double DEFAULT_FONT_SIZE = 14;

    UIComponents() {}

    public static Button createButton(String text, EventHandler<ActionEvent> action) {
        Button button = new Button(text);
        button.setFont(Font.font(DEFAULT_FONT, DEFAULT_FONT_SIZE));
        button.setOnAction(action);
        return button;
    }

    public static HBox createLabeledInput(String labelText, TextField textField) {
        Label label = new Label(labelText);
        label.setFont(Font.font(DEFAULT_FONT, DEFAULT_FONT_SIZE));

        textField.setFont(Font.font(DEFAULT_FONT, DEFAULT_FONT_SIZE));

        HBox container = new HBox(10, label, textField);
        container.setAlignment(Pos.CENTER);
        return container;
    }
}
