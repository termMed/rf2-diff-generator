package org.ihtsdo.changeanalyzer.data;

public class Rf2AttributeValueRefsetRow extends Rf2RefsetRow {
	private String valueId;

	public Rf2AttributeValueRefsetRow() {
		super();
	}

	public Rf2AttributeValueRefsetRow(String componentLine) {
		super(componentLine);
		try {
			if (componentLine != null) {
				String[] splited = componentLine.split("\\t");
				if (splited.length > 5) {
					this.valueId = splited[6];
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public String getValueId() {
		return valueId;
	}

	public void setValueId(String valueId) {
		this.valueId = valueId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((valueId == null) ? 0 : valueId.hashCode());
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
		Rf2AttributeValueRefsetRow other = (Rf2AttributeValueRefsetRow) obj;
		if (valueId == null) {
			if (other.valueId != null) {
				return false;
			}
		} else if (!valueId.equals(other.valueId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Rf2AttributeValueRefsetRow [valueId=" + valueId + "]";
	}

}
