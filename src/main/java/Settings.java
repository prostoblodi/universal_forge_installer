import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Objects;

public class Settings {
    private final Label mainLabel = new Label("Settings");

    private final Label defaultForgeVersionLabel = new Label("Default forge version:");
    private final ComboBox<Pair<String, String>> chooseDefaultForgeVersion = new ComboBox<>();

    private final CheckBox enableCustomLaunch = new CheckBox("Enable custom forge launch");

    private final Label minecraftFolderChooseLabel = new Label("Choose minecraft folder: ");
    private final TextField minecraftFolderField = new TextField();
    private final Button minecraftFolderButton = new Button("...");

    private final Stage stage = new Stage();

    public Settings() {
        setStyles();

        GridPane gp = new GridPane();
        gp.add(defaultForgeVersionLabel, 0, 1);
        gp.add(chooseDefaultForgeVersion, 1, 1);
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setAlignment(Pos.CENTER);

        GridPane gp2 = new GridPane();
        gp2.add(minecraftFolderChooseLabel, 0, 3);
        gp2.add(minecraftFolderField, 1, 3);
        gp2.add(minecraftFolderButton, 2, 3);
        gp2.setHgap(10);
        gp2.setVgap(10);
        gp2.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(mainLabel, gp, enableCustomLaunch, gp2);
        vbox.getStyleClass().add("vbox");

        Scene scene = new Scene(vbox);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        stage.setTitle("Settings");
        stage.setScene(scene);
    }

    public void show() {
        stage.show();
    }

    private void setStyles() {
        mainLabel.getStyleClass().add("label-main");
        defaultForgeVersionLabel.getStyleClass().add("label");
        minecraftFolderChooseLabel.getStyleClass().add("label");
        chooseDefaultForgeVersion.getStyleClass().add("combo-box");
    }
}
