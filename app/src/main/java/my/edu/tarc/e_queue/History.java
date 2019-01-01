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

import static my.edu.tarc.e_queue.Home.historyList;

public class History extends AppCompatActivity {
    public ListView listViewHistory;
    private OrganizationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listViewHistory = findViewById(R.id.listViewHistory);
        adapter = new OrganizationAdapter();
        listViewHistory.setAdapter(adapter);

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
        queueExtras.putInt("ORGANIZATION_ID", historyList.elementAt(position).ID);
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
            view = getLayoutInflater().inflate(R.layout.outlet_record,null);

            TextView textViewName = view.findViewById(R.id.textViewNameData);
            TextView textViewPhone = view.findViewById(R.id.textViewPhoneData);
            TextView textViewAddress = view.findViewById(R.id.textViewAddressData);
            TextView textViewDescription = view.findViewById(R.id.textViewDescriptionData);
            ImageView imageView = view.findViewById(R.id.locationImageView);

            textViewName.setText(Home.historyList.elementAt(position).name);
            textViewPhone.setText(Home.historyList.elementAt(position).phone);
            textViewAddress.setText(Home.historyList.elementAt(position).address);
            textViewDescription.setText(Home.historyList.elementAt(position).description);
            imageView.setImageResource(Home.images[Home.historyList.elementAt(position).ID]);

            return view;
        }
    }
}
