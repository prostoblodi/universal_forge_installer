import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UFI extends Application {
    private final ComboBox<String> chooseMinecraftVersion = new ComboBox<>();
    private final ComboBox<Pair<String, Byte>> chooseForgeVersion = new ComboBox<>();

    private static String minecraftVersion = "";
    private static Pair<String, Byte> forgeVersion = new Pair<>("", (byte) -1);

    private final HashMap<String, List<Pair<String, Byte>>> minecraftToForgeVersions = new HashMap<>();

    private final Label mainLabel = new Label("Universal Forge Installer");
    private final Label minecraftVersionLabel = new Label("Minecraft version: ");
    private final Label forgeVersionLabel = new Label("Forge version: ");
    private static final Label statusLabel = new Label("");
    private static final SimpleStringProperty textProperty = new SimpleStringProperty("");

    protected static final Button downloadButton = new Button("Download & Launch");
    private final Button settingsButton = new Button("Settings");

    protected static byte defaultMinecraftVersion;
    protected static byte defaultForgeVersion;
    protected static boolean customForgeLaunch;
    protected static String minecraftFolder;

    protected static String settingsPath = String.valueOf(Paths.get(System.getProperty("user.home"), "UFI", "UFI.settings"));
    protected static File settingsFile = new File(settingsPath);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        statusLabel.textProperty().bind(textProperty);

        setLBStyles();
        checkSettings();

        GridPane gp = new GridPane();
        gp.add(minecraftVersionLabel, 0, 1);
        gp.add(chooseMinecraftVersion, 1, 1);
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
                        showMinecraftVersions();
                        Platform.runLater(() -> updateStatusLabel((byte) 0));
                    } catch (IOException e) {
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
        downloadButton.getStyleClass().add("button");
        settingsButton.getStyleClass().add("button");
        statusLabel.getStyleClass().add("status-label");
    }

    private void setActions() {
        chooseMinecraftVersion.setOnAction(
                (_) -> new Thread(() -> {
                    try {
                        saveMinecraftVersion();
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
                                if (Installer.howOldIndex(minecraftVersion) >= 1) {
                                    updateStatusLabel((byte) 6);
                                    new Thread(() -> {
                                        Installer.run_forge();
                                        Platform.runLater(() -> updateStatusLabel((byte) 7));
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
    }

    private void saveMinecraftVersion() throws IOException {
        minecraftVersion = chooseMinecraftVersion.getValue();
        updateForgeVersions();
        System.out.println("* Saved minecraft version as: " + minecraftVersion);
    }

    private void saveForgeVersion() {
        forgeVersion = chooseForgeVersion.getValue();
        System.out.println("* Saved forge version as: " + forgeVersion);
    }

    private void showMinecraftVersions() throws IOException {
        List<String> assetClasses = getMinecraftVersions();
        Platform.runLater(() -> {
            chooseMinecraftVersion.getItems().addAll(assetClasses);
            if (defaultMinecraftVersion == 1){
                chooseMinecraftVersion.setValue(assetClasses.getFirst());
            }
        });
    }

    private void updateForgeVersions() throws IOException {
        List<Pair<String, Byte>> assetClasses = minecraftToForgeVersions.get(minecraftVersion) != null ? minecraftToForgeVersions.get(minecraftVersion) : getForgeVersions();

        Platform.runLater(() -> {
            chooseForgeVersion.getItems().setAll(assetClasses);

            switch (defaultForgeVersion) {
                case 0 -> chooseForgeVersion.setValue(assetClasses.stream().filter(pair -> pair.getKey().equals(Installer.minecraftToSpecifiedForgeVersions.get(minecraftVersion).get(1))).findFirst().orElse(new Pair<>("Unknown", (byte) -1)));
                case 1 -> chooseForgeVersion.setValue(assetClasses.stream().filter(pair -> pair.getKey().equals(Installer.minecraftToSpecifiedForgeVersions.get(minecraftVersion).getFirst())).findFirst().orElse(new Pair<>("Unknown", (byte) -1)));
                case 2 -> chooseForgeVersion.setValue(assetClasses.stream().filter(pair -> pair.getKey().equals(Installer.minecraftToSpecifiedForgeVersions.get(minecraftVersion).getLast())).findFirst().orElse(new Pair<>("Unknown", (byte) -1)));
            }

            minecraftToForgeVersions.putIfAbsent(minecraftVersion, assetClasses);

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
     *                 <li><code>1</code>: Downloading</li>
     *                 <li><code>2</code>: Downloaded</li>
     *                 <li><code>3</code>: Receiving Minecraft versions</li>
     *                 <li><code>4</code>: Receiving Forge versions</li>
     *                 <li><code>5</code>: Error occurred</li>
     *                 <li><code>6</code>: Installing</li>
     *                 <li><code>7</code>: Installed</li>
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
                case 5 -> textProperty.set("Error occurred");
                case 6 -> textProperty.set("Installing...");
                case 7 -> textProperty.set("Installed!");
            }
            System.out.println("Current status: " + (textProperty.get().isEmpty() ? "idle(nothing)" : textProperty.get()));
        });
    }

    private static void checkSettings() throws IOException {
        System.out.println("Г Checking settings...");

        if (!settingsFile.exists()) {
            System.out.println("| Settings file do not exists at " + settingsPath);

            defaultMinecraftVersion = 0;
            defaultForgeVersion = 0;
            customForgeLaunch = true;

            if (System.getProperty("os.name").toLowerCase().contains("win")){
                minecraftFolder = String.valueOf(new File(System.getenv("APPDATA"), ".minecraft"));
            } else {
                minecraftFolder = String.valueOf(new File(System.getProperty("user.home"), ".minecraft"));
            }

            updateSettingsFile();

            System.out.println("| Settings file created: " + settingsPath);
        } else {
            System.out.println("| Settings file already exists at " + settingsPath);

            List<String> lines = Files.readAllLines(Paths.get(settingsPath));
            for (String line : lines) {
                if (line.contains("defaultForgeVersionByte")){
                    String[] data = line.split("=");
                    defaultForgeVersion = Byte.parseByte(data[1]);
                } else if (line.contains("customForgeLaunch")){
                    String[] data = line.split("=");
                    customForgeLaunch = Boolean.parseBoolean(data[1]);
                } else if (line.contains("minecraftFolder")){
                    String[] data = line.split("=");
                    if (data.length > 1) {
                        minecraftFolder = data[1];
                    } else {
                        if (System.getProperty("os.name").toLowerCase().contains("win")){
                            minecraftFolder = String.valueOf(new File(System.getenv("APPDATA"), ".minecraft"));
                        } else {
                            minecraftFolder = String.valueOf(new File(System.getProperty("user.home"), ".minecraft"));
                        }
                    }
                } else if (line.contains("defaultMinecraftVersion")){
                    String[] data = line.split("=");
                    defaultMinecraftVersion = Byte.parseByte(data[1]);
                }
            }
        }

        System.out.printf("* Saved settings as: defaultForgeVersion: %d, customForgeLaunch: %b,%nL minecraftFolder: %s%n%n", defaultForgeVersion, customForgeLaunch, minecraftFolder);

        if (customForgeLaunch) {
            downloadButton.setText("Download & Install");
        } else {
            downloadButton.setText("Download & Launch");
        }
    }

    protected static void updateSettingsFile() throws IOException {
        try (FileWriter writer = new FileWriter(settingsFile)) {
            writer.write(String.format("defaultForgeVersionByte=%d%ncustomForgeLaunch=%b%nminecraftFolder=%s%ndefaultMinecraftVersion=%d",
                                        defaultForgeVersion, customForgeLaunch, minecraftFolder, defaultMinecraftVersion));
        }
    }
}
