package com.example.campusapp.activity.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.campusapp.LoginActivity;
import com.example.campusapp.R;
import com.example.campusapp.activity.ClipImageActivity;
import com.example.campusapp.activity.MyCommentActivity;
import com.example.campusapp.activity.MyMessageActivity;
import com.example.campusapp.activity.SystemMessageActivity;
import com.example.campusapp.activity.UpdatePassWordActivity;
import com.example.campusapp.application.MyApplication;
import com.example.campusapp.entity.User;
import com.example.campusapp.util.MLog;
import com.example.campusapp.util.SharedTools;
import com.example.campusapp.util.ToastUtil;
import com.example.campusapp.util.utilNet.Urls;
import com.example.campusapp.view.ImageTextView;
import com.example.campusapp.view.cameraView.CircleImageView;
import com.yanzhenjie.permission.AndPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MyFragment extends Fragment {

    View view;
    @BindView(R.id.personal_center_avatar)
    CircleImageView personal_center_avatar;
    @BindView(R.id.tv_persion_center_name)
    TextView tv_persion_center_name;
    @BindView(R.id.tv_personal_center_position)
    TextView tv_personal_center_position;
    @BindView(R.id.ll_personal_center)
    LinearLayout ll_personal_center;
    @BindView(R.id.iv_personal_center_checkCar_statistics)
    ImageView iv_personal_center_checkCar_statistics;
    @BindView(R.id.rl_my_lost_found)
    RelativeLayout rl_my_lost_found;
    @BindView(R.id.iv_personal_center_course)
    ImageView iv_personal_center_course;
    @BindView(R.id.rl_my_message_wall)
    RelativeLayout rl_my_message_wall;
    @BindView(R.id.iv_personal_center_system_set)
    ImageTextView iv_personal_center_system_set;
    @BindView(R.id.rl_personal_center_system_set)
    RelativeLayout rl_personal_center_system_set;
    @BindView(R.id.rl_personal_center_out_login)
    RelativeLayout rl_personal_center_out_login;
    @BindView(R.id.rl_my_comment)
    RelativeLayout rl_my_comment;
    Unbinder unbinder;
    Uri phone = Uri.parse("tel:10086");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_fragment, null);
        unbinder = ButterKnife.bind(this, view);
        user = (User) SharedTools.readObject(SharedTools.User);
        createCameraTempFile(savedInstanceState);
        initViews();
        return view;
    }

    User user;

    private void initViews() {
        try {
            tv_persion_center_name.setText(URLDecoder.decode(user.getRealname(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Glide.with(this)
                .load(user.getImageurl())
                .error(R.mipmap.default_avatar)
                .into(personal_center_avatar);
    }


    @OnClick({R.id.iv_system_message, R.id.rl_my_comment, R.id.rl_personal_center_out_login, R.id.personal_center_avatar, R.id.ll_personal_center, R.id.rl_my_lost_found, R.id.rl_my_message_wall, R.id.rl_personal_center_system_set, R.id.rl_my_assist_call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.personal_center_avatar:
            case R.id.ll_personal_center:
                // ???????????????????????????
                if (AndPermission.hasPermission(
                        getActivity(), Manifest.permission.CAMERA) && AndPermission.hasPermission(
                        getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showPop();
                } else {
                    // ???????????????
                    AndPermission.with(getActivity())
                            .requestCode(100)
                            .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .send();
                }
                break;
            case R.id.rl_my_lost_found:
                Intent myLostFound = new Intent(getActivity(), MyMessageActivity.class);
                myLostFound.putExtra("type", 1);
                startActivity(myLostFound);
                break;
            case R.id.rl_my_message_wall:
                Intent myMessageWall = new Intent(getActivity(), MyMessageActivity.class);
                myMessageWall.putExtra("type", 2);
                startActivity(myMessageWall);
                break;
            case R.id.rl_personal_center_system_set:
                Intent intent = new Intent(getActivity(), UpdatePassWordActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_personal_center_out_login:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("??????");
                builder.setMessage("????????????????????????");
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedTools.deleteDate(SharedTools.Password);
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        requireActivity().finish();

                    }
                });
                builder.setNegativeButton("??????", null);
                builder.create().show();
                break;
            case R.id.rl_my_comment:
                Intent myComment = new Intent(getActivity(), MyCommentActivity.class);
                startActivity(myComment);
                break;
            case R.id.iv_system_message:
                Intent systemMessage = new Intent(getActivity(), SystemMessageActivity.class);
                startActivity(systemMessage);
                break;
            case R.id.rl_my_assist_call:
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(phone);
                startActivity(dialIntent);
            default:
                break;
        }
    }


    //????????????
    private static final int REQUEST_CAPTURE = 100;
    //????????????
    private static final int REQUEST_PICK = 101;
    //????????????
    private static final int REQUEST_CROP_PHOTO = 102;
    //???????????????????????????????????????
    private File tempFile;


    PopupWindow settlementPop;

    private void showPop() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.my_information_pop, null);
        RelativeLayout rl_mip_xc = (RelativeLayout) view.findViewById(R.id.rl_mip_xc);
        RelativeLayout rl_mip_pz = (RelativeLayout) view.findViewById(R.id.rl_mip_pz);
        RelativeLayout rl_mip_qx = (RelativeLayout) view.findViewById(R.id.rl_mip_qx);


        settlementPop = new PopupWindow(view, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        settlementPop.setOutsideTouchable(true);
        settlementPop.setBackgroundDrawable(new BitmapDrawable());
        //??????PopupWindow??????????????????
        settlementPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        settlementPop.setTouchable(true);
        //???????????????

        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.alpha = 0.5f;
        getActivity().getWindow().setAttributes(params);

        // ???????????????????????????????????????????????????EditText????????????????????????
        settlementPop.setFocusable(true);
        // ????????????????????????????????????????????????????????????
        settlementPop.setAnimationStyle(R.style.mypopwindow_anim_style);

        settlementPop.showAtLocation(ll_personal_center, Gravity.BOTTOM, 0, 0);

        settlementPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                params.alpha = 1f;
                getActivity().getWindow().setAttributes(params);
            }
        });

        rl_mip_xc.setOnClickListener(new View.OnClickListener() {//????????????
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_PICK);

            }
        });

        rl_mip_pz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * ??????????????????
                 */
                tempFile = new File(String.valueOf(getContext().getExternalCacheDir()), "output_image.jpg");
                try {
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                    tempFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(tempFile.getAbsolutePath() + "!!!!");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoOutputUri;
                photoOutputUri = FileProvider.getUriForFile(MyFragment.this.requireContext(),
                        getActivity().getPackageName() + ".fileprovider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoOutputUri);
                startActivityForResult(intent, REQUEST_CAPTURE);
            }
        });

        rl_mip_qx.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             settlementPop.dismiss();
                                         }
                                     }

        );

    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param savedInstanceState
     */
    private void createCameraTempFile(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("tempFile")) {
            tempFile = (File) savedInstanceState.getSerializable("tempFile");
        } else {
            tempFile = new File(checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"),
                    System.currentTimeMillis() + ".jpg");
        }
        MLog.i("tempFile:" + tempFile.getPath());
    }

    /**
     * ????????????????????????
     */
    private static String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("tempFile", tempFile);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (settlementPop != null) {
            settlementPop.dismiss();
        }
        switch (requestCode) {
            case REQUEST_CAPTURE: //????????????????????????
                if (resultCode == Activity.RESULT_OK) {
                    gotoClipActivity(Uri.fromFile(tempFile));
                }
                break;
            case REQUEST_PICK:  //????????????????????????
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = intent.getData();
                    gotoClipActivity(uri);
                }
                break;
            case REQUEST_CROP_PHOTO:  //??????????????????
                if (resultCode == Activity.RESULT_OK) {
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    final String cropImagePath = getRealFilePathFromUri(getActivity().getApplicationContext(), uri);
                    Bitmap bitMap = BitmapFactory.decodeFile(cropImagePath);
                    personal_center_avatar.setImageBitmap(bitMap);
                    MLog.i("URI:" + cropImagePath);
                    compress(cropImagePath);

                }
                break;
            default:
                break;
        }
    }


    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");

    private void saveImage(File file) {

        // mImgUrl??????????????????url??????
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("file", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
        //???????????????
        RequestBody requestBody = builder.build();

        //????????????
        Request request = new Request.Builder()
                .url(Urls.ChangeHeadPortrait)//??????
                .addHeader("userId", user.getId() + "")
                .post(requestBody)//???????????????
                .build();
        System.out.println("HO");

        MyApplication.getOkHttpClient()
                .newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        MLog.i("?????????????????????????????????");
                        saveDateHandler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        MLog.i("???????????????" + result);
                        if (result != null && !"".equals(result)) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                user.setImageurl(jsonObject.getString("data"));
                                SharedTools.saveObject(user, SharedTools.User);
                                saveDateHandler.sendEmptyMessage(1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    //????????????
    private void compress(String path) {
        File file = null;
        try {
            file = new File(path);
        } catch (Exception e) {
            saveDateHandler.sendEmptyMessage(2);
            e.printStackTrace();
        }
        if (file != null && file.exists()) {
            saveImage(file);
        } else {
            saveDateHandler.sendEmptyMessage(2);

        }
    }


    Handler saveDateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ToastUtil.show(getActivity(), "??????????????????");
                    break;
                case 2:
                    ToastUtil.show(getActivity(), "??????????????????????????????!");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * ??????????????????
     *
     * @param uri
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), ClipImageActivity.class);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    /**
     * ??????Uri????????????????????????
     * ?????????file:///????????? ??? content://???????????????
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
