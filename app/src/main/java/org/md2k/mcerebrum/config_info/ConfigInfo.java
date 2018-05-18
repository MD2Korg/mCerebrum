package org.md2k.mcerebrum.config_info;
/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
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

import android.content.Context;

import io.paperdb.Paper;

public class ConfigInfo {
    private static final String _ID ="CONFIG";
    private String cid;
    private String type;
    private String title;
    private String summary;
    private String description;
    private String version;
    private String updates;
    private String expectedVersion;
    private String latestVersion;
    private String downloadFrom;
    private String downloadLink;

    public ConfigInfo(String cid, String type, String title, String summary, String description, String version, String updates, String expectedVersion, String latestVersion, String downloadFrom, String downloadLink) {
        this.cid = cid;
        this.type = type;
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.version = version;
        this.updates = updates;
        this.expectedVersion = expectedVersion;
        this.latestVersion = latestVersion;
        this.downloadFrom = downloadFrom;
        this.downloadLink = downloadLink;
    }

    public static void deleteAll(){
        Paper.book().delete(_ID);
    }
    private static ConfigInfo read(){
        return Paper.book().read(_ID, new ConfigInfo(null,null,null,null,null,null,null,null,null,null, null));
    }
    public static void write(ConfigInfo ccInfo){
        Paper.book().write(_ID, ccInfo);
    }

    public static String getTitle() {
        return read().title;
    }

    public static void setDownloadFrom(String downloadFrom) {
        ConfigInfo info = read();
        info.downloadFrom = downloadFrom;
        write(info);
    }

    public static String getDownloadFrom(Context context) {
        return read().downloadFrom;
    }

    public static String getVersion() {
        return read().version;
    }

    public static String getType() {
        return read().type;
    }

    public static String getCid() {
        return read().cid;
    }
}
