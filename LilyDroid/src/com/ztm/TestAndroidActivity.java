package com.ztm;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.NameValuePair;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ztm.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.ClipboardManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TestAndroidActivity extends Activity implements OnTouchListener,
OnGestureListener {

	private GestureDetector mGestureDetector;
	// ¿Ø¼þ

	private TextView textView;

	private ListView listView;

	
	private Button btnLink;

	// È«¾Ö±äÁ¿

	private List<String> LinkAdr;

	private String data;

	private List<TopicInfo> top10TopicList;

	private String topicUrl;

	private String newUrl;

	private String huifuUrl;

	
	// 1 ±íÊ¾´Ó10´óÌø×ª¹ýÈ¥µÄ£¬2±íÊ¾´ÓÌÖÂÛÇøÌø×ª¹ýÈ¥µÄ£¬3±íÊ¾´Ó¸÷ÇøÈÈµãÌø¹ýÈ¥
	int curStatus = 0;

	List<TopicInfo> areaTopic;

	int areaNowTopic = 0;
	
	boolean isWifi = false;

	private int nowPos;

	String urlString = "";

	String curAreaName = "";

	String curTopicId = "";
	
	String isPic;
	String isRem = "false";
	boolean isTouch ;
	boolean isIP;
	
	String loginId = "";
	String loginPwd = "";
	
	String androidmanufacturer;
	String androidmodel;
	
	
	String backWords = "";//
	boolean isBackWord = true;
	
	int runningTasks = 0;

	private ProgressDialog progressDialog = null;

	SharedPreferences sharedPreferences;

	List<String> areaNamList;
	
	String[] fastReList;

	Drawable drawableFav;

	Drawable drawableDis;

	boolean isLogin = false;



	Spanned topicData;

	int scrollY = 0;
	
	boolean topicWithImg = false;
	
	HashMap<String, String> bbsAll;
	
	HashMap<String, Integer> smilyAll;
	
	ArrayAdapter<String> bbsAlladapter;

	String loginURL = "http://bbs.nju.edu.cn/bbslogin?type=2";

	String loginoutURL = "http://bbs.nju.edu.cn/bbslogout";
	
	String synUrl = "http://bbs.nju.edu.cn/bbsleft";
	
	String bbsTop10String = "http://bbs.nju.edu.cn/bbstop10";

	
	Drawable xianDraw;
	int sWidth = 480;
	int sLength = 800;
	
	
	//TODO:¶¨ÒåÈ«¾Ö±äÁ¿
	/**
	 * Called when the activity is first created.
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGestureDetector = new GestureDetector(this);

		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.bkcolor);
		xianDraw = res.getDrawable(R.drawable.xian);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		sWidth = metric.widthPixels - 30; // ÆÁÄ»¿í¶È£¨ÏñËØ£©
		sLength = metric.heightPixels - 40; // ÆÁÄ»¿í¶È£¨ÏñËØ£©

		this.getWindow().setBackgroundDrawable(drawable);
		bbsAll = BBSAll.getBBSAll();
		
		smilyAll = BBSAll.getSmilyAll();
		String[] bbsAllArray = StringUtil.getArray(bbsAll);
		bbsAlladapter = new ArrayAdapter<String>(TestAndroidActivity.this,
				android.R.layout.simple_dropdown_item_1line, bbsAllArray);
		initPhoneState();
		initAllParams();
		
		StringUtil.initAll();
		
		
		chaToLogin();
	}

	private void InitMain() {
		chaToMain();
		getUrlHtml(bbsTop10String, Const.MSGWHAT);

	}

	private void initPhoneState()
	{
          
		try {
			
			

           Class<android.os.Build> build_class = android.os.Build.class;

           //È¡µÃÅÆ×Ó

           java.lang.reflect.Field manu_field = build_class.getField("MANUFACTURER");

           androidmanufacturer = (String) manu_field.get(new android.os.Build());

           //È¡µÃÐÍÌ–

           java.lang.reflect.Field field2 = build_class.getField("MODEL");

           androidmodel = (String) field2.get(new android.os.Build());

          
		} catch (Exception e) {
		
			e.printStackTrace();
		} 
	}

	private void chaToLogin() {
		setContentView(R.layout.login);
		Button btnlog = (Button) findViewById(R.id.btn_login);
		btnlog.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				EditText textName = (EditText) findViewById(R.id.textName);
				EditText textPwd = (EditText) findViewById(R.id.textPwd);
				CheckBox cb = (CheckBox) findViewById(R.id.cb_rem);
				if (cb.isChecked()) {
					isRem = "true";
				} else {
					isRem = "false";
				}
				loginId = textName.getText().toString();
				loginPwd = textPwd.getText().toString();
				String url = loginURL + "&id=" + loginId + "&pw=" + loginPwd;
				getUrlHtml(url, Const.MSGLOGIN);
			}

		});
		Button btnno = (Button) findViewById(R.id.btn_nolog);
		btnno.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				isLogin = false;
				InitMain();
			}

		});
		if (isRem.equals("true")) {
			EditText text = (EditText) findViewById(R.id.textName);
			text.setText(loginId);
			text = (EditText) findViewById(R.id.textPwd);
			text.setText(loginPwd);
			CheckBox cb = (CheckBox) findViewById(R.id.cb_rem);
			cb.setChecked(true);
		}
	}

	/**
	 * Ìø×ªµ½Ö÷½çÃæ
	 */
	private void chaToMain() {
		setContentView(R.layout.main);

		curStatus = 0;
		setTitle("È«Õ¾Ê®´ó");
		// ×¢Òâ½çÃæ¿Ø¼þµÄ³õÊ¼»¯µÄÎ»ÖÃ,²»Òª·ÅÔÚsetContentView()Ç°Ãæ
		listView = (ListView) findViewById(R.id.topicList);
		btnLink = (Button) findViewById(R.id.btn_link);

		btnLink.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// ¿ÉÒÔ´ò¿ªÒ»¸öÐÂÏß³ÌÀ´¶ÁÈ¡£¬¼ÓÈë¹ö¶¯ÌõµÈ
				getUrlHtml(bbsTop10String, Const.MSGWHAT);
			}

		});

		Button btnArea = (Button) findViewById(R.id.btn_all);

		btnArea.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// ¿ÉÒÔ´ò¿ªÒ»¸öÐÂÏß³ÌÀ´¶ÁÈ¡£¬¼ÓÈë¹ö¶¯ÌõµÈ

				LayoutInflater factory = LayoutInflater
						.from(TestAndroidActivity.this);
				final View textEntryView = factory.inflate(R.layout.dialog,
						null);
				AlertDialog dlg = new AlertDialog.Builder(
						TestAndroidActivity.this)

				.setTitle("ÌÖÂÛÇøÃû»òÖÐÎÄÃèÊö").setView(textEntryView)
						.setPositiveButton("³ö·¢",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										EditText secondPwd = (EditText) textEntryView
												.findViewById(R.id.username_edit);
										String inputPwd = secondPwd.getText()
												.toString();
										String areaText = bbsAll.get(inputPwd);
										areaText = areaText == null ? inputPwd
												: areaText;
										areaText = areaText.toLowerCase();
										areaText = areaText.replaceFirst(
												areaText.substring(0, 1),
												areaText.substring(0, 1)
														.toUpperCase());
										urlString = getResources().getString(
												R.string.areaStr)
												+ areaText;
										curAreaName = "" + areaText;

										getUrlHtml(urlString, Const.MSGAREA);

									}
								}).setNegativeButton("È¡Ïû",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
									}
								}).create();

				dlg.show();

				AutoCompleteTextView secondPwd = (AutoCompleteTextView) textEntryView
						.findViewById(R.id.username_edit);
				if (secondPwd.getAdapter() == null) {
					secondPwd.setAdapter(bbsAlladapter);
					secondPwd.setThreshold(1);
				}

			}

		});

		Button btnLike = (Button) findViewById(R.id.btn_like);

		btnLike.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						TestAndroidActivity.this);

				builder.setTitle("Ñ¡ÔñÄãÏëÈ¥µÄÌÖÂÛÇø£º");
				if (areaNamList == null || areaNamList.size() < 1) {

					return;
				}
				String[] a = new String[areaNamList.size()];
				int i = 0;
				for (String areName : areaNamList) {
					a[i] = areName;
					i++;
				}

				builder.setSingleChoiceItems(a, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {

								String areaText = areaNamList.get(i);
								urlString = getResources().getString(
										R.string.areaStr)
										+ areaText;
								curAreaName = "" + areaText;
								dialoginterface.dismiss();
								getUrlHtml(urlString, Const.MSGAREA);

							}
						});

				builder.setPositiveButton("È¡Ïû",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {

							}
						});
				builder.create().show();

			}

		});

		Button btnSet = (Button) findViewById(R.id.btn_set);

		btnSet.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				exitPro();
			}

		});

	}
	
	
	// ²Ëµ¥Ïî   
    final private int menuSettings=Menu.FIRST;  
    final private int menuLogout=Menu.FIRST+2;
    final private int menuSyn=Menu.FIRST+1;  
    private static final int REQ_SYSTEM_SETTINGS = 0;    

    //´´½¨²Ëµ¥   
    @Override    
    public boolean onPrepareOptionsMenu(Menu menu) 
    {        
    	return true;
    }

    @Override  
    public boolean onCreateOptionsMenu(Menu menu)  
    {  
        // ½¨Á¢²Ëµ¥   
        menu.add(Menu.NONE, menuSettings, 2, "ÉèÖÃ");  
        menu.add(Menu.NONE, menuSyn, 2, "Í¬²½ÊÕ²Ø");  
        menu.add(Menu.NONE, menuLogout, 2, "×¢Ïú");  
        return super.onCreateOptionsMenu(menu);  
    }  
    //²Ëµ¥Ñ¡ÔñÊÂ¼þ´¦Àí   
    @Override  
    public boolean onMenuItemSelected(int featureId, MenuItem item)  
    {  
        switch (item.getItemId())  
        {  
            case menuSettings:  
                //×ªµ½SettingsÉèÖÃ½çÃæ   
                startActivityForResult(new Intent(this, Settings.class), REQ_SYSTEM_SETTINGS);  
                break;  
            case menuLogout:  
                //×ªµ½µÇÂ¼½çÃæ   
            	getUrlHtml(loginoutURL,123);
                chaToLogin();
                break;
            case menuSyn:  
                //×ªµ½µÇÂ¼½çÃæ   
            	if(isLogin)
            		getUrlHtml(synUrl,Const.MSGSYN);
            	else
            	{
            		displayMsg("Äã»¹Ã»µÇÂ½ÄÅ~");
            	}
                break; 
                
            default:  
                break;  
        }  
        return super.onMenuItemSelected(featureId, item);  
    }  
    //SettingsÉèÖÃ½çÃæ·µ»ØµÄ½á¹û   
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {  
    	myParams();
    }  
    
    


	/**
	 * ²¶»ñ°´¼üÊÂ¼þ
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Èç¹ûÊÇ·µ»Ø¼ü,Ö±½Ó·µ»Øµ½×ÀÃæ
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (curStatus == 1) {
				chaToMain();
				if (top10TopicList != null) {
					setTopics();
				}
			} else if (curStatus == 2) {
				chaToArea(null);
			} else if (curStatus == 0) {
				exitPro();
			}
			return true;

		}
		else
		{
		return  super.onKeyDown(keyCode, event);
		}
	}



	private void initAllParams() {
		sharedPreferences = getSharedPreferences("LilyDroid",
				Context.MODE_PRIVATE);
		String name = sharedPreferences.getString("areaName", "");
		areaNamList = new ArrayList<String>();
		isRem = sharedPreferences.getString("isRem", "false");
		loginId = sharedPreferences.getString("loginId", "");
		loginPwd = sharedPreferences.getString("loginPwd", "");
		
		 myParams();
		WifiManager mWiFiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		if(mWiFiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED )
		{
			isWifi = true;
		}
		
		if (name == null || name.length() < 1)
			return;

		String[] split = name.split(",");
		for (String string : split) {
			areaNamList.add(string);
		}
	}
	
	private void myParams()
	{
		SharedPreferences sp = getSharedPreferences("com.ztm_preferences",
				Context.MODE_PRIVATE);
		isPic = sp.getString("picDS", "1");
		isTouch = sp.getBoolean("isTouch", true);
		isBackWord =  sp.getBoolean("isBackWord", true);
		backWords = sp.getString("backWords", "·¢ËÍ×Ô ÎÒµÄÐ¡°ÙºÏAndroid¿Í»§¶Ë by ${model}");
		isIP = sp.getBoolean("isIP", false);
		backWords = backWords.replaceAll("\\$\\{model\\}", androidmodel).replaceAll("\\$\\{manufa\\}", androidmanufacturer);
		String fastRe = sp.getString("fastRe", "É³·¢");
		if ( fastRe.length() < 1)
		{
			fastReList = null;
			return;
		}
		
		fastReList= fastRe.split("##");
		
		
		
	}
	
	private void displayMsg(String msg)
	{
		Toast.makeText(TestAndroidActivity.this, msg,
				Toast.LENGTH_SHORT).show();
	}
	

	/**
	 * TODO:ÏûÏ¢¿ØÖÆÆ÷
	 * ÏûÏ¢¿ØÖÆÆ÷£¬ÓÃÀ´¸üÐÂ½çÃæ£¬ÒòÎªÔÚÆÕÍ¨Ïß³ÌÊÇÎÞ·¨ÓÃÀ´¸üÐÂ½çÃæµÄ
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			runningTasks--;
			
			if (  msg.what!=Const.MSGPSTNEW && data.equals("error")) {
				displayMsg("ÄãµÄÍøÂçÃ²ËÆÓÐµãÐ¡ÎÊÌâ~");
				
			}
			else
			{
			switch (msg.what) {
			case Const.MSGWHAT:
				// ÉèÖÃÏÔÊ¾ÎÄ±¾
				// ´¦Àí½âÎödataÊý¾Ý
				top10TopicList = StringUtil.getTop10Topic(data);
				setTopics();
				break;
			case Const.MSGTOPIC:
				// ÉèÖÃÏÔÊ¾ÎÄ±¾

				chaToTopic(topicData);
				break;
			case Const.MSGTOPICNEXT:

				textView = (TextView) findViewById(R.id.label);
				ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
				sv.scrollTo(0, 0);
				textView.setText( getURLChanged(topicData));

				break;
			case Const.MSGTOPICREFREASH:
				textView = (TextView) findViewById(R.id.label);
				
				if(textView!=null)
					textView.setText(getURLChanged(topicData));
				break;
			case Const.MSGAREA:
				chaToArea(data);
				break;

			case Const.MSGAREAPAGES:
				areaPages(data);
				break;

			case Const.MSGLOGIN:
				checkLogin(data);
				break;
			case Const.MSGAUTOLOGIN:
				checkAutoLogin(data);
				break;
			case Const.MSGPST:
				checkForm(data);
				break;
			case Const.MSGPSTNEW:
				// ·¢ÎÄ¿ÉÄÜ»áÊ§°Ü£¬×¢Òâ±£ÁôÎÄÕÂ
				checkRst(data);
				break;

			case Const.MSGVIEWUSER:
				getUserData(data);
				break;
				
			case Const.MSGSYN:
				checkSyn(data);
				break;
				
				
				
			default:
				break;

			}
			}
			if(runningTasks<1)
			{
				runningTasks = 0;
				progressDialog.dismiss();
			}
			
		}

		

		

	};
	
	//Í¬²½ÊÕ²Ø¼Ð
	private void checkSyn(String data) {
		Document doc = Jsoup.parse(data);

		Elements as = doc.getElementsByTag("a");
		int ll = areaNamList.size();
		for (Element aTag : as) {
			String href = aTag.attr("href");
			if(href.contains("board?board="))
			{
				String tempAreaName = aTag.text().trim().toLowerCase();
			
				tempAreaName = tempAreaName.replaceFirst(
						tempAreaName.substring(0, 1),
						tempAreaName.substring(0, 1)
									.toUpperCase());
				
				if (areaNamList.contains(tempAreaName)) {
					continue;
				}
				areaNamList.add(tempAreaName);
				
			}
		}
		if(ll<areaNamList.size())
		{
			int ii= areaNamList.size()-ll;
			storeAreaName();
			displayMsg("Í¬²½WEBÊÕ²Ø¼Ð³É¹¦!¸üÐÂ"+ii+"¸öÒÑÊÕ²Ø°æÃæ");
		}
		else
		{
			displayMsg("ÄãµÄ±¾µØÊÕ²Ø¼ÐÒÑ¾­ÊÇ×îÐÂµÄÁË£¡");
		}
		
	}
	
	/**
	 * 
	 * »ñÈ¡ÓÃ»§ÐÅÏ¢
	 * @param data
	 */
	private void getUserData(String data) {
		//TODO:
		char s = 10;
		String backS = s + "";
		
		data = data.replaceAll(backS, "<br>");
		Document doc = Jsoup.parse(data);

		Elements scs = doc.getElementsByTag("textarea");
		
		if (scs.size() != 1) {
			Toast.makeText(TestAndroidActivity.this, "»ñÈ¡ÓÃ»§ÐÅÏ¢Ê§°Ü",
					Toast.LENGTH_SHORT).show();
		}
		else
		{
			Element textArea = scs.get(0);
			String infoView = textArea.text();
			
			
			String withSmile = StringUtil.addSmileySpans(infoView);
			LayoutInflater factory = LayoutInflater
			.from(TestAndroidActivity.this);
			final View info = factory.inflate(R.layout.infodlg, null);
			AlertDialog dlg = new AlertDialog.Builder(TestAndroidActivity.this)
			.setTitle("ÓÃ»§ÐÅÏ¢²éÑ¯").setView(info).setNegativeButton("È·¶¨",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
						}
					}).create();
			//·¢²ÊÕÕ¹¦ÄÜ
			textView = (TextView) info.findViewById(R.id.tvInfo);
			ScrollView sv = (ScrollView) info.findViewById(R.id.svInfo);
			sv.scrollTo(0, 0);
			textView.setText(Html.fromHtml(withSmile));
			
			dlg.show();

		}

	}

	/**
	 * ¼ì²é·¢ÎÄ½á¹û
	 */
	private void checkRst(String data) {

		if (data.contains("http-equiv='Refresh'")) {
			
			if(reid.equals("0"))
			{
				//·¢ÐÂÎÄÕÂÍê³É
				getUrlHtml(urlString, Const.MSGAREAPAGES);
			}
			else
			{
				//»Ø¸´Íê³É
				getUrlHtml(topicUrl + "&start="
						+ nowPos, Const.MSGTOPICREFREASH);
			}
			
			
			Toast.makeText(TestAndroidActivity.this, "·¢ÎÄ³É¹¦£¡",
					Toast.LENGTH_SHORT).show();
		}

		else // if(data.contains("javascript:history.go(-1)"))
		{
			Toast.makeText(TestAndroidActivity.this, "·¢ËÍÊ§°ÜÁËÄÅ~·¢ÎÄÄÚÈÝ±£´æÔÚ¼ôÌù°åÉÏ",
					Toast.LENGTH_SHORT).show();
			ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

			clipboard.setText(cont);
		}

	}

	/**
	 * ¼ì²âÊÇ·ñµÇÂ¼³É¹¦
	 * 
	 * @param data
	 */
	private void checkLogin(String data) {

		Document doc = Jsoup.parse(data);

		Elements scs = doc.getElementsByTag("script");

		if (scs.size() == 3) {
			String element = scs.get(1).toString();

			setCookies(element.substring(27, element.length() - 12));

			Toast.makeText(TestAndroidActivity.this, "µÇÂ¼³É¹¦£¡",
					Toast.LENGTH_SHORT).show();
			isLogin = true;

			Editor editor = sharedPreferences.edit();// »ñÈ¡±à¼­Æ÷
			editor.putString("isRem", isRem);
			if (isRem.equals("true")) {
				editor.putString("loginId", loginId);
				editor.putString("loginPwd", loginPwd);
			} else {
				editor.putString("loginId", "");
				editor.putString("loginPwd", "");
			}
			editor.commit();
			// progressDialog.dismiss();
			InitMain();

		} else if (scs.size() == 1) {
			if (data.contains("ÃÜÂë´íÎó") || data.contains("´íÎóµÄÊ¹ÓÃÕßÕÊºÅ")) {
				Toast.makeText(TestAndroidActivity.this, "ÓÃ»§Ãû»òÃÜÂë´í£¡",
						Toast.LENGTH_SHORT).show();
			} else if (data.contains("´ËÕÊºÅ±¾ÈÕlogin´ÎÊý¹ý¶à")) {
				Toast.makeText(TestAndroidActivity.this, "´ËÕÊºÅ±¾ÈÕlogin´ÎÊý¹ý¶à£¡",
						Toast.LENGTH_SHORT).show();
			}

			else {
				Toast.makeText(TestAndroidActivity.this, "µÇÂ½Ê§°Ü£¡",
						Toast.LENGTH_SHORT).show();
			}

			isLogin = false;

		}
		return;
	}
	
	
	/**
	 * ¼ì²âÊÇ·ñ£¡×Ô¶¯£¡µÇÂ¼³É¹¦
	 * 
	 * @param data
	 */
	private void checkAutoLogin(String data) {
		Document doc = Jsoup.parse(data);
		Elements scs = doc.getElementsByTag("script");
		if (scs.size() == 3) {
			String element = scs.get(1).toString();

			setCookies(element.substring(27, element.length() - 12));

			Toast.makeText(TestAndroidActivity.this, "µÇÂ¼³É¹¦£¡",
					Toast.LENGTH_SHORT).show();
			isLogin = true;
		} else if (scs.size() == 1) {
			if (data.contains("ÃÜÂë´íÎó") || data.contains("´íÎóµÄÊ¹ÓÃÕßÕÊºÅ")) {
				Toast.makeText(TestAndroidActivity.this, "ÓÃ»§Ãû»òÃÜÂë´í£¡",
						Toast.LENGTH_SHORT).show();
			} else if (data.contains("´ËÕÊºÅ±¾ÈÕlogin´ÎÊý¹ý¶à")) {
				Toast.makeText(TestAndroidActivity.this, "´ËÕÊºÅ±¾ÈÕlogin´ÎÊý¹ý¶à£¡",
						Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(TestAndroidActivity.this, "µÇÂ½Ê§°Ü£¡",
						Toast.LENGTH_SHORT).show();
			}
			isLogin = false;

		}
		return;
	}
	
	
	

	String pid;
	String reid;
	String cont;

	/**
	 * »ñÈ¡·¢ÎÄµÄ´°¿Ú
	 * @param formData
	 */
	protected void checkForm(String formData) {
		Document doc = Jsoup.parse(formData);
		Elements ins = doc.getElementsByTag("input");
		// progressDialog.dismiss();
		if (ins.size() != 12) {
			// µÇÂ¼Ê§°Ü£¬ÒªÇóÖØÐÂµÇÂ¼
			if (formData.contains("´Ò´Ò¹ý¿Í")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						TestAndroidActivity.this);
				builder.setMessage("Äã»¹Ã»µÇÂ½ÄØ~ÖØÐÂµÇÂ¼?").setCancelable(false)
						.setPositiveButton("µÇÂ¼",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										if(isRem.equals("true"))
										{
											//×Ô¶¯µÇÂ¼µÄ»°£¬×Ô¶¯µÇÂ¼
											String url = loginURL + "&id=" + loginId + "&pw=" + loginPwd;
											getUrlHtml(url, Const.MSGAUTOLOGIN);
										}
										else
										{
											chaToLogin();
										}
										
										
									}
								}).setNegativeButton("ËãÁË",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			} else if (formData.contains("ÄúÎÞÈ¨ÔÚ´ËÌÖÂÛÇø")) {
				Toast.makeText(TestAndroidActivity.this, "ÄúÎÞÈ¨ÔÚ´ËÌÖÂÛÇø·¢ÎÄ",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(TestAndroidActivity.this, "ÓÉÓÚÎ´Öª´íÎó·¢ÎÄÊ§°Ü",
						Toast.LENGTH_SHORT).show();
			}

		} else {

			String title = ins.get(0).attr("value");
			pid = ins.get(1).attr("value");
			reid = ins.get(2).attr("value");
			 String recont = "" ;
			try
			{
				recont = formData.substring(formData.indexOf("<textarea name=text rows=20 cols=80 wrap=physicle>")+50,formData.indexOf("</textarea>"));
			}
			catch(Exception e)
			{
				
			}
			final String extraRecont = recont;
			LayoutInflater factory = LayoutInflater
					.from(TestAndroidActivity.this);
			final View acdlgView = factory.inflate(R.layout.acdlg, null);
			 Builder altDlg = new AlertDialog.Builder(TestAndroidActivity.this)
					.setTitle("·¢ÎÄ").setView(acdlgView).setPositiveButton("·¢±í",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									EditText titleEdit = (EditText) acdlgView
											.findViewById(R.id.edt_title);
									String title = titleEdit.getText()
											.toString();
									titleEdit = (EditText) acdlgView
											.findViewById(R.id.edt_cont);
									cont = StringUtil.getStrBetter(titleEdit.getText()
											.toString());
											//ÒýÓÃÔ­ÎÄ
											CheckBox cb = (CheckBox) acdlgView.findViewById(R.id.cb_recont);
											if(cb.isChecked()&&extraRecont!=null&&extraRecont.length()>1)
											{
												cont+=extraRecont.substring(2);
											}
											sendTopic(title,cont);
								}

							});
							
			 if(extraRecont.length()<4)
				{
				 altDlg.setNegativeButton("È¡Ïû",
							new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialoginterface, int i) {

						}
					});
				}
			 else
			 {
				 altDlg.setNegativeButton("¿ìËÙ»Ø¸´",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
										if(fastReList.length<1)
											return;
										if(fastReList.length==1)
										{
											//TODO
											EditText titleEdit = (EditText) acdlgView
											.findViewById(R.id.edt_title);
											String title = titleEdit.getText().toString();
											String reText = fastReList[0];
											sendTopic(title,reText);
											return;
										}
										AlertDialog.Builder builder = new AlertDialog.Builder(
												TestAndroidActivity.this);

										builder.setTitle("Ñ¡ÔñÒªÊ¹ÓÃµÄ¿ì½Ý»Ø¸´£º");
										
										builder.setSingleChoiceItems(fastReList, 0,
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialoginterface, int i) {

														EditText titleEdit = (EditText) acdlgView
														.findViewById(R.id.edt_title);
														String title = titleEdit.getText().toString();
														String reText = fastReList[i]+"\n";
														dialoginterface.dismiss();
														sendTopic(title,reText);
													}
												});

										builder.setPositiveButton("È¡Ïû",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialoginterface, int i) {

													}
												});
										builder.create().show();

									}
							});
			 }
			 
							
				AlertDialog dlg =			altDlg.create();
			EditText titleEdit = (EditText) acdlgView
					.findViewById(R.id.edt_title);
			CheckBox cb = (CheckBox) acdlgView.findViewById(R.id.cb_recont);
			if(extraRecont.length()<4)
			{
				
				cb.setChecked(false);
				cb.setVisibility(CheckBox.INVISIBLE);
			}
			else
			{
				cb.setChecked(true);
			}
			titleEdit.setText(title);
			dlg.show();

		}
	}

	private void sendTopic(String title,String cont)
	{
		//ÊÖ»úÇ©Ãû
		if(isBackWord&&backWords!=null&&backWords.length()>0)
		{
			cont+="\n-\n[1;32m"+backWords+"[m\n";
		}
		
		try {
			title = URLEncoder.encode(title,
					"GB2312"); // new
								// String((title.replace(" ",
								// "%20")).getBytes("UTF-8"),"gb2312");
			String url = "http://bbs.nju.edu.cn/bbssnd?board="
					+ curAreaName
					+ "&title="
					+ title
					+ "&pid="
					+ pid
					+ "&reid="
					+ reid
					+ "&signature=1";
			// +"&text="+;

			NameValuePair[] newVp = { new NameValuePair(
					"text", cont) };

			nvpCont = newVp;

			getUrlHtml(url, Const.MSGPSTNEW);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private void setCookies(String cookStr) {

		char[] charArray = cookStr.toCharArray();
		int i = 0;
		int sp1 = 0;
		int sp2 = 0;
		for (char c : charArray) {
			if (sp1 == 0 && !Character.isDigit(c)) {
				sp1 = i;

			} else if (c == '+') {
				sp2 = i;
				break;
			}
			i++;
		}
		String NUM = (Integer.parseInt(cookStr.substring(0, sp1)) + 2) + "";
		String id = cookStr.substring(sp1 + 1, sp2);
		String KEY = (Integer.parseInt(cookStr.substring(sp2 + 1)) - 2) + "";
		//saveMyCookie( NUM, id , KEY);
		NetTraffic.setMyCookie( NUM, id , KEY);
	}


	
	
	

	/**
	 * ½«ÓÉÊý¾Ý×ª»¯ÎªListView¿É¶ÁµÄÐÎÊ½ ¹©10´óÊ¹ÓÃ
	 */
	private void setTopics() {

		LinkAdr = new ArrayList<String>();

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (TopicInfo topicInfo : top10TopicList) {

			Map<String, Object> map = new HashMap<String, Object>();

			map.put("topictitle", " " + topicInfo.getTitle());

			map.put("topicau", " ×÷Õß:" + topicInfo.getAuthor() + "  ÐÅÇø:"
					+ topicInfo.getArea() + "  »Ø¸´:" + topicInfo.getNums());

			list.add(map);

			LinkAdr.add("http://bbs.nju.edu.cn/" + topicInfo.getLink());

		}
		if (list.size() > 0) {

			SimpleAdapter adapter = new SimpleAdapter(this, list,
					R.layout.vlist, new String[] { "topictitle", "topicau" },
					new int[] { R.id.topictitle, R.id.topicau });
			listView.setAdapter(adapter);
			// Ìí¼Óµã»÷
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					topicUrl = LinkAdr.get(arg2);

					if (topicUrl == null)
						return;
					huifuUrl = topicUrl.replace("bbstcon?", "bbspst?");
					curStatus = 1;
					nowPos = 0;
					getUrlHtml(topicUrl, Const.MSGTOPIC);

				}
			});
		}
	}

	/**
	 * ½«ÓÉHTMLÒ³Ãæ×ª³öµÄÊý¾Ý×ª»¯ÎªListView¿É¶ÁµÄÐÎÊ½ ¹©ÌÖÂÛÇøÊ¹ÓÃ
	 */
	private void setAreaTopics() {

		LinkAdr = new ArrayList<String>();

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (TopicInfo topicInfo : areaTopic) {

			Map<String, Object> map = new HashMap<String, Object>();

			map.put("topictitle", " " + topicInfo.getTitle());
			if (topicInfo.getNums() == null || topicInfo.getNums().equals("")) {
				map
						.put("topicau", " ÖÃ¶¥   ×÷Õß:" + topicInfo.getAuthor()
								+ " - " + topicInfo.getPubDate() + "  ÈËÆø:"
								+ topicInfo.getHot());
			} else {
				map
						.put("topicau", " " + topicInfo.getNums() + "   ×÷Õß:"
								+ topicInfo.getAuthor() + " ÓÚ"
								+ topicInfo.getPubDate() + "  ÈËÆø:"
								+ topicInfo.getHot());
			}

			list.add(map);

			LinkAdr.add("http://bbs.nju.edu.cn/" + topicInfo.getLink());

		}
		if (list.size() > 0) {

			SimpleAdapter adapter = new SimpleAdapter(this, list,
					R.layout.vlist, new String[] { "topictitle", "topicau" },
					new int[] { R.id.topictitle, R.id.topicau });
			listView.setAdapter(adapter);
			// Ìí¼Óµã»÷
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					topicUrl = LinkAdr.get(arg2);

					if (topicUrl == null)
						return;

					huifuUrl = topicUrl.replace("bbstcon?", "bbspst?");
					curStatus = 2;
					nowPos = 0;
					scrollY = listView.getFirstVisiblePosition();

					getUrlHtml(topicUrl, Const.MSGTOPIC);

				}
			});
		}
	}

	boolean isNext = true;
	boolean isPrev = true;

	

	
	/**
	 * Ìø×ªµ½ÌÖÂÛÇø½çÃæ
	 * 
	 * @param AreaData
	 */
	private void chaToArea(String AreaData) {
		
		if(AreaData!=null&&AreaData.contains("´íÎó! ´íÎóµÄÌÖÂÛÇø"))
				{
			Toast.makeText(TestAndroidActivity.this, "¸ÃÌÖÂÛÇø²»´æÔÚ£¡",
					Toast.LENGTH_SHORT).show();
			return;
				}
		
		setContentView(R.layout.area);
		curStatus = 1;
		Button btnBack = (Button) findViewById(R.id.btn_back);
		btnBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				/**
				 * ¸Ä³É·¢±íÐÂ»°Ìâ
				 * 
				 * chaToMain(); if(top10TopicList!=null) { setTopics(); }
				 * 
				 */
				getUrlHtml(newUrl, Const.MSGPST);

			}
		});
		Button btnPre = (Button) findViewById(R.id.btn_pre);
		btnPre.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				goToPage(-21);
			}
		});

		Button btnNext = (Button) findViewById(R.id.btn_next);
		btnNext.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				goToPage(21);
			}
		});

		setTitle("µ±Ç°ÌÖÂÛÇø£º" + curAreaName);

		Button btnLike = (Button) findViewById(R.id.btn_like);

		if (areaNamList.contains(curAreaName)) {
			// btnLike.setBackgroundDrawable(drawableDis);
			btnLike.setText("ÍË¶©");
		} else {
			// btnLike.setBackgroundDrawable(drawableFav);
			btnLike.setText("ÊÕ²Ø");
		}

		btnLike.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Button btnLike = (Button) findViewById(R.id.btn_like);
				if (areaNamList.contains(curAreaName)) {
					areaNamList.remove(curAreaName);

					// btnLike.setBackgroundDrawable(drawableFav);
					btnLike.setText("ÊÕ²Ø");

				} else {
					areaNamList.add(curAreaName);
					// btnLike.setBackgroundDrawable(drawableDis);
					btnLike.setText("ÍË¶©");
				}
				storeAreaName();
			}

		});

		if (AreaData != null) {
			newUrl = "http://bbs.nju.edu.cn/bbspst?board=" + curAreaName;
			areaTopic = getAreaTopic(AreaData);
		}
		listView = (ListView) findViewById(R.id.topicList);

		setAreaTopics();
		if (AreaData == null) {
			listView.setSelection(scrollY);
		} else {

			listView.setSelection(areaTopic.size() - 1);
		}

	}

	private void storeAreaName() {
		String areaName = "";
		for (String name : areaNamList) {
			areaName += name + ",";
		}
		if (areaName.length() > 1) {
			areaName = areaName.substring(0, areaName.length() - 1);
		}
		Editor editor = sharedPreferences.edit();// »ñÈ¡±à¼­Æ÷
		editor.putString("areaName", areaName);
		editor.commit();
	}

	/**
	 * ÌÖÂÛÇø½çÃæ·­Ò³
	 * 
	 * @param AreaData
	 */
	private void goToPage(int pageNo) {
		int startPage = areaNowTopic + pageNo;
		if (startPage < 0) {
			startPage = 0;
		}

		getUrlHtml(urlString + "&start=" + startPage, Const.MSGAREAPAGES);

	}

	private void areaPages(String AreaData) {
		areaTopic = getAreaTopic(AreaData);
		listView = (ListView) findViewById(R.id.topicList);
		setAreaTopics();
		listView.setSelection(areaTopic.size() - 1);
	}
	
	 /**
	 * ½âÎö»ñÈ¡µÄÒ³Ãæ ´¦ÀíÌÖÂÛÇøµÄ»°ÌâÁÐ±í
	 * 
	 * @param data
	 * @return
	 */
	private List<TopicInfo> getAreaTopic(String data) {
		List<TopicInfo> tiList = new ArrayList<TopicInfo>();
		Document doc = Jsoup.parse(data);
		Elements tds = doc.getElementsByTag("td");
		int curPos = 0;
		int getTopicNo = 0;
		while (curPos < tds.size()) {
			if (curPos != 0) {
				TopicInfo ti = new TopicInfo();
				ti.setLink((tds.get(curPos + 4).getElementsByTag("a")).get(0)
						.attr("href"));// ÉèÖÃtitle
				ti.setTitle(tds.get(curPos + 4).text());// ÉèÖÃtitle
				
				String date = DateUtil.formatDateToStrNoWeek(DateUtil.getDatefromStrNoWeek(tds.get(curPos + 3).text()));
				if(date == null||date.equals("null"))
					
					ti.setPubDate(tds.get(curPos + 3).text());
				else
					ti.setPubDate(date);
				ti.setAuthor(tds.get(curPos + 2).text());
				ti.setHot(tds.get(curPos + 5).text());
				String notext = tds.get(curPos).text();
				ti.setNums(notext);
				tiList.add(ti);
				if (getTopicNo == 0) {

					if (notext != "" && Character.isDigit(notext.charAt(0))) {
						areaNowTopic = Integer.parseInt(notext);
						getTopicNo = 1;
					}
				}

			}

			curPos += 6;
		}

		return tiList;
	}
	class MyURLSpan extends ClickableSpan {
		
		  private String mUrl;
		   private boolean underline = false;
		     MyURLSpan(String url) {
		      mUrl = url;
		     }


		     
		     @Override
		  public void updateDrawState(TextPaint ds) {
		   super.updateDrawState(ds);
		    ds.setUnderlineText(underline);//Ò»°ãÁ´ÏÂÃæ¶¼ÓÐÒ»ÌõÏß£¬Í¦¶ñÐÄ£¬ ¸Ã·½·¨¿ÉÒÔÈ¥µôÄÇÌõÏß
		   // ds.setColor(Color.rgb(0, 0, 237));// ¸Ä±äÁ´½ÓµÄÑÕÉ«ÉèÖÃ
		  }

		  @Override
		     public void onClick(View widget) {
		            //´Ë´¦Ð´ÄãµÄ´¦ÀíÂß¼­
			  //System.out.println("123123");
			  // processHyperLinkClick(text); //µã»÷³¬Á´½ÓÊ±µ÷ÓÃ
			  if(mUrl.contains("bbsqry?userid"))
			  {
				  //²é¿´ÓÃ»§
				  getUrlHtml(mUrl, Const.MSGVIEWUSER);
			  }
			  else if(mUrl.toLowerCase().startsWith("http:")
						&& (mUrl.toLowerCase().endsWith(".jpg") 
								|| mUrl.toLowerCase().endsWith(".png")
								||mUrl.toLowerCase().endsWith(".jpeg")
								||mUrl.toLowerCase().endsWith(".gif")
								))
			  {
				  Intent intent = new Intent(TestAndroidActivity.this, ImageActivity.class);
				  intent.putExtra("mUrl", mUrl); 
				 
				  startActivity(intent);
			  }
			  else if(mUrl.toLowerCase().startsWith("http:"))
			  {
				  //super.
			  }

		   }
		 }

	 public  SpannableStringBuilder getURLChanged(Spanned topicData)
	 {
		 URLSpan[] spans = topicData.getSpans(0, topicData.length(), URLSpan.class);  
			SpannableStringBuilder style = new SpannableStringBuilder(topicData);
			for (URLSpan url : spans) {
				 style.removeSpan(url);
			     MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
			     style.setSpan(myURLSpan, topicData.getSpanStart(url), topicData
			       .getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			    }
			return style;
	 }

	/**
	 * Ìø×ªµ½Ä³¸ö»°Ìâ½çÃæ
	 * 
	 * @param AreaData
	 */
	private void chaToTopic(Spanned topicData) {

		setContentView(R.layout.topic);
		
		//topicData.getSpans(arg0, arg1, arg2);
		SpannableStringBuilder urlChanged = getURLChanged(topicData);

		textView = (TextView) findViewById(R.id.label);
		textView.setText(urlChanged);
		textView.setMovementMethod(LinkMovementMethod.getInstance());

//		
//		//textView.
		if(isTouch)
		{
			textView.setOnTouchListener(this);
			textView.setFocusable(true);
			textView.setLongClickable(true);
		}
		
		// WebView mWebView = (WebView) findViewById(R.id.label);
		// mWebView.loadData(getTopicInfo(data), "text/html", "iso-8859-1");

		Button btnBack = (Button) findViewById(R.id.btn_back);
		btnBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (curStatus == 1) {
					chaToMain();
					if (top10TopicList != null) {
						setTopics();
					}
				} else {
					chaToArea(null);
				}
			}
		});

		Button btnHuifu = (Button) findViewById(R.id.btn_huifu);
		btnHuifu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				getUrlHtml(huifuUrl, Const.MSGPST);
			}
		});

		Button btnPre = (Button) findViewById(R.id.btn_pre);
		btnPre.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (nowPos < 1) {
					Toast.makeText(TestAndroidActivity.this, "µ±Ç°ÎªµÚÒ»Ò³£¡",
							Toast.LENGTH_SHORT).show();
					return;

				}
				nowPos = nowPos - 30;
				getUrlHtml(topicUrl + "&start=" + nowPos, Const.MSGTOPICNEXT);

			}
		});

		Button btnNext = (Button) findViewById(R.id.btn_next);
		btnNext.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				if (isNext) {
					nowPos = nowPos + 30;
					getUrlHtml(topicUrl + "&start=" + nowPos, Const.MSGTOPICNEXT);
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							TestAndroidActivity.this);
					builder.setMessage("ÒÑÊÇ×îºóÒ»Ò³£¬ÊÇ·ñË¢ÐÂµ±Ç°Ò³?").setCancelable(false)
							.setPositiveButton("Ë¢ÐÂ",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											getUrlHtml(topicUrl + "&start="
													+ nowPos, Const.MSGTOPICREFREASH);
										}
									}).setNegativeButton("ËãÁË",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				}

			}
		});

	}


	private String dataUrl = "";
	private int datamsg = -1;
	NameValuePair[] nvpCont = null;
	Thread imageTrd;

	private void getUrlHtml(String url, int msg) {
		if (msg ==123 ||progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = ProgressDialog.show(TestAndroidActivity.this,
					"ÇëÉÔµÈ...", "×¥È¡ÍøÒ³ÐÅÏ¢ÖÐ...", true);
		}
		runningTasks++;

		dataUrl = url;
		datamsg = msg;
		new Thread() {

			@Override
			public void run() {
				// ÐèÒª»¨Ê±¼ä¼ÆËãµÄ·½·¨
				try {
					if (nvpCont == null) {
						data = NetTraffic.getHtmlContent(dataUrl);
					} else {
						data = NetTraffic.postHtmlContent(dataUrl, nvpCont);
						nvpCont = null;
					}
					// Thread.sleep(5000);
				} catch (Exception e) {
					data = "error";
				}

				if (datamsg == Const.MSGTOPIC || datamsg == Const.MSGTOPICNEXT
						|| datamsg == Const.MSGTOPICREFREASH)
				{
					
					if(imageTrd!=null&&imageTrd.isAlive())
					{
						imageTrd.setName("NoUse");
					}
					topicWithImg = false;
					final  String topicDataInfo = StringUtil.getTopicInfo(data,nowPos,isIP,isWifi,isPic);

					isNext = StringUtil.isNext;
					if(StringUtil.curAreaName!=null&&!StringUtil.curAreaName.equals("byztm"))
						curAreaName =  StringUtil.curAreaName;
					topicWithImg = StringUtil.topicWithImg;
					
					topicData = Html.fromHtml(topicDataInfo,
							new Html.ImageGetter() {
								public Drawable getDrawable(String source) {
									
									Drawable drawable = null;
									if ("xian".equals(source)) {
										drawable = xianDraw;
										drawable.setBounds(0, 0, sWidth, 2);
									}
									else if (source.startsWith("[")) 
									{
										try {
											drawable = fetchDrawable(source); 
										} catch (Exception e) {
											return null;
										}
										if (drawable==null) return null;
										int iw = drawable.getIntrinsicWidth();
										drawable.setBounds(0, 0, iw, drawable
												.getIntrinsicHeight());
									}
									return drawable;

								}
							}, null);
					if(topicWithImg)
					{
						
						imageTrd = new Thread(topicDataInfo) {

						@Override
						public void interrupt() {
							this.stop();
						}

						@Override
						public void run() {
							// ÐèÒª»¨Ê±¼ä¼ÆËãµÄ·½·¨
							topicData = Html.fromHtml(topicDataInfo,
									new Html.ImageGetter() {

										public Drawable getDrawable(String source) {
											
											Drawable drawable = null;
											if ("xian".equals(source)) {
												drawable = xianDraw;
												drawable.setBounds(0, 0, sWidth, 2);
											} 
											
											else if (source.startsWith("http")||source.startsWith("[")) {
												try {
													drawable = fetchDrawable(source); 
												} catch (Exception e) {
													return null;
												}
												if (drawable==null) return null;
												int iw = drawable.getIntrinsicWidth();
												drawable.setBounds(0, 0, iw, drawable
														.getIntrinsicHeight());
											}
											return drawable;

										}
									}, null);
							if(this.getName()!=null&&!this.getName().equals("NoUse"))
							{
								sendMsg(Const.MSGTOPICREFREASH);
							}

						}
					};
					imageTrd.start();
					}
					
				}
				sendMsg(datamsg);
			}
		}.start();

	}

	HashMap<String, SoftReference<Drawable>> drawableMap = new HashMap<String, SoftReference<Drawable>>();

	public Drawable fetchDrawable(String source) {
		SoftReference<Drawable> drawableRef = drawableMap.get(source);
		if (drawableRef != null) {
			Drawable drawable = drawableRef.get();
			if (drawable != null)
				return drawable;

			drawableMap.remove(source);
		}
		Drawable drawable =null;
		if(source.startsWith("["))
		{
			Resources res = getResources();
			Integer i = smilyAll.get(source);
			if(i!=null)
			{
				drawable = res.getDrawable( i);
			}
			else
				return null;
		}
		else if(source.startsWith("http"))
		{
			 drawable = zoomDrawable(source);
		}
		else
		{
			return null;
		}

		drawableRef = new SoftReference<Drawable>(drawable);
		drawableMap.put(source, drawableRef);

		return drawable;

	}

	/**
	 * ¸ù¾ÝÍ¼Æ¬ÍøÂçµØÖ·»ñÈ¡Í¼Æ¬µÄbyte[]ÀàÐÍÊý¾Ý
	 * 
	 * @param urlPath
	 *            Í¼Æ¬ÍøÂçµØÖ·
	 * @return Í¼Æ¬Êý¾Ý
	 */
	public byte[] getImageFromURL(String urlPath) {
		byte[] data = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlPath);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			// conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(6000);
			is = conn.getInputStream();
			if (conn.getResponseCode() == 200) {
				data = readInputStream(is);
			} else {
				data = null;
				return data;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			conn.disconnect();
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	/**
	 * ¶ÁÈ¡InputStreamÊý¾Ý£¬×ªÎªbyte[]Êý¾ÝÀàÐÍ
	 * 
	 * @param is
	 *            InputStreamÊý¾Ý
	 * @return ·µ»Øbyte[]Êý¾Ý
	 */
	public byte[] readInputStream(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = -1;
		try {
			while ((length = is.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}
			baos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] data = baos.toByteArray();
		try {
			is.close();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * ¸ù¾ÝÍøÂçÍ¼Æ¬µØÖ·¼¯ÅúÁ¿»ñÈ¡ÍøÂçÍ¼Æ¬
	 * 
	 * @param urlPath
	 *            ÍøÂçÍ¼Æ¬µØÖ·Êý×é
	 * @return ·µ»ØBitmapÊý¾ÝÀàÐÍµÄÊý×é
	 */
	public Drawable zoomDrawable(String urlPath) {

		Bitmap bitmaps;

		byte[] imageByte = getImageFromURL(urlPath.trim());

		// ÒÔÏÂÊÇ°ÑÍ¼Æ¬×ª»¯ÎªËõÂÔÍ¼ÔÙ¼ÓÔØ
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0,
				imageByte.length, options);

		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inInputShareable = true;

		int widthRatio = (int) Math.ceil(options.outWidth / sWidth);
		int heightRatio = (int) Math.ceil(options.outHeight / sLength);
		if (widthRatio > 1 || heightRatio > 1) {
			if (widthRatio > heightRatio) {
				options.inSampleSize = widthRatio;
			} else {
				options.inSampleSize = heightRatio;
			}
		}


		bitmaps = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length,
				options);
		return new BitmapDrawable(null, bitmaps);

	}

	

	private void exitPro() {
		new AlertDialog.Builder(TestAndroidActivity.this).setTitle("ÌáÊ¾")
				.setMessage("È·¶¨ÍË³öÂð£¿").setPositiveButton("È·¶¨",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								getUrlHtml(loginoutURL,123);
								
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
	
									e.printStackTrace();
								}
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}

						}).setNegativeButton("È¡Ïû",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();
	}

	private void sendMsg(int meg) {
		Message msg = new Message();
		msg.what = meg;
		handler.sendMessage(msg);
	}

	

	@Override
	protected void onDestroy() {

		super.onDestroy();

		getUrlHtml(loginoutURL,123);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}

		System.gc();

		System.exit(0);

	}

	public boolean onTouch(View arg0, MotionEvent arg1) {
		return mGestureDetector.onTouchEvent(arg1);
	}

	public boolean onDown(MotionEvent arg0) {
		return false;
	}
	
	
	private static final int SWIPE_MIN_DISTANCE = 120;   
	private static final int SWIPE_THRESHOLD_VELOCITY = 200; 
	

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE ) { 
			
			getUrlHtml(huifuUrl, Const.MSGPST);
			
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE ) {   
				
				if (curStatus == 1) {
					chaToMain();
					if (top10TopicList != null) {
						setTopics();
					}
				} else {
					chaToArea(null);
				}
			}  

		
		
		return false;
	}

	public void onLongPress(MotionEvent arg0) {
		
	}

	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	public void onShowPress(MotionEvent arg0) {

		
	}

	public boolean onSingleTapUp(MotionEvent arg0) {
		ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
		float x = arg0.getRawX();
		float y = arg0.getRawY();
		//µã»÷ÉÏ·­ºÍµã»÷ÏÂ·­
		if(y>sv.getHeight()-sLength/6&&x>(sWidth*3/4))
		{
			sv.scrollBy(0, sv.getHeight()-20);
		}
		if(y<sLength/3&&x>(sWidth*3/4))
		{
			sv.scrollBy(0, 20-sv.getHeight());
		}
	return false;
	}

}