package com.safeway.app.emmd_batch.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class EmmdUtils {

	public static Timestamp timeStampFromString(String str_date, String format) {
		try {
			DateFormat formatter = new SimpleDateFormat(format);
			Date date = (Date) formatter.parse(str_date);
			Timestamp timeStampDate = new Timestamp(date.getTime());

			return timeStampDate;
		} catch (ParseException e) {
			System.out.println("Exception :" + e);
			return null;
		}
	}
	
	public static Date dateFromString(String str_date, String format) {
		try {
			DateFormat formatter = new SimpleDateFormat(format);
			Date date = (Date) formatter.parse(str_date);
			
			return date;
		} catch (ParseException e) {
			System.out.println("Exception :" + e);
			return null;
		}
	}
	public static Integer getNumeric(String str){
		if (StringUtils.isNumeric(str)){
			return new Integer(str);
		}
		return 0;
	}
}
