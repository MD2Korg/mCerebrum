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
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.ramotion.foldingcell.FoldingCell;

import org.md2k.mcerebrum.R;
import org.md2k.mcerebrum.app.Application;

import java.util.HashSet;
import java.util.List;

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
            viewHolder.buttonOpenLong = (BootstrapButton) cell.findViewById(R.id.button_open_long);
            viewHolder.buttonSettingsShort = (BootstrapButton) cell.findViewById(R.id.button_settings_short);
            viewHolder.buttonRunShort = (BootstrapButton) cell.findViewById(R.id.button_run_short);
            viewHolder.buttonOpenShort = (BootstrapButton) cell.findViewById(R.id.button_open_short);
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

        viewHolder.buttonSettingsLong.setEnabled(true);
        viewHolder.buttonSettingsLong.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
        viewHolder.buttonSettingsShort.setEnabled(true);
        viewHolder.buttonSettingsShort.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);

        viewHolder.buttonRunLong.setEnabled(true);
        viewHolder.buttonRunLong.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
        viewHolder.buttonRunShort.setEnabled(true);
        viewHolder.buttonRunShort.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);

        viewHolder.buttonOpenLong.setEnabled(true);
        viewHolder.buttonOpenLong.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
        viewHolder.buttonOpenShort.setEnabled(true);
        viewHolder.buttonOpenShort.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
        viewHolder.status.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
        viewHolder.status.setFontAwesomeIcon("fa-check-square");
//            viewHolder.status.setText("installed");
//            viewHolder.status.setTextColor(Color.GREEN);
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
        View.OnClickListener onClickListenerOpen = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                responseCallBack.onResponse(position, FragmentFoldingUIAppSettings.OPEN);
            }
        };

        viewHolder.buttonOpenLong.setOnClickListener(onClickListenerOpen);
        viewHolder.buttonOpenShort.setOnClickListener(onClickListenerOpen);
        viewHolder.buttonSettingsLong.setOnClickListener(onClickListenerSettings);
        viewHolder.buttonSettingsShort.setOnClickListener(onClickListenerSettings);

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
        BootstrapButton buttonOpenLong;
        BootstrapButton buttonRunLong;
        BootstrapButton buttonSettingsShort;
        BootstrapButton buttonOpenShort;
        BootstrapButton buttonRunShort;
    }
}
