package org.ihtsdo.changeanalyzer.utils;

import java.io.*;

public class SortedSnapshotAnalyzer {

	private int[] idColumns;
	private File file;

	public SortedSnapshotAnalyzer(File file, int[] idColumns) {
		super();
		this.file = file;
		this.idColumns = idColumns;
	}

	public boolean isSortedSnapshotFile(){
		boolean sortedSnap=true;
		boolean same=false;
		try {
			long start1 = System.currentTimeMillis();

			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
			BufferedReader br = new BufferedReader(isr);

			double lines = 0;
			String nextLine;
			br.readLine();

			String[] splittedLine;
			int comp;

			String prevValues[]=new String[idColumns.length];

			if ((nextLine= br.readLine()) != null) {
				splittedLine =nextLine.split("\t");

				for (int i=0;i<idColumns.length;i++){
					prevValues[i]=splittedLine[idColumns[i]];
				}

				lines++;
			}
			if ( nextLine!=null) {
				while ((nextLine= br.readLine()) != null && sortedSnap && !same) {
					splittedLine = nextLine.split("\t");

					for (int i=0;i<idColumns.length;i++){
						same=false;
						comp=prevValues[i].compareTo(splittedLine[idColumns[i]]);
						if (comp<0){
							break;
						}
						if (comp>0){
							sortedSnap=false;
							break;
						}
						same=true;
					}
					for (int i=0;i<idColumns.length;i++){
						prevValues[i]=splittedLine[idColumns[i]];
					}

					lines++;
				}

			}
			br.close();
			if (same) sortedSnap=false;
			long end1 = System.currentTimeMillis();
			long elapsed1 = (end1 - start1);
			System.out.println("Sorted snapshot: " +  sortedSnap + " - Lines processed  : " + lines);
			System.out.println("Completed in " + elapsed1 + " ms");
			return sortedSnap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
