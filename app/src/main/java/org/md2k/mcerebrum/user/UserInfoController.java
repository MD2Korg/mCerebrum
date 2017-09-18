package org.md2k.mcerebrum.user;
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

import org.md2k.mcerebrum.data.MySharedPreference;
import org.md2k.md2k.system.user.UserInfo;

public class UserInfoController {
    private UserInfo userInfo;

    private static final String TITLE=UserInfoController.class.getSimpleName()+"_TITLE";
    private static final String LOGGED_IN=UserInfoController.class.getSimpleName()+"_LOGGED_IN";

    public UserInfoController(){
        userInfo=new UserInfo();
        set();
    }

    public void set(){
        userInfo.setTitle(new MySharedPreference().getString(TITLE, "<not_defined>"));
        userInfo.setLoggedIn(new MySharedPreference().getBoolean(LOGGED_IN, false));
    }
    public void clear(){
        MySharedPreference mySharedPreference=new MySharedPreference();
        mySharedPreference.clear(TITLE);
        mySharedPreference.clear(LOGGED_IN);
        set();
    }

    public void setTitle( String value) {
        if(value==null) value="<not defined>";
        userInfo.setTitle(value);
        new MySharedPreference().set(TITLE, value);
    }
    public void setLoggedIn(boolean value) {
        userInfo.setLoggedIn(value);
        new MySharedPreference().set(LOGGED_IN, value);
    }
    public String getTitle(){
        return userInfo.getTitle();
    }
    public boolean isLoggedIn(){
        return userInfo.isLoggedIn();
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
