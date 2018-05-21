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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.md2k.mcerebrum.datakit.Constants;
import org.md2k.mcerebrum.datakit.configuration.Configuration;
import org.md2k.mcerebrum.datakit.configuration.ConfigurationManager;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataType;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeLong;
import org.md2k.mcerebrum.core.datakitapi.datatype.RowObject;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSource;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceClient;
import org.md2k.mcerebrum.core.datakitapi.status.Status;
import org.md2k.mcerebrum.commons.storage_old.FileManager;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Provides wrapper methods for managing calls to database table methods.
 */
public class DatabaseLogger extends SQLiteOpenHelper {

    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = DatabaseLogger.class.getSimpleName();

    /** Instance of this class. */
    private static DatabaseLogger instance = null;

    /** <code>DataSource</code> table. */
    DatabaseTable_DataSource databaseTable_dataSource = null;

    /** Data table. */
    DatabaseTable_Data databaseTable_data = null;

    /** Database object. */
    SQLiteDatabase db = null;

    /** <code>GzipLogger</code> */
    gzipLogger gzLogger = null;

    /**
     * Constructor
     *
     * <p>
     *     This method initializes the data storage system to log data to both SQLite and gzip files.
     * </p>
     *
     * @param context Android context
     * @param path Path to the database.
     */
    public DatabaseLogger(Context context, String path) {
        super(context, path, null, 1);
        db = this.getWritableDatabase();
        Log.d(TAG, "DataBaseLogger() db isopen=" + db.isOpen() + " readonly=" + db.isReadOnly()
                                + " isWriteAheadLoggingEnabled=" + db.isWriteAheadLoggingEnabled());
        databaseTable_dataSource = new DatabaseTable_DataSource(db);
        gzLogger = new gzipLogger(context);
        databaseTable_data = new DatabaseTable_Data(db, gzLogger);
    }

    /**
     * Returns this instance of this class.
     *
     * @throws IOException
     */
    public static DatabaseLogger getInstance(Context context) throws IOException {
        if (instance == null) {
            Configuration configuration=ConfigurationManager.read(context);
            String directory=FileManager.getDirectory(context, configuration.database.location);
            Log.d(TAG, "directory=" + directory);
            if (directory != null)
                instance = new DatabaseLogger(context, directory + Constants.DATABASE_FILENAME);
            else throw new IOException("Database directory not found");
        }
        return instance;
    }

    /**
     * Checks if there is an instance running.
     *
     * @return Whether there is an instance running.
     */
    public static boolean isAlive(){
        return instance != null;
    }

    /**
     * Commits the remaining data to the data table and closes this instance.
     */
    public void close() {
        if(instance != null) {
            Log.d(TAG, "close()");
            databaseTable_data.stopPruning();
            databaseTable_data.commit(db);

            if (db.isOpen())
                db.close();
            super.close();
            db = null;
            instance = null;
        }
    }

    /**
     * Inserts a new row into the data table.
     *
     * @param dataSourceId Data source identifier.
     * @param dataType Array of data to insert.
     * @param isUpdate Whether this insertion is an update or not.
     * @return The status after insertion.
     */
    public Status insert(int dataSourceId, DataType[] dataType, boolean isUpdate) {
        return databaseTable_data.insert(db, dataSourceId, dataType, isUpdate);
    }

    /**
     * Inserts a row of high frequency data into the data table.
     *
     * @param dataSourceId Data source identifier.
     * @param dataType Array of data to insert.
     * @return The status after the insertion.
     */
    public Status insertHF(int dataSourceId, DataTypeDoubleArray[] dataType) {
        return databaseTable_data.insertHF(dataSourceId, dataType);
    }

    /**
     * Queries the database for the given data source during the given time frame.
     *
     * @param ds_id Data source identifier.
     * @param startTimestamp Beginning of the time frame.
     * @param endTimestamp End of the time frame.
     * @return The result of the query.
     */
    public ArrayList<DataType> query(int ds_id, long startTimestamp, long endTimestamp) {
        return databaseTable_data.query(db, ds_id, startTimestamp, endTimestamp);
    }

