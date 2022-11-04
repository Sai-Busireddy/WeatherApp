package edu.uncc.weather;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uncc.weather.databinding.FragmentWeatherForecastBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherForecastFragment extends Fragment {
    FragmentWeatherForecastBinding binding;
    private final OkHttpClient client = new OkHttpClient();

    private static final String ARG_PARAM_CITY = "City";
    private DataService.City mParamCity;

    public WeatherForecastFragment() {}

    public static WeatherForecastFragment newInstance(DataService.City param1) {
        WeatherForecastFragment fragment = new WeatherForecastFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CITY, param1);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamCity = (DataService.City) getArguments().getSerializable(ARG_PARAM_CITY);
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWeatherForecastBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Weather Forecast");

        binding.textViewCityName.setText(mParamCity.getCity() + ", " + mParamCity.getCountry());

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("api.openweathermap.org")
                .addPathSegments("data/2.5/forecast")
                .addQueryParameter("lat", String.valueOf(mParamCity.getLat()))
                .addQueryParameter("lon", String.valueOf(mParamCity.getLon()))
                .addQueryParameter("appid", MainActivity.API_KEY)
                .addQueryParameter("units", "imperial")
                .build();
        Request request = new Request.Builder()
                .url(httpUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Trouble retrieving Weather Forecast", Toast.LENGTH_LONG).show());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String body = response.body().string();
                    try {
                        JSONObject jsonBody = new JSONObject(body);
                        JSONArray list = jsonBody.getJSONArray("list");
                        ArrayList<Forecast> forecastList = new ArrayList<>();
                        for (int i = 0; i < list.length(); i++) {
                            forecastList.add(new Forecast(list.getJSONObject(i)));
                        }
                        getActivity().runOnUiThread(() -> binding.listView.setAdapter(new ForecastAdapter(getContext(), R.layout.forecast_row_item, forecastList)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

class ForecastAdapter extends ArrayAdapter<Forecast>{

    public ForecastAdapter(@NonNull Context context, int resource, @NonNull List<Forecast> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.forecast_row_item, parent, false);
        }

        Forecast forecast = getItem(position);
        TextView textViewDateTime = convertView.findViewById(R.id.textViewDateTime);
        TextView textViewTemp = convertView.findViewById(R.id.textViewTemp);
        TextView textViewTempMax = convertView.findViewById(R.id.textViewTempMax);
        TextView textViewTempMin = convertView.findViewById(R.id.textViewTempMin);
        TextView textViewHumidity = convertView.findViewById(R.id.textViewHumidity);
        TextView textViewDesc = convertView.findViewById(R.id.textViewDesc);
        ImageView imageViewWeatherIcon = convertView.findViewById(R.id.imageViewWeatherIcon);

        Picasso.get().load(forecast.getIconURL()).into(imageViewWeatherIcon);
        textViewDateTime.setText(forecast.getDate());
        textViewTemp.setText(forecast.getTemp());
        textViewTempMax.setText("Max: " + forecast.getTemp_max());
        textViewTempMin.setText("Min: " + forecast.getTemp_min());
        textViewHumidity.setText("Humidity: " + forecast.getHumidity());
        textViewDesc.setText(forecast.getDescription());

        return convertView;
    }
}