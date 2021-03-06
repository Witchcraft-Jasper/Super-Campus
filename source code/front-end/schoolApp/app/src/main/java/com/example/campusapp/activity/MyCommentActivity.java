package com.example.campusapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.example.campusapp.R;
import com.example.campusapp.activity.adapter.MessageListViewAdpater;
import com.example.campusapp.base.BaseActivity;
import com.example.campusapp.entity.Comment;
import com.example.campusapp.entity.MessageContent;
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
import com.example.campusapp.view.AutoListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCommentActivity extends BaseActivity {

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
                    for (MessageContent mes : newList){
                        mes.setUser((User) SharedTools.readObject(SharedTools.User));
                    }
                    if (htype == 0) {//??????????????? ?????????
                        messageContentList.clear();
                        messageContentList.addAll(newList);
                        //??????????????????
                        alv_my_comment.onRefreshComplete();
                        if (messageContentList.size() <= 0) {
                            alv_my_comment.setResultSize(0);
                        }

                        alv_my_comment.setResultSize(messageContentList.size());

                    } else if (htype == 2) {//????????????
                        alv_my_comment.onLoadComplete();
                        messageContentList.addAll(newList);
                        alv_my_comment.setResultSize(newList.size());
                    } else {//????????????
                        alv_my_comment.onRefreshComplete();
                        messageContentList.clear();
                        messageContentList.addAll(newList);
                        alv_my_comment.setResultSize(messageContentList.size());

                    }
                    if (null != messageListViewAdpater) {
                        messageListViewAdpater.notifyDataSetChanged();
                    }

                    break;
                case Constants.ERROR:
                    String msgStr = msg.obj.toString();
                    if (msgStr == null || msgStr.equals("")) {
                        ToastUtil.showNetworkError(MyCommentActivity.this);
                    } else {
                        ToastUtil.show(MyCommentActivity.this, msgStr);
                    }

                    break;
            }
        }
    };

    @BindView(R.id.et_my_comment_keyword)
    EditText et_my_comment_keyword;
    @BindView(R.id.alv_my_comment)
    AutoListView alv_my_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comment);
        ButterKnife.bind(this);

        initBaseViews();
        setTitle("????????????");

        gson = new Gson();
        user = (User) SharedTools.readObject(SharedTools.User);
        initViews();

    }

    User user;
    List<MessageContent> messageContentList;
    MessageListViewAdpater messageListViewAdpater;
    Gson gson;
    int page = 1;
    int size = 10;
    int httpType = 1;
    String keyWord = "";


    @Override
    public void onResume() {
        super.onResume();
        page = 1;
        httpType = 1;
        GetMessageByCommentUserId();
    }

    private void initViews() {
        messageContentList = new ArrayList<>();
        messageListViewAdpater = new MessageListViewAdpater(MyCommentActivity.this, messageContentList);
        alv_my_comment.setAdapter(messageListViewAdpater);

        alv_my_comment.setOnRefreshListener(new AutoListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                httpType = 1;
                GetMessageByCommentUserId();
            }
        });

        alv_my_comment.setOnLoadListener(new AutoListView.OnLoadListener() {
            @Override
            public void onLoad() {
                page++;
                httpType = 2;
                GetMessageByCommentUserId();
            }
        });

        et_my_comment_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                keyWord = s.toString();
                httpType = 1;
                page = 1;
                GetMessageByCommentUserId();
            }
        });

        messageListViewAdpater.setOnClickListener(new MessageListViewAdpater.OnClickListener() {
            @Override
            public void onDeleteMessage(final int messageId, final int messagePosition) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyCommentActivity.this);
                builder.setTitle("??????");
                builder.setMessage("???????????????????????????");
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteMessageById(messageId, messagePosition);
                    }
                });
                builder.setNegativeButton("??????", null);
                builder.create().show();
            }

            @Override
            public void onCommentClick(int messageId, int messagePosition) {
                showComment(messageId, messagePosition);
            }

            @Override
            public void onReplyClick(User commentUser, int messageId, int messagePosition) {
                showReply(commentUser, messageId, messagePosition);
            }

            @Override
            public void onDeleteCommentClick(final int commentId, final int messagePosition, final int commentPosition) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyCommentActivity.this);
                builder.setTitle("??????");
                builder.setMessage("???????????????????????????");
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteCommentById(commentId, messagePosition, commentPosition);
                    }
                });
                builder.setNegativeButton("??????", null);
                builder.create().show();
            }

            @Override
            public void onUpdateMessage(MessageContent messageContent) {

            }
        });
    }

    Dialog commentDialog;

    private void showComment(final int messageId, final int messagePosition) {
        commentDialog = new Dialog(MyCommentActivity.this, R.style.mystyle);// ?????????????????????dialog
        commentDialog.setContentView(R.layout.message_comment_item);// ????????????
        final EditText et_message_comment = (EditText) commentDialog.findViewById(R.id.et_message_comment);
        LinearLayout ll_message_commnet_save = (LinearLayout) commentDialog.findViewById(R.id.ll_message_commnet_save);
        ll_message_commnet_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_message_comment.getText().toString().equals("")) {
                    ToastUtil.show(MyCommentActivity.this, "???????????????");
                    return;
                }
                Comment comment = new Comment();
                comment.setMessageId(messageId);
                comment.setCommentContent(et_message_comment.getText().toString());
                comment.setCommentUserId(user.getId());
                saveData(comment, messagePosition);
            }
        });
        commentDialog.show();
    }


    private void showReply(final User commentUser, final int messageId, final int messagePosition) {
        commentDialog = new Dialog(MyCommentActivity.this, R.style.loading_dialog);// ?????????????????????dialog
        commentDialog.setContentView(R.layout.message_comment_item);// ????????????
        final EditText et_message_comment = (EditText) commentDialog.findViewById(R.id.et_message_comment);
        et_message_comment.setHint("?????? " + commentUser.getRealname());
        LinearLayout ll_message_commnet_save = (LinearLayout) commentDialog.findViewById(R.id.ll_message_commnet_save);
        ll_message_commnet_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_message_comment.getText().toString().equals("")) {
                    ToastUtil.show(MyCommentActivity.this, "???????????????");
                    return;
                }
                Comment comment = new Comment();
                comment.setMessageId(messageId);
                comment.setReplyUserId(user.getId());
                comment.setCommentContent(et_message_comment.getText().toString());
                comment.setCommentUserId(commentUser.getId());
                saveData(comment, messagePosition);
            }
        });
        commentDialog.show();
    }


    private void DeleteCommentById(int commentId, final int messagePosition, final int commentPosition) {
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("????????????:" + response);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("Success");
                    if (status) {
                        ToastUtil.show(MyCommentActivity.this, "????????????");
                        messageContentList.get(messagePosition).getComments().remove(commentPosition);
                        messageListViewAdpater.notifyDataSetChanged();
                    } else {
                        String strmsg = jsonObject.getString("Message");
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
        if (Utils.isNetworkAvailable(MyCommentActivity.this)) {
            dialog = Utils.createLoadingDialog(MyCommentActivity.this, getString(R.string.app_dialog_delete));
            dialog.show();
            String path = Urls.DeleteCommentById;
            path = path + "?commentId=" + commentId;
            MLog.i("???????????????" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }
    }

    private void DeleteMessageById(int messageId, final int messagePosition) {
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("????????????:" + response);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("Success");
                    if (status) {
                        ToastUtil.show(MyCommentActivity.this, "????????????");
                        messageContentList.remove(messagePosition);
                        messageListViewAdpater.notifyDataSetChanged();
                    } else {
                        String strmsg = jsonObject.getString("Message");
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
        if (Utils.isNetworkAvailable(MyCommentActivity.this)) {
            dialog = Utils.createLoadingDialog(MyCommentActivity.this, getString(R.string.app_dialog_delete));
            dialog.show();
            String path = Urls.DeleteMessageById;
            path = path + "?messageId=" + messageId;
            MLog.i("???????????????" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }
    }

    Dialog dialog;

    private void saveData(Comment comment, final int messagePosition) {

        MLog.i("???????????????" + gson.toJson(comment));

        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("????????????:" + response);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("Success");
                    if (status) {
                        String result = jsonObject.getString("Result");
                        Comment comment1 = gson.fromJson(result, Comment.class);
                        commentDialog.dismiss();
                        messageContentList.get(messagePosition).getComments().add(comment1);
                        messageListViewAdpater.notifyDataSetChanged();
                    } else {
                        String strmsg = jsonObject.getString("Message");
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
                dialog.dismiss();
            }
        };
        if (Utils.isNetworkAvailable(MyCommentActivity.this)) {
            dialog = Utils.createLoadingDialog(MyCommentActivity.this, getString(R.string.app_dialog_save));
            dialog.show();
            String path = Urls.AddComment;
            path = path + "?comment=" + gson.toJson(comment);
            MLog.i("???????????????" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }

    }

    /**
     * ??????????????????
     */
    private void GetMessageByCommentUserId() {

        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("??????????????????:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int responseCode = jsonObject.getInt("code");
                    if (responseCode == ResponseCode.QUERY_SUCCESS) {
                        String str = jsonObject.getString("data");
                        Message msg = new Message();
                        msg.obj = str;
                        msg.what = Constants.SUCCESS;
                        handler.sendMessage(msg);
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
            }
        };
        if (Utils.isNetworkAvailable(MyCommentActivity.this)) {
            String path = Urls.GetMessageByCommentUserId;
            path = path + "?userid=" + user.getId();
            MLog.i("?????????????????????" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }
    }
}
