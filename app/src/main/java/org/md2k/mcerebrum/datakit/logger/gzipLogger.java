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
import android.content.Context;

import org.md2k.mcerebrum.datakit.Constants;
import org.md2k.mcerebrum.datakit.configuration.Configuration;
import org.md2k.mcerebrum.datakit.configuration.ConfigurationManager;
import org.md2k.mcerebrum.core.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.mcerebrum.core.datakitapi.status.Status;
import org.md2k.mcerebrum.core.datakitapi.time.DateTime;
import org.md2k.mcerebrum.commons.storage_old.FileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * Class for creating gzip files for high frequency data.
 */
public class gzipLogger {

    /** Datetime constant. */
    private static final String C_DATETIME = "datetime";

    /** Sample constant. */
    private static final String C_SAMPLE = "sample";

    /** <code>DataSource</code> identifier constant. */
    private static final String C_DATASOURCE_ID = "datasource_id";

    /** Hashmap of output streams */
    private HashMap<Integer, Writer> outputStreams;

    /** Raw data directory. */
    private String RAWDIR = "";

    /** Timezone offset. */
    private long tz = DateTime.getTimeZoneOffset();

    /**
     * Constructor
     *
     * @param context Android context
     */
    public gzipLogger(Context context) {
        outputStreams = new HashMap<>();
        Configuration configuration = ConfigurationManager.read(context);
        RAWDIR = FileManager.getDirectory(context, FileManager.EXTERNAL_SDCARD_PREFERRED) + Constants.RAW_DIRECTORY;
    }

    /**
     * Compresses high frequency data into gzip files.
     *
     * @param hfValues Array of high frequency content values.
     * @param hfValueCount Number of high frequency values.
     * @return Status after the insertion.
     */
    public Status insert(ContentValues[] hfValues, int hfValueCount) {
        int ds_id;
        DataTypeDoubleArray dta;
        try {
            for (int ii = 0; ii < hfValueCount; ii++) {
                ContentValues value = hfValues[ii];
                ds_id = (int) value.get(C_DATASOURCE_ID);
                dta = DataTypeDoubleArray.fromRawBytes((long) value.get(C_DATETIME), (byte[]) value.get(C_SAMPLE));

                if (!outputStreams.containsKey(ds_id)) {
                    File outputDir = new File(RAWDIR + "raw" + ds_id + "/");
                    outputDir.mkdirs();
                    String date = new SimpleDateFormat("yyyyMMddHH", Locale.US).format(new Date(System.currentTimeMillis()));
                    String filename = date + "_" + ds_id + ".csv.gz";
                    File outputfile = new File(outputDir + "/" + filename);

                    FileOutputStream output;
                    try {
                        output = new FileOutputStream(outputfile, true);
                        Writer writer = new OutputStreamWriter(new GZIPOutputStream(output), "UTF-8");
                        outputStreams.put(ds_id, writer);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return new Status(Status.INTERNAL_ERROR);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return new Status(Status.INTERNAL_ERROR);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new Status(Status.INTERNAL_ERROR);
                    }
                }

                try {

                    outputStreams.get(ds_id).write(dta.getDateTime() + ",");
                    outputStreams.get(ds_id).write("" + tz + ",");
                    double[] samples = dta.getSample();
                    for (int i = 0; i < samples.length - 1; i++)
                        outputStreams.get(ds_id).write(samples[i] + ",");
                    outputStreams.get(ds_id).write(samples[samples.length - 1] + "\n");

                } catch (IOException e) {
                    e.printStackTrace();
                    return new Status(Status.INTERNAL_ERROR);
                }
            }

            for (Map.Entry<Integer, Writer> e : outputStreams.entrySet()) {
                try {
                    e.getValue().close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            outputStreams.clear();
            return new Status(Status.SUCCESS);
        }catch (Exception e){
            return new Status(Status.INTERNAL_ERROR);
        }
    }
}
