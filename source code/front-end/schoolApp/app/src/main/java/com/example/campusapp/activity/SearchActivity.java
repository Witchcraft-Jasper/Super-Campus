package com.example.campusapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.campusapp.R;
import com.example.campusapp.activity.adapter.MessageListViewAdpater;
import com.example.campusapp.entity.MessageContent;
import com.example.campusapp.util.Constants;
import com.example.campusapp.util.MLog;
import com.example.campusapp.util.ResponseCode;
import com.example.campusapp.util.ToastUtil;
import com.example.campusapp.util.Utils;
import com.example.campusapp.util.utilNet.HttpVolley;
import com.example.campusapp.util.utilNet.NetCallBackVolley;
import com.example.campusapp.util.utilNet.Urls;
import com.example.campusapp.view.AutoListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nightonke.jellytogglebutton.EaseTypes.EaseType;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    JellyToggleButton jellyToggleButton;
    MessageListViewAdpater messageListViewAdpater;
    EditText et_lost_found_keyword;
    AutoListView alv_lost_found;
    List<MessageContent> messageContentList;
    TextView titleText;
    private int searchType;
    private String keyWords;
    private Gson gson;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.SUCCESS:
                    String Data = msg.obj.toString();
                    int htype = msg.arg1;
                    List<MessageContent> newList = gson.fromJson(Data, new TypeToken<List<MessageContent>>() {
                    }.getType());
                    if (htype == 0) {//进来初始化 和刷新
                        messageContentList.clear();
                        messageContentList.addAll(newList);
                        //下拉刷新结束
                        alv_lost_found.onRefreshComplete();
                        if (messageContentList.size() <= 0) {
                            alv_lost_found.setResultSize(0);
                        }
                        alv_lost_found.setResultSize(messageContentList.size());

                    } else if (htype == 2) {//加载更多
                        alv_lost_found.onLoadComplete();
                        messageContentList.addAll(newList);
                        alv_lost_found.setResultSize(newList.size());
                    } else {//条件查询
                        alv_lost_found.onRefreshComplete();
                        messageContentList.clear();
                        messageContentList.addAll(newList);
                        alv_lost_found.setResultSize(messageContentList.size());
                    }
                    if (null != messageListViewAdpater) {
                        messageListViewAdpater.notifyDataSetChanged();
                    }

                    break;
                case Constants.ERROR:
                    String msgStr = msg.obj.toString();
                    if ("".equals(msgStr)) {
                        ToastUtil.showNetworkError(SearchActivity.this);
                    } else {
                        ToastUtil.show(SearchActivity.this, msgStr);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        jellyToggleButton = findViewById(R.id.jellyToggleButton);
        alv_lost_found = findViewById(R.id.alv_lost_found);
        et_lost_found_keyword = findViewById(R.id.et_message_search);
        titleText = findViewById(R.id.textView);
        messageContentList = new ArrayList<>();
        messageListViewAdpater = new MessageListViewAdpater(this, messageContentList);
        alv_lost_found.setAdapter(messageListViewAdpater);

        jellyToggleButton.setEaseType(EaseType.EaseInOutSine);
        jellyToggleButton.setLeftBackgroundColor(0xfff66754);
        jellyToggleButton.setRightBackgroundColor(0xff7BA6EF);
        searchType = getIntent().getIntExtra("type", 1);
        if (searchType == 1) {
            titleText.setText("标签搜索");
        } else {
            jellyToggleButton.setChecked(true);
            titleText.setText("标题搜索");
        }
        gson = new Gson();

        et_lost_found_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                keyWords = s.toString();
                searchMessage();
            }
        });

        jellyToggleButton.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                if (state == State.LEFT) {
                    searchType = 1;
                    titleText.setText("标签搜索");
                } else if (state == State.RIGHT) {
                    searchType = 2;
                    titleText.setText("标题搜索");
                }
            }
        });
    }

    private void searchMessage() {
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("获取信息失物招领:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int responseCode = jsonObject.getInt("code");
                    if (responseCode == ResponseCode.CODE_SUCCESS) {
                        String str = jsonObject.getString("data");
                        Message msg = new Message();
                        msg.obj = str;
                        msg.what = Constants.SUCCESS;
                        handler.sendMessage(msg);
                    } else {
                        String strmsg = "获取失物招领信息失败";
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
            }
        };
        if (Utils.isNetworkAvailable(this)) {
            String path;
            if (searchType == 1) {
                path = Urls.GetMessageTagSearch;
                path = path + "?tag=" + keyWords;
                MLog.i("按tag获取信息：" + path);
            } else {
                path = Urls.GetMessageTitleSearch;
                path = path + "?title=" + keyWords;
                MLog.i("按标题获取信息：" + path);
            }
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }
    }
}