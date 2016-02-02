package org.ihtsdo.changeanalyzer.data;

public class Rf2RefsetRow {
	private String id;
	private String effectiveTime;
	private int active;
	private String moduleId;
	private String refsetId;
	private String referencedComponentId;

	public Rf2RefsetRow() {
		super();
	}

	public Rf2RefsetRow(String conceptLine) {
		try {
			if (conceptLine != null) {
				String[] splited = conceptLine.split("\\t");
				if (splited.length >= 6) {
					this.id = splited[0];
					this.effectiveTime = splited[1];
					this.active = Integer.parseInt(splited[2]);
					this.moduleId = splited[3];
					this.refsetId = splited[4];
					this.referencedComponentId = splited[5];
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getRefsetId() {
		return refsetId;
	}

	public void setRefsetId(String refsetId) {
		this.refsetId = refsetId;
	}

	public String getReferencedComponentId() {
		return referencedComponentId;
	}

	public void setReferencedComponentId(String referencedComponentId) {
		this.referencedComponentId = referencedComponentId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + active;
		result = prime * result + ((effectiveTime == null) ? 0 : effectiveTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((moduleId == null) ? 0 : moduleId.hashCode());
		result = prime * result + ((referencedComponentId == null) ? 0 : referencedComponentId.hashCode());
		result = prime * result + ((refsetId == null) ? 0 : refsetId.hashCode());
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
		Rf2RefsetRow other = (Rf2RefsetRow) obj;
		if (active != other.active) {
			return false;
		}
		if (effectiveTime == null) {
			if (other.effectiveTime != null) {
				return false;
			}
		} else if (!effectiveTime.equals(other.effectiveTime)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (moduleId == null) {
			if (other.moduleId != null) {
				return false;
			}
		} else if (!moduleId.equals(other.moduleId)) {
			return false;
		}
		if (referencedComponentId == null) {
			if (other.referencedComponentId != null) {
				return false;
			}
		} else if (!referencedComponentId.equals(other.referencedComponentId)) {
			return false;
		}
		if (refsetId == null) {
			if (other.refsetId != null) {
				return false;
			}
		} else if (!refsetId.equals(other.refsetId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Rf2RefsetRow [id=" + id + ", effectiveTime=" + effectiveTime + ", active=" + active + ", moduleId=" + moduleId + ", refsetId=" + refsetId + ", referencedComponentId=" + referencedComponentId + "]";
	}

}
