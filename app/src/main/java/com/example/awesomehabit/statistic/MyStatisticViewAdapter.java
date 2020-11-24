package com.example.awesomehabit.statistic;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awesomehabit.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

public class MyStatisticViewAdapter extends RecyclerView.Adapter<MyStatisticViewAdapter.MyViewHolder> {

    private ArrayList<ArrayList<Float>> arrayListData;
    private ArrayList<ArrayList<String>> arrayListShortDay;
    private ArrayList<ArrayList<Calendar>> arrayListLongDay;

    private ArrayList<Float> listData;
    private ArrayList<String> listShortDay;
    private ArrayList<Calendar> listLongDay;
    private float mode = 7f;
    private int statisticType = 0;

    private Context context;

    public MyStatisticViewAdapter(Context context, ArrayList<ArrayList<Float>> arrayListData, ArrayList<ArrayList<String>> arrayListShortDay, ArrayList<ArrayList<Calendar>> arrayListLongDay, float mode, int statisticType) {
        this.arrayListData = arrayListData;
        this.arrayListShortDay = arrayListShortDay;
        this.arrayListLongDay = arrayListLongDay;
        this.mode = mode;
        this.context = context;
        this.statisticType = statisticType;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_statistic_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        listData = arrayListData.get(position);
        listLongDay = arrayListLongDay.get(position);
        listShortDay = arrayListShortDay.get(position);

        createBarChart(holder.barChart, holder.listView);
        initListView(holder.listView);
        setNameForBarChart(holder.textView);
    }

    private void setNameForBarChart(TextView textView) {
            if (mode == StatisticActivity.YEAR_MODE)
                textView.setText("Năm " + String.valueOf(listLongDay.get(0).get(Calendar.YEAR)));
            else
                textView.setText("Tháng " + String.valueOf(listLongDay.get(0).get(Calendar.MONTH) + 1));
    }

    @Override
    public int getItemCount() {
        return arrayListLongDay.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        BarChart barChart;
        ListView listView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            barChart = (BarChart) itemView.findViewById(R.id.barChartStatistic);
            listView = (ListView) itemView.findViewById(R.id.listViewDataDetailForStatistic);
            textView = (TextView) itemView.findViewById(R.id.textViewBarChartName);
        }
    }

    private void initListView(ListView listView) {
        ArrayList<MyDataView> items = new ArrayList<>();
        for (int i = 0; i < listLongDay.size(); i++) {
            items.add(new MyDataView(listLongDay.get(i), listData.get(i), mode, statisticType));
        }
        MyDataViewAdapter adapter = new MyDataViewAdapter(context, items);
        listView.setAdapter(adapter);
    }


    private void createBarChart(BarChart barChart, final ListView listView) {
        setContextForBarChart(barChart);
        barChart.getDescription().setText("");
        barChart.animateY(2000);

        customizeXAxis(barChart);

        barChart.setScaleEnabled(false);
        barChart.setScaleMinima(listShortDay.size() / mode, 0.75f);
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int pos = (int)e.getX();
                listView.setSelection(pos);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void customizeXAxis(BarChart barChart) {
        XAxis x = barChart.getXAxis();
        x.setDrawGridLines(false);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setValueFormatter(new IndexAxisValueFormatter(listShortDay));
        x.setLabelCount(listShortDay.size());
        x.setTextSize(20);

        if (mode == StatisticActivity.MONTH_MODE)
            x.setDrawLabels(false);
        else x.setDrawLabels(true);
    }

    private void setContextForBarChart(BarChart barChart) {
        ArrayList<BarEntry> data = new ArrayList<>();
        for (int i = 0; i < listData.size(); i++)
            data.add(new BarEntry(i, listData.get(i)));

        BarDataSet barDataSet = new BarDataSet(data, getLabel());
        barDataSet.setColors(ColorTemplate.rgb("#039dfc"));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);
        if (mode == StatisticActivity.MONTH_MODE)
            barDataSet.setDrawValues(false);
        else
            barDataSet.setDrawValues(true);

        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
    }

    private String getLabel() {
        if(statisticType == 1)
            return StatisticActivity.WATER_LABEL;
        return StatisticActivity.RUN_LABEL;
    }
}
