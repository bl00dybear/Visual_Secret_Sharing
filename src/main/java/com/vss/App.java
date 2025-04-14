package main.java.com.vss;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


public class App extends Application {

    private final SecretService service = SecretService.getInstance();
    private final MainController controller = MainController.getInstance();
    private static final Text asciiText = new Text("""
                
                  ██████ ▓█████  ▄████▄   ██▀███  ▓█████▄▄▄█████▓     ██████  ██░ ██  ▄▄▄       ██▀███   ██▓ ███▄    █   ▄████ 
                ▒██    ▒ ▓█   ▀ ▒██▀ ▀█  ▓██ ▒ ██▒▓█   ▀▓  ██▒ ▓▒   ▒██    ▒ ▓██░ ██▒▒████▄    ▓██ ▒ ██▒▓██▒ ██ ▀█   █  ██▒ ▀█▒
                ░ ▓██▄   ▒███   ▒▓█    ▄ ▓██ ░▄█ ▒▒███  ▒ ▓██░ ▒░   ░ ▓██▄   ▒██▀▀██░▒██  ▀█▄  ▓██ ░▄█ ▒▒██▒▓██  ▀█ ██▒▒██░▄▄▄░
                  ▒   ██▒▒▓█  ▄ ▒▓▓▄ ▄██▒▒██▀▀█▄  ▒▓█  ▄░ ▓██▓ ░      ▒   ██▒░▓█ ░██ ░██▄▄▄▄██ ▒██▀▀█▄  ░██░▓██▒  ▐▌██▒░▓█  ██▓
                ▒██████▒▒░▒████▒▒ ▓███▀ ░░██▓ ▒██▒░▒████▒ ▒██▒ ░    ▒██████▒▒░▓█▒░██▓ ▓█   ▓██▒░██▓ ▒██▒░██░▒██░   ▓██░░▒▓███▀▒
                ▒ ▒▓▒ ▒ ░░░ ▒░ ░░ ░▒ ▒  ░░ ▒▓ ░▒▓░░░ ▒░ ░ ▒ ░░      ▒ ▒▓▒ ▒ ░ ▒ ░░▒░▒ ▒▒   ▓▒█░░ ▒▓ ░▒▓░░▓  ░ ▒░   ▒ ▒  ░▒   ▒ 
                ░ ░▒  ░ ░ ░ ░  ░  ░  ▒     ░▒ ░ ▒░ ░ ░  ░   ░       ░ ░▒  ░ ░ ▒ ░▒░ ░  ▒   ▒▒ ░  ░▒ ░ ▒░ ▒ ░░ ░░   ░ ▒░  ░   ░ 
                ░  ░  ░     ░   ░          ░░   ░    ░    ░         ░  ░  ░   ░  ░░ ░  ░   ▒     ░░   ░  ▒ ░   ░   ░ ░ ░ ░   ░ 
                      ░     ░  ░░ ░         ░        ░  ░                 ░   ░  ░  ░      ░  ░   ░      ░           ░       ░ 
                                ░                                                                                              
                """);

    @Override
    public void start(Stage stage) {
        asciiText.setFont(Font.font("Monospaced", 14));

        Rectangle dropZone = createRectangle(600,200);

        Text dropText = createText("Drag and Drop");

        Button chooseButton = createOperationButton("Choose Image",Operation.CHOOSE_FILE);
        Button processButton = createOperationButton("Process Image", Operation.UPLOAD_IMAGE);

        StackPane dropContainer = createStackPane(dropZone, dropText, chooseButton);

        VBox fileInputContainer = createVBox(dropContainer,processButton);

        VBox mainContainer = createVBox(
                20.0,                        // spacing
                new Insets(20),                  // padding
                Pos.TOP_CENTER,                     // alignment
                "-fx-background-color: white",      // style
                asciiText,                          // children
                fileInputContainer
        );


        Scene scene = new Scene(mainContainer, 1000, 800);
        stage.setTitle("Visual Secret Sharing");
        stage.setScene(scene);
        stage.show();
    }


    private Button createOperationButton(String Text, Operation operation) {
        Button button = new Button(Text);
        button.setFont(Font.font("DejaVu Sans Mono", 14));

        button.setOnAction(event -> operation.execute());

        return button;
    }

    private Rectangle createRectangle(double width, double height) {
        Rectangle rectangle = new Rectangle(width, height);

        rectangle.setFill(Color.LIGHTGRAY);
        rectangle.setStroke(Color.GRAY);
        rectangle.setStrokeWidth(2);

        rectangle.setArcWidth(20);
        rectangle.setArcHeight(20);

        rectangle.setOnMouseEntered(e -> rectangle.setStroke(Color.DARKGRAY));
        rectangle.setOnMouseExited(e -> rectangle.setStroke(Color.GRAY));

        return rectangle;
    }

    private Text createText(String text) {
        Text text_aux = new Text(text);
        text_aux.setFont(Font.font("DejaVu Sans Mono", 14));

        return text_aux;
    }

    private VBox createVBox(Double spacing, Insets padding, Pos alignment, String style, Node... children) {
        VBox vbox = new VBox(spacing != null ? spacing : 10, children);
        vbox.setAlignment(alignment != null ? alignment : Pos.CENTER);
        vbox.setPadding(padding != null ? padding : new Insets(20, 0, 0, 0));
        if (style != null) {
            vbox.setStyle(style);
        }
        return vbox;
    }

    private VBox createVBox(Node... children) {
        return createVBox(null, null, null, null, children);
    }

    private StackPane createStackPane(Node... children) {
        StackPane stackPane = new StackPane(children);
        StackPane.setAlignment(children[1], Pos.CENTER); // Text sus
        StackPane.setAlignment(children[2], Pos.BOTTOM_CENTER);
        StackPane.setMargin(children[2], new Insets(10));

        return stackPane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}