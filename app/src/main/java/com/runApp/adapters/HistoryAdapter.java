package com.runApp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.runApp.R;
import com.runApp.models.EventWrapper;
import com.runApp.models.History;
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
 * Created by Rares on 04/01/15.
 */
public class HistoryAdapter extends BaseExpandableListAdapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TODAY_FIRST = 1;
    private static final int TYPE_TODAY_OTHER = 2;
    private static final int TYPE_FIRST = 3;
    private static final int TYPE_OTHER = 4;

    private final List<EventWrapper> wrappedEventsHeader;
    private ExpandableListView mExpandableListView;
    private final LayoutInflater inflater;
    private final Context mContext;
    private CallBack mCallBack;

    private final String ADD = "add_item";
    private final String DELETE = "delete_item";

    public HistoryAdapter(Context context, ArrayList<History> histories, ExpandableListView expandableListView) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mExpandableListView = expandableListView;
        wrappedEventsHeader = new ArrayList<EventWrapper>();

        Date today = new Date();

        Date lastDay = null;
        for (int i = 0; i < histories.size(); i++) {
            History history = histories.get(i);
            Date date = history.getStartTimeDate();
            if (date != null) {
                EventWrapper eventWrapper = new EventWrapper();
                String month = new SimpleDateFormat("MMMM yyyy").format(date);
                if (wrappedEventsHeader.size() > 0) {
                    EventWrapper lastItem = wrappedEventsHeader.get(wrappedEventsHeader.size() - 1);
                    if (!lastItem.headerTitle.equals(month)) {
                        EventWrapper header = new EventWrapper();
                        header.isHeader = true;
                        header.headerTitle = month;
                        wrappedEventsHeader.add(header);
//                        Log.e(TAG, "add header: " + date.toString() + "; " + eventModel.getId() + ", " + header.headerTitle + "/" + lastItem.headerTitle);
                    }
                } else {
                    EventWrapper header = new EventWrapper();
                    header.isHeader = true;
                    header.headerTitle = month;
                    wrappedEventsHeader.add(header);
                }
                eventWrapper.headerTitle = month;
                eventWrapper.history = history;
                try {
                    eventWrapper.day = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(date));
                } catch (ParseException e) {
                }
                if (lastDay == null) {
                    eventWrapper.isFirst = true;
                } else if (lastDay.compareTo(eventWrapper.day) == 0) {
                    eventWrapper.isFirst = false;
                } else if (lastDay.compareTo(eventWrapper.day) == -1) {
                    eventWrapper.isFirst = true;
                }
                lastDay = eventWrapper.day;
                eventWrapper.today = new SimpleDateFormat("yyyy-MM-dd").format(today).equals(new SimpleDateFormat("yyyy-MM-dd").format(eventWrapper.day));

                wrappedEventsHeader.add(eventWrapper);
            }
        }
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id) {
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
        EventWrapper wrapper = (EventWrapper) getGroup(groupPosition);
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
        final EventWrapper wrapper = ((EventWrapper) getGroup(position));
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
                    if (type == TYPE_TODAY_FIRST) {
                        View background = view.findViewById(R.id.history_item_day_date_container);
                        background.setBackgroundColor(mContext.getResources().getColor(R.color.actionbar_background));
                        holder.day.setTextColor(mContext.getResources().getColor(R.color.white));
                        holder.dayName.setTextColor(mContext.getResources().getColor(R.color.white));
                    } else {
                        View background = view.findViewById(R.id.history_item_day_date_container);
                        background.setBackgroundColor(mContext.getResources().getColor(R.color.calendar_day_background));
                    }
                    view.setTag(holder);
                    break;
                case TYPE_OTHER:
                case TYPE_TODAY_OTHER:
                    view = inflater.inflate(R.layout.item_history_2, viewGroup, false);
                    holder = new Holder(view);
                    if (type == TYPE_TODAY_OTHER) {
                        View background = view.findViewById(R.id.history_item_day_date_container);
                        background.setBackgroundColor(mContext.getResources().getColor(R.color.actionbar_background));
                    } else {
                        View background = view.findViewById(R.id.history_item_day_date_container);
                        background.setBackgroundColor(mContext.getResources().getColor(R.color.calendar_day_background));
                    }
                    view.setTag(holder);
                    break;
            }
        } else {
            holder = (Holder) view.getTag();
        }
        if (holder != null) {
//            if (mExpandableListView.isGroupExpanded(position) && type != TYPE_HEADER) {
//                holder.arrowDown.setVisibility(View.GONE);
//                holder.arrowUp.setVisibility(View.VISIBLE);
//            } else if (type != TYPE_HEADER) {
//                holder.arrowUp.setVisibility(View.GONE);
//                holder.arrowDown.setVisibility(View.VISIBLE);
//            }
            switch (type) {
                case TYPE_HEADER: {
                    holder.title.setText(wrapper.headerTitle);
                    holder.title.setClickable(false);
                    break;
                }
                case TYPE_FIRST:
                case TYPE_TODAY_FIRST: {
                    holder.borderPadding.setVisibility(View.GONE);
                    if (type == TYPE_TODAY_FIRST) {
                        holder.border.setVisibility(View.GONE);
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    holder.time.setText(sdf.format(wrapper.history.getStartTimeDate()) + " - " + sdf.format(wrapper.history.getEntTimeDate()));
//                    SimpleDateFormat sdf;// = new SimpleDateFormat("HH:mm", Locale.getDefault());
//                    holder.time.setText(getTime(sdf.format(wrapper.event.getStartTime()),sdf.format(wrapper.event.getEndTime())));
//                    String time = sdf.format(wrapper.event.getStartTime());
//                    holder.time.setText(getTime(sdf.format(wrapper.event.getStartTime()),sdf.format(wrapper.event.getEndTime())));
//                    holder.time.setText(time);
                    sdf = new SimpleDateFormat("d");
                    LogUtils.LOGE("convert", wrapper.history.getStartTime());
                    holder.day.setText(sdf.format(wrapper.history.getStartTimeDate()));
                    sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
                    holder.dayName.setText(sdf.format(wrapper.history.getStartTimeDate()).substring(0, 3));
                    holder.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCallBack.deleteItem(position - 1);


//                                                setAnimation(holder.container, position, DELETE);
//                                                exercises.remove(position);
//                                                new Timer().schedule(new TimerTask() {
//                                                    @Override
//                                                    public void run() {
//                                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                                            @Override
//                                                            public void run() {
//                                                                notifyItemRemoved(position);
//                                                                notifyItemRangeChanged(position, exercises.size());
//                                                            }
//                                                        });
//                                                    }
//                                                }, 400);
                        }
                    });
                    break;
                }
                case TYPE_OTHER:
                case TYPE_TODAY_OTHER: {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    holder.time.setText(sdf.format(wrapper.history.getStartTimeDate()) + " - " + sdf.format(wrapper.history.getEntTimeDate()));
//                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//                    String time = sdf.format(wrapper.event.getStartTime());
//                    holder.time.setText(getTime(sdf.format(wrapper.event.getStartTime()),sdf.format(wrapper.event.getEndTime())));
//                    holder.time.setText(time);
                    if (type == TYPE_TODAY_OTHER) {
                        holder.borderPadding.setBackgroundColor(Utils.getContext().getResources().getColor(R.color.actionbar_background));
                        holder.border.setVisibility(View.VISIBLE);
                    } else {
                        holder.borderPadding.setBackgroundColor(Utils.getContext().getResources().getColor(R.color.calendar_day_background));
                        holder.border.setVisibility(View.VISIBLE);
                    }
                    holder.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO delete history
                            mCallBack.deleteItem(position - 1);

                        }
                    });
                    break;
                }
            }
        }
        return view;
    }

