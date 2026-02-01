package com.example.havadurumuuygulamasi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    // Veritabanı dosyasının adı ve versiyonu
    private static final String DB_NAME = "meteo_v2.db";
    private static final int DB_VERSION = 3;

    // Tablo ve sütun isimleri
    private static final String TABLE = "favorites";
    private static final String COL_CITY = "city";

    // Kurucu metod: Veritabanı dosyasını hazırlıyor
    public Database(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE + " (" +
                        COL_CITY + " TEXT PRIMARY KEY" + // Şehir ismi anahtar olacak (aynısından iki tane olamaz)
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // Favori ekle
    public void addFavorite(String city) {
        if (city == null) return;
        city = city.trim();
        if (city.isEmpty()) return;

        SQLiteDatabase db = getWritableDatabase();

        // Verileri paketliyoruz (Sütun adı ve eklenecek veri)
        ContentValues cv = new ContentValues();
        cv.put(COL_CITY, city);

        // Şehir zaten varsa hata vermemesi için CONFLICT_IGNORE
        db.insertWithOnConflict(TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);

        db.close();
    }

    // Favori sil
    public void removeFavorite(String city) {
        if (city == null) return;
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, COL_CITY + "=?", new String[]{city});
        db.close();
    }

    // Favori mi kontrol et
    public boolean isFavorite(String city) {
        if (city == null) return false;

        SQLiteDatabase db = getReadableDatabase();

        // Sorgu çalıştırıyoruz: "Bu şehir listede var mı?"
        Cursor c = db.rawQuery(
                "SELECT 1 FROM " + TABLE + " WHERE " + COL_CITY + "=? LIMIT 1",
                new String[]{city}
        );

        // Eğer sonuç listesi boş değilse (ilk satıra gidebiliyorsa) şehir favoridir
        boolean exists = c.moveToFirst();

        c.close();
        db.close();

        return exists;
    }

    // Tüm favorileri listele
    public ArrayList<String> getAllFavorites() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // Tüm şehirleri alfabetik sırayla getiren sorgu
        Cursor c = db.rawQuery(
                "SELECT " + COL_CITY + " FROM " + TABLE + " ORDER BY " + COL_CITY + " ASC",
                null
        );

        // Liste sonuna kadar her satırı tek tek dolaşıp listeye ekliyoruz
        while (c.moveToNext()) {
            list.add(c.getString(0));
        }

        c.close();
        db.close();

        return list;
    }

    // Favprilerde arama
    public ArrayList<String> searchFavorites(String keyword) {
        ArrayList<String> list = new ArrayList<>();

        if (keyword == null) keyword = "";
        keyword = keyword.trim();

        SQLiteDatabase db = getReadableDatabase();
        Cursor c;

        // Eğer arama kutusu boşsa tüm listeyi göster
        if (keyword.isEmpty()) {
            c = db.rawQuery(
                    "SELECT " + COL_CITY + " FROM " + TABLE +
                            " ORDER BY " + COL_CITY + " ASC",
                    null
            );
        } else {
            // Yazılan kelimeyi içeren şehirleri bul
            c = db.rawQuery(
                    "SELECT " + COL_CITY + " FROM " + TABLE +
                            " WHERE " + COL_CITY + " LIKE ?" +
                            " ORDER BY " + COL_CITY + " ASC",
                    new String[]{"%" + keyword + "%"}
            );
        }

        while (c.moveToNext()) {
            list.add(c.getString(0));
        }

        c.close();
        db.close();

        return list;
    }
}