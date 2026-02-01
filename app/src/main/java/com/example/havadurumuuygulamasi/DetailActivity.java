package com.example.havadurumuuygulamasi;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    // Arayüz elemanlarını burada tanımlıyoruz
    private TextView tvCity, tvTemp, tvNowCondition;
    private ImageView imgNowIcon;
    private RecyclerView rvHourly;
    private Button btnBack;
    private MaterialButton btnFav;

    // Veritabanı ve seçilen şehir değişkeni
    private Database database;
    private String city = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // XML tarafındaki id'lerle java değişkenlerini eşleştiriyoruz
        tvCity = findViewById(R.id.tvCity);
        tvTemp = findViewById(R.id.tvTemp);
        tvNowCondition = findViewById(R.id.tvNowCondition);
        imgNowIcon = findViewById(R.id.imgNowIcon);
        rvHourly = findViewById(R.id.rvHourly);
        btnBack = findViewById(R.id.btnBack);
        btnFav = findViewById(R.id.btnFav);

        // Veritabanı bağlantısını başlatalım
        database = new Database(this);

        // Saatlik hava durumu listesi YATAY (Horizontal) kayacak şekilde ayarlandı
        rvHourly.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        // Ana sayfadan gönderilen şehir bilgisini alıyoruz
        String c = getIntent().getStringExtra("city");
        if (c != null) city = c.trim(); // Boşlukları temizleyelim

        // Başlangıçta kullanıcıya yükleniyor mesajı verelim ki dondu sanmasın
        tvCity.setText(toTitle(city));
        tvTemp.setText("-");
        tvNowCondition.setText("Yükleniyor...");

        // Favori butonunun güncel durumunu (Ekle mi Çıkar mı?) kontrol et
        updateFavButtonText();

        // Favori butonuna tıklanınca yapılacak işlemler
        btnFav.setOnClickListener(v -> {
            // Veritabanı yazma/silme işlemi bazen milisaniyeler de olsa sürer,
            // arayüzü kilitlememek için arka plan thread'i açtım.
            new Thread(() -> {
                boolean isFav = database.isFavorite(city);
                if (isFav) database.removeFavorite(city); // Zaten varsa sil
                else database.addFavorite(city);          // Yoksa ekle

                // Arayüzü güncellemek için tekrar ana thread'e dönmemiz şart
                runOnUiThread(this::updateFavButtonText);
            }).start();
        });

        // Hava durumu verilerini internetten çekmek için Thread başlatıyoruz
        // (Ana thread'de ağ işlemi yaparsak uygulama çöker)
        new Thread(() -> {
            try {
                // API'ye isteği burada atıyoruz
                WeatherResult r = WeatherApiHelper.getWeatherData(city);

                // Veri geldikten sonra ekrana basma işlemini UI thread'de yapıyoruz
                runOnUiThread(() -> showWeather(
                        r.currentTemp,
                        r.currentCode,
                        r.times,
                        r.temps,
                        r.codes
                ));
            } catch (Exception e) {
                // İnternet yoksa veya API hatası varsa kullanıcıya sebebini gösterelim
                runOnUiThread(() -> {
                    tvTemp.setText("-");
                    tvNowCondition.setText("Veri alınamadı: " + e.getMessage());
                });
            }
        }).start();

        // Geri tuşuna basınca aktiviteyi kapat
        btnBack.setOnClickListener(v -> finish());
    }

    // Favori butonunun metnini veritabanına bakarak güncelleyen yardımcı metod
    private void updateFavButtonText() {
        new Thread(() -> {
            boolean fav = database.isFavorite(city);
            // UI güncellemesi olduğu için runOnUiThread kullanıyoruz
            runOnUiThread(() -> btnFav.setText(fav ? "Favorilerden çıkar" : "Favorilere ekle"));
        }).start();
    }

    // API'den gelen verileri ekrandaki view'lara yerleştirir
    private void showWeather(
            double currentTemp,
            int currentCode,
            String[] times,
            double[] temps,
            int[] codes
    ) {
        // Dereceyi virgülden sonra 1 basamak olacak şekilde formatladım
        tvTemp.setText(String.format(Locale.getDefault(), "%.1f°C", currentTemp));

        // Hava kodu (0, 1, 45 vs) ne anlama geliyorsa ikonunu ve metnini bul
        WeatherUi.WeatherVisual now = WeatherUi.fromWeatherCode(currentCode);
        tvNowCondition.setText(now.label);
        imgNowIcon.setImageResource(now.iconResId);

        // Saatlik listeyi doldur
        if (times != null && temps != null && codes != null) {
            rvHourly.setAdapter(new HourlyAdapter(times, temps, codes));
        }
    }

    // Şehir isminin sadece baş harfini büyütmek için ufak bir metod (istanbul -> İstanbul)
    private String toTitle(String s) {
        s = s.trim();
        if (s.isEmpty()) return s;
        Locale tr = new Locale("tr", "TR");
        return s.substring(0, 1).toUpperCase(tr) + s.substring(1);
    }
}