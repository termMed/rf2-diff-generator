package org.ihtsdo.changeanalyzer.utils;


import org.ihtsdo.changeanalyzer.utils.FileAnalyzer.FileType;

import java.io.File;

public class ValidatorTester {

	public static void main(String args[]) throws Exception {
		System.out.println("******* File 1: " +
				FileAnalyzer.validate(new File("/Users/alo/Desktop/rf2 files/xsct2_Concept_Full_INT_20110131.txt"),
						FileType.RF2_CONCEPTS, new File("config/validation-rules.xml"))
		);
		System.out.println("******* File 2: " +
				FileAnalyzer.validate(new File("/Users/alo/Desktop/rf2 files/xsct2_Description_Full-en_INT_20110131.txt"),
						FileType.RF2_DESCRIPTIONS, new File("config/validation-rules.xml"))
		);
		System.out.println("******* File 3: " +
				FileAnalyzer.validate(new File("/Users/alo/Desktop/rf2 files/xsct2_Relationship_Full_INT_20110131.txt"),
						FileType.RF2_RELATIONSHIPS, new File("config/validation-rules.xml"))
		);
		System.out.println("******* File 4: " +
				FileAnalyzer.validate(new File("/Users/alo/Desktop/rf2 files/xder2_sRefset_SimpleMapFull_INT_20110131.txt"),
						FileType.RF2_SIMPLEMAPS, new File("config/validation-rules.xml"))
		);
		System.out.println("******* File 5: " +
				FileAnalyzer.validate(new File("/Users/alo/Desktop/rf2 files/xder2_cRefset_LanguageFull-en_INT_20110131.txt"),
						FileType.RF2_LANGUAGE, new File("config/validation-rules.xml"))
		);
		System.out.println("******* File 6: " +
				FileAnalyzer.validate(new File("/Users/alo/Desktop/rf2 files/xder2_cRefset_AttributeValueFull_INT_20110131.txt"),
						FileType.RF2_ATTRIBUTEVALUE, new File("config/validation-rules.xml"))
		);
		System.out.println("******* File 7: " +
				FileAnalyzer.validate(new File("/Users/alo/Desktop/rf2 files/xder2_cRefset_AssociationReferenceFull_INT_20110131.txt"),
						FileType.RF2_ASSOCIATION, new File("config/validation-rules.xml"))
		);
	}

}