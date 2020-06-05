package www.pide.com.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import www.pide.com.utils.UIHelper;
import www.pide.com.utils.URLs;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
        private IWXAPI mWechatApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("LKing", "/////////");
        mWechatApi = WXAPIFactory.createWXAPI(this, URLs.WEIXIN_APP_ID, true);
        mWechatApi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
            Log.e("LKing", "授权登录成功");
            final String code = ((SendAuth.Resp) resp).code;
            Log.d("asfsdgdfh",code+"////////////");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //获取AccessToken
                    getAccessToken(code);
                }
            }).start();
        } else {
            Log.e("LKing", "授权登录失败\n\n自动返回");
        }
    }
    /**
     * 获取openid accessToken值用于后期操作
     * @param code 请求码
     */
    private void getAccessToken(final String code) {
        String path = "https://mApi.weixin.qq.com/sns/oauth2/access_token?appid="
                + URLs.WEIXIN_APP_ID
                + "&secret="
                + URLs.APP_SECRET
                + "&code="
                + code
                + "&grant_type=authorization_code";
        String result = "";
        try {
            BufferedReader reader = null;
            StringBuffer sbf = new StringBuffer() ;

            URL url  = new URL(path) ;
            HttpURLConnection connection = (HttpURLConnection) url.openConnection() ;
            //设置超时时间 10s
            connection.setConnectTimeout(10000);
            //设置请求方式
            connection.setRequestMethod( "GET" ) ;
            connection.connect();
            InputStream is = connection.getInputStream() ;
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8" )) ;
            String strRead = null ;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
            }
            reader.close();
            result = sbf.toString();

            JSONObject jsonObject = null;
            jsonObject = new JSONObject(result);
            final String openid = jsonObject.getString("openid").toString().trim();
            final String access_token = jsonObject.getString("access_token").toString().trim();

            OkHttpUtils.post().url(URLs.IMGEURL + "socials/weixin/authorizations")
                    .addParams("access_token", access_token)
                    .addParams("openid", openid)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(okhttp3.Call call, Exception e) {
                    UIHelper.ToastMessageCenter(WXEntryActivity.this, "验证码超时，请重新获取验证码", 200);
                }

                @Override
                public void onResponse(okhttp3.Call call, String response) {
                    Log.d("asfsdgdfh",response);
                  // UIHelper.OpenActivity(WXEntryActivity.this,WXLoginActivity.class);
                }
            });

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    getUserMesg(access_token, openid);
//                }
//            }).start();
            Log.e("LKing","基础信息 = "+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取微信的个人信息
     * @param access_token
     * @param openid
     */
    private void getUserMesg(final String access_token, final String openid) {
        String path =  "https://mApi.weixin.qq.com/sns/userinfo?access_token="
                + access_token
                + "&openid="
                + openid;//微信登录地址
        String result = "";
        try {
            BufferedReader reader = null;
            StringBuffer sbf = new StringBuffer() ;

            URL url  = new URL(path) ;
            HttpURLConnection connection = (HttpURLConnection) url.openConnection() ;
            //设置超时时间 10s
            connection.setConnectTimeout(10000);
            //设置请求方式
            connection.setRequestMethod( "GET" ) ;
            connection.connect();
            InputStream is = connection.getInputStream() ;
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8" )) ;
            String strRead = null ;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
            }
            reader.close();
            result = sbf.toString();
            Log.e("LKing","用户信息 = "+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