//        Holder holder;
//        if (view == null) {
//            view = inflater.inflate(R.layout.item_friends_routine, viewGroup, false);
//            holder = new Holder(view);
//            view.setTag(holder);
//        } else {
//            holder = (Holder) view.getTag();
//        }
//        if (holder != null) {
//            if (mExpandableListView.isGroupExpanded(position)) {
//                holder.arrowDown.setVisibility(View.GONE);
//                holder.arrowUp.setVisibility(View.VISIBLE);
//            } else {
//                holder.arrowUp.setVisibility(View.GONE);
//                holder.arrowDown.setVisibility(View.VISIBLE);
//            }
//            FontUtils.getInst().setRobotoRegular(holder.title);
//            holder.title.setText(routine.getName());
//        }
//
//        return view;
//    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.item_friend_exercise, viewGroup, false);
        }
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
        @InjectView(R.id.history_item_time)
        TextView time;
        @Optional
        @InjectView(R.id.item_friends_routine_arrow_down)
        ImageView arrowDown;
        @Optional
        @InjectView(R.id.item_friends_routine_arrow_up)
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

    public interface CallBack {
        void deleteItem(int position);
    }

    public void setCallBack(CallBack callback) {
        mCallBack = callback;
    }

    public void removeItem(int position) {
        if (wrappedEventsHeader.get(position).isFirst && position > 0) {
//            wrappedEventsHeader.get(position + 1).isFirst = true;
        }
        wrappedEventsHeader.remove(position);
        notifyDataSetChanged();
    }

}
