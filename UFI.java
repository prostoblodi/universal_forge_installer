import javafx.application.Application;
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

    private final Button downloadButton = new Button("Download");

    @Override
    public void start(Stage primaryStage) throws Exception {
        setLBStyles();

        GridPane gp = new GridPane();
        gp.add(minecraftVersionLabel, 0, 1);
        gp.add(chooseMinecraftVersion, 1, 1);
        gp.add(forgeVersionLabel, 0, 2);
        gp.add(chooseForgeVersion, 1, 2);
        gp.setHgap(10);
        gp.setVgap(10);

        VBox vbox = new VBox(mainLabel, gp, downloadButton);
        vbox.getStyleClass().add("vbox");

        Scene scene = new Scene(vbox);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        showMinecraftVersions();
        updateForgeVersions();

        setActions();

        primaryStage.setTitle("Universal Forge Installer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setLBStyles(){ // LB означает Labels and Buttons
        mainLabel.getStyleClass().add("label-main");
        minecraftVersionLabel.getStyleClass().add("label");
        forgeVersionLabel.getStyleClass().add("label");
        downloadButton.getStyleClass().add("button");
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
                (_) -> System.out.println("Downloaded!") // действия для загрузки
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
        List<Pair<String, String>> assetClasses = getMinecraftVersions();

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
//        System.out.println(chooseMinecraftVersion.getItems());
    }

    private void updateForgeVersions() throws IOException {
        List<Pair<String, String>> assetClasses = getForgeVersions();

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
        List<String> versions = Fetcher.getMinecraftVersionsForForge();
        for(String version : versions){
            assetClasses.add(new Pair<>(version, String.valueOf(versions.indexOf(version))));
        }
        return assetClasses;
    }

    private List<Pair<String, String>> getForgeVersions() throws IOException {
        List<Pair<String, String>> assetClasses = new ArrayList<>();
        List<String> versions = Fetcher.getForgeVersionsForMinecraft(minecraftVersion.getKey());
        for(String version : versions){
            assetClasses.add(new Pair<>(version, String.valueOf(versions.indexOf(version))));
        }
        return assetClasses;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
