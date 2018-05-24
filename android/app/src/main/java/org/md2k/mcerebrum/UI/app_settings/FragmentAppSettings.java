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

import org.md2k.mcerebrum.MainApplication;
import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.core.access.appinfo.AppAccess;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.core.constant.MCEREBRUM;
import org.md2k.mcerebrum.system.appinfo.AppInstall;
import org.md2k.mcerebrum.system.appinfo.BroadCastMessage;

import java.util.ArrayList;

public class FragmentAppSettings extends Fragment {
    public static final int CONFIGURE = 0;
    public static final int LAUNCH = 1;
    public static final int CLEAR = 2;
    CellAppSettings adapter;
    AwesomeTextView textViewConfigured;
    AwesomeTextView textViewNotConfigured;
    AwesomeTextView textViewStatus;
    ArrayList<String> packageNames;
    boolean flag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_folding_ui_app_settings, parent, false);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppInstall.set(MainApplication.getContext());
        AppAccess.set(MainApplication.getContext());
        LocalBroadcastManager.getInstance(MainApplication.getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(MCEREBRUM.APP_ACCESS.APPCP_CHANGED));
        if (flag == true) {
            BroadCastMessage.send(getActivity(), MCEREBRUM.APP_ACCESS.OP_DATAKIT_STOP);
            flag = false;
        }
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
        packageNames = AppBasicInfo.get(MainApplication.getContext());
        textViewConfigured = (AwesomeTextView) view.findViewById(R.id.textview_configured);
        textViewNotConfigured = (AwesomeTextView) view.findViewById(R.id.textview_not_configured);
        textViewStatus = (AwesomeTextView) view.findViewById(R.id.textview_status);
        // prepare elements to display
        updateTextViewStatus();
        createAdapter();
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

    private void createAdapter() {
        adapter = new CellAppSettings(getActivity(), packageNames, new ResponseCallBack() {
            @Override
            public void onResponse(int position, int operation) {
                if (operation == CONFIGURE) {
                    flag = true;
                    AppAccess.configure(MainApplication.getContext(), packageNames.get(position));
                } else if (operation == LAUNCH) {
                    flag = true;
                    AppAccess.launch(MainApplication.getContext(), packageNames.get(position));
                } else if (operation == CLEAR) {
                    flag = true;
                    AppAccess.clear(MainApplication.getContext(), packageNames.get(position));
                }
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

    void updateTextViewStatus() {
        BootstrapText bootstrapTextS;
        BootstrapText bootstrapTextC = new BootstrapText.Builder(MainApplication.getContext()).addText("configured : " + String.valueOf(AppAccess.getRequiredAppConfigured(MainApplication.getContext()).size())).build();
        BootstrapText bootstrapTextN = new BootstrapText.Builder(MainApplication.getContext()).addText("not configured : " + String.valueOf(AppAccess.getRequiredAppNotConfigured(MainApplication.getContext()).size())).build();
        textViewConfigured.setBootstrapText(bootstrapTextC);
        textViewNotConfigured.setBootstrapText(bootstrapTextN);
        if (AppAccess.getRequiredAppNotConfigured(MainApplication.getContext()).size() == 0) {
            bootstrapTextS = new BootstrapText.Builder(MainApplication.getContext()).addText("Status: ").addFontAwesomeIcon("fa_check").build();
            textViewStatus.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            textViewStatus.setBootstrapText(bootstrapTextS);

/*
            bootstrapButtonInstall.setEnabled(false);
            bootstrapButtonInstall.setBootstrapBrand(DefaultBootstrapBrand.SEONDARY);
            bootstrapButtonInstall.setShowOutline(true);
*/

        } else {
            bootstrapTextS = new BootstrapText.Builder(MainApplication.getContext()).addText("Status: ").addFontAwesomeIcon("fa_times").build();
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
