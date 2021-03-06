package com.example.campusapp.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.campusapp.R;
import com.example.campusapp.base.BaseActivity;
import com.example.campusapp.entity.Schedule;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddScheduleActivity extends BaseActivity {

    @SuppressLint("HandlerLeak")
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
                        ToastUtil.showNetworkError(AddScheduleActivity.this);
                    } else {
                        ToastUtil.show(AddScheduleActivity.this, msgStr);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @BindView(R.id.et_schedule_name)
    EditText et_schedule_name;
    @BindView(R.id.et_schedule_room)
    EditText et_schedule_room;
    @BindView(R.id.et_schedule_teacher)
    EditText et_schedule_teacher;
    @BindView(R.id.tv_schedule_week)
    TextView tv_schedule_week;
    @BindView(R.id.ll_schedule_week)
    LinearLayout ll_schedule_week;
    @BindView(R.id.tv_schedule_day)
    TextView tv_schedule_day;
    @BindView(R.id.ll_schedule_day)
    LinearLayout ll_schedule_day;
    @BindView(R.id.et_schedule_start)
    EditText et_schedule_start;
    @BindView(R.id.et_schedule_step)
    EditText et_schedule_step;
    @BindView(R.id.ll_schedule_save)
    LinearLayout ll_schedule_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        ButterKnife.bind(this);
        initBaseViews();
        setTitle("????????????");
        type = getIntent().getIntExtra("type", 0);
        if (type == 2) {
            schedule = (com.zhuangfei.timetable.model.Schedule) getIntent().getSerializableExtra("schedule");
            setTitle("????????????");
        }
        initViews();
    }

    int type;
    com.zhuangfei.timetable.model.Schedule schedule;

    private void initViews() {
        weekList = new ArrayList<>();
        user = (User) SharedTools.readObject(SharedTools.User);
        gson = new Gson();
        if (type == 2) {
            et_schedule_name.setText(schedule.getName());
            et_schedule_room.setText(schedule.getRoom());
            et_schedule_teacher.setText(schedule.getTeacher());
            StringBuilder week = new StringBuilder();
            if (schedule.getWeekList() != null) {
                for (int i = 0; i < schedule.getWeekList().size(); i++) {
                    weekList.add(schedule.getWeekList().get(i));
                    if (week.toString().equals("")) {
                        week = new StringBuilder("???" + schedule.getWeekList().get(i) + "???");
                    } else {
                        week.append("??????").append(schedule.getWeekList().get(i)).append("???");
                    }
                }
            }

            tv_schedule_week.setText(week.toString());
            day = schedule.getDay();
            String day = "";
            switch (schedule.getDay()) {
                case 1:
                    day = "?????????";
                    break;
                case 2:
                    day = "?????????";
                    break;
                case 3:
                    day = "?????????";
                    break;
                case 4:
                    day = "?????????";
                    break;
                case 5:
                    day = "?????????";
                    break;
                case 6:
                    day = "?????????";
                    break;
                case 7:
                    day = "?????????";
                    break;
                default:
                    break;
            }
            tv_schedule_day.setText(day);
            et_schedule_start.setText(schedule.getStart() + "");
            et_schedule_step.setText(schedule.getStep() + "");
        }
    }

    User user;
    Dialog dialog;
    List<Integer> weekList;
    int day;
    Gson gson;


    @OnClick({R.id.ll_schedule_week, R.id.ll_schedule_day, R.id.ll_schedule_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_schedule_week:
                String[] areas = new String[25];
                for (int i = 0; i < 25; i++) {
                    areas[i] = "???" + (i + 1) + "???";
                }

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("??????????????????");
                alertBuilder.setMultiChoiceItems(areas, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                        if (isChecked) {
                            weekList.add(i + 1);
                        } else {
                            weekList.remove((Integer) (i + 1));
                        }
                    }
                });
                alertBuilder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String week = "";
                        for (int j = 0; j < weekList.size(); j++) {
                            if (week.equals("")) {
                                week = "???" + weekList.get(j) + "???";
                            } else {
                                week = week + "??????" + weekList.get(j) + "???";
                            }
                        }
                        tv_schedule_week.setText(week);
                        dialog.dismiss();
                    }
                });
                alertBuilder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog = alertBuilder.create();
                dialog.show();
                break;
            case R.id.ll_schedule_day:
                final String[] items = new String[]{"?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????"};
                AlertDialog.Builder alertBuildera = new AlertDialog.Builder(this);
                alertBuildera.setTitle("????????????????????????");
                alertBuildera.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        day = i + 1;
                    }
                });

                alertBuildera.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tv_schedule_day.setText(items[day - 1]);
                        dialog.dismiss();
                    }
                });
                alertBuildera.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog = alertBuildera.create();
                dialog.show();
                break;
            case R.id.ll_schedule_save:
                try {
                    saveData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void saveData() throws JSONException {
        Schedule newSchedule = new Schedule();
        if (schedule != null) {
            newSchedule.setScheduleId(schedule.getScheduleId());
        }
        newSchedule.setUserId(user.getId());
        newSchedule.setName(et_schedule_name.getText().toString());
        newSchedule.setRoom(et_schedule_room.getText().toString());
        newSchedule.setTeacher(et_schedule_teacher.getText().toString());
        String week = "";
        for (int i = 0; i < weekList.size(); i++) {
            if (week.equals("")) {
                week = weekList.get(i) + "";
            } else {
                week = week + "," + weekList.get(i) + "";
            }
        }
        newSchedule.setWeek(week);
        newSchedule.setDay(day);
        newSchedule.setStart(Utils.getInt(et_schedule_start.getText().toString()));
        newSchedule.setStep(Utils.getInt(et_schedule_step.getText().toString()));

        if (newSchedule.getName().equals("")) {
            ToastUtil.show(this, "?????????????????????");
            return;
        }
        if (week.equals("")) {
            ToastUtil.show(this, "??????????????????");
            return;
        }
        if (day == 0) {
            ToastUtil.show(this, "?????????????????????");
            return;
        }
        if (newSchedule.getStart() == 0) {
            ToastUtil.show(this, "??????????????????????????????");
            return;
        }
        if (newSchedule.getStep() == 0) {
            ToastUtil.show(this, "?????????????????????");
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("scheduleId", String.valueOf(newSchedule.getScheduleId()));
        map.put("userId", String.valueOf(newSchedule.getUserId()));
        map.put("name", newSchedule.getName());
        map.put("room", newSchedule.getRoom());
        map.put("teacher", newSchedule.getTeacher());
        map.put("start", String.valueOf(newSchedule.getStart()));
        map.put("step", String.valueOf(newSchedule.getStep()));
        map.put("day", String.valueOf(newSchedule.getDay()));
        map.put("week", newSchedule.getWeek());

        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("????????????:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int responseCode = jsonObject.getInt("code");
                    if (responseCode == ResponseCode.UPDATE_SUCCESS) {
                        //????????????
                        ToastUtil.show(AddScheduleActivity.this, "????????????");
                        finish();

                    } else {
                        String strmsg = "????????????";
                        Message msg = new Message();
                        msg.obj = strmsg;
                        msg.what = Constants.ERROR;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyErrorResponse(VolleyError error) {
                MLog.i(error.toString());
                //ToastUtil.showNetworkError(CustomerQueryActivity.this);
            }
        };
        if (Utils.isNetworkAvailable(this)) {

            String path = Urls.AddSchedule;
            MLog.i("???????????????" + path);
            HttpVolley.volleStringRequestPostLogin(path, map, ncbv);
        }

    }
}
