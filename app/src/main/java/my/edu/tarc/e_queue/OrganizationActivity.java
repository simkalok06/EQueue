package my.edu.tarc.e_queue;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import static my.edu.tarc.e_queue.Home.listViewOrganization;
import static my.edu.tarc.e_queue.Home.organizationList;

public class OrganizationActivity extends AppCompatActivity {

    // layout items
    private TextView TextViewOrganizationName;
    private TextView TextViewOrganizationPhone;
    private TextView TextViewOrganizationDescription;
    private TextView TextViewOrganizationAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization);

        TextViewOrganizationName = findViewById(R.id.textViewOrganizationName);
        TextViewOrganizationPhone = findViewById(R.id.textViewOrganizationPhone);
        TextViewOrganizationDescription = findViewById(R.id.textViewOrganizationDescription);
        TextViewOrganizationAddress = findViewById(R.id.textViewOrganizationAddress);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        //show appropriate organization info based on which item user pressed
        Integer organization_index = extras.getInt("ORGANIZATION_ID");
        TextViewOrganizationName.setText(organizationList.elementAt(organization_index).name);
        TextViewOrganizationPhone.setText(organizationList.elementAt(organization_index).phone);
        TextViewOrganizationDescription.setText(organizationList.elementAt(organization_index).description);
        TextViewOrganizationAddress.setText(organizationList.elementAt(organization_index).address);

    }

}
