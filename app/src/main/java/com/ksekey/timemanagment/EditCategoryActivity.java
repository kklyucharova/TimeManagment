package com.ksekey.timemanagment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.ksekey.timemanagment.entitiies.Category;
import com.ksekey.timemanagment.entitiies.Store;

import java.util.Arrays;

public class EditCategoryActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "id";

    private AppExecutors executors = new AppExecutors();
    private EditText name;
    private ImageView icon;
    private AppCompatSpinner spinner;

    private Store store;

    private int id;
    private Category category;

    protected String[] iconList = {"Иконка 1", "Иконка 2", "Иконка 3", "Иконка 4", "Иконка 5"};
    protected int[] iconIds = {R.drawable.ic_work, R.drawable.ic_delete, R.drawable.ic_dinner, R.drawable.ic_sleep, R.drawable.ic_rest};
    protected ArrayAdapter<String> iconArrayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        store = Store.getInstance(getApplicationContext());

        name = findViewById(R.id.category_name);
        icon = findViewById(R.id.category_icon);

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = EditCategoryActivity.this.name.getText().toString();
                executors.background().execute(new Runnable() {
                    @Override
                    public void run() {
                        category.setName(name);
                        store.save(category);
                        finish();
                    }
                });
            }
        });

        spinner = findViewById(R.id.icon_spinner);
        iconArrayAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, R.id.title_spinner, iconList);

        spinner.setAdapter(iconArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category.setName(iconList[position]);
                category.setIcon(iconIds[position]);
                icon.setImageResource(iconIds[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        id = getIntent().getIntExtra(EXTRA_ID, -1);
        loadCategory(id);
    }

    private void loadCategory(final int id) {
        executors.background().execute(new Runnable() {
            @Override
            public void run() {
                if (id != -1) {
                    category = store.getCategoryById(id);
                } else {
                    category = new Category();
                    category.setIcon(iconIds[0]);
                }
                executors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        fill(category);
                    }
                });
            }
        });
    }

    private void fill(Category category) {
        name.setText(category.getName());
        for (int i = 0; i < iconIds.length; i++) {
            if (iconIds[i] == category.getIcon()) {
                icon.setImageResource(category.getIcon());
                spinner.setSelection(i);
            }
        }
    }

}
