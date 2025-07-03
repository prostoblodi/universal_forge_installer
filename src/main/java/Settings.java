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

    private final List<Pair<String, Byte>> timings = List.of(
            new Pair<>("Never", (byte) 0),
            new Pair<>("Every day", (byte) 1),
            new Pair<>("Every week", (byte) 2),
            new Pair<>("Every month", (byte) 3),
            new Pair<>("Custom...", (byte) 4)
    );

    private final ComboBox<Pair<String, Byte>> defaultMinecraftVersionChoose = new ComboBox<>();
    private final ComboBox<Pair<String, Byte>> chooseDefaultForgeVersion = new ComboBox<>();
    private final ComboBox<Pair<String, Boolean>> enableMinecraftFileCacheChoose = new ComboBox<>();
    private final ComboBox<Pair<String, Boolean>> enableForgeCacheChoose = new ComboBox<>();
    private final ComboBox<Pair<String, Boolean>> enableForgeFileCacheChoose = new ComboBox<>();
    private final ComboBox<Pair<String, Boolean>> enableCustomLaunch = new ComboBox<>();
    private final TextField minecraftFolderField = new TextField();

    private final Button resetCacheButton = new Button("Reset cache");
    private final Button resetSettingsButton = new Button("Reset settings");

    private final Stage stage = new Stage();

    public Settings() {
        System.out.println("@ Settings launched!");

        initialize();
        setActions();

        HBox defaultMinecraftVersionFullBox = ComboHBoxGenerator( new Label("Default minecraft version:"), defaultMinecraftVersionChoose);
        HBox defaultForgeVersionFullBox = ComboHBoxGenerator(new Label("Default forge version:"), chooseDefaultForgeVersion);
        HBox enableMinecraftFileCachingFullBox = ComboHBoxGenerator(new Label("Cache minecraft versions into file:"), enableMinecraftFileCacheChoose);
        HBox enableForgeCachingFullBox = ComboHBoxGenerator(new Label("Cache forge versions:"), enableForgeCacheChoose);
        HBox enableForgeCachingFileFullBox = ComboHBoxGenerator(new Label("Cache forge versions to file:"), enableForgeFileCacheChoose);
        HBox customForgeLaunchFullBox = ComboHBoxGenerator(new Label("Enable custom forge launch: "), enableCustomLaunch);
        HBox folderFullBox = createFolderChooseHBox();

        Label mainLabel = new Label("Settings");
        mainLabel.getStyleClass().add("label-main");

        VBox windowLayout = new VBox(
                mainLabel, defaultMinecraftVersionFullBox, defaultForgeVersionFullBox,
                createSeparator("Caching"), enableMinecraftFileCachingFullBox, enableForgeCachingFullBox, enableForgeCachingFileFullBox,
                createSeparator("Custom forge launch"), customForgeLaunchFullBox, folderFullBox,
                createSeparator("Reset"), ButtonHBoxGenerator(resetCacheButton), ButtonHBoxGenerator(resetSettingsButton)
        );

        windowLayout.getStyleClass().add("settings-vbox");

        Scene scene = new Scene(windowLayout);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        stage.setTitle("Settings");
        stage.setScene(scene);
    }

    private HBox createFolderChooseHBox(){
        Button minecraftFolderButton = new Button("...");

        minecraftFolderButton.setOnAction((_) -> {
            String s = String.valueOf(new DirectoryChooser().showDialog(new Stage()));
            if (Objects.equals(s, "null")){s = "";}
            minecraftFolderField.setText(s);
        });

        HBox folderChoose = new HBox(minecraftFolderField, minecraftFolderButton);
        folderChoose.setAlignment(Pos.CENTER);
        folderChoose.setSpacing(10);

        VBox folderLabel = new VBox(new Label("Choose minecraft folder: "));
        folderLabel.setAlignment(Pos.CENTER);
        folderLabel.getStyleClass().add("settings-label");

        HBox folderFullBox = new HBox(folderLabel, new Region(), folderChoose);
        HBox.setHgrow(folderFullBox.getChildren().get(1), Priority.ALWAYS);
        folderFullBox.setAlignment(Pos.CENTER);

        return folderFullBox;
    }

    private HBox ButtonHBoxGenerator(Button button){
        button.getStyleClass().add("button");
        HBox hbox = new HBox(button);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
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

    private <K, T> Pair<String, T> getValue(List<Pair<String, T>> list, K setting){
        return list.stream().filter(pair -> pair.getValue().equals(setting)).findFirst().orElse(new Pair<>("Unknown", null));
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

        enableMinecraftFileCacheChoose.setOnAction((_) -> {
            Universal.enableMinecraftFileCaching = enableMinecraftFileCacheChoose.getValue().getValue();
            System.out.println("@ Minecraft versions file cache is now: " + Universal.enableMinecraftFileCaching);

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

        resetCacheButton.setOnAction((_) -> {
            if (Universal.cacheFile.delete()) {
                System.out.println("@ Cache file at " + Universal.cachePath + " deleted(cache reset successfully)");
                Universal.minecraftVersions.clear();
                Universal.lastUsedMinecraftVersion = "";
                Universal.minecraftToForgeVersions.clear();
                Universal.minecraftToSpecifiedForgeVersions.clear();

                try {
                    UFI.showMinecraftVersions(true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("@ Cache file at " + Universal.cachePath + " isn't deleted(cache reset failed)");
            }
        });

        resetSettingsButton.setOnAction((_) -> {
            if (Universal.settingsFile.delete()){
                System.out.println("@ Settings file at " + Universal.settingsPath + " deleted(settings reset)");

                Universal.enableMinecraftFileCaching = false;
                Universal.enableForgeCaching = false;
                Universal.enableForgeFileCaching = false;
                Universal.customForgeLaunch = true;
                Universal.defaultMinecraftVersion = (byte) 0;
                Universal.defaultForgeVersion = (byte) 0;
                Universal.minecraftFolder = "";

                try {
                    UFI.updateSettingsFile();
                    UFI.checkSettings();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                updateValues();
            }
        });
    }

    private void updateValues() {
        minecraftFolderField.setText(Universal.minecraftFolder);

        chooseDefaultForgeVersion.setValue(getValue(defaultForgeVersions, Universal.defaultForgeVersion));
        defaultMinecraftVersionChoose.setValue(getValue(defaultMinecraftVersions, Universal.defaultMinecraftVersion));

        enableMinecraftFileCacheChoose.setValue(getValue(enableOrDisable, Universal.enableMinecraftFileCaching));
        enableForgeCacheChoose.setValue(getValue(enableOrDisable, Universal.enableForgeCaching));
        enableForgeFileCacheChoose.setValue(getValue(enableOrDisable, Universal.enableForgeFileCaching));

        enableCustomLaunch.setValue(getValue(enableOrDisable, Universal.customForgeLaunch));
    }

    private void initialize() {
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(minecraftFolderField.textProperty());
        Tooltip.install(minecraftFolderField, tooltip);

        updateValues();

        minecraftFolderField.setOnContextMenuRequested(Event::consume);

        initializeComboBox(chooseDefaultForgeVersion, defaultForgeVersions);
        initializeComboBox(defaultMinecraftVersionChoose, defaultMinecraftVersions);
        initializeComboBox(enableMinecraftFileCacheChoose, enableOrDisable);
        initializeComboBox(enableForgeCacheChoose, enableOrDisable);
        initializeComboBox(enableForgeFileCacheChoose, enableOrDisable);
        initializeComboBox(enableCustomLaunch, enableOrDisable);
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