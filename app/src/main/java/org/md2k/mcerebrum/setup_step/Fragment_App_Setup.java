package org.md2k.mcerebrum.setup_step;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.md2k.mcerebrum.app.ApplicationManager;
import org.md2k.mcerebrum.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agency.tango.materialintroscreen.SlideFragment;

public class Fragment_App_Setup extends SlideFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_app_setup, container, false);
        /* TODO: reactivate
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
        ApplicationManager applicationManager=new ApplicationManager();
        applicationManager.read(getActivity().getExternalFilesDir(null).toString()+"/temp/mCerebrum/mCerebrum/applications.json");
        AdapterSetup adapterSetup=new AdapterSetup(getActivity(), applicationManager.get());

        // Getting a reference to listview of main.xml layout file
        ListView listView = (ListView) view.findViewById(R.id.listview);

        //  Sample2Activity.startThisActivity(this);

        // Setting the adapter to the listView
        listView.setAdapter(adapterSetup);
        TextView textView = (TextView) view.findViewById(R.id.textview_app_no);
        textView.setText("0/"+String.valueOf(applicationManager.get().length));
        */
        return view;
    }

    @Override
    public int backgroundColor() {
        return R.color.colorBackground;
    }

    @Override
    public int buttonsColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return "Error";
    }
}
    /** Called when the activity is first created. */
/*    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Each row in the list stores country name, currency and flag
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<10;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt", "Country : " + countries[i]);
            hm.put("cur","Currency : " + currency[i]);
            hm.put("flag", Integer.toString(flags[i]) );
            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = { "flag","txt","cur" };

        // Ids of views in listview_layout
        int[] to = { R.id.flag,R.id.txt,R.id.cur};

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_layout, from, to);

        // Getting a reference to listview of main.xml layout file
        ListView listView = ( ListView ) findViewById(R.id.listview);
        //  Sample2Activity.startThisActivity(this);

        // Setting the adapter to the listView
        listView.setAdapter(adapter);

    }

}
*/