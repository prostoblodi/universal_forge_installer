import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.List;
import java.util.Objects;

class Customizer {

    private final List<Pair<String, Byte>> colorFormats = List.of(
            new Pair<>("hex", (byte) 0),
            new Pair<>("rgba", (byte) 1)
    );

    private final List<Pair<String, Byte>> presets = List.of(
            new Pair<>("dark", (byte) 0),
            new Pair<>("light", (byte) 1)
    );

    private final ComboBox<Pair<String, Byte>> presetChooser = new ComboBox<>();

    private final Stage stage = new Stage();
    protected Scene scene;

    public Customizer(){
        System.out.println("# Customizer launched!");

        Label presetLabel = new Label("Preset:");
        presetLabel.getStyleClass().add("settings-label");
        presetChooser.getStyleClass().add("combo-box");

        HBox presetHBox = new HBox(presetLabel, presetChooser);
        presetHBox.setAlignment(Pos.CENTER);
        presetHBox.setSpacing(10);

        Label mainLabel = new Label("Customizer");
        mainLabel.getStyleClass().add("label-main");

        VBox windowLayout = new VBox(mainLabel, presetHBox, Universal.createSeparator("Buttons"));
        windowLayout.getChildren().addAll(buttonCustomize());

        windowLayout.getChildren().add(Universal.createSeparator("Choosers"));
        windowLayout.getChildren().addAll(choosersCustomize());

        windowLayout.getChildren().add(Universal.createSeparator("Text"));
        windowLayout.getChildren().addAll(textCustomize());

        windowLayout.getStyleClass().add("settings-vbox");

        scene = new Scene(windowLayout);

        if (Universal.isDarkMode) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles-dark.css")).toExternalForm());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles-light.css")).toExternalForm());
        }

        stage.setTitle("Customizer");
        stage.setScene(scene);
    }

    private List<HBox> buttonCustomize(){
        final TextField changeRoundingRadiusField = new TextField();
        final TextField changeColorField = new TextField();
        final ComboBox<Pair<String, Byte>> changeColorChooser = new ComboBox<>();
        final TextField textSizeChanger = new TextField();
        final TextField changeTextColorField = new TextField();
        final ComboBox<Pair<String, Byte>> changeTextColorChooser = new ComboBox<>();
        final TextField changeBackgroundOpacityField = new TextField();

        HBox changeRoundingRadius = changeSizeHBoxGenerator(new Label("Rounding radius: "), changeRoundingRadiusField, new Label("px"),
        "Set the written rounding radius for button.");
        HBox changeBGColor = changeColorHBoxGenerator(new Label("Background color: "), changeColorField, changeColorChooser,
        "Set the written color for the button background."); // ボタン背景の色を設定する
        HBox changeOpacity = changeOpacityHBoxGenerator(new Label("Button background opacity: "), changeBackgroundOpacityField, new Label("%"),
        "Set the written opacity percentage for the button background.");
        HBox changeTextSize = changeSizeHBoxGenerator(new Label("Text size: "), textSizeChanger, new Label("px"),
        "Set the written size for the button text.");
        HBox changeTextColor = changeColorHBoxGenerator(new Label("Text color: "), changeTextColorField, changeTextColorChooser,
        "Set the written color for the button text.");

        return List.of(changeRoundingRadius, changeBGColor, changeOpacity, changeTextSize, changeTextColor);
    }

    private List<HBox> choosersCustomize(){
        final TextField changeRoundingRadiusField = new TextField();
        final TextField changeColorField = new TextField();
        final ComboBox<Pair<String, Byte>> changeColorChooser = new ComboBox<>();
        final TextField textSizeChanger = new TextField();
        final TextField changeTextColorField = new TextField();
        final ComboBox<Pair<String, Byte>> changeTextColorChooser = new ComboBox<>();
        final TextField changeBackgroundOpacityField = new TextField();

        HBox changeRoundingRadius = changeSizeHBoxGenerator(new Label("Rounding radius: "), changeRoundingRadiusField, new Label("px"),
                "Set the written rounding radius for chooser.");
        HBox changeBGColor = changeColorHBoxGenerator(new Label("Background color: "), changeColorField, changeColorChooser,
                "Set the written color for the chooser background.");
        HBox changeOpacity = changeOpacityHBoxGenerator(new Label("Button background opacity: "), changeBackgroundOpacityField, new Label("%"),
                "Set the written opacity percentage for the chooser background.");
        HBox changeTextSize = changeSizeHBoxGenerator(new Label("Text size: "), textSizeChanger, new Label("px"),
                "Set the written size for the chooser text.");
        HBox changeTextColor = changeColorHBoxGenerator(new Label("Text color: "), changeTextColorField, changeTextColorChooser,
                "Set the written color for the chooser text.");

        return List.of(changeRoundingRadius, changeBGColor, changeOpacity, changeTextSize, changeTextColor);
    }

    private List<HBox> textCustomize(){
        final TextField textSizeChanger = new TextField();
        final TextField changeTextColorField = new TextField();
        final ComboBox<Pair<String, Byte>> changeTextColorChooser = new ComboBox<>();

        HBox changeTextSize = changeSizeHBoxGenerator(new Label("Text size: "), textSizeChanger, new Label("px"),
                "Set the written size for the chooser text.");
        HBox changeTextColor = changeColorHBoxGenerator(new Label("Text color: "), changeTextColorField, changeTextColorChooser,
                "Set the written color for the chooser text.");

        return List.of(changeTextSize, changeTextColor);
    }

    protected void show(){
        stage.show();
    }

    private HBox changeColorHBoxGenerator(Label label, TextField textField, ComboBox<Pair<String, Byte>> comboBox, String tooltip){
        HBox colorChoose = new HBox(textField, comboBox);
        colorChoose.setSpacing(10);

        label.getStyleClass().add("settings-label");

        HBox fullBox = new HBox(label, new Region(), colorChoose);
        HBox.setHgrow(fullBox.getChildren().get(1), Priority.ALWAYS);

        Universal.setToolTip(fullBox, tooltip);

        return fullBox;
    }

    private HBox changeSizeHBoxGenerator(Label label, TextField textField, Label label2, String tooltip){
        label.getStyleClass().add("settings-label");
        label2.getStyleClass().add("settings-label");

        HBox sizeChoose = new HBox(textField, label2);
        sizeChoose.setSpacing(10);

        HBox fullBox = new HBox(label, new Region(), sizeChoose);
        HBox.setHgrow(fullBox.getChildren().get(1), Priority.ALWAYS);

        Universal.setToolTip(fullBox, tooltip);

        return fullBox;
    }

    private HBox changeOpacityHBoxGenerator(Label label, TextField textField, Label label2, String tooltip){
        label.getStyleClass().add("settings-label");
        label2.getStyleClass().add("settings-label");

        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            if (Byte.parseByte(change.getControlNewText()) <= 100) {
                return change;
            } else {
                return null;
            }
        });

        textField.setTextFormatter(formatter);

        HBox sizeChoose = new HBox(textField, label2);
        sizeChoose.setSpacing(10);

        HBox fullBox = new HBox(label, new Region(), sizeChoose);
        HBox.setHgrow(fullBox.getChildren().get(1), Priority.ALWAYS);

        Universal.setToolTip(fullBox, tooltip);

        return fullBox;
    }
}
