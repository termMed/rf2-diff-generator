package org.ihtsdo.changeanalyzer.file;

import org.ihtsdo.changeanalyzer.data.Rf2DescriptionRow;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Rf2DescriptionFile extends Rf2File<Rf2DescriptionRow> {

	private Map<Long, String> conceptIdFsn;

	public Rf2DescriptionFile(String filePath) {
		super(filePath);
		try {
			loadFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Rf2DescriptionFile(String filePath, String startDate) {
		super(filePath);
		try {
			loadFileSnapshot(startDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void loadFile() throws Exception {
		conceptIdFsn = new HashMap<Long, String>();
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		br.readLine();
		rows = new HashMap<Long, Set<Rf2DescriptionRow>>();
		int counter = 1;
		while (br.ready()) {
			String line = br.readLine();
			Rf2DescriptionRow currentRow = new Rf2DescriptionRow(line);
			if (currentRow.getTypeId() == 900000000000003001L) {
				conceptIdFsn.put(currentRow.getConceptId(), currentRow.getTerm());
			}

			if (rows.containsKey(currentRow.getId())) {
				rows.get(currentRow.getId()).add(currentRow);
			} else {
				Set<Rf2DescriptionRow> rowSet = new HashSet<Rf2DescriptionRow>();
				rowSet.add(currentRow);
				rows.put(currentRow.getId(), (Set<Rf2DescriptionRow>) rowSet);
			}
			counter++;
			if (counter % 500000 == 0) {
				System.out.print("...............");
			}
		}
		System.out.println();
		br.close();
	}

	@Override
	protected void loadFileSnapshot(String startDate) throws Exception {
		conceptIdFsn = new HashMap<Long, String>();
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		br.readLine();
		rows = new HashMap<Long, Set<Rf2DescriptionRow>>();
		int counter = 1;
		int removedCounter = 0;
		while (br.ready()) {
			String line = br.readLine();
			Rf2DescriptionRow currentRow = new Rf2DescriptionRow(line);
			if (currentRow.getTypeId() == 900000000000003001L) {
				conceptIdFsn.put(currentRow.getConceptId(), currentRow.getTerm());
			}

			if (rows.containsKey(currentRow.getId())) {
				Set<Rf2DescriptionRow> rowSet = rows.get(currentRow.getId());
				for (Rf2DescriptionRow t : rowSet) {
					if (t.getEffectiveTime().compareTo(startDate) < 0 && currentRow.getEffectiveTime().compareTo(t.getEffectiveTime()) > 0 && currentRow.getEffectiveTime().compareTo(startDate) < 0) {
						rowSet.remove(t);
						removedCounter++;
						break;
					}
				}
				rowSet.add(currentRow);
			} else {
				Set<Rf2DescriptionRow> rowSet = new HashSet<Rf2DescriptionRow>();
				rowSet.add(currentRow);
				rows.put(currentRow.getId(), (Set<Rf2DescriptionRow>) rowSet);
			}
			counter++;
			if (counter % 500000 == 0) {
				System.out.print("...............");
			}
		}
		System.out.println("removed descriptions " + removedCounter);
		br.close();
	}

	/**
	 * Gets the descriptions that have changed from start date<BR>
	 * and the end Date from the parameter
	 * 
	 * @param startDate
	 *            : release date
	 * @param endDate
	 *            : release date
	 * @return List of description id's
	 */
	public ArrayList<Long> getChangedDescriptions(String startDate, String endDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		for (Long long1 : keyset) {
			Set<Rf2DescriptionRow> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			String firstTerm = null;
			String lastTerm = null;
			for (Rf2DescriptionRow rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstTerm = rf2Row.getTerm();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastTerm = rf2Row.getTerm();
				}
			}
			if (firstTerm != null && lastTerm != null && !firstTerm.equals(lastTerm)) {
				result.add(long1);
			}
		}
		return result;
	}

	public String getFsn(Long id) {
		return conceptIdFsn.get(id);
	}

	public ArrayList<Long> getCsidChanged(String startDate, String endDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		for (Long long1 : keyset) {
			Set<Rf2DescriptionRow> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			Long firstCsi = null;
			Long lastCsi = null;
			for (Rf2DescriptionRow rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstCsi = rf2Row.getCaseSignificanceId();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastCsi = rf2Row.getCaseSignificanceId();
				}
			}
			if (firstCsi != null && lastCsi != null && !firstCsi.equals(lastCsi)) {
				result.add(long1);
			}
		}
		return result;
	}

	public Long getConceptId(Long long1) {
		Long result = null;
		Set<Rf2DescriptionRow> currentRows = rows.get(long1);
		for (Rf2DescriptionRow rf2Row : currentRows) {
			result = rf2Row.getConceptId();
			break;
		}
		return result;
	}

	public Rf2DescriptionRow getLastActiveRow(String from, Long long1) {
		Rf2DescriptionRow result = null;
		Set<Rf2DescriptionRow> currentRows = rows.get(long1);
		String lastDate = "00000000";
		for (Rf2DescriptionRow rf2Row : currentRows) {
			if (rf2Row.getEffectiveTime().compareTo(from) >= 0 && rf2Row.getEffectiveTime().compareTo(lastDate) >= 0) {
				result = rf2Row;
				lastDate = rf2Row.getEffectiveTime();
			}
		}
		return result;
	}

	public ArrayList<Rf2DescriptionRow> getAllRows(String from, Long long1) {
		ArrayList<Rf2DescriptionRow> result = new ArrayList<Rf2DescriptionRow>();
		Set<Rf2DescriptionRow> currentRows = rows.get(long1);
		for (Rf2DescriptionRow rf2Row : currentRows) {
			if (rf2Row.getEffectiveTime().compareTo(from) >= 0) {
				result.add(rf2Row);
			}
		}
		return result;
	}

}
