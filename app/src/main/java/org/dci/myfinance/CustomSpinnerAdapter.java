package org.dci.myfinance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomSpinnerAdapter extends BaseAdapter {

    private final List<String> categoriesList;
    LayoutInflater inflater;

    public CustomSpinnerAdapter(Context context, List<String> categoriesList) {
        super();
        this.categoriesList = categoriesList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return categoriesList.size();
    }

    @Override
    public String getItem(int position) {
        return categoriesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        }
        view.setFocusable(false);
        view.setFocusableInTouchMode(false);
        String category = categoriesList.get(position);
        TextView spinnerElement = view.findViewById(R.id.spinnerElement);
        spinnerElement.setText(category);
        return view;
    }
}