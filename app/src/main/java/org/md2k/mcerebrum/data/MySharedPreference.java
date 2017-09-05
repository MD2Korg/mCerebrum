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
import android.content.SharedPreferences;

import org.md2k.mcerebrum.MyApplication;


public class MySharedPreference {
    private static final String NAME="MCEREBRUM";
    public void set(String key, String value) {
        SharedPreferences sharedPref = MyApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public void clear(String key) {
        SharedPreferences sharedPref = MyApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
    }

    public void set(String key, int value) {
        SharedPreferences sharedPref = MyApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public void set(String key, boolean value) {
        SharedPreferences sharedPref = MyApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public String getString(String key, String defaultValue) {
        SharedPreferences sharedPref = MyApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(key, defaultValue);
    }
    public void set(String key, String[] value) {
        SharedPreferences sharedPref = MyApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        StringBuilder sb = new StringBuilder();
        for (String aValue : value) {
            sb.append(aValue).append(",");
        }
        editor.putString(key, sb.toString());
        editor.apply();
    }
    public boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences sharedPref = MyApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, defaultValue);
    }
    public int getInt(String key, int defaultValue) {
        SharedPreferences sharedPref = MyApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(key, defaultValue);
    }

}
