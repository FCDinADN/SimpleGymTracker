package com.runApp.adapters;

/**
 * Created by Rares on 23/12/14.
 */
public class RoutineAdapter {//extends RecyclerView.Adapter<RoutineAdapter.ViewHolder> {

//    private ArrayList<Routine> mRoutines;
//    private Context mContext;
//    private Fragment mFragment;
//    private int lastPosition = -1;
//    private final String ADD = "add_item";
//    private final String DELETE = "delete_item";
//    private Routine mRoutine;
//
//    public RoutineAdapter(Fragment fragment, ArrayList<Routine> routines, Context context) {
//        mFragment = fragment;
//        mRoutines = routines;
//        mContext = context;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(mContext).inflate(R.layout.item_routine, parent, false);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        if (position == 0) {
//            holder.divider.setVisibility(View.GONE);
//        }
//        mRoutine = mRoutines.get(position);
//        setAnimation(holder.container, position, ADD);
//        holder.name.setText(mRoutine.getName());
//        holder.container.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fragmentManager = mFragment.getActivity().getSupportFragmentManager();
////                holder.container.setBackgroundColor(mFragment.getActivity().getResources().getColor(R.color.main_background));
//                Fragment fragment = new WorkoutFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString(WorkoutFragment.ROUTINE_NAME, mRoutines.get(position).getName());
//                bundle.putIntegerArrayList(WorkoutFragment.ROUTINE_EXERCISES_IDS, mRoutines.get(position).getSelectedExercisesIds());
//                LogUtils.LOGE("tag", "ids:" + mRoutines.get(position).getSelectedExercisesIds());
//                fragment.setArguments(bundle);
//                fragmentManager.beginTransaction()
////                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left)
//                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right)
//                        .replace(R.id.container, fragment)
//                        .addToBackStack("")
//                        .commit();
//                mFragment.getActivity().getSupportFragmentManager().executePendingTransactions();
//            }
//        });
//        holder.editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mRoutine = mRoutines.get(position);
//                FragmentManager fragmentManager = mFragment.getActivity().getSupportFragmentManager();
//                Fragment fragment = new RoutineFragment();
//                Bundle bundle = new Bundle();
//                bundle.putInt(RoutineFragment.FRAGMENT_TYPE, RoutineFragment.EDIT_ROUTINE);
//                bundle.putInt(RoutineFragment.ROUTINE_ID, mRoutine.getId());
//                bundle.putString(RoutineFragment.ROUTINE_NAME, mRoutine.getName());
//                bundle.putInt(RoutineFragment.ROUTINE_TYPE, mRoutine.getTypeInt());
//                LogUtils.LOGE("ROUTINE", "from adapter:" + mRoutine.getTypeInt());
//                bundle.putIntegerArrayList(RoutineFragment.ROUTINE_SELECTED_EXERCISES_IDS, mRoutine.getSelectedExercisesIds());
//                fragment.setArguments(bundle);
//                fragmentManager.beginTransaction()
//                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right)
//                        .replace(R.id.container, fragment)
//                        .addToBackStack("")
//                        .commit();
////                ArrayList<String> exercisesNames = getSelectedExercises(mRoutine.getExercises())
//            }
//        });
////        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                DialogHandler.showConfirmDialog(mFragment.getActivity(),
////                        R.string.dialog_delete_title,
////                        R.string.dialog_delete_routine_text,
////                        R.string.dialog_delete,
////                        R.string.dialog_cancel,
////                        new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                                if (which == DialogInterface.BUTTON_POSITIVE) {
////                                    GymDatabaseHelper.getInst().deleteRoutine(mRoutine);
////                                    setAnimation(holder.container, position, DELETE);
////                                    mRoutines.remove(position);
////                                    new Timer().schedule(new TimerTask() {
////                                        @Override
////                                        public void run() {
////                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
////                                                @Override
////                                                public void run() {
////                                                    notifyItemRemoved(position);
////                                                    notifyItemRangeChanged(position, mRoutines.size());
////                                                }
////                                            });
////                                        }
////                                    }, 400);
////                                }
////                            }
////                        });
////            }
////        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return mRoutines.size();
//    }
//
//    public void setRoutines(ArrayList<Routine> routines) {
//        mRoutines = routines;
//    }
//
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
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        @InjectView(R.id.routine_item_divider)
//        View divider;
//        @InjectView(R.id.routine_item_layout)
//        RelativeLayout container;
//        @InjectView(R.id.routine_item_name_title)
//        TextView name;
//        @InjectView(R.id.routine_item_edit)
//        ImageButton editButton;
//        @InjectView(R.id.routine_item_delete)
//        ImageButton deleteButton;
//
//        public ViewHolder(View view) {
//            super(view);
//            ButterKnife.inject(this, view);
//        }
//    }

}
