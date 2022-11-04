package edu.uncc.weather;

import org.json.JSONException;
import org.json.JSONObject;

public class Weather {
    private String temp, temp_max, temp_min, description, humidity,
            windSpeed, windDegree, cloudiness, iconURL;

    public Weather(JSONObject jsonBody) {

        try {
            JSONObject weather = jsonBody.getJSONArray("weather").getJSONObject(0);
            JSONObject main = jsonBody.getJSONObject("main");
            JSONObject wind = jsonBody.getJSONObject("wind");
            JSONObject clouds = jsonBody.getJSONObject("clouds");

            this.temp = main.getString("temp") + " F";
            this.temp_max = main.getString("temp_max") + " F";
            this.temp_min = main.getString("temp_min") + " F";
            this.description = weather.getString("description");
            this.humidity = main.getString("humidity") + "%";
            this.windSpeed = wind.getString("speed") + " miles/hr";
            this.windDegree = wind.getString("deg") + " degrees";
            this.cloudiness = clouds.getString("all") + "%";
            this.iconURL = "https://openweathermap.org/img/wn/" + weather.getString("icon") + "@2x.png";

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTemp() { return temp; }

    public String getTemp_max() { return temp_max; }

    public String getTemp_min() { return temp_min; }

    public String getDescription() { return description; }

    public String getHumidity() { return humidity; }

    public String getWindSpeed() { return windSpeed; }

    public String getWindDegree() { return windDegree; }

    public String getCloudiness() { return cloudiness; }

    public String getIconURL() { return iconURL; }
}
