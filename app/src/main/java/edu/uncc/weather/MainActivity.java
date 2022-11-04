package edu.uncc.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements CitiesFragment.CitiesFragmentListener, CurrentWeatherFragment.ICurrWeather {
    public static final String API_KEY = "1d94bbc3a06b578ae932631b4560e90e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.rootView, new CitiesFragment())
                .commit();
    }

    @Override
    public void gotoCurrentWeather(DataService.City city) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, CurrentWeatherFragment.newInstance(city))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoForecast(DataService.City city) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, WeatherForecastFragment.newInstance(city))
                .addToBackStack(null)
                .commit();
    }

    public static String toTitleCase(String str) {
        String[] words = str.toLowerCase().split(" ");
        StringBuilder convertedStr = new StringBuilder();
        for (String word : words) {
            convertedStr.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            if (!word.equals(words[words.length - 1])) {
                convertedStr.append(" ");
            }
        }
        return convertedStr.toString();
    }
}