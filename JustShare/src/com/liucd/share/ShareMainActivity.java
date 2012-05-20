package com.liucd.share;

import java.io.File;
import java.net.URLEncoder;

import weibo4andriod.Status;
import weibo4andriod.Weibo;
import weibo4andriod.WeiboException;
import weibo4andriod.androidexamples.OAuthConstant;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.liucd.share.common.DialogUtils;
import com.liucd.share.common.FileUtils;
import com.liucd.share.common.InfoHelper;
import com.liucd.share.common.MediaUtils;
import com.liucd.share.common.StringUtils;
import com.liucd.share.common.DialogUtils.DialogCallBack;
import com.liucd.share.db.AccessInfoHelper;
import com.mobclick.android.MobclickAgent;

/** 
 * 类说明：   乐分享 主界面
 * @author  @Cundong
 * @weibo   http://weibo.com/liucundong
 * @blog    http://www.liucundong.com
 * @date    Apr 29, 2011 2:50:48 PM
 * @version 1.0
 */
public class ShareMainActivity extends BaseActivity
{   
	private Button button = null;
	private ImageButton imgChooseBtn=null;
	private ImageView imgView = null;
	private TextView wordCounterTextView = null;
	private EditText contentEditText = null;
	private ProgressDialog dialog = null;
	
	private String accessToken, accessSecret;
	
	private static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0;
	private static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;
	private String thisLarge = null, theSmall = null;

	private static final int TOOLBAR0 = 0;
	private static final int TOOLBAR1 = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{   
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 						
	    setContentView(R.layout.main); 
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.header);
	   
		System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
    	System.setProperty("weibo4j.oauth.consumerSecret", Weibo.CONSUMER_SECRET);
    	
    	instance = this;
		mContext = getApplicationContext();
		button = (Button)findViewById( R.id.Button01 );
		imgChooseBtn = (ImageButton)findViewById( R.id.share_imagechoose );
		imgView = (ImageView)findViewById( R.id.share_image );
		wordCounterTextView = (TextView)findViewById( R.id.share_word_counter );
		contentEditText = (EditText)findViewById( R.id.share_content );
		
