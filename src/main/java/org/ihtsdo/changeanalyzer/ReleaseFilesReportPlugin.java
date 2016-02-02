package org.ihtsdo.changeanalyzer;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.changeanalyzer.data.*;
import org.ihtsdo.changeanalyzer.file.*;
import org.ihtsdo.changeanalyzer.model.*;
import org.ihtsdo.changeanalyzer.FileFilterAndSorter;
import org.ihtsdo.changeanalyzer.utils.FileHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @goal report-differences
 * @phase install
 */
//public class ReleaseFilesReportPlugin {
public class ReleaseFilesReportPlugin extends AbstractMojo {

	private static final String SUMMARY_FILE = "diff_index.json";

	private static final String REACTIVATED_CONCEPTS_REPORT = "reactivated_concepts.json";

	private static final String DEFINED_CONCEPTS_REPORT = "defined_concepts.json";

	public static final String NEW_CONCEPTS_FILE = "new_concepts.json";

	public static final String NEW_RELATIONSHIPS_FILE = "new_relationships.json";

	public static final String RETIRED_CONCEPT_REASON_FILE = "inactivated_concept_reason.json";

	public static final String NEW_DESCRIPTIONS_FILE = "new_descriptions.json";

	public static final String OLD_CONCEPTS_NEW_DESCRIPTIONS_FILE = "old_concepts_new_descriptions.json";

	public static final String OLD_CONCEPTS_NEW_RELATIONSHIPS_FILE = "old_concepts_new_relationships.json";

	public static final String NEW_INACTIVE_CONCEPTS_FILE = "new_inactive_concepts.json";

	public static final String REL_GROUP_CHANGED_FILE = "rel_group_changed_relationships.json";

	private static final Logger logger = Logger.getLogger(ReleaseFilesReportPlugin.class);

	private static final String RETIRED_DESCRIPTIONS_FILE = "inactivated_descriptions.json";

	private static final String REACTIVATED_DESCRIPTIONS_FILE = "reactivated_descriptions.json";

	private static final String PRIMITIVE_CONCEPTS_REPORT = "primitive_concepts.json";

	private static final String CHANGED_FSN="changed_fsn.json";

	private static final String SYN_ACCEPTABILITY_CHANGED = "acceptability_changed_on_synonym.json";

	private static final String TARGET_POINTER_TO_CHANGED_SOURCE_DESCRIPTION = "active_language_references_to_now_inactive_descriptions.json";

    private String sep = System.getProperty("line.separator");
    
    private Gson gson = new Gson();
	/**
	 * Location of the directory of report files
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * Location of the directory of the release folder.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File inputDirectory;

	/**
	 * Start date for the reports.
	 * 
	 * @parameter
	 * @required
	 */
	private String startDate;

	/**
	 * End date for the reports.
	 * 
	 * @parameter
	 * @required
	 */
	private String endDate;

	/**
	 * Release date. in case there is more than one release in the release
	 * folder.
	 * 
	 * @parameter
	 */
	private String releaseDate;

	/**
	 * Target Language File
	 * 
	 * @parameter
	 */
	private File targetLanguage;

	private ChangeSummary changeSummary;

	private boolean langTargetCtrl;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			if (!outputDirectory.exists()) {
				outputDirectory.mkdirs();
			}
			changeSummary=new ChangeSummary();
			logger.info("Loading descriptinos");
			Rf2DescriptionFile rf2DescFile = new Rf2DescriptionFile(getFilePath(ReleaseFileType.DESCRIPTION), startDate);
			logger.info("Loading concepts");
			Rf2ConceptFile conceptFile = new Rf2ConceptFile(getFilePath(ReleaseFileType.CONCEPT), startDate);
			logger.info("Loading attribute value refset");
			Rf2AttributeValueRefsetFile attrValue = new Rf2AttributeValueRefsetFile(getFilePath(ReleaseFileType.ATTRIBUTE_VALUE_REFSET));
			logger.info("Loading association value refset");
			Rf2AssociationRefsetFile associationFile = new Rf2AssociationRefsetFile(getFilePath(ReleaseFileType.ASSOCIATION_REFSET));
			logger.info("Loading source US language refset");
			String langPath=getFilePath(ReleaseFileType.LANGUAGE_REFSET);
			File inputFile=new File(langPath);
			File sourceUS=new File(outputDirectory,"tmpUSLang.txt");
			File tempFolder=new File(outputDirectory,"tmpSorting");

