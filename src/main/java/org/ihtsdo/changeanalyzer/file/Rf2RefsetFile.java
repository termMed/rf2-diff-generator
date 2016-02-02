package org.ihtsdo.changeanalyzer.file;

import org.ihtsdo.changeanalyzer.data.Rf2RefsetRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Rf2RefsetFile<T extends Rf2RefsetRow> {

	public static String CONCEPT_INACTIVATION_REFSET = "900000000000489007";
	public static String DESCRIPTION_INACTIVATION_REFSET = "900000000000490003";

	protected Map<String, Set<T>> rows;
	protected File file;

	public Rf2RefsetFile(String filePath) {
		file = new File(filePath);
		try {
			loadFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void loadFile() throws Exception {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		br.readLine();
		rows = new HashMap<String, Set<T>>();
		while (br.ready()) {
			String line = br.readLine();
			Rf2RefsetRow currentRow = new Rf2RefsetRow(line);
			if (rows.containsKey(currentRow.getId())) {
				rows.get(currentRow.getId()).add((T) currentRow);
			} else {
				Set<Rf2RefsetRow> rowSet = new HashSet<Rf2RefsetRow>();
				rowSet.add(currentRow);
				rows.put(currentRow.getId(), (Set<T>) rowSet);
			}
		}

		br.close();

	}

	public Set<String> getAllRefsetIds() {
		Set<String> result = new HashSet<String>();
		Set<String> keyset = rows.keySet();
		for (String long1 : keyset) {
			Set<T> currentRows = rows.get(long1);
			for (T rf2Row : currentRows) {
				result.add(rf2Row.getRefsetId());
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.termmed.rf2.file.Rf2FileInterface#getNewComponentIds(java.lang.String
	 * )
	 */
	public ArrayList<String> getNewComponentIds(String startDate) {
		ArrayList<String> result = new ArrayList<String>();
		Set<String> keyset = rows.keySet();
		for (String long1 : keyset) {
			Set<T> currentRows = rows.get(long1);
			String firstDate = "9999999";
			for (T rf2Row : currentRows) {
				if (rf2Row.getEffectiveTime().compareTo(firstDate) < 0) {
					firstDate = rf2Row.getEffectiveTime();
				}
			}
			if (firstDate.compareTo(startDate) > 0) {
				result.add(long1);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.termmed.rf2.file.Rf2FileInterface#getChangedComponentIds(java.lang
	 * .String, java.lang.String)
	 */
	public ArrayList<String> getChangedComponentIds(String startDate, String endDate) {
		ArrayList<String> result = new ArrayList<String>();
		Set<String> keyset = rows.keySet();
		for (String long1 : keyset) {
			Set<T> currentRows = rows.get(long1);
			int componentCount = 0;
			for (T rf2Row : currentRows) {
				if (betweenStartNi(startDate, endDate, rf2Row)) {
					componentCount++;
				}
			}
			if (componentCount > 0) {
				result.add(long1);
			}
		}
		return result;
	}

	private boolean betweenStartNi(String startDate, String endDate, T rf2Row) {
		return rf2Row.getEffectiveTime().compareTo(startDate) > 0 && rf2Row.getEffectiveTime().compareTo(endDate) <= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.termmed.rf2.file.Rf2FileInterface#getRetiredCount(java.lang.String,
	 * java.lang.String)
	 */
	public ArrayList<String> getRetiredCount(String startDate, String endDate) {
		ArrayList<String> result = new ArrayList<String>();
		Set<String> keyset = rows.keySet();
		for (String long1 : keyset) {
			Set<T> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			int firstState = 0;
			int lastState = 0;
			for (T rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstState = rf2Row.getActive();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastState = rf2Row.getActive();
				}
			}
			if (firstState > lastState) {
				result.add(long1);
			}
		}
		return result;
	}

	public ArrayList<String> getReactivatedCount(String startDate, String endDate) {
		ArrayList<String> result = new ArrayList<String>();
		Set<String> keyset = rows.keySet();
		for (String long1 : keyset) {
			Set<T> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			Integer firstState = null;
			Integer lastState = null;
			for (T rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstState = rf2Row.getActive();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastState = rf2Row.getActive();
				}
			}
			if (firstState != null && lastState != null && firstState < lastState) {
				result.add(long1);
			}
		}
		return result;
	}

	public ArrayList<String> getRefsetIdChanged(String startDate, String endDate) {
		ArrayList<String> result = new ArrayList<String>();
		Set<String> keyset = rows.keySet();
		for (String long1 : keyset) {
			Set<T> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			String firstField = null;
			String lastField = null;
			for (T rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstField = rf2Row.getRefsetId();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastField = rf2Row.getRefsetId();
				}
			}
			if (firstField != null && lastField != null && !firstField.equals(lastField)) {
				result.add(long1);
			}
		}
		return result;
	}

	public ArrayList<String> getReferencedComponentIdChanged(String startDate, String endDate) {
		ArrayList<String> result = new ArrayList<String>();
		Set<String> keyset = rows.keySet();
		for (String long1 : keyset) {
			Set<T> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			String firstField = null;
			String lastField = null;
			for (T rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstField = rf2Row.getReferencedComponentId();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastField = rf2Row.getReferencedComponentId();
				}
			}
			if (firstField != null && lastField != null && !firstField.equals(lastField)) {
				result.add(long1);
			}
		}
		return result;
	}

	public boolean between(String startDate, String endDate, T rf2Row) {
		return rf2Row.getEffectiveTime().compareTo(startDate) >= 0 && rf2Row.getEffectiveTime().compareTo(endDate) <= 0;
	}
	
	public void releasePreciousMemory(){
		rows.clear();
		rows = null;
		System.gc();
	}
	
}
