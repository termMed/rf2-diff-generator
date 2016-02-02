package org.ihtsdo.changeanalyzer.model;

public class Description {
	public Description(String dId, String effTime, String active, String cId,
			String lang, String type, String term, String ics) {
		super();
		this.dId = dId;
		this.effTime = effTime;
		this.active = active;
		this.cId = cId;
		this.lang = lang;
		this.type = type;
		this.term = term;
		this.ics = ics;
	}
	String dId;
	String effTime;
	String active;
	String cId;
	String lang;
	String type;
	String term;
	String ics;
	public String getdId() {
		return dId;
	}
	public void setdId(String dId) {
		this.dId = dId;
	}
	public String getEffTime() {
		return effTime;
	}
	public void setEffTime(String effTime) {
		this.effTime = effTime;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getcId() {
		return cId;
	}
	public void setcId(String cId) {
		this.cId = cId;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getIcs() {
		return ics;
	}
	public void setIcs(String ics) {
		this.ics = ics;
	}
}
