import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.List;
import java.util.Objects;

class Settings {
    private final Label mainLabel = new Label("Settings");

    private final Label defaultForgeVersionLabel = new Label("Default forge version:");
    private final ComboBox<Pair<String, Byte>> chooseDefaultForgeVersion = new ComboBox<>();
    private final List<Pair<String, Byte>> defaultForgeVersions = List.of(
            new Pair<>("Recommended", (byte) 0),
            new Pair<>("Newest", (byte) 1),
            new Pair<>("Oldest", (byte) 2)
    );

    private final Label enableCustomLaunchLabel = new Label("Enable custom forge launch: ");
    private final ComboBox<Pair<String, Boolean>> enableCustomLaunch = new ComboBox<>();
    private final List<Pair<String, Boolean>> customLaunches = List.of(
            new Pair<>("Enable", true),
            new Pair<>("Disable", false)
    );

    private final Label minecraftFolderChooseLabel = new Label("Choose minecraft folder: ");
    private final TextField minecraftFolderField = new TextField();

    private final Stage stage = new Stage();

    public Settings() {
        Button minecraftFolderButton = new Button("...");

        setStyles();
        initialize();

        minecraftFolderButton.setOnAction((_) -> minecraftFolderField.setText(String.valueOf(new DirectoryChooser().showDialog(new Stage()))));

        VBox defaultForgeVersionLabelBox = new VBox();
        defaultForgeVersionLabelBox.getChildren().add(defaultForgeVersionLabel);
        defaultForgeVersionLabelBox.setAlignment(Pos.CENTER_LEFT);

        VBox customForgeLaunchLabelsBox = new VBox();
        customForgeLaunchLabelsBox.getChildren().addAll(enableCustomLaunchLabel, minecraftFolderChooseLabel);
        customForgeLaunchLabelsBox.setAlignment(Pos.CENTER_LEFT);
        customForgeLaunchLabelsBox.setSpacing(15);

        HBox folderChoose = new HBox();
        folderChoose.getChildren().addAll(minecraftFolderField, minecraftFolderButton);
        folderChoose.setAlignment(Pos.CENTER);
        folderChoose.setSpacing(10);

        VBox defaultForgeVersionChooserBox = new VBox();
        defaultForgeVersionChooserBox.getChildren().add(chooseDefaultForgeVersion);
        defaultForgeVersionChooserBox.setAlignment(Pos.CENTER_RIGHT);

        VBox customForgeLaunchChoosersBox = new VBox();
        customForgeLaunchChoosersBox.getChildren().addAll(enableCustomLaunch, folderChoose);
        customForgeLaunchChoosersBox.setAlignment(Pos.CENTER_RIGHT);
        customForgeLaunchChoosersBox.setSpacing(10);

        HBox defaultForgeVersionFullBox = new HBox();
        defaultForgeVersionFullBox.getChildren().addAll(defaultForgeVersionLabelBox, new Region(), defaultForgeVersionChooserBox);
        HBox.setHgrow(defaultForgeVersionFullBox.getChildren().get(1), Priority.ALWAYS);
        defaultForgeVersionFullBox.setAlignment(Pos.CENTER_LEFT);

        HBox customForgeLaunchFullBox = new HBox();
        customForgeLaunchFullBox.getChildren().addAll(customForgeLaunchLabelsBox, new Region(), customForgeLaunchChoosersBox);
        HBox.setHgrow(customForgeLaunchFullBox.getChildren().get(1), Priority.ALWAYS);
        customForgeLaunchFullBox.setAlignment(Pos.CENTER_LEFT);

        VBox windowLayout = new VBox(mainLabel, defaultForgeVersionFullBox, createSeparator("Custom forge launch"), customForgeLaunchFullBox);
        windowLayout.getStyleClass().add("settings-vbox");

        Scene scene = new Scene(windowLayout);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        stage.setTitle("Settings");
        stage.setScene(scene);
    }

    protected void show() {
        stage.show();
    }

    private void setStyles() {
        mainLabel.getStyleClass().add("label-main");
        defaultForgeVersionLabel.getStyleClass().add("settings-label");
        enableCustomLaunchLabel.getStyleClass().add("settings-label");
        minecraftFolderChooseLabel.getStyleClass().add("settings-label");
        chooseDefaultForgeVersion.getStyleClass().add("combo-box");
    }

    private void initialize(){
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(minecraftFolderField.textProperty());
        Tooltip.install(minecraftFolderField, tooltip);

        minecraftFolderField.setOnContextMenuRequested(Event::consume);

        initializeEnableCustomForgeLaunch();
        initializeDefaultForgeVersionChooser();
    }

    private void initializeDefaultForgeVersionChooser(){
        chooseDefaultForgeVersion.getItems().addAll(defaultForgeVersions);
        chooseDefaultForgeVersion.setValue(defaultForgeVersions.getFirst());

        chooseDefaultForgeVersion.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, Byte> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getKey()); // Отображаем только название версии
                }
            }
        });

        chooseDefaultForgeVersion.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, Byte> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getKey()); // Отображаем название версии
                }
            }
        });
    }

    private void initializeEnableCustomForgeLaunch(){
        enableCustomLaunch.getItems().addAll(customLaunches);
        enableCustomLaunch.setValue(customLaunches.getFirst());

        enableCustomLaunch.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, Boolean> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getKey()); // Отображаем только название версии
                }
            }
        });

        enableCustomLaunch.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, Boolean> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getKey()); // Отображаем название версии
                }
            }
        });
    }

    private HBox createSeparator(String text) {
        Label label = new Label(text);

        Separator leftSeparator = new Separator(Orientation.HORIZONTAL);
        Separator rightSeparator = new Separator(Orientation.HORIZONTAL);

        HBox.setHgrow(leftSeparator, Priority.SOMETIMES);
        HBox.setHgrow(rightSeparator, Priority.SOMETIMES);

        HBox hbox = new HBox(leftSeparator, label, rightSeparator);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(10);

        return hbox;
    }

}
