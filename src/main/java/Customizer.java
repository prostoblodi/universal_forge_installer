import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

class Customizer {

    private final List<Pair<String, Byte>> presets = List.of(
            new Pair<>("dark", (byte) 0),
            new Pair<>("light", (byte) 1)
    );

    private final ComboBox<Pair<String, Byte>> presetChooser = new ComboBox<>();

    private final Stage stage = new Stage();
    protected static Scene scene;

    public Customizer(){
        System.out.println("# Customizer launched!");

        VBox windowLayout = new VBox();

        if (Universal.extendCustomizer){
            extend(windowLayout);
        } else {
            reduce(windowLayout);
        }

        windowLayout.getStyleClass().add("settings-vbox");

        ScrollPane scrollPane = new ScrollPane(windowLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");

        scene = new Scene(scrollPane);

        if (Universal.isDarkMode) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles-dark.css")).toExternalForm());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles-light.css")).toExternalForm());
        }

        stage.setTitle("Customizer");
        stage.setScene(scene);
    }

    protected void show(){
        stage.show();
    }

    private void reduce(VBox windowLayout){
        windowLayout.getChildren().clear();

        Label presetLabel = new Label("Preset:");
        presetLabel.getStyleClass().add("settings-label");
        presetChooser.getStyleClass().add("combo-box");
        Universal.initializeComboBox(presetChooser, presets);

        HBox presetHBox = new HBox(presetLabel, presetChooser);
        presetHBox.setAlignment(Pos.CENTER);
        presetHBox.setSpacing(10);

        windowLayout.getChildren().add(presetHBox);

        Label mainLabel = new Label("Customizer");
        mainLabel.getStyleClass().add("label-main");

        windowLayout.getChildren().add(mainLabel);

        CustomizerHBox universalHBox = new CustomizerHBox("chooser, button", true, true, true, true, true);

        windowLayout.getChildren().add(Universal.createSeparator("Universal"));
        windowLayout.getChildren().addAll(universalHBox.getHBoxes());

        CustomizerHBox labelsHBox = new CustomizerHBox("label", false, false, false, true, true);

        windowLayout.getChildren().add(Universal.createSeparator("Label"));
        windowLayout.getChildren().addAll(labelsHBox.getHBoxes());

        CustomizerHBox mainLabelHBox = new CustomizerHBox("main", false, false, false, true, true);

        windowLayout.getChildren().add(Universal.createSeparator("Main label"));
        windowLayout.getChildren().addAll(mainLabelHBox.getHBoxes());

        CustomizerHBox windowHBox = new CustomizerHBox("window", false, true, true, false, false);

        windowLayout.getChildren().add(Universal.createSeparator("Window"));
        windowLayout.getChildren().addAll(windowHBox.getHBoxes());
        Button extendButton = new Button("Extend");
        extendButton.getStyleClass().add("button");
        extendButton.setAlignment(Pos.CENTER);

        extendButton.setOnAction((_) -> {
            Universal.extendCustomizer = true;
            try {
                UFI.updateSettingsFile();
            } catch (IOException e) {
                UFI.updateStatusLabel((byte) 5);
            }
            extend(windowLayout);
        });

        windowLayout.getChildren().add(extendButton);
    }

    private void extend(VBox windowLayout){
        windowLayout.getChildren().clear();

        Label presetLabel = new Label("Preset:");
        presetLabel.getStyleClass().add("settings-label");
        presetChooser.getStyleClass().add("combo-box");
        Universal.initializeComboBox(presetChooser, presets);

        HBox presetHBox = new HBox(presetLabel, presetChooser);
        presetHBox.setAlignment(Pos.CENTER);
        presetHBox.setSpacing(10);

        windowLayout.getChildren().add(presetHBox);

        Label mainLabel = new Label("Customizer");
        mainLabel.getStyleClass().add("label-main");

        windowLayout.getChildren().add(mainLabel);

        CustomizerHBox buttonHBox = new CustomizerHBox("button", true, true, true, true, true);

        windowLayout.getChildren().add(Universal.createSeparator("Button"));
        windowLayout.getChildren().addAll(buttonHBox.getHBoxes());

        CustomizerHBox chooserHBox = new CustomizerHBox("chooser", true, true, true, true, true);

        windowLayout.getChildren().add(Universal.createSeparator("Chooser"));
        windowLayout.getChildren().addAll(chooserHBox.getHBoxes());

        CustomizerHBox mainLabelHBox = new CustomizerHBox("main", false, false, false, true, true);

        windowLayout.getChildren().add(Universal.createSeparator("Main label"));
        windowLayout.getChildren().addAll(mainLabelHBox.getHBoxes());

        CustomizerHBox labelHBox = new CustomizerHBox("label", false, false, false, true, true);

        windowLayout.getChildren().add(Universal.createSeparator("Label"));
        windowLayout.getChildren().addAll(labelHBox.getHBoxes());

        CustomizerHBox windowHBox = new CustomizerHBox("window", false, true, true, false, false);

        windowLayout.getChildren().add(Universal.createSeparator("Window"));
        windowLayout.getChildren().addAll(windowHBox.getHBoxes());
        Button extendButton = new Button("Reduce");
        extendButton.getStyleClass().add("button");
        extendButton.setAlignment(Pos.CENTER);

        extendButton.setOnAction((_) -> {
            Universal.extendCustomizer = false;
            try {
                UFI.updateSettingsFile();
            } catch (IOException e) {
                UFI.updateStatusLabel((byte) 5);
            }
            reduce(windowLayout);
        });

        windowLayout.getChildren().add(extendButton);
    }
}
