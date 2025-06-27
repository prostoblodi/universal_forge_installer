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

    protected final Pair<String, Boolean> unknownBooleanPair = new Pair<>("Unknown", null);
    protected final Pair<String, Byte> unknownBytePair = new Pair<>("Unknown", (byte) -1);

    private final List<Pair<String, Boolean>> enableOrDisable = List.of(
            new Pair<>("Enable", true),
            new Pair<>("Disable", false)
    );

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

    private final Label enableForgeCacheLabel = new Label("Cache forge versions:");
    private final ComboBox<Pair<String, Boolean>> enableForgeCacheChoose = new ComboBox<>();

//    private final Label enableForgeFileCacheLabel = new Label("Cache forge versions to file:");
//    private final ComboBox<Pair<String, Byte>> enableForgeFileCacheChoose = new ComboBox<>();

    private final Label enableCustomLaunchLabel = new Label("Enable custom forge launch: ");
    private final ComboBox<Pair<String, Boolean>> enableCustomLaunch = new ComboBox<>();

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

        HBox defaultMinecraftVersionFullBox = HBoxGenerator(defaultMinecraftVersionLabel, defaultMinecraftVersionChoose);
        HBox defaultForgeVersionFullBox = HBoxGenerator(defaultForgeVersionLabel, chooseDefaultForgeVersion);
        HBox enableForgeCachingFullBox = HBoxGenerator(enableForgeCacheLabel, enableForgeCacheChoose);
        HBox customForgeLaunchFullBox = HBoxGenerator(enableCustomLaunchLabel, enableCustomLaunch);

        HBox folderChoose = new HBox(minecraftFolderField, minecraftFolderButton);
        folderChoose.setAlignment(Pos.CENTER);
        folderChoose.setSpacing(10);

        VBox folderLabel = new VBox(minecraftFolderChooseLabel);
        folderLabel.setAlignment(Pos.CENTER);

        HBox folderFullBox = new HBox(folderLabel, new Region(), folderChoose);
        HBox.setHgrow(folderFullBox.getChildren().get(1), Priority.ALWAYS);
        folderFullBox.setAlignment(Pos.CENTER);

        VBox windowLayout = new VBox(
                mainLabel, defaultMinecraftVersionFullBox, defaultForgeVersionFullBox, createSeparator("Caching forge versions"),
                enableForgeCachingFullBox, createSeparator("Custom forge launch"), customForgeLaunchFullBox, folderFullBox);

        windowLayout.getStyleClass().add("settings-vbox");

        Scene scene = new Scene(windowLayout);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        stage.setTitle("Settings");
        stage.setScene(scene);
    }

    /**
     * Creates an HBox layout containing a label and a combo box, aligned with a flexible spacer.
     *
     * <p>
     * This method generates an {@link HBox} where the provided {@link Label} is positioned on the left,
     * the {@link ComboBox} is positioned on the right, and a resizable {@link Region} is placed
     * between them to maintain spacing. Each component is wrapped in a {@link VBox} for additional
     * alignment control.
     * </p>
     *
     * @param label     The {@link Label} to be displayed on the left side of the HBox.
     * @param comboBox  The {@link ComboBox} to be displayed on the right side of the HBox.
     * @return          An {@link HBox} containing the provided label and combo box, aligned with spacing.
     */
    private <T> HBox HBoxGenerator(Label label, ComboBox<T> comboBox){
        VBox labelBox = new VBox(label);
        labelBox.setAlignment(Pos.CENTER_LEFT);

        VBox comboBoxBox = new VBox(comboBox);
        comboBoxBox.setAlignment(Pos.CENTER_RIGHT);

        HBox fullBox = new HBox(label, new Region(), comboBox);
        HBox.setHgrow(fullBox.getChildren().get(1), Priority.ALWAYS);
        fullBox.setAlignment(Pos.CENTER);

        return fullBox;
    }

    protected void show() {
        stage.show();
    }

    private void setStyles() {
        mainLabel.getStyleClass().add("label-main");

        defaultMinecraftVersionLabel.getStyleClass().add("settings-label");
        defaultForgeVersionLabel.getStyleClass().add("settings-label");
        enableCustomLaunchLabel.getStyleClass().add("settings-label");
        enableForgeCacheLabel.getStyleClass().addAll("settings-label");
        minecraftFolderChooseLabel.getStyleClass().add("settings-label");

        defaultMinecraftVersionChoose.getStyleClass().add("combo-box");
        chooseDefaultForgeVersion.getStyleClass().add("combo-box");
        enableForgeCacheChoose.getStyleClass().add("combo-box");
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

        enableForgeCacheChoose.setOnAction((_) -> {
            UFI.enableForgeCaching = enableForgeCacheChoose.getValue().getValue();
            System.out.println("@ Forge versions cache is now: " + UFI.enableForgeCaching);

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
        chooseDefaultForgeVersion.setValue(defaultForgeVersions.stream().filter(pair -> pair.getValue().equals(UFI.defaultForgeVersion)).findFirst().orElse(unknownBytePair));
        defaultMinecraftVersionChoose.setValue(defaultMinecraftVersions.stream().filter(pair -> pair.getValue().equals(UFI.defaultMinecraftVersion)).findFirst().orElse(unknownBytePair));

        enableForgeCacheChoose.setValue(enableOrDisable.stream().filter(pair -> pair.getValue().equals(UFI.enableForgeCaching)).findFirst().orElse(unknownBooleanPair));
        enableCustomLaunch.setValue(enableOrDisable.stream().filter(pair -> pair.getValue().equals(UFI.customForgeLaunch)).findFirst().orElse(unknownBooleanPair));

        minecraftFolderField.setOnContextMenuRequested(Event::consume);

        initializeDefaultMinecraftVersionChooser();
        initializeDefaultForgeVersionChooser();
        initializeForgeCacheChooser();
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

    private void initializeForgeCacheChooser() {
        enableForgeCacheChoose.getItems().addAll(enableOrDisable);

        enableForgeCacheChoose.setCellFactory(_ -> new ListCell<>() {
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

        enableForgeCacheChoose.setButtonCell(new ListCell<>() {
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

    private void initializeEnableCustomForgeLaunch() {
        enableCustomLaunch.getItems().addAll(enableOrDisable);

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
