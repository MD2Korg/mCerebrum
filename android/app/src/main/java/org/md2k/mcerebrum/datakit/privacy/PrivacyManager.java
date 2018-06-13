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

package org.md2k.mcerebrum.datakit.privacy;

import android.content.Context;
import android.os.Handler;
import android.os.Messenger;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.md2k.mcerebrum.core.data_format.privacy.PrivacyData;
import org.md2k.mcerebrum.datakit.router.RoutingManager;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataType;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeJSONObject;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeLong;
import org.md2k.mcerebrum.core.datakitapi.datatype.RowObject;
import org.md2k.mcerebrum.core.datakitapi.source.application.Application;
import org.md2k.mcerebrum.core.datakitapi.source.application.ApplicationBuilder;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSource;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceClient;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceType;
import org.md2k.mcerebrum.core.datakitapi.source.platform.Platform;
import org.md2k.mcerebrum.core.datakitapi.source.platform.PlatformBuilder;
import org.md2k.mcerebrum.core.datakitapi.source.platform.PlatformType;
import org.md2k.mcerebrum.core.datakitapi.status.Status;
import org.md2k.mcerebrum.core.datakitapi.time.DateTime;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Provides methods for insuring the user's privacy preferences are respected.
 */
public class PrivacyManager {

    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = PrivacyManager.class.getSimpleName();

    /** Android context. */
    Context context;

    /** Instance of a <code>PrivacyManager</code> object. */
    private static PrivacyManager instance;

    /** Instance of a <code>routingManager</code> object. */
    RoutingManager routingManager;

    /** List of restricted data sources. */
//    SparseArray<Boolean> listPrivacyListDsId;

    /** An individual restricted data source. */
//    int dsIdPrivacy;

    /** Message handler. */
//    Handler handler;

    /** <code>PrivacyData</code> object from mCerebrum Utilites.
     *
     * <p>
     *     This object contains a duration, starting timestamp, and an arrayList of privacy types.
     * </p>
     */
//    PrivacyData privacyData;


/*
    Runnable timer = new Runnable() {
        @Override
        public void run() {
            deactivate();
        }
    };
*/

    /**
     * Returns whether the <code>PrivacyManager</code> is currently active.
     *
     * @return Whether <code>PrivacyManager</code> is active or not.
     */
/*
    public boolean isActive(){
        if (privacyData == null|| !privacyData.isStatus() || getRemainingTime()<=0)
            return false;
        else
            return true;
    }
*/

    /**
     * Constructor
     *
     * <p>
     *     The construction of <code>PrivacyManager</code> objects is logged.
     * </p>
     * @throws IOException
     */
    private PrivacyManager(Context context) throws IOException {
        Log.d(TAG,"PrivacyManager()..constructor()..");
        this.context = context;
        routingManager = RoutingManager.getInstance(context);
//        listPrivacyListDsId = new SparseArray<>();
//        handler = new Handler();
//        processPrivacyData();
    }

    /**
     * Returns this instance of the <code>PrivacyManager</code>.
     *
     * <p>
     *   If an instance doesn't already exist, it creates one.
     * </p>
     *
     * @throws IOException
     */
    public static PrivacyManager getInstance(Context context) throws IOException {
        if (instance == null)
            instance = new PrivacyManager(context);
        return instance;
    }

    /**
     * Iterates through <code>privacyData</code> to create a list of
     */
/*
    private void createPrivacyList() {
        if(!isActive())
            return;
        listPrivacyListDsId.clear();
        int id;
        for (int i = 0; i < privacyData.getPrivacyTypes().size(); i++) {
            for (int j = 0; j < privacyData.getPrivacyTypes().get(i).getDatasource().size(); j++) {
                ArrayList<DataSourceClient> dataSourceClients =
                    routingManager.find(privacyData.getPrivacyTypes().get(i).getDatasource().get(j));
                for (int k = 0; k < dataSourceClients.size(); k++) {
                    Log.d(TAG,"id=" + dataSourceClients.get(k).getDs_id());
                    id = dataSourceClients.get(k).getDs_id();
                    listPrivacyListDsId.put(id, true);
                }
            }
        }
        listPrivacyListDsId.remove(dsIdPrivacy);
    }
*/

    /**
     * Registers the given data source and creates a privacy list.
     *
     * @param dataSource Data source to register.
     * @return The registered <code>DataSourceClient</code>.
     */
    public DataSourceClient register(DataSource dataSource) {
        DataSourceClient dataSourceClient = routingManager.register(dataSource);
//        createPrivacyList();
        return dataSourceClient;
    }

