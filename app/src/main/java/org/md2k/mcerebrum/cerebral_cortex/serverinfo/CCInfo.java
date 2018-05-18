package org.md2k.mcerebrum.cerebral_cortex.serverinfo;
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

import io.paperdb.Paper;

public class CCInfo {
    private static final String _ID ="CC";
    private String url;
    private String userName;
    private String passwordHash;
    private String configFileName;
    private String currentVersion;
    private String latestVersion;

    public CCInfo(String url, String userName, String passwordHash, String configFileName, String currentVersion, String latestVersion) {
        this.url = url;
        this.userName = userName;
        this.passwordHash = passwordHash;
        this.configFileName = configFileName;
        this.currentVersion = currentVersion;
        this.latestVersion = latestVersion;
    }

    public static void deleteAll(){
        Paper.book().delete(_ID);
    }
    public static void setLatestVersion(String latestVersion){
        CCInfo ccInfo = read();
        ccInfo.latestVersion = latestVersion;
        write(ccInfo);
    }
    public static void setCurrentVersion(String currentVersion){
        CCInfo ccInfo = read();
        ccInfo.currentVersion = currentVersion;
        write(ccInfo);
    }
    public static boolean hasUpdate(){
        CCInfo ccInfo = read();
        if(ccInfo==null) return false;
        if(ccInfo.currentVersion==null || ccInfo.latestVersion==null) return false;
        return (!ccInfo.currentVersion.equalsIgnoreCase(ccInfo.latestVersion));
    }
    public static String getCurrentVersion() {
        return read().currentVersion;
    }
    private static CCInfo read(){
        return Paper.book().read(_ID, new CCInfo(null,null,null,null,null,null));
    }
    public static void write(CCInfo ccInfo){
        Paper.book().write(_ID, ccInfo);
    }
    public static String getLatestVersion() {
        return read().latestVersion;
    }
    public static String getPasswordHash() {
        return read().passwordHash;
    }

    public static String getUserName() {
        return read().userName;
    }

    public static String getUrl() {
        return read().url;
    }

    public static String getConfigFileName() {
        return read().configFileName;
    }
}
