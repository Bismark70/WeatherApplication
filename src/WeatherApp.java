import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WeatherApp extends Application {
    private static final String API_KEY = "3d885fab3dae807bf2b1a39456b06503";
    private WeatherService weatherService;
    private WeatherHistoryManager weatherHistoryManager;

    private StringProperty locationProperty = new SimpleStringProperty();
    private StringProperty temperatureProperty = new SimpleStringProperty();
    private StringProperty humidityProperty = new SimpleStringProperty();
    private StringProperty windSpeedProperty = new SimpleStringProperty();
    private StringProperty conditionProperty = new SimpleStringProperty();
    private StringProperty forecastProperty = new SimpleStringProperty();
    private StringProperty temperatureUnitProperty = new SimpleStringProperty("°C");
    private StringProperty windSpeedUnitProperty = new SimpleStringProperty("m/s");

    private ObservableList<WeatherHistory> weatherHistory = FXCollections.observableArrayList();
    private WeatherData currentWeatherData;

    @Override
    public void start(Stage primaryStage) {
        // Initialize the WeatherService and WeatherHistoryManager
        weatherService = new WeatherService(API_KEY);
        weatherHistoryManager = new WeatherHistoryManager();

        // Create the GUI components and bind them to the weather data
        BorderPane root = createGUI();

        // Set the scene and show the stage
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Weather App");
        primaryStage.show();
    }

	private BorderPane createGUI() {
        // Location input
        TextField locationInput = new TextField();
        locationInput.setPromptText("Enter location");
        locationInput.textProperty().bindBidirectional(locationProperty);

        // Search button
        Button searchButton = new Button("Search");
        searchButton.setOnAction(event -> updateWeatherData());
       
         // Temperature display
            Label temperatureLabel = new Label();
            temperatureLabel.textProperty().bind(temperatureProperty);

            // Humidity display
            Label humidityLabel = new Label();
            humidityLabel.textProperty().bind(humidityProperty);

            // Wind Speed display
            Label windSpeedLabel = new Label();
            windSpeedLabel.textProperty().bind(windSpeedProperty);

            // Condition display
            Label conditionLabel = new Label();
            conditionLabel.textProperty().bind(conditionProperty);

            VBox weatherDisplay = new VBox(
                new Label("Current Weather"),
                new Label("Temperature: "), temperatureLabel,
                new Label("Humidity: "), humidityLabel,
                new Label("Wind Speed: "), windSpeedLabel,
                new Label("Condition: "), conditionLabel
            );
            weatherDisplay.setSpacing(10);

        // Forecast display
        Label forecastLabel = new Label();
        forecastLabel.textProperty().bind(forecastProperty);
            
        VBox forecastDisplay = new VBox(
            new Label("Forecast"),
            forecastLabel
        );
        forecastDisplay.setSpacing(10);

     // Temperature Unit ChoiceBox
        ChoiceBox<String> tempChoiceBox = new ChoiceBox<>();
        tempChoiceBox.getItems().addAll("°C", "°F");
        tempChoiceBox.setValue(temperatureUnitProperty.get());
        tempChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                temperatureUnitProperty.set(newValue);
                refreshWeatherDisplay();
            }
        });

        // Wind Speed Unit ChoiceBox
        ChoiceBox<String> windChoiceBox = new ChoiceBox<>();
        windChoiceBox.getItems().addAll("m/s", "mph");
        windChoiceBox.setValue(windSpeedUnitProperty.get());
        windChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                windSpeedUnitProperty.set(newValue);
                refreshWeatherDisplay();
            }
        });

        // Unit Conversion
        HBox unitConversion = new HBox(
            new Label("Temperature Unit: "), tempChoiceBox,
            new Label("Wind Speed Unit: "), windChoiceBox
        );
        unitConversion.setSpacing(10);
        
        // Weather history
        TableView<WeatherHistory> historyTable = new TableView<>(weatherHistory);
        historyTable.setEditable(false);

