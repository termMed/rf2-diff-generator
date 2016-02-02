package org.ihtsdo.changeanalyzer.file;

import org.ihtsdo.changeanalyzer.data.Rf2Row;
import org.ihtsdo.changeanalyzer.utils.LRUCache2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Rf2File<T extends Rf2Row> {
	protected Map<Long, Set<T>> rows;
	protected File file;
	private LRUCache2<String, String> cache = null;

	protected void setUp() throws Exception {
		cache = new LRUCache2<String, String>(1000);
	}

	public Rf2File(String filePath) {
		file = new File(filePath);
		try {
			setUp();
			loadFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Rf2File(String filePath,String startDate) {
		file = new File(filePath);
		try {
			setUp();
			loadFileSnapshot(startDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void initialize() {
		rows = new HashMap<Long, Set<T>>();
	}

	protected void loadFile() throws Exception {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		br.readLine();
		rows = new HashMap<Long, Set<T>>();
		while (br.ready()) {
			String line = br.readLine();
			Rf2Row currentRow = new Rf2Row(line);
			if (rows.containsKey(currentRow.getId())) {
				rows.get(currentRow.getId()).add((T) currentRow);
			} else {
				Set<Rf2Row> rowSet = new HashSet<Rf2Row>();
				rowSet.add(currentRow);
				rows.put(currentRow.getId(), (Set<T>) rowSet);
			}
		}

		br.close();
	}
	protected void loadFileSnapshot(String startDate) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		br.readLine();
		rows = new HashMap<Long, Set<T>>();
		while (br.ready()) {
			String line = br.readLine();
			Rf2Row currentRow = new Rf2Row(line);
			if (rows.containsKey(currentRow.getId())) {
				Set<T> rowSet = rows.get(currentRow.getId());
				for (T t : rowSet) {
					if(t.getEffectiveTime().compareTo(startDate) < 0 && currentRow.getEffectiveTime().compareTo(t.getEffectiveTime()) > 0
							&& currentRow.getEffectiveTime().compareTo(startDate) < 0){
						rowSet.remove(t);
						break;
					}
				}
				rowSet.add((T) currentRow);
			} else {
				Set<Rf2Row> rowSet = new HashSet<Rf2Row>();
				rowSet.add(currentRow);
				rows.put(currentRow.getId(), (Set<T>) rowSet);
			}
		}
		
		br.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.termmed.rf2.file.Rf2FileInterface#getNewComponentIds(java.lang.String
	 * )
	 */
	public ArrayList<Long> getNewComponentIds(String startDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		for (Long long1 : keyset) {
			Set<T> currentRows = rows.get(long1);
			String firstDate = "9999999";
			for (T rf2Row : currentRows) {
				if (rf2Row.getEffectiveTime().compareTo(firstDate) < 0) {
					firstDate = rf2Row.getEffectiveTime();
				}
			}
			if (firstDate.compareTo(startDate) >= 0) {
				result.add(long1);
			}
		}
		return result;
	}

	public ArrayList<Long> getExistingComponentIds(String beforeDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		for (Long long1 : keyset) {
			Set<T> currentRows = rows.get(long1);
			String firstDate = "9999999";
			String lastDate = "0000000";
			for (T rf2Row : currentRows) {
				if (rf2Row.getEffectiveTime().compareTo(firstDate) <= 0) {
					firstDate = rf2Row.getEffectiveTime();
				}
				if (rf2Row.getEffectiveTime().compareTo(lastDate) > 0) {
					lastDate = rf2Row.getEffectiveTime();
				}
			}
			if (firstDate.compareTo(beforeDate) < 0 && lastDate.compareTo(beforeDate) >= 0) {
				result.add(long1);
			}
		}
		return result;
	}

	public int isNewComponent(Long long1, String startDate) {
		String firstDate = "9999999";
		Set<T> currentRows = rows.get(long1);
		for (T rf2Row : currentRows) {
			if (rf2Row.getEffectiveTime().compareTo(firstDate) < 0) {
				firstDate = rf2Row.getEffectiveTime();
			}
		}
		if (firstDate.compareTo(startDate) >= 0) {
			return 1;
		}
		return 0;
	}

	public Long getModuleId(Long id) {
		Long lastMoudleId = null;
		Set<T> currentRows = rows.get(id);
		String majorDate = "00000000";
		for (T rf2Row : currentRows) {
			if (rf2Row.getId() == id) {
				if (rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastMoudleId = rf2Row.getModuleId();
				}
			}
		}
		return lastMoudleId;
	}

	public ArrayList<Long> getNewInactiveComponentIds(String startDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		for (Long long1 : keyset) {
			Set<T> currentRows = rows.get(long1);
			String firstDate = "9999999";
			int status = 0;
			for (T rf2Row : currentRows) {
				if (rf2Row.getEffectiveTime().compareTo(firstDate) < 0) {
					firstDate = rf2Row.getEffectiveTime();
					status = rf2Row.getActive();
				}
			}
			if (firstDate.compareTo(startDate) >= 0 && status == 0) {
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
	public ArrayList<Long> getChangedComponentIds(String startDate, String endDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		for (Long long1 : keyset) {
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

	/**
	 * Gets all components that ended up retired between the specified date
	 * range
	 * 
	 * @param startDate
	 *            : The start date of the range, inclusive
	 * @param endDate
	 *            : the end date of the range, inclusive
	 * @return retired concepts ids
	 */
	public ArrayList<Long> getRetiredComponents(String startDate, String endDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		int conut = 0;
		System.out.println(rows.size());
		for (Long long1 : keyset) {
			Set<T> currentRows = rows.get(long1);
			String biggestStartDate = "00000000";
			String majorDate = "00000000";
			int firstState = 0;
			int lastState = 0;
			for (T rf2Row : currentRows) {
				if (rf2Row.getEffectiveTime().compareTo(startDate) <= 0 && rf2Row.getEffectiveTime().compareTo(biggestStartDate) >= 0) {
					biggestStartDate = rf2Row.getEffectiveTime();
					firstState = rf2Row.getActive();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastState = rf2Row.getActive();
				}
			}
			conut++;
			if (conut % 200000 == 0) {
				System.out.println(".........." + result.size());
			}
			if (firstState > lastState && !majorDate.equals("00000000")) {
				result.add(long1);
			}
		}
		return result;
	}

	public ArrayList<Long> getReactivatedComponents(String startDate, String endDate) {
		ArrayList<Long> result = new ArrayList<Long>();
		Set<Long> keyset = rows.keySet();
		for (Long long1 : keyset) {
			Set<T> currentRows = rows.get(long1);

			String biggestStartDate = "00000000";
			String majorDate = "00000000";
			int firstState = 0;
			int lastState = 0;
			for (T rf2Row : currentRows) {
				if (rf2Row.getEffectiveTime().compareTo(startDate) <= 0 && rf2Row.getEffectiveTime().compareTo(biggestStartDate) >= 0) {
					biggestStartDate = rf2Row.getEffectiveTime();
					firstState = rf2Row.getActive();
				}
				if (between(startDate, endDate, rf2Row) && rf2Row.getEffectiveTime().compareTo(majorDate) >= 0) {
					majorDate = rf2Row.getEffectiveTime();
					lastState = rf2Row.getActive();
				}
			}
			if (  !biggestStartDate.equals("00000000") && !majorDate.equals("00000000") && firstState < lastState) {
				result.add(long1);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.termmed.rf2.file.Rf2FileInterface#getNewRetiredCount()
	 */
	public int getNewRetiredCount() {
		return 0;
	}

	private boolean newConcept(String startDate) {
		// if (!rows.isEmpty() &&
		// rows.get(0).getEffectiveTime().compareTo(startDate) > 0) {
		// return true;
		// } else {
		return false;
		// }
	}

	private boolean changedConcept(String startDate, String endDate) {
		return true;
	}

	private boolean retiredConcept(String startDate, String endDate) {
		// if (!rows.isEmpty() &&
		// rows.get(0).getEffectiveTime().compareTo(startDate) == 0) {
		return true;
		// } else {
		// return false;
		// }
	}

	private boolean reactivated(String startDate, String endDate) {
		return true;
	}

	private boolean newRetired(String startDate, String endDate) {
		return true;
	}

	public boolean between(String startDate, String endDate, T rf2Row) {
		return rf2Row.getEffectiveTime().compareTo(startDate) >= 0 && rf2Row.getEffectiveTime().compareTo(endDate) <= 0;
	}

	public void releasePreciousMemory() {
		rows.clear();
		rows = null;
		System.gc();
	}
	
	public String getFileName(){
		return file.getName();
	}

}
