package com.example.knowyourgovernment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    private static String locationString = "Unspecified Location";
    private RecyclerView recyclerView;
    private OfficialAdapter officialAdapter;
    private Officials officials;
    private final List<Officials> officialList = new ArrayList<>();
    private String officialLoc;
    private TextView warning,textView;
    public String zipCode;
    private int pos;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        officialAdapter = new OfficialAdapter(officialList, this);
        recyclerView.setAdapter(officialAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        textView=findViewById(R.id.location);
        warning=findViewById(R.id.warning);
        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        determineLocation();

        if(!hasNetworkConnection()){
            warningShow("No Network Connection " +
                    "Data cannot be accessed/loaded without an internet connection.");
            textView.setText("No data for Location");
        }

    }

    public void warningShow(String update){
        warning.setVisibility(View.VISIBLE);
        warning.setText(update);
    }

    public void warningClose(){
        warning.setVisibility(View.INVISIBLE);
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        else if(menuItem.getItemId() == R.id.search) {
            if(hasNetworkConnection()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setGravity(Gravity.CENTER_HORIZONTAL);
                builder.setView(et);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setLocation(et.getText().toString());
                    }
                });
                builder.setNegativeButton("CANCEL", null);
                builder.setTitle(" Enter Address");

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }
   /* private void setManualLocation() {
         Check for network Connection
        if(!doNetworkCheck()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot be accessed/loaded  without a network connection");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }*/

    @Override
    public void onClick(View view) {
        pos = recyclerView.getChildLayoutPosition(view);
        officials = officialList.get(pos);
        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("OFFICIAL_DATA", officials);
        intent.putExtra(Intent.EXTRA_TEXT, officialLoc);
        startActivity(intent);
    }

    public void addOfficialData(Officials newOfficials) {
        officialList.add(newOfficials);
        officialAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                    return;
                }
            }
        }
        textView.setText("NO Data");
    }

    private void determineLocation() {
        if(checkMyLocPermission()) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    locationString = getPlace(location);
                    officialLoc = locationString;
                    textView.setText(officialLoc);
                    UpdateData updateData = new UpdateData(this,officialLoc);
                    new Thread(updateData).start();
                }
            }).addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                    e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    public String getPlace(Location loc) {
        StringBuilder sb = new StringBuilder();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String subThroughFare= addresses.get(0).getSubThoroughfare();
            String throughFare= addresses.get(0).getThoroughfare();
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String postalCode = addresses.get(0).getPostalCode();
            zipCode = postalCode;
            sb.append(String.format(
                    Locale.getDefault(),
                    "%s, %s, %s, %s, %s",
                    subThroughFare, throughFare, city, state, postalCode));
        } catch (IOException e) {
            sb.append("No address found");
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void setLocationText(String location){
        textView.setText(location);
    }

    public void setLocation(String input) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try{
            if(input.trim().isEmpty()) {
                Toast.makeText(this, "Enter City, State or Zipcode", Toast.LENGTH_LONG).show();
                return;
            }
            addresses = geocoder.getFromLocationName(input, 5);
            displayAddress(addresses);
        }
        catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        UpdateData updateData = new UpdateData(this, input);
        officialList.clear();
        new Thread(updateData).start();
    }

    private boolean checkMyLocPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }

    private void displayAddress(List<Address> addresses) {
        if(addresses.size() == 0){
            ((TextView) findViewById(R.id.location)).setText("No Data For Location");
            return;
        }
        Address address = addresses.get(0);

        String subThroughFare= address.getSubThoroughfare() == null ? "" : address.getSubThoroughfare();
        String throughFare=address.getThoroughfare() == null ? "" : address.getThoroughfare();
        String city = address.getLocality() == null ? "" : address.getLocality();
        String postalCode = address.getPostalCode() == null ? "" : address.getPostalCode();
        String state = address.getAdminArea() == null ? "" : address.getAdminArea();
        officialLoc = subThroughFare +" " + throughFare + " " + city + ", " + state + " " + postalCode;
        zipCode = postalCode;
        textView.setText(subThroughFare +" " + throughFare  +" "+ city + " " + state + " " + zipCode);
    }

}