package my.edu.tarc.e_queue;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

public class Organization {

    public Vector<String> name = new Vector <String>();
    public Vector<String> address = new Vector <String>();
    public Vector<String> phone = new Vector <String>();
    public Vector<String> description = new Vector <String>();
    public Vector<Integer> id = new Vector <Integer>();

    private String GET_URL = "https://bait2073equeue.000webhostapp.com/select_organization.php";

    public Organization() {

    }

    public void retrieveDataFromServer() {
        // retrieve data from server
        JsonArrayRequest jsonObjectRequest;
        jsonObjectRequest = new JsonArrayRequest(GET_URL + "?Id=",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject OrganizationDetail = (JSONObject) response.get(i);
                                id.add(OrganizationDetail.getInt("Id"));
                                name.add(OrganizationDetail.getString("Name"));
                                address.add(OrganizationDetail.getString("Address"));
                                phone.add(OrganizationDetail.getString("Phone"));
                                description.add(OrganizationDetail.getString("Description"));
                            }
                        } catch (Exception e) {
                            //Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
}