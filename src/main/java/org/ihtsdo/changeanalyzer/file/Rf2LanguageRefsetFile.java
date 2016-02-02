package org.ihtsdo.changeanalyzer.file;

import org.ihtsdo.changeanalyzer.data.Rf2LanguageRefsetRow;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Rf2LanguageRefsetFile extends Rf2RefsetFile<Rf2LanguageRefsetRow> {

	public Rf2LanguageRefsetFile(String filePath) {
		super(filePath);
	}

	@Override
	protected void loadFile() throws Exception {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		br.readLine();
		rows = new HashMap<String, Set<Rf2LanguageRefsetRow>>();
		while (br.ready()) {
			String line = br.readLine();
			Rf2LanguageRefsetRow currentRow = new Rf2LanguageRefsetRow(line);
			if (rows.containsKey(currentRow.getId())) {
				rows.get(currentRow.getId()).add(currentRow);
			} else {
				Set<Rf2LanguageRefsetRow> rowSet = new HashSet<Rf2LanguageRefsetRow>();
				rowSet.add(currentRow);
				rows.put(currentRow.getReferencedComponentId(), (Set<Rf2LanguageRefsetRow>) rowSet);
			}
		}

		br.close();
	}

	public ArrayList<String> getAcceptabilityIdChanged(String startDate, String endDate) {
		ArrayList<String> result = new ArrayList<String>();
		Set<String> keyset = rows.keySet();
		for (String long1 : keyset) {
			Set<Rf2LanguageRefsetRow> currentRows = rows.get(long1);
			String minorDate = "00000000";
			String majorDate = "00000000";
			String firstField = null;
			String lastField = null;
			for (Rf2LanguageRefsetRow rf2Row : currentRows) {
				if (rf2Row.getEffectiveTime().compareTo(startDate)<=0 && rf2Row.getEffectiveTime().compareTo(minorDate) >= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstField = rf2Row.getAcceptabilityId();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getActive()==1 && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastField = rf2Row.getAcceptabilityId();
				}
			}
			if (firstField != null && lastField != null && !firstField.equals(lastField)) {
				result.add(long1);
			}
		}
		return result;
	}
	public Rf2LanguageRefsetRow getLastActiveRow(String from, String string) {
		Rf2LanguageRefsetRow result = null;
		Set<Rf2LanguageRefsetRow> currentRows = rows.get(string);
		String lastDate = "00000000";
		for (Rf2LanguageRefsetRow rf2Row : currentRows) {
			if (rf2Row.getEffectiveTime().compareTo(from) >= 0 && rf2Row.getEffectiveTime().compareTo(lastDate) >= 0) {
				result = rf2Row;
				lastDate = rf2Row.getEffectiveTime();
			}
		}
		return result;
	}

	public boolean exists(Long long1) {
		return rows.containsKey(long1.toString());
	}

}
