package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private static final String REPUBLIC_URL = "https://www.gop.com";
    private static final String DEMOCRATIC_URL = "https://democrats.org";
    private ImageView officialPhoto;
    private ImageView partyIcon;
    private Officials officialObj;
    private TextView location;
    private TextView designation;
    private TextView name;
    private String party;
    private String imageURL;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        officialPhoto = findViewById(R.id.photoImageView);
        partyIcon = findViewById(R.id.partyIcon1);
        location = findViewById(R.id.Location);
        constraintLayout = findViewById(R.id.ConstraintLayout);
        designation = findViewById(R.id.designation);
        name = findViewById(R.id.Name);

        Intent intent = getIntent();

        if(intent.hasExtra("OFFICIAL")) {
            officialObj = (Officials) intent.getSerializableExtra("OFFICIAL");
        }
        party = officialObj.getParty();
        imageURL = officialObj.getPhotoURL();

        designation.setText(officialObj.getOffice());
        name.setText(officialObj.getName());


        if(intent.hasExtra("LOCATION")) {
            String location1 = intent.getStringExtra("LOCATION");
            location.setText(location1);
        }

        // setting the party icon
        if (party.contains("Democratic")) {
            partyIcon.setImageResource(R.drawable.dem_logo);
            constraintLayout.setBackgroundColor(Color.BLUE);
        }
        else if(party.contains("Republican")){
            partyIcon.setImageResource(R.drawable.rep_logo);
            constraintLayout.setBackgroundColor(Color.RED);
        }
        else{
            partyIcon.setVisibility(ImageView.GONE);
            constraintLayout.setBackgroundColor(Color.BLACK);
        }

        // setting the image using picasso
        if (!hasNetworkConnection()) {
            officialPhoto.setImageResource(R.drawable.brokenimage);
            return;
        }
        if (officialObj.getPhotoURL() != null){
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                    final String changedUrl = officialObj.getPhotoURL().replace("http:", "https:");
                    picasso.load(changedUrl) .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder) .into(officialPhoto);
                }
            }).build();

            picasso.load(officialObj.getPhotoURL()) .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder) .into(officialPhoto);
        } else {
            Picasso.get().load(officialObj.getPhotoURL()) .error(R.drawable.brokenimage).placeholder(R.drawable.missing) .into(officialPhoto);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public void onClicked(View view){
        Intent intent = null;
        if(officialObj.getParty().contains("Democratic")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DEMOCRATIC_URL));
        }
        else if(officialObj.getParty().contains("Republican")){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(REPUBLIC_URL));
        }
        startActivity(intent);
    }

}