/*
 * Copyright (c) 2018, The University of Memphis, MD2K Center of Excellence
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.md2k.mcerebrum.datakit.logger;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.md2k.mcerebrum.core.datakitapi.datatype.DataType;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeLong;
import org.md2k.mcerebrum.core.datakitapi.datatype.RowObject;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceClient;
import org.md2k.mcerebrum.core.datakitapi.status.Status;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Class for defining a <code>Data</code> table within the database.
 */
public class DatabaseTable_Data {

    /** Name fo the table. <p>Default is <code>"data"</code>.</p> */
    public static String TABLE_NAME = "data";

    private static String C_ID = "_id";

    private static String C_CLOUD_SYNC_BIT = "cc_sync";

    private static String C_DATETIME = "datetime";

    private static String C_SAMPLE = "sample";

    /** <code>DataSource</code> identifier. */
    private static String C_DATASOURCE_ID = "datasource_id";

    /** Size of a content value array. */
    private static final int CVALUE_LIMIT = 250;

    /** Size of a content value array for high frequency data. */
    private static final int HFVALUE_LIMIT = 500;

    /** Maximum number of rows in the data table. */
    private static final int MAX_DATA_ROW = 50000;

    private SparseArray<Subscription> subscriptionPrune;

    private Subscription subsPrune;
    SparseArray<DataSourceClient> dscs;

    /** Command string to create an index into the database based on <code>datasource_id</code>. */
    private static final String SQL_CREATE_DATA_INDEX = "CREATE INDEX IF NOT EXISTS index_datasource_id on "
                                                        + TABLE_NAME + " (" + C_DATASOURCE_ID + ");";
    /** Command string to create an index into the database based on <code>datasource_id</code> and <code>cloud_sync_bit</code>. */
    private static final String SQL_CREATE_CC_INDEX = "CREATE INDEX IF NOT EXISTS index_cc_datasource_id on "
                                        + TABLE_NAME + " (" + C_DATASOURCE_ID + ", " + C_CLOUD_SYNC_BIT + ");";

    /** Database table creation command. */
    private static final String SQL_CREATE_DATA = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            C_ID + " INTEGER PRIMARY KEY autoincrement, " +
            C_DATASOURCE_ID + " TEXT not null, " +
            C_CLOUD_SYNC_BIT + " INTEGER DEFAULT 0, " +
            C_DATETIME + " LONG, " +
            C_SAMPLE + " BLOB not null);";

    /**  */
    private static String C_COUNT = "c";

    /** 5 seconds waittime. */
    private static final long WAITTIME = 5 * 1000L;

    /** Content values array for data. */
    private ContentValues[] cValues = new ContentValues[CVALUE_LIMIT];

    /** Content values array for hight frequency data. */
    private ContentValues[] hfValues = new ContentValues[HFVALUE_LIMIT];

    /** Number of <code>ContentValues</code> to insert into the database. */
    private int cValueCount = 0;

    /** Number of <code>ContentValues</code> for high frequency data. */
    private int hfValueCount = 0;

    /** Time of the last insertion. */
    long lastUnlock = 0;
    long lastUnlockHF = 0;

    /** Used for turning <code>DataSource</code> objects into byte arrays, or serialization. */
    Kryo kryo;

    /** <code>GzipLogger</code> for creating gzip files of high frequency data. */
    private gzipLogger gzLogger;

    /**
     * Creates a data table in the database if one does not already exist.
     *
     * @param db database to check.
     * @param gzl <code>GzipLogger</code>.
     */
    DatabaseTable_Data(SQLiteDatabase db, gzipLogger gzl) {
        subscriptionPrune = new SparseArray<Subscription>();
        kryo = new Kryo();
        createIfNotExists(db);
        gzLogger = gzl;
        dscs = new SparseArray<>();
    }

    /**
     * Removes the table from the database.
     *
     * @param db Database.
     */
    public synchronized void removeAll(SQLiteDatabase db) {
        db.execSQL("DROP INDEX index_datasource_id");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    /**
     * Creates the tables for the database if needed.
     *
     * @param db Database.
     */
    public synchronized void createIfNotExists(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DATA);
        db.execSQL(SQL_CREATE_DATA_INDEX);
        db.execSQL(SQL_CREATE_CC_INDEX);
    }

