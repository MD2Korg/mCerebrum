package org.md2k.mcerebrum.menu;
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
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.md2k.mcerebrum.Constants;
import org.md2k.mcerebrum.R;
import org.md2k.system.constant.MCEREBRUM;
import org.md2k.system.provider.ConfigCP;
import org.md2k.system.provider.StudyCP;
import org.md2k.system.provider.UserCP;

import java.io.IOException;

public abstract class AbstractMenu {
    public static final int MENU_HOME=0;
    public static final int MENU_JOIN = 1;
/*
    public static final int MENU_LOGIN = 3;
    public static final int MENU_LOGOUT = 4;
*/
    public static final int MENU_LEAVE = 5;

    public static final int MENU_APP_ADD_REMOVE = 6;
    public static final int MENU_APP_SETTINGS = 7;
    public static final int MENU_STUDY_START = 8;
    public static final int MENU_CHECK_UPDATE = 9;

    //    public static final int OP_REPORT = 7;
//    public static final int OP_PLOT = 8;
//    public static final int OP_EXPORT_DATA = 9;


//    abstract IProfile[] getHeaderContentType(final Context context, UserInfoManager userInfo, StudyInfo studyInfo, final ResponseCallBack responseCallBack);

    public static IProfile[] getHeaderContent(final Context context, UserCP userCP, StudyCP studyCP, ConfigCP configCP, final ResponseCallBack responseCallBack) {
        switch (configCP.getType().toUpperCase()) {
            case MCEREBRUM.CONFIG.TYPE_FREEBIE:
                return new MenuFreebie().getHeaderContentType(context, userCP, studyCP, responseCallBack);
            case MCEREBRUM.CONFIG.TYPE_CONFIGURED:
                return new MenuConfigured().getHeaderContentType(context, userCP, studyCP, responseCallBack);
            case MCEREBRUM.CONFIG.TYPE_SERVER:
                return new MenuServer().getHeaderContentType(context, userCP, studyCP, responseCallBack);
            default:
                return new MenuFreebie().getHeaderContentType(context, userCP, studyCP, responseCallBack);
        }
    }

    public static IDrawerItem[] getMenuContent(final Context context, StudyCP studyCP, ConfigCP configCP, final ResponseCallBack responseCallBack) {
        switch (configCP.getType().toUpperCase()) {
            case MCEREBRUM.CONFIG.TYPE_FREEBIE:
                return new MenuFreebie().getMenuContent(responseCallBack);
            case MCEREBRUM.CONFIG.TYPE_CONFIGURED:
                return new MenuConfigured().getMenuContent(responseCallBack);
            case MCEREBRUM.CONFIG.TYPE_SERVER:
                return new MenuServer().getMenuContent(responseCallBack);
            default:
                return new MenuFreebie().getMenuContent(responseCallBack);
        }
    }


    static IDrawerItem[] getMenuContent(MenuContent[] menuContent, final ResponseCallBack responseCallBack) {
        IDrawerItem[] iDrawerItems = new IDrawerItem[menuContent.length];
        for (int i = 0; i < menuContent.length; i++) {
            switch (menuContent[i].type) {
                case MenuContent.PRIMARY_DRAWER_ITEM:
                    iDrawerItems[i] = new PrimaryDrawerItem().withName(menuContent[i].name).withIcon(menuContent[i].icon).withIdentifier(menuContent[i].identifier).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            responseCallBack.onResponse(drawerItem, (int) drawerItem.getIdentifier());
                            return false;
                        }
                    });
                    if(menuContent[i].badgeValue>0){
                        ((PrimaryDrawerItem)(iDrawerItems[i])).withBadge(String.valueOf(menuContent[i].badgeValue)).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700));;
                    }
                    break;
                case MenuContent.SECONDARY_DRAWER_ITEM:
                    iDrawerItems[i] = new SecondaryDrawerItem().withName(menuContent[i].name).withIcon(menuContent[i].icon).withIdentifier(menuContent[i].identifier).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            responseCallBack.onResponse(drawerItem, (int) drawerItem.getIdentifier());
                            return false;
                        }
                    });
                    if(menuContent[i].badgeValue>0){
                        ((SecondaryDrawerItem)(iDrawerItems[i])).withBadge(String.valueOf(menuContent[i].badgeValue)).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700));;
                    }
                    break;
                case MenuContent.SECTION_DRAWER_ITEM:
                    iDrawerItems[i] = new SectionDrawerItem().withName(menuContent[i].name).withIdentifier(menuContent[i].identifier).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            responseCallBack.onResponse(drawerItem, (int) drawerItem.getIdentifier());
                            return false;
                        }
                    });
            }
        }
        return iDrawerItems;
    }
    public static Drawable getIcon(Context context, String icon) {
        String filePath = Constants.CONFIG_MCEREBRUM_DIR()+icon;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            if(bitmap!=null)
                return new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception ignored) {

        }
        AssetManager am = context.getAssets();
        try {
            return new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(am.open("mcerebrum.png")));
        } catch (IOException ignored) {
        }
        return null;
    }
    public static Drawable getCoverImage(Context context, String image) {
        String filePath = Constants.CONFIG_MCEREBRUM_DIR()+image;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            if(bitmap!=null)
                return new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception ignored) {

        }
        AssetManager am = context.getAssets();
        try {
            return new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(am.open("header.jpg")));
        } catch (IOException ignored) {
        }
        return null;
    }

}

