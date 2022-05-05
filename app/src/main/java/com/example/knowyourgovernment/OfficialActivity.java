package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ActivityNotFoundException;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private static final String DEMOCRATIC_URL = "https://democrats.org";
    private static final String REPUBLICAN_URL = "https://www.gop.com";
    private String facebookID;
    private String twitterID;
    private String youtubeID;
    private Officials tempOfficialsObj;
    private Picasso picasso;
    private ConstraintLayout constraintLayout;
    private TextView address;
    private TextView phone;
    private TextView website;
    private TextView email;
    private ImageView officialPhotoView;
    private ImageView partyIcon;
    private TextView officialLocation;
    private TextView officialOffice;
    private TextView officialName;
    private TextView officialParty;
    private String imageURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        partyIcon = findViewById(R.id.partyIcon);
        officialOffice = findViewById(R.id.officialOffice);
        officialName = findViewById(R.id.officialName);
        officialParty = findViewById(R.id.officialParty);
        officialLocation = findViewById(R.id.officialLocation);
        officialPhotoView = findViewById(R.id.officialPhotoView);
        constraintLayout = findViewById(R.id.ConstraintLayout);
        picasso = Picasso.get();
        address = findViewById(R.id.addressLine);
        phone = findViewById(R.id.phoneLine);
        website = findViewById(R.id.websiteLine);
        email = findViewById(R.id.emailLine);

        picasso.setLoggingEnabled(true);
        Intent intent = getIntent();

        if(intent.hasExtra("OFFICIAL_DATA")) {
            tempOfficialsObj = (Officials) intent.getSerializableExtra("OFFICIAL_DATA");
            if(tempOfficialsObj != null) {
                if(tempOfficialsObj.getPhotoURL() != null){
                    imageURL = tempOfficialsObj.getPhotoURL();
                }
                else{
                    imageURL = "https://images-assets.nasa.gov/image/6900952/does_not_exist.jpg";
                }
            }
        }

        if(intent.hasExtra(Intent.EXTRA_TEXT)) {
            String location = intent.getStringExtra(Intent.EXTRA_TEXT);
            officialLocation.setText(location);
        }

        if(tempOfficialsObj.getPhoneNum() != null) {
            phone.setText(tempOfficialsObj.getPhoneNum());
        }
        else {
            TextView phoneTitle = findViewById(R.id.phoneTitle);
            phoneTitle.setVisibility(TextView.GONE);
            phone.setVisibility(TextView.GONE);
        }

        if(tempOfficialsObj.getWebURL() != null) {
            website.setText(tempOfficialsObj.getWebURL());
        }
        else
        {
            TextView webTitle = findViewById(R.id.websiteTitle);
            webTitle.setVisibility(TextView.GONE);
            website.setVisibility(TextView.GONE);
        }

        if(tempOfficialsObj.getAddress() != null) {
            address.setText(tempOfficialsObj.getAddress());
        }
        else {
            TextView addressTitle = findViewById(R.id.addressTitle);
            addressTitle.setVisibility(TextView.GONE);
            address.setVisibility(TextView.GONE);
        }

        if(tempOfficialsObj.getEmailID() != null) {
            email.setText(tempOfficialsObj.getEmailID());
        }
        else {
            TextView emailTitle = findViewById(R.id.emailTitle);
            emailTitle.setVisibility(TextView.GONE);
            email.setVisibility(TextView.GONE);
        }

        if(tempOfficialsObj.getFacebookID() != null) {
            facebookID = tempOfficialsObj.getFacebookID();
        }
        else {
            ImageView fbIcon = findViewById(R.id.facebook);
            fbIcon.setVisibility(ImageView.GONE);
        }

        if(tempOfficialsObj.getTwitterID() != null){
            twitterID = tempOfficialsObj.getTwitterID();
        }
        else {
            ImageView tIcon = findViewById(R.id.twitter);
            tIcon.setVisibility(ImageView.GONE);
        }

        if(tempOfficialsObj.getYoutubeID() != null){
            youtubeID = tempOfficialsObj.getYoutubeID();
        }
        else {
            ImageView yIcon = findViewById(R.id.youtube);
            yIcon.setVisibility(ImageView.GONE);
        }

        officialOffice.setText(tempOfficialsObj.getOffice());
        officialName.setText(tempOfficialsObj.getName());

        // set the party icon
        if(tempOfficialsObj.getParty() != null){
            officialParty.setText("(" + tempOfficialsObj.getParty() + ")");

            if(tempOfficialsObj.getParty().contains("Democratic")) {

                constraintLayout.setBackgroundColor(Color.BLUE);
                partyIcon.setImageResource(R.drawable.dem_logo);
            }
            else if (tempOfficialsObj.getParty().contains("Republican")) {

                constraintLayout.setBackgroundColor(Color.RED);
                partyIcon.setImageResource(R.drawable.rep_logo);
            }
            else {
                constraintLayout.setBackgroundColor(Color.BLACK);
                partyIcon.setVisibility(ImageView.GONE);
            }
        }

        Linkify.addLinks(address, Linkify.ALL);
        Linkify.addLinks(website, Linkify.ALL);
        Linkify.addLinks(phone, Linkify.ALL);
        Linkify.addLinks(email, Linkify.ALL);

        //set image using picasso
        if (!hasNetworkConnection()) {
            officialPhotoView.setImageResource(R.drawable.brokenimage);
            return;
        }
        if (tempOfficialsObj.getPhotoURL() != null){
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                    final String changedUrl = tempOfficialsObj.getPhotoURL().replace("http:", "https:");
                    picasso.load(changedUrl) .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder) .into(officialPhotoView);
                }
            }).build();

            picasso.load(tempOfficialsObj.getPhotoURL()) .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder) .into(officialPhotoView);
        } else {
            Picasso.get().load(tempOfficialsObj.getPhotoURL()) .error(R.drawable.brokenimage).placeholder(R.drawable.missing) .into(officialPhotoView);
        }

    }

    public void onClicked(View view) {
        Intent intent = null;
        if(tempOfficialsObj.getParty().contains("Democratic")){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DEMOCRATIC_URL));
        }
        else if(tempOfficialsObj.getParty().contains("Republican")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(REPUBLICAN_URL));
        }
        startActivity(intent);
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onClickedFacebook(View view) {
        String FACEBOOK_URL = "https://www.facebook.com/" + facebookID;
        Intent intent;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            getPackageManager().getPackageInfo("com.facebook.katana",0);
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if(versionCode >= 3002850) {
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            }
            else
            {
                urlToUse = "fb://page/" + facebookID;
            }
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
        }
        catch (Exception e){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
        }
        startActivity(intent);
    }

    public void onClickedYoutube(View view) {
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + youtubeID));
            startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + youtubeID)));
        }
    }

    public void onClickedTwitter(View view) {
        String twitterAppUrl = "twitter://user?screen_name=" + twitterID;
        String twitterWebUrl = "https://twitter.com/" + twitterID;
        Intent intent;
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
        }
        catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
        }
        startActivity(intent);
    }


    public void officialPhotoClicked(View view) {
        if(tempOfficialsObj.getPhotoURL() != null) {

            Intent intent = new Intent(this, PhotoActivity.class);
            String location = officialLocation.getText().toString();
            String party = officialParty.getText().toString();
            intent.putExtra("OFFICIAL", tempOfficialsObj);
            intent.putExtra("LOCATION", location);
            startActivity(intent);
        }
    }

}