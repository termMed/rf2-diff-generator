package org.ihtsdo.changeanalyzer.data;

public class Rf2AssociationRefsetRow extends Rf2RefsetRow {
	private String targetComponent;

	public Rf2AssociationRefsetRow() {
		super();
	}

	public Rf2AssociationRefsetRow(String componentLine) {
		super(componentLine);
		try {
			if (componentLine != null) {
				String[] splited = componentLine.split("\\t");
				if (splited.length > 5) {
					this.targetComponent = splited[6];
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public String getTargetComponent() {
		return targetComponent;
	}

	public void setTargetComponent(String targetComponent) {
		this.targetComponent = targetComponent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((targetComponent == null) ? 0 : targetComponent.hashCode());
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
		Rf2AssociationRefsetRow other = (Rf2AssociationRefsetRow) obj;
		if (targetComponent == null) {
			if (other.targetComponent != null) {
				return false;
			}
		} else if (!targetComponent.equals(other.targetComponent)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Rf2AssociationRefsetRow [targetComponent=" + targetComponent + "]";
	}

}
