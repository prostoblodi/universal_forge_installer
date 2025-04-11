import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UFI extends Application {
    private final ChoiceBox<Pair<String, String>> chooseMinecraftVersion = new ChoiceBox<>();
    private final ChoiceBox<Pair<String, String>> chooseForgeVersion = new ChoiceBox<>();

    private static Pair<String, String> minecraftVersion = new Pair<>("minecraft version 1", "m1");
    private static Pair<String, String> forgeVersion = new Pair<>("forge version 1", "f1");

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
        showForgeVersions();

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
                (_) -> saveMinecraftVersion()
        );

        chooseForgeVersion.setOnAction(
                (_) -> saveForgeVersion()
        );

        downloadButton.setOnAction(
                (_) -> System.out.println("Downloaded!") // действия для загрузки
        );
    }

    private void saveMinecraftVersion() {
        minecraftVersion = chooseMinecraftVersion.getValue();
        System.out.println("Saved minecraft version as: " + chooseMinecraftVersion.getValue());
    }

    private void saveForgeVersion() {
        forgeVersion = chooseForgeVersion.getValue();
        System.out.println("Saved forge version as: " + chooseForgeVersion.getValue());
    }

    private void showMinecraftVersions() {
        List<Pair<String, String>> assetClasses = new ArrayList<>();
        assetClasses.add(new Pair<>("minecraft version 2", "m2"));
        assetClasses.add(new Pair<>("minecraft version 3", "m3"));
        assetClasses.add(new Pair<>("minecraft version 4", "m4"));

        chooseMinecraftVersion.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pair<String, String> pair) {
                return pair.getKey();
            }

            @Override
            public Pair<String, String> fromString(String string) {
                return null;
            }
        });

        chooseMinecraftVersion.getItems().add(minecraftVersion);
        chooseMinecraftVersion.getItems().addAll(assetClasses);
        System.out.println(chooseMinecraftVersion.getItems());
        chooseMinecraftVersion.setValue(minecraftVersion);

        chooseMinecraftVersion.setSkin(new CustomChoiceBoxSkin<>(chooseMinecraftVersion));

    }

    private void showForgeVersions() {
        List<Pair<String, String>> assetClasses = new ArrayList<>();
        assetClasses.add(new Pair<>("forge version 2", "f2"));
        assetClasses.add(new Pair<>("forge version 3", "f3"));
        assetClasses.add(new Pair<>("forge version 4", "f4"));

        chooseForgeVersion.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pair<String, String> pair) {
                return pair.getKey();
            }

            @Override
            public Pair<String, String> fromString(String s) {
                return null;
            }
        });

        chooseForgeVersion.getItems().add(forgeVersion);
        chooseForgeVersion.getItems().addAll(assetClasses);
        chooseForgeVersion.setValue(forgeVersion);

        chooseForgeVersion.setSkin(new CustomChoiceBoxSkin<>(chooseForgeVersion));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