    /**
     * Attempts to insert the given data source into the database, if privacy preferences allow.
     *
     * @param ds_id Data source identifier.
     * @param dataTypes Array of <code>dataTypes</code> to insert.
     * @return A status corresponding to the success or error of the insert action.
     */
    public Status insert(int ds_id, DataType[] dataTypes) {
        Status status = new Status(Status.SUCCESS);

        if(ds_id == -1 || dataTypes == null)
            return new Status(Status.INTERNAL_ERROR);

//        if (listPrivacyListDsId.get(ds_id) == null) {
            status = routingManager.insert(ds_id, dataTypes);

            if(status.getStatusCode() == Status.INTERNAL_ERROR)
                return status;
  /*      }
        if(ds_id == dsIdPrivacy){
            Log.d(TAG,"privacy data...process start...");
            processPrivacyData();
        }
  */      return status;
    }

    /**
     * Prepares private data for insertion into the database.
     *
     * @param privacyData <code>PrivacyData</code> object.
     */
/*
    public void insertPrivacy(PrivacyData privacyData){
        this.privacyData = privacyData;
        Gson gson = new Gson();
        JsonObject sample = new JsonParser().parse(gson.toJson(privacyData)).getAsJsonObject();
        DataTypeJSONObject dataTypeJSONObject = new DataTypeJSONObject(DateTime.getDateTime(), sample);
        insert(dsIdPrivacy, new DataTypeJSONObject[]{dataTypeJSONObject});
    }
*/

    /**
     * Attempts to insert the given data source into the database, if privacy preferences allow.
     *
     * @param ds_id Data source identifier.
     * @param dataTypes Array of high frequency data.
     * @return The status after insertion.
     */
    public Status insertHF(int ds_id, DataTypeDoubleArray[] dataTypes) {
        Status status = new Status(Status.SUCCESS);
        if (ds_id == -1 || dataTypes == null)
            return new Status(Status.INTERNAL_ERROR);
//        if (listPrivacyListDsId.get(ds_id) == null) {
            status = routingManager.insertHF(ds_id, dataTypes);
            if (status.getStatusCode() == Status.INTERNAL_ERROR)
                return status;
/*
        }
        if (ds_id == dsIdPrivacy) {
            Log.d(TAG, "privacy data...process start...");
            processPrivacyData();
        }
*/
        return status;
    }

    /**
     * Returns the time remaining on the duration of the privacy data.
     *
     * @return The remaining time on the duration of the privacy data.
     */
/*
    public long getRemainingTime(){
        long currentTimeStamp = DateTime.getDateTime();
        long endTimeStamp = privacyData.getStartTimeStamp() + privacyData.getDuration().getValue();
        Log.d(TAG,"remaining time = " + (endTimeStamp - currentTimeStamp));
        return endTimeStamp - currentTimeStamp;
    }
*/

    /**
     * Adds <code>privacyData</code> to the last synced privacy data.
     */
/*
    private void processPrivacyData(){
        Log.d(TAG, "processPrivacyData()...");
        privacyData = queryLastPrivacyData();

        Log.d(TAG,"privacyData=" + privacyData);
        if(isActive())
            activate();
        else
            deactivate();
    }
*/

    /**
     * Queries the database for the given data source during the given time frame.
     *
     * @param ds_id Data source identifier.
     * @param starttimestamp Beginning of the time frame.
     * @param endtimestamp End of the time frame.
     * @return The result of the query.
     */
    public ArrayList<DataType> query(int ds_id, long starttimestamp, long endtimestamp) {
        return routingManager.query(ds_id, starttimestamp, endtimestamp);
    }

    /**
     * Queries the database for the last n samples of the given data source.
     *
     * @param ds_id Data source identifier.
     * @param last_n_sample Number of samples to get.
     * @return The result of the query.
     */
    public ArrayList<DataType> query(int ds_id, int last_n_sample) {
        return routingManager.query(ds_id, last_n_sample);
    }

    /**
     * Queries for the last key of the given data source.
     *
     * @param ds_id Data source identifier.
     * @param limit Maximum number of rows to return.
     * @return The result of the query.
     */
    public ArrayList<RowObject> queryLastKey(int ds_id, int limit) {
        return routingManager.queryLastKey(ds_id, limit);
    }

