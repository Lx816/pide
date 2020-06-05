package www.pide.com.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import www.example.liangmutian.mypicker.DataPickerDialog;
import com.google.gson.Gson;

import okhttp3.MediaType;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.ProfessionsBean;
import www.pide.com.bean.ProfessionsBean2;
import www.pide.com.bean.UserImgBean;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.QTime;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;
import www.pide.com.utils.Util;

import www.pide.com.R;

import www.pide.com.ui.view.CustomProgressDialog;
import www.pide.com.ui.view.XCRoundImageView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompleteInformationActivity extends BaseActivity {
    private ImageView down;
    private DataPickerDialog chooseDialog;
    private TextView mTextView;
    private List<String> list = new ArrayList<>();
    private EditText t_name, t_profession, t_company, t_need_resources, t_possession_resources;
    private String itemValue = "";
    private Context context;
    private List<ProfessionsBean2> professionsList = new ArrayList<>();
    private ImageView skip;
    private XCRoundImageView avatar_image;
    private Button add;
    private String name, profession, company, need_resources, possession_resources, profession_id;
    private byte[] mContent;
    Bitmap myBitmap;
    private CustomProgressDialog dialog;
    public static final int EXTERNAL_STORAGE_REQ_CODE = 10 ;
    private int avatar_image_id=-1;
    private String sign="";
    private String avatar="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTranslucentStatus_w();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_informat_actiivity);
        context = this;
        initView();
        initOnclick();
        intitProfessions();
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQ_CODE);
        }
    }

    private void initView() {
        down = (ImageView) findViewById(R.id.down);
        mTextView = (TextView) findViewById(R.id.textView);
        skip = (ImageView) findViewById(R.id.skip);
        t_name = (EditText) findViewById(R.id.name);
        t_profession = (EditText) findViewById(R.id.profession);
        t_company = (EditText) findViewById(R.id.company);
        t_need_resources = (EditText) findViewById(R.id.need_resources);
        t_possession_resources = (EditText) findViewById(R.id.possession_resources);
        add = (Button) findViewById(R.id.add);
        avatar_image = findViewById(R.id.avatar_image);
    }
    private Uri imageUri;
    private void initOnclick() {
        View.OnClickListener listener;
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.down:
                        down.setImageResource(R.mipmap.up);
                        showChooseDialog(list);
                        break;
                    case R.id.skip:
                        Intent intent=new Intent(CompleteInformationActivity.this,WCardActivity.class);
                        intent.putExtra("name","ws");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.add:
                        add();
                        break;
                    case R.id.avatar_image:
                        File outputImage = new File(Environment.getExternalStorageDirectory(),
                                "output_image.jpg");
                        imageUri = Uri.fromFile(outputImage);

                        try {
                            if (outputImage.exists()) {
                                outputImage.delete();
                            }
                            outputImage.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent intent0 = new Intent(Intent.ACTION_PICK,null);
                        //此处调用了图片选择器
                        //如果直接写intent.setDataAndType("image/*");
                        //调用的是系统图库
                        intent0.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        intent0.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent0, 1);
                        break;
                }
            }
        };
        down.setOnClickListener(listener);
        skip.setOnClickListener(listener);
        avatar_image.setOnClickListener(listener);
        add.setOnClickListener(listener);
    }

    /**
     * chooseDialog
     */
    private void showChooseDialog(List<String> mlist) {
        DataPickerDialog.Builder builder = new DataPickerDialog.Builder(this);
        chooseDialog = builder.setData(mlist).setSelection(0).setTitle("取消")
                .setOnDataSelectedListener(new DataPickerDialog.OnDataSelectedListener() {
                    @Override
                    public void onDataSelected(String mitemValue, int position) {
                        itemValue = mitemValue;
                        t_profession.setText(mitemValue);
                        profession_id = professionsList.get(position).getpId()+"";
                    }

                    @Override
                    public void onCancel() {
                        down.setImageResource(R.mipmap.down);
                    }
                }).create();
        chooseDialog.show();
        if (itemValue != "") {
            chooseDialog.setSelection(itemValue);
        }
    }

    private void intitProfessions() {
        Map<String,String> map=new HashMap<>();
        map.put("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN));
        OkHttpUtils.postString().url(URLs.IMGEURL + URLs.PDPROFESSIONSINFO+"?pageNum=1&pageSize=100")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(new Gson().toJson(map))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                UIHelper.ToastMessageCenter(context, "网络未连接！", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                ProfessionsBean p1= JsonUtil.fromJson(response,ProfessionsBean.class);
                professionsList = p1.getData().getRecords();
                for (ProfessionsBean2 str : professionsList) {
                    list.add(str.getName());
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //此处启动裁剪程序
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    //此处注释掉的部分是针对android 4.4路径修改的一个测试
                    //有兴趣的读者可以自己调试看看
                    String text=imageUri.toString();
                    intent.setDataAndType(data.getData(), "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, 3);
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    try {
                        //将output_image.jpg对象解析成Bitmap对象，然后设置到ImageView中显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(imageUri));
                        File head=compressImage(bitmap);
                        if (head != null) {
                            Util.showLoadingDialog(this, "加载中...");
                            /**
                             * 上传服务器代码
                             */
                            final String fileName = head.getName();
                            OkHttpUtils
                                    .post()
                                    .url(URLs.IMGEURL + URLs.UPLOADIMAGE)
                                    .addFile("file", fileName, head)
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(okhttp3.Call call, Exception e) {
                                            Util.closeLoadingDialog(context);
                                            UIHelper.ToastMessageCenter(context, "网络连接失败", 200);
                                        }

                                        @Override
                                        public void onResponse(okhttp3.Call call, String response) {
                                            Util.closeLoadingDialog(context);
                                            UserImgBean userImgBean = JsonUtil.fromJson(response, UserImgBean.class);
                                            if(userImgBean.getSuccess()){
                                                avatar=userImgBean.getData().getImage_url();
                                                Glide.with(context)
                                                        .load(userImgBean.getData().getImage_url())
                                                        .apply(new RequestOptions().placeholder(R.drawable.error))
                                                        .apply(new RequestOptions().error(R.drawable.error))
                                                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                                                        .into(avatar_image);
                                                UIHelper.ToastMessageCenter(CompleteInformationActivity.this, "上传成功", 2000);
                                            }else {
                                                UIHelper.ToastMessageCenter(context, userImgBean.getMsg(), 200);
                                            }
                                        }
                                    });
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public static String getImagePathFromURI(Activity activity, Uri uri) {
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        String path = null;
        if (cursor != null) {
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = activity.getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            if (cursor != null) {
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            }
        }
        return path;
    }
    private void add() {
        Util.showLoadingDialog(this,"加载中");
        String mcurrentTime= QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        name = t_name.getText().toString().trim();
        profession = t_profession.getText().toString().trim();
        company = t_company.getText().toString().trim();
        need_resources = t_need_resources.getText().toString().trim();
        possession_resources = t_possession_resources.getText().toString().trim();
        if(avatar.length()>0){
            if(name.length()>0 && profession.length()>0 && company.length()>0 && possession_resources.length()>0 && need_resources.length()>0){
                Map<String,String> map=new HashMap<>();
                map.put("currentTime",mcurrentTime);
                map.put("sign", sign);
                map.put("avatar", avatar);
                map.put("name", name);
                map.put("professionId", profession_id);
                map.put("company", company);
                map.put("possessionResources", possession_resources);
                map.put("needResources", need_resources);
                OkHttpUtils.postString().url(URLs.IMGEURL + URLs.EDIT)
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                        .content(new Gson().toJson(map))
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(okhttp3.Call call, Exception e) {
                                Util.closeLoadingDialog(context);
                                UIHelper.ToastMessageCenter(CompleteInformationActivity.this, "添加失败", 2000);
                            }

                            @Override
                            public void onResponse(okhttp3.Call call, String response) {
                                Util.closeLoadingDialog(context);
                                UIHelper.ToastMessageCenter(CompleteInformationActivity.this, "添加成功", 2000);
                                UIHelper.OpenActivity(context, WCardActivity.class);
                                finish();
                            }
                        });
            }else if(name.length()==0){
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(CompleteInformationActivity.this, "请输入姓名", 2000);
            }else if(profession.length()==0){
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(CompleteInformationActivity.this, "请选择行业", 2000);
            }else if(company.length()==0){
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(CompleteInformationActivity.this, "请输入公司名称", 2000);
            }else if(possession_resources.length()==0){
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(CompleteInformationActivity.this, "请输入拥有资源", 2000);
            }else if(need_resources.length()==0){
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(CompleteInformationActivity.this, "请输入需要资源", 2000);
            }
        }else {
            Util.closeLoadingDialog(context);
            UIHelper.ToastMessageCenter(CompleteInformationActivity.this, "请上传头像", 2000);
        }
    }
    /**
     * 压缩图片（质量压缩）
     * @param bitmap
     */
    public static File compressImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 500) {  //循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            long length = baos.toByteArray().length;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String filename = format.format(date);
        File file = new File(Environment.getExternalStorageDirectory(),filename+".png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        recycleBitmap(bitmap);
        return file;
    }
    public static void recycleBitmap(Bitmap... bitmaps) {
        if (bitmaps==null) {
            return;
        }
        for (Bitmap bm : bitmaps) {
            if (null != bm && !bm.isRecycled()) {
                bm.recycle();
            }
        }
    }
}
