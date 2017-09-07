package org.md2k.mcerebrum.UI.app_settings;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapText.Builder;
import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.ramotion.foldingcell.FoldingCell;

import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.app.Application;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */
public class FoldingCellListAdapterAppSettings extends ArrayAdapter<Application> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;
    private ResponseCallBack responseCallBack;
    Activity activity;


    public FoldingCellListAdapterAppSettings(Activity activity, List<Application> applications, ResponseCallBack responseCallBack) {
        super(activity, 0, applications);
        this.activity = activity;
        this.responseCallBack = responseCallBack;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // get item for selected view
        final Application application = getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell_app_settings, parent, false);
            viewHolder.title = (TextView) cell.findViewById(R.id.textview_title);
            viewHolder.summary = (TextView) cell.findViewById(R.id.textview_description);
            viewHolder.icon_short = (ImageView) cell.findViewById(R.id.imageview_icon_short);
            viewHolder.icon_long = (ImageView) cell.findViewById(R.id.imageview_icon_long);

            viewHolder.content_title = (TextView) cell.findViewById(R.id.textview_content_title);
            viewHolder.content_summary = (TextView) cell.findViewById(R.id.textview_content_description);
            viewHolder.description = (TextView) cell.findViewById(R.id.textview_content_detail);
            viewHolder.version = (TextView) cell.findViewById(R.id.textview_version_number);
            viewHolder.updateVersion = (TextView) cell.findViewById(R.id.textview_update_date);

            viewHolder.buttonSettingsLong = (BootstrapButton) cell.findViewById(R.id.button_settings_long);
            viewHolder.buttonRunLong = (BootstrapButton) cell.findViewById(R.id.button_run_long);
            viewHolder.buttonReportLong = (BootstrapButton) cell.findViewById(R.id.button_report_long);
            viewHolder.buttonSettingsShort = (BootstrapButton) cell.findViewById(R.id.button_settings_short);
            viewHolder.buttonRunShort = (BootstrapButton) cell.findViewById(R.id.button_run_short);
            viewHolder.buttonReportShort = (BootstrapButton) cell.findViewById(R.id.button_report_short);
            viewHolder.status = (AwesomeTextView) cell.findViewById(R.id.textview_status);


            cell.setTag(viewHolder);
        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }
        // bind data from selected element to view through view holder
        viewHolder.title.setText(application.getTitle());
        viewHolder.summary.setText(application.getSummary());
        viewHolder.content_title.setText(application.getTitle());
        viewHolder.content_summary.setText(application.getSummary());
        viewHolder.description.setText(application.getDescription());
        String versionName = application.getCurrentVersionName();
        if (versionName == null) versionName = "N/A";
        viewHolder.version.setText(versionName);
        viewHolder.icon_short.setImageDrawable(application.getIcon(getContext()));
        viewHolder.icon_long.setImageDrawable(application.getIcon(getContext()));

        View.OnClickListener onClickListenerRun = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseCallBack.onResponse(position, FragmentFoldingUIAppSettings.RUN);
            }
        };
        View.OnClickListener onClickListenerSettings = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseCallBack.onResponse(position, FragmentFoldingUIAppSettings.CONFIGURE);
            }
        };
        View.OnClickListener onClickListenerReport = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                responseCallBack.onResponse(position, FragmentFoldingUIAppSettings.REPORT);
            }
        };

        if(!application.isConfigurable())
            set(viewHolder.buttonSettingsLong, viewHolder.buttonSettingsShort, false, DefaultBootstrapBrand.SECONDARY, true, onClickListenerSettings);
        else if(application.isConfigured())
            set(viewHolder.buttonSettingsLong, viewHolder.buttonSettingsShort, true, DefaultBootstrapBrand.SUCCESS, true, onClickListenerSettings);
        else
            set(viewHolder.buttonSettingsLong, viewHolder.buttonSettingsShort, true, DefaultBootstrapBrand.SUCCESS, false, onClickListenerSettings);

        if(!application.hasReport())
            set(viewHolder.buttonReportLong, viewHolder.buttonReportShort, false, DefaultBootstrapBrand.SECONDARY, true, onClickListenerReport);
        else
            set(viewHolder.buttonReportLong, viewHolder.buttonReportShort, true, DefaultBootstrapBrand.SUCCESS, true, onClickListenerReport);

        if(!application.isRunInBackground())
            set(viewHolder.buttonRunLong, viewHolder.buttonRunShort, false, DefaultBootstrapBrand.SECONDARY, true, onClickListenerRun);
        else if(application.isRunning()){
            set(viewHolder.buttonRunLong, viewHolder.buttonRunShort, true, DefaultBootstrapBrand.DANGER, true, onClickListenerRun);
            viewHolder.buttonRunLong.setFontAwesomeIcon("fa_stop");
            viewHolder.buttonRunShort.setFontAwesomeIcon("fa_stop");
        }else{
            set(viewHolder.buttonRunLong, viewHolder.buttonRunShort, true, DefaultBootstrapBrand.SUCCESS, true, onClickListenerRun);
            viewHolder.buttonRunLong.setFontAwesomeIcon("fa_play");
            viewHolder.buttonRunShort.setFontAwesomeIcon("fa_play");
        }
        if(application.isRunning()) {
            viewHolder.status.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
            CharSequence time=convertTimeStampToDateTime(application.getRunningTime());
            viewHolder.status.setBootstrapText(new Builder(getContext()).addText(time).build());
        }else if(application.isConfigurable() && application.isConfigured()){
            viewHolder.status.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            viewHolder.status.setText("configured");
        }else if(application.isConfigurable()){
            viewHolder.status.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            viewHolder.status.setText("not configured");
        }else{
            viewHolder.status.setText("");
        }
        viewHolder.updateVersion.setText("N/A");

        //   viewHolder.requestsCount.setText(String.valueOf(item.getRequestsCount()));
        //     viewHolder.pledgePrice.setText(item.getPledgePrice());
        // set custom btn handler for list item from that item

  /*      if (app.getRequestBtnClickListener() != null) {
            viewHolder.contentRequestBtn.setOnClickListener(app.getRequestBtnClickListener());
        } else {
            // (optionally) add "default" handler if no handler found in item
            viewHolder.contentRequestBtn.setOnClickListener(defaultRequestBtnClickListener);
        }
*/

        return cell;
    }
    public static String convertTimeStampToDateTime(long timestamp){
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date currenTimeZone = calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
            return "";
        }
    }

    void set(BootstrapButton b1, BootstrapButton b2, boolean e, BootstrapBrand b, boolean o, View.OnClickListener l){
        b1.setEnabled(e);
        b1.setBootstrapBrand(b);
        b1.setShowOutline(o);
        b1.setOnClickListener(l);
        b2.setEnabled(e);
        b2.setBootstrapBrand(b);
        b2.setShowOutline(o);
        b2.setOnClickListener(l);
    }

    // simple methods for register cell state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }

    public View.OnClickListener getDefaultRequestBtnClickListener() {
        return defaultRequestBtnClickListener;
    }

    public void setDefaultRequestBtnClickListener(View.OnClickListener defaultRequestBtnClickListener) {
        this.defaultRequestBtnClickListener = defaultRequestBtnClickListener;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView summary;
        AwesomeTextView status;
        ImageView icon_short;
        ImageView icon_long;
        TextView content_title;
        TextView content_summary;
        TextView description;
        TextView version;
        TextView updateVersion;
        BootstrapButton buttonSettingsLong;
        BootstrapButton buttonReportLong;
        BootstrapButton buttonRunLong;
        BootstrapButton buttonSettingsShort;
        BootstrapButton buttonReportShort;
        BootstrapButton buttonRunShort;
    }
}
