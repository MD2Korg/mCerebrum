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

package org.md2k.mcerebrum.datakit.message;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;

import org.md2k.mcerebrum.datakit.privacy.PrivacyManager;
import org.md2k.mcerebrum.core.datakitapi.Constants;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataType;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeLong;
import org.md2k.mcerebrum.core.datakitapi.datatype.RowObject;
import org.md2k.mcerebrum.core.datakitapi.messagehandler.MessageType;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSource;
import org.md2k.mcerebrum.core.datakitapi.source.datasource.DataSourceClient;
import org.md2k.mcerebrum.core.datakitapi.status.Status;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Controls program flow based on messages.
 */
public class MessageController {

    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = MessageController.class.getSimpleName();

    /** Instance of a <code>MessageController</code> object. */
    private static MessageController instance;

    /** <code>PrivacyManager</code> object. */
    private PrivacyManager privacyManager;

    /**
     * Constructor
     *
     * @throws IOException
     */
    private MessageController(Context context) throws IOException {
        privacyManager = PrivacyManager.getInstance(context);
    }

    /**
     * Returns this instance of the <code>MessageController</code>.
     *
     * <p>
     *   If an instance doesn't already exist, it creates one.
     * </p>
     *
     * @throws IOException
     */
    public static MessageController getInstance(Context context) throws IOException {
        if (instance == null)
            instance = new MessageController(context);
        return instance;
    }

    /**
     * Closes the <code>MessageController</code>.
     *
     * <p>
     *   Logs the closing, closes <code>privacyManager</code>, and sets <code>instance</code> to null.
     * </p>
     */
    public void close(){
        Log.d(TAG, "messageController ... close()...instance=" + instance);
        if(instance!=null) {
            privacyManager.close();
            instance = null;
        }
    }

