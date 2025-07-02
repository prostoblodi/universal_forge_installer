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

import java.io.IOException;
import java.util.List;
import java.util.Objects;

class Settings {

    private final List<Pair<String, Boolean>> enableOrDisable = List.of(
            new Pair<>("Enable", true),
            new Pair<>("Disable", false)
    );

    private final List<Pair<String, Byte>> defaultMinecraftVersions = List.of(
            new Pair<>("None", (byte) 0),
            new Pair<>("Newest", (byte) 1),
            new Pair<>("Last used", (byte) 2)
    );

    private final List<Pair<String, Byte>> defaultForgeVersions = List.of(
            new Pair<>("Recommended", (byte) 0),
            new Pair<>("Newest", (byte) 1),
            new Pair<>("Oldest", (byte) 2)
    );

    private final ComboBox<Pair<String, Byte>> defaultMinecraftVersionChoose = new ComboBox<>();
    private final ComboBox<Pair<String, Byte>> chooseDefaultForgeVersion = new ComboBox<>();
    private final ComboBox<Pair<String, Boolean>> enableForgeCacheChoose = new ComboBox<>();
    private final ComboBox<Pair<String, Boolean>> enableForgeFileCacheChoose = new ComboBox<>();
    private final ComboBox<Pair<String, Boolean>> enableCustomLaunch = new ComboBox<>();
    private final TextField minecraftFolderField = new TextField();

    private final Stage stage = new Stage();

    public Settings() {
        System.out.println("@ Settings launched!");

        Button minecraftFolderButton = new Button("...");

        initialize();
        setActions();

        minecraftFolderButton.setOnAction((_) -> {
            String s = String.valueOf(new DirectoryChooser().showDialog(new Stage()));
            if (Objects.equals(s, "null")){s = "";}
            minecraftFolderField.setText(s);
        });

        HBox defaultMinecraftVersionFullBox = ComboHBoxGenerator( new Label("Default minecraft version:"), defaultMinecraftVersionChoose);
        HBox defaultForgeVersionFullBox = ComboHBoxGenerator(new Label("Default forge version:"), chooseDefaultForgeVersion);
        HBox enableForgeCachingFullBox = ComboHBoxGenerator(new Label("Cache forge versions:"), enableForgeCacheChoose);
        HBox enableForgeCachingFileFullBox = ComboHBoxGenerator(new Label("Cache forge versions to file:"), enableForgeFileCacheChoose);
        HBox customForgeLaunchFullBox = ComboHBoxGenerator(new Label("Enable custom forge launch: "), enableCustomLaunch);

        HBox folderChoose = new HBox(minecraftFolderField, minecraftFolderButton);
        folderChoose.setAlignment(Pos.CENTER);
        folderChoose.setSpacing(10);

        VBox folderLabel = new VBox(new Label("Choose minecraft folder: "));
        folderLabel.setAlignment(Pos.CENTER);

        HBox folderFullBox = new HBox(folderLabel, new Region(), folderChoose);
        HBox.setHgrow(folderFullBox.getChildren().get(1), Priority.ALWAYS);
        folderFullBox.setAlignment(Pos.CENTER);

        Label mainLabel = new Label("Settings");
        mainLabel.getStyleClass().add("main-label");

        VBox windowLayout = new VBox(
                mainLabel, defaultMinecraftVersionFullBox, defaultForgeVersionFullBox, createSeparator("Caching"),
                enableForgeCachingFullBox, enableForgeCachingFileFullBox, createSeparator("Custom forge launch"),
                customForgeLaunchFullBox, folderFullBox);

        windowLayout.getStyleClass().add("settings-vbox");


        Scene scene = new Scene(windowLayout);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        stage.setTitle("Settings");
        stage.setScene(scene);
    }

    private <T> HBox ComboHBoxGenerator(Label label, ComboBox<T> comboBox){
        label.getStyleClass().add("settings-label");
        VBox labelBox = new VBox(label);
        labelBox.setAlignment(Pos.CENTER_LEFT);

        comboBox.getStyleClass().add("combo-box");
        VBox comboBoxBox = new VBox(comboBox);
        comboBoxBox.setAlignment(Pos.CENTER_RIGHT);

        HBox fullBox = new HBox(label, new Region(), comboBox);
        HBox.setHgrow(fullBox.getChildren().get(1), Priority.ALWAYS);
        fullBox.setAlignment(Pos.CENTER);

        return fullBox;
    }

