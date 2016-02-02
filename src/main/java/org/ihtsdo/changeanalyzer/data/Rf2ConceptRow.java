package org.ihtsdo.changeanalyzer.data;

public class Rf2ConceptRow extends Rf2Row{
	private Long definitionStatusId;

	public Rf2ConceptRow() {
		super();
	}

	public Rf2ConceptRow(String conceptLine) {
		super(conceptLine);
		try {
			if (conceptLine != null) {
				String[] splited = conceptLine.split("\\t");
				if (splited.length == 5) {
					this.definitionStatusId = Long.parseLong(splited[4]);
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public Long getDefinitionStatusId() {
		return definitionStatusId;
	}

	public void setDefinitionStatusId(Long definitionStatusId) {
		this.definitionStatusId = definitionStatusId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((definitionStatusId == null) ? 0 : definitionStatusId.hashCode());
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
		Rf2ConceptRow other = (Rf2ConceptRow) obj;
		if (definitionStatusId == null) {
			if (other.definitionStatusId != null) {
				return false;
			}
		} else if (!definitionStatusId.equals(other.definitionStatusId)) {
			return false;
		}
		return true;
	}


}
