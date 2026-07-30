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

**Minimum Java version**: The project targets **JDK 17+** and has been
tested on OpenJDK 21. Use JDK 17 or newer for best compatibility.

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

Windows & Maven notes

- If you don't have `mvn` on Windows, either install Maven or use a
  Maven wrapper. Quick install options:

```powershell
# winget (Windows 10/11)
winget install Apache.Maven

# chocolatey (requires admin)
choco install maven -y
```

You can also run Maven directly from a local install without modifying
`PATH`, for example:

```powershell
& "$env:USERPROFILE\Tools\apache-maven-3.9.6\bin\mvn.cmd" javafx:run
```

Consider adding a Maven Wrapper (`mvnw` / `mvnw.cmd` + `.mvn/wrapper`) to
the repo so contributors can run `./mvnw javafx:run` without installing
Maven globally.

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

## Packaging & distribution

- The project includes the `maven-shade-plugin` to create an executable
  jar (`target/textor-<version>.jar`). Note: platform-specific JavaFX
  native libraries are not always bundled reliably into a simple
  "fat"-jar; `javafx-maven-plugin` is the recommended way to run during
  development. For production/native installers consider using
  `jpackage` or `jlink` to create platform-specific installers that
  include the required Java runtime and JavaFX native libraries.

## Troubleshooting

- GUI doesn't appear or JavaFX errors: ensure you're running a
  compatible JDK (17+) and that the `javafx` artifacts downloaded match
  your OS (the `javafx-maven-plugin` handles this automatically when
  `mvn` is used).
- Warnings about restricted/native access or `sun.misc.Unsafe` are
  emitted by some libraries on newer JDKs — they are typically
  non-fatal. If you want to suppress these you can run Java with the
  suggested `--enable-native-access` flags, but prefer testing on the
  target JDK and platform first.
- If you get UnsatisfiedLinkError or missing native libs when running a
  built jar, run with `mvn javafx:run` or build a native package with
  `jpackage` so native JavaFX binaries are included.

## Tests

- There are no automated tests included by default. If you add tests,
  run them with:

```bash
mvn test
```

## Contributing & Issues

- Contributions are welcome. Please open issues for bugs or feature
  requests and submit pull requests for fixes.
- Good first steps for contributors:
  - Fork the repo and create a feature branch.
  - Add tests for behavior changes.
  - Run `mvn -DskipTests=false package` to verify the build.

## Polishing suggestions (optional)

- Add a screenshot or short GIF of the UI in the README to help
  visitors quickly understand the app.
- Add a CI badge (GitHub Actions / other) to show build status.
- Add a `CHANGELOG.md` or release notes link for release history.

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
