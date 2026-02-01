package com.example.havadurumuuygulamasi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// HourlyAdapter: Saatlik hava durumu tahminlerini yatay listede gösteren adaptör.
public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.VH> {

    // İnternetten gelen 3 farklı veri dizisini (saatler, sıcaklıklar, kodlar) tutuyoruz.
    private final String[] times;
    private final double[] temps;
    private final int[] codes;      // Örn: 1 (Hava durumu kodu)

    // Kurucu metod: Verileri API'den geldikten sonra buraya teslim alıyoruz.
    public HourlyAdapter(String[] times, double[] temps, int[] codes) {
        this.times = times;
        this.temps = temps;
        this.codes = codes;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_hourly.xml tasarımını (küçük saatlik kutucuklar) oluşturuyoruz.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hourly, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        // Saat formatlama
        String t = times[position];
        // API'den gelen "2026-01-23T10:00" metnini sadece "10:00" kısmını alacak şekilde parçalıyoruz.
        String hour = (t != null && t.contains("T")) ? t.split("T")[1] : t;
        h.tvHour.setText(hour);

        // Sıcaklık yazdırma
        // Derece yuvarlayarak yanına °C eklenecek
        h.tvTemp.setText(String.format("%.0f°C", temps[position]));

        // İkon belirleme
        // Gelen hava durumu koduna göre (Örn: 0=Güneşli) WeatherUi üzerinden doğru görseli buluyoruz
        WeatherUi.WeatherVisual visual = WeatherUi.fromWeatherCode(codes[position]);
        h.imgIcon.setImageResource(visual.iconResId);
    }

    @Override
    public int getItemCount() {
        // Hangi dizi daha kısaysa ona göre sayı veriyoruz ki uygulama çökmesin
        return Math.min(times.length, Math.min(temps.length, codes.length));
    }

    // ViewHolder: Her bir saatlik kutunun içindeki elemanları hafızada tutan sınıftır
    static class VH extends RecyclerView.ViewHolder {
        TextView tvHour, tvTemp;
        ImageView imgIcon;

        VH(@NonNull View itemView) {
            super(itemView);
            // Tasarımdaki saat, sıcaklık ve ikon alanlarını Java'ya bağlayan yer
            tvHour = itemView.findViewById(R.id.tvHour);
            tvTemp = itemView.findViewById(R.id.tvHourTemp);
            imgIcon = itemView.findViewById(R.id.imgIcon);
        }
    }
}