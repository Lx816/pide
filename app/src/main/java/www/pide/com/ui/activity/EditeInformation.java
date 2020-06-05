package www.pide.com.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import www.example.liangmutian.mypicker.DataPickerDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.lzy.imagepicker.bean.ImageItem;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.ProfessionsBean;
import www.pide.com.bean.ProfessionsBean2;
import www.pide.com.bean.ShengBean;
import www.pide.com.bean.Sticky;
import www.pide.com.bean.User;
import www.pide.com.bean.UserBean;
import www.pide.com.bean.UserBean1;
import www.pide.com.bean.UserImgBean;
import www.pide.com.utils.GetJsonDataUtil;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.QTime;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;
import www.pide.com.utils.Util;

import www.pide.com.R;

import www.pide.com.ui.view.BottomWindow;
import www.pide.com.ui.view.XCRoundImageView;

/**
 * Created by Administrator on 2020/3/25.
 */
public class EditeInformation extends BaseActivity {
    private RelativeLayout back;
    private BottomWindow bottomWindow;
    private TextView phone;
    private RelativeLayout r_phone;
    private XCRoundImageView up_img;
    private Context context;
    private List<ProfessionsBean2> professionsList = new ArrayList<>();
    private List<String> list = new ArrayList<>();
    private DataPickerDialog chooseDialog;
    private TextView t_profession;
    private String profession_id = "-1";
    private RelativeLayout r4, r5, r6, r7, r8;
    private byte[] mContent;
    Bitmap myBitmap;
    public static final int EXTERNAL_STORAGE_REQ_CODE = 10;
    private TextView save_top;
    private Button save;
    private EditText name, company, position;
    private TextView address, possession_resources, need_resources, introduce;
    private String t_name, t_company, t_address;
    private int selectedPositin = 0;
    private User user = new User();
    //  省
    private List<ShengBean> options1Items = new ArrayList<ShengBean>();
    //  市
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    //  区
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private static String regEx = "[\u4e00-\u9fa5]"; // 中文范围
    private String mpossession_resources, mneed_resources, mintroduce;
    private UserBean1 user1;
    private String sign = "";
    private String avatar="";
    private String itemValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        setTranslucentStatus_b(context, R.color.white);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edite_information);
        //注册订阅者
        EventBus.getDefault().register(this);
        initView();
        initOnclick();
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQ_CODE);
        }
    }

    private void initView() {
        back = (RelativeLayout) findViewById(R.id.back);
        phone = (TextView) findViewById(R.id.phone);
        r_phone = (RelativeLayout) findViewById(R.id.r_phone);
        up_img = (XCRoundImageView) findViewById(R.id.up_img);
        t_profession = (TextView) findViewById(R.id.t_profession);
        r4 = (RelativeLayout) findViewById(R.id.r4);
        r5 = (RelativeLayout) findViewById(R.id.r5);
        r6 = (RelativeLayout) findViewById(R.id.r6);
        r7 = (RelativeLayout) findViewById(R.id.r7);
        save_top = (TextView) findViewById(R.id.save_top);
        save = (Button) findViewById(R.id.save);
        name = findViewById(R.id.name);
        company = findViewById(R.id.company);
        address = findViewById(R.id.address);
        possession_resources = findViewById(R.id.possession_resources);
        need_resources = findViewById(R.id.need_resources);
        introduce = findViewById(R.id.introduce);
        r8 = (RelativeLayout) findViewById(R.id.r8);
        position = findViewById(R.id.position);
        intitProfessions();
        initData();
    }

    private void initData() {
        Util.showLoadingDialog(this, "加载中");
        OkHttpUtils.post().url(URLs.IMGEURL + URLs.MYDETAIL)
                .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                Util.closeLoadingDialog(context);
                UIHelper.ToastMessageCenter(context, "获取数据失败", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                user1 = JsonUtil.fromJson(response, UserBean1.class);
                if (user1.isSuccess()) {
                    user = user1.getData();
                    name.setText(user.getName());
                    company.setText(user.getCompany());
                    phone.setText(user.getPhone());
                    t_profession.setText(user.getProfession_name());
                    address.setText(user.getAddress());
                    mpossession_resources = user.getPossession_resources();
                    mneed_resources = user.getNeed_resources();
                    mintroduce = user.getIntroduce();
                    position.setText(user.getPosition());
                    if (user.getPossession_resources() != null) {
                        possession_resources.setText(formatText(user.getPossession_resources(), 5));
                    }
                    if (user.getNeed_resources() != null) {
                        need_resources.setText(formatText(user.getNeed_resources(), 5));
                    }
                    if (user.getIntroduce() != null) {
                        introduce.setText(formatText(user.getIntroduce(), 5));
                    }
                    if(user.getAvatar()!=null && user.getAvatar()!=""){
                        avatar=user.getAvatar();
                    }
                    Glide.with(context)
                            .load(user.getAvatar())
                            .apply(new RequestOptions().placeholder(R.drawable.error))
                            .apply(new RequestOptions().error(R.drawable.error))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                            .into(up_img);
                    if (user.getProfession_name() != null) {
                        for (int i = 0; i < professionsList.size(); i++) {
                            ProfessionsBean2 s = professionsList.get(i);
                            if (s.getName().equals(user.getProfession_name())) {
                                selectedPositin = i;
                                profession_id = s.getpId() + "";
                            }
                        }
                    }
                    Util.closeLoadingDialog(context);
                } else {
                    Util.closeLoadingDialog(context);
                    UIHelper.ToastMessageCenter(context, user1.getMsg(), 200);
                    if (user1.getCode().equals("401")) {
                        EMClient.getInstance().logout(true);
                        SharedPreferencesUtil.delete(context, "LtAreaPeople");
                        UIHelper.OpenActivity(EditeInformation.this, LoginActivity.class);
                    }
                }
            }
        });
    }
   private Uri imageUri;
    private void initOnclick() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.back:
                        isEdite();
                        break;
                    case R.id.r_phone:
                        BottomPowWindow();
                        break;
                    case R.id.up_img:
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
                    case R.id.r4:
                        showChooseDialog(list);
                        break;
                    case R.id.r5:
                        Intent intent = new Intent(EditeInformation.this, EditeInformationItem.class);
                        intent.putExtra("titleName", "拥有资源");
                        intent.putExtra("name", user.getName());
                        intent.putExtra("num", "60");
                        intent.putExtra("param_name", "possessionResources");
                        intent.putExtra("input_t", mpossession_resources);
                        intent.putExtra("hint", "填写自己拥有的资源");
                        if (user.getName() != null) {
                            startActivityForResult(intent, 2);
                        } else {
                            startActivity(intent);
                        }
                        break;
                    case R.id.r6:
                        Intent intent1 = new Intent(EditeInformation.this, EditeInformationItem.class);
                        intent1.putExtra("titleName", "需要资源");
                        intent1.putExtra("name", user.getName());
                        intent1.putExtra("num", "60");
                        intent1.putExtra("param_name", "needResources");
                        intent1.putExtra("input_t", mneed_resources);
                        intent1.putExtra("hint", "填写自己需要的资源");
                        if (user.getName() != null) {
                            startActivityForResult(intent1, 2);
                        } else {
                            startActivity(intent1);
                        }
                        break;
                    case R.id.r7:
                        Intent intent2 = new Intent(EditeInformation.this, EditeInformationItem.class);
                        intent2.putExtra("titleName", "自我介绍");
                        intent2.putExtra("name", user.getName());
                        intent2.putExtra("num", "100");
                        intent2.putExtra("param_name", "introduce");
                        intent2.putExtra("input_t", mintroduce);
                        intent2.putExtra("hint", "一句话介绍自己的独特之处");
                        startActivityForResult(intent2, 2);
                        break;
                    case R.id.save_top:
                        save();
                        break;
                    case R.id.save:
                        save();
                        break;
                    case R.id.r8:
                        parseData();
                        showPickerView();
                        break;
                }
            }
        };
        back.setOnClickListener(listener);
        r_phone.setOnClickListener(listener);
        up_img.setOnClickListener(listener);
        r4.setOnClickListener(listener);
        r5.setOnClickListener(listener);
        r6.setOnClickListener(listener);
        r7.setOnClickListener(listener);
        save_top.setOnClickListener(listener);
        save.setOnClickListener(listener);
        r8.setOnClickListener(listener);
    }

    private void BottomPowWindow() {
        bottomWindow = new BottomWindow(EditeInformation.this, itemsOnClick, "我知道了", "修改手机号", "您已绑定此手机号", "可通过手机号+验证码的方式登录匹得");
        //设置弹窗位置
        bottomWindow.showAtLocation(EditeInformation.this.findViewById(R.id.ll_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            bottomWindow.dismiss();
            switch (v.getId()) {
                case R.id.item_popupwindows_left:

                    break;
                case R.id.item_popupwindows_right:
                    Intent intent = new Intent(EditeInformation.this, BindPhoneActivity.class);
                    intent.putExtra("phone", user.getPhone());
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }

    };

    private void intitProfessions() {
        list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN));
        OkHttpUtils.postString().url(URLs.IMGEURL + URLs.PDPROFESSIONSINFO + "?pageNum=1&pageSize=100")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(new Gson().toJson(map))
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                UIHelper.ToastMessageCenter(context, "网络未连接！", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                ProfessionsBean p1 = JsonUtil.fromJson(response, ProfessionsBean.class);
                professionsList = p1.getData().getRecords();
                for (ProfessionsBean2 str : professionsList) {
                    list.add(str.getName());
                }
            }
        });
    }

    /**
     * chooseDialog
     */
    private void showChooseDialog(List<String> mlist) {
        DataPickerDialog.Builder builder = new DataPickerDialog.Builder(this);
        chooseDialog = builder.setData(mlist).setSelection(selectedPositin).setTitle("取消")
                .setOnDataSelectedListener(new DataPickerDialog.OnDataSelectedListener() {
                    @Override
                    public void onDataSelected(String mitemValue, int position) {
                        t_profession.setText(mitemValue);
                        profession_id = professionsList.get(position).getpId() + "";
                        selectedPositin = position;
                    }

                    @Override
                    public void onCancel() {
                    }
                }).create();
        chooseDialog.show();
        if (itemValue != "") {
            chooseDialog.setSelection(itemValue);
        }

    }

    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private static final String IMAGE_FILE_NAME = "header.jpg";
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
                                                        .into(up_img);
                                                UIHelper.ToastMessageCenter(EditeInformation.this, "上传成功", 2000);
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

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }
    public String saveImage(String name, Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory().getPath());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * user转换为file文件
     *返回值为file类型
     * @param uri
     * @return
     */
    private File uri2File(Uri uri) {
        String img_path;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = ((Activity) context).managedQuery(uri, proj, null,
                null, null);
        if (actualimagecursor == null) {
            img_path = uri.getPath();
        } else {
            int actual_image_column_index = actualimagecursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            img_path = actualimagecursor
                    .getString(actual_image_column_index);
        }
        File file = new File(img_path);
        return file;
    }

    /**
     * 压缩图片（质量压缩）
     *
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
        File file = new File(Environment.getExternalStorageDirectory(), filename + ".png");
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
        if (bitmaps == null) {
            return;
        }
        for (Bitmap bm : bitmaps) {
            if (null != bm && !bm.isRecycled()) {
                bm.recycle();
            }
        }
    }

    private void save() {
        Util.showLoadingDialog(this, "加载中");
        t_name = name.getText().toString().trim();
        t_company = company.getText().toString().trim();
        t_address = address.getText().toString().trim();
        String mcurrentTime = QTime.QTime();
        sign = QTime.QSign(mcurrentTime);
        if (avatar != null && t_name.length() > 0 && t_company.length() > 0 && t_profession.getText().toString().trim().length() > 0 && possession_resources.getText().toString().trim().length() > 0 && need_resources.getText().toString().trim().length() > 0) {
            Map<String, String> map = new HashMap<>();
            map.put("currentTime", mcurrentTime);
            map.put("sign", sign);
            map.put("avatar", avatar);
            map.put("name", t_name);
            map.put("professionId", profession_id);
            map.put("company", t_company);
            map.put("address", t_address);
            map.put("position", position.getText().toString().trim());
            OkHttpUtils.postString().url(URLs.IMGEURL + URLs.EDIT)
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .addHeader("jwtToken", SharedPreferencesUtil.read(context, "LtAreaPeople", URLs.TOKEN))
                    .content(new Gson().toJson(map))
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
                            UserBean userBean = JsonUtil.fromJson(response, UserBean.class);
                            if (userBean.isSuccess()) {
                                UIHelper.ToastMessageCenter(EditeInformation.this, "保存成功", 2000);
                                EventBus.getDefault().post(new Sticky("2"));
                                finish();
                            } else {
                                UIHelper.ToastMessageCenter(context, userBean.getMsg(), 200);
                            }
                        }
                    });
        } else if (avatar == null) {
            Util.closeLoadingDialog(context);
            UIHelper.ToastMessageCenter(context, "请上传头像", 200);
        } else if (t_name.length() == 0) {
            Util.closeLoadingDialog(context);
            UIHelper.ToastMessageCenter(context, "请输入你的姓名", 200);
        } else if (t_company.length() == 0) {
            Util.closeLoadingDialog(context);
            UIHelper.ToastMessageCenter(context, "请输入你的公司名称", 200);
        } else if (Integer.parseInt(profession_id) < 0) {
            Util.closeLoadingDialog(context);
            UIHelper.ToastMessageCenter(context, "请选择行业", 200);
        } else if (possession_resources.getText().toString().trim().length() == 0) {
            Util.closeLoadingDialog(context);
            UIHelper.ToastMessageCenter(context, "请完善拥有资源信息", 200);
        } else if (need_resources.getText().toString().trim().length() == 0) {
            Util.closeLoadingDialog(context);
            UIHelper.ToastMessageCenter(context, "请完善需要资源信息", 200);
        }
    }

    /**
     * 解析数据并组装成自己想要的list
     */
    private void parseData() {
        String jsonStr = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据
//     数据解析
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<List<ShengBean>>() {
        }.getType();
        List<ShengBean> shengList = gson.fromJson(jsonStr, type);
//     把解析后的数据组装成想要的list
        options1Items = shengList;
//     遍历省
        for (int i = 0; i < shengList.size(); i++) {
//         存放城市
            ArrayList<String> cityList = new ArrayList<>();
//         存放区
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();
//         遍历市
            for (int c = 0; c < shengList.get(i).child.size(); c++) {
//        拿到城市名称
                String cityName = shengList.get(i).child.get(c).title;
                cityList.add(cityName);

                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表
                if (shengList.get(i).child.get(c).child == null || shengList.get(i).child.get(c).child.size() == 0) {
                    city_AreaList.add("");
                } else {
                    List<String> l = new ArrayList<>();
                    for (ShengBean.Qu bean : shengList.get(i).child.get(c).child) {
                        l.add(bean.title);
                    }
                    city_AreaList.addAll(l);
                }
                province_AreaList.add(city_AreaList);
            }
            /**
             * 添加城市数据
             */
            options2Items.add(cityList);
            /**
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }

    }

    /**
     * 展示选择器
     */
    private void showPickerView() {// 弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String a3 = "";
                if (options3Items.get(options1).get(options2).get(options3) != "") {
                    a3 = "-" + options3Items.get(options1).get(options2).get(options3);
                }
                String tx = options1Items.get(options1).title + "-" +
                        options2Items.get(options1).get(options2) + a3;
                address.setText(tx);
            }
        })

                .setTitleText("")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    public void isEdite() {
        t_name = name.getText().toString().trim();
        t_company = company.getText().toString().trim();
        t_address = address.getText().toString().trim();
        String mposition = position.getText().toString().trim();
        if (user.getAvatar() != null) {
            String address = "";
            if (user.getAddress() != null && user.getAddress()!="") {
                address = user.getAddress();
            }
            String mposition1 = "";
            if (user.getPosition() != null && user.getPosition()!="") {
                mposition1 = user.getPosition();
            }
            Log.d("3255325236",t_name);
            Log.d("3255325236",t_company);
            Log.d("3255325236",t_address);
            Log.d("3255325236",t_profession.getText().toString().trim());
            Log.d("3255325236",avatar);
            Log.d("3255325236",mposition);
            if (t_name.equals(user.getName()) && t_company.equals(user.getCompany()) && t_address.equals(address) && (t_profession.getText().toString().trim()).equals(user.getProfession_name()) && avatar.equals(user.getAvatar()) && mposition.equals(mposition1)) {
                EventBus.getDefault().post(new Sticky("2"));
                finish();
            } else {
                BottomPowWindow("放弃", "继续编辑", "你编辑的个人信息还没有保存,", "确定要放弃编辑吗？");
            }
        } else {
            if (avatar != null || !t_name.equals(user.getName()) || t_company.length() > 0 || t_address.length() > 0 || Integer.parseInt(profession_id) > 0 || mposition.length() > 0) {
                BottomPowWindow("放弃", "继续编辑", "你编辑的个人信息还没有保存,", "确定要放弃编辑吗？");
            } else {
                EventBus.getDefault().post(new Sticky("2"));
                finish();
            }
        }
    }

    private void BottomPowWindow(String t1, String t2, String t3, String t4) {
        bottomWindow = new BottomWindow(EditeInformation.this, mitemsOnClick, t1, t2, t3, t4);
        //设置弹窗位置
        bottomWindow.showAtLocation(EditeInformation.this.findViewById(R.id.ll_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener mitemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            bottomWindow.dismiss();
            switch (v.getId()) {
                case R.id.item_popupwindows_left:
                    EventBus.getDefault().post(new Sticky("2"));
                    finish();
                    break;
                case R.id.item_popupwindows_right:
                    break;
                default:
                    break;
            }
        }

    };

    //定义处理接收的方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(Sticky userEvent) {
        if (userEvent.msg.equals("拥有资源")) {
            mpossession_resources = userEvent.obj;
            possession_resources.setText(formatText(userEvent.obj, 5));
        } else if (userEvent.msg.equals("需要资源")) {
            mneed_resources = userEvent.obj;
            need_resources.setText(formatText(userEvent.obj, 5));
        } else if (userEvent.msg.equals("自我介绍")) {
            mintroduce = userEvent.obj;
            introduce.setText(formatText(userEvent.obj, 5));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销注册
        EventBus.getDefault().unregister(this);
    }

    /**
     * 格式化字符串
     *
     * @param string   原始输入字符串
     * @param maxCount 最大字符限制，中文算作2个字符，其他都算1个字符
     * @return
     */
    private static String formatText(String string, int maxCount) {
        if (string.length() > maxCount) {
            string = subStrByLen(string, maxCount);
        }
        return string;
    }

    /**
     * 截取字符串，超出最大字数截断并显示"..."
     *
     * @param str    原始字符串
     * @param length 最大字数限制（以最大字数限制7个为例，当含中文时，length应设为2*7，不含中文时设为7）
     * @return 处理后的字符串
     */
    public static String subStrByLen(String str, int length) {
        if (str == null || str.length() == 0) {
            return "";
        }
        int chCnt = getStrLen(str);
        // 超出进行截断处理
        if (chCnt > length) {
            int cur = 0;
            int cnt = 0;
            StringBuilder sb = new StringBuilder();
            while (cnt <= length && cur < str.length()) {
                char nextChar = str.charAt(cur);
                cnt++;
                if (cnt <= length) {
                    sb.append(nextChar);
                } else {
                    return sb.toString() + "...";
                }
                cur++;
            }
            return sb.toString() + "...";
        }
        // 未超出直接返回
        return str;
    }

    /**
     * 获取字符串中的中文字数
     */
    private static int getChCount(String str) {
        int cnt = 0;
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        ;
        while (matcher.find()) {
            cnt++;
        }
        return cnt;
    }

    /**
     * 获取字符长度，中文算作2个字符，其他都算1个字符
     */
    public static int getStrLen(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        return str.length() + getChCount(str);
    }
}
