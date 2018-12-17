package my.edu.tarc.e_queue;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    // layout items
    private Button registerButton;
    private EditText username, password, confirmPassword;
    private CheckBox termAndCondition;

    // variables
    private int registerStatus;

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
                registerStatus = register();

                // invalid registration
                if(registerStatus != 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);

                    builder.setCancelable(true);
                    builder.setTitle("Invalid Registration!");

                    if(registerStatus == 2)
                        builder.setMessage("Password and Confirm Password are not the same, please type again.");
                    else if(registerStatus == 1)
                        builder.setMessage("Blank information aren't allowed, please type again.");
                    else if(registerStatus == 3)
                        builder.setMessage("You have to agree with the Term & Condition before proceed to register.");

                    builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                }else{
                    Toast.makeText(Register.this,"Register Successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public int register(){
        if(password.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty() || username.getText().toString().isEmpty()){
            return 1; // blank info
        }else if(!password.getText().toString().equals(confirmPassword.getText().toString())) {
            return 2; // password mismatch
        }else if(!termAndCondition.isChecked()){
            return 3; // t&c is not checked
        }

        return 0; // register successful
    }

    public void openTermActivity(View view){
        Intent intent = new Intent(this, TermAndCondition.class);
        startActivity(intent);
    }
}
