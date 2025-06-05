package main.java.com.vss.view.screens;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.java.com.vss.controller.AuthController;
import main.java.com.vss.controller.DecryptController;
import main.java.com.vss.observer.ImageProcessingObserver;

import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import main.java.com.vss.view.InterfaceManager;
import main.java.com.vss.view.components.UIComponents;

public class DecryptScreen implements ImageProcessingObserver {
    private final InterfaceManager interfaceManager;
    private final AuthController authController;
    private final DecryptController decryptController;

    private final VBox root;
    private final ScrollPane scrollPane;
    private final VBox contentContainer;

    private List<ImageView> sharesPreview = new ArrayList<>();
    private VBox sharesContainer;
    private VBox secretContainer;
    private Button processButton;
    private Button encryptButton;
    private Button menuButton;

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

    public DecryptScreen(InterfaceManager interfaceManager, AuthController authController, DecryptController decryptController){
        this.interfaceManager = interfaceManager;
        this.authController = authController;
        this.decryptController = decryptController;

        root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white; -fx-background-insets: 0;");
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        contentContainer = new VBox(20);
        contentContainer.setAlignment(Pos.TOP_CENTER);

        initializeComponents();
        decryptController.addObserver(this);

        contentContainer.getChildren().addAll(
                createTitle(),
                createSharesSection(),
                createSecretSection()
//                createImageSection(),
//                createSharesSection(),
//                createControlSection()
        );

        scrollPane.setContent(contentContainer);
        root.getChildren().add(scrollPane);
    }

    public VBox getRoot() {
        return root;
    }

    private void initializeComponents() {
        secretContainer = new VBox(10);
        secretContainer.setAlignment(Pos.CENTER);

        sharesContainer = new VBox(10);
        sharesContainer.setAlignment(Pos.CENTER);

        processButton = UIComponents.createButton("Process Shares", event -> {
            try {
                decryptController.handleDecryptImage();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        processButton.setAlignment(Pos.CENTER);

        encryptButton = UIComponents.createButton("Encrypt", event->decryptController.handleEncrypt());
        encryptButton.setAlignment(Pos.CENTER);

        menuButton = UIComponents.createButton("Menu",event -> interfaceManager.showMainScreen());
        menuButton.setAlignment(Pos.CENTER);
    }

    private Text createTitle() {
        asciiText.setFont(Font.font("Monospaced", 14));
        return asciiText;
    }

    private VBox createSharesSection() {
        Rectangle dropZone = new Rectangle(600, 200);
        dropZone.setFill(javafx.scene.paint.Color.LIGHTGRAY);
        dropZone.setStroke(Color.GRAY);
        dropZone.setStrokeWidth(2);
        dropZone.setArcWidth(20);
        dropZone.setArcHeight(20);

        Text dropText = new Text("Drag and Drop Image");
        dropText.setFont(Font.font("DejaVu Sans Mono", 14));

        Button chooseButton = UIComponents.createButton("Choose Image", event ->
                decryptController.handleChooseImage(this.root));

        StackPane dropContainer = new StackPane(dropZone, dropText, chooseButton);
        StackPane.setAlignment(dropText, Pos.CENTER);
        StackPane.setAlignment(chooseButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(chooseButton, new Insets(10));

        Label previewLabel = new Label("Preview:");
        previewLabel.setFont(Font.font("DejaVu Sans Mono", 14));

        VBox previewContainer = new VBox(10, previewLabel, sharesContainer);
        previewContainer.setAlignment(Pos.CENTER);

        return new VBox(20, dropContainer, previewContainer);
    }

    private VBox createSecretSection() {
        Label secretLabel = new Label("Secret:");
        secretLabel.setFont(Font.font("DejaVu Sans Mono", 14));

        HBox processContainer = new HBox(20, processButton, encryptButton,menuButton);
        processContainer.setAlignment(Pos.CENTER);

        secretContainer.getChildren().add(secretLabel);
        secretContainer.setAlignment(Pos.CENTER);

        return new VBox(20, processContainer,secretContainer);
    }


    private Image convertToFxImage(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }


    @Override
    public void onImageLoaded(BufferedImage image){
        ImageView shareView = new ImageView(convertToFxImage(image));
        shareView.setFitWidth(150);
        shareView.setPreserveRatio(true);
        shareView.setSmooth(true);
        sharesContainer.getChildren().add(shareView);

        sharesPreview.add(shareView);
    }

    @Override
    public void onProcessingStarted(){}

    @Override
    public void onProcessingCompleted(List<Image> secret){
        sharesContainer.getChildren().clear();

        ImageView secretView = new ImageView(secret.getFirst());
        secretView.setFitWidth(250);
        secretView.setPreserveRatio(true);
        secretView.setSmooth(true);

        secretContainer.getChildren().add(secretView);
    }

    @Override
    public void onProcessingError(String errorMessage){}

}
