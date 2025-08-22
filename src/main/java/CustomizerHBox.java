import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

class CustomizerHBox {
    private List<HBox> list = new ArrayList<>();

    private final List<Pair<String, Byte>> colorFormats = List.of(
            new Pair<>("hex", (byte) 0),
            new Pair<>("rgba", (byte) 1)
    );

    protected CustomizerHBox(String nodeName, boolean roundRadius, boolean BGColor, boolean opacity, boolean textSize, boolean textColor){
        nodeName = nodeName.toLowerCase();

        if (roundRadius){
            final TextField changeRoundingRadiusField = new TextField();
            list.add(changeSizeHBoxGenerator(new Label("Rounding radius: "), changeRoundingRadiusField, new Label("px"),
                    "Set the written rounding radius for chooser."));
        }

        if (BGColor){
            final TextField changeColorField = new TextField();
            final ComboBox<Pair<String, Byte>> changeColorChooser = new ComboBox<>();
            list.add(changeColorHBoxGenerator(new Label("Background color: "), changeColorField, changeColorChooser,
                    String.format("Set the written color for the %s background.", nodeName)));
        }

        if (opacity){
            final TextField changeBackgroundOpacityField = new TextField();
            list.add(changeOpacityHBoxGenerator(new Label("Background opacity: "), changeBackgroundOpacityField, new Label("%"),
                    String.format("Set the written opacity percentage for the %s background.", nodeName)));
        }

        if (textSize){
            final TextField textSizeChanger = new TextField();
            list.add(changeSizeHBoxGenerator(new Label("Text size: "), textSizeChanger, new Label("px"),
                    String.format("Set the written size for the %s text.", nodeName)));
        }

        if (textColor){
            final TextField changeTextColorField = new TextField();
            final ComboBox<Pair<String, Byte>> changeTextColorChooser = new ComboBox<>();
            list.add(changeColorHBoxGenerator(new Label("Text color: "), changeTextColorField, changeTextColorChooser,
                    String.format("Set the written color for the %s text.", nodeName)));
        }
    }

    protected List<HBox> getHBoxes(){
        return list;
    }

    protected void setActions(){}

    private HBox changeColorHBoxGenerator(Label label, TextField textField, ComboBox<Pair<String, Byte>> comboBox, String tooltip){
        Universal.initializeComboBox(comboBox, colorFormats);

        HBox colorChoose = new HBox(textField, comboBox);
        colorChoose.setSpacing(10);

        label.getStyleClass().add("settings-label");

        HBox fullBox = new HBox(label, new Region(), colorChoose);
        HBox.setHgrow(fullBox.getChildren().get(1), Priority.ALWAYS);

        Universal.setToolTip(fullBox, tooltip);

        return fullBox;
    }

    private HBox changeSizeHBoxGenerator(Label label, TextField textField, Label label2, String tooltip){
        label.getStyleClass().add("settings-label");
        label2.getStyleClass().add("settings-label");

        HBox sizeChoose = new HBox(textField, label2);
        sizeChoose.setSpacing(10);

        HBox fullBox = new HBox(label, new Region(), sizeChoose);
        HBox.setHgrow(fullBox.getChildren().get(1), Priority.ALWAYS);

        Universal.setToolTip(fullBox, tooltip);

        return fullBox;
    }

    private HBox changeOpacityHBoxGenerator(Label label, TextField textField, Label label2, String tooltip){
        label.getStyleClass().add("settings-label");
        label2.getStyleClass().add("settings-label");

        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            try {
                if (Byte.parseByte(change.getControlNewText()) <= 100 && change.getControlNewText().length() < 4) {
                    return change;
                } else {
//                    System.out.println(String.format("не выйдет %s сделать :3", change.getControlNewText()));
                    return null;
                }
            }
            catch (Exception e){
//                System.out.println(String.format("не выйдет %s сделать :3", change.getControlNewText()));
                change.setText("");
                return change;
            }
        });

        textField.setTextFormatter(formatter);

        HBox sizeChoose = new HBox(textField, label2);
        sizeChoose.setSpacing(10);

        HBox fullBox = new HBox(label, new Region(), sizeChoose);
        HBox.setHgrow(fullBox.getChildren().get(1), Priority.ALWAYS);

        Universal.setToolTip(fullBox, tooltip);

        return fullBox;
    }
}
