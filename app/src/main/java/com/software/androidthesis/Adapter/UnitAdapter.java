package com.software.androidthesis.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.software.androidThesis.R;
import com.software.androidthesis.Activity.WordListActivity;

import java.util.List;
import java.util.Map;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/14 23:29
 * @Decription:
 */
// UnitAdapter
public class UnitAdapter extends BaseAdapter {
    private Context context;
    private List<String> units;
    private LayoutInflater inflater;
    private String bookName;

    public UnitAdapter(Context context, List<String> units, String bookName) {
        this.context = context;
        this.units = units;
        this.bookName = bookName;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return units.size();
    }

    @Override
    public Object getItem(int position) {
        return units.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_unit, parent, false);
        }

        TextView unitName = convertView.findViewById(R.id.unit_name);
        unitName.setText(units.get(position));

        return convertView;
    }
}
