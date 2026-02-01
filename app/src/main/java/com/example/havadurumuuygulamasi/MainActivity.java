package com.example.havadurumuuygulamasi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

// Uygulamanın ana giriş ekranı
public class MainActivity extends AppCompatActivity {

    private EditText etCity;            // Şehir ismi
    private MaterialButton btnGetWeather; // Sorgulama butonu
    private TextView tvStatus;           // Hata mesajlarını gösteren yazı

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Üst kısımdaki Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        etCity = findViewById(R.id.etCity);
        btnGetWeather = findViewById(R.id.btnGetWeather);
        tvStatus = findViewById(R.id.tvStatus);

        // Kullanıcı yazı yazarken ilk harfi otomatik büyüten dinleyici
        etCity.addTextChangedListener(new android.text.TextWatcher() {
            private boolean editing = false; // Kısırdöngüye girmemek için kontrol

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (editing) return;
                editing = true;

                String text = s.toString();
                if (!text.isEmpty()) {
                    // Türkçe karakter desteğiyle ilk harfi büyüt
                    Locale tr = new Locale("tr", "TR");
                    String fixed = text.substring(0, 1).toUpperCase(tr) + text.substring(1);

                    // Eğer metin değiştiyse kutuya yeni halini yaz ve imleci sona al
                    if (!fixed.equals(text)) {
                        etCity.setText(fixed);
                        etCity.setSelection(fixed.length());
                    }
                }
                editing = false;
            }
        });

        // "Hava Durumunu Getir" butonuna basınca ne olacağı
        btnGetWeather.setOnClickListener(v -> {
            String city = etCity.getText().toString().trim(); // Baştaki ve sondaki boşlukları temizle

            // Şehir girilmemişse kullanıcıyı uyar
            if (city.isEmpty()) {
                tvStatus.setText("Lütfen şehir adı girin");
                return;
            }

            tvStatus.setText(""); // Hata mesajını temizle

            // Detay sayfasına (DetailActivity) git ve girilen şehir ismini yanına al
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("city", city);
            startActivity(intent);
        });
    }

    // Menü
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Menüdeki seçeneklerden birine tıklandığında ne olacağı
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Favoriler butonuna basıldıysa favoriler sayfasını aç
        if (id == R.id.menu_favorites) {
            startActivity(new Intent(this, FavoritesActivity.class));
            return true;
        }

        // Ayarlar butonuna basıldıysa birdialog göster
        if (id == R.id.menu_settings) {
            new AlertDialog.Builder(this)
                    .setTitle("Ayarlar")
                    .setMessage("Gelecek sürümlerde sıcaklık birimi ve tema ayarları eklenecektir.")
                    .setPositiveButton("Tamam", null)
                    .show();
            return true;
        }

        // Yardım butonuna basıldıysa uygulamanın nasıl kullanılacağını göster
        if (id == R.id.menu_help) {
            new AlertDialog.Builder(this)
                    .setTitle("Yardım")
                    .setMessage("• Şehir adını yazıp butonuna basın.\n• Şehri favorilere ekleyebilirsiniz.")
                    .setPositiveButton("Tamam", null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}