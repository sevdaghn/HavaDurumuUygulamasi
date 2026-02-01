package com.example.havadurumuuygulamasi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// RecyclerView.Adapter: Bu sınıf, veri listesi ile ekran arasındaki köprüdür.
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.VH> {

    // Tıklama olaylarını dışarıya (FavoritesActivity'ye) haber vermek için arayüzler
    public interface OnClick {
        void onClick(String city);
    }

    public interface OnLongClick {
        void onLongClick(String city);
    }

    private final ArrayList<String> data; // Şehir isimlerini tutan liste
    private final OnClick onClick;         // Kısa tıklama görevi
    private final OnLongClick onLongClick; // Uzun basma görevi

    // Kurucu metod: Listeyi ve tıklama görevlerini buradan teslim alıyoruz
    public FavoritesAdapter(ArrayList<String> data, OnClick onClick, OnLongClick onLongClick) {
        this.data = data;
        this.onClick = onClick;
        this.onLongClick = onLongClick;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_favorite.xml tasarımını kod tarafına çekip bir View oluşturuyoruz
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new VH(v); // Bu tasarımı ViewHolder'a teslim et
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        // Listeden sıradaki şehri alıyoruz
        String city = data.get(position);
        // ViewHolder içindeki yazı kutusuna şehir ismini yazdırıyoruz
        h.tvCityFav.setText(city);

        // Kutuya kısa basılırsa:
        h.itemView.setOnClickListener(v -> onClick.onClick(city));

        // Kutuya uzun basılırsa:
        h.itemView.setOnLongClickListener(v -> {
            onLongClick.onLongClick(city);
            return true; // İşlem tamamlandı demek
        });
    }

    // Listede toplam kaç tane eleman olduğunu söyler
    @Override
    public int getItemCount() {
        return data.size();
    }

    // ViewHolder (VH): Listenin her bir satırındaki görsel elemanları hafızada tutan depo
    static class VH extends RecyclerView.ViewHolder {
        TextView tvCityFav;

        VH(@NonNull View itemView) {
            super(itemView);
            // Tasarımdaki yazı kutusunu bulup değişkene bağlıyoruz
            tvCityFav = itemView.findViewById(R.id.tvCityFav);
        }
    }
}