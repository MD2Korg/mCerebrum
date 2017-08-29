package org.md2k.mcerebrum.UI.folding_ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ramotion.foldingcell.FoldingCell;

import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.app.Application;
import org.md2k.mcerebrum.app.ApplicationManager;

import java.util.ArrayList;

public class FragmentFoldingUI extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_folding_ui, parent, false);
    }
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        ListView theListView = (ListView) view.findViewById(R.id.listview_folding_ui);
        ApplicationManager applicationManager=new ApplicationManager();
        // prepare elements to display
        final ArrayList<Application> items = applicationManager.getAppList(getContext());


        // add custom btn handler to first list item
/*
        items.get(0).setRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "CUSTOM HANDLER FOR FIRST BUTTON", Toast.LENGTH_SHORT).show();
            }
        });
*/
        final FoldingCellListAdapter adapter = new FoldingCellListAdapter(getActivity(), items);
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
