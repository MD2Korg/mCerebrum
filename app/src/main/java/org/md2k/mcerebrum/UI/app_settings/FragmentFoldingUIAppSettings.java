package org.md2k.mcerebrum.UI.app_settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ramotion.foldingcell.FoldingCell;

import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.app.Application;
import org.md2k.mcerebrum.app.ApplicationManager;

import java.util.ArrayList;

public class FragmentFoldingUIAppSettings extends Fragment {
    public static final int INSTALL=0;
    public static final int UNINSTALL=1;
    public static final int UPDATE=2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_folding_ui_app_settings, parent, false);
    }
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        ListView theListView = (ListView) view.findViewById(R.id.listview_folding_ui);
        ApplicationManager applicationManager=new ApplicationManager();
        // prepare elements to display
        final ArrayList<Application> items = applicationManager.getAppList(getContext());
        ArrayList<AppInfo> appInfos=new ArrayList<>();
        for(int i=0;i<items.size();i++){
            if(!items.get(i).isInstalled(getActivity())) continue;
            appInfos.add(new AppInfo(getContext(), items.get(i)));
        }
        // add custom btn handler to first list item
/*
        items.get(0).setRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "CUSTOM HANDLER FOR FIRST BUTTON", Toast.LENGTH_SHORT).show();
            }
        });
*/
        final FoldingCellListAdapterAppSettings adapter = new FoldingCellListAdapterAppSettings(getActivity(), appInfos, new ResponseCallBack() {
            @Override
            public void onResponse(int position, int operation) {
/*
                if(operation==UNINSTALL){
                    items.get(position).uninstall(getActivity(), 1000);
                }else if(operation==INSTALL){
                    if(items.get(position).isInstallFromPlayStore(getActivity()))
                        items.get(position).installPlayStore(getActivity());
                    else {
                        downloadAndInstall(items.get(position));
                    }
                }
*/
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
  }
