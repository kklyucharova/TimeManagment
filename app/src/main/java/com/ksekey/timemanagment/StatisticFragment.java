package com.ksekey.timemanagment;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.ksekey.timemanagment.entitiies.Category;
import com.ksekey.timemanagment.entitiies.MetaCategory;
import com.ksekey.timemanagment.entitiies.Store;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ikvant.
 */

public class StatisticFragment extends Fragment {
    private AppExecutors executors = new AppExecutors();
    private Store store;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private StatisticAdapter adapter = new StatisticAdapter();
    private RecyclerView statisticList;
    private Button dateStart;
    private Button dateEnd;
    private PieChart chart;


    private Date start = new Date();
    private Date end = new Date();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistic_fragment, container, false);

        store = Store.getInstance(getActivity());

        dateStart = view.findViewById(R.id.stat_start);
        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        start = (modifyDate(start, year, month, dayOfMonth));
                        reload();
                    }

                }, year, month, day).show();
            }
        });
        dateEnd = view.findViewById(R.id.stat_end);
        dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(end);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        end = (modifyDate(end, year, month, dayOfMonth));
                        reload();
                    }

                }, year, month, day).show();
            }
        });


        setupChart(view);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                view.findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_popular:
                                loadByType(1);
                                break;
                            case R.id.menu_time:
                                loadByType(2);
                                break;
                            case R.id.menu_usage:
                                loadByType(0);
                                break;
                            case R.id.menu_chart:
                                showChart();
                                break;
                        }
                        return true;
                    }
                });

        MainActivity activity = (MainActivity) getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Statistics");
        activity.setToolbar(toolbar);

        statisticList = view.findViewById(R.id.statistic_category);
        statisticList.setLayoutManager(new LinearLayoutManager(getActivity()));
        statisticList.setAdapter(adapter);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        dateStart.setText(dateFormat.format(start));
        dateEnd.setText(dateFormat.format(end));
        loadByType(0);
        loadDataForChart();
    }

    private Date modifyDate(Date date, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return date;
    }

    private void setupChart(View view) {
        chart = (PieChart) view.findViewById(R.id.chart);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(12f);
    }

    private void loadDataForChart() {
        executors.background().execute(new Runnable() {
            @Override
            public void run() {
                List<MetaCategory> metaCategories = store.loadCategoriesMax(start, end);
                List<Category> allCategories = store.getCategories();
                List<Category> categories = new ArrayList<>();
                for (MetaCategory category : metaCategories) {
                    categories.add(findCategoryById(allCategories, category.getCategoryId()));
                }
                setData(metaCategories, categories);
            }
        });
    }

    private void showChart() {
        chart.setVisibility(View.VISIBLE);
        statisticList.setVisibility(View.GONE);
    }

    private void setData(List<MetaCategory> metaCategories, List<Category> categories) {


        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        for (int i = 0; i < metaCategories.size(); i++) {
            entries.add(new PieEntry(metaCategories.get(i).getMeta(), categories.get(i).getName()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        final PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        executors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                chart.setData(data);

                // undo all highlights
                chart.highlightValues(null);

                chart.invalidate();
            }
        });
    }

    private void loadByType(final int type) {
        executors.background().execute(new Runnable() {
            @Override
            public void run() {
                List<Category> allCategories = store.getCategories();
                List<MetaCategory> metaCategories = new ArrayList<>();
                final List<String> metaStrings = new ArrayList<>();
                if (type == 0) {
                    metaCategories = store.loadCategoriesCount(start, end);
                } else if (type == 1) {
                    metaCategories = store.loadCategoriesSum(start, end);
                } else if (type == 2) {
                    metaCategories = store.loadCategoriesMax(start, end);
                }
                final List<Category> categories = new ArrayList<>();
                for (MetaCategory metaCategory : metaCategories) {
                    categories.add(findCategoryById(allCategories, metaCategory.getCategoryId()));
                    metaStrings.add(String.valueOf(metaCategory.getMeta()));
                }

                executors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        chart.setVisibility(View.GONE);
                        statisticList.setVisibility(View.VISIBLE);
                        adapter.setCategoryList(categories, metaStrings);
                    }
                });

            }
        });
    }

    private Category findCategoryById(List<Category> categories, int id) {
        for (Category category : categories) {
            if (category.getId() == id) {
                return category;
            }
        }
        return null;
    }

    private class StatisticAdapter extends RecyclerView.Adapter<ItemHolder> {

        private List<Category> categoryList = new ArrayList<>();
        private List<String> meta = new ArrayList<>();


        public void setCategoryList(List<Category> categoryList, List<String> meta) {
            this.categoryList = categoryList;
            this.meta = meta;
            notifyDataSetChanged();
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            holder.bind(categoryList.get(position), meta.get(position));
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private TextView meta;
        private ImageView icon;

        public ItemHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.item_text);
            meta = itemView.findViewById(R.id.item_meta);
            icon = itemView.findViewById(R.id.item_icon);
        }

        //заполняем содержимое списка
        public void bind(final Category category, String meta) {
            icon.setImageResource(category.getIcon());
            text.setText(category.getName());
            this.meta.setText(meta);
        }
    }


}
