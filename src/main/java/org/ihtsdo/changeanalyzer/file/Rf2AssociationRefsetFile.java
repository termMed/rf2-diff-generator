package org.ihtsdo.changeanalyzer.file;

import org.ihtsdo.changeanalyzer.data.Rf2AssociationRefsetRow;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Rf2AssociationRefsetFile extends Rf2RefsetFile<Rf2AssociationRefsetRow> {

	public Rf2AssociationRefsetFile(String filePath) {
		super(filePath);
	}

	@Override
	protected void loadFile() throws Exception {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		br.readLine();
		rows = new HashMap<String, Set<Rf2AssociationRefsetRow>>();
		while (br.ready()) {
			String line = br.readLine();
			Rf2AssociationRefsetRow currentRow = new Rf2AssociationRefsetRow(line);
			if (rows.containsKey(currentRow.getReferencedComponentId())) {
				rows.get(currentRow.getReferencedComponentId()).add(currentRow);
			} else {
				Set<Rf2AssociationRefsetRow> rowSet = new HashSet<Rf2AssociationRefsetRow>();
				rowSet.add(currentRow);
				rows.put(currentRow.getReferencedComponentId(), (Set<Rf2AssociationRefsetRow>) rowSet);
			}
		}

		br.close();
	}

	public ArrayList<String> getTargetComponentChanged(String startDate, String endDate) {
		ArrayList<String> result = new ArrayList<String>();
		Set<String> keyset = rows.keySet();
		for (String long1 : keyset) {
			Set<Rf2AssociationRefsetRow> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			String firstField = null;
			String lastField = null;
			for (Rf2AssociationRefsetRow rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstField = rf2Row.getTargetComponent();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastField = rf2Row.getTargetComponent();
				}
			}
			if (firstField != null && lastField != null && !firstField.equals(lastField)) {
				result.add(long1);
			}
		}
		return result;
	}

	public ArrayList<Rf2AssociationRefsetRow> getRowByReferencedComponentId(Long id) {
		ArrayList<Rf2AssociationRefsetRow> result = new ArrayList<Rf2AssociationRefsetRow>();
		Set<Rf2AssociationRefsetRow> currentRows = rows.get(id.toString());
		String majorDate = "00000000";
		if (currentRows != null) {
			for (Rf2AssociationRefsetRow rf2Row : currentRows) {
				if (rf2Row.getEffectiveTime().compareTo(majorDate) >= 0 && id.toString().equals(rf2Row.getReferencedComponentId())) {
					majorDate = rf2Row.getEffectiveTime();
					result.add(rf2Row);
				}
			}
		}
		return result;
	}

	public Rf2AssociationRefsetRow getLastRowByReferencedComponentId(Long id) {
		Rf2AssociationRefsetRow result =null;
		Set<Rf2AssociationRefsetRow> currentRows = rows.get(id.toString());
		String majorDate = "00000000";
		if (currentRows != null) {
			for (Rf2AssociationRefsetRow rf2Row : currentRows) {
				if (rf2Row.getEffectiveTime().compareTo(majorDate) >= 0 && id.toString().equals(rf2Row.getReferencedComponentId())) {
					majorDate = rf2Row.getEffectiveTime();
					result=rf2Row;
				}
			}
		}
		return result;
	}
}
