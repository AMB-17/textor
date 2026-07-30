package com.achraf.textor;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyCombination.CONTROL_DOWN;
import static javafx.scene.input.KeyCombination.SHIFT_DOWN;

/**
 * Controller for {@code textor.fxml}.
 *
 * <p>Holds all the editor logic: file IO, edit menu, format menu,
 * status bar bindings, unsaved-changes handling, and the About dialog.
 *
 * <p>All file IO uses {@link Files} with try-with-resources so streams
 * are always closed, even on exception. Errors are surfaced via modal
 * {@link Alert} dialogs rather than dumped to stdout.
 */
public final class TextorController implements Initializable {

    /* ---- Injected from FXML ---- */
    @FXML private TextArea       textArea;
    @FXML private Label          statusLabel;
    @FXML private CheckMenuItem  wrapItem;
    @FXML private MenuItem       newMenuItem;
    @FXML private MenuItem       openMenuItem;
    @FXML private MenuItem       saveMenuItem;
    @FXML private MenuItem       saveAsMenuItem;
    @FXML private MenuItem       exitMenuItem;
    @FXML private MenuItem       undoMenuItem;
    @FXML private MenuItem       redoMenuItem;
    @FXML private MenuItem       cutMenuItem;
    @FXML private MenuItem       copyMenuItem;
    @FXML private MenuItem       pasteMenuItem;
    @FXML private MenuItem       deleteMenuItem;
    @FXML private MenuItem       selectAllMenuItem;
    @FXML private MenuItem       fontSizeUpMenuItem;
    @FXML private MenuItem       fontSizeDownMenuItem;
    @FXML private MenuItem       aboutMenuItem;

    /* ---- Editor state ---- */
    private Stage       stage;
    private Path        currentFile;
    private boolean     dirty;
    private static final String APP_NAME = "Textor";
    private static final int    MIN_FONT = 8;
    private static final int    MAX_FONT = 72;
    private static final int    DEFAULT_FONT = 14;

    /* =========================================================
     *  Lifecycle
     * ========================================================= */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textArea.setWrapText(true);
        wrapItem.setSelected(true);
        textArea.setFont(Font.font("Monospaced", DEFAULT_FONT));

