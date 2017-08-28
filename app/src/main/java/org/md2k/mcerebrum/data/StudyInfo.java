package org.md2k.mcerebrum.data;
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

import org.md2k.mcerebrum.configuration.Config;

public class StudyInfo {
    private static final String ID=StudyInfo.class.getSimpleName()+"_ID";
    private static final String TYPE=StudyInfo.class.getSimpleName()+"_TYPE";
    private static final String TITLE=StudyInfo.class.getSimpleName()+"_TITLE";
    private static final String SUMMARY=StudyInfo.class.getSimpleName()+"_SUMMARY";
    private static final String DESCRIPTION=StudyInfo.class.getSimpleName()+"_DESCRIPTION";
    private static final String ICON=StudyInfo.class.getSimpleName()+"_ICON";
    private static final String COVER_IMAGE=StudyInfo.class.getSimpleName()+"_COVER_IMAGE";
    private static final String VERSION=StudyInfo.class.getSimpleName()+"_VERSION";
    private static final String NOT_DEFINED="<not defined>";
    private static final String START_AT_BOOT=StudyInfo.class.getSimpleName()+"_START_AT_BOOT";
    private static final String STARTED=StudyInfo.class.getSimpleName()+"_STARTED";
    public static void save(Context context, Config config) {
        MySharedPreference mySharedPreference=new MySharedPreference();
        mySharedPreference.set(context,ID,config.getId());
        mySharedPreference.set(context,TYPE,config.getType());
        mySharedPreference.set(context,TITLE, config.getTitle());
        mySharedPreference.set(context,SUMMARY, config.getSummary());
        mySharedPreference.set(context,DESCRIPTION, config.getDescription());
        mySharedPreference.set(context,ICON, config.getIcon());
        mySharedPreference.set(context,COVER_IMAGE, config.getCover_image());
        mySharedPreference.set(context,VERSION, config.getVersion());
        mySharedPreference.set(context, START_AT_BOOT, config.isStart_at_boot());
        mySharedPreference.set(context, STARTED, false);
    }

    public String getId(Context context) {
        return new MySharedPreference().getString(context, ID,NOT_DEFINED);
    }

    public String getTitle(Context context) {
        return new MySharedPreference().getString(context, TITLE,NOT_DEFINED);
    }

    public String getSummary(Context context) {
        return new MySharedPreference().getString(context, SUMMARY,NOT_DEFINED);
    }

    public String getDescription(Context context) {
        return new MySharedPreference().getString(context, DESCRIPTION,NOT_DEFINED);
    }

    public String getVersion(Context context) {
        return new MySharedPreference().getString(context, VERSION,NOT_DEFINED);
    }
    public boolean isStartAtBoot(Context context){
        return new MySharedPreference().getBoolean(context, START_AT_BOOT, false);
    }
    public boolean isStarted(Context context){
        return new MySharedPreference().getBoolean(context, STARTED, false);
    }
    public void setStarted(Context context, boolean value){
        new MySharedPreference().set(context, STARTED, value);
    }

}
