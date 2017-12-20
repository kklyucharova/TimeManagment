package com.ksekey.timemanagment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.ksekey.timemanagment.entitiies.Category;
import com.ksekey.timemanagment.entitiies.Photo;
import com.ksekey.timemanagment.entitiies.Record;
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

public class EditRecordActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "id";

    private static final int REQUEST_IMAGE_GET = 1;

    private AppExecutors executors = new AppExecutors();
    private EditText description;
    private AppCompatSpinner spinner;
    private Button date;
    private Button dateStart;
    private Button dateEnd;
    private Store store;

    private Record record;
    protected List<Category> categoryList = new ArrayList<>();
    protected ArrayAdapter<Category> categoryArrayAdapter;
    private PhotoAdapter photoAdapter = new PhotoAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);

        store = Store.getInstance(getApplicationContext());

        description = findViewById(R.id.description);
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String desc = description.getText().toString();
                executors.background().execute(new Runnable() {
                    @Override
                    public void run() {
                        record.setDescription(desc);
                        store.save(record);
                        finish();
                    }
                });
            }
        });

        spinner = findViewById(R.id.category_spinner);
        categoryArrayAdapter = new ArrayAdapter<Category>(this, R.layout.item_spinner, R.id.title_spinner, categoryList);
        spinner.setAdapter(categoryArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                record.setCategoryId(categoryList.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        date = findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(record.getStart());
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(EditRecordActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        record.setStart(modifyDate(record.getStart(), year, month, dayOfMonth));
                        record.setEnd(modifyDate(record.getEnd(), year, month, dayOfMonth));
                        setDate(record);
                    }

                    @NonNull
                    private Date modifyDate(Date date, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        return calendar.getTime();
                    }
                }, year, month, day).show();
            }
        });

        dateStart = findViewById(R.id.date_start);
        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(record.getStart());
                int minute = calendar.get(Calendar.MINUTE);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                new TimePickerDialog(EditRecordActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(record.getStart());
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        record.setStart(calendar.getTime());
                        setDate(record);
                        recalculateMinutes(record);
                    }
                }, hour, minute, true).show();
            }
        });
        dateEnd = findViewById(R.id.date_end);
        dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(record.getEnd());
                int minute = calendar.get(Calendar.MINUTE);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                new TimePickerDialog(EditRecordActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(record.getEnd());
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        record.setEnd(calendar.getTime());
                        setDate(record);
                        recalculateMinutes(record);
                    }
                }, hour, minute, true).show();
            }
        });

        Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executors.background().execute(new Runnable() {
                    @Override
                    public void run() {
                        store.deleteRecord(record.getId());
                        executors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                });
            }
        });

        photoAdapter = new PhotoAdapter();
        RecyclerView photos = findViewById(R.id.photos);
        photos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photos.setAdapter(photoAdapter);

        findViewById(R.id.add_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        loadRecord(id);
    }

    private void recalculateMinutes(Record record) {
        long millis = record.getEnd().getTime() - record.getStart().getTime();
        int minutes = (int) (millis / (1000 * 60));
        record.setMinutes(minutes);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            Uri fullPhotoUri = data.getData();
            final Photo photo = new Photo();
            photo.setRecordId(record.getId());
            photo.setPhotoUri(fullPhotoUri);
            executors.background().execute(new Runnable() {
                @Override
                public void run() {
                    store.save(photo);
                    loadPhotos(record.getId());
                }
            });
        }
    }

    private void loadRecord(final int id) {
        executors.background().execute(new Runnable() {
            @Override
            public void run() {
                categoryList = store.getCategories();
                categoryArrayAdapter.clear();
                categoryArrayAdapter.addAll(categoryList);

                if (id != -1) {
                    record = store.getRecotdById(id);
                } else {
                    record = new Record();
                    int id = (int) store.save(record);
                    record.setId(id);
                }
                executors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        categoryArrayAdapter.notifyDataSetChanged();
                        fill(record);
                    }
                });
            }
        });
    }

    private void fill(Record record) {
        description.setText(record.getDescription());

        for (int i = 0; i < categoryList.size(); i++) {
            if (record.getCategoryId() == 0 || record.getCategoryId() == categoryList.get(i).getId()) {
                spinner.setSelection(i);
                break;
            }
        }
        setDate(record);
        loadPhotos(record.getId());
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }


    private void loadPhotos(final int id) {
        executors.background().execute(new Runnable() {
            @Override
            public void run() {
                final List<Photo> photos = store.getPhotosForRecord(id);
                executors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        photoAdapter.setPhotoList(photos);
                    }
                });
            }
        });
    }

    private void setDate(Record record) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("hh:mm");

        date.setText(dateFormat.format(record.getStart()));

        dateStart.setText(timeFormat.format(record.getStart()));
        dateEnd.setText(timeFormat.format(record.getEnd()));
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoItem> {

        private List<Photo> photoList = new ArrayList<>();

        public void setPhotoList(List<Photo> photoList) {
            this.photoList = photoList;
            notifyDataSetChanged();
        }

        @Override
        public PhotoItem onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
            return new PhotoItem(v);
        }

        @Override
        public void onBindViewHolder(PhotoItem holder, int position) {
            holder.bind(photoList.get(position));
        }

        @Override
        public int getItemCount() {
            return photoList.size();
        }

        public void delete(long id) {
            for (int i = 0; i < photoList.size(); i++) {
                if (photoList.get(i).getId() == id) {
                    photoList.remove(i);
                    break;
                }
            }
            notifyDataSetChanged();

        }
    }

    private class PhotoItem extends RecyclerView.ViewHolder {

        private ImageView photoView;
        private Button delete;

        public PhotoItem(View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photo);
            delete = itemView.findViewById(R.id.delete_photo);
        }

        public void bind(final Photo photo) {
            photoView.setImageURI(photo.getPhotoUri());
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photoAdapter.delete(photo.getId());

                    executors.background().execute(new Runnable() {
                        @Override
                        public void run() {
                            store.deletePhoto(photo.getId());
                        }
                    });
                }
            });
        }
    }
}
