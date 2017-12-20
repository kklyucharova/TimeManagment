package com.ksekey.timemanagment.entitiies;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.Date;
import java.util.List;

/**
 * Created by ikvant.
 */

public class Store {
    private static Store instance;

    private AppDatabase database;

    public static Store getInstance(Context context) {
        if (instance == null) {
            instance = new Store(context);
        }
        return instance;
    }

    private Store(Context context) {
        database = Room.databaseBuilder(context, AppDatabase.class, "store2.db").build();
    }

    public List<Record> getRecords() {
        return database.recordDao().loadAll();
    }

    public List<Category> getCategories() {
        return database.categoryDao().loadAll();
    }

    public Record getRecotdById(int id) {
        return database.recordDao().loadById(id);
    }

    public Category getCategoryById(int id) {
        return database.categoryDao().loadById(id);
    }

    public void save(Photo photo) {
        database.photoDao().save(photo);
    }

    public List<Photo> getPhotosForRecord(int recordId) {
        return database.photoDao().loadAllByRecordId(recordId);
    }

    public long save(Record record) {
        return database.recordDao().save(record);
    }

    public void deleteRecord(int id){
        database.recordDao().delete(id);
    }

    public void save(Category category) {
        database.categoryDao().save(category);
    }

    public void deletePhoto(long id) {
        database.photoDao().deleteById((int) id);
    }

    public List<MetaCategory> loadCategoriesCount(Date start, Date end) {
        return database.categoryDao().getCountRecords(start, end);
    }

    public List<MetaCategory> loadCategoriesSum(Date start, Date end) {
        return database.categoryDao().getSumRecords(start, end);
    }

    public List<MetaCategory> loadCategoriesMax(Date start, Date end) {
        return database.categoryDao().getMaxRecords(start, end);
    }

    public boolean getRecordsByCategory(int id) {
        return database.recordDao().loadByCategory(id);
    }

    public void deleteCategory(int id) {
        database.categoryDao().delete(id);
    }
}