    /**
     * Returns the size of the query in number of rows.
     *
     * @return The number of rows in the query.
     */
    public DataTypeLong querySize() {
        return routingManager.querySize();
    }

    /**
     * Unregisters the given data source by removing it from the <code>Publisher</code> array.
     *
     * @param ds_id Data source identifier to remove.
     * @return The status after the given data source has been unregistered.
     */
    public Status unregister(int ds_id) {
        return routingManager.unregister(ds_id);
    }

    /**
     * Subscribes the given data source to <code>MessageSubscriber</code>.
     *
     * @param ds_id Data source identifier.
     * @param packageName
     * @param reply Reference to a message handler.
     * @return The status after subscription
     */
    public Status subscribe(int ds_id, String packageName, Messenger reply) {
        return routingManager.subscribe(ds_id, packageName, reply);
    }

    /**
     * Unsubscribes the given data source.
     *
     * @param ds_id Data source identifier.
     * @param packageName
     * @param reply Reference to a message handler.
     * @return The status after unsubscribing.
     */
    public Status unsubscribe(int ds_id, String packageName, Messenger reply) {
        return routingManager.unsubscribe(ds_id, packageName, reply);
    }

    /**
     * Finds the given data sources within the database.
     *
     * @param dataSource <code>DataSource</code> to find.
     * @return An arrayList of matching <code>DataSourceClients</code>
     */
    public ArrayList<DataSourceClient> find(DataSource dataSource) {
        return routingManager.find(dataSource);
    }


    /**
     * Creates a <code>DataSource</code> object with a type set to <code>PRIVACY</code>.
     *
     * @return
     */
    private DataSource createDataSourcePrivacy() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).build();
        Application application = new ApplicationBuilder().setId(context.getPackageName()).build();
        return new DataSourceBuilder().setType(DataSourceType.PRIVACY).setPlatform(platform)
                .setApplication(application).build();
    }

    /**
     * Clears <code>listPrivacyListDsId</code> and closes the <code>RoutingManager</code> and
     * <code>PrivacyManager</code>.
     */
    public void close() {
        Log.d(TAG, "PrivacyManager()..close()..instance=" + instance);
        if(instance != null) {
//            listPrivacyListDsId.clear();
            routingManager.close();
            instance = null;
        }
    }

    /**
     * Removes any remaining callbacks from the timer.
     */
/*
    void activate() {
        handler.removeCallbacks(timer);
        createPrivacyList();
        handler.postDelayed(timer,getRemainingTime());
    }
*/

    /**
     * Returns the <code>PrivacyData</code> object of the caller.
     *
     * @return The <code>PrivacyData</code> object of the caller.
     */
/*
    public PrivacyData getPrivacyData(){
        return privacyData;
    }
*/

    /**
     * Queries the last privacy data sent to the database.
     *
     * @return The <code>PrivacyData</code> from the query.
     */
/*
    private PrivacyData queryLastPrivacyData() {
        Gson gson = new Gson();
        dsIdPrivacy = routingManager.register(createDataSourcePrivacy()).getDs_id();

        ArrayList<DataType> dataTypes = routingManager.query(dsIdPrivacy, 1);

        if (dataTypes == null || dataTypes.size() == 0)
            return null;
        DataTypeJSONObject dataTypeJSONObject = (DataTypeJSONObject) dataTypes.get(0);
        return gson.fromJson(dataTypeJSONObject.getSample().toString(), PrivacyData.class);
    }
*/

    /**
     * Clears <code>listPrivacyListDsId</code>, nullifies <code>privacyData</code> and removes any
     * remaining timer callbacks.
     */
/*
    private void deactivate() {
        Log.d(TAG, "privacy deactivated...");
        listPrivacyListDsId.clear();
        privacyData = null;
        handler.removeCallbacks(timer);
    }
*/

    /**
     * Updates the given data source's summary.
     *
     * @param dataSourceClient Data source to update.
     * @param dataType Data type of the summary data.
     * @return Status after the update.
     */
    public Status updateSummary(DataSourceClient dataSourceClient, DataType dataType) {
        Status status = new Status(Status.SUCCESS);
        if(dataSourceClient == null || dataType == null)
            return new Status(Status.INTERNAL_ERROR);
//        if (listPrivacyListDsId.get(dataSourceClient.getDs_id()) == null) {
            status = routingManager.updateSummary(dataSourceClient.getDataSource(), dataType);
            if(status.getStatusCode() == Status.INTERNAL_ERROR)
                return status;
//        }
        return status;
    }
}
