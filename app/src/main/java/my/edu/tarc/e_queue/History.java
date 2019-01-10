package my.edu.tarc.e_queue;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import static my.edu.tarc.e_queue.Home.finalUsername;
import static my.edu.tarc.e_queue.Home.historyList;
import static my.edu.tarc.e_queue.Home.organizationList;

public class History extends AppCompatActivity {
    public ListView listViewHistory;
    private OrganizationAdapter adapter;
    private ProgressDialog  progressDialog;
    private String GET_URL = "https://bait2073equeue.000webhostapp.com/select_history.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listViewHistory = findViewById(R.id.listViewHistory);
        adapter = new OrganizationAdapter();
        listViewHistory.setAdapter(adapter);

        retrieveDataFromServer();

        listViewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //go to queue activity
                openOrganizationactivity(position);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listViewHistory.setAdapter(adapter);
    }

    public void openOrganizationactivity(int position){
        Bundle queueExtras = new Bundle();
        queueExtras.putInt("ORGANIZATION_ID", organizationList.elementAt(historyList.elementAt(position).organizationID).ID);
        Intent intent = new Intent(this, OrganizationActivity.class);
        intent.putExtras(queueExtras);
        startActivity(intent);
    }

    class OrganizationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Home.historyList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.history_record,null);

            TextView textViewName = view.findViewById(R.id.textViewNameDataH);
            TextView textViewPhone = view.findViewById(R.id.textViewPhoneDataH);
            TextView textViewAddress = view.findViewById(R.id.textViewAddressDataH);
            TextView textViewBookingTime = view.findViewById(R.id.textViewTimeDataH);
            ImageView imageView = view.findViewById(R.id.imageView3);

            textViewName.setText(organizationList.elementAt(Home.historyList.elementAt(position).organizationID).name);
            textViewPhone.setText(organizationList.elementAt(Home.historyList.elementAt(position).organizationID).phone);
            textViewAddress.setText(organizationList.elementAt(Home.historyList.elementAt(position).organizationID).address);
            textViewBookingTime.setText(Home.historyList.elementAt(position).qTime);
            imageView.setImageResource(Home.images[organizationList.elementAt(Home.historyList.elementAt(position).organizationID).ID]);

            return view;
        }
    }

    public void retrieveDataFromServer() {
        historyList.clear();
        progressDialog = new ProgressDialog(History.this);
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
                                JSONObject HistoryDetail = (JSONObject) response.get(i);
                                HistoryData serverData = new HistoryData(
                                        Integer.parseInt(HistoryDetail.getString("OrganisationID")),
                                        HistoryDetail.getString("QueueTime"),
                                        HistoryDetail.getString("AccountID"));

                                if(serverData.accountID.equals(finalUsername))
                                    historyList.add(serverData);
                            }
                            progressDialog.dismiss();

                            listViewHistory.setAdapter(adapter);

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
}
