package com.example.havadurumuuygulamasi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

// internete gidip hava durumunu çekip getiren yardımcı sınıf
public class WeatherApiHelper {

    // 1 - İnternetten veriyi alma metodu
    private static String getJson(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        // Bağlantıyı kurulumu
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setConnectTimeout(8000); // 8 saniyede bağlanamazsan vazgeç
        c.setReadTimeout(8000);    // 8 saniyede veriyi okuyamazsan vazgeç

        // Sunucudan gelen cevap kodunu kontrol et
        int code = c.getResponseCode();
        if (code != 200) {
            throw new Exception("HTTP Hatası: " + code);
        }

        // Gelen veriyi satır satır okuyup birleştirme
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        c.disconnect();
        return sb.toString(); // Okuduğumuz veriyi bir bütün metin olarak döndür
    }

    // 2 - Şehir ismiyle hava durumu sorgulama
    public static WeatherResult getWeatherData(String city) throws Exception {

        // Önce şehrin koordinatlarını bulmalıy çünkü hava durumu sitesi "Ankara"dan anlamaz, sayısal koordinat ister.
        String geoUrl =
                "https://geocoding-api.open-meteo.com/v1/search?name="
                        + URLEncoder.encode(city, "UTF-8")
                        + "&count=1&language=tr";

        // Koordinat bilgilerini internetten çekiyoruz
        JSONObject geoJson = new JSONObject(getJson(geoUrl));

        // Şehir bulunamadıysa hata
        if (!geoJson.has("results")) throw new Exception("Şehir bulunamadı");
        JSONArray results = geoJson.getJSONArray("results");
        if (results.length() == 0) throw new Exception("Şehir bulunamadı");

        // Gelen ilk sonucun koordinatlarını al
        JSONObject location = results.getJSONObject(0);
        double lat = location.getDouble("latitude");
        double lon = location.getDouble("longitude");

        // 3 - Hava durumu sorgusu
        String weatherUrl =
                "https://api.open-meteo.com/v1/forecast?latitude=" + lat
                        + "&longitude=" + lon
                        + "&current=temperature_2m,weather_code" // Şu anki sıcaklık ve hava tipi
                        + "&hourly=temperature_2m,weather_code"  // Saatlik tahminler
                        + "&forecast_hours=12"                   // önümüzdeki 12 saat
                        + "&timezone=auto";

        // Hava durumu verisini indiriyoruz
        JSONObject root = new JSONObject(getJson(weatherUrl));

        // Şu anki verileri ayıklıyoruz
        JSONObject current = root.getJSONObject("current");
        double currentTemp = current.getDouble("temperature_2m");
        int currentCode = current.getInt("weather_code");

        // Saatlik verileri ayıklıyoruz (Diziler halinde gelir)
        JSONObject hourly = root.getJSONObject("hourly");
        JSONArray timesArr = hourly.getJSONArray("time");
        JSONArray tempsArr = hourly.getJSONArray("temperature_2m");
        JSONArray codesArr = hourly.getJSONArray("weather_code");

        // Uygulama çökmesin diye en küçük dizi boyutuna göre 12 adet veri hazırlanıyor burada
        int n = Math.min(12, Math.min(timesArr.length(), Math.min(tempsArr.length(), codesArr.length())));

        String[] times = new String[n];
        double[] temps = new double[n];
        int[] codes = new int[n];

        // Gelen verinin listelere dolumu
        for (int i = 0; i < n; i++) {
            times[i] = timesArr.getString(i);
            temps[i] = tempsArr.getDouble(i);
            codes[i] = codesArr.getInt(i);
        }

        // Tüm bu ayıklanmış bilgileri bir paket (WeatherResult) yapıp gönderiyoruz
        return new WeatherResult(currentTemp, currentCode, times, temps, codes);
    }
}