import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Pair;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Universal {
    protected static List<String> minecraftVersions = new ArrayList<>();
    protected static final HashMap<String, List<Pair<String, Short>>> minecraftToForgeVersions = new HashMap<>();
    protected static final HashMap<String, List<String>> minecraftToSpecifiedForgeVersions = new HashMap<>();

    protected static byte defaultMinecraftVersion;
    protected static byte defaultForgeVersion;
    protected static byte baseTimings;
    protected static Pair<Short, Byte> customTimings = new Pair<>((short) 1, (byte) 1); // just to prevent null

    protected static boolean enableMinecraftFileCaching;
    protected static boolean enableForgeCaching;
    protected static boolean enableForgeFileCaching;
    protected static boolean customForgeLaunch;
    protected static boolean isDarkMode = false;

    protected static String minecraftFolder;
    protected static String lastUsedMinecraftVersion;

    protected static boolean extendCustomizer;

    protected static Path forgeJarsDir = Paths.get(System.getProperty("user.home"), "UFI", "ForgeJars");
    protected static String settingsPath = String.valueOf(Paths.get(System.getProperty("user.home"), "UFI", "UFI.settings"));
    protected static String cachePath = String.valueOf(Paths.get(System.getProperty("user.home"), "UFI", "UFI.cache"));

    protected static File settingsFile = new File(settingsPath);
    protected static File cacheFile = new File(cachePath);

    protected static boolean isCacheEnabled() {
        return defaultMinecraftVersion == 2 || enableForgeCaching || enableForgeFileCaching || enableMinecraftFileCaching || baseTimings == 8;
    }

    /**
     * Determines the "age index" of a Minecraft version based on the provided version string.
     *
     * @param minecraftVersion the version string in the format "x.y.z" (e.g., "1.5.2").
     * @return a byte representing the "age index" of the version:
     *         <ul>
     *           <li><code>2</code> for versions newer than or equal to 1.5.2</li>
     *           <li><code>1</code> for versions 1.4.x, 1.5.x, or 1.3.2</li>
     *           <li><code>0</code> for older versions</li>
     *         </ul>
     */
    protected static byte howOldIndex(String minecraftVersion) {
        String[] versionStringParsed = minecraftVersion.split("\\.");
        int[] versionParsed = new int[versionStringParsed.length];

        for (int i = 0; i < versionStringParsed.length; i++) {
            versionParsed[i] = Integer.parseInt(versionStringParsed[i]);
        }

        if (versionParsed[1] > 5 || (versionParsed[1] == 5 && versionParsed[2] == 2)) {
            return (byte) 2;
        } else if(versionParsed[1] == 4 || (versionParsed[1] == 5) || (versionParsed[1] == 3 && versionParsed[2] == 2)){
            return (byte) 1;
        } else {
            return (byte) 0;
        }
    }

    protected static <T> HBox ComboHBoxGenerator(Label label, ComboBox<T> comboBox) {
        label.getStyleClass().add("settings-label");
        comboBox.getStyleClass().add("combo-box");

        HBox fullBox = new HBox(label, new Region(), comboBox);
        HBox.setHgrow(fullBox.getChildren().get(1), Priority.ALWAYS);

        return fullBox;
    }

    /**
     * Initializes a ComboBox with key-value pairs.
     * Only the key is displayed to the user,
     * while the value remains hidden but can be accessed in code.
     *
     * @param comboBox the combo box to initialize
     * @param comboBoxPairs list of key-value pairs to populate the combo box
     */
    protected static <T> void initializeComboBox(ComboBox<Pair<String, T>> comboBox, List<Pair<String, T>> comboBoxPairs) {
        comboBox.getItems().addAll(comboBoxPairs);

        comboBox.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, T> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getKey());
                }
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, T> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getKey());
                }
            }
        });
    }

    /**
     * Find a setting that matches the settings specified in the list
     * @param list list of available settings
     * @param setting setting from this list
     * @return
     * <ul>
     *     <li><code>if element found:</code>a pair with the needed setting</li>
     *     <li><code>if element not found:</code>a pair with "Unknown", null</li>
     * </ul>
     */
    protected static <K, T> Pair<String, T> getValue(List<Pair<String, T>> list, K setting) {
        return list.stream().filter(pair -> pair.getValue().equals(setting)).findFirst().orElse(new Pair<>("Unknown", null));
    }

    protected static HBox createSeparator(String text) {
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

    protected static void setToolTip(Node node, String text){
        Tooltip tooltip = new Tooltip();
        tooltip.setText(text);
        Tooltip.install(node, tooltip);
    }

}
