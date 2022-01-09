package com.example.campusapp.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.example.campusapp.R;
import com.example.campusapp.activity.adapter.SystemMessageListApapter;
import com.example.campusapp.base.BaseActivity;
import com.example.campusapp.entity.Notification;
import com.example.campusapp.util.Constants;
import com.example.campusapp.util.MLog;
import com.example.campusapp.util.ToastUtil;
import com.example.campusapp.util.Utils;
import com.example.campusapp.util.utilNet.HttpVolley;
import com.example.campusapp.util.utilNet.NetCallBackVolley;
import com.example.campusapp.util.utilNet.Urls;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SystemMessageActivity extends BaseActivity {

    private int page = 1;//当前多少页
    private final int size = 10;//每页显示多少条

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.SUCCESS:
                    String Data = msg.obj.toString();
                    List<Notification> newList = gson.fromJson(Data, new TypeToken<List<Notification>>() {
                    }.getType());
                    if (httpType == 0) {//进来初始化 和刷新
                        announcementList.clear();
                        announcementList.addAll(newList);
                        //下拉刷新结束
                        srl_swiperefreshlayout.setRefreshing(false);
                        if (announcementList.size() < 10) {
                            announcementListApapter.changeMoreStatus(SystemMessageListApapter.LOADING_END);
                        }
                    } else if (httpType == 2) {//加载更多
                        announcementList.addAll(newList);
                        announcementListApapter.changeMoreStatus(SystemMessageListApapter.LOADING_MORE);
                    }
                    if (null != announcementListApapter) {
                        announcementListApapter.notifyDataSetChanged();
                    }
                    if (announcementList.size() == 0) {
                        rv_system_message.setVisibility(View.GONE);
                        ll_system_message_noData.setVisibility(View.VISIBLE);
                    } else {
                        rv_system_message.setVisibility(View.VISIBLE);
                        ll_system_message_noData.setVisibility(View.GONE);
                    }

                    break;
                case Constants.ERROR:
                    String msgStr = msg.obj.toString();
                    if (msgStr == null || msgStr.equals("")) {
                        ToastUtil.showNetworkError(SystemMessageActivity.this);
                    } else {
                        ToastUtil.show(SystemMessageActivity.this, msgStr);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @BindView(R.id.rv_system_message)
    RecyclerView rv_system_message;
    @BindView(R.id.ll_system_message_noData)
    LinearLayout ll_system_message_noData;
    @BindView(R.id.srl_swiperefreshlayout)
    SwipeRefreshLayout srl_swiperefreshlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_message);
        ButterKnife.bind(this);
        gson = new Gson();
        initBaseViews();
        setTitle("重要通知");
        initViews();
        GetNotification();
    }

    int lastVisibleItem;
    SystemMessageListApapter announcementListApapter;
    List<Notification> announcementList;
    Gson gson;
    int httpType = 0;


    private void initViews() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rv_system_message.setLayoutManager(linearLayoutManager);
        announcementList = new ArrayList<>();
        announcementListApapter = new SystemMessageListApapter(this, announcementList);
        rv_system_message.setAdapter(announcementListApapter);

        srl_swiperefreshlayout.setColorSchemeResources(R.color.title_color);
        srl_swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                httpType = 0;
                GetNotification();
            }
        });

        //RecyclerView滑动监听
        rv_system_message.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == announcementListApapter.getItemCount()) {
                    page = page + 1;
                    httpType = 2;
                    GetNotification();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });


        announcementListApapter.setOnClickListener(new SystemMessageListApapter.OnClickListener() {
            @Override
            public void setOnItemClickListener(View view, int position) {

            }
        });

    }

    /**
     * 获取公告
     */
    private void GetNotification() {

        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("获取公告:" + response);
                String Data = response;
                Message msg = new Message();
                msg.obj = Data;
                msg.what = Constants.SUCCESS;
                handler.sendMessage(msg);

            }

            @Override
            public void onMyErrorResponse(VolleyError error) {
                MLog.i(error.toString());
                ToastUtil.showNetworkError(SystemMessageActivity.this);
            }
        };
        if (Utils.isNetworkAvailable(this)) {
            String path = Urls.GetNotification;
            MLog.i("获取公告：" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }
    }
}
