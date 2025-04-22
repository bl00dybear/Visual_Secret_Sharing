package main.java.com.vss.view.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.java.com.vss.controller.AuthController;
import main.java.com.vss.controller.EncryptController;
import main.java.com.vss.view.InterfaceManager;
import main2.java.com.vss.presentation.view.components.UIFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EncryptScreen {
    private final InterfaceManager interfaceManager;
    private final AuthController authController;
    private final EncryptController encryptController;

    private VBox root;
    private final ScrollPane scrollPane;
    private final VBox contentContainer;

    private TextField totalSharesField;
    private TextField minSharesField;
    private ImageView imagePreview;
    private VBox sharesContainer;
    private Button processButton;
    private ProgressIndicator progressIndicator;

    private List<Image> shares = new ArrayList<>();

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

    public EncryptScreen(InterfaceManager interfaceManager, AuthController authController, EncryptController encryptController){
        this.interfaceManager = interfaceManager;
        this.authController = authController;
        this.encryptController = encryptController;

        root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white; -fx-background-insets: 0;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        contentContainer = new VBox(20);
        contentContainer.setAlignment(Pos.TOP_CENTER);

        initializeComponents();

        contentContainer.getChildren().addAll(
                createTitle(),
                createImageSection()
//                createSharesSection(),
//                createControlSection()
        );

        scrollPane.setContent(contentContainer);
        root.getChildren().add(scrollPane);
    }
    public VBox getRoot() {
        return root;
    }


    private Text createTitle() {
        asciiText.setFont(Font.font("Monospaced", 14));

        return asciiText;
    }

    private void initializeComponents() {
        // Inițializăm câmpurile de text
        totalSharesField = new TextField();
        totalSharesField.setPromptText("Total number of shares");
        totalSharesField.setPrefWidth(200);

        minSharesField = new TextField();
        minSharesField.setPromptText("Minimum number of shares");
        minSharesField.setPrefWidth(200);

        // Inițializăm vizualizarea imaginii
        imagePreview = new ImageView();
        imagePreview.setFitWidth(300);
        imagePreview.setFitHeight(200);
        imagePreview.setPreserveRatio(true);
        imagePreview.setSmooth(true);
        imagePreview.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Inițializăm containerul pentru share-uri
        sharesContainer = new VBox(10);
        sharesContainer.setAlignment(Pos.CENTER);

        // Inițializăm indicatorul de progres
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        // Inițializăm butoanele
        processButton = UIFactory.createButton("Process Image", event -> handleProcessImage());
    }

    private VBox createImageSection() {
        // Creează zona de drop pentru imagine
        Rectangle dropZone = new Rectangle(600, 200);
        dropZone.setFill(Color.LIGHTGRAY);
        dropZone.setStroke(Color.GRAY);
        dropZone.setStrokeWidth(2);
        dropZone.setArcWidth(20);
        dropZone.setArcHeight(20);

        Text dropText = new Text("Drag and Drop Image");
        dropText.setFont(Font.font("DejaVu Sans Mono", 14));

        Button chooseButton = UIFactory.createButton("Choose Image", event -> handleChooseImage());

        StackPane dropContainer = new StackPane(dropZone, dropText, chooseButton);
        StackPane.setAlignment(dropText, Pos.CENTER);
        StackPane.setAlignment(chooseButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(chooseButton, new Insets(10));

        // Creează containerul pentru preview
        Label previewLabel = new Label("Preview:");
        previewLabel.setFont(Font.font("DejaVu Sans Mono", 14));

        VBox previewContainer = new VBox(10, previewLabel, imagePreview);
        previewContainer.setAlignment(Pos.CENTER);

        return new VBox(20, dropContainer, previewContainer);
    }

    private VBox createSharesSection() {
        HBox totalSharesContainer = UIFactory.createLabeledInput("Total number of shares:", totalSharesField);
        HBox minSharesContainer = UIFactory.createLabeledInput("Minimum number of shares:", minSharesField);

        VBox inputFields = new VBox(10, totalSharesContainer, minSharesContainer);
        inputFields.setAlignment(Pos.CENTER);

        HBox processContainer = new HBox(20, processButton, progressIndicator);
        processContainer.setAlignment(Pos.CENTER);

        Label sharesLabel = new Label("Generated Shares:");
        sharesLabel.setFont(Font.font("DejaVu Sans Mono", 14));

        sharesContainer.getChildren().add(sharesLabel);

        return new VBox(20, inputFields, processContainer, sharesContainer);
    }

    private HBox createControlSection() {
        Button saveButton = UIFactory.createButton("Save Shares", event -> handleSaveShares());
        Button clearButton = UIFactory.createButton("Clear All", event -> handleClear());

        HBox controls = new HBox(20, saveButton, clearButton);
        controls.setAlignment(Pos.CENTER);

        return controls;
    }

    private void handleProcessImage(){}

    private void handleChooseImage(){
        File file = encryptController.chooseImageFile((Stage) root.getScene().getWindow());
        if (file != null) {
            try {
                encryptController.loadImage(file);
                // Imaginea va fi actualizată prin callback-ul onImageLoaded
            } catch (IOException e) {
                showAlert("Error", "Failed to load image: " + e.getMessage());
            }
        }
    }

    private void handleSaveShares(){}

    private void handleClear(){}

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
