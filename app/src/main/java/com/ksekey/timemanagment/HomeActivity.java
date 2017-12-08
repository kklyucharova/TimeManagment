package com.ksekey.timemanagment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksekey.timemanagment.entitiies.Record;
import com.ksekey.timemanagment.entitiies.Store;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recordList;

    private RecordAdapter adapter = new RecordAdapter();

    private Store store;

    private AppExecutors appExecutors = new AppExecutors();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        store = Store.getInstance(getApplicationContext());

        recordList = findViewById(R.id.record_list);
        recordList.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        loadRecords();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    private void loadRecords() {
        appExecutors.background().execute(new Runnable() {
            @Override
            public void run() {
                final List<Record> records = store.getRecords();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setRecords(records);
                    }
                });
            }
        });
    }

    class RecordAdapter extends RecyclerView.Adapter<ItemHolder> {

        private List<Record> records = new ArrayList<>(100);

        public void setRecords(List<Record> records) {
            this.records.clear();
            this.records.addAll(records);
            notifyDataSetChanged();
        }

        public RecordAdapter() {

        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_record, parent, false));
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            holder.bind(records.get(position));
        }

        @Override
        public int getItemCount() {
            return records.size();
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private TextView time;
        private ImageView icon;

        public ItemHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.item_text);
            time = itemView.findViewById(R.id.item_time);
            icon = itemView.findViewById(R.id.item_icon);
        }

        public void bind(Record record) {
            text.setText(record.getDescroption());
        }
    }
}
