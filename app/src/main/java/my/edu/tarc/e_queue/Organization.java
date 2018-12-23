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
    public int ID, qNumber;
    public String name,address, phone,description;
    public double timePerCust;

    public Organization() {
    }

    public Organization(int ID, String name, String address, String phone, String description, int qNumber, double timePerCust) {
        this.ID = ID;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.qNumber = qNumber;
        this.timePerCust = timePerCust;
    }
}