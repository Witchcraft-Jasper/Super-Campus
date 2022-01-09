package com.example.campusapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.example.campusapp.activity.adapter.CardAdapter;
import com.example.campusapp.entity.User;
import com.example.campusapp.util.Constants;
import com.example.campusapp.util.MLog;
import com.example.campusapp.util.SharedTools;
import com.example.campusapp.util.ToastUtil;
import com.example.campusapp.util.Utils;
import com.example.campusapp.util.utilNet.HttpVolley;
import com.example.campusapp.util.utilNet.NetCallBackVolley;
import com.example.campusapp.util.utilNet.Urls;
import com.fashare.stack_layout.StackLayout;
import com.fashare.stack_layout.transformer.AngleTransformer;
import com.fashare.stack_layout.transformer.StackPageTransformer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestActivity extends AppCompatActivity {

    static List<Integer> sRandomColors = new ArrayList<>();

    static {
        for (int i = 0; i < 100; i++) {
            sRandomColors.add(new Random().nextInt() | 0xff000000);
        }
    }

    SwipeRefreshLayout mRefreshLayout;
    StackLayout mStackLayout;
    CardAdapter mAdapter;
    Gson gson;
    List<User> mData = new ArrayList<>();
    User user;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.SUCCESS:
                    String Data = msg.obj.toString();
                    int htype = msg.arg1;

                    List<User> newList = gson.fromJson(Data, new TypeToken<List<User>>() {
                    }.getType());
                    if (htype == 0) {//进来初始化 和刷新
                        mData.clear();
                        mData.addAll(newList);
                        mAdapter.notifyDataSetChanged();
                    } else if (htype == 2) {//加载更多
                        mData.addAll(newList);
                        mAdapter.notifyDataSetChanged();
                    } else {//条件查询
                        mData.clear();
                        mData.addAll(newList);
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                case Constants.ERROR:
                    String msgStr = msg.obj.toString();
                    if (msgStr == null || msgStr.equals("")) {
                        ToastUtil.showNetworkError(TestActivity.this);
                    } else {
                        ToastUtil.show(TestActivity.this, msgStr);
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
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        mAdapter = new CardAdapter(TestActivity.this, mData);
        mStackLayout = (StackLayout) findViewById(R.id.stack_layout);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_refresh);
        mStackLayout.setAdapter(mAdapter);
        gson = new Gson();
        user = (User) SharedTools.readObject(SharedTools.User);
        GetMessageAll();

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                curPage = 0;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        curPage = 0;
                        GetMessageAll();
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        mStackLayout.addPageTransformer(
                new StackPageTransformer(),     // 堆叠
                new MyAlphaTransformer(),       // 渐变
                new AngleTransformer()          // 角度
        );

        mStackLayout.setOnSwipeListener(new StackLayout.OnSwipeListener() {
            @Override
            public void onSwiped(View swipedView, int swipedItemPos, boolean isSwipeLeft, int itemLeft) {
                toast((isSwipeLeft ? "往左" : "往右") + "移除" + mData.get(swipedItemPos) + "." + "剩余" + itemLeft + "项");

                // 少于5条, 加载更多
                if (itemLeft < 5) {
                    GetMessageAll();
                }
            }
        });
    }

    int curPage = 0;

    private void GetMessageAll() {
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("获取好友信息:" + response);
                String result = response;
                Message msg = new Message();
                msg.obj = result;
                msg.what = Constants.SUCCESS;
                handler.sendMessage(msg);
            }

            @Override
            public void onMyErrorResponse(VolleyError error) {
                MLog.i(error.toString());
                //ToastUtil.showNetworkError(CustomerQueryActivity.this);
            }
        };
        if (Utils.isNetworkAvailable(this)) {
            String path = Urls.GetFriends;
            path += "?userid=" + user.getId();
            MLog.i("获取好友信息：" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }
    }



    void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}