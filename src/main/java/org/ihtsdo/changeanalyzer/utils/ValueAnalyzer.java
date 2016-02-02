package org.ihtsdo.changeanalyzer.utils;

public class ValueAnalyzer {

	public static enum OPERATOR {GREATER,GREATER_EQUAL,LOWER,LOWER_EQUAL,EQUAL,NOT_EQUAL};

	private OPERATOR operator;
	private String filterValue;
	public ValueAnalyzer(OPERATOR operator, String filterValue){
		this.operator=operator;
		this.filterValue=filterValue;
	}
	public boolean StringAnalyze(String value){
		switch (operator){
		case EQUAL:
			return value.equals(filterValue);
		case GREATER:
			return value.compareTo(filterValue)>0;
		case GREATER_EQUAL:
			return value.compareTo(filterValue)>=0;
		case LOWER:
			return value.compareTo(filterValue)<0;
		case LOWER_EQUAL:
			return value.compareTo(filterValue)<=0;
		case NOT_EQUAL:
			return value.compareTo(filterValue)!=0;
			
		}
		return false;
	}
}