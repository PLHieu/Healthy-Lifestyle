package com.example.awesomehabit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class CustomPageAdapter extends PagerAdapter {
    private Context mContext;
    public CustomPageAdapter(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return 1000;
    }
    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }
    /*
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * {@link #finishUpdate(ViewGroup)}.
     *
     * @param container The containing View in which the page will be shown.
     * @param position  The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        //ModelObject modelObject = ModelObject.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.card_list, collection, false);
        TextView tv=layout.findViewById(R.id.tvPos);
        tv.setText(String.valueOf(position));
        collection.addView(layout);
        return layout;
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

}
