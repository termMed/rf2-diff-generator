package org.ihtsdo.changeanalyzer.file;

import org.apache.log4j.Logger;
import org.ihtsdo.changeanalyzer.data.Rf2ConceptRow;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Rf2ConceptFile extends Rf2File<Rf2ConceptRow> {
	private String DEFINED_ID = "900000000000074008";
	private String PRIMITIVE_ID = "900000000000073002";
	private static final Logger logger = Logger.getLogger(Rf2ConceptFile.class);
	public Rf2ConceptFile(String filePath) {
		super(filePath);
	}
	
	public Rf2ConceptFile(String filePath, String startDate) {
		super(filePath);
		try {
			loadFileSnapshot(startDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void loadFile() throws Exception {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		br.readLine();
		rows = new HashMap<Long, Set<Rf2ConceptRow>>();
		while (br.ready()) {
			String line = br.readLine();
			Rf2ConceptRow currentRow = new Rf2ConceptRow(line);
			if (rows.containsKey(currentRow.getId())) {
				rows.get(currentRow.getId()).add(currentRow);
			} else {
				Set<Rf2ConceptRow> rowSet = new HashSet<Rf2ConceptRow>();
				rowSet.add(currentRow);
				rows.put(currentRow.getId(), (Set<Rf2ConceptRow>) rowSet);
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
		rows = new HashMap<Long, Set<Rf2ConceptRow>>();
		int removedCounter = 0;
		while (br.ready()) {
			String line = br.readLine();
			Rf2ConceptRow currentRow = new Rf2ConceptRow(line);
			if (rows.containsKey(currentRow.getId())) {
				Set<Rf2ConceptRow> rowSet = rows.get(currentRow.getId());
				for (Rf2ConceptRow t : rowSet) {
					if (t.getEffectiveTime().compareTo(startDate) < 0 
							&& currentRow.getEffectiveTime().compareTo(t.getEffectiveTime()) > 0 
							&& currentRow.getEffectiveTime().compareTo(startDate) < 0) {
						rowSet.remove(t);
						removedCounter++;
						break;
					}
				}
				rowSet.add(currentRow);
			} else {
				Set<Rf2ConceptRow> rowSet = new HashSet<Rf2ConceptRow>();
				rowSet.add(currentRow);
				rows.put(currentRow.getId(), (Set<Rf2ConceptRow>) rowSet);
			}
		}
		logger.info("Removed concepts older than start date: " + removedCounter);
		br.close();
	}


	public Long getDefinitionStatusId(Long id) {
		Long lastMoudleId = null;
		Set<Rf2ConceptRow> currentRows = rows.get(id);
		String majorDate = "00000000";
		for (Rf2ConceptRow rf2Row : currentRows) {
			if (rf2Row.getId() == id) {
				if (rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastMoudleId = rf2Row.getDefinitionStatusId();
				}
			}
		}
		return lastMoudleId;
	}

	public ArrayList<Long> getDefinedConectps(String startDate, String endDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		for (Long long1 : keyset) {
			Set<Rf2ConceptRow> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			Long firstDefID = null;
			Long lastDefID = null;
			for (Rf2ConceptRow rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstDefID = rf2Row.getDefinitionStatusId();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastDefID = rf2Row.getDefinitionStatusId();
				}
			}
			if (firstDefID != null && lastDefID != null && firstDefID.equals(PRIMITIVE_ID) && lastDefID.equals(DEFINED_ID)) {
				result.add(long1);
			}
		}
		return result;
	}

	public ArrayList<Long> getPrimitivatedConectps(String startDate, String endDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		for (Long long1 : keyset) {
			Set<Rf2ConceptRow> currentRows = rows.get(long1);
			String minorDate = "99999999";
			String majorDate = "00000000";
			Long firstDefID = null;
			Long lastDefID = null;
			for (Rf2ConceptRow rf2Row : currentRows) {
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(minorDate) <= 0) {
					minorDate = rf2Row.getEffectiveTime();
					firstDefID = rf2Row.getDefinitionStatusId();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastDefID = rf2Row.getDefinitionStatusId();
				}
			}
			if (firstDefID != null && lastDefID != null && firstDefID.equals(DEFINED_ID) && lastDefID.equals(PRIMITIVE_ID)) {
				result.add(long1);
			}
		}
		return result;
	}

}
