import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
    private static final Label statusLabel = new Label("Привет, мир!");
    private static final SimpleStringProperty textProperty = new SimpleStringProperty("Привет, мир!");

    private final Button downloadButton = new Button("Download");

    @Override
    public void start(Stage primaryStage) throws Exception {

        statusLabel.textProperty().bind(textProperty);
        setLBStyles();

        GridPane gp = new GridPane();
        gp.add(minecraftVersionLabel, 0, 1);
        gp.add(chooseMinecraftVersion, 1, 1);
        gp.add(forgeVersionLabel, 0, 2);
        gp.add(chooseForgeVersion, 1, 2);
        gp.setHgap(10);
        gp.setVgap(10);

        VBox vbox = new VBox(mainLabel, gp, downloadButton, statusLabel);
        vbox.getStyleClass().add("vbox");

        Scene scene = new Scene(vbox);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        setActions();

        primaryStage.setTitle("Universal Forge Installer");
        primaryStage.setScene(scene);
        primaryStage.show();

        showMinecraftVersions();
    }

    private void setLBStyles(){ // LB means Labels and Buttons
        mainLabel.getStyleClass().add("label-main");
        minecraftVersionLabel.getStyleClass().add("label");
        forgeVersionLabel.getStyleClass().add("label");
        downloadButton.getStyleClass().add("button");
        statusLabel.getStyleClass().add("status-label");
    }

    private void setActions(){
        chooseMinecraftVersion.setOnAction(
                (_) -> {
                    try {
                        saveMinecraftVersion();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        chooseForgeVersion.setOnAction(
                (_) -> saveForgeVersion()
        );

        downloadButton.setOnAction(
                (_) -> {
                    try {
                        updateStatusLabel((byte) 1);
                        Installer.download_forge(minecraftVersion.getKey(), forgeVersion.getKey());
                     Platform.runLater(() -> {
                        if (Installer.isNewIndex(minecraftVersion.getKey())){updateStatusLabel((byte) 0);}
                        else {updateStatusLabel((byte) 2);}
                     });
                    } catch (IOException | URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    private void saveMinecraftVersion() throws IOException{
        minecraftVersion = chooseMinecraftVersion.getValue();
        updateForgeVersions();
        System.out.println("Saved minecraft version as: " + minecraftVersion);
    }

    private void saveForgeVersion() {
        forgeVersion = chooseForgeVersion.getValue();
        System.out.println("Saved forge version as: " + forgeVersion);
    }

    private void showMinecraftVersions() throws IOException {
        updateStatusLabel((byte) 3);
        List<Pair<String, String>> assetClasses = getMinecraftVersions();
        updateStatusLabel((byte) 0);

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
    }

    private void updateForgeVersions() throws IOException {
        updateStatusLabel((byte) 4);
        List<Pair<String, String>> assetClasses = getForgeVersions();
        updateStatusLabel((byte) 0);

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

        chooseForgeVersion.getItems().add(forgeVersion);
        chooseForgeVersion.getItems().setAll(assetClasses);
        chooseForgeVersion.setValue(forgeVersion);
    }

    private List<Pair<String, String>> getMinecraftVersions() throws IOException {
        List<Pair<String, String>> assetClasses = new ArrayList<>();
        List<String> versions = Installer.getMinecraftVersionsForForge();
        for(String version : versions){
            assetClasses.add(new Pair<>(version, String.valueOf(versions.indexOf(version))));
        }
        return assetClasses;
    }

    private List<Pair<String, String>> getForgeVersions() throws IOException {
        List<Pair<String, String>> assetClasses = new ArrayList<>();
        List<String> versions = Installer.getForgeVersionsForMinecraft(minecraftVersion.getKey());
        for(String version : versions){
            assetClasses.add(new Pair<>(version, String.valueOf(versions.indexOf(version))));
        }
        return assetClasses;
    }

    public static void updateStatusLabel(byte status) {
        if (status == 0){
            textProperty.set("");
        } else if (status == 1){
            textProperty.set("Downloading...");
        } else if (status == 2){
            textProperty.set("Downloaded!");
        } else if (status == 3){
            textProperty.set("Receiving minecraft versions...");
        } else if (status == 4){
            textProperty.set("Receiving forge versions...");
        }
        System.out.println("Current text: " + textProperty.get());

    }

    public static void main(String[] args) {
        launch(args);
    }
}
