package org.md2k.mcerebrum.setup_step;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.CircularProgressButton;

import org.md2k.mcerebrum.app.Application;
import org.md2k.mcerebrum.R;

public class AdapterInstall extends ArrayAdapter<Application> {
    private final Context context;
    private final Application[] applications;

    public AdapterInstall(Context context, Application[] applications) {
        super(context, -1, applications);
        this.context = context;
        this.applications = applications;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listview_app_install, parent, false);
/* TODO: reactivate
        ImageView imageView= (ImageView) rowView.findViewById(R.id.image_app_icon);
        Bitmap b = BitmapFactory.decodeFile(context.getExternalFilesDir(null).toString()+"/temp/mCerebrum/mCerebrum/"+applications[position].getIcon());
        imageView.setImageBitmap(b);
        TextView textView= (TextView) rowView.findViewById(R.id.textview_app_title);
        textView.setText(applications[position].getTitle());
        TextView textView1= (TextView) rowView.findViewById(R.id.textview_summary);
        textView1.setText(applications[position].getSummary());
        CircularProgressButton c= (CircularProgressButton) rowView.findViewById(R.id.circularButton1);
        c.setProgress(1);
        c.setIndeterminateProgressMode(true);
*/
        return rowView;
    }
}
