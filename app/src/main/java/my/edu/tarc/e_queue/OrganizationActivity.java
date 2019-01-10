package my.edu.tarc.e_queue;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;

import static my.edu.tarc.e_queue.Home.favoriteList;
import static my.edu.tarc.e_queue.Home.finalUsername;
import static my.edu.tarc.e_queue.Home.historyList;
import static my.edu.tarc.e_queue.Home.listViewOrganization;
import static my.edu.tarc.e_queue.Home.organizationList;
import static my.edu.tarc.e_queue.Home.trackQueue;

public class OrganizationActivity extends AppCompatActivity {

    // layout items
    private TextView TextViewOrganizationName;
    private TextView TextViewOrganizationPhone;
    private TextView TextViewOrganizationDescription;
    private TextView TextViewOrganizationAddress;
    private TextView TextViewWaitTime;
    private TextView TextViewQueued;
    private ImageView imageView;
    private Button favoriteButton;
    private Button queueButton;

    // variables
    private String GET_URL = "https://bait2073equeue.000webhostapp.com/select_organization.php";
    private String GET_URL2 = "https://bait2073equeue.000webhostapp.com/update_queue.php";
    private String URL_SAVE = "https://bait2073equeue.000webhostapp.com/insert_history.php";
    private ProgressDialog  progressDialog;
    private Timestamp currentTime;
    private String organisationID;
    private String currentTimes;
    private boolean validate;
    int organization_index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization);

        TextViewOrganizationName = findViewById(R.id.textViewOrganizationName);
        TextViewOrganizationPhone = findViewById(R.id.textViewOrganizationPhone);
        TextViewOrganizationDescription = findViewById(R.id.textViewOrganizationDescription);
        TextViewOrganizationAddress = findViewById(R.id.textViewOrganizationAddress);
        TextViewWaitTime = findViewById(R.id.textViewWaitTime);
        TextViewQueued = findViewById(R.id.textViewQueued);
        imageView = findViewById(R.id.imageView2);
        favoriteButton = findViewById(R.id.buttonFavorite);
        queueButton = findViewById(R.id.buttonQueue);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        //show appropriate organization info based on which item user pressed
        organization_index = extras.getInt("ORGANIZATION_ID");

        //organizationList.elementAt(organization_index).qNumber++;
        //updateQueueNumber(organization_index);
        TextViewOrganizationName.setText(organizationList.elementAt(organization_index).name);
        TextViewOrganizationPhone.setText(organizationList.elementAt(organization_index).phone);
        TextViewOrganizationDescription.setText(organizationList.elementAt(organization_index).description);
        TextViewOrganizationAddress.setText(organizationList.elementAt(organization_index).address);
        TextViewWaitTime.setText(calculateWaitTime(organization_index));
        TextViewQueued.setText(Integer.toString(organizationList.elementAt(organization_index).qNumber));
        imageView.setImageResource(Home.images[organization_index]);

        updateFavoriteButton();

        queueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openQueueActivity();
                if(validate){
                    organisationID = Integer.toString(organization_index);
                    currentTime = new Timestamp(System.currentTimeMillis());
                    currentTimes = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime);


                    progressDialog = new ProgressDialog(OrganizationActivity.this);
                    progressDialog.setMessage("Loading..."); // Setting Message
                    progressDialog.setTitle("History writing"); // Setting Title
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                    progressDialog.show(); // Display Progress Dialog
                    progressDialog.setCancelable(false);


                    // write data into server
                    try {
                        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_SAVE, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                JSONObject jsonObject;
                                try {
                                    jsonObject = new JSONObject(response);
                                    int success = jsonObject.getInt("success");
                                    String message = jsonObject.getString("message");


                                    // stop the process dialog
                                    progressDialog.dismiss();

                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params;
                                params = new HashMap<>();
                                params.put("OrganisationID", organisationID);
                                params.put("QueueTime", currentTimes);
                                params.put("AccountID", finalUsername);
                                return params;
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("Content-Type", "application/x-www-form-urlencoded");
                                return params;
                            }


                        };
                        NetworkCalls.getInstance().addToRequestQueue(postRequest);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    public void updateFavoriteButton(){
        if(favoriteList.size() == 0){
            favoriteButton.setText("Add to Favorite");
        }
        for(int i =0;i<favoriteList.size();i++) {
            if (organization_index == favoriteList.elementAt(i).ID){
                favoriteButton.setText("Remove from Favorite");
                break;
            }else
                favoriteButton.setText("Add to Favorite");

        }
    }

    public String calculateWaitTime(int position){
        float waittime = organizationList.elementAt(position).qNumber * (float)organizationList.elementAt(position).timePerCust;
        int waitMin = (int)waittime;
        waittime = waittime - waitMin;
        float waitSecond = waittime * 60;

        return waitMin+" minute(s) "+(int)waitSecond+" second(s)";
    }

    public void refresh(){
        organizationList.clear();
        progressDialog = new ProgressDialog(OrganizationActivity.this);
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("Retrieving Data"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);

        // retrieve data from server
        JsonArrayRequest jsonObjectRequest;
        jsonObjectRequest = new JsonArrayRequest(GET_URL + "?Id=",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject OrganizationDetail = (JSONObject) response.get(i);
                                Organization serverData = new Organization(OrganizationDetail.getInt("Id"),
                                        OrganizationDetail.getString("Name"),
                                        OrganizationDetail.getString("Address"),
                                        OrganizationDetail.getString("Phone"),
                                        OrganizationDetail.getString("Description"),
                                        OrganizationDetail.getInt("QNumber"),
                                        OrganizationDetail.getDouble("TimePerCust"));

                                organizationList.add(serverData);
                            }
                            progressDialog.dismiss();

                            TextViewWaitTime.setText(calculateWaitTime(organization_index));
                            TextViewQueued.setText(Integer.toString(organizationList.elementAt(organization_index).qNumber));
                            //listViewOrganization.setAdapter(adapter);

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Error: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        // Add the request to the RequestQueue.
        NetworkCalls.getInstance().addToRequestQueue(jsonObjectRequest);


    }

    public void updateQueueNumber(int position){
        JsonArrayRequest jsonObjectRequest;
        jsonObjectRequest = new JsonArrayRequest(GET_URL2+"?QNumber="+organizationList.elementAt(position).qNumber + "&Id="+position,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(OrganizationActivity.this);

                            builder.setCancelable(true);
                            builder.setTitle("Queue Successful!");
                            builder.setMessage(" ");
                            builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            builder.show();

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Toast.makeText(getApplicationContext(), "Error: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        // Add the request to the RequestQueue.
        NetworkCalls.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void openQueueActivity() {
        TrackQueueData trackData = new TrackQueueData(organizationList.elementAt(organization_index), organizationList.elementAt(organization_index).qNumber+1);
        validate = true;
        for(int i = 0; i < trackQueue.size();i++){
            if(trackQueue.elementAt(i).organization.ID == organization_index){
                Toast.makeText(getApplicationContext(), "You are already queuing for this!", Toast.LENGTH_SHORT).show();
                validate = false;
            }
        }
        if(validate){
            Toast.makeText(getApplicationContext(), "Queue successful.", Toast.LENGTH_SHORT).show();
            trackQueue.add(trackData);
            //historyList.add(organizationList.elementAt(organization_index));

            // increment q number
            organizationList.elementAt(organization_index).qNumber++;
            updateQueueNumber(organization_index);


            // call queue activity
            Bundle queueExtras = new Bundle();
            queueExtras.putInt("index", trackQueue.size()-1);
            Intent intent2 = new Intent(this, QueueActivity.class);
            intent2.putExtras(queueExtras);
            startActivity(intent2);
        }
    }

    public void refreshButton(View view){
        refresh();
    }

    public void addOrRemoveFromFavoriteButton(View view){
        boolean isAdd = false;
        int falseAt = 0;
        if(favoriteList.size() == 0){
            isAdd = true;
        }else {
            int size = favoriteList.size();
            for (int i = 0; i < size; i++) {
                if (organization_index == favoriteList.elementAt(i).ID) {
                    isAdd = false;
                    falseAt = i;
                    break;
                } else {
                    isAdd = true;
                }
            }
        }

        if(isAdd){
            favoriteList.add(organizationList.elementAt(organization_index)); // add
            Toast.makeText(getApplicationContext(), "You have added this organization into your favorite list.", Toast.LENGTH_SHORT).show();
            favoriteButton.setText("Remove from Favorite");
        }else{
            favoriteList.remove(favoriteList.elementAt(falseAt)); // remove
            Toast.makeText(getApplicationContext(), "You have removed this organization into your favorite list.", Toast.LENGTH_SHORT).show();
            favoriteButton.setText("Add to Favorite");
        }
    }

    public void openMap(View view) {
        Uri gmmIntentUri;
        if(organization_index == 0)
        {
            gmmIntentUri = Uri.parse("geo:3.101257, 101.741692?q=" + Uri.encode("Klinik Yap & Looi"));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
        else if(organization_index == 1)
        {
            gmmIntentUri = Uri.parse("geo:1.342795, 103.776483?q=" + Uri.encode("Yong Kang Reflexology Centre"));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
        else if(organization_index == 2)
        {
            gmmIntentUri = Uri.parse("geo:3.092730, 101.740615?q=" + Uri.encode("CIMB Bank Taman Mutiara"));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
    }

    public void openPhone(View view){
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        // Set the data for the intent as the phone number.
        String phoneNum = Home.organizationList.elementAt(organization_index).phone.toString();

        dialIntent.setData(Uri.parse("tel:" + phoneNum));
        // If package resolves to an app, send intent.
        if (dialIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(dialIntent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Can't resolve app for ACTION_DIAL Intent.",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
