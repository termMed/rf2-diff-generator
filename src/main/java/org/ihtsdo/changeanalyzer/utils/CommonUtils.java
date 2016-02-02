package org.ihtsdo.changeanalyzer.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class CommonUtils {
    private static String newLine="\r\n";
    private static HashMap<String, String> HashNames;
    private static File rf2Description;
    public static String[] getSmallestArray(HashMap<BufferedReader, String[]> passedMap, int[] sortColumns) {

        List<String[]> mapValues = new ArrayList<String[]>(passedMap.values());
        //
        Collections.sort(mapValues, new ArrayComparator(sortColumns, false));

        return mapValues.get(0);
    }

    public static String[] getRefsetIds(File file) {
        String[] result = new String[]{};
        HashSet<String> hashRefset = new HashSet<String>();

        FileInputStream ifis;
        try {
            ifis = new FileInputStream(file);
            InputStreamReader iisr = new InputStreamReader(ifis, "UTF-8");
            BufferedReader ibr = new BufferedReader(iisr);

            ibr.readLine();
            String line;
            String[] splittedLine;
            String refsetId;
            while ((line = ibr.readLine()) != null) {
                splittedLine = line.split("\t", -1);
                refsetId = splittedLine[4];
                hashRefset.add(refsetId);
            }
            ibr.close();
            ifis = null;
            iisr = null;
            ibr = null;
            System.gc();
            result = hashRefset.toArray(new String[] {});
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    public static void concatFile(HashSet<File> hFile, File outputfile) {

        try{

            String fileName=outputfile.getName();
            File fTmp = new File(outputfile.getParentFile()  + "/tmp_" + fileName);

            if (fTmp.exists())
                fTmp.delete();

            FileOutputStream fos = new FileOutputStream( fTmp);
            OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);

            boolean first = true;
            String nextLine;
            for (File file:hFile){

                FileInputStream fis = new FileInputStream(file	);
                InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
                BufferedReader br = new BufferedReader(isr);

                nextLine=br.readLine();
                if (first && nextLine!=null){
                    bw.append(nextLine);
                    bw.append("\r\n");
                    first=false;
                }

                while ((nextLine=br.readLine())!=null){
                    bw.append(nextLine);
                    bw.append("\r\n");

                }
                br.close();
                isr.close();
                fis.close();
                br=null;
                isr=null;
                fis=null;

            }

            bw.close();

            if (outputfile.exists())
                outputfile.delete();
            fTmp.renameTo(outputfile) ;

            if (fTmp.exists())
                fTmp.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }finally{

        }
    }
    public static void MergeFile(HashSet<File> hFile, File outputfile) {

        try{
            if (outputfile.exists())
                outputfile.delete();

            outputfile.createNewFile();

            String fileName=outputfile.getName();
            File fTmp = new File(outputfile.getParentFile()  + "/tmp_" + fileName);


            boolean first = true;
            String nextLine;
            for (File file:hFile){


                if (fTmp.exists())
                    fTmp.delete();

                FileOutputStream fos = new FileOutputStream( fTmp);
                OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
                BufferedWriter bw = new BufferedWriter(osw);

                FileInputStream fis = new FileInputStream(file	);
                InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
                BufferedReader br = new BufferedReader(isr);

                FileInputStream ofis = new FileInputStream(outputfile	);
                InputStreamReader oisr = new InputStreamReader(ofis,"UTF-8");
                BufferedReader obr = new BufferedReader(oisr);


                nextLine=br.readLine();
                if (first && nextLine!=null){
                    bw.append(nextLine);
                    bw.append(newLine);
                    first=false;
                }

                while ((nextLine=obr.readLine())!=null){
                    bw.append(nextLine);
                    bw.append(newLine);

                }
                while ((nextLine=br.readLine())!=null){
                    bw.append(nextLine);
                    bw.append(newLine);

                }
                bw.close();
                br.close();
                obr.close();

                if (outputfile.exists())
                    outputfile.delete();
                fTmp.renameTo(outputfile) ;
            }

            if (fTmp.exists())
                fTmp.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }finally{

        }
    }

    public static void FilterFile(File file,File outputFile, int filterColumnIndex,ValueAnalyzer valueAnalyzer) {

        try{
            if (outputFile.exists())
                outputFile.delete();

            outputFile.createNewFile();
            String nextLine;


            FileOutputStream fos = new FileOutputStream( outputFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);

            FileInputStream fis = new FileInputStream(file	);
            InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
            BufferedReader br = new BufferedReader(isr);


            nextLine=br.readLine();
            String[] splittedLine=null;
            bw.append(nextLine);
            bw.append(newLine);

            while ((nextLine=br.readLine())!=null){
                splittedLine=nextLine.split("\t",-1);
                if (valueAnalyzer.StringAnalyze( splittedLine[filterColumnIndex])){
                    bw.append(nextLine);
                    bw.append(newLine);
                }
            }
            bw.close();
            br.close();



        } catch (IOException e) {
            e.printStackTrace();
        }finally{

        }
    }
    public static HashMap<String, String> getNames(File descriptionFile,String date) throws IOException {
        if (HashNames!=null && rf2Description!=null && rf2Description.getAbsolutePath()==descriptionFile.getAbsolutePath()){
            return HashNames;
        }
        HashNames=new HashMap<String, String>();

        File tmpSorted=new File("tmpSorted");
        File tmpSorting=new File("tmpSorting");
        if (!tmpSorted.exists()){
            tmpSorted.mkdirs();
        }
        if (!tmpSorting.exists()){
            tmpSorting.mkdirs();
        }
        rf2Description=descriptionFile;

        File sortDes=new File(tmpSorted,"tmp_" + descriptionFile.getName());
        FileSorter fs=new FileSorter(descriptionFile,sortDes,tmpSorting,new int[]{0,1});
        fs.execute();
        fs=null;
        System.gc();

        File snapDes=new File(tmpSorted,"Snap_" + descriptionFile.getName());

        SnapshotGeneratorMultiColumn ssh=new SnapshotGeneratorMultiColumn(sortDes, date, new int[]{0}, 1, snapDes, null, null);

        ssh.execute();
        ssh=null;
        System.gc();

        FileInputStream fis = new FileInputStream(snapDes);
        InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
        BufferedReader br = new BufferedReader(isr);


        br.readLine();

        String line;
        String[] spl;
        while((line=br.readLine())!=null){
            spl=line.split("\t",-1);
            if (spl[2].compareTo("1")==0
                    && spl[6].compareTo("900000000000003001")==0){
                HashNames.put(spl[4], spl[7]);
            }
        }

        br.close();
        System.gc();
        return HashNames;
    }
}
