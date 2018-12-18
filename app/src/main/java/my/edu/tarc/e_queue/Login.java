package my.edu.tarc.e_queue;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    // layout items
    private Button loginButton;
    private EditText username, password;

    // variables
    private String adminUsername = "Shorino";
    private String adminPassword = "qwertyuiop";
    private String usernameServer, passwordServer;
    private int loginStatus;
    private String GET_URL = "https://bait2073equeue.000webhostapp.com/select_account_where.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        loginButton = findViewById(R.id.buttonLogin);
        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // retrieve data from server
                JsonArrayRequest jsonObjectRequest;
                jsonObjectRequest = new JsonArrayRequest(GET_URL+"?Username="+username.getText().toString(),
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject accountDetail = (JSONObject) response.get(i);
                                usernameServer = accountDetail.getString("Username");
                                passwordServer = accountDetail.getString("Password");
                            }

                            loginStatus = loginValidation();

                            if(loginStatus != 0){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

                                builder.setCancelable(true);
                                builder.setTitle("Invalid Login!");
                                if(loginStatus == 1)
                                    builder.setMessage("Blank information aren't allowed, please type again.");
                                else
                                    builder.setMessage("Wrong Username and Password.");

                                builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                    });
                                builder.show();
                            }else{
                                // display login successful message to user
                                Toast.makeText(Login.this,"Login Successful!", Toast.LENGTH_SHORT).show();
                            }
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
        });

        // create a network connection from app to website
        NetworkCalls.getInstance().setContext(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openRegisterActivity(View view){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public int loginValidation(){
        if(username.getText().toString().equals(adminUsername) && password.getText().toString().equals(adminPassword)){
            return 0; // login successful
        } else if(username.getText().toString().equals(usernameServer) && password.getText().toString().equals(passwordServer)){
            return 0; // login successful
        } else if(username.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
            return 1; // blank info
        }

        return 2; //wrong info
    }
}
