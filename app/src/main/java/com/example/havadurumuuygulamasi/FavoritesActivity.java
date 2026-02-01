package com.example.havadurumuuygulamasi;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    // Veritabanı, liste adaptörü ve verilerin tutulacağı liste
    private Database database;
    private FavoritesAdapter adapter;
    private ArrayList<String> data;

    // Görsel bileşenler
    private TextView tvFavInfo;
    private RecyclerView rvFavorites;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Veritabanı bağlantısını hazırla
        database = new Database(this);

        // Arayüz elemanlarını Java tarafına bağlıyoruz
        tvFavInfo = findViewById(R.id.tvFavInfo);
        rvFavorites = findViewById(R.id.rvFavorites);
        etSearch = findViewById(R.id.etSearch);

        // Listeyi alt alta (dikey) görünecek şekilde ayarla
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));

        // Boş bir liste oluştur
        data = new ArrayList<>();

        // Adaptörü kurulumu (Tıklama ve Uzun basma olayları için)
        adapter = new FavoritesAdapter(
                data,
                city -> {
                    // Şehre kısa basınca Detay sayfasına git ve o şehri gönder
                    Intent i = new Intent(this, DetailActivity.class);
                    i.putExtra("city", city);
                    startActivity(i);
                },
                city -> {
                    // Şehre uzun basınca veritabanından sil ve listeyi anında yenile
                    database.removeFavorite(city);
                    loadFavorites(etSearch.getText().toString());
                }
        );

        // Hazırladığımız adaptörü listeye (RecyclerView) bağla
        rvFavorites.setAdapter(adapter);

        // Sayfa ilk açıldığında tüm favori şehirleri getir
        loadFavorites("");

        // Arama kutusunu dinlemeye başlıyoruz (Yazarken filtreleme yapacak)
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Kullanıcı her harf yazdığında bu metod çalışır
                String keyword = s.toString();
                loadFavorites(keyword); // Yazılan kelimeye göre listeyi yenile
            }
        });

        // Geri butonu işlemi
        MaterialButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    // Veritabanından verileri çekip listeye basan ana metodumuz
    private void loadFavorites(String keyword) {
        // Eski verileri temizle
        data.clear();
        // Veritabanına gidip arama kelimesine uygun şehirleri getir ve listeye ekle
        data.addAll(database.searchFavorites(keyword));
        // Adaptöre "veriler değişti, ekranı yeniden çiz" emri ver
        adapter.notifyDataSetChanged();

        // Kaç sonuç bulunduğuna dair bilgilendirme yazısını güncelle
        int count = data.size();
        if (keyword == null || keyword.trim().isEmpty()) {
            tvFavInfo.setText(
                    "Toplam " + count + " favori bulundu.\n" +
                            "Bir şehre tıklarsan açılır. Uzun basarsan silinir."
            );
        } else {
            tvFavInfo.setText(
                    "'" + keyword + "' için " + count + " sonuç bulundu.\n" +
                            "Bir şehre tıklarsan açılır. Uzun basarsan silinir."
            );
        }
    }
}