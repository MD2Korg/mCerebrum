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

package org.md2k.mcerebrum.datakit.router;

import android.content.Context;
import android.os.Messenger;
import android.util.Log;

import org.md2k.mcerebrum.datakit.logger.DatabaseLogger;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataType;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeDouble;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeFloat;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeFloatArray;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeInt;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeIntArray;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeLong;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeLongArray;
import org.md2k.mcerebrum.core.datakitapi.datatype.RowObject;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSource;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceClient;
import org.md2k.mcerebrum.core.datakitapi.status.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Routes messages and method calls to <code>Publishers</code>, <code>Publisher</code>,
 * <code>MessageSubscriber</code>, and <code>DatabaseSubscriber</code>.
 */
public class RoutingManager {

    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = RoutingManager.class.getSimpleName();

    /** Instance of a <code>RoutingManager</code> object. */
    private static RoutingManager instance;

    /** Android context. */
    private Context context;

    /** <code>DatabaseLogger</code> for interacting with the database. */
    private DatabaseLogger databaseLogger;

    /** List of data source message publishers. */
    private Publishers publishers;

    /**
     * Constructor
     *
     * @throws IOException
     */
    private RoutingManager(Context context) throws IOException {
        Log.d(TAG, "RoutingManager()....constructor()");
        this.context = context;
        databaseLogger = DatabaseLogger.getInstance(context);
        publishers = new Publishers();
    }

    /**
     * Returns this instance of this class.
     *
     * @throws IOException
     */
    public static RoutingManager getInstance(Context context) throws IOException {
        if (instance == null)
            instance = new RoutingManager(context);
        return instance;
    }

    /**
     * Registers the given data source.
     *
     * <p>
     *     If the data source is persistent, the data source gets added to the list of <code>Publisher</code>s
     *     and subscribed to the <code>databaseLogger</code>.
     * </p>
     *
     * @param dataSource Data source to register.
     * @return A <code>DataSourceClient</code> constructed from the given <code>dataSource</code>.
     */
    public DataSourceClient register(DataSource dataSource) {
        DataSourceClient dataSourceClient = registerDataSource(dataSource);
        if (dataSource.isPersistent()) {
            publishers.addPublisher(dataSourceClient.getDs_id(), databaseLogger);
        } else {
            publishers.addPublisher(dataSourceClient.getDs_id());
        }
        return dataSourceClient;
    }

    /**
     * Inserts the given <code>DataType</code> array into the database.
     *
     * @param ds_id Data source identifier.
     * @param dataTypes Array of data to insert.
     * @return The status after insertion.
     */
    public Status insert(int ds_id, DataType[] dataTypes) {
        return publishers.receivedData(ds_id, dataTypes, false);
    }

    /**
     * Inserts the given <code>DataType</code> array into the database.
     *
     * @param ds_id Data source identifier.
     * @param dataTypes Array of high frequency data to insert.
     * @return The status after insertion.
     */
    public Status insertHF(int ds_id, DataTypeDoubleArray[] dataTypes) {
        return publishers.receivedDataHF(ds_id, dataTypes);
    }

    /**
     * Queries the database for data within the given time frame.
     *
     * @param ds_id Data source identifier.
     * @param starttimestamp Beginning of the time frame.
     * @param endtimestamp End of the time frame.
     * @return ArrayList of <code>DataType</code>s that match the query.
     */
    public ArrayList<DataType> query(int ds_id, long starttimestamp, long endtimestamp) {
        return databaseLogger.query(ds_id, starttimestamp, endtimestamp);
    }

    /**
     * Queries the database for the last n samples.
     *
     * @param ds_id Data source identifier.
     * @param last_n_sample Number of samples to return.
     * @return ArrayList of <code>DataType</code>s that match the query.
     */
    public ArrayList<DataType> query(int ds_id, int last_n_sample) {
        return databaseLogger.query(ds_id, last_n_sample);
    }


    /**
     * Returns an arrayList of <code>RowObject</code>s matching the query.
     *
     * @param ds_id Data source identifier.
     * @param limit Maximum number of rows to return.
     * @return
     */
    public ArrayList<RowObject> queryLastKey(int ds_id, int limit) {
        return databaseLogger.queryLastKey(ds_id, limit);
    }


    /**
     * Returns the size of the query in number of rows.
     *
     * @return The number of rows within the query.
     */
    public DataTypeLong querySize() {
        return databaseLogger.querySize();
    }

    /**
     * Unregisters the given data source by removing it from the <code>Publisher</code> array.
     *
     * @param ds_id Data source identifier to remove.
     * @return The status after the given data source has been unregistered.
     */
    public Status unregister(int ds_id) {
        int statusCode = publishers.remove(ds_id);
        return new Status(statusCode);
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
        int statusCode = publishers.subscribe(ds_id, packageName, reply);
        Log.d(TAG, "subscribe_status=" + statusCode + " ds_id=" + ds_id + " package_name=" + packageName);
        return new Status(statusCode);
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
        int statusCode = publishers.unsubscribe(ds_id, packageName, reply);
        return new Status(statusCode);
    }

