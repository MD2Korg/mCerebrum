package org.md2k.mcerebrum.UI.app_settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.ramotion.foldingcell.FoldingCell;

import org.md2k.mcerebrum.ActivityMain;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.app.AppBasicInfoController;
import org.md2k.mcerebrum.app.AppInfoController;
import org.md2k.mcerebrum.app.ApplicationManager;

import java.util.ArrayList;

public class FragmentAppSettings extends Fragment {
    public static final int CONFIGURE = 0;
    public static final int LAUNCH = 1;
    public static final int CLEAR = 2;
    ApplicationManager applicationManager;
    CellAppSettings adapter;
    ArrayList<AppInfoController> appInfoControllers;
    AwesomeTextView textViewConfigured;
    AwesomeTextView textViewNotConfigured;
    AwesomeTextView textViewStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_folding_ui_app_settings, parent, false);
    }
    @Override
    public void onPause(){
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("connection"));
        applicationManager.setmCerebrumInfo();
        adapter.notifyDataSetChanged();
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            adapter.notifyDataSetChanged();
        }
    };
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        ListView theListView = (ListView) view.findViewById(R.id.listview_folding_ui);
        applicationManager = ((ActivityMain) getActivity()).applicationManager;
        textViewConfigured = (AwesomeTextView) view.findViewById(R.id.textview_configured);
        textViewNotConfigured = (AwesomeTextView) view.findViewById(R.id.textview_not_configured);
        textViewStatus = (AwesomeTextView) view.findViewById(R.id.textview_status);
        final AppInfoController[] apps = applicationManager.getAppInfoControllers();
        // prepare elements to display
        appInfoControllers = new ArrayList<>();
        for (int i = 0; i < apps.length; i++) {
//            if (!apps[i].getInstallInfoController().isInstalled()) continue;
//            if (apps[i].getAppBasicInfoController().isType(AppBasicInfoController.TYPE_MCEREBRUM)) continue;
            appInfoControllers.add(apps[i]);
        }
        updateTextViewStatus();
        adapter = new CellAppSettings(getActivity(), appInfoControllers, new ResponseCallBack() {
            @Override
            public void onResponse(int position, int operation) {
                if (operation == CONFIGURE) {
                    applicationManager.getAppInfoControllers()[position].getmCerebrumController().configure(null);
                }else if (operation == LAUNCH) {
                    applicationManager.getAppInfoControllers()[position].getmCerebrumController().launch(null);
                } else if(operation == CLEAR){
                    applicationManager.getAppInfoControllers()[position].getmCerebrumController().clear(null);
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

/*
    void getInfo(AppBasicInfo application, int requestCode) {
        try {
            Intent intent = new Intent();
            intent.putExtra("REQUEST", Access.REQUEST_INFO);
            intent.setComponent(new ComponentName(application.getPackageName(), application.getPackageName() + ".ActivityMCerebrumAccess"));
            startActivityForResult(intent, requestCode);
        } catch (Exception ignored) {

        }
*/

    /*
            RxActivityResult.on(this).startIntent(intent)
                    .subscribe(new Consumer<Result<FragmentAppSettings>>() {
                        @Override
                        public void accept(Result<FragmentAppSettings> result) throws Exception {
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
    }

*/
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode >= 2000 && requestCode <= 3000) {
                requestCode = requestCode - 2000;
                Info info = data.getParcelableExtra(Access.RESPONSE);
                Log.d("abc", "requestCode="+requestCode);
                applicationManager.getAppMCs()[requestCode].getInfo(info);
            }
        } catch (Exception ignored) {

        }
    }
*/
    void updateTextViewStatus(){
        BootstrapText bootstrapTextS;
        BootstrapText bootstrapTextC = new BootstrapText.Builder(getContext()).addText("configured : " + String.valueOf(applicationManager.getRequiredAppConfigured().size())).build();
        BootstrapText bootstrapTextN = new BootstrapText.Builder(getContext()).addText("not configured : " + String.valueOf(applicationManager.getRequiredAppNotConfigured().size())).build();
        textViewConfigured.setBootstrapText(bootstrapTextC);
        textViewNotConfigured.setBootstrapText(bootstrapTextN);
        if(applicationManager.getRequiredAppNotConfigured().size()==0) {
            bootstrapTextS = new BootstrapText.Builder(getContext()).addText("Status: ").addFontAwesomeIcon("fa_check").build();
            textViewStatus.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            textViewStatus.setBootstrapText(bootstrapTextS);
/*
            bootstrapButtonInstall.setEnabled(false);
            bootstrapButtonInstall.setBootstrapBrand(DefaultBootstrapBrand.SECONDARY);
            bootstrapButtonInstall.setShowOutline(true);
*/
        } else {
            bootstrapTextS = new BootstrapText.Builder(getContext()).addText("Status: ").addFontAwesomeIcon("fa_times").build();
            textViewStatus.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            textViewStatus.setBootstrapText(bootstrapTextS);
/*
            bootstrapButtonInstall.setEnabled(true);
            bootstrapButtonInstall.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            bootstrapButtonInstall.setShowOutline(false);
*/
        }
    }

}
