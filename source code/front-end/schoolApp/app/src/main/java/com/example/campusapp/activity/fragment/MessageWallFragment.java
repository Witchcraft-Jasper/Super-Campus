package com.example.campusapp.activity.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.example.campusapp.R;
import com.example.campusapp.activity.AddLostFoundActivity;
import com.example.campusapp.activity.SystemMessageActivity;
import com.example.campusapp.activity.adapter.MessageListViewAdpater;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class MessageWallFragment extends Fragment {
    User user;
    List<MessageContent> messageContentList = new ArrayList<>();
    MessageListViewAdpater messageListViewAdpater;
    Gson gson;
    int page = 1;
    int size = 10;
    int httpType = 1;
    String keyWord = "";
    String imageurl;

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
//                    System.out.println("asd"+newList.get(0).getImagesUrl()[0]);
//                    System.out.println("qqqq" +newList.get(0).getUser());
                    if (htype == 0) {//??????????????? ?????????
                        messageContentList.clear();
                        messageContentList.addAll(newList);
                        //??????????????????
                        alv_message.onRefreshComplete();
                        if (messageContentList.size() <= 0) {
                            alv_message.setResultSize(0);
                        }

                        alv_message.setResultSize(messageContentList.size());

                    } else if (htype == 2) {//????????????
                        alv_message.onLoadComplete();
                        messageContentList.addAll(newList);
                        alv_message.setResultSize(newList.size());
                    } else {//????????????
                        alv_message.onRefreshComplete();
                        messageContentList.clear();
                        messageContentList.addAll(newList);
                        alv_message.setResultSize(messageContentList.size());

                    }
                    if (null != messageListViewAdpater) {
                        messageListViewAdpater.notifyDataSetChanged();
                    }

                    break;
                case Constants.ERROR:
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
    @BindView(R.id.iv_add)
    ImageView iv_add;
    @BindView(R.id.et_message_keyword)
    EditText et_message_keyword;
    @BindView(R.id.alv_message)
    AutoListView alv_message;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.message_wall_fragment, null);
        unbinder = ButterKnife.bind(this, view);

        gson = new Gson();
        user = (User) SharedTools.readObject(SharedTools.User);
        initViews();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 1;
        httpType = 1;
        GetMessageAll();
    }


    private void initViews() {
        messageListViewAdpater = new MessageListViewAdpater(getActivity(), messageContentList);
        alv_message.setAdapter(messageListViewAdpater);

        alv_message.setOnRefreshListener(new AutoListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                httpType = 1;
                GetMessageAll();
            }
        });

        alv_message.setOnLoadListener(new AutoListView.OnLoadListener() {
            @Override
            public void onLoad() {
                page++;
                httpType = 2;
                GetMessageAll();
            }
        });

        et_message_keyword.addTextChangedListener(new TextWatcher() {
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
                GetMessageAll();
            }
        });

        messageListViewAdpater.setOnClickListener(new MessageListViewAdpater.OnClickListener() {
            @Override
            public void onDeleteMessage(final int messageId, final int messagePosition) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        commentDialog = new Dialog(getActivity(), R.style.mystyle);// ?????????????????????dialog
        commentDialog.setContentView(R.layout.message_comment_item);// ????????????
        final EditText et_message_comment = (EditText) commentDialog.findViewById(R.id.et_message_comment);
        LinearLayout ll_message_commnet_save = (LinearLayout) commentDialog.findViewById(R.id.ll_message_commnet_save);
        ll_message_commnet_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(et_message_comment.getText().toString())) {
                    ToastUtil.show(getActivity(), "???????????????");
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


        commentDialog = new Dialog(getActivity(), R.style.loading_dialog);// ?????????????????????dialog
        commentDialog.setContentView(R.layout.message_comment_item);// ????????????
        final EditText et_message_comment = (EditText) commentDialog.findViewById(R.id.et_message_comment);
        et_message_comment.setHint("?????? " + commentUser.getRealname());
        LinearLayout ll_message_commnet_save = (LinearLayout) commentDialog.findViewById(R.id.ll_message_commnet_save);
        ll_message_commnet_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(et_message_comment.getText().toString())) {
                    ToastUtil.show(getActivity(), "???????????????");
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

    @OnClick({R.id.iv_add, R.id.iv_system_message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
                Intent intent = new Intent(getActivity(), AddLostFoundActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
                break;
            case R.id.iv_system_message:
                Intent systemMessage = new Intent(getActivity(), SystemMessageActivity.class);
                startActivity(systemMessage);
                break;
        }
    }

    private void DeleteCommentById(int commentId, final int messagePosition, final int commentPosition) {
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
                        messageContentList.get(messagePosition).getComments().remove(commentPosition);
                        messageListViewAdpater.notifyDataSetChanged();
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
                    int responseCode = jsonObject.getInt("code");
                    if (responseCode == ResponseCode.DELETE_SUCCESS) {
                        ToastUtil.show(getContext(), "????????????");
                        messageContentList.remove(messagePosition);
                        messageListViewAdpater.notifyDataSetChanged();
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
            String path = Urls.DeleteMessageById;
            path = path + "?messageId=" + messageId;
            MLog.i("???????????????" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }
    }

    Dialog dialog;

    private void saveData(Comment comment, final int messagePosition) {
        MLog.i("???????????????" + gson.toJson(comment));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(new Date());
        comment.setCreateTime(time);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("messageId", comment.getMessageId());
            jsonObject.put("commentUserId", comment.getCommentUserId());
            jsonObject.put("commentContent", comment.getCommentContent());
            jsonObject.put("createTime", comment.getCreateTime());
            if (comment.getReplyUserId() != 0) {
                jsonObject.put("replyUserId", comment.getCommentContent());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("????????????:" + response);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int responseCode = jsonObject.getInt("code");
                    if (responseCode == ResponseCode.UPDATE_SUCCESS) {
                        String result = jsonObject.getString("data");
                        Comment comment1 = gson.fromJson(result, Comment.class);
                        commentDialog.dismiss();
                        messageContentList.get(messagePosition).getComments().add(comment1);
                        messageListViewAdpater.notifyDataSetChanged();
                    } else {
                        String strmsg = "??????????????????";
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
        if (Utils.isNetworkAvailable(getActivity())) {
            dialog = Utils.createLoadingDialog(getActivity(), getString(R.string.app_dialog_save));
            dialog.show();
            String path = Urls.AddComment;
            MLog.i("???????????????" + path);
            HttpVolley.volleStringRequestPostJson(path, jsonObject, ncbv);

        }

    }

    /**
     * ?????????????????????
     */
    private void GetMessageAll() {
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("?????????????????????:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int responseCode = jsonObject.getInt("code");
                    if (responseCode == ResponseCode.LOAD_SUCCESS) {
                        String str = jsonObject.getString("data");
                        Message msg = new Message();
                        msg.obj = str;
                        msg.what = Constants.SUCCESS;
//                        loadData(msg);
                        handler.sendMessage(msg);
                    } else {
                        String strmsg = "???????????????????????????";
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
        if (Utils.isNetworkAvailable(getActivity())) {
            String path = Urls.GetMessageAll;
            path = path + "?messageType=2";
            MLog.i("????????????????????????" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
