package my.edu.tarc.e_queue;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Queue;

import static my.edu.tarc.e_queue.Home.listViewOrganization;
import static my.edu.tarc.e_queue.Home.organizationList;
import static my.edu.tarc.e_queue.Home.trackQueue;

public class QueueActivity extends AppCompatActivity {
    // layout items
    private TextView TextViewOrganizationName2;
    private TextView TextViewWaitTime2;
    private TextView TextViewQueued2;
    private TextView TextViewQPosition;

    // variables
    private String GET_URL = "https://bait2073equeue.000webhostapp.com/select_organization.php";
    private String GET_URL2 = "https://bait2073equeue.000webhostapp.com/update_queue.php";
    private ProgressDialog  progressDialog;
    int organization_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        TextViewOrganizationName2 = findViewById(R.id.textViewOrganizationName2);
        TextViewWaitTime2 = findViewById(R.id.textViewWaitTime2);
        TextViewQueued2 = findViewById(R.id.textViewQueued2);
        TextViewQPosition = findViewById(R.id.textViewQPosition);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        organization_index = extras.getInt("ORGANIZATION_ID");

        TextViewOrganizationName2.setText(organizationList.elementAt(organization_index).name);
        TextViewQueued2.setText(Integer.toString(organizationList.elementAt(organization_index).qNumber));
        TextViewWaitTime2.setText(calculateWaitTime(organization_index));
        TextViewQueued2.setText(Integer.toString(organizationList.elementAt(organization_index).qNumber));
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
        progressDialog = new ProgressDialog(QueueActivity.this);
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

                            TextViewWaitTime2.setText(calculateWaitTime(organization_index));
                            TextViewQueued2.setText(Integer.toString(organizationList.elementAt(organization_index).qNumber));
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(QueueActivity.this);

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

    public void refreshButton(View view){
        refresh();
    }

    public void cancelButton(View view){
        organizationList.elementAt(organization_index).qNumber--;
        updateQueueNumber(organization_index);
        for(int i = 0; i<trackQueue.size();i++){
            if(trackQueue.elementAt(i).position == organization_index){
                trackQueue.remove(i);
            }
        }
        finish();
    }
}