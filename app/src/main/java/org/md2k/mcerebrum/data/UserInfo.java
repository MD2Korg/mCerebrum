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

public class UserInfo {
    private static final String TITLE=UserInfo.class.getSimpleName()+"_TITLE";
    private static final String LOGGED_IN=UserInfo.class.getSimpleName()+"_LOGGED_IN";
    public static void save(Context context, String title, boolean isLoggedIn){
        setTitle(context, title);
        setLoggedIn(context, isLoggedIn);
    }

    private static void setTitle(Context context, String value) {
        if(value==null) value="<not defined>";
        new MySharedPreference().set(context, TITLE, value);
    }
    private static void setLoggedIn(Context context, boolean value) {
        new MySharedPreference().set(context, LOGGED_IN, value);
    }
    public String getTitle(Context context){
        return new MySharedPreference().getString(context, TITLE, "<not_defined>");
    }
    public boolean isLoggedIn(Context context){
        return new MySharedPreference().getBoolean(context,LOGGED_IN, false);
    }
}
