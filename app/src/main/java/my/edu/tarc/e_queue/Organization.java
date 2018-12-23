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

    public static Vector<String> name = new Vector <String>();
    public static Vector<String> address = new Vector <String>();
    public static Vector<String> phone = new Vector <String>();
    public static Vector<String> description = new Vector <String>();
    public static Vector<Integer> id = new Vector <Integer>();

    public Organization() {

    }
}