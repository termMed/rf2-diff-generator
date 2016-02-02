package org.ihtsdo.changeanalyzer.utils;

import java.io.*;

public class SortAnalyzer {

	private int[] sortColumns;
	private File file;

	public SortAnalyzer(File file, int[] sortColumns) {
		super();
		this.file = file;
		this.sortColumns = sortColumns;
	}

	public boolean isSortedFile(){
		boolean sorted=true;
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

			String prevValues[]=new String[sortColumns.length];

			if ((nextLine= br.readLine()) != null) {
				splittedLine =nextLine.split("\t");

				for (int i=0;i<sortColumns.length;i++){
					prevValues[i]=splittedLine[sortColumns[i]];
				}

				lines++;
			}
			if ( nextLine!=null) {
				while ((nextLine= br.readLine()) != null && sorted ) {
					splittedLine = nextLine.split("\t");

					for (int i=0;i<sortColumns.length;i++){
						comp=prevValues[i].compareTo(splittedLine[sortColumns[i]]);
						if (comp<0){
							break;
						}
						if (comp>0){
							sorted=false;
							break;
						}
					}
					for (int i=0;i<sortColumns.length;i++){
						prevValues[i]=splittedLine[sortColumns[i]];
					}

					lines++;
				}

			}

			br.close();
			long end1 = System.currentTimeMillis();
			long elapsed1 = (end1 - start1);
			System.out.println("Sorted: " +  sorted + " - Lines processed  : " + lines);
			System.out.println("Completed in " + elapsed1 + " ms");
			return sorted;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
