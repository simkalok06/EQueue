package my.edu.tarc.e_queue;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    // layout items
    private Button registerButton;
    private EditText username, password, confirmPassword;
    private CheckBox termAndCondition;

    // variables
    private int registerStatus;
    private String URL_SAVE = "https://bait2073equeue.000webhostapp.com/insert_account.php";
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = findViewById(R.id.buttonRegister);
        username = findViewById(R.id.editTextUsername2);
        password = findViewById(R.id.editTextPassword2);
        confirmPassword = findViewById(R.id.editTextConfirmPassword);
        termAndCondition = findViewById(R.id.checkBoxTermAndCondition);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerStatus = registerValidation();

                // invalid registration
                if(registerStatus != 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

                    builder.setCancelable(true);
                    builder.setTitle("Invalid Registration!");

                    if(registerStatus == 2)
                        builder.setMessage("Password and Confirm Password are not the same.");
                    else if(registerStatus == 1)
                        builder.setMessage("Blank information aren't allowed.");
                    else if(registerStatus == 3)
                        builder.setMessage("You have to agree with the Term & Condition before proceed to register.");
                    else if(registerStatus == 4)
                        builder.setMessage("Password length must be 8 characters or above.");

                    builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) { dialogInterface.cancel();
                        }
                    });
                    builder.show();
                }else{
                    progressDialog = new ProgressDialog(Register.this);
                    progressDialog.setMessage("Loading..."); // Setting Message
                    progressDialog.setTitle("Registration"); // Setting Title
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

                                            if(success == 1) {
                                                // display register successful to user
                                                Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
                                                Home.finalUsername = username.getText().toString();
                                                openHomeActivity();
                                            }else{
                                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);

                                                builder.setCancelable(true);
                                                builder.setTitle("Invalid Registration!");
                                                builder.setMessage(message);

                                                builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) { dialogInterface.cancel();
                                                    }
                                                });
                                                builder.show();
                                            }

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
                                params.put("Username", username.getText().toString());
                                params.put("Password", password.getText().toString());
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
    private boolean hasSpace(String string){
        for(int i = 0; i < string.length(); i++){
            if(string.charAt(i) == ' ')
                return true;
        }
        return false;
    }
    public int registerValidation() {
        if (password.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty() || username.getText().toString().isEmpty()) {
            return 1; // blank info
        } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            return 2; // password mismatch
        } else if (!termAndCondition.isChecked()) {
            return 3; // t&c is not checked
        } else if (password.getText().toString().length() < 8){
            return 4; // password less than 8 chars
        }
        return 0; // register successful
    }
    public void openTermActivity(View view){
        Intent intent = new Intent(this, TermAndCondition.class);
        startActivity(intent);
    }
    public void openHomeActivity(){
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // TODO read shared preferences file
        sharedPreferences = getSharedPreferences(getString(R.string.pref_file), MODE_PRIVATE);

        String usernameEditText,passwordEditText,confirmedPasswordEditText;

        usernameEditText = sharedPreferences.getString("username", "");
        passwordEditText = sharedPreferences.getString("password", "");
        confirmedPasswordEditText = sharedPreferences.getString("confirmedPassword", "");

        username.setText(usernameEditText);
        password.setText(passwordEditText);
        confirmPassword.setText(confirmedPasswordEditText);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String usernameET, passwordET, confirmedPasswordET;

        usernameET = username.getText().toString();
        passwordET = password.getText().toString();
        confirmedPasswordET = confirmPassword.getText().toString();

        editor.putString("username",usernameET);
        editor.putString("password",passwordET);
        editor.putString("confirmedPassword", confirmedPasswordET);

        editor.apply();
    }


}
