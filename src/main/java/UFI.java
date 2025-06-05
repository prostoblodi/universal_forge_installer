import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UFI extends Application {
    private final ComboBox<Pair<String, String>> chooseMinecraftVersion = new ComboBox<>();
    private final ComboBox<Pair<String, String>> chooseForgeVersion = new ComboBox<>();

    private static Pair<String, String> minecraftVersion = new Pair<>("", "");
    private static Pair<String, String> forgeVersion = new Pair<>("", "");

    private final Label mainLabel = new Label("Universal Forge Installer");
    private final Label minecraftVersionLabel = new Label("Minecraft version: ");
    private final Label forgeVersionLabel = new Label("Forge version: ");
    private static final Label statusLabel = new Label("");
    private static final SimpleStringProperty textProperty = new SimpleStringProperty("");

    private final Button downloadButton = new Button("Download");

    @Override
    public void start(Stage primaryStage) {
        statusLabel.textProperty().bind(textProperty);
        setLBStyles();

        GridPane gp = new GridPane();
        gp.add(minecraftVersionLabel, 0, 1);
        gp.add(chooseMinecraftVersion, 1, 1);
        gp.add(forgeVersionLabel, 0, 2);
        gp.add(chooseForgeVersion, 1, 2);
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setAlignment(Pos.BASELINE_CENTER);

        VBox vbox = new VBox(mainLabel, gp, downloadButton, statusLabel);
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
                            Installer.download_forge(minecraftVersion.getKey(), forgeVersion.getKey());
                            Platform.runLater(() -> updateStatusLabel((byte) 2));
                        } catch (IOException | URISyntaxException e) {
                            UFI.updateStatusLabel((byte) 5);
                            throw new RuntimeException(e);
                        }
                    }).start();
                }
        );
    }

    private void saveMinecraftVersion() throws IOException {
        minecraftVersion = chooseMinecraftVersion.getValue();
        updateForgeVersions();
        System.out.println("Saved minecraft version as: " + minecraftVersion);
    }

    private void saveForgeVersion() {
        updateStatusLabel((byte) 0);
        forgeVersion = chooseForgeVersion.getValue();
        System.out.println("Saved forge version as: " + forgeVersion);
    }

    private void showMinecraftVersions() throws IOException {
        List<Pair<String, String>> assetClasses = getMinecraftVersions();

        Platform.runLater(() -> {
            chooseMinecraftVersion.setConverter(new StringConverter<>() {
                @Override
                public String toString(Pair<String, String> pair) {
                    return pair == null ? "" : pair.getKey();
                }

                @Override
                public Pair<String, String> fromString(String string) {
                    return null;
                }
            });

            chooseMinecraftVersion.getItems().addAll(assetClasses);
        });
    }

    private void updateForgeVersions() throws IOException {
        List<Pair<String, String>> assetClasses = getForgeVersions();

        Platform.runLater(() -> {
            chooseForgeVersion.setConverter(new StringConverter<>() {
                @Override
                public String toString(Pair<String, String> pair) {
                    return pair == null ? "" : pair.getKey();
                }

                @Override
                public Pair<String, String> fromString(String s) {
                    return null;
                }
            });

            chooseForgeVersion.getItems().setAll(assetClasses);
            chooseForgeVersion.setValue(forgeVersion);
        });
    }

    private List<Pair<String, String>> getMinecraftVersions() throws IOException {
        updateStatusLabel((byte) 3);
        List<Pair<String, String>> assetClasses = new ArrayList<>();

        List<String> versions = Installer.getMinecraftVersionsForForge();
        for (String version : versions) {
            assetClasses.add(new Pair<>(version, String.valueOf(versions.indexOf(version))));
        }

        return assetClasses;
    }

    private List<Pair<String, String>> getForgeVersions() throws IOException {
        updateStatusLabel((byte) 4);
        List<Pair<String, String>> assetClasses = new ArrayList<>();

        List<String> versions = Installer.getForgeVersionsForMinecraft(minecraftVersion.getKey());
        for (String version : versions) {
            assetClasses.add(new Pair<>(version, String.valueOf(versions.indexOf(version))));
        }

        return assetClasses;
    }

    /** Method that assigns a value to the status label
     *
     * @param status number that determines which status to set according to this logic:
     * <ul>
     *      <li>0 - idle(nothing)</li>
     *      <li>1 - downloading</li>
     *      <li>2 - downloaded</li>
     *      <li>3 - minecraft versions are receiving</li>
     *      <li>4 - forge versions are receiving</li>
     *      <li>5 - error</li>
     *      <li>6 - if the user clicks download without selecting a version</li>
     * </ul>
     */

    public static void updateStatusLabel(byte status) {
        Platform.runLater(() -> {
            switch (status) {
                case 0 -> textProperty.set(""); // idle
                case 1 -> textProperty.set("Downloading...");
                case 2 -> textProperty.set("Downloaded!");
                case 3 -> textProperty.set("Receiving Minecraft versions...");
                case 4 -> textProperty.set("Receiving Forge versions...");
                case 5 -> textProperty.set("Error occurred");
            }
            System.out.println("Current status: " + textProperty.get());
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