    /**
     * Finds the given data sources within the database.
     *
     * @param dataSource <code>DataSource</code> to find.
     * @return An arrayList of matching <code>DataSourceClients</code>
     */
    public ArrayList<DataSourceClient> find(DataSource dataSource) {
        ArrayList<DataSourceClient> dataSourceClients = databaseLogger.find(dataSource);
        if (dataSourceClients.size() > 0) {
            for (int i = 0; i < dataSourceClients.size(); i++) {
                if (publishers.isExist(dataSourceClients.get(i).getDs_id())) {
                    int ds_id = dataSourceClients.get(i).getDs_id();
                    DataSourceClient dataSourceClient = new DataSourceClient(ds_id, dataSourceClients.get(i).getDataSource(),
                            new Status(Status.DATASOURCE_ACTIVE));
                    dataSourceClients.set(i, dataSourceClient);
                }
            }
        }
        return dataSourceClients;
    }

    /**
     * Registers the given data source with <code>DatabaseLogger</code>.
     *
     * @param dataSource Data source to register.
     * @return The registered <code>DataSourceClient</code>.
     */
    private DataSourceClient registerDataSource(DataSource dataSource) {
        DataSourceClient dataSourceClient;
        if (dataSource == null || dataSource.getType() == null || dataSource.getApplication().getId() == null)
            dataSourceClient = new DataSourceClient(-1, dataSource, new Status(Status.DATASOURCE_INVALID));
        else {
            ArrayList<DataSourceClient> dataSourceClients = databaseLogger.find(dataSource);
            if (dataSourceClients.size() == 0) {
                dataSourceClient = databaseLogger.register(dataSource);
            } else if (dataSourceClients.size() == 1) {
                dataSourceClient = new DataSourceClient(dataSourceClients.get(0).getDs_id(), dataSourceClients
                        .get(0).getDataSource(), new Status(Status.DATASOURCE_EXIST));
            } else {
                dataSourceClient = new DataSourceClient(-1, dataSource, new Status(Status.DATASOURCE_MULTIPLE_EXIST));
            }
        }
        return dataSourceClient;
    }

    /**
     * Closes the <code>RoutingManager</code>, <code>publishers</code>, and <code>databaseLogger</code>.
     */
    public void close() {
        Log.d(TAG, "RoutingManager()...close()...");
        if (instance != null) {
            publishers.close();
            databaseLogger.close();
            instance = null;
        }
    }

    /**
     * Updates the given data source's summary.
     *
     * @param dataSource Data source to update.
     * @param dataType Data type of the summary data.
     * @return Status after the update.
     */
    public Status updateSummary(DataSource dataSource, DataType dataType) {
        Status status = null;
        for(int i = 0; i < 4; i++) {
            int ds_id = registerSummary(dataSource, i);
            long updatedTimestamp = getUpdatedTimestamp(dataType.getDateTime(), i);
            ArrayList<DataType> dataTypeLast = query(ds_id, 1);

            if(dataTypeLast.size() == 0){
                status = publishers.receivedData(ds_id, new DataType[]{createDataType(dataType, null,
                    updatedTimestamp)}, false);

            }else if(i == 0){
                status = publishers.receivedData(ds_id, new DataType[]{createDataType(dataType, dataTypeLast.get(0),
                    updatedTimestamp)}, true);

            }else if (dataTypeLast.get(0).getDateTime() != updatedTimestamp) {
                status = publishers.receivedData(ds_id, new DataType[]{createDataType(dataType, null,
                    updatedTimestamp)}, false);

            } else {
                status = publishers.receivedData(ds_id, new DataType[]{createDataType(dataType, dataTypeLast.get(0),
                    updatedTimestamp)}, true);
            }
        }
        return status;
    }

    /**
     * Registers a new <code>DataSourceClient</code> summary.
     *
     * @param dataSource <code>DataSource</code> to summarize.
     * @param now Determines what kind of summary is being registered.
     * @return The <code>DataSource</code> identifier associated with the summary.
     */
    private int registerSummary(DataSource dataSource, int now){
        String type = dataSource.getType();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(dataSource);
        switch(now) {
            case 0: dataSourceBuilder = dataSourceBuilder.setType(type + "_SUMMARY_TOTAL");
                break;
            case 1: dataSourceBuilder = dataSourceBuilder.setType(type + "_SUMMARY_MINUTE");
                break;
            case 2: dataSourceBuilder = dataSourceBuilder.setType(type + "_SUMMARY_HOUR");
                break;
            case 3: dataSourceBuilder = dataSourceBuilder.setType(type + "_SUMMARY_DAY");
                break;
            default:
        }
        DataSourceClient dataSourceClient = register(dataSourceBuilder.build());
        return dataSourceClient.getDs_id();
    }


