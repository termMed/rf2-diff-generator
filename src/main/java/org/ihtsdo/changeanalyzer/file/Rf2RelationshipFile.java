package org.ihtsdo.changeanalyzer.file;

import org.ihtsdo.changeanalyzer.data.Rf2RelationshipRow;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Rf2RelationshipFile extends Rf2File<Rf2RelationshipRow> {

	public Rf2RelationshipFile(String filePath) {
		super(filePath);
	}

	public Rf2RelationshipFile(String filePath, String startDate) {
		super(filePath, startDate);
	}

	@Override
	protected void loadFile() throws Exception {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		br.readLine();
		rows = new HashMap<Long, Set<Rf2RelationshipRow>>();
		int counter = 1;
		while (br.ready()) {
			String line = br.readLine();
			Rf2RelationshipRow currentRow = new Rf2RelationshipRow(line);
			if (rows.containsKey(currentRow.getId())) {
				rows.get(currentRow.getId()).add(currentRow);
			} else {
				Set<Rf2RelationshipRow> rowSet = new HashSet<Rf2RelationshipRow>();
				rowSet.add(currentRow);
				rows.put(currentRow.getId(), (Set<Rf2RelationshipRow>) rowSet);
			}
			counter++;
			if (counter % 500000 == 0) {
				System.out.println(counter);
			}
		}

		br.close();
	}

	@Override
	protected void loadFileSnapshot(String startDate) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		br.readLine();
		rows = new HashMap<Long, Set<Rf2RelationshipRow>>();
		int counter = 1;
		int removeedCounter = 1;
		while (br.ready()) {
			String line = br.readLine();
			Rf2RelationshipRow currentRow = new Rf2RelationshipRow(line);
			if (rows.containsKey(currentRow.getId())) {
				Set<Rf2RelationshipRow> rowSet = rows.get(currentRow.getId());
				for (Rf2RelationshipRow t : rowSet) {
					if (t.getEffectiveTime().compareTo(startDate) < 0 && currentRow.getEffectiveTime().compareTo(t.getEffectiveTime()) > 0
							&& currentRow.getEffectiveTime().compareTo(startDate) < 0) {
						rowSet.remove(t);
						removeedCounter++;
						break;
					}
				}
				rowSet.add(currentRow);
			} else {
				Set<Rf2RelationshipRow> rowSet = new HashSet<Rf2RelationshipRow>();
				rowSet.add(currentRow);
				rows.put(currentRow.getId(), (Set<Rf2RelationshipRow>) rowSet);
			}
			counter++;
			if (counter % 500000 == 0) {
				System.out.println(counter);
			}
		}
		System.out.println("Removed " + removeedCounter);

		br.close();
	}

	public ArrayList<Long> getRelGroupChanged(String startDate, String endDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		for (Long long1 : keyset) {
			Set<Rf2RelationshipRow> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			int firstField = -1;
			int lastField = -1;
			for (Rf2RelationshipRow rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstField = rf2Row.getRelationshipGroup();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastField = rf2Row.getRelationshipGroup();
				}
			}
			if (firstField != -1 && lastField != -1 && firstField != lastField) {
				result.add(long1);
			}
		}
		return result;
	}

	public ArrayList<Long> getModifierIdChanged(String startDate, String endDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		for (Long long1 : keyset) {
			Set<Rf2RelationshipRow> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			Long firstField = null;
			Long lastField = null;
			for (Rf2RelationshipRow rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstField = rf2Row.getModifierId();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastField = rf2Row.getModifierId();
				}
			}
			if (firstField != null && lastField != null && !firstField.equals(lastField)) {
				result.add(long1);
			}
		}
		return result;
	}

	public ArrayList<Long> getCharacteristicTypeIdChanged(String startDate, String endDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		for (Long long1 : keyset) {
			Set<Rf2RelationshipRow> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			Long firstField = null;
			Long lastField = null;
			for (Rf2RelationshipRow rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstField = rf2Row.getCharacteristicTypeId();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastField = rf2Row.getCharacteristicTypeId();
				}
			}
			if (firstField != null && lastField != null && !firstField.equals(lastField)) {
				result.add(long1);
			}
		}
		return result;
	}

	public ArrayList<Long> getTypeIdChanged(String startDate, String endDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		for (Long long1 : keyset) {
			Set<Rf2RelationshipRow> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			Long firstField = null;
			Long lastField = null;
			for (Rf2RelationshipRow rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstField = rf2Row.getTypeId();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastField = rf2Row.getTypeId();
				}
			}
			if (firstField != null && lastField != null && !firstField.equals(lastField)) {
				result.add(long1);
			}
		}
		return result;
	}

	public ArrayList<Long> getDestIdChanged(String startDate, String endDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		for (Long long1 : keyset) {
			Set<Rf2RelationshipRow> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			Long firstField = null;
			Long lastField = null;
			for (Rf2RelationshipRow rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstField = rf2Row.getDestinationId();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastField = rf2Row.getDestinationId();
				}
			}
			if (firstField != null && lastField != null && !firstField.equals(lastField)) {
				result.add(long1);
			}
		}
		return result;
	}

	public ArrayList<Long> getSourceIdChanged(String startDate, String endDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		for (Long long1 : keyset) {
			Set<Rf2RelationshipRow> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			Long firstField = null;
			Long lastField = null;
			for (Rf2RelationshipRow rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstField = rf2Row.getSourceId();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastField = rf2Row.getSourceId();
				}
			}
			if (firstField != null && lastField != null && !firstField.equals(lastField)) {
				result.add(long1);
			}
		}
		return result;
	}

	public Rf2RelationshipRow getById(Long long1, String from) {
		Set<Rf2RelationshipRow> rowsById = rows.get(long1);
		Rf2RelationshipRow result = null;
		for (Rf2RelationshipRow rf2RelationshipRow : rowsById) {
			String majorDate = "0000000";
			if (rf2RelationshipRow.getEffectiveTime().compareTo(from) >= 0 && rf2RelationshipRow.getEffectiveTime().compareTo(majorDate) >= 0) {
				majorDate = rf2RelationshipRow.getEffectiveTime();
				result = rf2RelationshipRow;
			}
		}
		return result;
	}

	public ArrayList<Rf2RelationshipRow> getAllRows(String from, Long long1) {
		ArrayList<Rf2RelationshipRow> result = new ArrayList<Rf2RelationshipRow>();
		Set<Rf2RelationshipRow> currentRows = rows.get(long1);
		for (Rf2RelationshipRow rf2Row : currentRows) {
			if (rf2Row.getEffectiveTime().compareTo(from) >= 0) {
				result.add(rf2Row);
			}
		}
		return result;
	}

}
