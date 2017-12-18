package com.ksekey.timemanagment.entitiies;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ikvant.
 */

@Database(entities = {Record.class, Category.class, Photo.class}, version = 1)
@TypeConverters(value = {AppDatabase.Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecordDao recordDao();

    public abstract CategoryDao categoryDao();

    public abstract PhotoDao photoDao();

    @Dao
    interface RecordDao {

        @Query("SELECT * FROM Record WHERE id=:id")
        Record loadById(int id);

        @Query("SELECT * FROM Record")
        List<Record> loadAll();


        @Query("DELETE FROM Record")
        void delete();

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        long save(Record record);
    }

    @Dao
    interface CategoryDao {
        @Query("SELECT * FROM Category WHERE id=:id")
        Category loadById(int id);

        @Query("SELECT categoryId, COUNT(*) as meta FROM Record WHERE start BETWEEN :start and :end  GROUP BY categoryId ORDER BY meta")
        List<MetaCategory> getCountRecords(Date start, Date end);

        @Query("SELECT categoryId, SUM(minutes) as meta FROM Record WHERE start BETWEEN :start and :end  GROUP BY categoryId ORDER BY meta")
        List<MetaCategory> getSumRecords(Date start, Date end);

        @Query("SELECT categoryId, MAX(minutes) as meta FROM Record WHERE start BETWEEN :start and :end  GROUP BY categoryId ORDER BY meta")
        List<MetaCategory> getMaxRecords(Date start, Date end);

        @Query("SELECT * FROM Category")
        List<Category> loadAll();

        @Query("DELETE FROM Category")
        void delete();

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void save(Category record);


    }

    @Dao
    interface PhotoDao {
        @Query("SELECT * FROM Photo")
        List<Photo> loadAll();

        @Query("DELETE FROM Photo")
        void delete();

        @Query("DELETE FROM Photo WHERE id=:id")
        void deleteById(int id);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void save(Photo record);

        @Query("SELECT * FROM Photo WHERE recordId=:recordId")
        List<Photo> loadAllByRecordId(int recordId);
    }

    public static class Converters {
        private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-MM", Locale.UK);

        @TypeConverter
        public static Date dateFromString(String formatString) {
            try {
                return dateFormat.parse(formatString);
            } catch (ParseException e) {
                return null;
            }
        }

        @TypeConverter
        public static String dateToString(Date date) {
            return dateFormat.format(date);
        }
    }

}

