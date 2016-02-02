package org.ihtsdo.changeanalyzer.model;

import java.util.List;

public class ChangeSummary {
	String title;
	String from;
	String to;
	String executionTime;
	List<FileChangeReport> reports;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(String executionTime) {
		this.executionTime = executionTime;
	}
	public List<FileChangeReport> getReports() {
		return reports;
	}
	public void setReports(List<FileChangeReport> reports) {
		this.reports = reports;
	}
	
}
