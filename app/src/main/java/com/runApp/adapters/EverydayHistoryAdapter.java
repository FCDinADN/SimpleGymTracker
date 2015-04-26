package com.runApp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.runApp.R;
import com.runApp.models.EverydayActivity;
import com.runApp.models.EverydayEventWrapper;
import com.runApp.utils.LogUtils;
import com.runApp.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by Rares on 25/04/15.
 */
public class EverydayHistoryAdapter extends BaseExpandableListAdapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TODAY_FIRST = 1;
    private static final int TYPE_TODAY_OTHER = 2;
    private static final int TYPE_FIRST = 3;
    private static final int TYPE_OTHER = 4;

    private final List<EverydayEventWrapper> wrappedEventsHeader;
    private ExpandableListView mExpandableListView;
    private final LayoutInflater inflater;
    private final Context mContext;
    //Index used to animate only the piechart opened
    private int animateMeIndex = -10000;

    public EverydayHistoryAdapter(Context context, ArrayList<EverydayActivity> everydayActivities, ExpandableListView expandableListView) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mExpandableListView = expandableListView;
        wrappedEventsHeader = new ArrayList<>();

        Date lastDay = null;
        for (int i = 0; i < everydayActivities.size(); i++) {
            EverydayActivity everydayActivity = everydayActivities.get(i);
            Date date = everydayActivity.getDateDateFormat();
            if (date != null) {
                EverydayEventWrapper eventWrapper = new EverydayEventWrapper();
                String month = new SimpleDateFormat("MMMM yyyy").format(date);
                if (wrappedEventsHeader.size() > 0) {
                    EverydayEventWrapper lastItem = wrappedEventsHeader.get(wrappedEventsHeader.size() - 1);
                    if (!lastItem.headerTitle.equals(month)) {
                        EverydayEventWrapper header = new EverydayEventWrapper();
                        header.isHeader = true;
                        header.headerTitle = month;
                        wrappedEventsHeader.add(header);
//                        Log.e(TAG, "add header: " + date.toString() + "; " + eventModel.getId() + ", " + header.headerTitle + "/" + lastItem.headerTitle);
                    }
                } else {
                    EverydayEventWrapper header = new EverydayEventWrapper();
                    header.isHeader = true;
                    header.headerTitle = month;
                    wrappedEventsHeader.add(header);
                }
                eventWrapper.headerTitle = month;
                eventWrapper.mEverydayActivity = everydayActivity;
                try {
                    eventWrapper.day = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(date));
                } catch (ParseException ignored) {
                }
                if (lastDay == null) {
                    eventWrapper.isFirst = true;
                } else eventWrapper.isFirst = lastDay.compareTo(eventWrapper.day) != 0;
                lastDay = eventWrapper.day;
                wrappedEventsHeader.add(eventWrapper);
            }
        }
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id) {
                animateMeIndex = groupPosition;
                return wrappedEventsHeader.get(groupPosition).isHeader;
            }
        });
    }

    @Override
    public int getGroupCount() {
        return wrappedEventsHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return wrappedEventsHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getGroupTypeCount() {
        return 5;
    }

    @Override
    public int getGroupType(int groupPosition) {
        EverydayEventWrapper wrapper = (EverydayEventWrapper) getGroup(groupPosition);
        if (wrapper.isHeader) {
            return TYPE_HEADER;
        } else if (wrapper.today) {
            if (wrapper.isFirst) {
                return TYPE_TODAY_FIRST;
            } else {
                return TYPE_TODAY_OTHER;
            }
        } else if (wrapper.isFirst) {
            return TYPE_FIRST;
        } else {
            return TYPE_OTHER;
        }
    }

    @Override
    public View getGroupView(final int position, boolean isExpanded, View view, ViewGroup viewGroup) {
        int type = getGroupType(position);
        final EverydayEventWrapper wrapper = ((EverydayEventWrapper) getGroup(position));
        Holder holder = null;
        if (view == null) {
            switch (type) {
                case TYPE_HEADER:
                    view = inflater.inflate(R.layout.item_calendar_header, viewGroup, false);
                    holder = new Holder(view);
                    view.setTag(holder);
                    break;
                case TYPE_FIRST:
                case TYPE_TODAY_FIRST:
                    view = inflater.inflate(R.layout.item_history_2, viewGroup, false);
                    holder = new Holder(view);
                    View background = view.findViewById(R.id.history_item_day_date_container);
                    background.setBackgroundColor(mContext.getResources().getColor(R.color.calendar_day_background));
                    holder.day.setTextColor(mContext.getResources().getColor(R.color.primary_text));
                    holder.dayName.setTextColor(mContext.getResources().getColor(R.color.primary_text));
                    view.setTag(holder);
                    break;
                case TYPE_OTHER:
                case TYPE_TODAY_OTHER:
                    view = inflater.inflate(R.layout.item_history_2, viewGroup, false);
                    holder = new Holder(view);
                    View container = view.findViewById(R.id.history_item_day_date_container);
                    container.setBackgroundColor(mContext.getResources().getColor(R.color.calendar_day_background));
                    view.setTag(holder);
                    break;
            }
        } else {
            holder = (Holder) view.getTag();
        }
        if (holder != null) {
            if (mExpandableListView.isGroupExpanded(position) && type != TYPE_HEADER) {
                holder.arrowDown.setVisibility(View.GONE);
                holder.arrowUp.setVisibility(View.VISIBLE);
            } else if (type != TYPE_HEADER) {
                holder.arrowUp.setVisibility(View.GONE);
                holder.arrowDown.setVisibility(View.VISIBLE);
            }
            switch (type) {
                case TYPE_HEADER: {
                    holder.title.setText(wrapper.headerTitle);
                    holder.title.setClickable(false);
                    break;
                }
                case TYPE_FIRST:
                case TYPE_TODAY_FIRST: {
                    holder.borderPadding.setVisibility(View.GONE);
                    holder.border.setVisibility(View.GONE);
                    holder.title.setText(wrapper.mEverydayActivity.getSteps() + " steps, " + wrapper.mEverydayActivity.getCalories() + " calories burnt.");
                    SimpleDateFormat sdf = new SimpleDateFormat("d");
                    holder.day.setText(sdf.format(wrapper.mEverydayActivity.getDateDateFormat()));
                    sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
                    holder.dayName.setText(sdf.format(wrapper.mEverydayActivity.getDateDateFormat()).substring(0, 3));
                    holder.time.setVisibility(View.GONE);
                    holder.delete.setVisibility(View.GONE);
                    break;
                }
                case TYPE_OTHER:
                case TYPE_TODAY_OTHER: {
                    holder.title.setText(wrapper.mEverydayActivity.getSteps() + " steps, " + wrapper.mEverydayActivity.getCalories() + " calories burnt.");
                    holder.borderPadding.setBackgroundColor(Utils.getContext().getResources().getColor(R.color.calendar_day_background));
                    holder.border.setVisibility(View.VISIBLE);
                    holder.time.setVisibility(View.GONE);
                    holder.delete.setVisibility(View.GONE);
                    break;
                }
            }
        }
        return view;
    }

    private PieChart mChart;

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
        if (view == null) {
            LogUtils.LOGE("getChildView", "view is NULL");
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.item_everyday_history, viewGroup, false);
        } else {
            LogUtils.LOGE("getChildView", "view is NOT NULL");
        }

        EverydayEventWrapper wrapper = ((EverydayEventWrapper) getGroup(groupPosition));

        mChart = ((PieChart) view.findViewById(R.id.pieChart));
        mChart.setUsePercentValues(false);
        mChart.setDescription("");

        mChart.setClickable(false);

        mChart.setCenterTextTypeface(Typeface.createFromAsset(mContext.getAssets(), "Lato-Light.ttf"));

        mChart.setDrawHoleEnabled(true);

        mChart.setHoleRadius(60f);

        mChart.setDrawCenterText(true);

        mChart.setCenterText(wrapper.mEverydayActivity.getCalories() + " from 300");

        mChart.setRotationAngle(270);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(false);

        setData(wrapper.mEverydayActivity.getCalories(), 100);

        mChart.setDrawLegend(false);

