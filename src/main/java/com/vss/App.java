package main.java.com.vss;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.image.ImageView;

public class App extends Application {
    private final SecretService service = SecretService.getInstance();
    private final MainController controller = MainController.getInstance();

    private TextField usernameField;
    private PasswordField passwordField;
    private TextField textField1;
    private TextField textField2;
    private ImageView imagePreview;
    private ImageView[] sharesPreview;

    private VBox mainContent; // Acum va conține fie login, fie scrollPane cu mainInterface
    private ScrollPane mainInterfaceScrollPane;

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

    @Override
    public void start(Stage stage) {
        controller.setAppReference(this);

        asciiText.setFont(Font.font("Monospaced", 14));

        // Inițializăm mainContent - va conține fie login, fie scrollPane cu mainInterface
        mainContent = new VBox();

        // Inițializăm scrollPane pentru mainInterface (îl vom adăuga când e necesar)
        mainInterfaceScrollPane = new ScrollPane();
        mainInterfaceScrollPane.setFitToWidth(true);
        mainInterfaceScrollPane.setStyle("-fx-background: white; -fx-background-color: white; -fx-background-insets: 0;");
        mainInterfaceScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainInterfaceScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        showLoginInterface();

        VBox mainContainer = createVBox(
                20.0,                        // spacing
                new Insets(20),                   // padding
                Pos.TOP_CENTER,                     // alignment
                "-fx-background-color: white",      // style
                asciiText,
                mainContent  // Aici vom schimba între login și mainInterface
        );

        Scene scene = new Scene(mainContainer, 1000, 800);
        stage.setTitle("Visual Secret Sharing");
        stage.setScene(scene);
        stage.show();
    }

    private Button createButton(String text, EventHandler<ActionEvent> action) {
        Button button = new Button(text);
        button.setFont(Font.font("DejaVu Sans Mono", 14));
        button.setOnAction(action);
        return button;
    }

    private Button createOperationButton(String text, Runnable operation) {
        return createButton(text, event -> operation.run());
    }

    private VBox createLoginForm() {
        Label title = new Label("LOGIN");
        title.setFont(Font.font("DejaVu Sans Mono", 28));
        title.setTextFill(Color.web("#2c3e50"));
        title.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 20 0;");

        Label subtitle = new Label("Welcome back! Please enter your credentials");
        subtitle.setFont(Font.font("DejaVu Sans Mono", 12));
        subtitle.setTextFill(Color.GRAY);
        VBox titleContainer = new VBox(5, title, subtitle);
        titleContainer.setAlignment(Pos.CENTER);

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setFont(Font.font("DejaVu Sans Mono", 14));
        usernameField.setPrefWidth(300);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setFont(Font.font("DejaVu Sans Mono", 14));
        passwordField.setPrefWidth(300);

        Button loginButton = createButton("Login", e -> handleLogin());
        Button createAccountButton = createButton("Create Account", e -> handleCreateAccount());

        HBox buttonContainer = new HBox(20, loginButton, createAccountButton);
        buttonContainer.setAlignment(Pos.CENTER);

        return createVBox(
                15.0,
                new Insets(30, 0, 0, 0),
                Pos.CENTER,
                null,
                titleContainer,
                createLabeledInput("Username:", "", usernameField),
                createLabeledInput("Password:", "", passwordField),
                buttonContainer
        );
    }

    public void updateImagePreview(Image image) {
        imagePreview.setImage(image);
    }

