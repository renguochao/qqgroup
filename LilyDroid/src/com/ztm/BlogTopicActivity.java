package com.ztm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sonyericsson.zoom.ImageTextButton;
import com.sonyericsson.zoom.ImageZoomView;
import com.sonyericsson.zoom.SimpleZoomListener;
import com.sonyericsson.zoom.ZoomState;
import com.sonyericsson.zoom.SimpleZoomListener.ControlType;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import android.widget.AdapterView.OnItemClickListener;

public class BlogTopicActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.bkcolor);
		this.getWindow().setBackgroundDrawable(drawable);
		setTitle("博客浏览");
		Intent intent = getIntent();
		String result = intent.getStringExtra("withSmile");
		String topicUrl = intent.getStringExtra("topicUrl");
		
		final String blogcocon = topicUrl.replace("blogcon", "blogcocon");
		final String blogcomment = topicUrl.replace("blogcon", "blogcomment");
		//http://bbs.nju.edu.cn/blogcocon?userid=Shelly&file=1315018855
		//http://bbs.nju.edu.cn/blogcomment?userid=Shelly&file=1315018855
		//http://bbs.nju.edu.cn/blogcon?userid=Shelly&file=1315018855
		setContentView(R.layout.blogtopic);
		TextView textView = (TextView) findViewById(R.id.label);
		textView.setText(Html.fromHtml(result));
		textView.setTextSize(18);
		
		Button btnPre = (Button) findViewById(R.id.btn_read);
		btnPre.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				NetTraffic.getUrlHtml(BlogTopicActivity.this,blogcomment, Const.MSGREMAIL,handler);
			}
		});
		
		
		
	}
	
	/**
	 *  消息控制器，用来更新界面，因为在普通线程是无法用来更新界面的
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			NetTraffic.runningTasks--;
			if (msg.what != Const.MSGPSTNEW && NetTraffic.data.equals("error")) {
				//NetTraffic.displayMsg("你的网络貌似有点小问题~");
			} else {
				switch (msg.what) {
				case Const.BLOGAREA:
					chaToComment(NetTraffic.data);
					break;
				default:
					break;
				}
			}
			if (NetTraffic.runningTasks < 1) {
				NetTraffic.runningTasks = 0;
				NetTraffic.progressDialog.dismiss();
			}

		}
	};
	
	private void chaToComment(String topicData) {
		char s = 10;
		String backS = s + "";
		String nbs = "<br>";
		topicData = topicData.replaceAll(backS,nbs );
		Document doc = Jsoup.parse(topicData);
		Elements scs = doc.getElementsByTag("textarea");
		if (scs.size() != 1) {
			//NetTraffic.displayMsg("获取博客内容失败!");
		} else {
			Element textArea = scs.get(0);
			String infoView = nbs + textArea.text();
			
			infoView = StringUtil.getBetterTopic(infoView);

			String withSmile = StringUtil.addSmileySpans(infoView,null);
			
			Intent intent = new Intent(BlogTopicActivity.this,
					BlogTopicActivity.class);
			
			
			startActivity(intent);
		}

	}
	
	
	
	
}