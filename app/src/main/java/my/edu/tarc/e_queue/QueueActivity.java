package my.edu.tarc.e_queue;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import static my.edu.tarc.e_queue.Home.organizationList;
import static my.edu.tarc.e_queue.Home.trackQueue;

public class QueueActivity extends AppCompatActivity {
    // layout items
    private TextView TextViewOrganizationName2;
    private TextView TextViewWaitTime2;
    private TextView TextViewQueued2;
    private TextView TextViewQPosition;
    private ImageView qrCode;

    // variables
    private String GET_URL = "https://bait2073equeue.000webhostapp.com/select_organization.php";
    private String GET_URL2 = "https://bait2073equeue.000webhostapp.com/update_queue.php";
    private ProgressDialog  progressDialog;
    int queueIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        TextViewOrganizationName2 = findViewById(R.id.textViewOrganizationName2);
        TextViewWaitTime2 = findViewById(R.id.textViewWaitTime2);
        TextViewQueued2 = findViewById(R.id.textViewQueued2);
        TextViewQPosition = findViewById(R.id.textViewQPosition);
        qrCode = findViewById(R.id.imageViewQR);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        queueIndex = extras.getInt("index");

        TextViewOrganizationName2.setText(trackQueue.elementAt(queueIndex).organization.name);
        TextViewQueued2.setText(Integer.toString(trackQueue.elementAt(queueIndex).organization.qNumber));
        TextViewWaitTime2.setText(calculateWaitTime(trackQueue.elementAt(queueIndex).organization.ID));
        TextViewQPosition.setText(Integer.toString(trackQueue.elementAt(queueIndex).myQNumber));
        qrCode.setImageResource(R.drawable.qr);
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

                            // update number of people queuing and wait time to latest
                            TextViewWaitTime2.setText(calculateWaitTime(trackQueue.elementAt(queueIndex).organization.ID));
                            TextViewQueued2.setText(Integer.toString(organizationList.elementAt(trackQueue.elementAt(queueIndex).organization.ID).qNumber));

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
        organizationList.elementAt(trackQueue.elementAt(queueIndex).organization.ID).qNumber--;
        updateQueueNumber(trackQueue.elementAt(queueIndex).organization.ID);
        for(int i = 0; i<trackQueue.size();i++){
            if(trackQueue.elementAt(i).organization.ID == trackQueue.elementAt(queueIndex).organization.ID){
                trackQueue.remove(i);
            }
        }
        finish();
    }
}