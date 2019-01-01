package my.edu.tarc.e_queue;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.Vector;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // layout items
    private TextView TextViewUsername;
    private NavigationView navigationView;
    private View navHeaderView;
    public static ListView listViewOrganization;
    private OrganizationAdapter adapter;

    // variables
    public static String finalUsername;
    private String GET_URL = "https://bait2073equeue.000webhostapp.com/select_organization.php";
    private ProgressDialog  progressDialog;
    public static Vector<Organization> organizationList = new Vector<Organization>();
    public static Vector<TrackQueueData> trackQueue = new Vector<TrackQueueData>();
    public static int[] images = {R.drawable.dentist, R.drawable.massage, R.drawable.bank};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // custom stuffs
        navigationView = findViewById(R.id.nav_view);
        navHeaderView =  navigationView.getHeaderView(0);
        TextViewUsername = navHeaderView.findViewById(R.id.textViewUsername);
        TextViewUsername.setText(finalUsername);
        listViewOrganization = findViewById(R.id.listView);
        adapter = new OrganizationAdapter();

        listViewOrganization.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //go to organization activity
                openOrganizationActivity(position);
            }
        });

        retrieveDataFromServer();

        // default method
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    public void openOrganizationActivity(int position){
        //get the item that user selected
        //int test = listViewOrganization.getSelectedItemPosition();
        //a bundle object(like a struct) to store info to send to another activity
        Bundle organizationExtras = new Bundle();
        organizationExtras.putInt("ORGANIZATION_ID",position);
        Intent intent = new Intent(this, OrganizationActivity.class);
        intent.putExtras(organizationExtras);
        startActivity(intent);

        //update queue number
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_trackQueue) {
            Intent intent = new Intent(this, TrackYourQ.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_favorite) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_aboutUs) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout(View view){
        organizationList.clear();
        finish();
    }

    public void retrieveDataFromServer() {
        organizationList.clear();
        progressDialog = new ProgressDialog(Home.this);
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

                            listViewOrganization.setAdapter(adapter);

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

    class OrganizationAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return organizationList.size();
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
            view = getLayoutInflater().inflate(R.layout.outlet_record,null);

            TextView textViewName = view.findViewById(R.id.textViewNameData);
            TextView textViewPhone = view.findViewById(R.id.textViewPhoneData);
            TextView textViewAddress = view.findViewById(R.id.textViewAddressData);
            TextView textViewDescription = view.findViewById(R.id.textViewDescriptionData);
            ImageView imageView = view.findViewById(R.id.locationImageView);

            textViewName.setText(organizationList.elementAt(position).name);
            textViewPhone.setText(organizationList.elementAt(position).phone);
            textViewAddress.setText(organizationList.elementAt(position).address);
            textViewDescription.setText(organizationList.elementAt(position).description);
            imageView.setImageResource(images[position]);

            return view;
        }
    }
}