historyTable.getColumns().addAll(
            createTableColumn("Location", "location"),
            createTableColumn("Temperature", "temperature"),
            createTableColumn("Humidity", "humidity"),
            createTableColumn("Wind Speed", "windSpeed"),
            createTableColumn("Condition", "condition"),
            createTableColumn("Time", "time")
        );

        VBox historyDisplay = new VBox(
            new Label("Weather History"),
            historyTable
        );
        historyDisplay.setSpacing(10);

        // Layout
        GridPane grid = new GridPane();
        grid.add(locationInput, 0, 0);
        grid.add(searchButton, 1, 0);
        grid.add(weatherDisplay, 0, 1);
        grid.add(forecastDisplay, 1, 1);
        grid.add(unitConversion, 0, 2, 2, 1);
        grid.add(historyDisplay, 0, 3, 2, 1);
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20));

        grid.setBackground(new Background(
            new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)
        ));

        BorderPane root = new BorderPane();
        root.setCenter(grid);
        return root;
    }

    private void updateWeatherData() {
        String location = locationProperty.get() == null ? "" : locationProperty.get().trim();
        if (location.isEmpty()) {
            showError("Please enter a location.");
            return;
        }

        WeatherData weatherData = weatherService.getWeatherData(location);
        if (weatherData != null) {
            locationProperty.set(location);
            currentWeatherData = weatherData;
            refreshWeatherDisplay();

            WeatherHistory history = new WeatherHistory(
                location,
                temperatureProperty.get(),
                humidityProperty.get(),
                windSpeedProperty.get(),
                conditionProperty.get(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
            weatherHistory.add(0, history);
            weatherHistoryManager.saveWeatherHistory(history);
        } else {
            showError("Unable to retrieve weather data for " + location + ".");
        }
    }

    private void refreshWeatherDisplay() {
        if (currentWeatherData == null) {
            return;
        }

        double temperature = currentWeatherData.getTemperature();
        if ("°F".equals(temperatureUnitProperty.get())) {
            temperature = temperature * 9 / 5 + 32;
        }

        double windSpeed = currentWeatherData.getWindSpeed();
        if ("mph".equals(windSpeedUnitProperty.get())) {
            windSpeed *= 2.23693629;
        }

        temperatureProperty.set(String.format(Locale.ROOT, "%.1f %s", temperature, temperatureUnitProperty.get()));
        humidityProperty.set(String.format(Locale.ROOT, "%.0f%%", currentWeatherData.getHumidity()));
        windSpeedProperty.set(String.format(Locale.ROOT, "%.1f %s", windSpeed, windSpeedUnitProperty.get()));
        conditionProperty.set(currentWeatherData.getCondition());
        forecastProperty.set(currentWeatherData.getForecast());
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Weather App");
        alert.setHeaderText("Weather lookup failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private TableColumn<WeatherHistory, String> createTableColumn(String title, String property) {
        TableColumn<WeatherHistory, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> cellData.getValue().getStringProperty(property));
        return column;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class WeatherHistory {
        private final StringProperty location;
        private final StringProperty temperature;
        private final StringProperty humidity;
        private final StringProperty windSpeed;
        private final StringProperty condition;
        private final StringProperty time;

        public WeatherHistory(String location, String temperature, String humidity, String windSpeed, String condition, String time) {
            this.location = new SimpleStringProperty(location);
            this.temperature = new SimpleStringProperty(temperature);
            this.humidity = new SimpleStringProperty(humidity);
            this.windSpeed = new SimpleStringProperty(windSpeed);
            this.condition = new SimpleStringProperty(condition);
            this.time = new SimpleStringProperty(time);
        }

public StringProperty getStringProperty(String property) {
            switch (property) {
                case "location":
                    return location;
                case "temperature":
                    return temperature;
                case "humidity":
                    return humidity;
                case "windSpeed":
                    return windSpeed;
                case "condition":
                    return condition;
                case "time":
                    return time;
                default:
                    throw new IllegalArgumentException("Invalid property: " + property);
            }
        }
    }

    private static class WeatherData {
        private double temperature;
        private double humidity;
        private double windSpeed;
        private String condition;
        private String forecast;

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public double getHumidity() {
            return humidity;
        }

        public void setHumidity(double humidity) {
            this.humidity = humidity;
        }

        public double getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(double windSpeed) {
            this.windSpeed = windSpeed;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getForecast() {
            return forecast;
        }

        public void setForecast(String forecast) {
            this.forecast = forecast;
        }
    }

    private static class WeatherService {
        private final String apiKey;
        private final Map<String, WeatherData> weatherDataCache = new HashMap<>();

        public WeatherService(String apiKey) {
            this.apiKey = apiKey;
        }

        public WeatherData getWeatherData(String location) {
            // Implement the logic to fetch weather data from the weather API
            // and return a WeatherData object

            // For now, we'll just return some sample data
            if (!weatherDataCache.containsKey(location)) {
                WeatherData weatherData = new WeatherData();
                weatherData.setTemperature(20.5);
                weatherData.setHumidity(60.0);
                weatherData.setWindSpeed(5.2);
                weatherData.setCondition("Sunny");
                weatherData.setForecast("Mostly sunny with a high of 25°C");
                weatherDataCache.put(location, weatherData);
            }

            return weatherDataCache.get(location);
        }
    }

    private static class WeatherHistoryManager {
        // Implement the logic to save and retrieve weather history data
        // (e.g., using a file, database, or in-memory storage)

        public void saveWeatherHistory(WeatherHistory history) {
            // Save the weather history data
        }
    }
}