    /**
     * Starts a transaction, inserts all available <code>ContentValues</code>, and ends the transaction.
     *
     * @param db Database
     * @param tableName Name of the table to insert rows into.
     * @return The status after insertion.
     */
    private synchronized Status insertDB(SQLiteDatabase db, String tableName) {
        try {
            if (cValueCount == 0)
                return new Status(Status.SUCCESS);

            long st = System.currentTimeMillis();
            db.beginTransaction();

            for (int i = 0; i < cValueCount; i++) {
                db.insert(tableName, null, cValues[i]);
            }
            gzLogger.insert(cValues, cValueCount, dscs);
            cValueCount = 0;

            try {
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            return new Status(Status.INTERNAL_ERROR);
        }
        return new Status(Status.SUCCESS);
    }

    /**
     * Updates the given data source in the database.
     *
     * @param db Database.
     * @param dsid Data source identifier.
     * @param dataType Data type to update.
     * @return Always returns true.
     */
    public synchronized boolean update(SQLiteDatabase db, int dsid, DataType dataType) {
        try {
            insertDB(db, TABLE_NAME);
            ContentValues values = prepareData(dsid, dataType);
            String[] args = new String[]{Long.toString(dsid), Long.toString(dataType.getDateTime())};
            db.update(TABLE_NAME, values, "datasource_id = ? AND datetime = ?", args);
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Updates or inserts the data type accordingly.
     *
     * @param db Database.
     * @param dataSourceClient Data source identifier.
     * @param dataType Data type to insert.
     * @param isUpdate Whether the data is an update.
     * @return The status after insertion.
     */
    public synchronized Status insert(SQLiteDatabase db, DataSourceClient dataSourceClient, DataType[] dataType, boolean isUpdate) {
        dscs.put(dataSourceClient.getDs_id(), dataSourceClient);
        if (isUpdate) {
            for (DataType aDataType : dataType) update(db, dataSourceClient.getDs_id(), aDataType);
        } else {
            for (DataType aDataType : dataType) insert(db, dataSourceClient.getDs_id(), aDataType);
        }
        return new Status(Status.SUCCESS);
    }


    /**
     * Inserts the given data into a <code>ContentValues</code> object.
     *
     * @param db Database.
     * @param dataSourceId Data source identifier.
     * @param dataType Data type to insert.
     * @return The status after insertion.
     */
    private synchronized Status insert(SQLiteDatabase db, int dataSourceId, DataType dataType) {
        Status status = new Status(Status.SUCCESS);
        if (dataType.getDateTime() - lastUnlock >= WAITTIME || cValueCount >= CVALUE_LIMIT) {
            status = insertDB(db, TABLE_NAME);
            cValueCount = 0;
            lastUnlock = dataType.getDateTime();
        }
        ContentValues contentValues = prepareData(dataSourceId, dataType);
        cValues[cValueCount++] = contentValues;
        return status;
    }

    /**
     * Wrapper method for <code>insertHF()</code> that iterates through a data type array.
     *
     * @param dataSourceClient Data source identifier.
     * @param dataType Data type to insert.
     * @return The status after insertion.
     */
    public synchronized Status insertHF(DataSourceClient dataSourceClient, DataTypeDoubleArray[] dataType) {
        dscs.put(dataSourceClient.getDs_id(), dataSourceClient);
        for (DataTypeDoubleArray aDataType : dataType)
            insertHF(dataSourceClient.getDs_id(), aDataType);
        return new Status(Status.SUCCESS);
    }

    /**
     * Insert high frequency data into a <code>ContentValues</code> object.
     *
     * @param dsId Data source identifier.
     * @param dataType Data type to insert.
     * @return The status after insertion.
     */
    private synchronized Status insertHF(int dsId, DataTypeDoubleArray dataType) {
        Status status = new Status(Status.SUCCESS);
        if (dataType.getDateTime() - lastUnlockHF >= WAITTIME || hfValueCount >= HFVALUE_LIMIT) {
            status = gzLogger.insertHF(hfValues, hfValueCount, dscs);
            hfValueCount = 0;
            lastUnlockHF = dataType.getDateTime();
        }
        ContentValues contentValues = prepareDataHF(dsId, dataType);
        hfValues[hfValueCount++] = contentValues;
        return status;
    }


    /**
     * Prepares a high frequency <code>ContentValues</code> object.
     *
     * @param dsId Data source identifier.
     * @param dataType Data type to convert.
     * @return The constructed <code>ContentValues</code> object.
     */
    public synchronized ContentValues prepareDataHF(int dsId, DataTypeDoubleArray dataType) {
        ContentValues contentValues = new ContentValues();
        byte[] dataTypeArray = dataType.toRawBytes();

        contentValues.put(C_DATASOURCE_ID, dsId);
        contentValues.put(C_DATETIME, dataType.getDateTime());
        contentValues.put(C_SAMPLE, dataTypeArray);
        return contentValues;
    }


    /**
     * Creates a selection command string for the given data source during the given time frame.
     *
     * @param ds_id Data stream identifier.
     * @param starttimestamp Beginning of the time frame.
     * @param endtimestamp End of the time frame.
     * @return The prepared selection string.
     */
    private synchronized String[] prepareSelectionArgs(int ds_id, long starttimestamp, long endtimestamp) {
        ArrayList<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(String.valueOf(ds_id));
        selectionArgs.add(String.valueOf(starttimestamp));
        selectionArgs.add(String.valueOf(endtimestamp));
        return selectionArgs.toArray(new String[selectionArgs.size()]);
    }

    /**
     * Creates a selection command string for the given data source.
     *
     * @param ds_id Data source to select for.
     * @return The prepared selection string.
     */
    private synchronized String[] prepareSelectionArgs(int ds_id) {
        ArrayList<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(String.valueOf(ds_id));
        return selectionArgs.toArray(new String[selectionArgs.size()]);
    }

    /**
     * Creates a selection command string.
     *
     * @return The prepared selection string.
     */
    private synchronized String prepareSelection() {
        String selection;
        selection = C_DATASOURCE_ID + "=? AND " + C_DATETIME + " >=? AND " + C_DATETIME + " <=?";
        return selection;
    }

    /**
     * Creates a selection command string for the last samples.
     *
     * @return The prepared selection string.
     */
    private synchronized String prepareSelectionLastSamples() {
        String selection;
        selection = C_DATASOURCE_ID + "=?";
        return selection;
    }

    /**
     * Queries the database for the given data source during the given time frame.
     *
     * @param ds_id Data source identifier.
     * @param starttimestamp Beginning of the time frame.
     * @param endtimestamp End of the time frame.
     * @return The result of the query.
     */
    public synchronized ArrayList<DataType> query(SQLiteDatabase db, int ds_id, long starttimestamp, long endtimestamp) {
        long totalst = System.currentTimeMillis();
        insertDB(db, TABLE_NAME);
        Cursor mCursor;
        ArrayList<DataType> dataTypes = new ArrayList<>();
        String[] columns = new String[]{C_SAMPLE};
        String selection = prepareSelection();
        String[] selectionArgs = prepareSelectionArgs(ds_id, starttimestamp, endtimestamp);
        mCursor = db.query(TABLE_NAME,
                columns, selection, selectionArgs, null, null, null);
        if (mCursor.moveToFirst()) {
            do {
                try {
                    dataTypes.add(fromBytes(mCursor.getBlob(mCursor.getColumnIndex(C_SAMPLE))));
                } catch (Exception e) {
                    Log.e("DataKit", "Object failed deserialization");
                    Log.e("DataKit", "DataSourceID: " + ds_id + " Row: " + mCursor.getLong(mCursor.getColumnIndex(C_ID)));
                    e.printStackTrace();
                }
            } while (mCursor.moveToNext());
        }
        mCursor.close();

        return dataTypes;
    }

    /**
     * Queries the database for the last n samples of the given data source.
     *
     * @param db Database.
     * @param ds_id Data source identifier.
     * @param last_n_sample Last n samples to return.
     * @return A list of <code>DataType</code>s matching the query.
     */
    public synchronized ArrayList<DataType> query(SQLiteDatabase db, int ds_id, int last_n_sample) {
        long totalst = System.currentTimeMillis();
        insertDB(db, TABLE_NAME);
        ArrayList<DataType> dataTypes = new ArrayList<>();
        String[] columns = new String[]{C_SAMPLE};
        String selection = prepareSelectionLastSamples();
        String[] selectionArgs = prepareSelectionArgs(ds_id);
        Cursor mCursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, "_id DESC", String.valueOf(last_n_sample));
        if (mCursor.moveToFirst()) {
            do {
                try {
                    dataTypes.add(fromBytes(mCursor.getBlob(mCursor.getColumnIndex(C_SAMPLE))));
                } catch (Exception e) {
                    Log.e("DataKit", "Object failed deserialization");
                    Log.e("DataKit", "DataSourceID: " + ds_id + " Row: " + mCursor.getLong(mCursor.getColumnIndex(C_ID)));
                    e.printStackTrace();
                }
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return dataTypes;
    }


    /**
     * Queries the database for the last inserted key.
     *
     * @param db Database.
     * @param ds_id Data source identifier.
     * @param limit Limit on how many rows to return in the query.
     * @return A list of <code>RowObject</code>s that match the query.
     */
    public synchronized ArrayList<RowObject> queryLastKey(SQLiteDatabase db, int ds_id, int limit) {
        long totalst = System.currentTimeMillis();
        insertDB(db, TABLE_NAME);
        ArrayList<RowObject> rowObjects = new ArrayList<>();
        String sql = "select _id, sample from data where cc_sync = 0 and datasource_id=" + Integer.toString(ds_id)
                + " LIMIT " + Integer.toString(limit);
        Cursor mCursor = db.rawQuery(sql, null);
        if (mCursor.moveToFirst()) {
            do {
                try {
                    DataType dt = fromBytes(mCursor.getBlob(mCursor.getColumnIndex(C_SAMPLE)));
                    rowObjects.add(new RowObject(mCursor.getLong(mCursor.getColumnIndex(C_ID)), dt));
                } catch (Exception e) {
                    Log.e("DataKit", "Object failed deserialization");
                    Log.e("DataKit", "DataSourceID: " + ds_id + " Row: " + mCursor.getLong(mCursor.getColumnIndex(C_ID)));
                    e.printStackTrace();
                }
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return rowObjects;
    }

    /**
     * Queries the database for already synced data.
     *
     * @param db Database.
     * @param ds_id Data stream identifier.
     * @param ageLimit Limit on the age of the data.
     * @param limit Limit on how many rows to return in the query.
     * @return A list of <code>RowObject</code>s that match the query.
     */
    public synchronized ArrayList<RowObject> querySyncedData(SQLiteDatabase db, int ds_id, long ageLimit, int limit) {
        long totalst = System.currentTimeMillis();
        insertDB(db, TABLE_NAME);
        ArrayList<RowObject> rowObjects = new ArrayList<>(limit);
        String sql = "select _id, sample from data where cc_sync = 1 and datasource_id=" + Integer.toString(ds_id)
                + " and datetime <= " + ageLimit + " LIMIT " + Integer.toString(limit);
        Cursor mCursor = db.rawQuery(sql, null);
        if (mCursor.moveToFirst()) {
            do {
                try {
                    DataType dt = fromBytes(mCursor.getBlob(mCursor.getColumnIndex(C_SAMPLE)));
                    rowObjects.add(new RowObject(mCursor.getLong(mCursor.getColumnIndex(C_ID)), dt));
                } catch (Exception e) {
                    Log.e("DataKit", "Object failed deserialization");
                    Log.e("DataKit", "DataSourceID: " + ds_id + " Row: " + mCursor.getLong(mCursor.getColumnIndex(C_ID)));
                    e.printStackTrace();
                }
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return rowObjects;
    }


    /**
     * Deletes the already synced data from the database.
     *
     * @param db Database.
     * @param dsid Data source identifier.
     * @param lastSyncKey Key of the last synced data.
     * @return Always returns true.
     */
    public synchronized boolean removeSyncedData(SQLiteDatabase db, int dsid, long lastSyncKey) {
        insertDB(db, TABLE_NAME);
        String[] args = new String[]{Long.toString(lastSyncKey), Integer.toString(dsid)};
        db.delete(TABLE_NAME, "cc_sync = 1 AND _id <= ? AND datasource_id = ?", args);
        return true;
    }

    /**
     * Prunes already synced data from the database.
     *
     * @param db Database.
     * @param prunes List of data sources to prune.
     */
    public void pruneSyncData(final SQLiteDatabase db, final ArrayList<Integer> prunes) {
        final int[] current = new int[1];
        if(prunes == null || prunes.size() == 0) return;
        current[0] = 0;
        if(subsPrune != null && !subsPrune.isUnsubscribed())
            subsPrune.unsubscribe();
        subsPrune = Observable.range(1,1000000).takeUntil(new Func1<Integer, Boolean>() {
            /**
             * Prunes the database if it is too large.
             *
             * @param aLong Delete interval
             * @return
             */
            @Override
            public Boolean call(Integer aLong) {
                Log.d("abc", "current=" + current[0] + " size=" + prunes.size());
                if(current[0] >= prunes.size())
                    return true;
                DataTypeLong countRow = queryCount(db, prunes.get(current[0]), false);
                Log.d("abc", "id=" + prunes.get(current[0]) + " count=" + countRow.getSample());
                if(countRow.getSample() > MAX_DATA_ROW){
                    long prune = countRow.getSample() - MAX_DATA_ROW;
                    Log.d("abc", "id=" + prunes.get(current[0]) + " before delete interval=" + aLong);
                    if(prune > MAX_DATA_ROW) prune = MAX_DATA_ROW;
                    String ALTER_TBL = "delete from " + TABLE_NAME + " where _id in (select _id from "
                            + TABLE_NAME + " where datasource_id=" + Integer.toString(prunes.get(current[0]))
                            + " AND cc_sync=1 order by _id limit " + Long.toString(prune)+")";
                    db.execSQL(ALTER_TBL);
                    Log.d("abc", "id=" + prunes.get(current[0]) + " after delete");
                }else{ // These logs are "abc", logs at line 368 are "DataKit"
                    Log.d("abc", "current++");
                    current[0]++;
                }
                return false;
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onNext(Integer aLong) {}
        });
    }

    /**
     * Prunes synced data from the database.
     *
     * @param db Database to prune.
     * @param dsid Data source identifier.
     */
    public void pruneSyncData(final SQLiteDatabase db, final int dsid) {
        Subscription s = subscriptionPrune.get(dsid);
        if(s != null && !s.isUnsubscribed())
            s.unsubscribe();
        if(subsPrune != null && !subsPrune.isUnsubscribed())
            subsPrune.unsubscribe();
        Random rn = new Random();
        int value = 15 + rn.nextInt(30);
        s = Observable.interval(rn.nextInt(5),value, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                        .observeOn(Schedulers.newThread()).takeUntil(new Func1<Long, Boolean>() {
            /**
             * Deletes the given number of rows for the given data source identifier from the given table.
             *
             * @param aLong Delete interval.
             * @return Whether the deletion is complete or not.
             */
            @Override
            public Boolean call(Long aLong) {
                DataTypeLong countRow = queryCount(db, dsid, false);
                if(countRow.getSample() > MAX_DATA_ROW){
                    long prune = countRow.getSample() - MAX_DATA_ROW;
                    Log.d("abc","id=" + dsid + " before delete interval=" + aLong);
                    if(prune > MAX_DATA_ROW) prune = MAX_DATA_ROW;
                    String ALTER_TBL = "delete from " + TABLE_NAME + " where _id in (select _id from "
                            + TABLE_NAME + " where datasource_id=" + Integer.toString(dsid)
                            + " AND cc_sync=1 order by _id limit " + Long.toString(prune) + ")";
                    db.execSQL(ALTER_TBL);
                    Log.d("abc", "id=" + dsid + " after delete");
                    return false;
                }else
                    return true;
            }
        }).subscribe(new Observer<Long>() {
            /** Logs on completion. */
            @Override
            public void onCompleted() {
                Log.d("abc","id=" + dsid + " onCompleted()");
            }

            /** Logs on error. */
            @Override
            public void onError(Throwable e) {
                Log.d("abc", "id=" + dsid + " error e=" + e.getMessage());
            }

            /** Logs on next. */
            @Override
            public void onNext(Long aLong) {
                Log.d("abc", "id=" + dsid + " onNext()");
            }
        });
        subscriptionPrune.put(dsid, s);
        Log.d("abc", "id=" + dsid + " after added to sparse array");
    }

    /**
     * Sets the synced bit of the last synced key.
     *
     * @param db Database.
     * @param dsid Data source identifier.
     * @param lastSyncKey Key of the last sync.
     * @return Always returns true;
     */
    public synchronized boolean setSyncedBit(SQLiteDatabase db, int dsid, long lastSyncKey) {
        insertDB(db, TABLE_NAME);
        ContentValues values = new ContentValues();
        int bit = 1;
        values.put("cc_sync", bit);
        String[] args = new String[]{Long.toString(lastSyncKey), Integer.toString(dsid)};
        db.update(TABLE_NAME, values, "cc_sync = 0 AND _id <= ? AND datasource_id = ?", args);
        return true;
    }


    /**
     * Returns the number of rows in the query.
     *
     * @param db Database
     * @return The number of rows in the query.
     */
    public synchronized DataTypeLong querySize(SQLiteDatabase db) {
        long totalst = System.currentTimeMillis();
        insertDB(db, TABLE_NAME);
        String sql = "select count(_id)as c from data";
        Cursor mCursor = db.rawQuery(sql, null);
        DataTypeLong count = new DataTypeLong(0L, 0L);
        if (mCursor.moveToFirst()) {
            do {
                count = new DataTypeLong(0L, mCursor.getLong(mCursor.getColumnIndex(C_COUNT)));
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return count;
    }

    /**
     * Counts the number of rows that are either synced or not synced.
     *
     * @param db Database
     * @param ds_id Data source identifier.
     * @param unsynced Whether the query is unsynced or not.
     * @return The number of rows matching the query.
     */
    public synchronized DataTypeLong queryCount(SQLiteDatabase db, int ds_id, boolean unsynced) {
        long totalst = System.currentTimeMillis();
        String sql = "select count(_id)as c from " + TABLE_NAME + " where " + C_DATASOURCE_ID + " = " + ds_id;
        if (unsynced)
            sql += " and " + C_CLOUD_SYNC_BIT + " = 0";
        Cursor mCursor = db.rawQuery(sql, null);
        DataTypeLong count = new DataTypeLong(0L, 0L);
        if (mCursor.moveToFirst()) {
            do {
                count = new DataTypeLong(0L, mCursor.getLong(mCursor.getColumnIndex(C_COUNT)));
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return count;
    }

    /**
     * Clears <code>subscriptionPrune</code> and unsubscribes <code>subsPrune</code>.
     */
    void stopPruning() {
        for (int i = 0; i < subscriptionPrune.size(); i++) {
            int key = subscriptionPrune.keyAt(i);
            Subscription s = subscriptionPrune.get(key);
            if (s != null && !s.isUnsubscribed()) s.unsubscribe();
        }
        subscriptionPrune.clear();
        if(subsPrune != null && !subsPrune.isUnsubscribed())
            subsPrune.unsubscribe();
    }

    /**
     * Puts the <code>DataType</code> into a <code>ContentValues</code> object.
     *
     * @param dataSourceId Data source identifier.
     * @param dataType Data type to prepare
     * @return The constructed <code>ContentValues</code> object.
     */
    public synchronized ContentValues prepareData(int dataSourceId, DataType dataType) {
        ContentValues contentValues = new ContentValues();

        byte[] dataTypeArray = toBytes(dataType);

        contentValues.put(C_DATASOURCE_ID, dataSourceId);
        contentValues.put(C_DATETIME, dataType.getDateTime());
        contentValues.put(C_SAMPLE, dataTypeArray);
        return contentValues;
    }


    /**
     * Commits an insertion into the database.
     *
     * @param db Database to commit to.
     */
    public synchronized void commit(SQLiteDatabase db) {
        insertDB(db, TABLE_NAME);
    }

    /**
     * Converts a <code>DataType</code> to a byte array.
     *
     * @param dataType <code>DataType</code> to convert.
     * @return A byte array containing the data type.
     */
    private synchronized byte[] toBytes(DataType dataType) {
        byte[] bytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.writeClassAndObject(output, dataType);
        output.close();
        bytes = baos.toByteArray();
        return bytes;
    }

    /**
     * Reads a <code>DataType</code> from a byte array.
     *
     * @param bytes Array to read the <code>DataType</code> from.
     * @return The <code>DataType</code> read from the array.
     */
    private synchronized DataType fromBytes(byte[] bytes) {
        Input input = new Input(new ByteArrayInputStream(bytes));
        DataType dataType = (DataType) kryo.readClassAndObject(input);
        input.close();
        return dataType;
    }
}
