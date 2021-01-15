package com.example.sma;

import android.app.Application;

import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class Offline extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        //db.setFirestoreSettings(settings);


        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        //built.setIndicatorsEnabled(true);
        // built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
