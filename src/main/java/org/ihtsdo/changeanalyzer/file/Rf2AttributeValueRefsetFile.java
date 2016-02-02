package org.ihtsdo.changeanalyzer.file;

import org.ihtsdo.changeanalyzer.data.Rf2AttributeValueRefsetRow;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Rf2AttributeValueRefsetFile extends Rf2RefsetFile<Rf2AttributeValueRefsetRow> {

	public Rf2AttributeValueRefsetFile(String filePath) {
		super(filePath);
	}

	@Override
	protected void loadFile() throws Exception {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		br.readLine();
		rows = new HashMap<String, Set<Rf2AttributeValueRefsetRow>>();
		while (br.ready()) {
			String line = br.readLine();
			Rf2AttributeValueRefsetRow currentRow = new Rf2AttributeValueRefsetRow(line);
			if (rows.containsKey(currentRow.getReferencedComponentId())) {
				rows.get(currentRow.getReferencedComponentId()).add(currentRow);
			} else {
				Set<Rf2AttributeValueRefsetRow> rowSet = new HashSet<Rf2AttributeValueRefsetRow>();
				rowSet.add(currentRow);
				rows.put(currentRow.getReferencedComponentId(), (Set<Rf2AttributeValueRefsetRow>) rowSet);
			}
		}

		br.close();
	}

	public ArrayList<String> getValueIdChanged(String startDate, String endDate) {
		ArrayList<String> result = new ArrayList<String>();
		Set<String> keyset = rows.keySet();
		for (String long1 : keyset) {
			Set<Rf2AttributeValueRefsetRow> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			String firstField = null;
			String lastField = null;
			for (Rf2AttributeValueRefsetRow rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstField = rf2Row.getValueId();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastField = rf2Row.getValueId();
				}
			}
			if (firstField != null && lastField != null && !firstField.equals(lastField)) {
				result.add(long1);
			}
		}
		return result;
	}

	// id effectiveTime active moduleId refsetId referencedComponentId valueId

	public Rf2AttributeValueRefsetRow getRowByReferencedComponentId(Long id) {
		Rf2AttributeValueRefsetRow result = null;
		Set<Rf2AttributeValueRefsetRow> currentRows = rows.get(id);
		String majorDate = "00000000";
		if (currentRows != null) {
			for (Rf2AttributeValueRefsetRow rf2Row : currentRows) {
				if (rf2Row.getEffectiveTime().compareTo(majorDate) >= 0 && id.toString().equals(rf2Row.getReferencedComponentId())) {
					majorDate = rf2Row.getEffectiveTime();
					result = rf2Row;
				}
			}
		}
		return result;
	}
}
