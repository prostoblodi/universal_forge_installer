import javafx.scene.control.ChoiceBox;
import javafx.scene.control.skin.ChoiceBoxSkin;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

/**
 * Custom skin for ChoiceBox with dark theme that simply adds proper
 * CSS class to popup and handles pseudo-classes for hover and selected states
 */
public class CustomChoiceBoxSkin<T> extends ChoiceBoxSkin<T> {

    private static final PseudoClass HOVER = PseudoClass.getPseudoClass("hover");
    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    private static final String POPUP_STYLE_CLASS = "choice-box-popup";

    /**
     * Constructor
     * @param control The ChoiceBox to skin
     */
    public CustomChoiceBoxSkin(ChoiceBox<T> control) {
        super(control);

        // Apply proper styling for when popup shows
        getSkinnable().showingProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { // When popup is showing
                applyPopupStyling();
            }
        });
    }

    private void applyPopupStyling() {
        // Ensure CSS classes are properly applied for popup
        javafx.application.Platform.runLater(() -> {
            // Get the popup from the scene
            Node popup = findPopup();
            if (popup != null) {
                // Add our custom style class for CSS selection
                popup.getStyleClass().add(POPUP_STYLE_CLASS);

                // Find the ListView that displays items
                ListView<?> listView = findListView(popup);
                if (listView != null) {
                    // Apply custom cell factory that handles pseudo-classes
                    applyCellFactory(listView);
                }
            }
        });
    }

    private Node findPopup() {
        if (getSkinnable().getScene() == null) {
            return null;
        }

        // Look for popup in scene
        for (Node node : getSkinnable().getScene().getRoot().getChildrenUnmodifiable()) {
            if (node instanceof StackPane &&
                    node.getStyleClass().contains("popup")) {
                return node;
            }
        }
        return null;
    }

    private ListView<?> findListView(Node popup) {
        if (popup instanceof StackPane) {
            for (Node child : ((StackPane) popup).getChildren()) {
                if (child instanceof ListView) {
                    return (ListView<?>) child;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void applyCellFactory(ListView<?> listView) {
        ListView<T> typedListView = (ListView<T>) listView;

        // Cell factory that handles hover and selection pseudo-classes
        typedListView.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
            @Override
            public ListCell<T> call(ListView<T> param) {
                return new ListCell<T>() {
                    {
                        // Setup handlers for hover pseudo-class
                        setOnMouseEntered(e -> pseudoClassStateChanged(HOVER, true));
                        setOnMouseExited(e -> pseudoClassStateChanged(HOVER, false));
                    }

                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);

                        // Update selected pseudo-class
                        pseudoClassStateChanged(SELECTED, isSelected());

                        if (empty || item == null) {
                            setText(null);
                        } else {
                            // Use converter if available
                            if (getSkinnable().getConverter() != null) {
                                setText(getSkinnable().getConverter().toString(item));
                            } else {
                                setText(item.toString());
                            }
                        }
                    }
                };
            }
        });
    }
}