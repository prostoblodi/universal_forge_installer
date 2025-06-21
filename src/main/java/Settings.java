import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.List;
import java.util.Objects;

public class Settings {
    private final Label mainLabel = new Label("Settings");

    private final Label defaultForgeVersionLabel = new Label("Default forge version:");
    private final ComboBox<Pair<String, Byte>> chooseDefaultForgeVersion = new ComboBox<>();
    private final List<Pair<String, Byte>> defaultForgeVersions = List.of(
            new Pair<>("Default", (byte) 0),
            new Pair<>("Recommended", (byte) 1),
            new Pair<>("Newest", (byte) 2)
    );

    private final Label enableCustomLaunchLabel = new Label("Enable custom forge launch: ");
    private final ComboBox<Pair<String, Boolean>> enableCustomLaunch = new ComboBox<>();
    private final List<Pair<String, Boolean>> customLaunches = List.of(
            new Pair<>("Default", true),
            new Pair<>("Enable", true),
            new Pair<>("Disable", false)
    );

    private final Label minecraftFolderChooseLabel = new Label("Choose minecraft folder: ");
    private final TextField minecraftFolderField = new TextField();

    private final Stage stage = new Stage();

    public Settings() {
        Button minecraftFolderButton = new Button("...");

        setStyles();
        initializeDefaultForgeVersionChooser();
        initializeEnableCustomForgeLaunch();
        minecraftFolderButton.setOnAction((_) -> minecraftFolderField.setText(String.valueOf(new DirectoryChooser().showDialog(stage))));

        VBox vbox = new VBox();
        vbox.getChildren().addAll(defaultForgeVersionLabel, enableCustomLaunchLabel, minecraftFolderChooseLabel);
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setSpacing(15);

        HBox folderChoose = new HBox();
        folderChoose.getChildren().addAll(minecraftFolderField, minecraftFolderButton);
        folderChoose.setAlignment(Pos.CENTER);
        folderChoose.setSpacing(10);

        VBox vbox2 = new VBox();
        vbox2.getChildren().addAll(chooseDefaultForgeVersion, enableCustomLaunch, folderChoose);
        vbox2.setAlignment(Pos.CENTER_RIGHT);
        vbox2.setSpacing(10);

        HBox hboxes = new HBox();
        hboxes.getChildren().addAll(vbox, vbox2);
        hboxes.setAlignment(Pos.CENTER);

        VBox vbox3 = new VBox(mainLabel, hboxes);
        vbox3.getStyleClass().add("settings-vbox");

        Scene scene = new Scene(vbox3);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        stage.setTitle("Settings");
        stage.setScene(scene);
    }

    public void show() {
        stage.show();
    }

    private void setStyles() {
        mainLabel.getStyleClass().add("label-main");
        defaultForgeVersionLabel.getStyleClass().add("settings-label");
        enableCustomLaunchLabel.getStyleClass().add("settings-label");
        minecraftFolderChooseLabel.getStyleClass().add("settings-label");
        chooseDefaultForgeVersion.getStyleClass().add("combo-box");
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
}
