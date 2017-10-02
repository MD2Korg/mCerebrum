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

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.md2k.system.provider.StudyCP;
import org.md2k.system.provider.UserCP;

class MenuFreebie extends AbstractMenu {
     IProfile[] getHeaderContentType(final Context context, UserCP userCP, StudyCP studyCP, final ResponseCallBack responseCallBack){
        IProfile[] iProfiles=new IProfile[1];
        iProfiles[0]=new ProfileDrawerItem().withName(userCP.getTitle()).withIcon(getIcon(context, studyCP.getIcon()));
/*
        iProfiles[1]=new ProfileSettingDrawerItem().withName("Join Study").withIcon(FontAwesome.Icon.faw_link).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                responseCallBack.onResponse(MENU_JOIN);
                return true;
            }
        });
*/
        return iProfiles;
    }
    IDrawerItem[] getMenuContent(ResponseCallBack responseCallBack){
        return getMenuContent(menuContent, responseCallBack);
    }
    private  MenuContent[] menuContent = new MenuContent[]{
            new MenuContent("Home", FontAwesome.Icon.faw_home, MenuContent.PRIMARY_DRAWER_ITEM, MENU_HOME,0),
            new MenuContent("Add/Remove Apps", FontAwesome.Icon.faw_plus, MenuContent.PRIMARY_DRAWER_ITEM, MENU_APP_ADD_REMOVE,0),
            new MenuContent("App Settings", FontAwesome.Icon.faw_cog, MenuContent.PRIMARY_DRAWER_ITEM, MENU_APP_SETTINGS,0),
            new MenuContent("Check Update", FontAwesome.Icon.faw_refresh, MenuContent.PRIMARY_DRAWER_ITEM, MENU_CHECK_UPDATE,0),
            new MenuContent("Join Study", FontAwesome.Icon.faw_link, MenuContent.PRIMARY_DRAWER_ITEM, MENU_JOIN,0)
//            new MenuContent("Report",FontAwesome.Icon.faw_bar_chart,MenuContent.PRIMARY_DRAWER_ITEM, OP_REPORT),
//            new MenuContent("Plot",FontAwesome.Icon.faw_line_chart,MenuContent.PRIMARY_DRAWER_ITEM, OP_PLOT),
//            new MenuContent("Export Data",FontAwesome.Icon.faw_upload,MenuContent.PRIMARY_DRAWER_ITEM, OP_EXPORT_DATA),
//            new MenuContent("Help",null,MenuContent.SECTION_DRAWER_ITEM, OP_HELP),
//            new MenuContent("Quick Tour",FontAwesome.Icon.faw_film,MenuContent.SECONDARY_DRAWER_ITEM, OP_QUICK_TOUR),
//            new MenuContent("Frequently asked question",FontAwesome.Icon.faw_question_circle,MenuContent.SECONDARY_DRAWER_ITEM),
//            new MenuContent("About",null,MenuContent.SECTION_DRAWER_ITEM, OP_ABOUT),
//            new MenuContent("Who we are",FontAwesome.Icon.faw_users,MenuContent.SECONDARY_DRAWER_ITEM),
//            new MenuContent("Terms and Conditions",FontAwesome.Icon.faw_pencil_square_o,MenuContent.SECONDARY_DRAWER_ITEM),
//            new MenuContent("Privacy Policy",FontAwesome.Icon.faw_lock,MenuContent.SECONDARY_DRAWER_ITEM),
//            new MenuContent("Contact",FontAwesome.Icon.faw_envelope_o,MenuContent.SECONDARY_DRAWER_ITEM),
//            new MenuContent("Feedback",FontAwesome.Icon.faw_comment,MenuContent.SECONDARY_DRAWER_ITEM),
    };

}