    private VBox createMainAppInterface() {
        this.textField1 = new TextField();
        this.textField2 = new TextField();

        Rectangle dropZone = createRectangle(600, 200);
        Text dropText = createText("Drag and Drop");
        Button chooseButton = createOperationButton("Choose Image", ()->{controller.chooseFile();});
        Button processButton = createOperationButton("Process Image", ()->{
            if(!this.textField1.getText().isEmpty() && !this.textField2.getText().isEmpty() && service.isImageUploaded()) {
                try {
                    Integer minimumNumOfShares = Integer.parseInt(textField2.getText().trim());
                    Integer totalNumberOfShares = Integer.parseInt(textField1.getText().trim());

                    if(minimumNumOfShares > totalNumberOfShares) {
                        showAlert("Error", "Minimum number of shares exceeded");
                    }else {
                        shares = service.processImage(minimumNumOfShares, totalNumberOfShares);
                        showMainInterface();
                    }
                } catch (NumberFormatException | IOException e) {
                    showAlert("Error", "Invalid number");
                    return;
                }

//                System.out.println("aici");
            }else {
                showAlert("Error", "Minimum share num and total shares num required");
                if(!service.isImageUploaded()){
                    showAlert("Error", "Secret not uploaded");
                }
            }
        });

        StackPane dropContainer = createStackPane(dropZone, dropText, chooseButton);

        HBox totalNumOfShares = createLabeledInput("", "Total num of shares", textField1);
        HBox minimalNumOfShares = createLabeledInput("", "Minimal num of shares", textField2);

        VBox inputFieldsContainer = createVBox(totalNumOfShares, minimalNumOfShares);

        Button logoutButton = createButton("Logout", e -> handleLogout());
        HBox bottomBar = new HBox(logoutButton);
        bottomBar.setAlignment(Pos.BASELINE_RIGHT);

        imagePreview = new ImageView();
        imagePreview.setFitWidth(300); // Setăm o lățime maximă
        imagePreview.setFitHeight(200); // Setăm o înălțime maximă
        imagePreview.setPreserveRatio(true); // păstrează proporțiile imaginii
        imagePreview.setSmooth(true);
        imagePreview.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px;");

        Label previewLabel = new Label("Preview:");
        previewLabel.setFont(Font.font("DejaVu Sans Mono", 14));

        VBox imageContainer = new VBox(10, previewLabel, imagePreview);
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setPadding(new Insets(20, 0, 20, 0));

        System.out.println("numar de share uri "+shares.size());

        sharesPreview = new ImageView[shares.size()];

        if(!shares.isEmpty()) {
            for (int i=0;i< shares.size();i+=1) {
                sharesPreview[i] = new ImageView(shares.get(i));

                sharesPreview[i].setFitWidth(300); // Setăm o lățime maximă
                sharesPreview[i].setFitHeight(200); // Setăm o înălțime maximă
                sharesPreview[i].setPreserveRatio(true); // păstrează proporțiile imaginii
                sharesPreview[i].setSmooth(true);
                sharesPreview[i].setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px;");
            }

            VBox sharesContainer = new VBox(10);
            sharesContainer.getChildren().add(previewLabel);
            sharesContainer.getChildren().addAll(Arrays.asList(sharesPreview));
            sharesContainer.setAlignment(Pos.CENTER);
            sharesContainer.setPadding(new Insets(20, 0, 20, 0));

            return createVBox(
                    15.0,
                    new Insets(10, 0, 0, 0),
                    Pos.TOP_CENTER,
                    null,
                    dropContainer,
                    inputFieldsContainer,
                    processButton,
                    sharesContainer,
                    bottomBar
            );
        }
        else{
            return createVBox(
                    15.0,
                    new Insets(10, 0, 0, 0),
                    Pos.TOP_CENTER,
                    null,
                    dropContainer,
                    inputFieldsContainer,
                    processButton,
                    imageContainer,
                    bottomBar
            );
        }


    }


    private void showLoginInterface() {
        mainContent.getChildren().clear();

        VBox loginForm = createLoginForm();

        Rectangle loginBox = createRectangle(400, 250);

        StackPane loginContainer = new StackPane(loginBox, loginForm);

        mainContent.getChildren().add(loginContainer);
        mainContent.setAlignment(Pos.CENTER);
    }

    private void showMainInterface() {
        mainContent.getChildren().clear();

        // Creăm interfața principală
        VBox mainAppInterface = createMainAppInterface();

        // Punem interfața principală în ScrollPane
        mainInterfaceScrollPane.setContent(mainAppInterface);

        // Adăugăm ScrollPane în mainContent
        mainContent.getChildren().add(mainInterfaceScrollPane);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Eroare", "Ambele câmpuri sunt obligatorii!");
            return;
        }

        if (controller.authenticate(username, password)) {
            showMainInterface();
        } else {
            showAlert("Eroare", "Nume de utilizator sau parolă incorectă!");
        }
    }

    private void handleCreateAccount() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Eroare", "Ambele câmpuri sunt obligatorii!");
            return;
        }

        if (controller.createAccount(username, password)) {
            showAlert("Succes", "Cont creat cu succes! Acum vă puteți autentifica.");
        } else {
            showAlert("Eroare", "Numele de utilizator există deja!");
        }
    }

    private void handleLogout() {
        controller.logout();
        showLoginInterface();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private HBox createLabeledInput(String labelText, String promptText, TextField textField) {
        Label label = new Label(labelText);
        label.setFont(Font.font("DejaVu Sans Mono", 14));

        textField.setPromptText(promptText);
        textField.setFont(Font.font("DejaVu Sans Mono", 14));
        textField.setPrefWidth(300);

        HBox hbox = new HBox(10, label, textField);
        hbox.setAlignment(Pos.CENTER);

        return hbox;
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
        if (children.length > 1) {
            StackPane.setAlignment(children[1], Pos.CENTER);
        }
        if (children.length > 2) {
            StackPane.setAlignment(children[2], Pos.BOTTOM_CENTER);
            StackPane.setMargin(children[2], new Insets(10));
        }
        return stackPane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}