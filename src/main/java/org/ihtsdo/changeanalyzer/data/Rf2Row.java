package org.ihtsdo.changeanalyzer.data;

public class Rf2Row {
	private Long id;
	private String effectiveTime;
	private int active;
	private Long moduleId;

	public Rf2Row() {

	}

	public Rf2Row(String conceptLine) {
		try {
			if (conceptLine != null) {
				String[] splited = conceptLine.split("\\t");
				if (splited.length >= 4) {
					this.id = Long.parseLong(splited[0]);
					this.effectiveTime = splited[1];
					this.active = Integer.parseInt(splited[2]);
					this.moduleId = Long.parseLong(splited[3]);
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + active;
		result = prime * result + ((effectiveTime == null) ? 0 : effectiveTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((moduleId == null) ? 0 : moduleId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Rf2Row other = (Rf2Row) obj;
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
		return true;
	}


}
