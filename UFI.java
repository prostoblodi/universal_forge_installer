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

public class UFI extends Application {
    private final ChoiceBox<Pair<String, String>> chooseMinecraftVersion = new ChoiceBox<>();
    private final ChoiceBox<Pair<String, String>> chooseForgeVersion = new ChoiceBox<>();

    private static Pair<String, String> minecraftVersion = new Pair<>("minecraft version 1", "m1");
    private static Pair<String, String> forgeVersion = new Pair<>("forge version 1", "f1");

    @Override
    public void start(Stage primaryStage) throws Exception {

        Label label = new Label("Universal Forge Installer");
        label.setPrefWidth(200);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #73648A;");

        Label minecraftVersionLabel = new Label("Minecraft version: ");
        minecraftVersionLabel.setStyle("-fx-text-fill: #73648A;");

        chooseMinecraftVersion.setPrefWidth(200);
        chooseMinecraftVersion.setStyle("-fx-background-color: #453750; -fx-text-fill: #73648A;");

        Label forgeVersionLabel = new Label("Forge version: ");
        forgeVersionLabel.setStyle("-fx-text-fill: #73648A;");

        chooseForgeVersion.setPrefWidth(200);
        chooseForgeVersion.setStyle("-fx-background-color: #453750; -fx-text-fill: #73648A;");

        Button downloadButton = new Button("Download");
        downloadButton.setStyle("-fx-background-color: #453750; -fx-text-fill: #73648A;");

        GridPane gp = new GridPane();
        gp.add(minecraftVersionLabel, 0, 1);
        gp.add(chooseMinecraftVersion, 1, 1);
        gp.add(forgeVersionLabel, 0, 2);
        gp.add(chooseForgeVersion, 1, 2);
        gp.setHgap(10);
        gp.setVgap(10);

        VBox vbox = new VBox(label, gp, downloadButton);
        vbox.setSpacing(10.0d);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(40));
        vbox.setStyle("-fx-background-color: #0C0910;");

        Scene scene = new Scene(vbox);

        showMinecraftVersions();
        showForgeVersions();

        downloadButton.setOnAction(
                (_) -> saveVersions()
        );

        primaryStage.setTitle("Universal Forge Installer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveVersions() {
        minecraftVersion = chooseMinecraftVersion.getValue();
        System.out.println("Saved minecraft version as: " + chooseMinecraftVersion.getValue());
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