			if (!tempFolder.exists()) {
				tempFolder.mkdirs();
			}
			FileFilterAndSorter ffs=new FileFilterAndSorter(inputFile, sourceUS, tempFolder, new int[]{4}, new Integer[]{4}, new String[]{"900000000000509007"});
			ffs.execute();
			ffs=null;
			System.gc();
			Rf2LanguageRefsetFile sourceUSLangFile = new Rf2LanguageRefsetFile(sourceUS.getAbsolutePath());
//			Rf2LanguageRefsetFile sourceGBLangFile=null;
			Rf2LanguageRefsetFile targetLangFile=null;
			langTargetCtrl=false;
			if (targetLanguage!=null){
				if (targetLanguage.exists()){
					langTargetCtrl=true;
//					logger.info("Loading source GB language refset");
//					File sourceGB=new File(outputDirectory,"tmpGBLang.txt");
//					
//					if (!tempFolder.exists()) {
//						tempFolder.mkdirs();
//					}
//					ffs=new FileFilterAndSorter(inputFile, sourceGB, tempFolder, new int[]{4}, new Integer[]{4}, new String[]{"900000000000508004"});
//					ffs.execute();
//					ffs=null;
//					System.gc();
//					sourceGBLangFile = new Rf2LanguageRefsetFile(sourceGB.getAbsolutePath());
					
					logger.info("Loading target language refset");
					targetLangFile = new Rf2LanguageRefsetFile(targetLanguage.getAbsolutePath());
					System.gc();
				}else{
					logger.info("Target language doesn't exist");
				}
			}else{
					logger.info("Target language is null");
			}
//			logger.info("Loading relationships");
//			Rf2RelationshipFile relFile = new Rf2RelationshipFile(getFilePath(ReleaseFileType.RELATIONSHIP), startDate);

			ArrayList<Long> repcomponents = generateNewConceptsReport(rf2DescFile, conceptFile);

			ArrayList<Long> concepts=generatingRetiredConceptReasons(rf2DescFile, conceptFile, attrValue, associationFile);
			
			repcomponents.addAll(concepts);
			
			concepts=null;
//			relFile.releasePreciousMemory();

			concepts=reactivatedConceptsReport(rf2DescFile, conceptFile);
			
			repcomponents.addAll(concepts);
			
			concepts=null;

			conceptFile.releasePreciousMemory();

					
			repcomponents=generatingChangedFSN(rf2DescFile, repcomponents);

			if (langTargetCtrl){
				repcomponents=generateTargetDescriptionPointerToSource(rf2DescFile,targetLangFile,repcomponents);
			}
			System.gc();
			
			repcomponents=generateRetiredDescriptionsReport(rf2DescFile, repcomponents);
			
			repcomponents=generatingExistingConceptsNewDescriptions(rf2DescFile, repcomponents);
			System.gc();
			
			repcomponents=generateReactivatedDescriptionsReport(rf2DescFile, repcomponents);
			
			repcomponents=generateDescriptionAcceptabilityChanges(sourceUSLangFile, rf2DescFile, repcomponents);
		
//ver		punteroalingles
//			generateNewRelationshipsReport(rf2DescFile, relFile);

//			generateOldConceptsNewRelationships(rf2DescFile, relFile, newcomponents);
			//
//			generateRelGroupChangedRelationships(rf2DescFile, relFile, startDate, endDate);


//			generatingDefinedConceptsReport(rf2DescFile, conceptFile);

//			generatingPrimitiveConceptsReport(rf2DescFile, conceptFile);

			repcomponents=null;
			
