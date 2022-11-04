package edu.uncc.weather;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.uncc.weather.databinding.FragmentCurrentWeatherBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrentWeatherFragment extends Fragment {
    private static final String ARG_PARAM_CITY = "ARG_PARAM_CITY";
    private DataService.City mCity;
    FragmentCurrentWeatherBinding binding;
    private final OkHttpClient client = new OkHttpClient();

    public CurrentWeatherFragment() {
    }

    public static CurrentWeatherFragment newInstance(DataService.City city) {
        CurrentWeatherFragment fragment = new CurrentWeatherFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCity = (DataService.City) getArguments().getSerializable(ARG_PARAM_CITY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Current Weather");

        binding.textViewCityName.setText(mCity.getCity() + ", " + mCity.getCountry());

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("api.openweathermap.org")
                .addPathSegments("data/2.5/weather")
                .addQueryParameter("lat", String.valueOf(mCity.getLat()))
                .addQueryParameter("lon", String.valueOf(mCity.getLon()))
                .addQueryParameter("appid", MainActivity.API_KEY)
                .addQueryParameter("units", "imperial")
                .build();
        Request request = new Request.Builder()
                .url(httpUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Trouble retrieving Weather", Toast.LENGTH_LONG).show());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String body = response.body().string();
                    try {
                        JSONObject jsonBody = new JSONObject(body);
                        Weather weather = new Weather(jsonBody);

                        getActivity().runOnUiThread(() -> {
                            Picasso.get().load(weather.getIconURL()).into(binding.imageViewWeatherIcon);
                            binding.textViewTemp.setText(weather.getTemp());
                            binding.textViewTempMax.setText(weather.getTemp_max());
                            binding.textViewTempMin.setText(weather.getTemp_min());
                            binding.textViewDesc.setText(MainActivity.toTitleCase(weather.getDescription()));
                            binding.textViewHumidity.setText(weather.getHumidity());
                            binding.textViewWindSpeed.setText(weather.getWindSpeed());
                            binding.textViewWindDegree.setText(weather.getWindDegree());
                            binding.textViewCloudiness.setText(weather.getCloudiness());

                            binding.buttonCheckForecast.setOnClickListener(v -> var_CurrWeather.gotoForecast(mCity));
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        var_CurrWeather = (ICurrWeather) context;
    }

    ICurrWeather var_CurrWeather;
    interface ICurrWeather{
        void gotoForecast(DataService.City city);
    }
}