    private <T> void initializeComboBox(ComboBox<Pair<String, T>> comboBox, List<Pair<String, T>> comboBoxPairs){
        comboBox.getItems().addAll(comboBoxPairs);

        comboBox.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, T> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getKey());
                }
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, T> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getKey());
                }
            }
        });
    }

    protected void show() {
        stage.show();
    }

    private void setActions () {
        defaultMinecraftVersionChoose.setOnAction((_) -> {
            Universal.defaultMinecraftVersion = defaultMinecraftVersionChoose.getValue().getValue();
            System.out.println("@ Default minecraft version changed to: " + Universal.defaultMinecraftVersion);

            try {
                UFI.updateSettingsFile();
            } catch (IOException e) {
                UFI.updateStatusLabel((byte) 5);
                throw new RuntimeException(e);
            }
        });

        chooseDefaultForgeVersion.setOnAction((_) -> {
            Universal.defaultForgeVersion = chooseDefaultForgeVersion.getValue().getValue();
            System.out.println("@ Default forge version changed to: " + Universal.defaultForgeVersion);

            try {
                UFI.updateSettingsFile();
            } catch (IOException e) {
                UFI.updateStatusLabel((byte) 5);
                throw new RuntimeException(e);
            }
        });

        enableForgeCacheChoose.setOnAction((_) -> {
            Universal.enableForgeCaching = enableForgeCacheChoose.getValue().getValue();
            System.out.println("@ Forge versions cache is now: " + Universal.enableForgeCaching);

            try {
                UFI.updateSettingsFile();
            } catch (IOException e) {
                UFI.updateStatusLabel((byte) 5);
                throw new RuntimeException(e);
            }
        });

        enableForgeFileCacheChoose.setOnAction((_) -> {
            Universal.enableForgeFileCaching = enableForgeFileCacheChoose.getValue().getValue();
            System.out.println("@ Forge versions cache to file is now: " + Universal.enableForgeFileCaching);

            try {
                UFI.updateSettingsFile();
            } catch (IOException e){
                UFI.updateStatusLabel((byte) 5);
                throw new RuntimeException(e);
            }
        });

        enableCustomLaunch.setOnAction((_) -> {
            Universal.customForgeLaunch = enableCustomLaunch.getValue().getValue();
            System.out.println("@ Forge custom launch changed to: " + Universal.customForgeLaunch);

            try {
                UFI.updateSettingsFile();
                if (enableCustomLaunch.getValue().getValue()) {
                    UFI.downloadButton.setText("Download & Install");
                } else {
                    UFI.downloadButton.setText("Download & Launch");
                }
            } catch (IOException e) {
                UFI.updateStatusLabel((byte) 5);
                throw new RuntimeException(e);
            }
        });

        minecraftFolderField.textProperty().addListener((_, _, _) -> {
            Universal.minecraftFolder = minecraftFolderField.getText();
            System.out.println("@ Minecraft folder changed to: " + Universal.minecraftFolder);

            try {
                UFI.updateSettingsFile();
            } catch (IOException e) {
                UFI.updateStatusLabel((byte) 5);
                throw new RuntimeException(e);
            }
        });
    }

    private void initialize() {
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(minecraftFolderField.textProperty());
        Tooltip.install(minecraftFolderField, tooltip);

        minecraftFolderField.setText(Universal.minecraftFolder);

        chooseDefaultForgeVersion.setValue(getValue(defaultForgeVersions, Universal.defaultForgeVersion));
        defaultMinecraftVersionChoose.setValue(getValue(defaultMinecraftVersions, Universal.defaultMinecraftVersion));

        enableForgeCacheChoose.setValue(getValue(enableOrDisable, Universal.enableForgeCaching));
        enableForgeFileCacheChoose.setValue(getValue(enableOrDisable, Universal.enableForgeFileCaching));

        enableCustomLaunch.setValue(getValue(enableOrDisable, Universal.customForgeLaunch));

        minecraftFolderField.setOnContextMenuRequested(Event::consume);

        initializeComboBox(chooseDefaultForgeVersion, defaultForgeVersions);
        initializeComboBox(defaultMinecraftVersionChoose, defaultMinecraftVersions);
        initializeComboBox(enableForgeCacheChoose, enableOrDisable);
        initializeComboBox(enableForgeFileCacheChoose, enableOrDisable);
        initializeComboBox(enableCustomLaunch, enableOrDisable);
    }

    private <K, T> Pair<String, T> getValue(List<Pair<String, T>> list, K setting){
        return list.stream().filter(pair -> pair.getValue().equals(setting)).findFirst().orElse(new Pair<>("Unknown", null));
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
