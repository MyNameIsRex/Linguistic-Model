package data.psychologytheory.linguisticmodel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ApplicationHandler extends Application {
    private Text introductionText;
    private TextField inputTextField;
    private Button sendInputButton;
    private Alert outputAlert;

    private StringBuilder input;
    private LinguisticModel linguisticModel;

    @Override
    public void start(Stage stage) throws Exception {
        this.input = new StringBuilder();
        this.linguisticModel = new LinguisticModel();

        StackPane layout = new StackPane();

        this.introductionText = new Text();
        this.introductionText.setText("In the text fields below, enter the lexical word in different sections, leave empty if it is not applicable");
        this.introductionText.setTranslateY(-30);
        layout.getChildren().add(this.introductionText);

        this.inputTextField = new TextField();
        this.inputTextField.setMaxWidth(550);
        layout.getChildren().add(this.inputTextField);

        this.outputAlert = new Alert(Alert.AlertType.INFORMATION);
        this.outputAlert.setHeaderText("Word has been parsed");
        this.outputAlert.setTitle("Budai Rukai Parser");


        this.sendInputButton = new Button();
        this.sendInputButton.setText("Enter");
        this.sendInputButton.setTranslateY(32.5);
        this.sendInputButton.setOnAction((e) -> {
            if (!this.inputTextField.getText().isEmpty()) {
                this.input.setLength(0);
                this.input.append(this.inputTextField.getText());
                this.linguisticModel.initialize(this.input.toString());
                this.outputAlert.setContentText("Output: " + this.linguisticModel.getOutput());
                this.outputAlert.showAndWait();
            }
        });

        layout.getChildren().add(this.sendInputButton);

        stage.setScene(new Scene(layout, 600, 100));
        stage.setTitle("Budai Rukai Parser");
        stage.setResizable(false);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