        bindStatus();
        installDirtyListeners();
        installKeyboardShortcuts();
    }

    /** Called by {@link Textor#start} once the stage is ready. */
    public void bindStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(this::onCloseRequest);
        updateWindowTitle();
    }

    /* =========================================================
     *  File menu
     * ========================================================= */

    @FXML
    void onNew() {
        if (!confirmDiscardIfDirty()) return;
        textArea.clear();
        currentFile = null;
        markClean();
    }

    @FXML
    void onOpen() {
        if (!confirmDiscardIfDirty()) return;

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Document");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Text files (*.txt, *.md, *.log)", "*.txt", "*.md", "*.log"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "All files", "*.*"));
        if (currentFile != null) {
            chooser.setInitialDirectory(currentFile.toFile().getParentFile());
        }
        var file = chooser.showOpenDialog(stage);
        if (file == null) return;

        Path path = file.toPath();
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            textArea.setText(content);
            textArea.positionCaret(0);
            currentFile = path;
            markClean();
        } catch (IOException ex) {
            errorDialog("Open failed", "Could not read " + path.getFileName(), ex);
        }
    }

    @FXML
    void onSave() {
        if (currentFile == null) {
            onSaveAs();
            return;
        }
        writeToFile(currentFile);
    }

    @FXML
    void onSaveAs() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Document");
        chooser.setInitialFileName(currentFile == null ? "untitled.txt" : currentFile.getFileName().toString());
        if (currentFile != null) {
            chooser.setInitialDirectory(currentFile.toFile().getParentFile());
        }
        var file = chooser.showSaveDialog(stage);
        if (file == null) return;
        writeToFile(file.toPath());
    }

    @FXML
    void onExit() {
        if (confirmDiscardIfDirty()) {
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    /* =========================================================
     *  Edit menu
     * ========================================================= */

    @FXML void onUndo()       { textArea.undo(); }
    @FXML void onRedo()       { textArea.redo(); }
    @FXML void onCut()        { textArea.cut(); }
    @FXML void onCopy()       { textArea.copy(); }
    @FXML void onPaste()      { textArea.paste(); }
    @FXML void onSelectAll()  { textArea.selectAll(); }

    @FXML
    void onDelete() {
        var sel = textArea.getSelection();
        if (sel.getLength() > 0) {
            textArea.deleteText(sel);
        }
    }

    /* =========================================================
     *  Format menu
     * ========================================================= */

    @FXML
    void onToggleWrap() {
        textArea.setWrapText(wrapItem.isSelected());
    }

    @FXML
    void onFontUp() {
        setFontSize(getCurrentFontSize() + 2);
    }

    @FXML
    void onFontDown() {
        setFontSize(getCurrentFontSize() - 2);
    }

    /* =========================================================
     *  Help menu
     * ========================================================= */

    @FXML
    void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About " + APP_NAME);
        alert.setHeaderText(APP_NAME + " 1.1.0");
        alert.setContentText(
                "A lightweight notepad clone built with JavaFX.\n\n" +
                "Author: Achraf Menasria\n" +
                "License: MIT\n" +
                "Java: " + System.getProperty("java.version") + "\n" +
                "JavaFX: " + System.getProperty("javafx.version", "n/a") + "\n\n" +
                "Shortcuts:\n" +
                "  Ctrl+N  New\n" +
                "  Ctrl+O  Open\n" +
                "  Ctrl+S  Save / Save As (Shift)\n" +
                "  Ctrl+W  Close\n" +
                "  Ctrl+=  Bigger font\n" +
                "  Ctrl+-  Smaller font");
        alert.initOwner(stage);
        alert.showAndWait();
    }

    /* =========================================================
     *  Internal helpers
     * ========================================================= */

    private void writeToFile(Path path) {
        try {
            Files.writeString(path, textArea.getText(), StandardCharsets.UTF_8);
            currentFile = path;
            markClean();
        } catch (IOException ex) {
            errorDialog("Save failed", "Could not write to " + path.getFileName(), ex);
        }
    }

    /** Binds status bar to live word/char/line/caret stats. */
    private void bindStatus() {
        statusLabel.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    String text  = textArea.getText();
                    String caret = String.valueOf(textArea.getCaretPosition());
                    int    chars = text.length();
                    int    words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
                    int    lines = text.isEmpty() ? 0 : text.split("\n", -1).length;
                    String fileName = currentFile == null ? "untitled" : currentFile.getFileName().toString();
                    return String.format("%s   |   Ln %s   |   %d lines   |   %d words   |   %d chars",
                            fileName, caret, lines, words, chars);
                },
                textArea.textProperty(),
                textArea.caretPositionProperty(),
                /* dirty flag reflected via title update */ textArea.textProperty()
        ));
    }

    /** Marks the document dirty whenever the user edits text (after load). */
    private void installDirtyListeners() {
        textArea.textProperty().addListener((obs, old, neu) -> {
            if (!old.equals(neu)) markDirty();
        });
    }

    private void installKeyboardShortcuts() {
        // File menu
        newMenuItem.setAccelerator(new KeyCodeCombination(N, CONTROL_DOWN));
        openMenuItem.setAccelerator(new KeyCodeCombination(O, CONTROL_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(S, CONTROL_DOWN));
        saveAsMenuItem.setAccelerator(new KeyCodeCombination(S, CONTROL_DOWN, SHIFT_DOWN));
        exitMenuItem.setAccelerator(new KeyCodeCombination(W, CONTROL_DOWN));

        // Edit menu — Cut/Copy/Paste/Undo/Redo have platform defaults on
        // the TextArea itself; we just expose menu items for discoverability.
        selectAllMenuItem.setAccelerator(new KeyCodeCombination(A, CONTROL_DOWN));
        deleteMenuItem.setAccelerator(new KeyCodeCombination(DELETE));

        // Format menu
        fontSizeUpMenuItem.setAccelerator(new KeyCodeCombination(EQUALS, CONTROL_DOWN));
        fontSizeDownMenuItem.setAccelerator(new KeyCodeCombination(MINUS, CONTROL_DOWN));

        // Global key trap: Ctrl+W should not close without confirm.
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (new KeyCodeCombination(W, CONTROL_DOWN).match(e)) {
                onExit();
                e.consume();
            }
        });
    }

    private int getCurrentFontSize() {
        return (int) Math.round(textArea.getFont().getSize());
    }

    private void setFontSize(int size) {
        int clamped = Math.max(MIN_FONT, Math.min(MAX_FONT, size));
        textArea.setFont(Font.font(textArea.getFont().getFamily(), clamped));
    }

    private void markDirty() {
        if (!dirty) {
            dirty = true;
            updateWindowTitle();
        }
    }

    private void markClean() {
        dirty = false;
        updateWindowTitle();
    }

    private void updateWindowTitle() {
        String name = (currentFile == null ? "untitled" : currentFile.getFileName().toString())
                + (dirty ? " *" : "");
        stage.setTitle(name + " - " + APP_NAME);
    }

    /** Asks the user to save if there are unsaved changes. Returns false if they cancelled. */
    private boolean confirmDiscardIfDirty() {
        if (!dirty) return true;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(stage);
        alert.setTitle("Unsaved changes");
        alert.setHeaderText("Do you want to save your changes?");
        alert.setContentText("Your changes will be lost if you don't save them.");

        ButtonType save    = new ButtonType("Save",    ButtonBar.ButtonData.YES);
        ButtonType dontSave = new ButtonType("Don't save", ButtonBar.ButtonData.NO);
        ButtonType cancel  = new ButtonType("Cancel",  ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(save, dontSave, cancel);

        Optional<ButtonType> choice = alert.showAndWait();
        if (choice.isEmpty()) return false;
        ButtonType result = choice.get();
        if (result == cancel) return false;
        if (result == save) {
            onSave();
            // If save was cancelled (still dirty), abort the parent action.
            return !dirty;
        }
        return true; // don't save
    }

    private void onCloseRequest(WindowEvent e) {
        if (!confirmDiscardIfDirty()) {
            e.consume();
        }
    }

    private void errorDialog(String title, String header, Throwable cause) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(cause.getMessage() == null ? cause.toString() : cause.getMessage());
        alert.showAndWait();
    }
}
