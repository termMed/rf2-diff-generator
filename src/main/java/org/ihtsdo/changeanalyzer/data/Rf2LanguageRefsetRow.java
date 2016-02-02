package org.ihtsdo.changeanalyzer.data;

public class Rf2LanguageRefsetRow extends Rf2RefsetRow {
	private String acceptabilityId;

	public Rf2LanguageRefsetRow() {
		super();
	}

	public Rf2LanguageRefsetRow(String componentLine) {
		super(componentLine);
		try {
			if (componentLine != null) {
				String[] splited = componentLine.split("\\t");
				if (splited.length > 5) {
					this.acceptabilityId = splited[6];
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public String getAcceptabilityId() {
		return acceptabilityId;
	}

	public void setAcceptabilityId(String acceptabilityId) {
		this.acceptabilityId = acceptabilityId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((acceptabilityId == null) ? 0 : acceptabilityId.hashCode());
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
		Rf2LanguageRefsetRow other = (Rf2LanguageRefsetRow) obj;
		if (acceptabilityId == null) {
			if (other.acceptabilityId != null) {
				return false;
			}
		} else if (!acceptabilityId.equals(other.acceptabilityId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Rf2LanguageRefsetRow [acceptabilityId=" + acceptabilityId + "]";
	}

}
