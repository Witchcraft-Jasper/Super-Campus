package com.example.campusapp.activity.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.example.campusapp.R;
import com.example.campusapp.activity.AddScheduleActivity;
import com.example.campusapp.activity.SystemMessageActivity;
import com.example.campusapp.entity.User;
import com.example.campusapp.util.Constants;
import com.example.campusapp.util.MLog;
import com.example.campusapp.util.ResponseCode;
import com.example.campusapp.util.SharedTools;
import com.example.campusapp.util.ToastUtil;
import com.example.campusapp.util.Utils;
import com.example.campusapp.util.utilNet.HttpVolley;
import com.example.campusapp.util.utilNet.NetCallBackVolley;
import com.example.campusapp.util.utilNet.Urls;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.view.WeekView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class ScheduleFragment extends Fragment {

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ResponseCode.LOAD_SUCCESS:
                    break;
                case ResponseCode.LOAD_FAILED:
                    String msgStr = msg.obj.toString();
                    if (msgStr == null || msgStr.equals("")) {
                        ToastUtil.showNetworkError(getActivity());
                    } else {
                        ToastUtil.show(getActivity(), msgStr);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    View view;
    @BindView(R.id.id_title)
    TextView titleTextView;
    @BindView(R.id.id_layout)
    LinearLayout id_layout;
    @BindView(R.id.id_weekview)
    WeekView mWeekView;
    @BindView(R.id.id_timetableView)
    TimetableView mTimetableView;
    @BindView(R.id.iv_system_message)
    ImageView iv_system_message;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.schedule_fragment, null);
        unbinder = ButterKnife.bind(this, view);
        user = (User) SharedTools.readObject(SharedTools.User);
        gson = new Gson();
        initViews();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        GetScheduleByUserId();
    }

    User user;
    Gson gson;

    private void initViews() {
        scheduleList = new ArrayList<>();
        iv_system_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SystemMessageActivity.class);
                startActivity(intent);
            }
        });
    }

    Dialog dialog;

    private void GetScheduleByUserId() {
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("????????????:" + response);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int responseCode = jsonObject.getInt("code");
                    if (responseCode == ResponseCode.QUERY_SUCCESS) {
                        JSONObject result = jsonObject.getJSONObject("data");
                        String schedule = result.getString("scheduleList");
                        List<com.example.campusapp.entity.Schedule> newList = gson.fromJson(schedule, new TypeToken<List<com.example.campusapp.entity.Schedule>>() {
                        }.getType());
                        scheduleList.clear();
                        scheduleList.addAll(newList);
                        initTimetableView();
                    } else {
                        String strmsg = "?????????????????????";
                        Message msg = new Message();
                        msg.obj = strmsg;
                        msg.what = Constants.ERROR;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyErrorResponse(VolleyError error) {
                MLog.i(error.toString());
                dialog.dismiss();
                //ToastUtil.showNetworkError(CustomerQueryActivity.this);
            }
        };
        if (Utils.isNetworkAvailable(getActivity())) {
            dialog = Utils.createLoadingDialog(getActivity(), getString(R.string.app_dialog_getData));
            dialog.show();
            String path = Urls.GetScheduleByUserId;
            path = path + "?userId=" + user.getId();
            MLog.i("???????????????" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }
    }

    List<com.example.campusapp.entity.Schedule> scheduleList;

    //?????????????????????????????????????????????
    int target = -1;

    /**
     * ?????????????????????
     */
    private void initTimetableView() {
        mTimetableView.isShowWeekends(true).updateView();
        mTimetableView.isShowNotCurWeek(false).updateView();

        mWeekView.itemCount(16);//??????16???
        //????????????????????????
        mWeekView.source(scheduleList)
                .curWeek(16)
                .callback(new IWeekView.OnWeekItemClickedListener() {
                    @Override
                    public void onWeekClicked(int week) {
                        int cur = mTimetableView.curWeek();
                        //???????????????????????????????????????cur->????????????week
                        mTimetableView.onDateBuildListener()
                                .onUpdateDate(cur, week);
                        mTimetableView.changeWeekOnly(week);
                    }
                }).callback(new IWeekView.OnWeekLeftClickedListener() {
            @Override
            public void onWeekLeftClicked() {
                onWeekLeftLayoutClicked();
            }
        })
                .isShow(false)//???????????????????????????
                .showView();

        mTimetableView.source(scheduleList)
                .curWeek(15)
                .curTerm("")
                .maxSlideItem(8)
                .monthWidthDp(50)
                //?????????
                //?????????0.1f????????????0.1f??????????????????0.6f
                //??????????????????0->1???0???????????????1????????????
//                .alpha(0.1f, 0.1f, 0.6f)
                .callback(new ISchedule.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, List<Schedule> scheduleList) {
                        display(scheduleList);
                    }
                })
                .callback(new ISchedule.OnItemLongClickListener() {
                    @Override
                    public void onLongClick(View v, int day, int start, final Schedule subject) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("??????");
                        builder.setMessage("???????????????????????????");
                        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DeleteScheduleById(subject);
                            }
                        });
                        builder.setNegativeButton("??????", null);
                        builder.create().show();
                    }
                })
                .callback(new ISchedule.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, List<Schedule> scheduleList) {
                        Intent intent = new Intent(getActivity(), AddScheduleActivity.class);
                        intent.putExtra("schedule", scheduleList.get(0));
                        intent.putExtra("type", 2);
                        startActivity(intent);
                    }
                })
                .callback(new ISchedule.OnWeekChangedListener() {
                    @Override
                    public void onWeekChanged(int curWeek) {
                        titleTextView.setText("???" + curWeek + "???");
                    }
                })
                //????????????????????????
                .callback(new ISchedule.OnFlaglayoutClickListener() {
                    @Override
                    public void onFlaglayoutClick(int day, int start) {
                        mTimetableView.hideFlaglayout();
                        Intent intent = new Intent(getActivity(), AddScheduleActivity.class);
                        intent.putExtra("type", 1);
                        startActivity(intent);
                    }
                })
                .showView();
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    @Override
    public void onStart() {
        super.onStart();
        mTimetableView.onDateBuildListener()
                .onHighLight();
    }

    /**
     * ?????????????????????????????????????????????<br/>
     * ???????????????????????????
     */
    protected void onWeekLeftLayoutClicked() {
        final String items[] = new String[25];
        int itemCount = mWeekView.itemCount();
        for (int i = 0; i < itemCount; i++) {
            items[i] = "???" + (i + 1) + "???";
        }
        target = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("???????????????");
        builder.setSingleChoiceItems(items, mTimetableView.curWeek() - 1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        target = i;
                    }
                });
        builder.setPositiveButton("??????????????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (target != -1) {
                    mWeekView.curWeek(target + 1).updateView();
                    mTimetableView.changeWeekForce(target + 1);
                }
            }
        });
        builder.setNegativeButton("??????", null);
        builder.create().show();
    }

    /**
     * ????????????
     *
     * @param beans
     */
    protected void display(List<Schedule> beans) {
        String str = "";
        for (Schedule bean : beans) {
            str += bean.getName() + "," + bean.getWeekList().toString() + "," + bean.getStart() + "," + bean.getStep() + "\n";
        }
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.id_title, R.id.id_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_layout:
                //??????????????????????????????????????????????????????????????????????????????
                //???????????????
                if (mWeekView.isShowing()) {
                    mWeekView.isShow(false);
                    titleTextView.setTextColor(getResources().getColor(R.color.app_course_textcolor_blue));
                    int cur = mTimetableView.curWeek();
                    mTimetableView.onDateBuildListener()
                            .onUpdateDate(cur, cur);
                    mTimetableView.changeWeekOnly(cur);
                } else {
                    mWeekView.isShow(true);
                    titleTextView.setTextColor(getResources().getColor(R.color.app_red));
                }
                break;

        }
    }

    private void DeleteScheduleById(final Schedule schedule) {
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("????????????:" + response);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int responseCode = jsonObject.getInt("code");
                    if (responseCode == ResponseCode.DELETE_SUCCESS) {
                        ToastUtil.show(getContext(), "????????????");
                        mTimetableView.dataSource().remove(schedule);
                        mTimetableView.updateView();
                    } else {
                        String strmsg = "????????????";
                        Message msg = new Message();
                        msg.obj = strmsg;
                        msg.what = Constants.ERROR;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyErrorResponse(VolleyError error) {
                MLog.i(error.toString());
                dialog.dismiss();
                //ToastUtil.showNetworkError(CustomerQueryActivity.this);
            }
        };
        if (Utils.isNetworkAvailable(getActivity())) {
            dialog = Utils.createLoadingDialog(getActivity(), getString(R.string.app_dialog_delete));
            dialog.show();
            String path = Urls.DeleteScheduleById;
            path = path + "?scheduleId=" + schedule.getScheduleId();
            MLog.i("???????????????" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
