package com.example.knowyourgovernment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    private TextView appTitle;
    private static final String DATA_URL = "https://developers.google.com/civic-information/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_about);

        appTitle=findViewById(R.id.appTitle);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onClicked(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DATA_URL));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No App Found");
            builder.setMessage("No Application found that handles ACTION_VIEW (https) intents");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

}