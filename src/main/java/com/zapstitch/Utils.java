package com.zapstitch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Utils {
	public static String calculateDateTenDaysAgo(Date startDate) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DATE, -10);

		Date endDate = cal.getTime();
		return dateFormat.format(endDate);
	}

	@SuppressWarnings("deprecation")
	public static String driverFunction(String authCode){
		String accessToken = OauthClient.getAccessToken(authCode);
		OauthClient oauthClient = new OauthClient(accessToken);
		String emailAddress = oauthClient.getEmailAddress();

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date startDate = new Date(System.currentTimeMillis());

		String startDateT1 = dateFormat.format(startDate);
		String endDateT1 = Utils.calculateDateTenDaysAgo(startDate);

		String startDateT2 = dateFormat.format(new Date(endDateT1));
		String endDateT2 = Utils.calculateDateTenDaysAgo(new Date(startDateT2));

		String startDateT3 = dateFormat.format(new Date(endDateT2));
		String endDateT3 = Utils.calculateDateTenDaysAgo(new Date(startDateT3));

		String startDateT4 = dateFormat.format(new Date(endDateT3));
		String endDateT4 = Utils.calculateDateTenDaysAgo(new Date(startDateT4));

		String startDateT5 = dateFormat.format(new Date(endDateT4));
		String endDateT5 = Utils.calculateDateTenDaysAgo(new Date(startDateT5));

		String startDateT6 = dateFormat.format(new Date(endDateT5));
		String endDateT6 = Utils.calculateDateTenDaysAgo(new Date(startDateT6));

		String startDateT7 = dateFormat.format(new Date(endDateT6));
		String endDateT7 = Utils.calculateDateTenDaysAgo(new Date(startDateT7));

		String startDateT8 = dateFormat.format(new Date(endDateT7));
		String endDateT8 = Utils.calculateDateTenDaysAgo(new Date(startDateT8));

		String startDateT9 = dateFormat.format(new Date(endDateT8));
		String endDateT9 = Utils.calculateDateTenDaysAgo(new Date(startDateT9));

		Thread t1 = new Thread(new Runner(oauthClient,startDateT1,endDateT1)); 
		Thread t2 = new Thread(new Runner(oauthClient,startDateT2,endDateT2));
		Thread t3 = new Thread(new Runner(oauthClient,startDateT3,endDateT3));
		Thread t4 = new Thread(new Runner(oauthClient,startDateT4,endDateT4)); 
		Thread t5 = new Thread(new Runner(oauthClient,startDateT5,endDateT5));
		Thread t6 = new Thread(new Runner(oauthClient,startDateT6,endDateT6));
		Thread t7 = new Thread(new Runner(oauthClient,startDateT7,endDateT7)); 
		Thread t8 = new Thread(new Runner(oauthClient,startDateT8,endDateT8));
		Thread t9 = new Thread(new Runner(oauthClient,startDateT9,endDateT9));

		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();
		t7.start();
		t8.start();
		t9.start();
		try {
			t1.join();
			t2.join();
			t3.join();
			t4.join();
			t5.join();
			t6.join();
			t7.join();
			t8.join();
			t9.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ArrayList<EmailCounter> emailReportArrayList = convertHashMapToArrayList(OauthClient.getCompleteEmailReport());
		System.out.println("\nAll threads : \n");
		for (EmailCounter emailCounter : emailReportArrayList) {
			System.out.println(emailCounter.getEmailId() + " - " + emailCounter.getCounter());
		}
		return generatetext(emailAddress,startDate,new Date(endDateT9),emailReportArrayList);
	}

	public static String generatetext(String emailAddress,Date startDate, Date endDate,ArrayList<EmailCounter> emailReportArrayList) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
			String startDateFormatted = dateFormat.format(startDate);
			String endDateFormatted = dateFormat.format(endDate);
			StringBuilder text = new StringBuilder();
			text.append("--------------------------------------------------------------------<br>");
			text.append("Email : " + emailAddress +"<br>");
			text.append("Start Date : " + startDateFormatted + "<br>");
			text.append("End Date : " + endDateFormatted + "<br>");
			text.append("Email                              #convs<br>");
			for(EmailCounter emailCounter : emailReportArrayList) {
				text.append(emailCounter.getEmailId() + "                             " + emailCounter.getCounter() + "<br>");
			}
			text.append("--------------------------------------------------------------------<br>");
			return text.toString();
		} catch(Exception e) {
			e.printStackTrace();
		}


		return null;
	}



	public static ArrayList<EmailCounter> convertHashMapToArrayList(ConcurrentHashMap<String,Integer> completeEmailReport) {

		int counter;
		ArrayList<EmailCounter> emailReportArrayList = new ArrayList<EmailCounter>();
		for (String str : completeEmailReport.keySet()) {

			counter = completeEmailReport.get(str);
			EmailCounter emailCounter = new EmailCounter(str,counter);
			emailReportArrayList.add(emailCounter);
		}
		Collections.sort(emailReportArrayList);
		return emailReportArrayList;

	}
}
