import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
            new Pair<>("Every year", (byte) 4),
            new Pair<>("Every decade", (byte) 5),
            new Pair<>("Every century", (byte) 6),
            new Pair<>("Every millennium", (byte) 7),
            new Pair<>("Custom...", (byte) 8)
    );

    private final List<Pair<String, Byte>> customTimings = List.of(
            new Pair<>("hours", (byte) 0),
            new Pair<>("days", (byte) 1),
            new Pair<>("weeks", (byte) 2),
            new Pair<>("years", (byte) 3),
            new Pair<>("decades", (byte) 4),
            new Pair<>("centuries", (byte) 5),
            new Pair<>("millenniums", (byte) 6)
    );

    private final HBox customTimingsHBox;
    private final HBox enableTimings;

    private final ComboBox<Pair<String, Byte>> defaultMinecraftVersionChoose = new ComboBox<>();
    private final ComboBox<Pair<String, Byte>> chooseDefaultForgeVersion = new ComboBox<>();
    private final ComboBox<Pair<String, Boolean>> enableMinecraftFileCacheChoose = new ComboBox<>();
    private final ComboBox<Pair<String, Boolean>> enableForgeCacheChoose = new ComboBox<>();
    private final ComboBox<Pair<String, Boolean>> enableForgeFileCacheChoose = new ComboBox<>();
    private final ComboBox<Pair<String, Byte>> timingsChoose = new ComboBox<>();
    private final ComboBox<Pair<String, Byte>> customTimingsChoose = new ComboBox<>();
    private final ComboBox<Pair<String, Boolean>> enableCustomLaunch = new ComboBox<>();

    private final TextField minecraftFolderField = new TextField();
    private final TextField timingsField = new TextField();

    private final Button customizerButton = new Button("Open customizer");
    private final Button resetCacheButton = new Button("Reset cache");
    private final Button resetSettingsButton = new Button("Reset settings");

    private final Stage stage = new Stage();
    protected static Scene scene;

    public Settings() {
        System.out.println("@ Settings launched!");

        initialize();
        setActions();

        HBox defaultMinecraftVersionFullBox = Universal.ComboHBoxGenerator(new Label("Default minecraft version:"), defaultMinecraftVersionChoose);
        Universal.setToolTip(defaultMinecraftVersionFullBox, "The version of minecraft that is set when you start the application");

        HBox defaultForgeVersionFullBox = Universal.ComboHBoxGenerator(new Label("Default forge version:"), chooseDefaultForgeVersion);
        Universal.setToolTip(defaultForgeVersionFullBox, "The version of forge that is set when you start the application");

        HBox enableMinecraftFileCachingFullBox = Universal.ComboHBoxGenerator(new Label("Cache minecraft versions into file:"), enableMinecraftFileCacheChoose);
        Universal.setToolTip(enableMinecraftFileCachingFullBox, "Enable or disable saving a list of minecraft versions to a file");

        HBox enableForgeCachingFullBox = Universal.ComboHBoxGenerator(new Label("Cache forge versions:"), enableForgeCacheChoose);
        Universal.setToolTip(enableForgeCachingFullBox, "Enable or disable saving forge versions to a RAM");

        HBox enableForgeCachingFileFullBox = Universal.ComboHBoxGenerator(new Label("Cache forge versions to file:"), enableForgeFileCacheChoose);
        Universal.setToolTip(enableForgeCachingFileFullBox, "Enable or disable saving forge versions to a file");

        enableTimings = Universal.ComboHBoxGenerator(new Label("Enable versions list auto-update: "), timingsChoose); // Утфиду фгещ-гзвфеу ща мукышщты дшые...
        Universal.setToolTip(enableTimings, "Frequency of minecraft and forge version list updates");

        HBox customForgeLaunchFullBox = Universal.ComboHBoxGenerator(new Label("Enable custom forge launch:"), enableCustomLaunch);
        Universal.setToolTip(customForgeLaunchFullBox, "Enable or disable headless forge installation");

        HBox folderFullBox = createFolderChooseHBox();
        Universal.setToolTip(folderFullBox, "Location of minecraft folder(By default in linux: ~/.minecraft, in windows %APPDATA%\\.minecraft)");

        customTimingsHBox = createCustomTimingsChoose(new Label("Update frequency:"), new Label("every"));

        customTimingsHBox.setVisible(Universal.baseTimings == 8);
        customTimingsHBox.setManaged(Universal.baseTimings == 8);

        enableTimings.setVisible(Universal.isCacheEnabled());
        enableTimings.setManaged(Universal.isCacheEnabled());

        Label mainLabel = new Label("Settings");
        mainLabel.getStyleClass().add("label-main");

        VBox windowLayout = new VBox(
                mainLabel, defaultMinecraftVersionFullBox, defaultForgeVersionFullBox,
                Universal.createSeparator("Caching"), enableMinecraftFileCachingFullBox, enableForgeCachingFullBox, enableForgeCachingFileFullBox,
                enableTimings, customTimingsHBox,
                Universal.createSeparator("Custom forge launch"), customForgeLaunchFullBox, folderFullBox,
                Universal.createSeparator("Customize"), ButtonHBoxGenerator(customizerButton),
                Universal.createSeparator("Reset"), ButtonHBoxGenerator(resetCacheButton), ButtonHBoxGenerator(resetSettingsButton)
        );

        windowLayout.getStyleClass().add("settings-vbox");

        scene = new Scene(windowLayout);

        if (Universal.isDarkMode) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles-dark.css")).toExternalForm());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles-light.css")).toExternalForm());
        }

        stage.setTitle("Settings");
        stage.setScene(scene);
    }

    private HBox createCustomTimingsChoose(Label label, Label label2) {
        label.getStyleClass().add("settings-label");

        customTimingsChoose.getStyleClass().add("combo-box");
        timingsField.setPrefWidth(50);

        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().length() <= 4) {
                return change;
            } else {
                return null;
            }
        });

        timingsField.setTextFormatter(formatter);

        HBox fullBox = new HBox(label, new Region(), label2, timingsField, customTimingsChoose);
        HBox.setHgrow(fullBox.getChildren().get(1), Priority.ALWAYS);
        fullBox.setSpacing(10);

        return fullBox;
    }

    private HBox createFolderChooseHBox() {
        Button minecraftFolderButton = new Button("...");

        minecraftFolderButton.setOnAction((_) -> {
            String s = String.valueOf(new DirectoryChooser().showDialog(new Stage()));
            if (Objects.equals(s, "null")){s = "";}
            minecraftFolderField.setText(s);
        });

        HBox folderChoose = new HBox(minecraftFolderField, minecraftFolderButton);
        folderChoose.setSpacing(10);

        VBox folderLabel = new VBox(new Label("Choose minecraft folder: "));
        folderLabel.getStyleClass().add("settings-label");

        HBox folderFullBox = new HBox(folderLabel, new Region(), folderChoose);
        HBox.setHgrow(folderFullBox.getChildren().get(1), Priority.ALWAYS);

        return folderFullBox;
    }

    private HBox ButtonHBoxGenerator(Button button) {
        button.getStyleClass().add("button");
        HBox hbox = new HBox(button);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    protected void show() {
        stage.show();
    }

    private void setActions() {
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

            enableTimings.setVisible(Universal.isCacheEnabled());
            enableTimings.setManaged(Universal.isCacheEnabled());

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

            enableTimings.setVisible(Universal.isCacheEnabled());
            enableTimings.setManaged(Universal.isCacheEnabled());

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

            enableTimings.setVisible(Universal.isCacheEnabled());
            enableTimings.setManaged(Universal.isCacheEnabled());

            try {
                UFI.updateSettingsFile();
            } catch (IOException e){
                UFI.updateStatusLabel((byte) 5);
                throw new RuntimeException(e);
            }
        });

        timingsChoose.setOnAction((_) -> {
            Universal.baseTimings = timingsChoose.getValue().getValue();
            System.out.println("@ Enable versions list auto-update is now: " + Universal.baseTimings);

            customTimingsHBox.setVisible(Universal.baseTimings == 8);
            customTimingsHBox.setManaged(Universal.baseTimings == 8);

            if (Universal.baseTimings != 8) {Updater.setTiming(Universal.baseTimings);}

            try {
                UFI.updateSettingsFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        customTimingsChoose.setOnAction((_) -> {
            Universal.customTimings = new Pair<>(Universal.customTimings.getKey(), customTimingsChoose.getValue().getValue());
            System.out.println("@ Custom timing is now: " + Universal.customTimings);

            Updater.setCustomTiming(Universal.customTimings);

            try {
                UFI.updateSettingsFile();
            } catch (IOException e) {
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

        timingsField.textProperty().addListener((_, _, _) -> {
            Universal.customTimings = new Pair<>(Short.parseShort(timingsField.getText()), customTimingsChoose.getValue().getValue());
            System.out.println("@ Custom timing is now: " + Universal.customTimings);

            Updater.setCustomTiming(Universal.customTimings);

            try {
                UFI.updateSettingsFile();
            } catch (IOException e) {
                UFI.updateStatusLabel((byte) 5);
                throw new RuntimeException(e);
            }
        });

        customizerButton.setOnAction((_) -> new Customizer().show());
        
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
                Universal.customTimings = new Pair<>((short) 1, (byte) 1);
                Universal.baseTimings = 0;

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
        timingsField.setText(String.valueOf(Universal.customTimings.getKey()));

        chooseDefaultForgeVersion.setValue(Universal.getValue(defaultForgeVersions, Universal.defaultForgeVersion));
        defaultMinecraftVersionChoose.setValue(Universal.getValue(defaultMinecraftVersions, Universal.defaultMinecraftVersion));

        enableMinecraftFileCacheChoose.setValue(Universal.getValue(enableOrDisable, Universal.enableMinecraftFileCaching));
        enableForgeCacheChoose.setValue(Universal.getValue(enableOrDisable, Universal.enableForgeCaching));
        enableForgeFileCacheChoose.setValue(Universal.getValue(enableOrDisable, Universal.enableForgeFileCaching));
        timingsChoose.setValue(Universal.getValue(timings, Universal.baseTimings));
        customTimingsChoose.setValue(Universal.getValue(customTimings, Universal.customTimings.getValue()));

        enableCustomLaunch.setValue(Universal.getValue(enableOrDisable, Universal.customForgeLaunch));
    }

    private void initialize() {
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(minecraftFolderField.textProperty());
        Tooltip.install(minecraftFolderField, tooltip);

        updateValues();

        minecraftFolderField.setOnContextMenuRequested(Event::consume);

        Universal.initializeComboBox(chooseDefaultForgeVersion, defaultForgeVersions);
        Universal.initializeComboBox(defaultMinecraftVersionChoose, defaultMinecraftVersions);
        Universal.initializeComboBox(enableMinecraftFileCacheChoose, enableOrDisable);
        Universal.initializeComboBox(enableForgeCacheChoose, enableOrDisable);
        Universal.initializeComboBox(enableForgeFileCacheChoose, enableOrDisable);
        Universal.initializeComboBox(enableCustomLaunch, enableOrDisable);
        Universal.initializeComboBox(timingsChoose, timings);
        Universal.initializeComboBox(customTimingsChoose, customTimings);
    }
}