package main.java.com.vss.view.screens;

import javafx.embed.swing.SwingFXUtils;
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
import main.java.com.vss.controller.AuthController;
import main.java.com.vss.controller.EncryptController;
import main.java.com.vss.observer.ImageProcessingObserver;
import main.java.com.vss.view.InterfaceManager;
import main.java.com.vss.view.components.UIComponents;


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class EncryptScreen implements ImageProcessingObserver {
    private final InterfaceManager interfaceManager;
    private final AuthController authController;
    private final EncryptController encryptController;

    private final VBox root;
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
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        contentContainer = new VBox(20);
        contentContainer.setAlignment(Pos.TOP_CENTER);

        initializeComponents();

        encryptController.addObserver(this);

        contentContainer.getChildren().addAll(
                createTitle(),
                createImageSection(),
                createSharesSection(),
                createControlSection()
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
        totalSharesField = new TextField();
        totalSharesField.setPromptText("Total number of shares");
        totalSharesField.setPrefWidth(200);

        minSharesField = new TextField();
        minSharesField.setPromptText("Minimum number of shares");
        minSharesField.setPrefWidth(200);

        imagePreview = new ImageView();
        imagePreview.setFitWidth(300);
        imagePreview.setFitHeight(200);
        imagePreview.setPreserveRatio(true);
        imagePreview.setSmooth(true);
        imagePreview.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px;");

        sharesContainer = new VBox(10);
        sharesContainer.setAlignment(Pos.CENTER);

//        progressIndicator = new ProgressIndicator();
//        progressIndicator.setVisible(false);

        processButton = UIComponents.createButton("Process Secret", event -> encryptController.handleProcessImage(
                Integer.parseInt(totalSharesField.getText()),
                Integer.parseInt(minSharesField.getText())
        ));
    }

    private VBox createImageSection() {
        Rectangle dropZone = new Rectangle(600, 200);
        dropZone.setFill(Color.LIGHTGRAY);
        dropZone.setStroke(Color.GRAY);
        dropZone.setStrokeWidth(2);
        dropZone.setArcWidth(20);
        dropZone.setArcHeight(20);

        Text dropText = new Text("Drag and Drop Image");
        dropText.setFont(Font.font("DejaVu Sans Mono", 14));

        Button chooseButton = UIComponents.createButton("Choose Image", event ->
                encryptController.handleChooseImage(this.root));

        StackPane dropContainer = new StackPane(dropZone, dropText, chooseButton);
        StackPane.setAlignment(dropText, Pos.CENTER);
        StackPane.setAlignment(chooseButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(chooseButton, new Insets(10));

        Label previewLabel = new Label("Preview:");
        previewLabel.setFont(Font.font("DejaVu Sans Mono", 14));

        VBox previewContainer = new VBox(10, previewLabel, imagePreview);
        previewContainer.setAlignment(Pos.CENTER);

        return new VBox(20, dropContainer, previewContainer);
    }

    private VBox createSharesSection() {
        HBox totalSharesContainer = UIComponents.createLabeledInput("", totalSharesField);
        HBox minSharesContainer = UIComponents.createLabeledInput("", minSharesField);

        VBox inputFields = new VBox(10, totalSharesContainer, minSharesContainer);
        inputFields.setAlignment(Pos.CENTER);

        HBox processContainer = new HBox(20, processButton);
        processContainer.setAlignment(Pos.CENTER);

        Label sharesLabel = new Label("Generated Shares:");
        sharesLabel.setFont(Font.font("DejaVu Sans Mono", 14));

        sharesContainer.getChildren().add(sharesLabel);

        return new VBox(20, inputFields, processContainer, sharesContainer);
    }

    private HBox createControlSection() {
        Button saveButton = UIComponents.createButton("Save Shares", event -> encryptController.handleSaveShares(
                this.shares,"/home/sebi/Visual_Secret_Sharing/shares"
        ));
        Button clearButton = UIComponents.createButton("Clear All", event -> encryptController.handleClear());

        HBox controls = new HBox(20, saveButton, clearButton);
        controls.setAlignment(Pos.CENTER);

        return controls;
    }


    private Image convertToFxImage(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    @Override
    public void onImageLoaded(BufferedImage image){
        javafx.scene.image.Image fxImage = convertToFxImage(image);
        imagePreview.setImage(fxImage);
    }

    @Override
    public void onProcessingStarted(){}

    @Override
    public void onProcessingCompleted(List<Image> shares){
        this.shares = shares;

        sharesContainer.getChildren().clear();

        for (Image share : shares) {
            ImageView shareView = new ImageView(share);
            shareView.setFitWidth(150);
            shareView.setPreserveRatio(true);
            shareView.setSmooth(true);
            sharesContainer.getChildren().add(shareView);
        }
    }

    @Override
    public void onProcessingError(String errorMessage){}


}
