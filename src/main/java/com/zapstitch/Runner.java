package com.zapstitch;

public class Runner implements Runnable{
	OauthClient oauthClient;
	String startDate;
	String endDate;
	public Runner(OauthClient oauthClient,String startDate,String endDate){
		this.oauthClient = oauthClient;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	public void run() {
		this.oauthClient.driverClass(this.startDate, this.endDate);
	}
}
