# Textor

A lightweight notepad clone built with **JavaFX 21** and **Java 17**.

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)
![License](https://img.shields.io/badge/license-MIT-green)
![Build](https://img.shields.io/badge/build-mvn%20package-brightgreen)

<p align="center">
  <img src="src/main/resources/img/icon.png" width="120" alt="Textor icon"/>
</p>

---

## Features

| Category | What it does |
|----------|--------------|
| **File** | New, Open, Save, Save As, Exit — with confirmation on unsaved changes |
| **Edit** | Undo, Redo, Cut, Copy, Paste, Delete, Select All |
| **Format** | Word-wrap toggle, font size +/- (8–72 px) |
| **Status bar** | Live caret position, line/word/char count, current file name, encoding badge |
| **Shortcuts** | `Ctrl+N/O/S/W`, `Ctrl+Shift+S` (Save As), `Ctrl+=` / `Ctrl+-` (font) |
| **Dark theme** | Catppuccin Mocha-inspired stylesheet, customizable via `textor.css` |

## Why this exists

A small personal exercise in JavaFX — building a useful desktop app from
scratch with proper MVC separation (FXML + controller), Maven build, and
idiomatic Java IO. Started as a 100-line tutorial clone, refactored into
something portfolio-ready.

## Requirements

- **JDK 17 or later** (built and tested on OpenJDK 21)
- **Maven 3.6+**
- No need to download the JavaFX SDK separately — the `javafx-maven-plugin`
  pulls the right native jars for your OS from Maven Central.

## Build & Run

```bash
# 1. Clone
git clone https://github.com/achrafmenasria/textor.git
cd textor

# 2. Run (downloads JavaFX automatically)
mvn javafx:run

# 3. Package a runnable jar
mvn package
java -jar target/textor-1.1.0.jar
```

## Project structure

```
textor/
├── pom.xml                              # Maven build, JavaFX deps, shade plugin
├── src/main/java/com/achraf/textor/
│   ├── Textor.java                      # Application entry, loads FXML
│   └── TextorController.java            # All editor logic
└── src/main/resources/
    ├── img/icon.png                     # App icon (classpath resource)
    └── ui/
        ├── textor.fxml                  # UI layout (menu bar + textarea + statusbar)
        └── textor.css                   # Dark theme stylesheet
```

## Keyboard shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl+N` | New file |
| `Ctrl+O` | Open file |
| `Ctrl+S` | Save (or Save As if file is new) |
| `Ctrl+Shift+S` | Save As |
| `Ctrl+W` | Close (asks to save if dirty) |
| `Ctrl+A` | Select all |
| `Delete` | Delete selection |
| `Ctrl+=` | Increase font size |
| `Ctrl+-` | Decrease font size |

## Roadmap

- [ ] Line numbers gutter
- [ ] Find & Replace (`Ctrl+F` / `Ctrl+H`)
- [ ] Multi-tab editing
- [ ] Syntax highlighting for Markdown / JSON
- [ ] Persisted recent-files list
- [ ] Light/dark theme toggle

## License

MIT — see [LICENSE](LICENSE).
