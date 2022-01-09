package com.example.campusapp.activity;

import android.app.Dialog;
import android.content.Intent;
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
import com.example.campusapp.base.BaseActivity;
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
import com.example.campusapp.view.ImageTextView;


import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdatePassWordActivity extends BaseActivity {

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.SUCCESS:
                    break;
                case Constants.ERROR:
                    String msgStr = msg.obj.toString();
                    if (msgStr == null || msgStr.equals("")) {
                        ToastUtil.showNetworkError(UpdatePassWordActivity.this);
                    } else {
                        ToastUtil.show(UpdatePassWordActivity.this, msgStr);
                    }

                    break;
            }
        }
    };

    @BindView(R.id.et_update_password_original)
    EditText et_update_password_original;
    @BindView(R.id.tv_update_password_original_according)
    ImageTextView tv_update_password_original_according;
    @BindView(R.id.et_update_password_new)
    EditText et_update_password_new;
    @BindView(R.id.tv_update_password_new_according)
    ImageTextView tv_update_password_new_according;
    @BindView(R.id.et_update_password_new_confirm)
    EditText et_update_password_new_confirm;
    @BindView(R.id.tv_update_password_new_confirm_according)
    ImageTextView tv_update_password_new_confirm_according;
    @BindView(R.id.ll_update_password_determine)
    LinearLayout ll_update_password_determine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pass_word);
        ButterKnife.bind(this);
        user = (User) SharedTools.readObject(SharedTools.User);
        initBaseViews();
        setTitle(getString(R.string.update_password_title));

        initViews();
    }

    User user;

    private void initViews() {
        et_update_password_original.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    tv_update_password_original_according.setVisibility(View.VISIBLE);
                } else {
                    tv_update_password_original_according.setVisibility(View.GONE);
                }
            }
        });

        et_update_password_new.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    tv_update_password_new_according.setVisibility(View.VISIBLE);
                } else {
                    tv_update_password_new_according.setVisibility(View.GONE);
                }
            }
        });

        et_update_password_new_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    tv_update_password_new_confirm_according.setVisibility(View.VISIBLE);
                } else {
                    tv_update_password_new_confirm_according.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick({R.id.ll_update_password_determine, R.id.tv_update_password_original_according, R.id.tv_update_password_new_according, R.id.tv_update_password_new_confirm_according})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_update_password_original_according:
                et_update_password_original.setText("");
                break;
            case R.id.tv_update_password_new_according:
                et_update_password_new.setText("");
                break;
            case R.id.tv_update_password_new_confirm_according:
                et_update_password_new_confirm.setText("");
                break;
            case R.id.ll_update_password_determine:
                String old = et_update_password_original.getText().toString();
                String newOne = et_update_password_new.getText().toString();
                String newTwo = et_update_password_new_confirm.getText().toString();
                if (old.equals("")) {
                    ToastUtil.show(this, "请输入原密码!");
                    return;
                }
                if (newOne.equals("")) {
                    ToastUtil.show(this, "请输入新密码!");
                    return;
                }
                if (newTwo.equals("")) {
                    ToastUtil.show(this, "请确认新密码!");
                    return;
                }
                if (newOne.length() < 6 || newTwo.length() < 6) {
                    ToastUtil.show(this, "密码长度不能小于6位!");
                    return;
                }

                if (!newOne.equals(newTwo)) {
                    ToastUtil.show(this, "新密码不一致!");
                    return;
                }
                if (!old.equals(user.getPassword())) {
                    ToastUtil.show(this, "原密码错误!");
                    return;
                }
                ChangePassword(old, newOne);


                break;
        }
    }


    Dialog dialog;

    /**
     * 修改密码
     */
    private void ChangePassword(String oldpass, String newpass) {
        User sendUser = new User();
        sendUser.setId(user.getId());
        sendUser.setPassword(newpass);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", sendUser.getId());
            jsonObject.put("password", sendUser.getPassword());
            jsonObject.put("name", sendUser.getName());
            jsonObject.put("type", sendUser.getType());
            jsonObject.put("realname", sendUser.getRealname());
            jsonObject.put("imageurl", sendUser.getImageurl());
            jsonObject.put("email", sendUser.getEmail());
            jsonObject.put("sex", sendUser.getSex());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("修改密码:" + response);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int responseCode = jsonObject.getInt("code");
                    if (responseCode == ResponseCode.UPDATE_SUCCESS) {
                        ToastUtil.show(UpdatePassWordActivity.this, "修改成功,请重新登录!");
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Thread.sleep(1000);
                                    SharedTools.deleteDate(SharedTools.Password);
                                    Intent i = UpdatePassWordActivity.this.getPackageManager().getLaunchIntentForPackage(UpdatePassWordActivity.this.getPackageName());
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    UpdatePassWordActivity.this.startActivity(i);
                                    System.exit(0);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();

                    } else {
                        String strmsg = "密码修改失败";
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
                ToastUtil.showNetworkError(UpdatePassWordActivity.this);
                dialog.dismiss();
            }
        };
        if (Utils.isNetworkAvailable(this)) {
            dialog = Utils.createLoadingDialog(this, getString(R.string.app_dialog_save));
            dialog.show();
            MLog.i("修改密码：" + Urls.ChangePassword);
            HttpVolley.volleStringRequestPostJson(Urls.ChangePassword, jsonObject, ncbv);
        }
    }


}
