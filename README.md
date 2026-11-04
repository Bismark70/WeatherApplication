# Weather Application

A JavaFX desktop weather application with a location search form, current-weather display, unit conversion, forecast information, and a weather-history table.

## Features

- Search for weather by location.
- Display temperature, humidity, wind speed, and conditions.
- Convert temperature between Celsius and Fahrenheit.
- Convert wind speed between metres per second and miles per hour.
- Show a short forecast.
- Add each successful search to the in-memory history table.
- Display an error message when the location is empty or weather data cannot be retrieved.

## Requirements

- Windows 10 or later.
- JDK 24 or a compatible newer JDK.
- JavaFX SDK 27.0.1.
- Visual Studio Code with the Extension Pack for Java.

The current JavaFX SDK is expected at:

```text
C:\Program Files\Java\javafx-sdk-27.0.1\lib
```

The JavaFX SDK must contain files such as `javafx.controls.jar`, `javafx.fxml.jar`, and `javafx.graphics.jar`.

## Opening the Project

1. Open Visual Studio Code.
2. Select **File > Open Folder**.
3. Open the `WeatherApplication` folder.
4. Install the **Extension Pack for Java** if VS Code prompts you.
5. Allow the Java language server to finish loading the project.

JavaFX library paths are configured in `.vscode/settings.json`. Launch arguments, including the JavaFX module path, are configured in `.vscode/launch.json`.

## Running the Application

### From VS Code

1. Open `src/WeatherApp.java`.
2. Open the **Run and Debug** panel.
3. Select **WeatherApp** from the configuration list.
4. Press **F5** or select **Start Debugging**.

### From PowerShell

From the project directory, compile the application:

```powershell
javac --module-path "C:\Program Files\Java\javafx-sdk-27.0.1\lib" `
  --add-modules javafx.controls,javafx.fxml `
  -d bin src\WeatherApp.java
```

Run the compiled application:

```powershell
java --module-path "C:\Program Files\Java\javafx-sdk-27.0.1\lib" `
  --add-modules javafx.controls,javafx.fxml `
  -cp bin WeatherApp
```

## Using the Application

1. Enter a location in the search field.
2. Select **Search**.
3. Review the current weather and forecast.
4. Use the temperature and wind-speed selectors to change units.
5. Review previous successful searches in the **Weather History** table.

The current `WeatherService` uses sample data and caches it by location. Replace its `getWeatherData` implementation with an HTTP API integration when live weather data is required. Store API credentials outside source control when adding a real service.

## Project Structure

```text
WeatherApplication/
├── .vscode/
│   ├── launch.json       # VS Code launch configurations
│   └── settings.json     # Java source, output, and JavaFX libraries
├── src/
│   └── WeatherApp.java   # JavaFX application and weather models
├── bin/                  # Compiled .class files
└── README.md
```

## Troubleshooting

### JavaFX packages cannot be found

Confirm that the JavaFX JAR files exist in:

```text
C:\Program Files\Java\javafx-sdk-27.0.1\lib
```

If the SDK is installed elsewhere, update the paths in both `.vscode/settings.json` and `.vscode/launch.json`.

Then run **Java: Clean Java Language Server Workspace** from the VS Code Command Palette and reload the window.

### JavaFX module errors occur when launching

Ensure the launch configuration contains:

```text
--module-path "C:/Program Files/Java/javafx-sdk-27.0.1/lib" --add-modules javafx.controls,javafx.fxml
```

### Check the active Java version

Run:

```powershell
java -version
javac -version
```

The compiler and runtime should be compatible with the installed JavaFX SDK.

## Notes

- Weather history is currently stored in memory and is lost when the application closes.
- The API key constant is retained for the planned weather-service integration; do not commit real credentials to a public repository.
- The application uses a solid light-blue background and does not require image resources.