    /**
     * Executes an action based on the incoming message.
     *
     * <p>
     *     These message types bundle any relevant data and prepare another message.
     *     <ul>
     *         <li><code>REGISTER</code></li>
     *         <li><code>UNREGISTER</code></li>
     *         <li><code>SUBSCRIBE</code></li>
     *         <li><code>UNSUBSCRIBE</code></li>
     *         <li><code>FIND</code></li>
     *         <li><code>QUERYSIZE</code></li>
     *         <li><code>QUERY</code></li>
     *         <li><code>QUERYPRIMARYKEY</code></li>
     *     </ul>
     * </p>
     * <p>
     *     INSERT, INSERT_HIGH_FREQUENCY, and SUMMARY call the corresponding methods in <code>PrivacyManager</code>.
     * </p>
     *
     * @param incomingMessage Message incoming from the handler.
     * @return A new message or null.
     */
    public Message execute(Message incomingMessage) {
        Bundle bundle;
        Status status;
        switch (incomingMessage.what) {
            case MessageType.REGISTER:
                incomingMessage.getData().setClassLoader(DataSource.class.getClassLoader());
                DataSourceClient dataSourceClient = privacyManager.register((DataSource)incomingMessage
                                                                  .getData().getParcelable(DataSource
                                                                  .class.getSimpleName()));
                bundle = new Bundle();
                bundle.putParcelable(DataSourceClient.class.getSimpleName(), dataSourceClient);
                return prepareMessage(incomingMessage, bundle);

            case MessageType.UNREGISTER:
                status = privacyManager.unregister(incomingMessage.getData().getInt(Constants.RC_DSID));
                bundle = new Bundle();
                bundle.putParcelable(Status.class.getSimpleName(), status);
                return prepareMessage(incomingMessage, bundle);

            case MessageType.SUBSCRIBE:
                Status statusSubscribe = privacyManager.subscribe(incomingMessage.getData().getInt(Constants.RC_DSID),
                                incomingMessage.getData().getString(Constants.PACKAGE_NAME), incomingMessage.replyTo);
                bundle = new Bundle();
                bundle.putParcelable(Status.class.getSimpleName(), statusSubscribe);
                return prepareMessage(incomingMessage, bundle);

            case MessageType.UNSUBSCRIBE:
                Status statusUnsubscribe = privacyManager.unsubscribe(incomingMessage.getData().getInt(Constants.RC_DSID),
                                    incomingMessage.getData().getString(Constants.PACKAGE_NAME), incomingMessage.replyTo);
                bundle = new Bundle();
                bundle.putParcelable(Status.class.getSimpleName(), statusUnsubscribe);
                return prepareMessage(incomingMessage, bundle);

            case MessageType.FIND:
                incomingMessage.getData().setClassLoader(DataSource.class.getClassLoader());
                ArrayList<DataSourceClient> dataSourceClients = privacyManager.find((DataSource) incomingMessage.getData()
                        .getParcelable(DataSource.class.getSimpleName()));
                bundle = new Bundle();
                bundle.putParcelableArrayList(DataSourceClient.class.getSimpleName(), dataSourceClients);
                return prepareMessage(incomingMessage, bundle);

            case MessageType.INSERT:
                incomingMessage.getData().setClassLoader(DataType[].class.getClassLoader());
                Parcelable[] parcelables = incomingMessage.getData().getParcelableArray(DataType.class.getSimpleName());
                assert parcelables != null;
                DataType[] dataTypesInsert = new DataType[parcelables.length];
                for(int i = 0; i < parcelables.length; i++)
                    dataTypesInsert[i] = (DataType) parcelables[i];
                privacyManager.insert(incomingMessage.getData().getInt(Constants.RC_DSID), dataTypesInsert);
                return null;

            case MessageType.SUMMARY:
                incomingMessage.getData().setClassLoader(DataType.class.getClassLoader());
                Parcelable parcelableU=incomingMessage.getData().getParcelable(DataType.class.getSimpleName());
                assert parcelableU != null;
                DataType dataTypeU = (DataType) parcelableU;
                privacyManager.updateSummary((DataSourceClient) incomingMessage.getData()
                            .getParcelable(Constants.RC_DATASOURCE_CLIENT), dataTypeU);
                return null;

            case MessageType.INSERT_HIGH_FREQUENCY:
                incomingMessage.getData().setClassLoader(DataTypeDoubleArray[].class.getClassLoader());
                Parcelable[] parcelablesHF=incomingMessage.getData()
                            .getParcelableArray(DataTypeDoubleArray.class.getSimpleName());
                assert parcelablesHF != null;
                DataTypeDoubleArray[] dataTypeDoubleArraysHF = new DataTypeDoubleArray[parcelablesHF.length];
                for(int i = 0; i < parcelablesHF.length; i++)
                    dataTypeDoubleArraysHF[i] = (DataTypeDoubleArray) parcelablesHF[i];
                privacyManager.insertHF(incomingMessage.getData().getInt(Constants.RC_DSID), dataTypeDoubleArraysHF);
                return null;

            case MessageType.QUERYSIZE:
                DataTypeLong object = privacyManager.querySize();
                bundle = new Bundle();
                bundle.putParcelable(DataTypeLong.class.getSimpleName(), object);
                return prepareMessage(incomingMessage, bundle);

            case MessageType.QUERY:
                ArrayList<DataType> dataTypes = null;
                if (incomingMessage.getData().containsKey(Constants.RC_STARTTIMESTAMP))
                    dataTypes = privacyManager.query(incomingMessage.getData().getInt(Constants.RC_DSID),
                            incomingMessage.getData().getLong(Constants.RC_STARTTIMESTAMP),
                            incomingMessage.getData().getLong(Constants.RC_ENDTIMESTAMP));
                else if (incomingMessage.getData().containsKey(Constants.RC_LAST_N_SAMPLE))
                    dataTypes = privacyManager.query(incomingMessage.getData().getInt(Constants.RC_DSID),
                            incomingMessage.getData().getInt(Constants.RC_LAST_N_SAMPLE));
                bundle = new Bundle();
                bundle.putParcelableArrayList(DataType.class.getSimpleName(), dataTypes);
                return prepareMessage(incomingMessage, bundle);

            case MessageType.QUERYPRIMARYKEY:
                ArrayList<RowObject> objectTypes;
                objectTypes = privacyManager.queryLastKey(incomingMessage.getData().getInt(Constants.RC_DSID),
                        incomingMessage.getData().getInt(Constants.RC_LIMIT));
                bundle = new Bundle();
                bundle.putParcelableArrayList(RowObject.class.getSimpleName(), objectTypes);
                return prepareMessage(incomingMessage, bundle);
        }
        return null;
    }

    /**
     * Prepares a message from an incoming message and a bundle.
     *
     * @param incomingMessage Message incoming from the handler.
     * @param bundle Bundle of data to add to the message.
     * @return The prepped message.
     */
    public Message prepareMessage(Message incomingMessage, Bundle bundle) {
        Message message = Message.obtain(null, 0, 0, 0);
        message.what = incomingMessage.what;
        message.arg1 = incomingMessage.arg1;
        message.setData(bundle);
        return message;
    }
}
