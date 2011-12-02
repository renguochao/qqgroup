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

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.ClientProtocolException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ztm.R;
import com.ztm.AsyncImageLoader.ImageCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.Button;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TestAndroidActivity extends Activity {

	private static final int MSGWHAT = 0x000000;

	private static final int MSGTOPIC = 0x000001;

	private static final int MSGTOPICNEXT = 0x000002;

	private static final int MSGAREA = 0x000003;
	private static final int MSGAREAPAGES = 0x000004;

	private static final int MSGLOGIN = 0x000005;

	private static final int MSGPST = 0x000006;

	private static final int MSGPSTNEW = 0x000007;

	private static final int MSGTOPICREFREASH = 0x000008;

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

	int curStatus = 0;// 1 ±íÊ¾´Ó10´óÌø×ª¹ýÈ¥µÄ£¬2±íÊ¾´ÓÌÖÂÛÇøÌø×ª¹ýÈ¥µÄ

	List<TopicInfo> areaTopic;

	int areaNowTopic = 0;

	private int nowPos;

	String urlString = "";

	String curAreaName = "";

	String curTopicId = "";

	String isRem = "false";

	String loginId = "";
	String loginPwd = "";

	private ProgressDialog progressDialog = null;

	SharedPreferences sharedPreferences;

	List<String> areaNamList;

	Drawable drawableFav;

	Drawable drawableDis;

	boolean isLogin = false;

	Cookie[] cookies = null;

	Spanned topicData;

	int scrollY = 0;
	HashMap<String, String> bbsAll;
	ArrayAdapter<String> bbsAlladapter;

	String loginURL = "http://bbs.nju.edu.cn/bbslogin?type=2";// &id=tiztm&pw=6116938

	String loginoutURL = "http://bbs.nju.edu.cn/bbslogout";

	String pstURL = "http://bbs.nju.edu.cn/vd94982/bbspst?board=Pictures&file=M.1322474567.A";
	Drawable xianDraw;
	int sWidth = 480;
	int sLength = 800;

	/**
	 * Called when the activity is first created.
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.bkcolor);
		xianDraw = res.getDrawable(R.drawable.xian);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		sWidth = metric.widthPixels - 30; // ÆÁÄ»¿í¶È£¨ÏñËØ£©
		sLength = metric.heightPixels - 40; // ÆÁÄ»¿í¶È£¨ÏñËØ£©
		// float density = metric.density; // ÆÁÄ»ÃÜ¶È£¨0.75 / 1.0 / 1.5£©
		// sWidth = (int)(width*density -20);

		this.getWindow().setBackgroundDrawable(drawable);
		bbsAll = BBSAll.getBBSAll();
		String[] bbsAllArray = getArray(bbsAll);
		bbsAlladapter = new ArrayAdapter<String>(TestAndroidActivity.this,
				android.R.layout.simple_dropdown_item_1line, bbsAllArray);
		initAllParams();
		chaToLogin();
	}

	private void InitMain() {
		chaToMain();
		getTop10();

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
				getUrlHtml(url, MSGLOGIN);
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
				getTop10();
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

										getUrlHtml(urlString, MSGAREA);

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
								getUrlHtml(urlString, MSGAREA);

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

		Button btnExit = (Button) findViewById(R.id.btn_exit);

		btnExit.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				exitPro();
			}

		});

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

		}

		return true;// super.onKeyDown(keyCode, event);
	}

	private String[] getArray(HashMap<String, String> bbsAll2) {
		String[] ba = new String[bbsAll2.size() * 2];
		int i = 0;
		Set<String> keySet = bbsAll2.keySet();
		for (String string : keySet) {
			ba[i] = string;
			ba[i + 1] = bbsAll2.get(string);
			i += 2;
		}
		return ba;
	}

	private void initAllParams() {
		sharedPreferences = getSharedPreferences("LilyDroid",
				Context.MODE_PRIVATE);
		String name = sharedPreferences.getString("areaName", "");
		areaNamList = new ArrayList<String>();
		isRem = sharedPreferences.getString("isRem", "false");
		loginId = sharedPreferences.getString("loginId", "");
		loginPwd = sharedPreferences.getString("loginPwd", "");

		if (name == null || name.length() < 1)
			return;

		String[] split = name.split(",");
		for (String string : split) {
			areaNamList.add(string);
		}

	}

	/**
	 * 
	 * ÏûÏ¢¿ØÖÆÆ÷£¬ÓÃÀ´¸üÐÂ½çÃæ£¬ÒòÎªÔÚÆÕÍ¨Ïß³ÌÊÇÎÞ·¨ÓÃÀ´¸üÐÂ½çÃæµÄ
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (data.equals("error")) {
				Toast.makeText(TestAndroidActivity.this, "ÄãµÄÍøÂçÃ²ËÆÓÐµãÐ¡ÎÊÌâ~",
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				return;
			}

			switch (msg.what) {
			case MSGWHAT:
				// ÉèÖÃÏÔÊ¾ÎÄ±¾
				// ´¦Àí½âÎödataÊý¾Ý
				top10TopicList = getTop10Topic(data);
				setTopics();
				break;
			case MSGTOPIC:
				// ÉèÖÃÏÔÊ¾ÎÄ±¾

				chaToTopic(topicData);
				break;
			case MSGTOPICNEXT:

				textView = (TextView) findViewById(R.id.label);
				ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
				sv.scrollTo(0, 0);
				textView.setText(topicData);

				break;
			case MSGTOPICREFREASH:
				textView = (TextView) findViewById(R.id.label);
				textView.setText(topicData);
				break;
			case MSGAREA:
				chaToArea(data);
				break;

			case MSGAREAPAGES:
				areaPages(data);
				break;

			case MSGLOGIN:
				checkLogin(data);
				break;
			case MSGPST:
				checkForm(data);
				break;
			case MSGPSTNEW:
				// ·¢ÎÄ¿ÉÄÜ»áÊ§°Ü£¬×¢Òâ±£ÁôÎÄÕÂ
				checkRst(data);
				break;

			default:
				break;

			}
			progressDialog.dismiss();
		}

	};

	/**
	 * ¼ì²é·¢ÎÄ½á¹û
	 */
	private void checkRst(String data) {

		if (data.contains("http-equiv='Refresh'")) {
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

	String pid;
	String reid;
	String cont;

	protected void checkForm(String formData) {
		Document doc = Jsoup.parse(formData);
		Elements ins = doc.getElementsByTag("input");
		// progressDialog.dismiss();
		if (ins.size() != 12) {
			// µÇÂ¼Ê§°Ü£¬ÒªÇóÖØÐÂµÇÂ¼
			if (formData.contains("´Ò´Ò¹ý¿Í")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						TestAndroidActivity.this);
				builder.setMessage("Äã»¹Ã»µÇÂ½ÄØ~È¥µÇÂ¼?").setCancelable(false)
						.setPositiveButton("µÇÂ¼",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										chaToLogin();
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
				Toast.makeText(TestAndroidActivity.this, "·¢ÎÄÊ§°Ü",
						Toast.LENGTH_SHORT).show();
			}

		} else {

			String title = ins.get(0).attr("value");
			pid = ins.get(1).attr("value");
			reid = ins.get(2).attr("value");

			LayoutInflater factory = LayoutInflater
					.from(TestAndroidActivity.this);
			final View acdlgView = factory.inflate(R.layout.acdlg, null);
			AlertDialog dlg = new AlertDialog.Builder(TestAndroidActivity.this)
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
									cont = getStrBetter(titleEdit.getText()
											.toString());

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

										getUrlHtml(url, MSGPSTNEW);
									} catch (UnsupportedEncodingException e) {
										e.printStackTrace();
									}

								}

							}).setNegativeButton("È¡Ïû",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();
			EditText titleEdit = (EditText) acdlgView
					.findViewById(R.id.edt_title);

			titleEdit.setText(title);
			dlg.show();

		}
	}

	public boolean isEnglish(char c) {
		if ((c >= 0 && c <= 9) || (c >= 'a' && c <= 'z')
				|| (c >= 'A' && c <= 'z') || c == ' ') {
			return true;
		} else
			return false;
	}

	private String getStrBetter(String string) {

		String scon = string;// new
								// String(string.getBytes("iso-8859-1"),"gb2312");

		String[] split = scon.split("\n");
		StringBuilder allSb = new StringBuilder();
		for (String sp : split) {
			StringBuilder sb = new StringBuilder(sp);
			int len = sp.length();
			int tempLen = 0;
			for (int i = 0; i < len; i++) {
				char charAt = sb.charAt(i);
				if (isEnglish(charAt)) {
					tempLen++;
				} else {
					tempLen += 2;
				}
				if (tempLen >= 80) {
					sb.insert(i + 1, '\n');
					len++;
					i++;
					tempLen = 0;
				}
			}

			// for(int i=0;i<sp.length()/40;i++)
			// {
			// sb.insert((i+1)*40, '\n');
			// }
			allSb.append(sb.append('\n'));
		}

		string = allSb.toString();

		return string;
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

		cookies = new Cookie[3];

		cookies[0] = new Cookie();
		cookies[0].setDomain("bbs.nju.edu.cn");
		cookies[0].setPath("/");
		cookies[0].setName("_U_NUM");
		cookies[0].setValue(NUM);

		cookies[1] = new Cookie();
		cookies[1].setDomain("bbs.nju.edu.cn");
		cookies[1].setPath("/");
		cookies[1].setName("_U_UID");
		cookies[1].setValue(id);

		cookies[2] = new Cookie();
		cookies[2].setDomain("bbs.nju.edu.cn");
		cookies[2].setPath("/");
		cookies[2].setName("_U_KEY");
		cookies[2].setValue(KEY);

	}

	/**
	 * ½«ÓÉHTMLÒ³Ãæ×ª³öµÄÊý¾Ý×ª»¯ÎªListView¿É¶ÁµÄÐÎÊ½ ¹©10´óÊ¹ÓÃ
	 */
	private void setTopics() {

		LinkAdr = new ArrayList<String>();

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (TopicInfo topicInfo : top10TopicList) {

			Map<String, Object> map = new HashMap<String, Object>();

			map.put("topictitle", " " + topicInfo.getTitle());

			map.put("topicau", " ×÷Õß£º" + topicInfo.getAuthor() + "  ÐÅÇø£º"
					+ topicInfo.getArea() + "  »Ø¸´£º" + topicInfo.getNums());

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
					getUrlHtml(topicUrl, MSGTOPIC);

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
						.put("topicau", " ÖÃ¶¥   ×÷Õß£º" + topicInfo.getAuthor()
								+ " - " + topicInfo.getPubDate() + "  ÈËÆø£º"
								+ topicInfo.getHot());
			} else {
				map
						.put("topicau", " " + topicInfo.getNums() + "   ×÷Õß£º"
								+ topicInfo.getAuthor() + " - "
								+ topicInfo.getPubDate() + "  ÈËÆø£º"
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

					getUrlHtml(topicUrl, MSGTOPIC);

				}
			});
		}
	}

	boolean isNext = true;
	boolean isPrev = true;

	/**
	 * ½âÎö»ñÈ¡µÄÒ³Ãæ ´¦ÀíÄ³¸öÌØ¶¨µÄ»°Ìâ
	 * 
	 * @param data
	 * @return
	 */
	private String getTopicInfo(String data) {
		StringBuffer tiList = new StringBuffer("<br>");
		char s = 10;
		String backS = s + "";
		data = data.replaceAll(backS, "<br>");
		Document doc = Jsoup.parse(data);

		Elements tds = doc.getElementsByTag("textarea");
		int k = 0;
		for (Element element : tds) {
			String text = element.text();
			String content = "";
			/**
			 * ½âÎöTopicµÄ´úÂë ÔÝÊ±²»ÓÃ int auNo = text.indexOf("·¢ÐÅÈË:"); int areaNo =
			 * text.indexOf("ÐÅÇø:"); int titleNo = text.indexOf("±ê Ìâ:"); int
			 * mailNo = text.indexOf("·¢ÐÅÕ¾:"); int tailNo = text.indexOf("<br>
			 * --<br>
			 * "); //¿ÉÒÔ¿¼ÂÇÌáÉýÐ§ÂÊµÄµØ·½ //StringBuffer sb = new StringBuffer("");
			 * 
			 * String au=""; if(auNo==-1||areaNo==-1) { content=text; } else {
			 * au = text.substring(auNo+5, areaNo-2); if(k==0) { String area =
			 * text.substring(areaNo+4, titleNo); curAreaName =
			 * area.replaceAll("<br>
			 * ", ""); String title = text.substring(titleNo+5, mailNo);
			 * tiList+="±êÌâ£º"+title+"ÐÅÇø£º"+area+"<br>
			 * <br>
			 * ";
			 * 
			 * }
			 * 
			 * if(tailNo<mailNo+40) { content =text.substring( mailNo+40); }
			 * else { content = text.substring( mailNo+40, tailNo); }
			 * if(content.startsWith("br>")) { content = content.substring(3); }
			 * }
			 */

			content = text;
			String nowP = "";
			{
				nowP = (nowPos + k) + "";
				if (k == 0) {
					nowP = "0";
				}

			}
			if (k == tds.size() - 1) {
				if (k < 30) {
					isNext = false;
				} else {
					isNext = true;
				}

			}

			k++;

			String nbs = "<br>";// &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

			if (k == 1 && nowPos > 1) {
				content = content.substring(4, content.indexOf("·¢ÐÅÕ¾:"))
						+ "<br><br>...(ÒÔÏÂÊ¡ÂÔ)<br>";
			} else {
				/** * ´¦ÀíÓ²»Ø³µ */
				String[] split = content.split("<br>");

				int j = 0;
				int tempBr = 0;
				StringBuffer sb = new StringBuffer();
				// TODO:²ÎÊý»¯
				// int picNo = 0;
				for (String sconA : split) {

					if (j < 3) {
						j++;
						if (j == 1) {
							sconA = sconA.substring(4);
							int ind = sconA.indexOf(" (");
							int inArea = sconA.indexOf(", ÐÅÇø");

							if (k == 1) {
								// TODO:¿É¼ÓÈëµã»÷²é¿´ÓÃ»§ÐÅÏ¢¹¦ÄÜ
								sb.append("<font color=#0000EE >").append(
										sconA.substring(0, ind)).append(
										"</font>").append(
										sconA.substring(ind, inArea)).append(
										nbs)
										.append(sconA.substring(inArea + 1))
										.append(nbs);
							} else {
								sb.append("<font color=#0000EE >").append(
										sconA.substring(0, ind)).append(
										"</font>").append(
										sconA.substring(ind, inArea)).append(
										nbs);

							}
							continue;
						}
						if (j == 2 && k != 1)
							continue;
						// if(j==3)
						// {
						// //.substring(15,sconA.length()-1)
						// sconA =
						// "<font size='8dp' color=#669900 >"+sconA+"</font>";
						// }

						sb.append(sconA).append(nbs);
						continue;
					} else if (sconA.length() < 1) {
						if (tempBr == 0) {
							tempBr = 1;
							sb.append(sconA).append(nbs);
						}
						continue;
					}

					if (sconA.contains("À´Ô´:£®") || sconA.contains("ÐÞ¸Ä:£®")
							|| sconA.equals("--")) {
						continue;
					}
					sconA = sconA.trim();
					if (sconA.toLowerCase().startsWith("http:")
							&& (sconA.toLowerCase().endsWith(".jpg") || sconA
									.toLowerCase().endsWith(".png")||sconA.toLowerCase().endsWith(".jpeg"))) {

						sb.append("<img src='").append(sconA).append("'><br>");

						continue;
					}

					tempBr = 0;
					String scon = "";
					try {
						scon = new String(sconA.getBytes("gb2312"),
								"iso-8859-1");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if (scon.length() < 71 || scon.length() > 89) {
						sb.append(sconA + nbs);
					} else {
						sb.append(sconA);
					}

				}
				content = sb.toString();
			}

			if (nowP == "0") {
				tiList.append("Â¥Ö÷: ").append(content).append(
						"<img src='xian'><br><br>");// +au+"</font></B>"
			} else {
				tiList.append(nowP).append("Â¥£º").append(content).append(
						"<img src='xian'><br><br>");// +au+"</font></B>"
				// tiList
				// +="<B><font color=#0080ff >"+nowP+"Â¥:</font>"+nbs+content+"<br><br>";//+au+"</font></B>"
			}
		}
		return tiList.toString();
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
				ti.setPubDate(tds.get(curPos + 3).text());
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

	/**
	 * ½âÎö»ñÈ¡µÄÒ³Ãæ ´¦Àí10´óÁÐ±í
	 * 
	 * @param data
	 * @return
	 */
	private List<TopicInfo> getTop10Topic(String data) {
		List<TopicInfo> tiList = new ArrayList<TopicInfo>();
		Document doc = Jsoup.parse(data);
		Elements tds = doc.getElementsByTag("td");
		if (tds.size() != 55) {
			TopicInfo ti = new TopicInfo();
			ti.setTitle(getResources().getString(R.string.getnew));
			tiList.add(ti);
			return tiList;
		}

		for (int i = 1; i < 11; i++) {
			int pos = i * 5;
			TopicInfo ti = new TopicInfo();
			ti.setRank(i + "");
			ti.setLink((tds.get(pos + 2).getElementsByTag("a")).get(0).attr(
					"href"));// ÉèÖÃtitle
			ti.setTitle(tds.get(pos + 2).text());// ÉèÖÃtitle
			ti.setArea(tds.get(pos + 1).text());
			ti.setNums(tds.get(pos + 4).text());
			ti.setAuthor(tds.get(pos + 3).text());
			tiList.add(ti);
		}

		return tiList;
	}

	/**
	 * Ìø×ªµ½ÌÖÂÛÇø½çÃæ
	 * 
	 * @param AreaData
	 */
	private void chaToArea(String AreaData) {
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
				getUrlHtml(newUrl, MSGPST);

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

		getUrlHtml(urlString + "&start=" + startPage, MSGAREAPAGES);

	}

	private void areaPages(String AreaData) {
		areaTopic = getAreaTopic(AreaData);
		listView = (ListView) findViewById(R.id.topicList);
		setAreaTopics();
		listView.setSelection(areaTopic.size() - 1);
	}

	int textViewY = 0;

	/**
	 * Ìø×ªµ½Ä³¸ö»°Ìâ½çÃæ
	 * 
	 * @param AreaData
	 */
	private void chaToTopic(Spanned topicData) {

		setContentView(R.layout.topic);
		textView = (TextView) findViewById(R.id.label);
		textView.setText(topicData);

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
				getUrlHtml(huifuUrl, MSGPST);
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
				getUrlHtml(topicUrl + "&start=" + nowPos, MSGTOPICNEXT);

			}
		});

		Button btnNext = (Button) findViewById(R.id.btn_next);
		btnNext.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// if(!isNext)
				// {
				// Toast.makeText(TestAndroidActivity.this, "µ±Ç°Îª×îºóÒ»Ò³£¡",
				// Toast.LENGTH_SHORT)
				// .show();
				// return;
				//					
				// }
				if (isNext) {
					nowPos = nowPos + 30;
					getUrlHtml(topicUrl + "&start=" + nowPos, MSGTOPICNEXT);
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							TestAndroidActivity.this);
					builder.setMessage("ÒÑÊÇ×îºóÒ»Ò³£¬ÊÇ·ñË¢ÐÂµ±Ç°Ò³?").setCancelable(false)
							.setPositiveButton("Ë¢ÐÂ",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											getUrlHtml(topicUrl + "&start="
													+ nowPos, MSGTOPICREFREASH);
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

	AsyncImageLoader asyncImageLoader = new AsyncImageLoader();

	private void getTop10() {
		String urlString = getResources().getString(R.string.bbstop10);
		getUrlHtml(urlString, MSGWHAT);
	}

	private String dataUrl = "";
	private int datamsg = -1;
	NameValuePair[] nvpCont = null;

	private void getUrlHtml(String url, int msg) {
		if (progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = ProgressDialog.show(TestAndroidActivity.this,
					"ÇëÉÔµÈ...", "×¥È¡ÍøÒ³ÐÅÏ¢ÖÐ...", true);
		}

		dataUrl = url;
		datamsg = msg;
		new Thread() {

			@Override
			public void run() {
				// ÐèÒª»¨Ê±¼ä¼ÆËãµÄ·½·¨
				try {
					if (nvpCont == null) {
						data = getHtmlContent(dataUrl);
					} else {
						data = postHtmlContent(dataUrl, nvpCont);
						nvpCont = null;
					}
					// Thread.sleep(5000);
				} catch (Exception e) {
					data = "error";
				}

				if (datamsg == MSGTOPIC || datamsg == MSGTOPICNEXT
						|| datamsg == MSGTOPICREFREASH)
					topicData = Html.fromHtml(getTopicInfo(data),
							new Html.ImageGetter() {

								public Drawable getDrawable(String source) {
									// Òì²½¼ÓÔØÍ¼Æ¬
									// Drawable drawable =
									// asyncImageLoader.loadDrawable(
									// source, new ImageCallback() {
									//   
									// @Override
									// public void imageLoaded(Drawable
									// imageDrawable,
									// String imageUrl) {
									// if (imageDrawable == null) {
									// } else {
									// imageDrawable.setBounds(0, 0,
									// imageDrawable.getIntrinsicWidth(),
									// imageDrawable
									// .getIntrinsicHeight());
									// }
									// dra = imageDrawable;
									// }
									// });
									// if(source.equals("1")){
									// drawable =
									// Main.this.getResources().getDrawable(R.drawable.aa);
									// } else if (source.equals("2")){
									// drawable =
									// Main.this.getResources().getDrawable(R.drawable.b);
									// } else {
									// drawable =
									// Main.this.getResources().getDrawable(R.drawable.icon);
									// }
									URL url;
									Drawable drawable = null;
									if (source.equals("xian")) {
										drawable = xianDraw;
										drawable.setBounds(0, 0, sWidth, 2);
									} else {
										try {
											// URL myFileUrl = new URL(source);
											drawable = fetchDrawable(source); // Drawable.createFromStream(myFileUrl.openStream(),
																				// "is");
										} catch (Exception e) {
											return null;
										}
										int iw = drawable.getIntrinsicWidth();
										drawable.setBounds(0, 0, iw, drawable
												.getIntrinsicHeight());
									}
									return drawable;

								}
							}, null);

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

		Drawable drawable = zoomDrawable(source);
		drawableRef = new SoftReference<Drawable>(drawable);
		drawableMap.put(urlString, drawableRef);

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
								getHtmlContent(loginoutURL);
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

	private HttpClient getClient() {
		// ¹¹ÔìHttpClientµÄÊµÀý
		HttpClient httpClient = new HttpClient();

		if (cookies != null) {
			for (Cookie cc : cookies) {
				httpClient.getState().addCookie(cc);
			}
		}

		httpClient.getParams().setCookiePolicy(
				CookiePolicy.BROWSER_COMPATIBILITY);
		httpClient.getParams().setParameter(
				"http.protocol.single-cookie-header", true);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(
				5000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(10000);

		return httpClient;
	}

	HttpClient httpClient = null;

	/**
	 * ¸ù¾ÝurlÈ¡µÃÆä¶ÔÓ¦µÄresponse
	 */
	private String getHtmlContent(String url) {
		if (!url.startsWith("http"))
			url = "http:////" + url;
		String result = "";// ·µ»ØµÄ½á¹û
		StringBuffer resultBuffer = new StringBuffer();

		httpClient = getClient();
		// ´´½¨GET·½·¨µÄÊµÀý
		GetMethod getMethod = new GetMethod(url);
		// getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
		// new DefaultHttpMethodRetryHandler());
		getMethod.getParams().setContentCharset("GB2312");
		try {
			// Ö´ÐÐgetMethod
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(
					getMethod.getResponseBodyAsStream(), getMethod
							.getResponseCharSet()));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				resultBuffer.append(inputLine);
				resultBuffer.append("\n");
			}
			result = new String(resultBuffer);
			return result;
		} catch (HttpException e) {
			// ·¢ÉúÖÂÃüµÄÒì³££¬¿ÉÄÜÊÇÐ­Òé²»¶Ô»òÕß·µ»ØµÄÄÚÈÝÓÐÎÊÌâ
			result = "error";
			e.printStackTrace();
		} catch (IOException e) {
			// ·¢ÉúÍøÂçÒì³£
			result = "error";
			e.printStackTrace();
		} finally {
			// ÊÍ·ÅÁ¬½Ó
			getMethod.releaseConnection();
		}
		return result;
	}

	/**
	 * POST Êý¾Ýµ½·þÎñÆ÷ÉÏ
	 */
	private String postHtmlContent(String url, NameValuePair[] nvp) {
		if (!url.startsWith("http"))
			url = "http:////" + url;
		String result = "";// ·µ»ØµÄ½á¹û
		StringBuffer resultBuffer = new StringBuffer();

		httpClient = getClient();
		// ´´½¨GET·½·¨µÄÊµÀý
		PostMethod post = new PostMethod(url);
		// getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
		// new DefaultHttpMethodRetryHandler());
		post.getParams().setContentCharset("GB2312");
		post.setRequestBody(nvp);
		try {
			// Ö´ÐÐgetMethod
			int statusCode = httpClient.executeMethod(post);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + post.getStatusLine());
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(post
					.getResponseBodyAsStream(), post.getResponseCharSet()));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				resultBuffer.append(inputLine);
				resultBuffer.append("\n");
			}
			result = new String(resultBuffer);
			return result;
		} catch (HttpException e) {
			// ·¢ÉúÖÂÃüµÄÒì³££¬¿ÉÄÜÊÇÐ­Òé²»¶Ô»òÕß·µ»ØµÄÄÚÈÝÓÐÎÊÌâ
			result = "error";
			e.printStackTrace();
		} catch (IOException e) {
			// ·¢ÉúÍøÂçÒì³£
			result = "error";
			e.printStackTrace();
		} finally {
			// ÊÍ·ÅÁ¬½Ó
			post.releaseConnection();
		}
		return result;
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

		getHtmlContent(loginoutURL);

		System.gc();

		System.exit(0);

	}

}