			if (sourceUS!=null && sourceUS.exists()){
				sourceUS.delete();
			}
			if(tempFolder.exists()){
				FileHelper.emptyFolder(tempFolder);
				tempFolder.delete();
			}
			saveSummary();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private ArrayList<Long> generateTargetDescriptionPointerToSource(
			Rf2DescriptionFile rf2DescFile,
			Rf2LanguageRefsetFile targetLangFile,
			ArrayList<Long> repcomponents) throws Exception {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		fos = new FileOutputStream(new File(outputDirectory, TARGET_POINTER_TO_CHANGED_SOURCE_DESCRIPTION));
		logger.info("Generating " + TARGET_POINTER_TO_CHANGED_SOURCE_DESCRIPTION);
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
		ArrayList<Long> changedDesc = rf2DescFile.getChangedComponentIds(startDate, endDate);
		int count=0;
		boolean bPrim=true;
		bw.append("[");
		for (Long long1 : changedDesc) {
			Rf2LanguageRefsetRow langRow = targetLangFile.getLastActiveRow(startDate,long1.toString());
			if (langRow!=null && langRow.getActive()==1){
				Rf2DescriptionRow rf2DescRow = rf2DescFile.getLastActiveRow(startDate,long1);
				if (!repcomponents.contains(rf2DescRow.getConceptId()) &&
						rf2DescRow.getActive()==0) {
					repcomponents.add(rf2DescRow.getConceptId());
					if (!bPrim){
						bw.append(",");
					}else{
						bPrim=false;
					}
					
					Description desc=new Description(long1.toString(), rf2DescRow.getEffectiveTime() , String.valueOf(rf2DescRow.getActive()) , rf2DescRow.getConceptId().toString() ,
							rf2DescRow.getLanguageCode() , rf2DescFile.getFsn(rf2DescRow.getTypeId()) , rf2DescRow.getTerm() ,
						    rf2DescFile.getFsn(rf2DescRow.getCaseSignificanceId()));
					bw.append(gson.toJson(desc).toString());
					desc=null;
					bw.append(sep);
					count++;
				}
			}
		}
		bw.append("]");
		bw.close();

		addFileChangeReport(TARGET_POINTER_TO_CHANGED_SOURCE_DESCRIPTION,count,"Active language references to now inactive descriptions.");
		
		return repcomponents;		
	}

	private ArrayList<Long> generateDescriptionAcceptabilityChanges(
			Rf2LanguageRefsetFile sourceLangFile, Rf2DescriptionFile rf2DescFile,ArrayList<Long> repcomponents) throws Exception {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		fos = new FileOutputStream(new File(outputDirectory, SYN_ACCEPTABILITY_CHANGED));
		logger.info("Generating " + SYN_ACCEPTABILITY_CHANGED);
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
		ArrayList<String> changedLang = sourceLangFile.getAcceptabilityIdChanged(startDate, endDate);
		int count=0;
		boolean bPrim=true;
		bw.append("[");
		for (String string : changedLang) {
			Rf2DescriptionRow rf2DescRow = rf2DescFile.getLastActiveRow(startDate, Long.parseLong(string));
				if (!repcomponents.contains(rf2DescRow.getConceptId()) 
						&& rf2DescRow.getTypeId()!=900000000000003001L) {
					repcomponents.add(rf2DescRow.getConceptId());
					if (!bPrim){
						bw.append(",");
					}else{
						bPrim=false;
					}
					Description desc=new Description(string, rf2DescRow.getEffectiveTime() , String.valueOf(rf2DescRow.getActive()) , rf2DescRow.getConceptId().toString() ,
							rf2DescRow.getLanguageCode() , rf2DescFile.getFsn(rf2DescRow.getTypeId()) , rf2DescRow.getTerm() ,
						    rf2DescFile.getFsn(rf2DescRow.getCaseSignificanceId()));
					bw.append(gson.toJson(desc).toString());
					desc=null;
					bw.append(sep);
					count++;
				}
		}
		bw.append("]");
		bw.close();

		addFileChangeReport(SYN_ACCEPTABILITY_CHANGED,count,"Acceptability changed in descriptions (no FSN)");
		
		return repcomponents;
	}

	private void saveSummary() throws IOException {
		
		changeSummary.setTitle("Changes in International Edition " +  endDate + " Development Path since " + startDate + " International Release");
		Date now=new Date();
		changeSummary.setExecutionTime(now.toString());
		changeSummary.setFrom(startDate);
		changeSummary.setTo(endDate);
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		fos = new FileOutputStream(new File(outputDirectory, SUMMARY_FILE));
		logger.info("Generating diff_index.json");
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
		
		bw.append(gson.toJson(changeSummary).toString());
		bw.append(sep);
		
		bw.close();
		System.gc();
		
	}

	private String getFilePath(ReleaseFileType descriptions) {
		String result = "";
		switch (descriptions) {
		case DESCRIPTION:
			result = getFilePathRecursive(inputDirectory, "description");
			break;
		case CONCEPT:
			result = getFilePathRecursive(inputDirectory, "concept");
			break;
		case RELATIONSHIP:
			result = getFilePathRecursive(inputDirectory, "relationship");
			break;
		case ASSOCIATION_REFSET:
			result = getFilePathRecursive(inputDirectory, "associationreference");
			break;
		case ATTRIBUTE_VALUE_REFSET:
			result = getFilePathRecursive(inputDirectory, "attributevalue");
			break;
		case LANGUAGE_REFSET:
			result = getFilePathRecursive(inputDirectory, "refset_language");
			break;
		default:
			break;
		}
		return result;
	}

