package com.zapstitch;

public class EmailCounter implements Comparable<EmailCounter> {

	private String emailId;
	private int counter;

	public EmailCounter(String emailId, int counter) {
		super();
		this.emailId = emailId;
		this.counter = counter;
	}
	public String getEmailId() {
		return emailId;
	}

	public int getCounter() {
		return counter;
	}

	public int compareTo(EmailCounter emailCounter) {
		
		int counter = emailCounter.getCounter();
		return counter - this.counter;
		
	}

}
