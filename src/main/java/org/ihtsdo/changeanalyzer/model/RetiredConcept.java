package org.ihtsdo.changeanalyzer.model;

public class RetiredConcept extends Concept{
	String reason;
	String assocRefset;
	String assocTarget;
	String isNew;
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getAssocRefset() {
		return assocRefset;
	}
	public RetiredConcept(String cId, String defStatus, String term,
			String semtag, String reason, String assocRefset,
			String assocTarget, String isNew) {
		super(cId, defStatus, term, semtag);
		this.reason = reason;
		this.assocRefset = assocRefset;
		this.assocTarget = assocTarget;
		this.isNew = isNew;
	}
	public void setAssocRefset(String assocRefset) {
		this.assocRefset = assocRefset;
	}
	public String getAssocTarget() {
		return assocTarget;
	}
	public void setAssocTarget(String assocTarget) {
		this.assocTarget = assocTarget;
	}
	public String getIsNew() {
		return isNew;
	}
	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}
}
