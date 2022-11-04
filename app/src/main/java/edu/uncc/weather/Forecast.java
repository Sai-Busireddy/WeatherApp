package edu.uncc.weather;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Forecast {
    private String date, temp, temp_max, temp_min, description, humidity, iconURL;

    public Forecast(JSONObject jsonBody) {

        try {
            JSONObject main = jsonBody.getJSONObject("main");
            JSONObject weather = jsonBody.getJSONArray("weather").getJSONObject(0);

            this.date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(jsonBody.getLong("dt") * 1000));
            this.temp = main.getString("temp") + " F";
            this.temp_max = main.getString("temp_max") + " F";
            this.temp_min = main.getString("temp_min") + " F";
            this.humidity = main.getString("humidity") + "%";
            this.description = MainActivity.toTitleCase(weather.getString("description"));
            this.iconURL = "https://openweathermap.org/img/wn/" + weather.getString("icon") + "@2x.png";

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getDate() {
        return date;
    }

    public String getTemp() {
        return temp;
    }

    public String getTemp_max() {
        return temp_max;
    }

    public String getTemp_min() {
        return temp_min;
    }

    public String getDescription() {
        return description;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getIconURL() {
        return iconURL;
    }
}
