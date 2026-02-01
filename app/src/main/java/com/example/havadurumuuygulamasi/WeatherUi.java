package com.example.havadurumuuygulamasi;

public class WeatherUi {

    public static class WeatherVisual {
        public final int iconResId;
        public final String label;

        public WeatherVisual(int iconResId, String label) {
            this.iconResId = iconResId;
            this.label = label;
        }
    }

    public static WeatherVisual fromWeatherCode(int code) {

        // Open-Meteo weather_code (basic mapping)
        if (code == 0)
            return new WeatherVisual(R.drawable.baseline_wb_sunny_24, "Güneşli");

        if (code >= 1 && code <= 3)
            return new WeatherVisual(R.drawable.baseline_wb_cloudy_24, "Parçalı bulutlu");

        if (code == 45 || code == 48)
            return new WeatherVisual(R.drawable.baseline_wb_cloudy_24, "Sisli");

        // Yağmur
        if ((code >= 51 && code <= 57) || (code >= 61 && code <= 67) || (code >= 80 && code <= 82))
            return new WeatherVisual(R.drawable.baseline_umbrella_24, "Yağmurlu");

        // Kar
        if (code >= 71 && code <= 77)
            return new WeatherVisual(R.drawable.baseline_ac_unit_24, "Karlı");

        // Fırtına
        if (code >= 95 && code <= 99)
            return new WeatherVisual(R.drawable.baseline_flash_on_24, "Gök gürültülü");

        return new WeatherVisual(R.drawable.baseline_wb_cloudy_24, "Hava durumu");
    }
}