    /**
     * Queries the database for the last n samples of the given data source.
     *
     * @param ds_id Data source identifier.
     * @param last_n_sample Number of samples to get.
     * @return The result of the query.
     */
    public ArrayList<DataType> query(int ds_id, int last_n_sample) {
        return databaseTable_data.query(db, ds_id, last_n_sample);
    }

    /**
     * Queries for the last key of the given data source.
     *
     * @param ds_id Data source identifier.
     * @param limit Maximum number of rows to return.
     * @return The result of the query.
     */
    public ArrayList<RowObject> queryLastKey(int ds_id, int limit) {
        return databaseTable_data.queryLastKey(db, ds_id, limit);
    }

    /**
     * Queries the database for the last synced data.
     *
     * @param ds_id Data source identifier.
     * @param ageLimit Limit on the age of the data.
     * @param limit Maximum number of rows to return.
     * @return The result of the query.
     */
    public ArrayList<RowObject> querySyncedData(int ds_id, long ageLimit, int limit) {
        return databaseTable_data.querySyncedData(db, ds_id, ageLimit, limit);
    }

    /**
     * Sets a synced bit.
     *
     * @param ds_id Data source identifier.
     * @param key Key for this sync.
     * @return Always returns true from <code>databaseTable_data.setSyncedBit()</code>.
     */
    public boolean setSyncedBit(int ds_id, long key) {
        return databaseTable_data.setSyncedBit(db, ds_id, key);
    }

    /**
     * Removes data from the table.
     *
     * @param ds_id Data source identifier.
     * @param key Key for this sync.
     * @return Always returns true from <code>databaseTable_data.removeSyncedData()</code>.
     */
    public boolean removeSyncedData(int ds_id, long key) {
        return databaseTable_data.removeSyncedData(db, ds_id, key);
    }

    /**
     * Prunes the synced data for the given data source.
     *
     * @param ds_id Data source identifier.
     */
    public void pruneSyncData(int ds_id){
        databaseTable_data.pruneSyncData(db, ds_id);
    }

    /**
     * Prunes synced data from the database.
     *
     * @param prune List of data source identifiers to prune.
     */
    public void pruneSyncData(ArrayList<Integer> prune){
        databaseTable_data.pruneSyncData(db, prune);
    }

    /**
     * Returns the size of the query in number of rows.
     *
     * @return The number of rows in the query.
     */
    public DataTypeLong querySize() {
        return databaseTable_data.querySize(db);
    }

    /**
     * Registers the given data source to a row in the data source table.
     *
     * @param dataSource Data source to register.
     * @return The registered <code>DataSourceClient</code>.
     */
    public DataSourceClient register(DataSource dataSource) {
        return databaseTable_dataSource.register(db, dataSource);
    }

    /**
     * Finds the given data source in the data source table.
     *
     * @param dataSource Data source to find.
     * @return List of matching <code>DataSourceClient</code>s.
     */
    public ArrayList<DataSourceClient> find(DataSource dataSource) {
        return databaseTable_dataSource.findDataSource(db, dataSource);
    }

    /**
     * Overridden method, does nothing.
     *
     * @param db Database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {}

    /**
     * Overridden method, does nothing.
     *
     * @param db Database.
     * @param oldVersion Old version number.
     * @param newVersion New version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    /**
     * Calls <code>onUpgrade()</code>.
     *
     * @param db Database.
     * @param oldVersion Old version number.
     * @param newVersion New version number.
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Returns the number of rows in the query, either synced or unsynced.
     *
     * @param ds_id Data source identifier.
     * @param unsynced Whether the query is unsynced or synced.
     * @return The number of rows in the query.
     */
    public DataTypeLong queryCount(int ds_id, boolean unsynced) {
        return databaseTable_data.queryCount(db, ds_id, unsynced);
    }

}
