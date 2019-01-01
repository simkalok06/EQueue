package my.edu.tarc.e_queue;

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

import static my.edu.tarc.e_queue.Home.trackQueue;

public class TrackYourQ extends AppCompatActivity {

    public  ListView listViewYourQ;
    private OrganizationAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_your_q);

        listViewYourQ = findViewById(R.id.listView2);
        adapter = new OrganizationAdapter();
        listViewYourQ.setAdapter(adapter);

        listViewYourQ.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //go to queue activity
                openQueueactivity(position);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listViewYourQ.setAdapter(adapter);
    }

    public void openQueueactivity(int position){
        Bundle queueExtras = new Bundle();
        queueExtras.putInt("index", position);
        Intent intent = new Intent(this, QueueActivity.class);
        intent.putExtras(queueExtras);
        startActivity(intent);
    }

    class OrganizationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Home.trackQueue.size();
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

            textViewName.setText(Home.trackQueue.elementAt(position).organization.name);
            textViewPhone.setText(Home.trackQueue.elementAt(position).organization.phone);
            textViewAddress.setText(Home.trackQueue.elementAt(position).organization.address);
            textViewDescription.setText(Home.trackQueue.elementAt(position).organization.description);
            imageView.setImageResource(Home.images[Home.trackQueue.elementAt(position).organization.ID]);

            return view;
        }
    }
}
