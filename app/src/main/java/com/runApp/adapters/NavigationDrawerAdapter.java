package com.runApp.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.runApp.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Eduard on 01.01.2015.
 */
public class NavigationDrawerAdapter extends BaseAdapter {

    //    private static final int ITEM_VIEW_TYPE_SPACING = 0;
    private static final int ITEM_VIEW_TYPE_NORMAL = 1;

    private Context context;
    private LayoutInflater inflater;
    private final String[] navDrawerItems;
    private final int[] navDrawerIcons;

    public NavigationDrawerAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        navDrawerItems = context.getResources().getStringArray(R.array.nav_drawer_items);
        TypedArray imgs = context.getResources().obtainTypedArray(R.array.nav_drawer_icons);
        navDrawerIcons = new int[imgs.length()];
        for (int i = 0; i < imgs.length(); i++) {
            navDrawerIcons[i] = imgs.getResourceId(i, -1);
        }
        imgs.recycle();
    }

    @Override
    public int getCount() {
        return navDrawerItems.length;
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
//        if (position == 0) {
//            return ITEM_VIEW_TYPE_SPACING;
//        } else
        return ITEM_VIEW_TYPE_NORMAL;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
//        return getItemViewType(position) != ITEM_VIEW_TYPE_SPACING;
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);
        if (convertView == null) {
//            if (type == ITEM_VIEW_TYPE_SPACING) {
//                convertView = new View(context);
//            } else {
            convertView = inflater.inflate(R.layout.navigation_bar_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
//            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        if (type == ITEM_VIEW_TYPE_NORMAL) {
        holder.icon.setImageResource(navDrawerIcons[position]);
        holder.text.setText(navDrawerItems[position]);
//        FontUtils.getInst().setRobotoRegular(holder.text);
//        } else if (type == ITEM_VIEW_TYPE_SPACING) {
//            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
//                    AbsListView.LayoutParams.MATCH_PARENT,
//                    context.getResources().getDimensionPixelSize(R.dimen.navigation_drawer_spacing)
//                            * (position == 1 ? 1 : 2));
//            convertView.setLayoutParams(lp);
//        }

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.nav_item_icon)
        ImageView icon;

        @InjectView(R.id.nav_item_title)
        TextView text;

        private ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
