package org.ihtsdo.changeanalyzer.model;

public class Concept {
	public Concept(String cId, String defStatus, String term, String semtag) {
		super();
		this.cId = cId;
		this.defStatus = defStatus;
		this.term = term;
		this.semtag = semtag;
	}
	String cId;
	String defStatus;
	String term;
	String semtag;
	public String getConceptId() {
		return cId;
	}
	public void setConceptId(String conceptId) {
		this.cId = conceptId;
	}
	public String getDefStatus() {
		return defStatus;
	}
	public void setDefStatus(String defStatus) {
		this.defStatus = defStatus;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getSemtag() {
		return semtag;
	}
	public void setSemtag(String semtag) {
		this.semtag = semtag;
	}
}
