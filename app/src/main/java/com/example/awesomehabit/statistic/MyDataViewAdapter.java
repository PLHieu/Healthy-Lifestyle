package com.example.awesomehabit.statistic;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyDataViewAdapter extends ArrayAdapter<MyDataView> {
    public MyDataViewAdapter(Context context, ArrayList<MyDataView> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        MyDataView item = getItem(position);
        convertView = createView(item);

        bindData(item, convertView);

        return convertView;
    }

    private void bindData(MyDataView item, View convertView) {
        ItemViewManager.bindPlease(getContext(), item, convertView);
    }

    private View createView(MyDataView item) {
        View res = ItemViewManager.createItemView(this.getContext(),item);
        return res;
    }
}
