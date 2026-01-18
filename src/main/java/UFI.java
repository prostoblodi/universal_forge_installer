import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UFI extends Application {

    private static final Button reload = new Button();
    private final Button theme = new Button();
    private final Button resetFiles = new Button();

    private static final ComboBox<String> chooseMinecraftVersion = new ComboBox<>();
    private static final ComboBox<Pair<String, Short>> chooseForgeVersion = new ComboBox<>();

    private static String minecraftVersion = "";
    private static Pair<String, Short> forgeVersion = new Pair<>("", (short) -1);

    private final Label mainLabel = new Label("Universal Forge Installer");
    private final Label minecraftVersionLabel = new Label("Minecraft version: ");
    private final Label forgeVersionLabel = new Label("Forge version: ");
    private static final Label statusLabel = new Label("");
    private static final SimpleStringProperty textProperty = new SimpleStringProperty("");

    protected static final Button downloadButton = new Button("Download & Launch");
    private final Button settingsButton = new Button("Settings");

    protected static Scene scene;

    @Override
    public void start(Stage primaryStage) throws IOException {
        GridPane gp = new GridPane();
        gp.add(minecraftVersionLabel, 0, 1);
        gp.add(chooseMinecraftVersion, 1, 1);
        gp.add(reload, 2, 1);
        gp.add(forgeVersionLabel, 0, 2);
        gp.add(chooseForgeVersion, 1, 2);
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setAlignment(Pos.CENTER);

        statusLabel.setMinWidth(200);
        statusLabel.setAlignment(Pos.CENTER);

        BorderPane down = new BorderPane();
        BorderPane.setMargin(statusLabel, new Insets(0, 0, 0, 40));
        down.setCenter(statusLabel);
        down.setRight(theme);
        down.setLeft(resetFiles);

        Universal.setToolTip(reload, "Reset versions cache");
        Universal.setToolTip(theme, Universal.isDarkMode ? "Change theme to light" : "Change theme to dark");
        Universal.setToolTip(resetFiles, "Delete forge files");

        VBox vbox = new VBox(mainLabel, gp, downloadButton, settingsButton, down);

        scene = new Scene(vbox);

        initialize(vbox);

        primaryStage.setTitle("Universal Forge Installer");
        primaryStage.setScene(scene);
        primaryStage.show();

        setLBStyles();

        new Thread(() -> {
            try {
                Platform.runLater(() -> {
                    try {
                        showMinecraftVersions(false);
                        Platform.runLater(() -> updateStatusLabel((byte) 0));
                    } catch (IOException e) {
                        UFI.updateStatusLabel((byte) 5);
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                updateStatusLabel((byte) 5);
            }
        }).start();
    }

    private void initialize(VBox vbox) throws IOException {
        if (!new File(System.getProperty("user.home"), "UFI").exists()){
            System.out.println(new File(String.valueOf(Paths.get(System.getProperty("user.home"), "UFI"))).mkdirs());
        }

        checkSettings();
        setActions();

        statusLabel.textProperty().bind(textProperty);
        vbox.getStyleClass().add("vbox");

        if (Universal.isDarkMode) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles-dark.css")).toExternalForm());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles-light.css")).toExternalForm());
        }

        if (Universal.isCacheEnabled()) {
            checkCache();
            Updater.checkUpdates();
        }

        updateReloadButton();
    }

    private void setLBStyles() { // LB means Labels and Buttons
        mainLabel.getStyleClass().add("label-main");
        minecraftVersionLabel.getStyleClass().add("label");
        forgeVersionLabel.getStyleClass().add("label");
        reload.getStyleClass().add("button");
        downloadButton.getStyleClass().add("button");
        settingsButton.getStyleClass().add("button");
        statusLabel.getStyleClass().add("status-label");
        reload.getStyleClass().add("reload-button");
        theme.getStyleClass().add("theme-button");
        resetFiles.getStyleClass().add("bin-button");
    }

    private void setActions() {
        chooseMinecraftVersion.setOnAction(
                (_) -> new Thread(() -> {
                    try {
                        saveMinecraftVersion(false);
                        Platform.runLater(() -> updateStatusLabel((byte) 0));
                    } catch (IOException e) {
                        UFI.updateStatusLabel((byte) 5);
                        throw new RuntimeException(e);
                    }
                }).start()
        );

        chooseForgeVersion.setOnAction(
                (_) -> saveForgeVersion()
        );

        downloadButton.setOnAction(
                (_) -> {
                    if (!minecraftVersion.isEmpty() && !forgeVersion.getKey().isEmpty()) {
                        updateStatusLabel((byte) 1); // downloading
                        new Thread(() -> {
                            try {
                                Installer.download_forge(minecraftVersion, forgeVersion);
                            } catch (IOException | URISyntaxException e) {
                                updateStatusLabel((byte) 5); // error
                                Thread.currentThread().interrupt();
                            }

                            if (!Thread.currentThread().isInterrupted()) {
                                Platform.runLater(() -> {
                                    updateStatusLabel((byte) 2); // downloaded

                                    if (Universal.howOldIndex(minecraftVersion) >= 1) {
                                        if (Universal.customForgeLaunch) {
                                            updateStatusLabel((byte) 6); // installing
                                        } else {
                                            updateStatusLabel((byte) 8); // launching
                                        }

                                        new Thread(() -> {
                                            try {
                                                Installer.run_forge();
                                            } catch (RuntimeException e) {
                                                updateStatusLabel((byte) 5);
                                                Thread.currentThread().interrupt();
                                            }

                                            if (!Thread.currentThread().isInterrupted()) {
                                                Platform.runLater(() -> {
                                                    if (Universal.customForgeLaunch) {
                                                        updateStatusLabel((byte) 7); // installed
                                                    } else {
                                                        updateStatusLabel((byte) 9); // launched
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                });
                            }
                        }).start();
                    }
                });

        settingsButton.setOnAction((_) -> new Settings().show());

        reload.setOnAction((_) -> new Thread(() -> {
                Universal.minecraftVersions.clear();
                Universal.minecraftToForgeVersions.clear();
                Universal.minecraftToSpecifiedForgeVersions.clear();

                try {
                    showMinecraftVersions(true);
                    Platform.runLater(() -> updateStatusLabel((byte) 0));
                } catch (IOException e) {
                    UFI.updateStatusLabel((byte) 5);
                    throw new RuntimeException(e);
                }
            }).start());

        theme.setOnAction((_) -> {
            Universal.isDarkMode = !Universal.isDarkMode;

            try {
                updateSettingsFile();
            } catch (IOException e) {
                updateStatusLabel((byte) 5);
                throw new RuntimeException(e);
            }

            updateThemes(scene, Settings.scene, Customizer.scene);
        });

        resetFiles.setOnAction((_) -> new Thread(() -> {
            try {
                Files.delete(Universal.forgeJarsDir);
            } catch (IOException e) {
                updateStatusLabel((byte) 5);
            }
        }).start());
    }

    private static void saveMinecraftVersion(boolean ignoreCache) throws IOException {
        minecraftVersion = chooseMinecraftVersion.getValue();
        Universal.lastUsedMinecraftVersion = minecraftVersion;

        if (Universal.isCacheEnabled()) {updateCacheFile();}
        updateForgeVersions(ignoreCache);

        System.out.println("* Saved minecraft version as: " + minecraftVersion);
    }

    private void saveForgeVersion() {
        forgeVersion = chooseForgeVersion.getValue();
        System.out.println("* Saved forge version as: " + forgeVersion);
    }

    protected static void showMinecraftVersions(boolean ignoreCache) throws IOException {
        if (Universal.enableMinecraftFileCaching && Universal.minecraftVersions.isEmpty()){
            Universal.minecraftVersions = getMinecraftVersions();
            updateCacheFile();
        }

        List<String> assetClasses = Universal.enableMinecraftFileCaching ? Universal.minecraftVersions : getMinecraftVersions();

        Platform.runLater(() -> {
            chooseMinecraftVersion.getItems().setAll(assetClasses);
            if (Universal.defaultMinecraftVersion == 1){
                chooseMinecraftVersion.setValue(assetClasses.getFirst());
            } else if (Universal.defaultMinecraftVersion == 2 && !Objects.equals(Universal.lastUsedMinecraftVersion, "null")){
                chooseMinecraftVersion.setValue(Universal.lastUsedMinecraftVersion);
                try {
                    saveMinecraftVersion(ignoreCache);
                } catch (IOException e) {
                    UFI.updateStatusLabel((byte) 5);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static void updateForgeVersions(boolean ignoreCache) throws IOException {
        List<Pair<String, Short>> assetClasses;


        if (Universal.enableForgeCaching && !ignoreCache){
            assetClasses = Universal.minecraftToForgeVersions.get(minecraftVersion) != null ? Universal.minecraftToForgeVersions.get(minecraftVersion) : getForgeVersions();
        } else {
            assetClasses = getForgeVersions();
        }

        Platform.runLater(() -> {
            chooseForgeVersion.getItems().setAll(assetClasses);
            switch (Universal.defaultForgeVersion) {
                case 0 -> chooseForgeVersion.setValue(assetClasses.stream().filter(pair -> pair.getKey().equals(Universal.minecraftToSpecifiedForgeVersions.get(minecraftVersion).get(1))).findFirst().orElse(new Pair<>("Unknown", (short) -1)));
                case 1 -> chooseForgeVersion.setValue(assetClasses.stream().filter(pair -> pair.getKey().equals(Universal.minecraftToSpecifiedForgeVersions.get(minecraftVersion).getFirst())).findFirst().orElse(new Pair<>("Unknown", (short) -1)));
                case 2 -> chooseForgeVersion.setValue(assetClasses.stream().filter(pair -> pair.getKey().equals(Universal.minecraftToSpecifiedForgeVersions.get(minecraftVersion).getLast())).findFirst().orElse(new Pair<>("Unknown", (short) -1)));
            }

            if (minecraftVersion == null || minecraftVersion.replaceAll(" ", "").isEmpty()){
                chooseForgeVersion.setValue(null);
            }

            if (Universal.enableForgeCaching) {
                Universal.minecraftToForgeVersions.putIfAbsent(minecraftVersion, assetClasses);
                try {
                    updateCacheFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            chooseForgeVersion.setCellFactory(_ -> new ListCell<>() {
                @Override
                protected void updateItem(Pair<String, Short> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.getKey());
                    }
                }
            });

            chooseForgeVersion.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Pair<String, Short> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.getKey());
                    }
                }
            });
        });
    }

    private static List<String> getMinecraftVersions() throws IOException {
        updateStatusLabel((byte) 3);

        List<String> versions = Installer.getMinecraftVersionsForForge();
        Platform.runLater(() -> updateStatusLabel((byte) 0));

        return versions;
    }

    private static List<Pair<String, Short>> getForgeVersions() throws IOException {
        updateStatusLabel((byte) 4);

        List<String> output = Installer.getForgeVersionsForMinecraft(minecraftVersion);
        List<Pair<String, Short>> assetClasses = new ArrayList<>();

        for (String version : output) {
            assetClasses.add(new Pair<>(version, (short) output.indexOf(version)));
        }

        return assetClasses;
    }

    /**
     * Updates the status label based on the provided status code.
     *
     * @param status the status code indicating the current operation:
     *               <ul>
     *                 <li><code>0</code>: Idle</li>
     *                 <li><code>1</code>: Downloading...</li>
     *                 <li><code>2</code>: Downloaded!</li>
     *                 <li><code>3</code>: Receiving Minecraft versions...</li>
     *                 <li><code>4</code>: Receiving Forge versions...</li>
     *                 <li><code>5</code>: Error occurred!</li>
     *                 <li><code>6</code>: Installing...</li>
     *                 <li><code>7</code>: Installed!</li>
     *                 <li><code>8</code>: Launching...</li>
     *                 <li><code>9</code>: Launched!</li>
     *               </ul>
     */
    protected static void updateStatusLabel(byte status) {
        Platform.runLater(() -> {
            switch (status) {
                case 0 -> textProperty.set(""); // idle
                case 1 -> textProperty.set("Downloading...");
                case 2 -> textProperty.set("Downloaded!");
                case 3 -> textProperty.set("Receiving Minecraft versions...");
                case 4 -> textProperty.set("Receiving Forge versions...");
                case 5 -> textProperty.set("Error occurred!");
                case 6 -> textProperty.set("Installing...");
                case 7 -> textProperty.set("Installed!");
                case 8 -> textProperty.set("Launching...");
                case 9 -> textProperty.set("Launched!");
            }
            System.out.println("Current status: " + (textProperty.get().isEmpty() ? "idle(nothing)" : textProperty.get()));
        });
    }

    private static void updateReloadButton() {
        reload.setVisible(Universal.isCacheEnabled());
        reload.setManaged(Universal.isCacheEnabled());
    }

    protected static void checkSettings() throws IOException {
        System.out.println("Г Checking settings...");

        if (!Universal.settingsFile.exists()) {
            System.out.println("| Settings file do not exists at " + Universal.settingsPath);

            Files.createFile(Path.of(Universal.settingsPath));

            Universal.defaultMinecraftVersion = 0;
            Universal.defaultForgeVersion = 0;
            Universal.customForgeLaunch = true;

            if (System.getProperty("os.name").toLowerCase().contains("win")){
                Universal.minecraftFolder = String.valueOf(new File(System.getenv("APPDATA"), ".minecraft"));
            } else {
                Universal.minecraftFolder = String.valueOf(new File(System.getProperty("user.home"), ".minecraft"));
            }

            updateSettingsFile();

            System.out.println("| Settings file created: " + Universal.settingsPath);
        } else {
            System.out.println("| Settings file already exists at " + Universal.settingsPath);

            List<String> lines = Files.readAllLines(Paths.get(Universal.settingsPath));
            String[] data;

            for (String line : lines) {
                if (line.contains("defaultForgeVersionByte")){
                    data = line.split("=");
                    Universal.defaultForgeVersion = Byte.parseByte(data[1]);
                } else if (line.contains("customForgeLaunch")) {
                    data = line.split("=");
                    Universal.customForgeLaunch = Boolean.parseBoolean(data[1]);
                } else if (line.contains("minecraftFolder")) {
                    data = line.split("=");
                    if (data.length > 1) {
                        Universal.minecraftFolder = data[1];
                    } else {
                        if (System.getProperty("os.name").toLowerCase().contains("win")){
                            Universal.minecraftFolder = String.valueOf(new File(System.getenv("APPDATA"), ".minecraft"));
                        } else {
                            Universal.minecraftFolder = String.valueOf(new File(System.getProperty("user.home"), ".minecraft"));
                        }
                    }
                } else if (line.contains("defaultMinecraftVersionByte")) {
                    data = line.split("=");
                    Universal.defaultMinecraftVersion = Byte.parseByte(data[1]);
                } else if (line.contains("enableForgeCaching")) {
                    data = line.split("=");
                    Universal.enableForgeCaching = Boolean.parseBoolean(data[1]);
                } else if (line.contains("enableForgeFileCaching")) {
                    data = line.split("=");
                    Universal.enableForgeFileCaching = Boolean.parseBoolean(data[1]);
                } else if (line.contains("enableMinecraftFileCaching")){
                    data = line.split("=");
                    Universal.enableMinecraftFileCaching = Boolean.parseBoolean(data[1]);
                } else if (line.contains("baseTimings")){
                    data = line.split("=");
                    Universal.baseTimings = Byte.parseByte(data[1]);
                } else if (line.contains("customTimings")) {
                    data = line.split("=");
                    Universal.customTimings = new Pair<>(Short.parseShort(data[1]), Byte.parseByte(data[2]));
                } else if (line.contains("isDarkMode")) {
                    data = line.split("=");
                    Universal.isDarkMode = Boolean.parseBoolean(data[1]);
                } else if (line.contains("extendCustomizer")){
                    data = line.split("=");
                    Universal.extendCustomizer = Boolean.parseBoolean(data[1]);
                }
            }
        }

        System.out.printf(
                "* Saved settings as: defaultForgeVersion: %d, customForgeLaunch: %b,%n" +
                "| minecraftFolder: %s, defaultMinecraftVersion: %d,%n" +
                "| enableForgeCache: %b, enableForgeFileCache: %b,%n" +
                "L enableMinecraftFileCaching: %b, baseTimings: %b, customTimings: %s%n%n",
                Universal.defaultForgeVersion, Universal.customForgeLaunch, Universal.minecraftFolder,
                Universal.defaultForgeVersion, Universal.enableForgeCaching, Universal.enableForgeFileCaching,
                Universal.enableMinecraftFileCaching, Universal.baseTimings, Universal.customTimings
        );

        if (Universal.customForgeLaunch) {
            downloadButton.setText("Download & Install");
        } else {
            downloadButton.setText("Download & Launch");
        }
    }

    protected static void checkCache() throws IOException {
        System.out.println("Г Checking cache...");

        if (!Universal.cacheFile.exists()){
            System.out.println("| Cache file do not exists at " + Universal.cachePath);
            Files.createFile(Path.of(Universal.cachePath));
            updateCacheFile();
        } else {
            System.out.println("| Cache file already exists at " + Universal.cachePath);

            List<String> lines = Files.readAllLines(Paths.get(Universal.cachePath));
            String[] data;

            for (String line : lines){
                if (line.contains("defaultMinecraftVersion")){
                    data = line.split("=");
                    Universal.lastUsedMinecraftVersion = data.length >= 2 ? data[1] : null;
                } else if (line.contains("minecraftToForgeVersions") && Universal.enableForgeFileCaching) {
                    data = line.split("=", 2);
                    if (!Objects.equals(data[1].replaceAll(" ", ""), "{}")) {
                        parseToHashMap(data[1], Universal.minecraftToForgeVersions, true);
                    }
                } else if (line.contains("minecraftToSpecifiedForgeVersions") && Universal.enableForgeFileCaching) {
                    data = line.split("=", 2);
                    if (!Objects.equals(data[1].replaceAll(" ", ""), "{}")) {
                        parseToHashMap(data[1], Universal.minecraftToSpecifiedForgeVersions, false);
                    }
                } else if (line.contains("minecraftVersions") && Universal.enableMinecraftFileCaching){
                    data = line.split("=");
                    for (String i : data[1].replace("[", "").replace("]", "").split(",")){
                        Universal.minecraftVersions.add(i.replaceAll(" ", ""));
                    }
                } else if (line.contains("lastRun")) {
                    data = line.split("=");
                    Updater.lastRun = Instant.ofEpochMilli(Long.parseLong(data[1]));
                }
            }
        }

        System.out.println("L Cache successfully checked.");
    }

    protected static void updateSettingsFile() throws IOException {
        if(!Universal.settingsFile.exists()){
            System.out.println(Universal.settingsFile.createNewFile());
        }

        try (FileWriter writer = new FileWriter(Universal.settingsFile)) {
            writer.write(String.format(
                    "defaultForgeVersionByte=%d%ncustomForgeLaunch=%b%nminecraftFolder=%s%ndefaultMinecraftVersionByte=%d%n" +
                    "enableForgeCaching=%b%nenableForgeFileCaching=%b%nenableMinecraftFileCaching=%b%nbaseTimings=%d%ncustomTimings=%s%n" +
                    "isDarkMode=%b%nextendCustomizer=%b",
                    Universal.defaultForgeVersion, Universal.customForgeLaunch, Universal.minecraftFolder, Universal.defaultMinecraftVersion,
                    Universal.enableForgeCaching, Universal.enableForgeFileCaching, Universal.enableMinecraftFileCaching, Universal.baseTimings,
                    Universal.customTimings, Universal.isDarkMode, Universal.extendCustomizer
            ));
        }

        updateReloadButton();
    }

    protected static void updateCacheFile() throws IOException {
        try (FileWriter writer = new FileWriter(Universal.cacheFile)){
            writer.write(String.format("defaultMinecraftVersion=%s%nminecraftToForgeVersions=%s%nminecraftToSpecifiedForgeVersions=%s%nminecraftVersions=%s%nlastRun=%s",
                    Universal.lastUsedMinecraftVersion, Universal.minecraftToForgeVersions, Universal.minecraftToSpecifiedForgeVersions, Universal.minecraftVersions, Updater.lastRun.toEpochMilli()));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void parseToHashMap(String line, HashMap<String, List<T>> hashMap, boolean isPair) {
        line = line.replace("{", "").replace("}", "").replaceAll(" ", "");

        if (isPair) {
            for (String entry : line.split("],")) {
                entry = entry.replace("]", "");
                String[] keyValue = entry.split("=\\[");

                String key = keyValue[0];
                List<Pair<String, Byte>> pairList = new ArrayList<>();

                for (String item : keyValue[1].split(",")) {
                    String[] pair = item.split("=");
                    String pairKey = pair[0];
                    byte pairValue = Byte.parseByte(pair[1]);
                    pairList.add(new Pair<>(pairKey, pairValue));
                }

                hashMap.put(key, (List<T>) pairList);
            }
        } else {
            for (String entry : line.split("],")) {
                entry = entry.replace("]", "");
                String[] keyValue = entry.split("=\\[");

                String key = keyValue[0];

                List<String> stringList = new ArrayList<>(Arrays.asList(keyValue[1].split(",")));

                hashMap.put(key, (List<T>) stringList);
            }
        }
    }

    protected void updateThemes(Scene... scenes) {
        for (Scene scene : scenes) {
            if (scene == null){
                continue;
            }

            scene.getStylesheets().clear();

            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(Universal.isDarkMode ? "styles-dark.css" : "styles-light.css")).toExternalForm());
            Universal.setToolTip(theme, Universal.isDarkMode ? "Change theme to light" : "Change theme to dark");
        }
    }

}

//1