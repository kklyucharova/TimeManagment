package com.ksekey.timemanagment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksekey.timemanagment.entitiies.Category;
import com.ksekey.timemanagment.entitiies.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ikvant.
 */

public class CategoriesFragment extends Fragment {
    private RecyclerView recordList;

    private CategoryAdapter adapter = new CategoryAdapter();

    private Store store;

    private AppExecutors appExecutors = new AppExecutors();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lists, container, false);
        store = Store.getInstance(getActivity().getApplicationContext());

        recordList = view.findViewById(R.id.items_list);
        recordList.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEditActivity(-1);
            }
        });

        MainActivity activity = (MainActivity) getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Categories");
        activity.setToolbar(toolbar);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecords();
    }

    private void loadRecords() {
        appExecutors.background().execute(new Runnable() {
            @Override
            public void run() {
                final List<Category> categories = store.getCategories();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setCategories(categories);
                    }
                });
            }
        });
    }

    private void startEditActivity(int id) {
        Intent intent = new Intent(getActivity(), EditCategoryActivity.class);
        intent.putExtra(EditCategoryActivity.EXTRA_ID, id);
        startActivity(intent);
    }

    class CategoryAdapter extends RecyclerView.Adapter<ItemHolder> {

        private List<Category> categories = new ArrayList<>(100);

        public void setCategories(List<Category> categories) {
            this.categories.clear();
            this.categories.addAll(categories);
            notifyDataSetChanged();
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false));
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            holder.bind(categories.get(position));
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private TextView time;
        private ImageView icon;

        public ItemHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.item_text);
            time = itemView.findViewById(R.id.item_meta);
            icon = itemView.findViewById(R.id.item_icon);
        }

        //заполняем содержимое списка
        public void bind(final Category category) {
            icon.setImageResource(category.getIcon());
            text.setText(category.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEditActivity((int) category.getId());
                }
            });
        }
    }
}
