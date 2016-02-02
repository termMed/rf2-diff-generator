package org.ihtsdo.changeanalyzer.data;

public class Rf2DescriptionRow extends Rf2Row {
	private Long conceptId;
	private String languageCode;
	private Long typeId;
	private String term;
	private Long caseSignificanceId;

	public Rf2DescriptionRow() {
		super();
	}

	public Rf2DescriptionRow(String conceptLine) {
		super(conceptLine);
		try {
			if (conceptLine != null) {
				String[] splited = conceptLine.split("\\t");
				if (splited.length == 9) {
					this.conceptId = Long.parseLong(splited[4]);
					this.languageCode = splited[5];
					this.typeId = Long.parseLong(splited[6]);
					this.term = splited[7];
					this.caseSignificanceId = Long.parseLong(splited[8]);
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public Long getConceptId() {
		return conceptId;
	}

	public void setConceptId(Long conceptId) {
		this.conceptId = conceptId;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Long getCaseSignificanceId() {
		return caseSignificanceId;
	}

	public void setCaseSignificanceId(Long caseSignificanceId) {
		this.caseSignificanceId = caseSignificanceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((caseSignificanceId == null) ? 0 : caseSignificanceId.hashCode());
		result = prime * result + ((conceptId == null) ? 0 : conceptId.hashCode());
		result = prime * result + ((languageCode == null) ? 0 : languageCode.hashCode());
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Rf2DescriptionRow other = (Rf2DescriptionRow) obj;
		if (caseSignificanceId == null) {
			if (other.caseSignificanceId != null) {
				return false;
			}
		} else if (!caseSignificanceId.equals(other.caseSignificanceId)) {
			return false;
		}
		if (conceptId == null) {
			if (other.conceptId != null) {
				return false;
			}
		} else if (!conceptId.equals(other.conceptId)) {
			return false;
		}
		if (languageCode == null) {
			if (other.languageCode != null) {
				return false;
			}
		} else if (!languageCode.equals(other.languageCode)) {
			return false;
		}
		if (term == null) {
			if (other.term != null) {
				return false;
			}
		} else if (!term.equals(other.term)) {
			return false;
		}
		if (typeId == null) {
			if (other.typeId != null) {
				return false;
			}
		} else if (!typeId.equals(other.typeId)) {
			return false;
		}
		return true;
	}

}