//        mChart.animateX(1500);

        if (animateMeIndex == groupPosition) {
            mChart.animateXY(1500, 1500);
            animateMeIndex = -10000;
        }

//         mChart.spin(2000, 0, 360);
//
//        Legend l = mChart.getLegend();
//        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(5f);

//        if (historyLogs.get(groupPosition).getType().equals("Running")) {
//            ((TextView) view.findViewById(R.id.item_friend_exercise_name)).setText("37 mins");
//        } else {
//            ((TextView) view.findViewById(R.id.item_friend_exercise_name)).setText("13 X 40 kg");
//        }
//        ((TextView) view.findViewById(R.id.item_friend_exercise_add_comment)).setText(historyLogs.get(groupPosition).getType());
//        view.findViewById(R.id.item_friend_exercise_add_comment).setBackgroundColor(mFragment.getActivity().getResources().getColor(R.color.white));
//        FontUtils.getInst().setRobotoLight((TextView) view.findViewById(R.id.item_friend_exercise_name));
//        FontUtils.getInst().setRobotoLightItalian((TextView) view.findViewById(R.id.item_friend_exercise_add_comment));
        return view;
    }

    private void setData(float calories, float range) {

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        yVals1.add(new Entry(calories, 0));
        yVals1.add(new Entry(300 - calories, 1));

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < 2; i++)
            xVals.add("");

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(0f);

        dataSet.setColors(new int[]{Color.parseColor("#133860"), Color.parseColor("#D5B855")});

        PieData data = new PieData(xVals, dataSet);
//        data.setValueFormatter(new PercentFormatter());
//        data.setValueTextSize(11f);
//        data.setValueTextColor(Color.WHITE);
//        data.setValueTypeface(tf);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    class Holder {
        @InjectView(R.id.history_item_title)
        TextView title;
        @Optional
        @InjectView(R.id.history_time_layout)
        LinearLayout time;
        @Optional
        @InjectView(R.id.history_item_arrow_down)
        ImageView arrowDown;
        @Optional
        @InjectView(R.id.history_item_arrow_up)
        ImageView arrowUp;
        @Optional
        @InjectView(R.id.history_item_day)
        TextView day;
        @Optional
        @InjectView(R.id.history_item_day_name)
        TextView dayName;
        @Optional
        @InjectView(R.id.history_item_border)
        View border;
        @Optional
        @InjectView(R.id.history_item_border_padding)
        View borderPadding;
        @Optional
        @InjectView(R.id.history_item_delete)
        ImageView delete;

        public Holder(View view) {
            ButterKnife.inject(this, view);
        }
    }

//    private void setAnimation(View viewToAnimate, int position, String type) {
//        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_right);
//        switch (type) {
//            case ADD:
//                //if the bound view wasn't previously displayed on the screen
//                if (position > lastPosition) {
//                    viewToAnimate.startAnimation(animation);
//                    lastPosition = position;
//                }
//                break;
//            case DELETE:
//                animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_right);
//                viewToAnimate.startAnimation(animation);
//                break;
//        }
//    }

}
