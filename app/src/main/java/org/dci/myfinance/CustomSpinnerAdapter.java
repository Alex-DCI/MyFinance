package org.dci.myfinance;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomSpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<String> categoriesList;
    private LayoutInflater inflater;
    private static final int MAX_WIDTH_DP = 200;

    public CustomSpinnerAdapter(Context applicationContext, List<String> categoriesList) {
        this.context = applicationContext;
        this.categoriesList = categoriesList;
        inflater = LayoutInflater.from(applicationContext);
    }

    @Override
    public int getCount() {
        return categoriesList.size();
    }

    @Override
    public String getItem(int i) {
        return categoriesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
            holder = new ViewHolder();
            holder.category = convertView.findViewById(R.id.spinnerElement);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String category = categoriesList.get(position);
        holder.category.setText(category);


        int maxWidthPx = (int) (MAX_WIDTH_DP * context.getResources().getDisplayMetrics().density);
        holder.category.setMaxWidth(maxWidthPx);


        holder.category.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.category.setSingleLine(true);
        holder.category.setMarqueeRepeatLimit(-1);
        holder.category.setHorizontallyScrolling(true);
        holder.category.setFocusable(true);
        holder.category.setFocusableInTouchMode(true);
        holder.category.setFreezesText(true);
        holder.category.setSelected(true);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    private static class ViewHolder {
        TextView category;
    }
}
