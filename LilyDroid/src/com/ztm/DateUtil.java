package com.ztm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {


	public static void main(String[] args) {
		
		System.out.println(formatDateToStr(getDatefromStr("Mon Dec 5 20:30:55 2011")));
		
		System.out.println(formatDateToStrNoWeek(getDatefromStrNoWeek("Dec 5 20:30")));

	}
	static SimpleDateFormat ddf = new SimpleDateFormat( 
			"MMM d HH:mm" , Locale.US); 
	public static Date getDatefromStrNoWeek(String newtime)
	{
		
		Date sdate = null;
		try {
			sdate = ddf.parse(newtime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return sdate;
	}
	static SimpleDateFormat cdf = new SimpleDateFormat( 
	"M-d HH:mm"); 
	public static String formatDateToStrNoWeek(Date newtime)
	{
		
		if(newtime==null) return null;
		String ss = cdf.format(newtime);
		return ss;
	}
	
	static SimpleDateFormat bdf = new SimpleDateFormat( 
			"EEE MMM d HH:mm:ss yyyy" , Locale.US); 
	public static Date getDatefromStr(String newtime)
	{
		
		Date sdate = null;
		try {
			sdate = bdf.parse(newtime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return sdate;
	}
	static SimpleDateFormat df = new SimpleDateFormat( 
	"yyyyƒÍM‘¬d»’ HH:mm:ss");
	public static String formatDateToStr(Date newtime)
	{
		
		if(newtime==null) return null;
		String ss = df.format(newtime);
		return ss;
	}

}
