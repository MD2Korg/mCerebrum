package org.md2k.mcerebrum.study_info;
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

public class StudyInfo {
    private static final String _ID ="STUDY";
    private String sid;
    private String type;
    private String title;
    private String summary;
    private String description;
    private String version;
    private String icon;
    private String coverImage;
    private boolean startAtBoot;
    private boolean started;

    public StudyInfo(String sid, String type, String title, String summary, String description, String version, String icon, String coverImage, boolean startAtBoot, boolean started) {
        this.sid = sid;
        this.type = type;
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.version = version;
        this.icon = icon;
        this.coverImage = coverImage;
        this.startAtBoot = startAtBoot;
        this.started = started;
    }

    public static void deleteAll(){
        Paper.book().delete(_ID);
    }
    public static void setStarted(boolean started){
        StudyInfo info = read();
        info.started=started;
        write(info);
    }
    private static StudyInfo read(){
        return Paper.book().read(_ID, new StudyInfo(null,null,null,null,null,null,null,null,false,false));
    }
    public static void write(StudyInfo ccInfo){
        Paper.book().write(_ID, ccInfo);
    }

    public static String getTitle() {
        return read().title;
    }
    public static String getIcon() {
        return read().icon;
    }

    public static boolean isStarted() {
        return read().started;
    }

    public static String getCoverImage() {
        return read().coverImage;
    }

    public static boolean isStartAtBoot() {
        return read().startAtBoot;
    }
}
