package org.md2k.mcerebrum.UI.app_settings;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ramotion.foldingcell.FoldingCell;

import org.md2k.mcerebrum.ActivityMain;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.app.Application;
import org.md2k.mcerebrum.app.ApplicationManager;
import org.md2k.mcerebrum.core.access.Access;
import org.md2k.mcerebrum.core.access.Info;

import java.util.ArrayList;

public class FragmentFoldingUIAppSettings extends Fragment {
    public static final int CONFIGURE = 0;
    public static final int OPEN = 1;
    public static final int RUN = 2;
    public static final int REQUEST_INFO = 2000;
    ApplicationManager applicationManager;
    FoldingCellListAdapterAppSettings adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_folding_ui_app_settings, parent, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        ListView theListView = (ListView) view.findViewById(R.id.listview_folding_ui);
        applicationManager = ((ActivityMain) getActivity()).applicationManager;
        final Application[] applications = applicationManager.getApplications();
        // prepare elements to display
        final ArrayList<Application> appInfos = new ArrayList<>();
        for(int i=0;i<applications.length;i++){
            if (!applications[i].isInstalled()) continue;
            appInfos.add(applications[i]);
            getInfo(applications[i], REQUEST_INFO+i);

        }
        adapter = new FoldingCellListAdapterAppSettings(getActivity(), appInfos, new ResponseCallBack() {
            @Override
            public void onResponse(int position, int operation) {
                if (operation == CONFIGURE) {
                    Intent intent = new Intent();
                    intent.putExtra("REQUEST", Access.REQUEST_CONFIGURE);

                    intent.setComponent(new ComponentName(appInfos.get(position).getPackageName(), appInfos.get(position).getPackageName()+".ActivityMCerebrum"));
                    startActivity(intent);

//                    items.get(position).uninstall(getActivity(), 1000);
                } else if (operation == OPEN) {
//                    if(items.get(position).isInstallFromPlayStore(getActivity()))
//                        items.get(position).installPlayStore(getActivity());
//                    else {
//                        downloadAndInstall(items.get(position));
//                    }
                }
            }
        });
        theListView.setAdapter(adapter);

        // set on click event listener to list view
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // toggle clicked cell state
                ((FoldingCell) view).toggle(false);
                // register in adapter that state for selected cell is toggled
                adapter.registerToggle(pos);
            }
        });
    }

    void getInfo(Application application, int requestCode) {
        try {
            Intent intent = new Intent();
            intent.putExtra("REQUEST", Access.REQUEST_INFO);
            intent.setComponent(new ComponentName(application.getPackageName(), application.getPackageName() + ".ActivityMCerebrum"));
            startActivityForResult(intent, requestCode);
        }catch (Exception ignored){

        }

/*
        RxActivityResult.on(this).startIntent(intent)
                .subscribe(new Consumer<Result<FragmentFoldingUIAppSettings>>() {
                    @Override
                    public void accept(Result<FragmentFoldingUIAppSettings> result) throws Exception {
                        Intent data = result.data();
                        int resultCode = result.resultCode();
                        result.data().get
                        // the requestCode using which the activity is started can be received here.
                        int requestCode = result.requestCode();

*/
/*
                    if(requestCode == YourActivity.YOUR_REQUEST_CODE)
                    {
                        // Do Something
                    }

                    if (resultCode == RESULT_OK) {
//                        result.targetUI().showImage(data);
                    } else {
//                        result.targetUI().printUserCanceled();
                    }
*//*

                    }
                });
*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode >= 2000 && requestCode <= 3000) {
                requestCode = requestCode - 2000;
                Info info = data.getParcelableExtra(Access.RESPONSE);
                Log.d("abc", "abc");
                applicationManager.getApplications()[requestCode].updateStatus(info);
                adapter.notifyDataSetChanged();
            }
        }catch (Exception ignored){

        }
    }

}
