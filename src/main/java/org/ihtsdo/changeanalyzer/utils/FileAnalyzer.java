package org.ihtsdo.changeanalyzer.utils;

import org.apache.commons.configuration.XMLConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileAnalyzer {

	public enum FileType {
		RF1_CONCEPTS("rf1-concepts"), RF1_DESCRIPTIONS("rf1-descriptions"), RF1_RELATIONSHIPS("rf1-relationships"), RF1_SUBSETS("rf1-subsets"), RF1_SUBSETMEMBERS("rf1-subsetmembers"), RF1_CROSSMAPS(
				"rf1-crossmaps"), RF1_CROSSMAPSETS("rf1-crossmapsets"), RF1_CROSSMAPTARGETS("rf1-crossmaptargets"), RF2_CONCEPTS("rf2-concepts"), RF2_DESCRIPTIONS("rf2-descriptions"), RF2_RELATIONSHIPS(
				"rf2-relationships"), RF2_SIMPLEMAPS("rf2-simplemaps"), RF2_LANGUAGE("rf2-language"), RF2_ATTRIBUTEVALUE("rf2-attributevalue"), RF2_ASSOCIATION("rf2-association");
		private String typeName;

		private FileType(String typeName) {
			this.typeName = typeName;
		}

		public String getTypeName() {
			return this.typeName;
		}
	}

	public static boolean validate(File inputFile, FileType fileType, File validationConfig) {
		boolean result = true;
		boolean nameResult = false;
		boolean headerResult = false;
		boolean lineResult = false;
		boolean finalLineResult = true;
		try {
			XMLConfiguration xmlConfig = new XMLConfiguration(validationConfig);

			List<String> namePatterns = new ArrayList<String>();

			Object prop = xmlConfig.getProperty("files.file.fileType");
			if (prop instanceof Collection) {
				namePatterns.addAll((Collection) prop);
			}
			System.out.println("namePatterns.size() :" + namePatterns.size());
			boolean toCheck = false;
			String nameRule = null;
			String headerRule = null;
			String contentRule = null;
			for (int i = 0; i < namePatterns.size(); i++) {
				if (xmlConfig.getString("files.file(" + i + ").fileType").equals(fileType.getTypeName())) {
					toCheck = true;
					nameRule = xmlConfig.getString("files.file(" + i + ").nameRule.regex");
					headerRule = xmlConfig.getString("files.file(" + i + ").headerRule.regex");
					contentRule = xmlConfig.getString("files.file(" + i + ").contentRule.regex");
				}
			}
			if (toCheck) {
				nameResult = inputFile.getName().matches(nameRule);
				System.out.println("Validating: " + inputFile.getName());
				// System.out.println(" ** Result for name: " + nameResult);

				FileInputStream fis = new FileInputStream(inputFile);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader br = new BufferedReader(isr);

				double lines = 1;
				String line;
				String header = br.readLine();
				headerResult = header.matches(headerRule);
				// System.out.print(header);
				// System.out.println(" ** Result for Header: " + headerResult);
				while ((line = br.readLine()) != null) {
					lines++;
					lineResult = line.matches(contentRule);
					if (!lineResult && finalLineResult) {
						finalLineResult = false;
						System.out.print(line);
						System.out.println(" ** Eror in line: " + lineResult);
						break;
					}
				}
				// add nameResult
				result = (true == headerResult == finalLineResult);
				System.out.println("Validation result = " + result);

			} else {
				System.out.println("Skip: " + inputFile.getName());
				result = true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (org.apache.commons.configuration.ConfigurationException e) {
			e.printStackTrace();
		}
		return result;
	}

}
