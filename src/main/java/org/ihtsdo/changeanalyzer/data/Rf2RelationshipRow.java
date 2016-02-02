package org.ihtsdo.changeanalyzer.data;

public class Rf2RelationshipRow extends Rf2Row {
	// sourceId destinationId relationshipGroup typeId characteristicTypeId
	// modifierId
	private Long sourceId;
	private Long destinationId;
	private int relationshipGroup;
	private Long typeId;
	private Long characteristicTypeId;
	private Long modifierId;

	public Rf2RelationshipRow() {
		super();
	}

	public Rf2RelationshipRow(String conceptLine) {
		super(conceptLine);
		try {
			if (conceptLine != null) {
				String[] splited = conceptLine.split("\\t");
				if (splited.length == 10) {
					this.sourceId = Long.parseLong(splited[4]);
					this.destinationId = Long.parseLong(splited[5]);
					this.relationshipGroup = Integer.parseInt(splited[6]);
					this.typeId = Long.parseLong(splited[7]);
					this.characteristicTypeId = Long.parseLong(splited[8]);
					this.modifierId = Long.parseLong(splited[9]);
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public Long getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(Long destinationId) {
		this.destinationId = destinationId;
	}

	public int getRelationshipGroup() {
		return relationshipGroup;
	}

	public void setRelationshipGroup(int relationshipGroup) {
		this.relationshipGroup = relationshipGroup;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public Long getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	public void setCharacteristicTypeId(Long characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}

	public Long getModifierId() {
		return modifierId;
	}

	public void setModifierId(Long modifierId) {
		this.modifierId = modifierId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((characteristicTypeId == null) ? 0 : characteristicTypeId.hashCode());
		result = prime * result + ((destinationId == null) ? 0 : destinationId.hashCode());
		result = prime * result + ((modifierId == null) ? 0 : modifierId.hashCode());
		result = prime * result + relationshipGroup;
		result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
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
		Rf2RelationshipRow other = (Rf2RelationshipRow) obj;
		if (characteristicTypeId == null) {
			if (other.characteristicTypeId != null) {
				return false;
			}
		} else if (!characteristicTypeId.equals(other.characteristicTypeId)) {
			return false;
		}
		if (destinationId == null) {
			if (other.destinationId != null) {
				return false;
			}
		} else if (!destinationId.equals(other.destinationId)) {
			return false;
		}
		if (modifierId == null) {
			if (other.modifierId != null) {
				return false;
			}
		} else if (!modifierId.equals(other.modifierId)) {
			return false;
		}
		if (relationshipGroup != other.relationshipGroup) {
			return false;
		}
		if (sourceId == null) {
			if (other.sourceId != null) {
				return false;
			}
		} else if (!sourceId.equals(other.sourceId)) {
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

	@Override
	public String toString() {
		return "Rf2RelationshipRow [sourceId=" + sourceId + ", destinationId=" + destinationId + ", relationshipGroup=" + relationshipGroup + ", typeId=" + typeId + ", characteristicTypeId=" + characteristicTypeId + ", modifierId=" + modifierId + "]";
	}

}