    /**
     * Creates new <code>DataType</code> objects containing the current sample plus the previous sample.
     *
     * @param dataType The current sample.
     * @param dataTypeLast The last sample collected.
     * @param time Time the sample was collected.
     * @return The created <code>DataType</code>.
     */
    private DataType createDataType(DataType dataType, DataType dataTypeLast, long time){
        if(dataType instanceof DataTypeDouble) {
            double sampleCur = ((DataTypeDouble) dataType).getSample();
            double sampleLast = 0;
            if(dataTypeLast != null) {
                sampleLast = ((DataTypeDouble) dataTypeLast).getSample();
            }
            return new DataTypeDouble(time, sampleCur + sampleLast);
        }
        if(dataType instanceof DataTypeDoubleArray) {
            double[] sampleCur = ((DataTypeDoubleArray) dataType).getSample();
            double[] sampleFinal = new double[sampleCur.length];
            System.arraycopy(sampleCur, 0, sampleFinal, 0, sampleCur.length);
            if(dataTypeLast != null) {
                double[] sampleLast = ((DataTypeDoubleArray) dataTypeLast).getSample();
                for(int i = 0; i < sampleLast.length; i++)
                    sampleFinal[i] += sampleLast[i];
            }
            return new DataTypeDoubleArray(time, sampleFinal);
        }
        if(dataType instanceof DataTypeInt) {
            int sampleCur = ((DataTypeInt) dataType).getSample();
            int sampleLast = 0;
            if(dataTypeLast != null) {
                sampleLast = ((DataTypeInt) dataTypeLast).getSample();
            }
            return new DataTypeInt(time, sampleCur + sampleLast);
        }
        if(dataType instanceof DataTypeIntArray) {
            int[] sampleCur = ((DataTypeIntArray) dataType).getSample();
            int[] sampleFinal = new int[sampleCur.length];
            System.arraycopy(sampleCur, 0, sampleFinal, 0, sampleCur.length);
            if(dataTypeLast != null) {
                if(dataTypeLast instanceof DataTypeInt)
                    sampleCur = null;
                int[] sampleLast = ((DataTypeIntArray) dataTypeLast).getSample();
                for(int i = 0; i < sampleLast.length; i++)
                    sampleFinal[i] += sampleLast[i];
            }
            return new DataTypeIntArray(time, sampleFinal);
        }
        if(dataType instanceof DataTypeLong) {
            long sampleCur = ((DataTypeLong) dataType).getSample();
            long sampleLast = 0;
            if(dataTypeLast != null) {
                sampleLast = ((DataTypeLong) dataTypeLast).getSample();
            }
            return new DataTypeLong(time, sampleLast+sampleCur);
        }
        if(dataType instanceof DataTypeLongArray) {
            long[] sampleCur = ((DataTypeLongArray) dataType).getSample();
            long[] sampleFinal = new long[sampleCur.length];
            System.arraycopy(sampleCur, 0, sampleFinal, 0, sampleCur.length);
            if(dataTypeLast != null) {
                long[] sampleLast = ((DataTypeLongArray) dataTypeLast).getSample();
                for(int i = 0; i < sampleLast.length; i++)
                    sampleFinal[i] += sampleLast[i];
            }
            return new DataTypeLongArray(time, sampleFinal);
        }
        if(dataType instanceof DataTypeFloat) {
            float sampleCur = ((DataTypeFloat) dataType).getSample();
            float sampleLast = 0;
            if(dataTypeLast != null) {
                sampleLast = ((DataTypeFloat) dataTypeLast).getSample();
            }
            return new DataTypeFloat(time, sampleCur + sampleLast);
        }
        if(dataType instanceof DataTypeFloatArray) {
            float[] sampleCur = ((DataTypeFloatArray) dataType).getSample();
            float[] sampleFinal = new float[sampleCur.length];
            System.arraycopy(sampleCur, 0, sampleFinal, 0, sampleCur.length);
            if(dataTypeLast!=null) {
                float[] sampleLast = ((DataTypeFloatArray) dataTypeLast).getSample();
                for(int i = 0; i < sampleLast.length; i++)
                    sampleFinal[i] += sampleLast[i];
            }
            return new DataTypeFloatArray(time, sampleFinal);
        }
        return null;
    }

    /**
     * Updates the given timestamp to the current time in milliseconds.
     *
     * @param curTime Timestamp to update.
     * @param summaryType Type of timestamp to return.
     * @return The current timestamp.
     */
    private long getUpdatedTimestamp(long curTime, int summaryType) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(curTime);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        switch(summaryType){
            case 1:
                return c.getTimeInMillis();
            case 2:
                c.set(Calendar.MINUTE, 0);
                return c.getTimeInMillis();
            case 3:
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.HOUR_OF_DAY, 0);
                return c.getTimeInMillis();
            default:
                return curTime;
        }
    }
}
