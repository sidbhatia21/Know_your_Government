package com.example.knowyourgovernment;

import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class UpdateData implements Runnable{

    private static final String API_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyADKt3c9NBR6oveeqMBCpvQ9mwNw3qCj5M&address=";
    private MainActivity mainActivity;
    private final String location;

    public UpdateData(MainActivity mainActivity, String location) {
        this.mainActivity = mainActivity;
        this.location = location;
    }

    @Override
    public void run() {
        Uri.Builder uriBuilder = Uri.parse(API_URL + location).buildUpon();
        String urlToUse = uriBuilder.toString();

        RequestQueue queue = Volley.newRequestQueue(mainActivity);                                // creating a request queue

        Response.Listener<JSONObject> listener =                                                // creating success listener which will call parseJson
                response -> parseJSON(response.toString());

        Response.ErrorListener error = error1 -> {                                             // error listener
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(new String(error1.networkResponse.data));
                parseJSON(null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        JsonObjectRequest jsonObjectRequest =                                                  // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        queue.add(jsonObjectRequest);
    }

    private void parseJSON(String string){
        try
        {
            JSONObject officialsObj = new JSONObject(string);
            JSONObject normalizedInput = officialsObj.getJSONObject("normalizedInput");
            String city = normalizedInput.getString("city")+ ", "+normalizedInput.getString("state")+ ", "+normalizedInput.getString("zip");
            mainActivity.setLocationText(city);
            Officials newOfficials;

            JSONArray offices = officialsObj.getJSONArray("offices");
            JSONArray officials =  officialsObj.getJSONArray("officials");

            for(int i = 0; i < offices.length(); i++) {

                JSONObject office = (JSONObject) offices.get(i);
                final String officeName = office.getString("name");
                JSONArray officialIndices =  office.getJSONArray("officialIndices");

                for(int j = 0; j < officialIndices.length(); j++) {
                    int index = Integer.parseInt(officialIndices.get(j).toString());

                    JSONObject officialDetails = (JSONObject) officials.get(index);
                    final String officialName = officialDetails.getString("name");
                    newOfficials = new Officials(officeName, officialName);

                    if(officialDetails.has("address")) {
                        JSONArray address = officialDetails.getJSONArray("address");
                        JSONObject startAddress = (JSONObject) address.get(0);
                        String line1 = "";
                        String line2 = "";
                        String cityLine = "";
                        String stateLine = "";
                        String zipLine = "";
                        if(startAddress.has("line1")){
                            line1 = startAddress.getString("line1");
                        }
                        if(startAddress.has("line2")) {
                            line2 = startAddress.getString("line2");
                        }
                        if(startAddress.has("city")) {
                            cityLine = startAddress.getString("city");
                        }
                        if(startAddress.has("state")) {
                            stateLine = startAddress.getString("state");
                        }
                        if(startAddress.has("zip")) {
                            zipLine = startAddress.getString("zip");
                        }

                        String line1F = line1 + line2;
                        String line2F = cityLine + ", " + stateLine + " " + zipLine;
                        String completeAddress = line1F + ", " + line2F;
                        newOfficials.setAddress(completeAddress);
                    }

                    if(officialDetails.has("party")) {
                        String party = officialDetails.getString("party");
                        newOfficials.setParty(party);
                    }

                    if(officialDetails.has("phones")) {
                        JSONArray phoneArr = officialDetails.getJSONArray("phones");
                        String phoneNum = (String) phoneArr.get(0);
                        newOfficials.setPhoneNum(phoneNum);
                    }

                    if(officialDetails.has("urls")){
                        JSONArray urlArr = officialDetails.getJSONArray("urls");
                        String url = (String) urlArr.get(0);
                        newOfficials.setWebURL(url);
                    }


                    if(officialDetails.has("emails")){
                        JSONArray emailArr = officialDetails.getJSONArray("emails");
                        String email = (String) emailArr.get(0);
                        newOfficials.setEmailID(email);
                    }

                    if(officialDetails.has("photoUrl")){
                        String photoUrl = officialDetails.getString("photoUrl");
                        newOfficials.setPhotoURL(photoUrl);
                    }

                    if(officialDetails.has("channels")){
                        JSONArray channelArr = officialDetails.getJSONArray("channels");
                        for(int z = 0; z < channelArr.length(); z++){
                            JSONObject channel = (JSONObject) channelArr.get(z);
                            String channelType = channel.getString("type");
                            String channelId = channel.getString("id");
                            switch (channelType){
                                case "Facebook":
                                    newOfficials.setFacebookID(channelId);
                                    break;
                                case "Twitter":
                                    newOfficials.setTwitterID(channelId);
                                    break;
                                case "YouTube":
                                    newOfficials.setYoutubeID(channelId);
                                    break;
                                default:
                                    // error message

                            }
                        }
                    }

                    final Officials newOfficialsF = newOfficials;
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.addOfficialData(newOfficialsF);
                            String str = officeName + " " + officialName;

                        }
                    });
                }
            }

        }
        catch(Exception exception) {
            exception.printStackTrace();
        }
    }
}
