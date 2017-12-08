package com.ksekey.timemanagment.entitiies;

import android.arch.persistence.room.Room;
import android.content.Context;

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
}
