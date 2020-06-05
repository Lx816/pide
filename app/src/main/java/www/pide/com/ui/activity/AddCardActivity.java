package www.pide.com.ui.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import www.example.liangmutian.mypicker.DataPickerDialog;
import com.google.gson.reflect.TypeToken;

import okhttp3.FormBody;
import www.pide.com.base.BaseActivity;
import www.pide.com.bean.MicroCardsBean;
import www.pide.com.bean.ProfessionsBean;
import www.pide.com.bean.Sticky;
import www.pide.com.bean.UserImgBean;
import www.pide.com.utils.JsonUtil;
import www.pide.com.utils.SharedPreferencesUtil;
import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;

import www.pide.com.R;

import www.pide.com.ui.view.BottomWindow;
import www.pide.com.ui.view.CustomProgressDialog;
import www.pide.com.ui.view.XCRoundImageView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.greenrobot.eventbus.EventBus;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddCardActivity extends BaseActivity {
    private RelativeLayout back;
    private EditText add_hy;
    private ImageView add_down;
    private DataPickerDialog chooseDialog;
    private String itemValue = "";
    private Intent intent;
    private Context context;
    private EditText name,position,company,phone,wechat,address,possession_resources,need_resources;
    private String profession_id;
    private Button add;
    private List<ProfessionsBean> professionsList = new ArrayList<>();
    private List<String> list = new ArrayList<>();
    private String t_name,t_position,t_company,t_phone,t_wechat,t_address,t_possession_resources,t_need_resources,t_add_hy,professions_name;
    private MicroCardsBean microCardsBean=new MicroCardsBean();
    private int selectedPositin=0;
    private XCRoundImageView up_img;
    private BottomWindow bottomWindow;
    private TextView title;
    private byte[] mContent;
    Bitmap myBitmap;
    private CustomProgressDialog dialog;
    public static final int EXTERNAL_STORAGE_REQ_CODE = 10 ;
    private int avatar_image_id=-1;
    private Button b_phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTranslucentStatus_w();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_card_activity);
        context=this;
        intent = getIntent();
        initView();
        initOmclick();
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQ_CODE);
        }
    }

    private void initView() {
        back = (RelativeLayout) findViewById(R.id.back);
        add_hy = (EditText) findViewById(R.id.add_hy);
        add_down = (ImageView) findViewById(R.id.add_down);
        name = (EditText) findViewById(R.id.name);
        position = (EditText) findViewById(R.id.position);
        company = (EditText) findViewById(R.id.company);
        phone = (EditText) findViewById(R.id.phone);
        wechat = (EditText) findViewById(R.id.wechat);
        address = (EditText) findViewById(R.id.address);
        possession_resources = (EditText) findViewById(R.id.possession_resources);
        need_resources = (EditText) findViewById(R.id.need_resources);
        add= (Button) findViewById(R.id.add);
        up_img= (XCRoundImageView) findViewById(R.id.up_img);
        title= (TextView) findViewById(R.id.title);
        b_phone= (Button) findViewById(R.id.b_phone);
        intitProfessions();
    }

    private void initOmclick() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.back:
                        isEdite();
                        break;
                    case R.id.add_down:
                        add_down.setImageResource(R.mipmap.up);
                        showChooseDialog(list);
                        break;
                    case R.id.add:
                        initAdd();
                        break;
                    case R.id.up_img:
                        Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent1, 1);
                        break;
                    case R.id.b_phone:
                        if(phone.length()>0){
                           wechat.setText(phone.getText().toString().trim());
                        }else {
                            UIHelper.ToastMessageCenter(context, "请输入手机号码", 200);
                        }
                        break;
                }
            }
        };
        back.setOnClickListener(listener);
        add_down.setOnClickListener(listener);
        add.setOnClickListener(listener);
        up_img.setOnClickListener(listener);
        b_phone.setOnClickListener(listener);
    }

    private void initAdd() {
        t_name=name.getText().toString().trim();
        t_position=position.getText().toString().trim();
        t_company=company.getText().toString().trim();
        t_phone=phone.getText().toString().trim();
        t_wechat=wechat.getText().toString().trim();
        t_address=address.getText().toString().trim();
        t_possession_resources=possession_resources.getText().toString().trim();
        t_need_resources=need_resources.getText().toString().trim();
        t_add_hy=add_hy.getText().toString().trim();
        if(avatar_image_id>0){
            if(t_name.length()>0 && t_company.length()>0 && t_phone.length()==11 && t_address.length()>0 && t_add_hy.length()>0){
                if(intent.getExtras()==null){
                    OkHttpUtils.post().url(URLs.IMGEURL + URLs.MICROCARD)
                            .addParams("name",t_name)
                            .addParams("avatar_image_id",avatar_image_id+"")
                            .addParams("position",t_position)
                            .addParams("company",t_company)
                            .addParams("phone",t_phone)
                            .addParams("wechat",t_wechat)
                            .addParams("profession_id",profession_id)
                            .addParams("address",t_address)
                            .addParams("possession_resources",t_possession_resources)
                            .addParams("need_resources",t_need_resources)
                            .addParams("token", SharedPreferencesUtil.read(context,"LtAreaPeople",URLs.TOKEN))
                            .build().execute(new StringCallback() {
                        @Override
                        public void onError(okhttp3.Call call, Exception e) {
                            UIHelper.ToastMessageCenter(context, "添加失败！", 200);
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, String s) {
                            UIHelper.ToastMessageCenter(context, "添加成功", 200);
                            EventBus.getDefault().post(new Sticky("1"));
                            finish();
                        }
                    });
                }else {
                    FormBody build = new FormBody.Builder()
                            .add("id",microCardsBean.getId()+"")
                            .add("name",t_name)
                            .add("avatar_image_id",avatar_image_id+"")
                            .add("position",t_position)
                            .add("company",t_company)
                            .add("phone",t_phone)
                            .add("wechat",t_wechat)
                            .add("profession_id",profession_id)
                            .add("address",t_address)
                            .add("possession_resources",t_possession_resources)
                            .add("need_resources",t_need_resources)
                            .add("token",SharedPreferencesUtil.read(context,"LtAreaPeople",URLs.TOKEN))
                            .build();
                    OkHttpUtils.put().requestBody(build)
                            .url(URLs.IMGEURL + URLs.MICROCARD)
                            .build().execute(new StringCallback() {
                        @Override
                        public void onError(okhttp3.Call call, Exception e) {
                            UIHelper.ToastMessageCenter(context, "保存失败！", 200);
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, String response) {
                            UIHelper.ToastMessageCenter(context, "保存成功", 200);
                            EventBus.getDefault().post(new Sticky("1"));
                            finish();
                        }
                    });
                }
            }else {
                if (t_phone.length() == 0) {
                    UIHelper.ToastMessageCenter(context, "请输入你的手机号码", 200);
                } else if(isMobileNO(t_phone)) {
                    UIHelper.ToastMessageCenter(context, "你输入的是一个无效的手机号码", 200);
                }else {
                    UIHelper.ToastMessageCenter(context, "必填项不能为空", 200);
                }
            }
        }else {
            UIHelper.ToastMessageCenter(context, "请先上传头像，再编辑名片信息", 200);
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][3456789]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
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
                        itemValue = mitemValue;
                        add_hy.setText(itemValue);
                        add_down.setImageResource(R.mipmap.down);
                        for (ProfessionsBean str : professionsList) {
//                            if(str.getName()==itemValue){
//                                profession_id = str.getId();
//                            }
                        }
                    }

                    @Override
                    public void onCancel() {
                        add_down.setImageResource(R.mipmap.down);
                    }
                }).create();
        chooseDialog.show();
        if (itemValue != "") {
            chooseDialog.setSelection(itemValue);
        }
    }

    private void intitProfessions() {
        OkHttpUtils.get().url(URLs.IMGEURL + URLs.PROFESSIONS)
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e) {
                UIHelper.ToastMessageCenter(context, "网络未连接！", 200);
            }

            @Override
            public void onResponse(okhttp3.Call call, String response) {
                professionsList = JsonUtil.fromJson(response, new TypeToken<List<ProfessionsBean>>() {
                }.getType());
                for (ProfessionsBean str : professionsList) {
                    //list.add(str.getName());
                }
                if(intent.getExtras()==null){
                }else {
                    add.setText("保存");
                    title.setText("编辑");
                    microCardsBean=(MicroCardsBean)intent.getSerializableExtra("microCardsBean");
                    OkHttpUtils.get().url(URLs.IMGEURL + URLs.MICROCARDS+"/"+microCardsBean.getId())
                            .addParams("token",SharedPreferencesUtil.read(context,"LtAreaPeople",URLs.TOKEN))
                            .build().execute(new StringCallback() {
                        @Override
                        public void onError(okhttp3.Call call, Exception e) {
                            UIHelper.ToastMessageCenter(context, "获取数据失败", 200);
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, String response) {
                            microCardsBean=new MicroCardsBean();
                            microCardsBean=JsonUtil.fromJson(response,MicroCardsBean.class);
                            avatar_image_id=microCardsBean.getImage_id();
                            name.setText(microCardsBean.getName());
                            position.setText(microCardsBean.getPosition());
                            company.setText(microCardsBean.getCompany());
                            phone.setText(microCardsBean.getPhone());
                            wechat.setText(microCardsBean.getWechat());
                            address.setText(microCardsBean.getAddress());
                            possession_resources.setText(microCardsBean.getPossession_resources());
                            need_resources.setText(microCardsBean.getNeed_resources());
                            Glide.with(context)
                                    .load(microCardsBean.getAvatar())
                                    .apply(new RequestOptions().placeholder(R.drawable.error))
                                    .apply(new RequestOptions().error(R.drawable.error))
                                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                                    .into(up_img);
                            profession_id=microCardsBean.getProfession_id()+"";
                            add_hy.setText(microCardsBean.getProfession_name());
                            professions_name=microCardsBean.getProfession_name();
                            for (int i=0;i<professionsList.size();i++){
                               // String p_id=professionsList.get(i).getId();
//                                if(profession_id.equals(p_id)){
//                                    selectedPositin=i;
//                                }

                            }
                        }
                    });
                }
            }
        });
    }

    public void isEdite() {
        t_name=name.getText().toString().trim();
        t_position=position.getText().toString().trim();
        t_company=company.getText().toString().trim();
        t_phone=phone.getText().toString().trim();
        t_wechat=wechat.getText().toString().trim();
        t_address=address.getText().toString().trim();
        t_possession_resources=possession_resources.getText().toString().trim();
        t_need_resources=need_resources.getText().toString().trim();
        t_add_hy=add_hy.getText().toString().trim();
        if(intent.getExtras()==null){
            if(t_name.length()>0 || t_position.length()>0 || t_company.length()>0 || t_phone.length()>0 || t_wechat.length()>0 || t_address.length()>0 || t_possession_resources.length()>0 || t_need_resources.length()>0 || t_add_hy.length()>0 || avatar_image_id>0){
                BottomPowWindow("放弃","继续编辑","你编辑的名片还没发布，确定要放弃编辑吗？");
            }else {
                finish();
            }
        }else {
            String position="",wechat="",possession_resources="",need_resources="";
            if(microCardsBean.getPosition()!=null){
                position=microCardsBean.getPosition();
            }
            if(microCardsBean.getWechat()!=null){
                wechat=microCardsBean.getWechat();
            }
            if(microCardsBean.getPossession_resources()!=null){
                possession_resources=microCardsBean.getPossession_resources();
            }
            if(microCardsBean.getNeed_resources()!=null){
                need_resources=microCardsBean.getNeed_resources();
            }
            if(t_name.equals(microCardsBean.getName()) && t_position.equals(position) && t_company.equals(microCardsBean.getCompany()) && t_phone.equals(microCardsBean.getPhone()) && t_wechat.equals(wechat) && t_address.equals(microCardsBean.getAddress()) && t_possession_resources.equals(possession_resources) && t_need_resources.equals(need_resources) && t_add_hy.equals(professions_name) && avatar_image_id==microCardsBean.getImage_id()){
                finish();
            }else {
                BottomPowWindow("放弃","继续编辑","你编辑的名片还没保存，确定要放弃编辑吗？");
            }
        }
    }
    private void BottomPowWindow(String t1,String t2,String t3) {
        bottomWindow = new BottomWindow(AddCardActivity.this, itemsOnClick,t1,t2,t3,"");
        //设置弹窗位置
        bottomWindow.showAtLocation(AddCardActivity.this.findViewById(R.id.ll_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            bottomWindow.dismiss();
            switch (v.getId()) {
                case R.id.item_popupwindows_left:
                    finish();
                    break;
                case R.id.item_popupwindows_right:
                    break;
                default:
                    break;
            }
        }

    };
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    ContentResolver cr = this.getContentResolver();
                    Cursor cursor = cr.query(Uri.parse(data.getData().toString()), null, null, null, null);// 根据Uri从数据库中找
                    if (cursor != null) {
                        cursor.moveToFirst();// 把游标移动到首位，因为这里的Uri是包含ID的所以是唯一的不需要循环找指向第一个就是了
                        String filePath = cursor.getString(cursor.getColumnIndex("_data"));// 获取图片路
                        String orientation = cursor.getString(cursor
                                .getColumnIndex("orientation"));// 获取旋转的角度
                        cursor.close();
                        if (filePath != null) {
                            myBitmap = BitmapFactory.decodeFile(filePath);//根据Path读取资源图片
                            int angle = 0;
                            if (orientation != null && !"".equals(orientation)) {
                                angle = Integer.parseInt(orientation);
                            }
                            if (angle != 0) {
                                // 下面的方法主要作用是把图片转一个角度，也可以放大缩小等
                                Matrix m = new Matrix();
                                int width = myBitmap.getWidth();
                                int height = myBitmap.getHeight();
                                m.setRotate(angle); // 旋转angle度
                                myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, width, height,
                                        m, true);// 从新生成图片

                            }
                        }
                    }
                    File head = compressImage(myBitmap);
                    dialog = UIHelper.showdialog(this,"加载中");
                    if (head != null) {
                        dialog.show();
                        /**
                         * 上传服务器代码
                         */
                        final String fileName = head.getName();
                        OkHttpUtils
                                .post()
                                .url(URLs.IMGEURL + URLs.IMAGES)
                                .addParams("token", SharedPreferencesUtil.read(context,"LtAreaPeople",URLs.TOKEN))
                                .addParams("type", "avatar")
                                .addFile("image", fileName, head)
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(okhttp3.Call call, Exception e) {
                                        dialog.dismiss();
                                        UIHelper.ToastMessageCenter(AddCardActivity.this, "头像上传失败", 2000);
                                    }

                                    @Override
                                    public void onResponse(okhttp3.Call call, String response) {
                                        dialog.dismiss();
                                        UserImgBean userImgBean = JsonUtil.fromJson(response, UserImgBean.class);
                                        //avatar_image_id=userImgBean.getId();
                                        //Glide.with(context).load(userImgBean.getPath()).into(up_img);
                                        UIHelper.ToastMessageCenter(AddCardActivity.this, "上传成功", 2000);
                                    }
                                });
                    }

                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
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
