package com.zapstitch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test {

	public static void main(String[] args) {
		Long startTime = System.currentTimeMillis();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date startDate = new Date(System.currentTimeMillis());
        System.out.println("result is "+ dateFormat.format(startDate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DATE, -10);
        
        Date endDate = cal.getTime();
        
       
        System.out.println( dateFormat.format(endDate));
        Long endTime = System.currentTimeMillis();
        System.out.println("Duration : " + (endTime - startTime));
        
        StringBuilder text = new StringBuilder();
		text.append("--------------------------------------------------------------------\n");
		text.append("hello");
		System.out.println(text.toString());
	}

}
