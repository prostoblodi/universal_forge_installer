import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class UFI extends Application {

    private final Button reload = new Button("", new ImageView(new Image("reload.png", 24, 24, true, true)));

    private final ComboBox<String> chooseMinecraftVersion = new ComboBox<>();
    private final ComboBox<Pair<String, Byte>> chooseForgeVersion = new ComboBox<>();

    private static String minecraftVersion = "";
    private static Pair<String, Byte> forgeVersion = new Pair<>("", (byte) -1);

    private final Label mainLabel = new Label("Universal Forge Installer");
    private final Label minecraftVersionLabel = new Label("Minecraft version: ");
    private final Label forgeVersionLabel = new Label("Forge version: ");
    private static final Label statusLabel = new Label("");
    private static final SimpleStringProperty textProperty = new SimpleStringProperty("");

    protected static final Button downloadButton = new Button("Download & Launch");
    private final Button settingsButton = new Button("Settings");

    protected static String settingsPath = String.valueOf(Paths.get(System.getProperty("user.home"), "UFI", "UFI.settings"));
    protected static File settingsFile = new File(settingsPath);

    protected static String cachePath = String.valueOf(Paths.get(System.getProperty("user.home"), "UFI", "UFI.cache"));
    protected static File cacheFile = new File(cachePath);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        statusLabel.textProperty().bind(textProperty);

        setLBStyles();
        checkSettings();
        if (Universal.isCacheEnabled()) {checkCache();}

        GridPane gp = new GridPane();
        gp.add(minecraftVersionLabel, 0, 1);
        gp.add(chooseMinecraftVersion, 1, 1);
        gp.add(reload, 2, 1);
        gp.add(forgeVersionLabel, 0, 2);
        gp.add(chooseForgeVersion, 1, 2);
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(mainLabel, gp, downloadButton, settingsButton, statusLabel);
        vbox.getStyleClass().add("vbox");

        Scene scene = new Scene(vbox);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        setActions();

        primaryStage.setTitle("Universal Forge Installer");
        primaryStage.setScene(scene);
        primaryStage.show();

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

    private void setLBStyles() { // LB means Labels and Buttons
        mainLabel.getStyleClass().add("label-main");
        minecraftVersionLabel.getStyleClass().add("label");
        forgeVersionLabel.getStyleClass().add("label");
        reload.getStyleClass().add("button");
        downloadButton.getStyleClass().add("button");
        settingsButton.getStyleClass().add("button");
        statusLabel.getStyleClass().add("status-label");
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
                    updateStatusLabel((byte) 1);
                    new Thread(() -> {
                        try {
                            Installer.download_forge(minecraftVersion, forgeVersion);
                            Platform.runLater(() -> {
                                updateStatusLabel((byte) 2);
                                if (Universal.howOldIndex(minecraftVersion) >= 1) {
                                    if (Universal.customForgeLaunch) {updateStatusLabel((byte) 6);}
                                    else {updateStatusLabel((byte) 8);}
                                    new Thread(() -> {
                                        Installer.run_forge();
                                        Platform.runLater(() -> {if (Universal.customForgeLaunch) {updateStatusLabel((byte) 7);} else{updateStatusLabel((byte) 9);}});
                                    }).start();
                                }
                            });
                        } catch (IOException | URISyntaxException e) {
                            UFI.updateStatusLabel((byte) 5);
                            throw new RuntimeException(e);
                        }
                    }).start();
                }
        );

        settingsButton.setOnAction((_) -> new Settings().show());

        reload.setOnAction((_) -> new Thread(() -> {
                try {
                    showMinecraftVersions(true);
                    Platform.runLater(() -> updateStatusLabel((byte) 0));
                } catch (IOException e) {
                    UFI.updateStatusLabel((byte) 5);
                    throw new RuntimeException(e);
                }
            }).start()
        );
    }

    private void saveMinecraftVersion(boolean ignoreCache) throws IOException {
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

    private void showMinecraftVersions(boolean ignoreCache) throws IOException {
        if (Universal.enableMinecraftFileCaching && Universal.minecraftVersions.isEmpty()){
            Universal.minecraftVersions = getMinecraftVersions();
            updateCacheFile();
        }

        List<String> assetClasses = Universal.enableMinecraftFileCaching ? Universal.minecraftVersions : getMinecraftVersions();

        Platform.runLater(() -> {
            chooseMinecraftVersion.getItems().addAll(assetClasses);
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

    private void updateForgeVersions(boolean ignoreCache) throws IOException {
        List<Pair<String, Byte>> assetClasses;


        if (Universal.enableForgeCaching && !ignoreCache){
            assetClasses = Universal.minecraftToForgeVersions.get(minecraftVersion) != null ? Universal.minecraftToForgeVersions.get(minecraftVersion) : getForgeVersions();
        } else {
            assetClasses = getForgeVersions();
        }

        Platform.runLater(() -> {
            chooseForgeVersion.getItems().setAll(assetClasses);
            System.out.println(Universal.minecraftToSpecifiedForgeVersions.get(minecraftVersion));

            switch (Universal.defaultForgeVersion) {
                case 0 -> chooseForgeVersion.setValue(assetClasses.stream().filter(pair -> pair.getKey().equals(Universal.minecraftToSpecifiedForgeVersions.get(minecraftVersion).get(1))).findFirst().orElse(new Pair<>("Unknown", (byte) -1)));
                case 1 -> chooseForgeVersion.setValue(assetClasses.stream().filter(pair -> pair.getKey().equals(Universal.minecraftToSpecifiedForgeVersions.get(minecraftVersion).getFirst())).findFirst().orElse(new Pair<>("Unknown", (byte) -1)));
                case 2 -> chooseForgeVersion.setValue(assetClasses.stream().filter(pair -> pair.getKey().equals(Universal.minecraftToSpecifiedForgeVersions.get(minecraftVersion).getLast())).findFirst().orElse(new Pair<>("Unknown", (byte) -1)));
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
                protected void updateItem(Pair<String, Byte> item, boolean empty) {
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
                protected void updateItem(Pair<String, Byte> item, boolean empty) {
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

    private List<String> getMinecraftVersions() throws IOException {
        updateStatusLabel((byte) 3);
        return Installer.getMinecraftVersionsForForge();
    }

    private List<Pair<String, Byte>> getForgeVersions() throws IOException {
        updateStatusLabel((byte) 4);

        List<String> output = Installer.getForgeVersionsForMinecraft(minecraftVersion);
        List<Pair<String, Byte>> assetClasses = new ArrayList<>();

        for (String version : output) {
            assetClasses.add(new Pair<>(version, (byte) output.indexOf(version)));
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

    private static void checkSettings() throws IOException {
        System.out.println("Г Checking settings...");

        if (!settingsFile.exists()) {
            System.out.println("| Settings file do not exists at " + settingsPath);

            Universal.defaultMinecraftVersion = 0;
            Universal.defaultForgeVersion = 0;
            Universal.customForgeLaunch = true;

            if (System.getProperty("os.name").toLowerCase().contains("win")){
                Universal.minecraftFolder = String.valueOf(new File(System.getenv("APPDATA"), ".minecraft"));
            } else {
                Universal.minecraftFolder = String.valueOf(new File(System.getProperty("user.home"), ".minecraft"));
            }

            updateSettingsFile();

            System.out.println("| Settings file created: " + settingsPath);
        } else {
            System.out.println("| Settings file already exists at " + settingsPath);

            List<String> lines = Files.readAllLines(Paths.get(settingsPath));
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
                }
            }
        }

        System.out.printf(
                "* Saved settings as: defaultForgeVersion: %d, customForgeLaunch: %b,%n" +
                "| minecraftFolder: %s, defaultMinecraftVersion: %d,%n" +
                "| enableForgeCache: %b, enableForgeFileCache: %b,%n" +
                "L enableMinecraftFileCaching: %b%n%n",
                Universal.defaultForgeVersion, Universal.customForgeLaunch, Universal.minecraftFolder,
                Universal.defaultForgeVersion, Universal.enableForgeCaching, Universal.enableForgeFileCaching,
                Universal.enableMinecraftFileCaching
        );

        if (Universal.customForgeLaunch) {
            downloadButton.setText("Download & Install");
        } else {
            downloadButton.setText("Download & Launch");
        }
    }

    private static void checkCache() throws IOException {
        System.out.println("Г Checking cache...");

        if (!cacheFile.exists()){
            System.out.println("| Cache file do not exists at " + cachePath);
            updateCacheFile();
        } else {
            System.out.println("| Cache file already exists at " + cachePath);

            List<String> lines = Files.readAllLines(Paths.get(cachePath));
            String[] data;

            for (String line : lines){
                if (line.contains("defaultMinecraftVersion")){
                    data = line.split("=");
                    Universal.lastUsedMinecraftVersion = data[1];
                } else if (line.contains("minecraftToForgeVersions") && Universal.enableForgeFileCaching) {
                    data = line.split("=", 2);
                    if (!Objects.equals(data[1], "{}")) {
                        parseToHashMap(data[1], Universal.minecraftToForgeVersions, true);
                    }
                } else if (line.contains("minecraftToSpecifiedForgeVersions") && Universal.enableForgeFileCaching) {
                    data = line.split("=", 2);
                    if (!Objects.equals(data[1], "{}")) {
                        parseToHashMap(data[1], Universal.minecraftToSpecifiedForgeVersions, false);
                    }
                } else if (line.contains("minecraftVersions") && Universal.enableMinecraftFileCaching){
                    data = line.split("=");
                    for (String i : data[1].replace("[", "").replace("]", "").split(",")){
                        Universal.minecraftVersions.add(i.replaceAll(" ", ""));
                    }
                }
            }
        }

        System.out.println("L Cache successfully checked.");
    }

    protected static void updateSettingsFile() throws IOException {
        try (FileWriter writer = new FileWriter(settingsFile)) {
            writer.write(String.format(
                    "defaultForgeVersionByte=%d%ncustomForgeLaunch=%b%nminecraftFolder=%s%ndefaultMinecraftVersionByte=%d%n" +
                    "enableForgeCaching=%b%nenableForgeFileCaching=%b%nenableMinecraftFileCaching=%b",
                    Universal.defaultForgeVersion, Universal.customForgeLaunch, Universal.minecraftFolder, Universal.defaultMinecraftVersion,
                    Universal.enableForgeCaching, Universal.enableForgeFileCaching, Universal.enableMinecraftFileCaching));
        }
    }

    protected static void updateCacheFile() throws IOException {
        try (FileWriter writer = new FileWriter(cacheFile)){
            writer.write(String.format("defaultMinecraftVersion=%s%nminecraftToForgeVersions=%s%nminecraftToSpecifiedForgeVersions=%s%nminecraftVersions=%s",
                    Universal.lastUsedMinecraftVersion, Universal.minecraftToForgeVersions, Universal.minecraftToSpecifiedForgeVersions, Universal.minecraftVersions));
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

}
