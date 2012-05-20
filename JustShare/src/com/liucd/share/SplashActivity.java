package com.liucd.share;

import java.io.File;
import java.util.SortedSet;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;

import com.liucd.share.bean.AccessInfo;
import com.liucd.share.common.InfoHelper;
import com.liucd.share.common.StringUtils;
import com.liucd.share.db.AccessInfoHelper;
import com.mobclick.android.MobclickAgent;

/** 
 * 类说明：   程序启动欢迎界面 
 * @author  @Cundong
 * @weibo   http://weibo.com/liucundong
 * @blog    http://www.liucundong.com
 * @date    Apr 29, 2011 2:50:48 PM
 * @version 1.0
 */
public class SplashActivity extends BaseActivity
{
	private Context mContext;
	private AccessInfo accessInfo = null;
	
	private CommonsHttpOAuthConsumer httpOauthConsumer;
	private OAuthProvider httpOauthprovider;
	private final static String callBackUrl="founderapp://SplashActivity";
	
	public  static SplashActivity webInstance = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{   
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		setContentView( R.layout.splash );
		
		webInstance = this;
        mContext = getApplicationContext();
        accessInfo = InfoHelper.getAccessInfo(mContext);
        
    	httpOauthConsumer = new CommonsHttpOAuthConsumer(getString(R.string.app_sina_consumer_key), 
    			getString(R.string.app_sina_consumer_secret));
    	
		httpOauthprovider = new DefaultOAuthProvider(
				"http://api.t.sina.com.cn/oauth/request_token",
				"http://api.t.sina.com.cn/oauth/access_token",
				"http://api.t.sina.com.cn/oauth/authorize");
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		MobclickAgent.onResume(this);
		
        //之前登陆过
        if(accessInfo!=null)
        {   
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("thisLarge", getImgPathByCaptureSendFilter());
			bundle.putString("accessToken", accessInfo.getAccessToken());
			bundle.putString("accessSecret", accessInfo.getAccessSecret());
			intent.putExtras(bundle);
			intent.setClass(SplashActivity.this, ShareMainActivity.class);
			startActivity(intent);
			finish();
        }
        else
        {
			startOAuthView();
        }
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	/**
	 * 捕捉android.intent.action.SEND，并得到捕捉到的图片路径
	 * @return
	 */
	private String getImgPathByCaptureSendFilter()
	{
		String thisLarge = "";
		Uri mUri = null;
		final Intent intent = getIntent();
		final String action = intent.getAction();
		if( !StringUtils.isBlank(action) && "android.intent.action.SEND".equals(action) ) 
		{
			boolean hasExtra = intent.hasExtra("android.intent.extra.STREAM");
			if(hasExtra)
			{
				mUri = (Uri)intent.getParcelableExtra("android.intent.extra.STREAM");
			}
			
			if( mUri!=null )
			{   
				String mUriString = mUri.toString();
				mUriString = Uri.decode(mUriString);
				
				String pre1 = "file://" + SDCARD + File.separator;
				String pre2 = "file://" + SDCARD_MNT + File.separator;
				
				if( mUriString.startsWith(pre1) )
				{    
					thisLarge = Environment.getExternalStorageDirectory().getPath() + File.separator + mUriString.substring( pre1.length() );
				}
				else if( mUriString.startsWith(pre2) )
				{
					thisLarge = Environment.getExternalStorageDirectory().getPath() + File.separator + mUriString.substring( pre2.length() );
				}
				else
				{
					thisLarge = getAbsoluteImagePath(mUri);
				}
			}	
		}
		return thisLarge;
	}
	
	private void startOAuthView()
	{
        try
        {
    		String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, callBackUrl);
    		
    		Intent intent = new Intent();
    		Bundle bundle = new Bundle();
    		bundle.putString("url", authUrl);
    		intent.putExtras(bundle);
    		intent.setClass(mContext , WebViewActivity.class);
    		startActivity(intent);
    	}
        catch(Exception e)
    	{
    		
    	}
	}
	
	@Override
    protected void onNewIntent(Intent intent) 
	{
    	super.onNewIntent(intent);
    	
    	Uri uri = intent.getData();
    	if(uri==null)
    	{
    		return;
    	}
    	
    	String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
    	
    	try 
    	{
            httpOauthprovider.setOAuth10a(true); 
            httpOauthprovider.retrieveAccessToken(httpOauthConsumer,verifier);
        } 
    	catch (OAuthMessageSignerException ex) {
            ex.printStackTrace();
        } 
    	catch (OAuthNotAuthorizedException ex) {
            ex.printStackTrace();
        } 
    	catch (OAuthExpectationFailedException ex) {
            ex.printStackTrace();
        } 
    	catch (OAuthCommunicationException ex) {
            ex.printStackTrace();
        }
        
        SortedSet<String> userInfoSet = httpOauthprovider.getResponseParameters().get("user_id");
        if(userInfoSet!=null&&!userInfoSet.isEmpty())
        {
            String userID = userInfoSet.first();
            String accessToken = httpOauthConsumer.getToken();
            String accessSecret = httpOauthConsumer.getTokenSecret();
            
            AccessInfo accessInfo = new AccessInfo();
            accessInfo.setUserID(userID);
            accessInfo.setAccessToken(accessToken);
            accessInfo.setAccessSecret(accessSecret);
            
            AccessInfoHelper accessDBHelper = new AccessInfoHelper(mContext);
            accessDBHelper.open();
            accessDBHelper.create(accessInfo);
            accessDBHelper.close();
            
            Intent intent2 = new Intent();
    		Bundle bundle = new Bundle();
    		bundle.putString("thisLarge", getImgPathByCaptureSendFilter());
    		bundle.putString("accessToken", accessInfo.getAccessToken());
    		bundle.putString("accessSecret", accessInfo.getAccessSecret());
    		intent2.putExtras(bundle);
    		intent2.setClass(SplashActivity.this, ShareMainActivity.class);
    		startActivity(intent2);
    		
    		WebViewActivity.webInstance.finish();
    		finish();
        }
    }
}