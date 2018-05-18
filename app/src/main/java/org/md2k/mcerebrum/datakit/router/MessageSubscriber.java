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

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import org.md2k.mcerebrum.core.datakitapi.datatype.DataType;
import org.md2k.mcerebrum.core.datakitapi.messagehandler.MessageType;

/**
 * Handles message subscribing for the <code>Publisher</code> class.
 */
public class MessageSubscriber{

    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = MessageSubscriber.class.getSimpleName();

    /** Reference to message handler. */
    Messenger reply;

    /** Name of the package the message comes from. */
    String packageName;

    /**
     * Constructor
     *
     * @param packageName Name of the package the message comes from.
     * @param reply Reference to message handler.
     */
    public MessageSubscriber(String packageName, Messenger reply){
        this.packageName = packageName;
        this.reply = reply;
    }

    /**
     * Bundles data and sends it in a message.
     *
     * @param ds_id Data source identifier.
     * @param data Data to be sent.
     * @return Whether the sending was successful or not.
     */
    public boolean update(int ds_id,DataType[] data) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(DataType.class.getSimpleName(), data);
        bundle.putInt("ds_id",ds_id);
        Message message = prepareMessage(bundle, MessageType.SUBSCRIBED_DATA);
        try {
            reply.send(message);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    /**
     * Constructs a message.
     *
     * @param bundle Data to be sent with the message.
     * @param messageType Type of message.
     * @return The prepared message.
     */
    public Message prepareMessage(Bundle bundle, int messageType) {
        Message message = Message.obtain(null, 0, 0, 0);
        message.what = messageType;
        message.setData(bundle);
        return message;
    }
}
