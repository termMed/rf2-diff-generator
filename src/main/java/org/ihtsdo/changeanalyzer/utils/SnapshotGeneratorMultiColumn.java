package org.ihtsdo.changeanalyzer.utils;

import java.io.*;

public class SnapshotGeneratorMultiColumn {

	private File sortedFile;
	private Integer[] columnFilterIxs;
	private String[] columnFilterValues;
	private String date;
	private int effectiveTimeColumn;
	private File outputFile;
	private int[] componentColumnsId;

	public SnapshotGeneratorMultiColumn(File sortedFile, String date,
                                        int[] componentColumnsId, int effectiveTimeColumn, File outputFile,
                                        Integer[] columnFilterIxs, String[] columnFilterValues) {
		super();
		this.sortedFile = sortedFile;
		this.date = date;
		this.componentColumnsId = componentColumnsId;
		this.effectiveTimeColumn = effectiveTimeColumn;
		this.outputFile = outputFile;
		this.columnFilterIxs=columnFilterIxs;
		this.columnFilterValues=columnFilterValues;
	}

	public void execute(){
		
		try {
			long start1 = System.currentTimeMillis();

			FileInputStream fis = new FileInputStream(sortedFile);
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
			BufferedReader br = new BufferedReader(isr);

			double lines = 0;
			String nextLine;
			String header = br.readLine();

			if (outputFile.exists()){
				outputFile.delete();
			}
			FileOutputStream fos = new FileOutputStream( outputFile);
			OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);
			
			bw.append(header);
			bw.append("\r\n");

			String[] prevCompoId=new String[componentColumnsId.length];
			String prevLine="";
			boolean newId;
			String[] splittedLine;
			String[] prevSplittedLine;
			boolean bContinue=true;

			while ((prevLine= br.readLine()) != null) {
				prevSplittedLine =prevLine.split("\t",-1);
				
				if (columnFilterIxs!=null){
					bContinue = true;
					for (int i=0;i<columnFilterIxs.length;i++){
						if (prevSplittedLine[columnFilterIxs[i]].compareTo(columnFilterValues[i])!=0){
							bContinue=false;
							break;
						}
					}
				}
				if (bContinue){
					if (prevSplittedLine[effectiveTimeColumn].compareTo(date)<=0){
						for (int i=0;i<componentColumnsId.length;i++){
							prevCompoId[i]=prevSplittedLine[componentColumnsId[i]];
						}
						break;
					}
				}
			}
			if ( prevCompoId[0]!=null && !prevCompoId[0].equals("") ){
				while ((nextLine= br.readLine()) != null) {
					splittedLine = nextLine.split("\t",-1);
					
					if (columnFilterIxs!=null){
						bContinue = true;
						for (int i=0;i<columnFilterIxs.length;i++){
							if (splittedLine[columnFilterIxs[i]].compareTo(columnFilterValues[i])!=0){
								bContinue=false;
								break;
							}
						}
					}
					if (bContinue){

						newId=false;
						for (int i=0;i<componentColumnsId.length;i++){
							if(!splittedLine[componentColumnsId[i]].equals(prevCompoId[i])){
								newId=true;
								break;
							}
						}
						if (!newId){
							if (splittedLine[effectiveTimeColumn].compareTo(date)<=0){
								prevLine=nextLine;
								
							}
						}else{
							if (splittedLine[effectiveTimeColumn].compareTo(date)<=0){
								bw.append(prevLine);
								bw.append("\r\n");
								prevLine=nextLine;

								for (int i=0;i<componentColumnsId.length;i++){
									prevCompoId[i]=splittedLine[componentColumnsId[i]];
								}
								lines++;
							}
						}
					}
				}

				bw.append(prevLine);
				bw.append("\r\n");
				lines++;
				
			}
			
			bw.close();
			br.close();
			long end1 = System.currentTimeMillis();
			long elapsed1 = (end1 - start1);
			System.out.println("Lines in output file  : " + lines);
			System.out.println("Completed in " + elapsed1 + " ms");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
