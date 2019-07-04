package com.example.weatherforecasting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    //UI variables
    TextView cityName;
    TextView weatherText;
    TextView tempText;
    TextView humidityText;

    String weather;
    String temp;
    String humidity;

    //Api variable
    String api;
    String id = BuildConfig.ApiKey;

    //User city
    String city;

    //This function runs when button pressed
    public void getWeather(View view){
        city = cityName.getText().toString();

        if(city.isEmpty()){
            Toast.makeText(this, "City name can't be empty", Toast.LENGTH_SHORT).show();
        }else{

            DownloadWeather task = new DownloadWeather();

            api = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + id;

            try {
                task.execute(api);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    //This function print data on screen
    public void printData(){
        weatherText.setText(weather);

        double tempInDouble = Double.parseDouble(temp) - 273.15;
        int tempInInteger =(int) Math.floor(tempInDouble);

        String tempInCelsius = Integer.toString(tempInInteger) + "° C";

        tempText.setText(tempInCelsius);
        humidityText.setText(humidity + "%");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (TextView) findViewById(R.id.cityName);
        weatherText = (TextView) findViewById(R.id.weatherText);
        tempText = (TextView) findViewById(R.id.tempText);
        humidityText = (TextView) findViewById(R.id.humidityText);
    }

    public class DownloadWeather extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                String result = "";

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;
            } catch(Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");
                String tempInfo = jsonObject.getString("main");

                JSONArray weatherArr = new JSONArray(weatherInfo);
                for(int i = 0; i < weatherArr.length(); i++){
                    JSONObject weatherPart = weatherArr.getJSONObject(i);

                    weather = weatherPart.getString("description");
                }

                JSONObject tempPart = new JSONObject(tempInfo);
                temp = tempPart.getString("temp");
                humidity = tempPart.getString("humidity");

                printData();

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
