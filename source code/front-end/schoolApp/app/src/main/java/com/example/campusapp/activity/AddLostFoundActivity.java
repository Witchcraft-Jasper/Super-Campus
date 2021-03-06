package com.example.campusapp.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.VolleyError;
import com.example.campusapp.R;
import com.example.campusapp.activity.adapter.PhotoAdapter;
import com.example.campusapp.activity.adapter.RecyclerItemClickListener;
import com.example.campusapp.application.MyApplication;
import com.example.campusapp.base.BaseActivity;
import com.example.campusapp.entity.MessageContent;
import com.example.campusapp.entity.User;
import com.example.campusapp.util.Constants;
import com.example.campusapp.util.MLog;
import com.example.campusapp.util.ObjectDetector;
import com.example.campusapp.util.ResponseCode;
import com.example.campusapp.util.SharedTools;
import com.example.campusapp.util.ToastUtil;
import com.example.campusapp.util.Utils;
import com.example.campusapp.util.utilNet.HttpVolley;
import com.example.campusapp.util.utilNet.NetCallBackVolley;
import com.example.campusapp.util.utilNet.Urls;
import com.google.gson.Gson;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddLostFoundActivity extends BaseActivity {

    private ImageView imageView;
    private Executor executor = Executors.newSingleThreadExecutor();
    private ObjectDetector detector;

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
                        ToastUtil.showNetworkError(AddLostFoundActivity.this);
                    } else {
                        ToastUtil.show(AddLostFoundActivity.this, msgStr);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @BindView(R.id.et_title)
    EditText et_title;
    @BindView(R.id.et_content)
    EditText et_content;
    @BindView(R.id.recyclerView_image)
    RecyclerView recyclerView_image;
    @BindView(R.id.ll_save)
    LinearLayout ll_save;
    @BindView(R.id.et_tag)
    EditText et_tag;
    @BindView(R.id.title_tag)
    TextView title_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lost_found);
        ButterKnife.bind(this);
        imageView = findViewById(R.id.detectView);
        initBaseViews();
        setTitle("????????????");
        type = getIntent().getIntExtra("type", 0);
        if (type == 3) {
            messageContent = (MessageContent) getIntent().getSerializableExtra("messageContent");
        }
        user = (User) SharedTools.readObject(SharedTools.User);
        gson = new Gson();

        detector = new ObjectDetector("labels.txt", "model.pb", getAssets());
        try {
            detector.load();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
        }
        initViews();
    }

    int type;
    MessageContent messageContent;
    User user;
    Gson gson;
    PhotoAdapter photoAdapter;
    ArrayList<String> photoLists;

    private void initViews() {
        photoLists = new ArrayList<>();
        if (type == 3) {
            et_title.setText(messageContent.getMessageTitle());
            et_content.setText(messageContent.getMessageContent());
            if (messageContent.getImages() != null) {
                for (int i = 0; i < messageContent.getImages().size(); i++) {
                    photoLists.add(Urls.BASE_IMG + messageContent.getImages().get(i).getImageUrl());
                }
            }
        }
        photoAdapter = new PhotoAdapter(AddLostFoundActivity.this, photoLists);
        recyclerView_image.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        if (type == 1) {
            photoAdapter.setMAX(1);
        } else {
            photoAdapter.setMAX(6);
        }
        recyclerView_image.setAdapter(photoAdapter);
        recyclerView_image.addOnItemTouchListener(new RecyclerItemClickListener(AddLostFoundActivity.this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (photoAdapter.getItemViewType(position) == PhotoAdapter.TYPE_ADD) {
                            // ???????????????????????????
                            if (AndPermission.hasPermission(AddLostFoundActivity.this, Manifest.permission.CAMERA)
                                    && AndPermission.hasPermission(AddLostFoundActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                showPhotoPop();
                            } else {
                                // ???????????????
                                AndPermission.with(AddLostFoundActivity.this)
                                        .requestCode(101)
                                        .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .send();
                            }

                        } else {
                            PhotoPreview.builder()
                                    .setPhotos(photoLists)
                                    .setCurrentItem(position)
                                    .start(AddLostFoundActivity.this);
                        }
                    }
                }));
    }


    @OnClick({R.id.ll_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_save:
                saveData();
                break;
        }
    }

    Dialog dialog;
    int messageId;

    private void saveData() {
        if (photoLists.size() == 0) {
            ToastUtil.show(this, "???????????????");
            return;
        }
        MessageContent newMessageContent = new MessageContent();
        if (type == 1) {
            newMessageContent.setMessageType(1);

        } else if (type == 2) {
            newMessageContent.setMessageType(2);
        }

        if (messageContent != null) {
            newMessageContent.setMessageId(messageContent.getMessageId());
            newMessageContent.setDeleteiImages(messageContent.getDeleteiImages());
        }
        newMessageContent.setMessageTitle(et_title.getText().toString());
        newMessageContent.setMessageContent(et_content.getText().toString());
        if (type == 1) {
            newMessageContent.setTag(et_tag.getText().toString());
        }
        if ("".equals(newMessageContent.getMessageTitle())) {
            ToastUtil.show(this, "???????????????");
            return;
        }
        if ("".equals(newMessageContent.getMessageContent())) {
            ToastUtil.show(this, "???????????????");
            return;
        }
        newMessageContent.setUserId(user.getId());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(new Date());
        newMessageContent.setCreateTime(time);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("create_time", newMessageContent.getCreateTime());
            jsonObject.put("content", URLEncoder.encode(newMessageContent.getMessageContent(), "UTF-8"));
            jsonObject.put("title", URLEncoder.encode(newMessageContent.getMessageTitle(), "UTF-8"));
            jsonObject.put("user_id", String.valueOf(newMessageContent.getUserId()));
            jsonObject.put("tag", URLEncoder.encode(newMessageContent.getTag(), "UTF-8"));
            jsonObject.put("type", "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("????????????:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int responseCode = jsonObject.getInt("code");
                    if (responseCode != 0) {
                        messageId = responseCode;
                        saveImage();
                    } else {
                        dialog.dismiss();
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
                dialog.dismiss();
            }
        };
        if (Utils.isNetworkAvailable(this)) {
            dialog = Utils.createLoadingDialog(this, getString(R.string.app_dialog_save));
            dialog.show();
            String path = Urls.AddMessage;
            MLog.i("???????????????" + path);
            HttpVolley.volleStringRequestPostJson(path, jsonObject, ncbv);
        }
    }

    int index = 0;//???????????????????????????
    List<String> saveImageUrlList;

    /**
     * ????????????
     */
    private void saveImage() {
        saveImageUrlList = new ArrayList<>();
        if (photoLists != null && photoLists.size() > 0) {
            for (int i = 0; i < photoLists.size(); i++) {
                if (!photoLists.get(i).contains("http")) {
                    saveImageUrlList.add(photoLists.get(i));
                }
            }
        }

        if (saveImageUrlList.size() > 0) {
            if (Utils.isNetworkAvailable(this)) {
                compress(saveImageUrlList.get(index));
            }
        } else {
            dialog.dismiss();
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
                .url(Urls.AddImage)//??????
                .addHeader("messageId", messageId + "")
                .post(requestBody)//???????????????
                .build();

        MyApplication.getOkHttpClient()
                .newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("????????????:e.getLocalizedMessage() = " + e.getLocalizedMessage());
                        //????????????
                        compress(saveImageUrlList.get(index));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        MLog.i("????????????:" + result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int responseCode = jsonObject.getInt("code");
                            if (responseCode == ResponseCode.CODE_SUCCESS) {
                                if (index < saveImageUrlList.size() - 1) {
                                    index++;
                                    compress(saveImageUrlList.get(index));
                                } else {
                                    //????????????????????????
                                    saveDateHandler.sendEmptyMessage(1);
                                }
                            } else {
                                String strmsg = "????????????";
                                MLog.i("?????????" + index + "????????????errorMsg:" + strmsg);
                                //????????????
                                compress(saveImageUrlList.get(index));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    Handler saveDateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    dialog.dismiss();
                    ToastUtil.show(AddLostFoundActivity.this, "????????????");
                    finish();
                    break;
                case 2:
                    if (index < saveImageUrlList.size() - 1) {
                        index++;
                        compress(saveImageUrlList.get(index));
                    } else {
                        //????????????????????????
                        saveDateHandler.sendEmptyMessage(1);
                    }
                    break;
            }

        }
    };

    private void compress(String path) {
        File file = new File(path);
        if (!file.exists()) {//???????????????
            saveDateHandler.sendEmptyMessage(2);
        }
        saveImage(file);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            if (messageContent != null && messageContent.getImages() != null && messageContent.getImages().size() > 0) {
                for (int i = 0; i < photoLists.size(); i++) {
                    boolean isExists = false;
                    if (photoLists.get(i).contains("http")) {
                        for (int j = 0; j < photos.size(); j++) {
                            if (photoLists.get(i).equals(photos.get(j))) {
                                isExists = true;
                                break;
                            }
                        }
                    }
                    if (!isExists) {
                        for (int j = 0; j < messageContent.getImages().size(); j++) {
                            if (photoLists.get(i).contains(messageContent.getImages().get(j).getImageUrl())) {
                                messageContent.getDeleteiImages().add(messageContent.getImages().get(j));
                            }
                        }
                    }
                }
            }

            photoLists.clear();

            if (photos != null) {
                for (int j = 0; j < photos.size(); j++) {
                    final ProgressDialog progress = new ProgressDialog(this);
                    final int index = j + 1;
                    progress.setTitle("?????????");
                    progress.setMessage("????????????");
                    progress.setCancelable(false);
                    progress.show();

                    List<String> finalPhotos = photos;
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap originImage = BitmapFactory.decodeFile(finalPhotos.get(index - 1));
                            Bitmap bitmapInput = Bitmap.createBitmap(detector.getInputSize(), detector.getInputSize(), Bitmap.Config.ARGB_8888);
                            final Matrix originToInput = Utils.getImageTransformationMatrix(
                                    originImage.getWidth(), originImage.getHeight(), detector.getInputSize(), detector.getInputSize(),
                                    0, false);
                            final Canvas canvas = new Canvas(bitmapInput);
                            canvas.drawBitmap(originImage, originToInput, null);

                            final List<DetectionResult> results = detector.detect(bitmapInput, 0.6f);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.hide();
                                    final Bitmap copiedImage = originImage.copy(Bitmap.Config.ARGB_8888, true);
                                    final Canvas resultCanvas = new Canvas(copiedImage);
                                    final Paint paint = new Paint();
                                    paint.setColor(Color.RED);
                                    paint.setStyle(Paint.Style.STROKE);
                                    paint.setStrokeWidth(25.0f);

                                    Paint textPaint = new Paint();
                                    textPaint.setColor(Color.WHITE);
                                    textPaint.setStyle(Paint.Style.FILL);
                                    textPaint.setTextSize((float) (0.09 * copiedImage.getWidth()));
                                    Matrix inputToOrigin = new Matrix();
                                    originToInput.invert(inputToOrigin);
                                    if (!results.isEmpty()) {
                                        for (DetectionResult result : results) {
                                            RectF box = result.getBox();
                                            inputToOrigin.mapRect(box);
                                            resultCanvas.drawRect(box, paint);
                                            resultCanvas.drawText(result.getLabel(), box.left, box.top, textPaint);
                                        }
                                        imageView.setImageBitmap(copiedImage);
                                        et_tag.setText(results.get(0).getLabel());
                                        Toast.makeText(getApplicationContext(), "???????????????" + index + "???????????????: " + results.get(0).getLabel(), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "??????" + index + "???????????????????????????", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                }
                photoLists.addAll(photos);
            }
            photoAdapter.notifyDataSetChanged();
        }

        if (requestCode == REQUEST_CAPTURE) {
            if (resultCode != 0) {
                photoLists.add(path);
                photoAdapter.notifyDataSetChanged();
            } else {
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                    MLog.i("????????????");
                }
            }
        }
    }


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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // ??????????????????????????????????????????AndPermission???????????????????????????PermissionListener???
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // ???????????????????????????
            showPhotoPop();
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // ???????????????????????????
            // ?????????????????????????????????????????????????????????????????????????????????????????????
            if (AndPermission.hasAlwaysDeniedPermission(AddLostFoundActivity.this, deniedPermissions)) {

                // ???????????????????????????????????????
                AndPermission.defaultSettingDialog(AddLostFoundActivity.this, 300)
                        .setTitle(getString(R.string.permission_fail_apply))
                        .setMessage(getString(R.string.permission_fail_apply_message_one))
                        .setPositiveButton(getString(R.string.permission_set))
                        .show();
            }
        }
    };


    //????????????
    private static final int REQUEST_CAPTURE = 100;
    String path = "";//????????????????????????????????????

    private void showPhotoPop() {

        View photoPopView = LayoutInflater.from(AddLostFoundActivity.this).inflate(R.layout.my_information_pop, null);

        RelativeLayout rl_mip_xc = (RelativeLayout) photoPopView.findViewById(R.id.rl_mip_xc);
        RelativeLayout rl_mip_pz = (RelativeLayout) photoPopView.findViewById(R.id.rl_mip_pz);
        RelativeLayout rl_mip_qx = (RelativeLayout) photoPopView.findViewById(R.id.rl_mip_qx);

        final PopupWindow photpPop = new PopupWindow(photoPopView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        photpPop.setOutsideTouchable(true);
        photpPop.setBackgroundDrawable(new BitmapDrawable());

        photpPop.setTouchable(true);
        // ???????????????????????????????????????????????????EditText????????????????????????
        photpPop.setFocusable(true);
        //???????????????
        WindowManager.LayoutParams params = AddLostFoundActivity.this.getWindow().getAttributes();
        params.alpha = 0.5f;
        AddLostFoundActivity.this.getWindow().setAttributes(params);
        // ????????????????????????????????????????????????????????????
        photpPop.setAnimationStyle(R.style.mypopwindow_anim_style);
        photpPop.showAtLocation(recyclerView_image, Gravity.BOTTOM, 0, 0);

        photpPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = AddLostFoundActivity.this.getWindow().getAttributes();
                params.alpha = 1f;
                AddLostFoundActivity.this.getWindow().setAttributes(params);
            }
        });


        rl_mip_xc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == 1) {
                    PhotoPicker.builder()
                            .setPhotoCount(1)
                            .setShowCamera(true)
                            .setPreviewEnabled(false)
                            .setSelected(photoLists)
                            .start(AddLostFoundActivity.this);
                } else {
                    PhotoPicker.builder()
                            .setPhotoCount(6)
                            .setShowCamera(true)
                            .setPreviewEnabled(false)
                            .setSelected(photoLists)
                            .start(AddLostFoundActivity.this);
                }
                photpPop.dismiss();
            }
        });

        rl_mip_pz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photpPop.dismiss();
                path = String.valueOf(AddLostFoundActivity.this.getExternalCacheDir());
                String filename = Utils.getNowTimeYYYYMMDDHHmmssSSS() + ".jpg";
                File file = new File(path, filename);
                if (!file.exists()) {
                    //????????????????????????????????????
                    try {
                        boolean makeFile = file.createNewFile();
                        System.out.println(makeFile + "????????");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoOutputUri;
                photoOutputUri = FileProvider.getUriForFile(AddLostFoundActivity.this,
                        AddLostFoundActivity.this.getPackageName() + ".fileprovider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoOutputUri);
                startActivityForResult(intent, REQUEST_CAPTURE);
                path = path + filename;
                MLog.i("????????????????????????:" + path);

            }
        });

        rl_mip_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photpPop.dismiss();
            }
        });
    }
}