	public String getFilePathRecursive(File folder, String namePart) {
		String result = "";
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			int i = 0;
			while (i < files.length && result.equals("")) {
				result = getFilePathRecursive(files[i], namePart);
				i++;
			}
		} else {
			if (folder.getName().toLowerCase().contains(namePart)) {
				if (releaseDate != null && !releaseDate.equals("") && folder.getName().contains(releaseDate)) {
					result = folder.getPath();
				} else if (releaseDate == null || releaseDate.equals("")) {
					result = folder.getPath();
				}
			}
		}
		return result;
	}

	private void generateRelGroupChangedRelationships(Rf2DescriptionFile rf2DescFile, Rf2RelationshipFile relFile, String startDate, String endDate) throws Exception {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		ArrayList<Long> newRels = relFile.getRelGroupChanged(startDate, endDate);
		fos = new FileOutputStream(new File(outputDirectory, REL_GROUP_CHANGED_FILE));
		logger.info("Generating rel_group_changed_relationships.json");
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
		int count=0;
		boolean bPrim=true;
		bw.append("[");
		for (Long long1 : newRels) {
			if (!bPrim){
				bw.append(",");
			}else{
				bPrim=false;
			}
			Rf2RelationshipRow row = relFile.getById(long1, startDate);
			Relationship rel=new Relationship(row.getId().toString() , String.valueOf(row.getActive()) , rf2DescFile.getFsn(row.getSourceId()),row.getSourceId().toString(), rf2DescFile.getFsn(row.getDestinationId()) ,
					 rf2DescFile.getFsn(row.getTypeId()) , rf2DescFile.getFsn(row.getCharacteristicTypeId()));
				bw.append(gson.toJson(rel).toString());
				rel=null;
				bw.append(sep);
				count++;
		}
		bw.append("]");
		bw.close();

		addFileChangeReport(REL_GROUP_CHANGED_FILE,count,"Relationships group number changed");
		
	}

	private void generatingInactiveConcepts(Rf2DescriptionFile rf2DescFile, Rf2ConceptFile conceptFile) throws FileNotFoundException, UnsupportedEncodingException, IOException {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		fos = new FileOutputStream(new File(outputDirectory, NEW_INACTIVE_CONCEPTS_FILE));
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
		ArrayList<Long> newInactive = conceptFile.getNewInactiveComponentIds(startDate);
		generateConceptReport(rf2DescFile, conceptFile, bw, newInactive);
		addFileChangeReport(NEW_INACTIVE_CONCEPTS_FILE,newInactive.size(),"New inactive concepts");
	}

	private void generateConceptReport(Rf2DescriptionFile rf2DescFile, Rf2ConceptFile conceptFile, BufferedWriter bw, ArrayList<Long> newInactive) throws IOException {
		boolean bPrim=true;
		bw.append("[");
		for (Long long1 : newInactive) {
			if (!bPrim){
				bw.append(",");
			}else{
				bPrim=false;
			}
			String fsn = rf2DescFile.getFsn(long1);
			Pattern p = Pattern.compile("\\((.*?)\\)", Pattern.DOTALL);
			String semanticTag = "";
			if (fsn != null) {
				Matcher matcher = p.matcher(fsn);
				while (matcher.find()) {
					semanticTag = matcher.group(1);
				}
			}
			Concept concept=new Concept(long1.toString() ,rf2DescFile.getFsn(conceptFile.getDefinitionStatusId(long1)) , fsn , semanticTag);
			bw.append(gson.toJson(concept).toString());
			concept=null;
			bw.append(sep);
		}
		bw.append("]");
		bw.close();
	}

	private void generatingDefinedConceptsReport(Rf2DescriptionFile rf2DescFile, Rf2ConceptFile conceptFile) throws FileNotFoundException, UnsupportedEncodingException, IOException {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		fos = new FileOutputStream(new File(outputDirectory, DEFINED_CONCEPTS_REPORT));
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
		ArrayList<Long> newInactive = conceptFile.getDefinedConectps(startDate, endDate);
		generateConceptReport(rf2DescFile, conceptFile, bw, newInactive);

		addFileChangeReport(DEFINED_CONCEPTS_REPORT,newInactive.size(),"New fully defined concepts");
		
	}

	private ArrayList<Long> reactivatedConceptsReport(Rf2DescriptionFile rf2DescFile, Rf2ConceptFile conceptFile) throws FileNotFoundException, UnsupportedEncodingException, IOException {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		fos = new FileOutputStream(new File(outputDirectory, REACTIVATED_CONCEPTS_REPORT));
		logger.info("Generating " + REACTIVATED_CONCEPTS_REPORT);
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
		ArrayList<Long> reactConcepts = conceptFile.getReactivatedComponents(startDate, endDate);
		
		generateConceptReport(rf2DescFile, conceptFile, bw, reactConcepts);

		addFileChangeReport(REACTIVATED_CONCEPTS_REPORT,reactConcepts.size(),"Ractivated concepts");
		
		return reactConcepts;
	}

	private void generatingPrimitiveConceptsReport(Rf2DescriptionFile rf2DescFile, Rf2ConceptFile conceptFile) throws FileNotFoundException, UnsupportedEncodingException, IOException {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		fos = new FileOutputStream(new File(outputDirectory, PRIMITIVE_CONCEPTS_REPORT));
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
		ArrayList<Long> newInactive = conceptFile.getPrimitivatedConectps(startDate, endDate);
		generateConceptReport(rf2DescFile, conceptFile, bw, newInactive);

		addFileChangeReport(PRIMITIVE_CONCEPTS_REPORT,newInactive.size(),"New primitive concepts");
	}

	private void generateOldConceptsNewRelationships(Rf2DescriptionFile rf2DescFile, Rf2RelationshipFile relFile, ArrayList<Long> newcomponents) throws FileNotFoundException,
            UnsupportedEncodingException, IOException {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		fos = new FileOutputStream(new File(outputDirectory, OLD_CONCEPTS_NEW_RELATIONSHIPS_FILE));
		logger.info("Generating old_concepts_new_relationships.json");
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
		ArrayList<Long> existingRels = relFile.getExistingComponentIds(startDate);
		int count=0;
		boolean bPrim=true;
		bw.append("[");
		for (Long long1 : existingRels) {
			ArrayList<Rf2RelationshipRow> rf2RelRows = relFile.getAllRows(startDate, long1);
			for (Rf2RelationshipRow row : rf2RelRows) {
				if (!newcomponents.contains(Long.parseLong(row.getSourceId().toString()))) {
					if (!bPrim){
						bw.append(",");
					}else{
						bPrim=false;
					}
					Relationship rel=new Relationship(row.getId().toString() , String.valueOf(row.getActive()) , rf2DescFile.getFsn(row.getSourceId()),row.getSourceId().toString(), rf2DescFile.getFsn(row.getDestinationId()) ,
							 rf2DescFile.getFsn(row.getTypeId()) , rf2DescFile.getFsn(row.getCharacteristicTypeId()));
						bw.append(gson.toJson(rel).toString());
						rel=null;
						bw.append(sep);
						count++;
				}
			}
		}
		bw.append("]");
		bw.close();

		addFileChangeReport(OLD_CONCEPTS_NEW_RELATIONSHIPS_FILE,count,"New relationships in existing concepts");
		
	}
	
	private ArrayList<Long> generatingExistingConceptsNewDescriptions(Rf2DescriptionFile rf2DescFile, ArrayList<Long> repcomponents) throws FileNotFoundException, UnsupportedEncodingException, IOException {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		fos = new FileOutputStream(new File(outputDirectory, OLD_CONCEPTS_NEW_DESCRIPTIONS_FILE));
		logger.info("Generating " + OLD_CONCEPTS_NEW_DESCRIPTIONS_FILE);
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
		ArrayList<Long> newDescriptions = rf2DescFile.getNewComponentIds(startDate);
		int count=0;
		boolean bPrim=true;
		bw.append("[");
		for (Long long1 : newDescriptions) {
			ArrayList<Rf2DescriptionRow> rf2DescRows = rf2DescFile.getAllRows(startDate, long1);
			for (Rf2DescriptionRow rf2DescRow : rf2DescRows) {
				if (!repcomponents.contains(rf2DescRow.getConceptId()) 
						&& rf2DescRow.getTypeId()!=900000000000003001L) {
					repcomponents.add(rf2DescRow.getConceptId());
					if (!bPrim){
						bw.append(",");
					}else{
						bPrim=false;
					}
					Description desc=new Description(long1.toString() , rf2DescRow.getEffectiveTime() , String.valueOf(rf2DescRow.getActive()) , rf2DescRow.getConceptId().toString() ,
							rf2DescRow.getLanguageCode() , rf2DescFile.getFsn(rf2DescRow.getTypeId()) , rf2DescRow.getTerm() ,
						    rf2DescFile.getFsn(rf2DescRow.getCaseSignificanceId()));
					bw.append(gson.toJson(desc).toString());
					desc=null;
					bw.append(sep);
					count++;
				}
			}
		}
		bw.append("]");
		bw.close();

		addFileChangeReport(OLD_CONCEPTS_NEW_DESCRIPTIONS_FILE,count,"New descriptions (no FSN) in existing concepts");
		
		return repcomponents;
	}
	
	private ArrayList<Long> generatingChangedFSN(Rf2DescriptionFile rf2DescFile, ArrayList<Long> repcomponents) throws FileNotFoundException, UnsupportedEncodingException, IOException {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		fos = new FileOutputStream(new File(outputDirectory, CHANGED_FSN));
		logger.info("Generating " + CHANGED_FSN);
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
		ArrayList<Long> descriptions = rf2DescFile.getChangedComponentIds(startDate, endDate);
		int count=0;
		boolean bPrim=true;
		bw.append("[");
		for (Long long1 : descriptions) {
			ArrayList<Rf2DescriptionRow> rf2DescRows = rf2DescFile.getAllRows(startDate, long1);
			for (Rf2DescriptionRow rf2DescRow : rf2DescRows) {
				if (!repcomponents.contains(rf2DescRow.getConceptId()) 
						&& rf2DescRow.getActive()==1 
						&& rf2DescRow.getTypeId()==900000000000003001L ) {
					repcomponents.add(rf2DescRow.getConceptId());
					if (!bPrim){
						bw.append(",");
					}else{
						bPrim=false;
					}
					Description desc=new Description(long1.toString() , rf2DescRow.getEffectiveTime() , String.valueOf(rf2DescRow.getActive()) , rf2DescRow.getConceptId().toString() ,
							rf2DescRow.getLanguageCode() , rf2DescFile.getFsn(rf2DescRow.getTypeId()) , rf2DescRow.getTerm() ,
						    rf2DescFile.getFsn(rf2DescRow.getCaseSignificanceId()));
					bw.append(gson.toJson(desc).toString());
					desc=null;
					bw.append(sep);
					count++;
				}
			}
		}
		bw.append("]");
		bw.close();

		addFileChangeReport(CHANGED_FSN,count,"Changed FSNs");
		return repcomponents;
	}

	private ArrayList<Long> generateRetiredDescriptionsReport(Rf2DescriptionFile rf2DescFile, ArrayList<Long> repcomponents) throws FileNotFoundException, UnsupportedEncodingException, IOException {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		fos = new FileOutputStream(new File(outputDirectory, RETIRED_DESCRIPTIONS_FILE));
		logger.info("Generating " + RETIRED_DESCRIPTIONS_FILE);
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
		ArrayList<Long> retiredDescriptions = rf2DescFile.getRetiredComponents(startDate, endDate);
		ArrayList<Long> filteredRetDesc=new ArrayList<Long>();
		for(Long retiredDesc:retiredDescriptions){

			ArrayList<Rf2DescriptionRow> rf2DescRows = rf2DescFile.getAllRows(startDate, retiredDesc);
			for (Rf2DescriptionRow rf2DescRow : rf2DescRows) {
				if (!repcomponents.contains(rf2DescRow.getConceptId())
						&& rf2DescRow.getActive()==0 
						&& rf2DescRow.getTypeId()!=900000000000003001L ) {
					filteredRetDesc.add(retiredDesc);
					repcomponents.add(rf2DescRow.getConceptId());
				}
			}
		}
		int count=writeDescriptionsFile(rf2DescFile, bw, filteredRetDesc);

		addFileChangeReport(RETIRED_DESCRIPTIONS_FILE,count,"Inactivated descriptions (no FSN)");
		
		return repcomponents;
	}

	private ArrayList<Long> generateReactivatedDescriptionsReport(Rf2DescriptionFile rf2DescFile, ArrayList<Long> repcomponents) throws FileNotFoundException, UnsupportedEncodingException, IOException {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		fos = new FileOutputStream(new File(outputDirectory, REACTIVATED_DESCRIPTIONS_FILE));
		logger.info("Generating " + REACTIVATED_DESCRIPTIONS_FILE);
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
		ArrayList<Long> reactivedDescriptions = rf2DescFile.getReactivatedComponents(startDate, endDate);
		ArrayList<Long> filteredReactDesc=new ArrayList<Long>();
		for(Long retiredDesc:reactivedDescriptions){

			ArrayList<Rf2DescriptionRow> rf2DescRows = rf2DescFile.getAllRows(startDate, retiredDesc);
			for (Rf2DescriptionRow rf2DescRow : rf2DescRows) {
				if (!repcomponents.contains(rf2DescRow.getConceptId())
						&& rf2DescRow.getActive()==1 && rf2DescRow.getTypeId()!=900000000000003001L ) {
					repcomponents.add(rf2DescRow.getConceptId());
					filteredReactDesc.add(retiredDesc);
				}
			}
		}
		int count=writeDescriptionsFile(rf2DescFile,  bw, filteredReactDesc);
		
		addFileChangeReport(REACTIVATED_DESCRIPTIONS_FILE,count,"Reactivated descriptions");
		
		return repcomponents;
	}

	private int writeDescriptionsFile(Rf2DescriptionFile rf2DescFile,  BufferedWriter bw, ArrayList<Long> descriptions) throws IOException {
		int count=0;
		boolean bPrim=true;
		bw.append("[");
		for (Long long1 : descriptions) {
			Rf2DescriptionRow rf2DescRow = rf2DescFile.getLastActiveRow(startDate, long1);
//			if (!newcomponents.contains(rf2DescRow.getConceptId())) {
				if (!bPrim){
					bw.append(",");
				}else{
					bPrim=false;
				}
				Description desc=new Description(long1.toString() , rf2DescRow.getEffectiveTime() , String.valueOf(rf2DescRow.getActive()) , rf2DescRow.getConceptId().toString() ,
						rf2DescRow.getLanguageCode() , rf2DescFile.getFsn(rf2DescRow.getTypeId()) , rf2DescRow.getTerm() ,
					    rf2DescFile.getFsn(rf2DescRow.getCaseSignificanceId()));
				bw.append(gson.toJson(desc).toString());
				desc=null;
				bw.append(sep);
				count++;
//			}
		}
		bw.append("]");
		bw.close();
		return count;
	}

	private ArrayList<Long> generatingRetiredConceptReasons(Rf2DescriptionFile rf2DescFile, Rf2ConceptFile conceptFile, Rf2AttributeValueRefsetFile attrValue, Rf2AssociationRefsetFile associationFile)
			throws FileNotFoundException, UnsupportedEncodingException, IOException {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		ArrayList<Long> retiredConcepts = conceptFile.getRetiredComponents(startDate, endDate);
		fos = new FileOutputStream(new File(outputDirectory, RETIRED_CONCEPT_REASON_FILE));
		logger.info("Generating " + RETIRED_CONCEPT_REASON_FILE);
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
		int count=0;
		boolean bPrim=true;
		bw.append("[");
		for (Long long1 : retiredConcepts) {
			Rf2AttributeValueRefsetRow refsetRow = attrValue.getRowByReferencedComponentId(long1);
			Rf2AssociationRefsetRow associationRow = associationFile.getLastRowByReferencedComponentId(long1);

			String fsn = rf2DescFile.getFsn(long1);
			Pattern p = Pattern.compile("\\((.*?)\\)", Pattern.DOTALL);
			String semanticTag = "";
			if (fsn != null) {
				Matcher matcher = p.matcher(fsn);
				while (matcher.find()) {
					semanticTag = matcher.group(1);
				}
			}
			if (associationRow!=null) {
					String assValue = associationRow.getTargetComponent();
					if (!bPrim){
						bw.append(",");
					}else{
						bPrim=false;
					}
					if (refsetRow != null) {
						String value = refsetRow.getValueId();
						RetiredConcept concept=new RetiredConcept(long1.toString() ,rf2DescFile.getFsn(conceptFile.getDefinitionStatusId(long1)) , fsn , semanticTag,
								rf2DescFile.getFsn(Long.parseLong(value)),rf2DescFile.getFsn(Long.parseLong(associationRow.getRefsetId())),
								rf2DescFile.getFsn(Long.parseLong(assValue)) , String.valueOf(conceptFile.isNewComponent(long1, startDate)));
						bw.append(gson.toJson(concept).toString());
						
						concept=null;
					} else {
						RetiredConcept concept=new RetiredConcept(long1.toString() ,rf2DescFile.getFsn(conceptFile.getDefinitionStatusId(long1)) , fsn , semanticTag,
								"no reason",rf2DescFile.getFsn(Long.parseLong(associationRow.getRefsetId())),
								rf2DescFile.getFsn(Long.parseLong(assValue)) , String.valueOf(conceptFile.isNewComponent(long1, startDate)));
						bw.append(gson.toJson(concept).toString());
						concept=null;
					}
					bw.append(sep);
					count++;
			} else {
				if (!bPrim){
					bw.append(",");
				}else{
					bPrim=false;
				}
				if (refsetRow != null) {
					String value = refsetRow.getValueId();
					RetiredConcept concept=new RetiredConcept(long1.toString() ,rf2DescFile.getFsn(conceptFile.getDefinitionStatusId(long1)) , fsn , semanticTag,
							rf2DescFile.getFsn(Long.parseLong(value)),"no association","-" ,"-");
					bw.append(gson.toJson(concept).toString());
					concept=null;
				} else {
					RetiredConcept concept=new RetiredConcept(long1.toString() ,rf2DescFile.getFsn(conceptFile.getDefinitionStatusId(long1)) , fsn , semanticTag,
							"no reason","no association","-" ,"-");
					bw.append(gson.toJson(concept).toString());
					concept=null;
				}
				bw.append(sep);
				count++;
			}
		}
		bw.append("]");
		bw.close();
		attrValue.releasePreciousMemory();
		associationFile.releasePreciousMemory();

		addFileChangeReport(RETIRED_CONCEPT_REASON_FILE,count,"Inactivated concepts");
		
		return retiredConcepts;
		
	}

	private void addFileChangeReport(String fileName, int count, String reportName) {
		FileChangeReport fileChanges= new FileChangeReport();
		fileChanges.setFile(fileName);
		fileChanges.setCount(count);
		fileChanges.setName(reportName);
		List<FileChangeReport> lChanges= changeSummary.getReports();
		if (lChanges==null){
			lChanges=new ArrayList<FileChangeReport>();
		}
		lChanges.add(fileChanges);
		changeSummary.setReports(lChanges);
	}

	private void generateNewRelationshipsReport(Rf2DescriptionFile rf2DescFile, Rf2RelationshipFile relFile) throws FileNotFoundException, UnsupportedEncodingException, IOException {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		ArrayList<Long> newRels = relFile.getNewComponentIds(startDate);
		fos = new FileOutputStream(new File(outputDirectory, NEW_RELATIONSHIPS_FILE));
		logger.info("Generating new_relationships.json");
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
		boolean bPrim=true;
		bw.append("[");
		for (Long long1 : newRels) {
			if (!bPrim){
				bw.append(",");
			}else{
				bPrim=false;
			}
			Rf2RelationshipRow row = relFile.getById(long1, startDate);
			Relationship rel=new Relationship(row.getId().toString() , String.valueOf(row.getActive()) , rf2DescFile.getFsn(row.getSourceId()),row.getSourceId().toString(), rf2DescFile.getFsn(row.getDestinationId()) ,
				 rf2DescFile.getFsn(row.getTypeId()) , rf2DescFile.getFsn(row.getCharacteristicTypeId()));
			bw.append(gson.toJson(rel).toString());
			rel=null;
			bw.append(sep);
		}
		bw.append("]");
		bw.close();

		addFileChangeReport(NEW_RELATIONSHIPS_FILE,newRels.size(),"New relationships");
	}

	private ArrayList<Long> generateNewConceptsReport(Rf2DescriptionFile rf2DescFile, Rf2ConceptFile conceptFile) throws FileNotFoundException, UnsupportedEncodingException, IOException {
		logger.info("getting new conscpt ids");
		ArrayList<Long> newcomponents = conceptFile.getNewComponentIds(startDate);
		FileOutputStream fos = new FileOutputStream(new File(outputDirectory, NEW_CONCEPTS_FILE));
		logger.info("Generating " + NEW_CONCEPTS_FILE);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		boolean bPrim=true;
		bw.append("[");
		for (Long long1 : newcomponents) {
			if (!bPrim){
				bw.append(",");
			}else{
				bPrim=false;
			}
			String fsn = rf2DescFile.getFsn(long1);
			Pattern p = Pattern.compile("\\((.*?)\\)", Pattern.DOTALL);
			String semanticTag = "";
			if (fsn != null) {
				Matcher matcher = p.matcher(fsn);
				while (matcher.find()) {
					semanticTag = matcher.group(1);
				}
			}
			Concept concept=new Concept(long1.toString() ,rf2DescFile.getFsn(conceptFile.getDefinitionStatusId(long1)) , fsn , semanticTag);
			bw.append(gson.toJson(concept).toString());
			concept=null;
			bw.append(sep);
		}
		bw.append("]");
		bw.close();

		addFileChangeReport(NEW_CONCEPTS_FILE,newcomponents.size(),"New concepts");
		
		return newcomponents;
	}


	public enum ReleaseFileType {
		DESCRIPTION, CONCEPT, RELATIONSHIP, ATTRIBUTE_VALUE_REFSET, ASSOCIATION_REFSET, LANGUAGE_REFSET
	}

}
