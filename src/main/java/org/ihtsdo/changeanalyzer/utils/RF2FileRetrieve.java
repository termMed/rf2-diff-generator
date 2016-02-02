package org.ihtsdo.changeanalyzer.utils;

import java.io.File;

public class RF2FileRetrieve {
	private static final String TERMINOLOGY_FOLDER = "Terminology";
	private static final String REFSET_FOLDER = "Refset";
	private static final String ATTRIBUTEVALUE_FILENAME_PART = "AttributeValue";
	private static final Object CONTENT_FOLDER = "Content";
	private static final String END_FILE = ".txt";
	private static final String ASSOCIATION_FILENAME_PART = "AssociationReference";
	private static final Object LANGUAGE_FOLDER = "Language";
	private static final String LANGUAGE_FILENAME_PART = "LanguageFull";
	private static final Object CROSSMAP_FOLDER = "CrossMap";
	private static final String SIMPLEMAP_FILENAME_PART = "SimpleMapFull";
	private static final String CONCEPT_FILENAME_PART = "Concept_";
	private static final String DESCRIPTION_FILENAME_PART = "Description_";
	private static final String RELATIONSHIP_FILENAME_PART = "Relationship_";

	private String releaseFolder;
	private String conceptFile;
	private String descriptionFile;
	private String relationshipFile;
	private String attributeValueFile;
	private String simpleMapFile;
	private String associationFile;
	private String languageFile;

	public RF2FileRetrieve(String releaseFolder) throws Exception {
		super();
		this.releaseFolder = releaseFolder;
		conceptFile = "";
		descriptionFile = "";
		relationshipFile = "";
		attributeValueFile = "";
		simpleMapFile = "";
		associationFile = "";
		languageFile = "";

		File rFolder = new File(this.releaseFolder);
		if (rFolder.isDirectory()) {
			for (File folder : rFolder.listFiles()) {
				if (folder.isDirectory() && folder.getName().equals(TERMINOLOGY_FOLDER)) {
					getTerminololyComponents(folder);
				}
				if (folder.isDirectory() && folder.getName().equals(REFSET_FOLDER)) {
					getRefsetComponents(folder);
				}
			}
		}
		String strErr = "";
		if (conceptFile.equals("")) {
			strErr = "Concept file not found.\n";
		}
		if (descriptionFile.equals("")) {
			strErr += "Description file not found.\n";
		}

		if (relationshipFile.equals("")) {
			strErr += "Relationship file not found.\n";
		}

		if (attributeValueFile.equals("")) {
			strErr += "Attribute value file not found.\n";
		}

		if (simpleMapFile.equals("")) {
			strErr += "Simple map file not found.\n";
		}

		if (associationFile.equals("")) {
			strErr += "Association file not found.\n";
		}

		if (languageFile.equals("")) {
			strErr += "Language file not found.\n";
		}
		if (!strErr.equals("")) {
			throw new Exception("Errors in RF2 retrive file: " + strErr);
		}

	}

	private void getRefsetComponents(File folder) {
		for (File childFolder : folder.listFiles()) {
			if (childFolder.isDirectory() && childFolder.getName().equals(CONTENT_FOLDER)) {
				for (File component : childFolder.listFiles()) {
					if (component.getName().indexOf(ATTRIBUTEVALUE_FILENAME_PART) > -1 && component.getName().toLowerCase().endsWith(END_FILE)) {
						attributeValueFile = component.getAbsolutePath();
					}
					if (component.getName().indexOf(ASSOCIATION_FILENAME_PART) > -1 && component.getName().toLowerCase().endsWith(END_FILE)) {
						associationFile = component.getAbsolutePath();
					}
				}
			}
			if (childFolder.isDirectory() && childFolder.getName().equals(LANGUAGE_FOLDER)) {
				for (File component : childFolder.listFiles()) {
					if (component.getName().indexOf(LANGUAGE_FILENAME_PART) > -1 && component.getName().toLowerCase().endsWith(END_FILE)) {
						languageFile = component.getAbsolutePath();
					}
				}
			}
			if (childFolder.isDirectory() && childFolder.getName().equals(CROSSMAP_FOLDER)) {
				for (File component : childFolder.listFiles()) {
					if (component.getName().indexOf(SIMPLEMAP_FILENAME_PART) > -1 && component.getName().toLowerCase().endsWith(END_FILE)) {
						simpleMapFile = component.getAbsolutePath();
					}
				}
			}
		}
	}

	private void getTerminololyComponents(File folder) {
		for (File component : folder.listFiles()) {
			if (component.getName().indexOf(CONCEPT_FILENAME_PART) > -1 && component.getName().toLowerCase().endsWith(END_FILE)) {
				conceptFile = component.getAbsolutePath();
			}
			if (component.getName().indexOf(DESCRIPTION_FILENAME_PART) > -1 && component.getName().toLowerCase().endsWith(END_FILE)) {
				descriptionFile = component.getAbsolutePath();
			}
			if (component.getName().indexOf(RELATIONSHIP_FILENAME_PART) > -1 && component.getName().toLowerCase().endsWith(END_FILE)) {
				relationshipFile = component.getAbsolutePath();
			}
		}
	}

	/**
	 * @return the releaseFolder
	 */
	public String getReleaseFolder() {
		return releaseFolder;
	}

	/**
	 * @return the conceptFile
	 */
	public String getConceptFile() {
		return conceptFile;
	}

	/**
	 * @return the descriptionFile
	 */
	public String getDescriptionFile() {
		return descriptionFile;
	}

	/**
	 * @return the relationshipFile
	 */
	public String getRelationshipFile() {
		return relationshipFile;
	}

	/**
	 * @return the attributeValueFile
	 */
	public String getAttributeValueFile() {
		return attributeValueFile;
	}

	/**
	 * @return the simpleMapFile
	 */
	public String getSimpleMapFile() {
		return simpleMapFile;
	}

	/**
	 * @return the associationFile
	 */
	public String getAssociationFile() {
		return associationFile;
	}

	/**
	 * @return the languageFile
	 */
	public String getLanguageFile() {
		return languageFile;
	}
}