    	dialog = new ProgressDialog(instance);
		dialog.setMessage("分享中...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);

		Bundle bundle = getIntent().getExtras();
		if(bundle!=null)
		{
			thisLarge = bundle.containsKey("thisLarge")?bundle.getString("thisLarge"):"";
			accessToken = bundle.containsKey("accessToken")?bundle.getString("accessToken"):"";
			accessSecret = bundle.containsKey("accessSecret")?bundle.getString("accessSecret"):"";
		}
		
		button.setOnClickListener( new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				
				if( !InfoHelper.checkNetWork(mContext) )
				{
					Toast.makeText( mContext, "网络连接失败，请检查网络设置！", Toast.LENGTH_LONG ).show();
				}
				else
				{
					if( isChecked() )
					{
						dialog.show();
						
						Thread thread = new Thread( new UpdateStatusThread() );
						thread.start();	
					}
				}	
			}
		});
		
		imgChooseBtn.setOnClickListener( new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				CharSequence[] items = {"手机相册", "手机拍照", "清除照片"};
				imageChooseItem(items);
			}
		});
		
		//侦听EditText字数改变
		TextWatcher watcher = new TextWatcher() 
		{
		    public void onTextChanged(CharSequence s, int start, int before, int count) 
		    {
		    	textCountSet();
		    }
		    
		    public void beforeTextChanged(CharSequence s, int start, int count,
		            int after)
		    {
		    	textCountSet();
		    }
		    
			@Override
			public void afterTextChanged(Editable s) {
				textCountSet();
			}
		};
		
		contentEditText.addTextChangedListener(watcher);
		
		if(!StringUtils.isBlank(thisLarge))
		{
			String imgName = FileUtils.getFileName(thisLarge);
			
	    	Bitmap bitmap = loadImgThumbnail(imgName, MediaStore.Images.Thumbnails.MICRO_KIND );
			if(bitmap!=null)
			{
				imgView.setBackgroundDrawable(new BitmapDrawable(bitmap));
				imgView.setOnClickListener( new OnClickListener(){
					@Override
					public void onClick(View v) {
		    			Intent intent = new Intent();
						intent.setAction(android.content.Intent.ACTION_VIEW);
						intent.setDataAndType(Uri.fromFile(new File(thisLarge)),"image/*");
						startActivity(intent);
					}
		        });
			} 
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		MobclickAgent.onResume(this);
	}

	/**
	 * 设置稿件字数
	 */
	private void textCountSet()
	{   
    	String textContent = contentEditText.getText().toString();
    	int currentLength = textContent.length();
    	if( currentLength <= 140 )
    	{
    		wordCounterTextView.setTextColor( Color.BLACK );
    		wordCounterTextView.setText( String.valueOf(textContent.length()) );
    	}
    	else
    	{
    		wordCounterTextView.setTextColor( Color.RED );
    		wordCounterTextView.setText( String.valueOf(140-currentLength) );
    	}
	}
	
	/**
	 * 操作选择
	 * @param items
	 */
	public void imageChooseItem(CharSequence[] items )
	{
		AlertDialog imageDialog = new AlertDialog.Builder(instance).setTitle("增加图片").setItems(items,
			new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int item)
				{
					//手机选图
					if( item == 0 )
					{
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
						intent.setType("image/*"); 
						startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BYSDCARD); 
					}
					//拍照
					else if( item == 1 )
					{	  
						Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");	
						
						String camerName = InfoHelper.getFileName();
						String fileName = "Share" + camerName + ".tmp";	
						
						File camerFile = new File( InfoHelper.getCamerPath(), fileName );
								
						theSmall = InfoHelper.getCamerPath() + fileName;
						thisLarge = getLatestImage();
						
						Uri originalUri = Uri.fromFile( camerFile );
					    intent.putExtra(MediaStore.EXTRA_OUTPUT, originalUri); 	
						startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BYCAMERA);
					}   
					else if( item == 2 )
					{
						thisLarge = null;
						imgView.setBackgroundDrawable(null);
					}
				}}).create();
		
		 imageDialog.show();
	}
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{ 
        if ( requestCode == REQUEST_CODE_GETIMAGE_BYSDCARD ) 
        { 
        	if (resultCode != RESULT_OK) 
    		{   
    	        return;   
    	    }
        	
        	if(data == null)    return;
        	
        	Uri thisUri = data.getData();
        	String thePath = InfoHelper.getAbsolutePathFromNoStandardUri(thisUri);
        	
        	//如果是标准Uri
        	if( StringUtils.isBlank(thePath) )
        	{
        		thisLarge = getAbsoluteImagePath(thisUri);
        	}
        	else
        	{
        		thisLarge=thePath;
        	}
        	
        	String attFormat = FileUtils.getFileFormat(thisLarge);
        	if( !"photo".equals(MediaUtils.getContentType(attFormat)) )
        	{
        		Toast.makeText(mContext, "请选择图片文件！", Toast.LENGTH_SHORT).show();
        		return;
        	}
        	String imgName = FileUtils.getFileName(thisLarge);
    		
        	Bitmap bitmap = loadImgThumbnail(imgName, MediaStore.Images.Thumbnails.MICRO_KIND );
    		if(bitmap!=null)
    		{
    			imgView.setBackgroundDrawable(new BitmapDrawable(bitmap));
    		}
        }
        //拍摄图片
        else if(requestCode ==REQUEST_CODE_GETIMAGE_BYCAMERA )
        {	
        	if (resultCode != RESULT_OK) 
    		{   
    	        return;   
    	    }
        	
        	super.onActivityResult(requestCode, resultCode, data);
        	
        	Bitmap bitmap = InfoHelper.getScaleBitmap(mContext, theSmall);
        	
    		if(bitmap!=null)
    		{
    			imgView.setBackgroundDrawable(new BitmapDrawable(bitmap));
    		}
        }
        
        imgView.setOnClickListener( new OnClickListener(){
			@Override
			public void onClick(View v) {
    			Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(thisLarge)),"image/*");
				startActivity(intent);
			}
        });
    }
	
	/**
	 * 数据合法性判断
	 * @return
	 */
	private boolean isChecked()
	{
		boolean ret = true;
		if( StringUtils.isBlank(contentEditText.getText().toString()) )
		{
			Toast.makeText(mContext, "说点什么吧", Toast.LENGTH_SHORT ).show();
			ret = false;
		}
		else if( contentEditText.getText().toString().length() > 140 )
		{
			int currentLength = contentEditText.getText().toString().length();
			
			Toast.makeText(mContext, "已超出"+(currentLength-140)+"字", Toast.LENGTH_SHORT ).show();
			ret = false;
		}
		return ret;
	}
	
    Handler handle = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{	
			if(dialog!=null)
			{
				dialog.dismiss();
			}
			
			thisLarge = null;
			contentEditText.setText("");
			imgView.setBackgroundDrawable(null);
			
			MobclickAgent.onEvent(mContext, "doShare");
			
			if( msg.what>0 )
			{
				Toast.makeText(mContext, "微博分享成功", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(mContext, "微博分享失败", Toast.LENGTH_SHORT).show();
			}
		}   
	};
	
    Handler endSessionHandle = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{	
			finish();
		}   
	};
	
	//分享线程
    class UpdateStatusThread implements Runnable
    {	
		public void run() 
		{ 
			int what = -1;
			Weibo weibo = OAuthConstant.getInstance().getWeibo();	
            weibo.setToken( accessToken, accessSecret );
            try 
            {
            	String msg = contentEditText.getText().toString();
            	if(msg.getBytes().length != msg.length())
            	{
            		msg = URLEncoder.encode(msg, "UTF-8");
            	}
            	
            	Status status = null;
            	if( StringUtils.isBlank(thisLarge) )
            	{
            		status = weibo.updateStatus(msg);
            	}
            	else
            	{
            		File file = new File(thisLarge);
            		status = weibo.uploadStatus(msg, file);
            	}
				
				if(status!=null)
				{
					what=1;
				}
			} 
            catch (Exception e) 
            {
            	e.printStackTrace();
            	Log.e("WeiboPub", e.getMessage());
			}	
			handle.sendEmptyMessage(what);
		}
	}
    
    //用户注销线程
    class EndSessionThread implements Runnable
    {	
		public void run() 
		{
			AccessInfoHelper accessDBHelper = new AccessInfoHelper(mContext);
			accessDBHelper.open();
			accessDBHelper.delete();
			accessDBHelper.close();   
			Weibo weibo = OAuthConstant.getInstance().getWeibo();
			try 
			{
				weibo.endSession();
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			endSessionHandle.sendEmptyMessage(201);
		}
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		menu.add(0, TOOLBAR0, 1, "退出程序" ).setIcon( android.R.drawable.ic_menu_revert );
		menu.add(0, TOOLBAR1, 2, "注销登录" ).setIcon( android.R.drawable.ic_menu_delete );
		return super.onCreateOptionsMenu(menu);
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
    	if( item.getItemId() == 0 )
    	{
    		finish();
    	}
    	else
    	{	
    		DialogUtils.dialogBuilder(instance, "提示","确定要注销登录并退出？", new DialogCallBack(){
				@Override
				public void callBack() {
					dialog.show();
					dialog.setMessage("注销登录中...");
					Thread thread = new Thread( new EndSessionThread() );
					thread.start();	
				}
    		});
    	}
    	return super.onOptionsItemSelected(item);
	}
}