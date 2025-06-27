import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
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
    private final Label mainLabel = new Label("Settings");

    private final Label defaultMinecraftVersionLabel = new Label("Default minecraft version:");
    private final ComboBox<Pair<String, Byte>> defaultMinecraftVersionChoose = new ComboBox<>();
    private final List<Pair<String, Byte>> defaultMinecraftVersions = List.of(
            new Pair<>("None", (byte) 0),
            new Pair<>("Newest", (byte) 1),
            new Pair<>("Last used", (byte) 2)
    );


    private final Label defaultForgeVersionLabel = new Label("Default forge version:");
    private final ComboBox<Pair<String, Byte>> chooseDefaultForgeVersion = new ComboBox<>();
    private final List<Pair<String, Byte>> defaultForgeVersions = List.of(
            new Pair<>("Recommended", (byte) 0),
            new Pair<>("Newest", (byte) 1),
            new Pair<>("Oldest", (byte) 2)
    );

    private final Label enableCustomLaunchLabel = new Label("Enable custom forge launch: ");
    private final ComboBox<Pair<String, Boolean>> enableCustomLaunch = new ComboBox<>();
    private final List<Pair<String, Boolean>> customLaunches = List.of(
            new Pair<>("Enable", true),
            new Pair<>("Disable", false)
    );

    private final Label minecraftFolderChooseLabel = new Label("Choose minecraft folder: ");
    private final TextField minecraftFolderField = new TextField();

    private final Stage stage = new Stage();

    public Settings() {
        System.out.println("@ Settings launched!");

        Button minecraftFolderButton = new Button("...");

        setStyles();
        initialize();
        setActions();

        minecraftFolderButton.setOnAction((_) -> {
            String s = String.valueOf(new DirectoryChooser().showDialog(new Stage()));
            if (Objects.equals(s, "null")){s = "";}
            minecraftFolderField.setText(s);
        });

        VBox defaultMinecraftVersionLabelBox = new VBox();
        defaultMinecraftVersionLabelBox.getChildren().add(defaultMinecraftVersionLabel);
        defaultMinecraftVersionLabelBox.setAlignment(Pos.CENTER_LEFT);

        VBox defaultForgeVersionLabelBox = new VBox();
        defaultForgeVersionLabelBox.getChildren().add(defaultForgeVersionLabel);
        defaultForgeVersionLabelBox.setAlignment(Pos.CENTER_LEFT);

        VBox customForgeLaunchLabelsBox = new VBox();
        customForgeLaunchLabelsBox.getChildren().addAll(enableCustomLaunchLabel, minecraftFolderChooseLabel);
        customForgeLaunchLabelsBox.setAlignment(Pos.CENTER_LEFT);
        customForgeLaunchLabelsBox.setSpacing(15);

        HBox folderChoose = new HBox();
        folderChoose.getChildren().addAll(minecraftFolderField, minecraftFolderButton);
        folderChoose.setAlignment(Pos.CENTER);
        folderChoose.setSpacing(10);

        VBox defaultMinecraftVersionChooserBox = new VBox();
        defaultMinecraftVersionChooserBox.getChildren().add(defaultMinecraftVersionChoose);
        defaultMinecraftVersionChooserBox.setAlignment(Pos.CENTER_RIGHT);

        VBox defaultForgeVersionChooserBox = new VBox();
        defaultForgeVersionChooserBox.getChildren().add(chooseDefaultForgeVersion);
        defaultForgeVersionChooserBox.setAlignment(Pos.CENTER_RIGHT);

        VBox customForgeLaunchChoosersBox = new VBox();
        customForgeLaunchChoosersBox.getChildren().addAll(enableCustomLaunch, folderChoose);
        customForgeLaunchChoosersBox.setAlignment(Pos.CENTER_RIGHT);
        customForgeLaunchChoosersBox.setSpacing(10);

        HBox defaultMinecraftVersionFullBox = new HBox();
        defaultMinecraftVersionFullBox.getChildren().addAll(defaultMinecraftVersionLabelBox, new Region(), defaultMinecraftVersionChooserBox);
        HBox.setHgrow(defaultMinecraftVersionFullBox.getChildren().get(1), Priority.ALWAYS);
        defaultMinecraftVersionFullBox.setAlignment(Pos.CENTER);

        HBox defaultForgeVersionFullBox = new HBox();
        defaultForgeVersionFullBox.getChildren().addAll(defaultForgeVersionLabelBox, new Region(), defaultForgeVersionChooserBox);
        HBox.setHgrow(defaultForgeVersionFullBox.getChildren().get(1), Priority.ALWAYS);
        defaultForgeVersionFullBox.setAlignment(Pos.CENTER);

        HBox customForgeLaunchFullBox = new HBox();
        customForgeLaunchFullBox.getChildren().addAll(customForgeLaunchLabelsBox, new Region(), customForgeLaunchChoosersBox);
        HBox.setHgrow(customForgeLaunchFullBox.getChildren().get(1), Priority.ALWAYS);
        customForgeLaunchFullBox.setAlignment(Pos.CENTER);

        VBox windowLayout = new VBox(mainLabel, defaultMinecraftVersionFullBox, defaultForgeVersionFullBox, createSeparator("Custom forge launch"), customForgeLaunchFullBox);
        windowLayout.getStyleClass().add("settings-vbox");

        Scene scene = new Scene(windowLayout);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        stage.setTitle("Settings");
        stage.setScene(scene);
    }

    protected void show() {
        stage.show();
    }

    private void setStyles() {
        mainLabel.getStyleClass().add("label-main");

        defaultMinecraftVersionLabel.getStyleClass().add("settings-label");
        defaultForgeVersionLabel.getStyleClass().add("settings-label");
        enableCustomLaunchLabel.getStyleClass().add("settings-label");
        minecraftFolderChooseLabel.getStyleClass().add("settings-label");

        defaultMinecraftVersionChoose.getStyleClass().add("combo-box");
        chooseDefaultForgeVersion.getStyleClass().add("combo-box");
    }

    private void setActions () {
        defaultMinecraftVersionChoose.setOnAction((_) -> {
            UFI.defaultMinecraftVersion = defaultMinecraftVersionChoose.getValue().getValue();
            System.out.println("@ Default minecraft version changed to: " + UFI.defaultMinecraftVersion);

            try {
                UFI.updateSettingsFile();
            } catch (IOException e) {
                UFI.updateStatusLabel((byte) 5);
                throw new RuntimeException(e);
            }
        });

        chooseDefaultForgeVersion.setOnAction((_) -> {
            UFI.defaultForgeVersion = chooseDefaultForgeVersion.getValue().getValue();
            System.out.println("@ Default forge version changed to: " + UFI.defaultForgeVersion);

            try {
                UFI.updateSettingsFile();
            } catch (IOException e) {
                UFI.updateStatusLabel((byte) 5);
                throw new RuntimeException(e);
            }
        });

        enableCustomLaunch.setOnAction((_) -> {
            UFI.customForgeLaunch = enableCustomLaunch.getValue().getValue();
            System.out.println("@ Forge custom launch changed to: " + UFI.customForgeLaunch);

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
            UFI.minecraftFolder = minecraftFolderField.getText();
            System.out.println("@ Minecraft folder changed to: " + UFI.minecraftFolder);

            try {
                UFI.updateSettingsFile();
            } catch (IOException e) {
                UFI.updateStatusLabel((byte) 5);
                throw new RuntimeException(e);
            }
        });
    }

    private void initialize() {
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(minecraftFolderField.textProperty());
        Tooltip.install(minecraftFolderField, tooltip);

        minecraftFolderField.setText(UFI.minecraftFolder);
        chooseDefaultForgeVersion.setValue(defaultForgeVersions.stream().filter(pair -> pair.getValue().equals(UFI.defaultForgeVersion)).findFirst().orElse(new Pair<>("Unknown", (byte) -1)));
        defaultMinecraftVersionChoose.setValue(defaultMinecraftVersions.stream().filter(pair -> pair.getValue().equals(UFI.defaultMinecraftVersion)).findFirst().orElse(new Pair<>("Unknown", (byte) -1)));
        enableCustomLaunch.setValue(customLaunches.stream().filter(pair -> pair.getValue().equals(UFI.customForgeLaunch)).findFirst().orElse(new Pair<>("Unknown", null)));

        minecraftFolderField.setOnContextMenuRequested(Event::consume);

        initializeDefaultMinecraftVersionChooser();
        initializeDefaultForgeVersionChooser();
        initializeEnableCustomForgeLaunch();
    }

    private void initializeDefaultForgeVersionChooser() {
        chooseDefaultForgeVersion.getItems().addAll(defaultForgeVersions);

        chooseDefaultForgeVersion.setCellFactory(_ -> new ListCell<>() {
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

        chooseDefaultForgeVersion.setButtonCell(new ListCell<>() {
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
    }

    private void initializeDefaultMinecraftVersionChooser() {
        defaultMinecraftVersionChoose.getItems().addAll(defaultMinecraftVersions);

        defaultMinecraftVersionChoose.setCellFactory(_ -> new ListCell<>() {
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

        defaultMinecraftVersionChoose.setButtonCell(new ListCell<>() {
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
    }

    private void initializeEnableCustomForgeLaunch() {
        enableCustomLaunch.getItems().addAll(customLaunches);

        enableCustomLaunch.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, Boolean> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getKey());
                }
            }
        });

        enableCustomLaunch.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, Boolean> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getKey());
                }
            }
        });
    }

    private HBox createSeparator(String text) {
        Label label = new Label(text);

        Separator leftSeparator = new Separator(Orientation.HORIZONTAL);
        Separator rightSeparator = new Separator(Orientation.HORIZONTAL);

        HBox.setHgrow(leftSeparator, Priority.SOMETIMES);
        HBox.setHgrow(rightSeparator, Priority.SOMETIMES);

        HBox hbox = new HBox(leftSeparator, label, rightSeparator);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(10);

        return hbox;
    }
}
