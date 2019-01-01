package my.edu.tarc.e_queue;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import static my.edu.tarc.e_queue.Home.favoriteList;
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

    // variables
    private String GET_URL = "https://bait2073equeue.000webhostapp.com/select_organization.php";
    private String GET_URL2 = "https://bait2073equeue.000webhostapp.com/update_queue.php";
    private ProgressDialog  progressDialog;
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

    public void openQueueActivity(View view) {
        TrackQueueData trackData = new TrackQueueData(organizationList.elementAt(organization_index), organizationList.elementAt(organization_index).qNumber+1);
        boolean validate = true;
        for(int i = 0; i < trackQueue.size();i++){
            if(trackQueue.elementAt(i).organization.ID == organization_index){
                Toast.makeText(getApplicationContext(), "You are already queuing for this!", Toast.LENGTH_SHORT).show();
                validate = false;
            }
        }
        if(validate){
            Toast.makeText(getApplicationContext(), "Queue successful.", Toast.LENGTH_SHORT).show();
            trackQueue.add(trackData);
            historyList.add(organizationList.elementAt(organization_index));

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
}
