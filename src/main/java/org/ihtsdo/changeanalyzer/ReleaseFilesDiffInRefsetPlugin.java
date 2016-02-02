package org.ihtsdo.changeanalyzer;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.changeanalyzer.utils.Type5UuidFactory;
import org.ihtsdo.changeanalyzer.data.Rf2DescriptionRow;
import org.ihtsdo.changeanalyzer.data.Rf2LanguageRefsetRow;
import org.ihtsdo.changeanalyzer.file.*;
import org.ihtsdo.changeanalyzer.FileFilterAndSorter;
import org.ihtsdo.changeanalyzer.utils.CommonUtils;
import org.ihtsdo.changeanalyzer.utils.FileHelper;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

/**
 * @goal report-differences-to-refset
 * @phase install
 */

public class ReleaseFilesDiffInRefsetPlugin extends AbstractMojo {

	public enum ReleaseFileTypes {
		DESCRIPTION, CONCEPT, RELATIONSHIP, ATTRIBUTE_VALUE_REFSET, ASSOCIATION_REFSET, LANGUAGE_REFSET, SIMPLE_REFSET, STATED_RELATIONSHIP, TEXT_DEFINITION, SIMPLE_MAP
	}
	
	private static final String REFSET_CONCEPTS_TMP_FOLDER = "refsetConcepts";

	private static final String SIMPLE_REFSET_TXT = "simpleRefset.txt";

	private static final String REFSET_LANGUAGE_TXT = "refsetLanguage.txt";

	private static final String REFSET_STATED_RELS_TXT = "refsetStatedRels.txt";

	private static final String REFSET_RELATIONSHIPS_TXT = "refsetRelationships.txt";

	private static final String REFSET_DESCRIPTIONS_TXT = "refsetDescriptions.txt";

	private static final String REFSET_CONCEPTS_TXT = "refsetConcepts.txt";

	private static final String REACTIVATED_CONCEPTS_REPORT = "reactivated_concepts.json";

	public static final String NEW_CONCEPTS_FILE = "new_concepts.json";

	public static final String NEW_RELATIONSHIPS_FILE = "new_relationships.json";

	public static final String RETIRED_CONCEPT_REASON_FILE = "inactivated_concept_reason.json";

	public static final String NEW_DESCRIPTIONS_FILE = "new_descriptions.json";

	public static final String OLD_CONCEPTS_NEW_DESCRIPTIONS_FILE = "old_concepts_new_descriptions.json";

	public static final String OLD_CONCEPTS_NEW_RELATIONSHIPS_FILE = "old_concepts_new_relationships.json";

	public static final String NEW_INACTIVE_CONCEPTS_FILE = "new_inactive_concepts.json";

	public static final String REL_GROUP_CHANGED_FILE = "rel_group_changed_relationships.json";

	private static final Logger logger = Logger.getLogger(ReleaseFilesDiffInRefsetPlugin.class);

	private static final String RETIRED_DESCRIPTIONS_FILE = "inactivated_descriptions.json";

	private static final String REACTIVATED_DESCRIPTIONS_FILE = "reactivated_descriptions.json";


	private static final String CHANGED_FSN="changed_fsn.json";

	private static final String SYN_ACCEPTABILITY_CHANGED = "acceptability_changed_on_synonym.json";

	private static final String TARGET_POINTER_TO_CHANGED_SOURCE_DESCRIPTION = "active_language_references_to_now_inactive_descriptions.json";


	/**
	 * Location of the directory of report files
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * Location of the directory of the full release folder.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File inputFullDirectory;

	/**
	 * Location of the directory of the snapshot release folder.
	 * 
	 * @parameter
	 * @required
	 */
	private File inputSnapshotDirectory;

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

	private boolean langTargetCtrl=false;

	private BufferedWriter bwsr;



	private void fileConsolidate() throws IOException {

		File tmpdir=new File(outputDirectory, REFSET_CONCEPTS_TMP_FOLDER);
		tmpdir.mkdirs();
		mergeFiles(ReleaseFileTypes.CONCEPT,REFSET_CONCEPTS_TXT, tmpdir);
		mergeFiles(ReleaseFileTypes.DESCRIPTION,REFSET_DESCRIPTIONS_TXT, tmpdir);
		mergeFiles(ReleaseFileTypes.RELATIONSHIP,REFSET_RELATIONSHIPS_TXT, tmpdir);
		mergeFiles(ReleaseFileTypes.STATED_RELATIONSHIP,REFSET_STATED_RELS_TXT, tmpdir);
		mergeFiles(ReleaseFileTypes.LANGUAGE_REFSET,REFSET_LANGUAGE_TXT, tmpdir);
		mergeFiles(ReleaseFileTypes.SIMPLE_REFSET,SIMPLE_REFSET_TXT, tmpdir);
		
		copyReleaseFileToOutput(ReleaseFileTypes.TEXT_DEFINITION);
		copyReleaseFileToOutput(ReleaseFileTypes.SIMPLE_MAP);
		copyReleaseFileToOutput(ReleaseFileTypes.ASSOCIATION_REFSET);
		copyReleaseFileToOutput(ReleaseFileTypes.ATTRIBUTE_VALUE_REFSET);
		
		FileHelper.emptyFolder(tmpdir);
		tmpdir.delete();
		
	}


	private void copyReleaseFileToOutput(ReleaseFileTypes fileType) throws IOException {

		String rFilePath = getFilePath(fileType, inputSnapshotDirectory);
		File releaseFile=new File(rFilePath);
		File outputFile= new File(outputDirectory,releaseFile.getName());
		FileHelper.copyTo(releaseFile, outputFile);
	}		
	

	private void mergeFiles(ReleaseFileTypes fileType, String txtfile, File tmpDir) {
		HashSet<File> hFile = new HashSet<File>();
		String rFilePath = getFilePath(fileType, inputSnapshotDirectory);
		File releaseFile=new File(rFilePath);
		hFile.add(new File(tmpDir,txtfile));
		hFile.add(releaseFile);
		CommonUtils.MergeFile(hFile, new File(outputDirectory,releaseFile.getName()));
	}

	private void createRefsetConcepts() throws IOException {
		File tmpdir=new File(outputDirectory, REFSET_CONCEPTS_TMP_FOLDER);
		tmpdir.mkdirs();
		File file=new File(tmpdir,REFSET_CONCEPTS_TXT);
		BufferedWriter bwc=getWriter(file);
		bwc.append("id	effectiveTime	active	moduleId	definitionStatusId");
		bwc.append("\r\n");
		bwc.append("1	" + endDate + "	1	900000000000207008	900000000000074008");
		bwc.append("\r\n");
		bwc.append("2	" + endDate + "	1	900000000000207008	900000000000074008");
		bwc.append("\r\n");
		bwc.append("3	" + endDate + "	1	900000000000207008	900000000000074008");
		bwc.append("\r\n");
		bwc.append("4	" + endDate + "	1	900000000000207008	900000000000074008");
		bwc.append("\r\n");
		bwc.append("5	" + endDate + "	1	900000000000207008	900000000000074008");
		bwc.append("\r\n");
		bwc.append("6	" + endDate + "	1	900000000000207008	900000000000074008");
		bwc.append("\r\n");
		bwc.append("7	" + endDate + "	1	900000000000207008	900000000000074008");
		bwc.append("\r\n");
		bwc.append("8	" + endDate + "	1	900000000000207008	900000000000074008");
		bwc.append("\r\n");
		bwc.append("9	" + endDate + "	1	900000000000207008	900000000000074008");
		bwc.append("\r\n");

		bwc.close();
		bwc=null;
		
		File dfile=new File(tmpdir,REFSET_DESCRIPTIONS_TXT);
		BufferedWriter bwd=getWriter(dfile);
		bwd.append("id	effectiveTime	active	moduleId	conceptId	languageCode	typeId	term	caseSignificanceId");
		bwd.append("\r\n");
		bwd.append("1	" + endDate + "	1	900000000000207008	1	en	900000000000013009	New concepts for " + endDate + " release simple reference set	900000000000020002");
		bwd.append("\r\n");
		bwd.append("2	" + endDate + "	1	900000000000207008	1	en	900000000000003001	New concepts for " + endDate + " release simple reference set (foundation metadata concept)	900000000000020002");
		bwd.append("\r\n");
		bwd.append("3	" + endDate + "	1	900000000000207008	2	en	900000000000013009	Inactivated concepts for " + endDate + " release simple reference set	900000000000020002");
		bwd.append("\r\n");
		bwd.append("4	" + endDate + "	1	900000000000207008	2	en	900000000000003001	Inactivated concepts for " + endDate + " release simple reference set (foundation metadata concept)	900000000000020002");
		bwd.append("\r\n");
		bwd.append("5	" + endDate + "	1	900000000000207008	3	en	900000000000013009	Reactivated concepts for " + endDate + " release simple reference set	900000000000020002");
		bwd.append("\r\n");
		bwd.append("6	" + endDate + "	1	900000000000207008	3	en	900000000000003001	Reactivated concepts for " + endDate + " release simple reference set (foundation metadata concept)	900000000000020002");
		bwd.append("\r\n");
		bwd.append("7	" + endDate + "	1	900000000000207008	4	en	900000000000013009	Changed FSN for " + endDate + " release simple reference set	900000000000020002");
		bwd.append("\r\n");
		bwd.append("8	" + endDate + "	1	900000000000207008	4	en	900000000000003001	Changed FSN for " + endDate + " release simple reference set (foundation metadata concept)	900000000000020002");
		bwd.append("\r\n");
		bwd.append("9	" + endDate + "	1	900000000000207008	5	en	900000000000013009	Active language references to now inactive descriptions for " + endDate + " release simple reference set	900000000000020002");
		bwd.append("\r\n");
		bwd.append("10	" + endDate + "	1	900000000000207008	5	en	900000000000003001	Active language references to now inactive descriptions for " + endDate + " release simple reference set (foundation metadata concept)	900000000000020002");
		bwd.append("\r\n");
		bwd.append("11	" + endDate + "	1	900000000000207008	6	en	900000000000013009	Inactivated descriptions for " + endDate + " release simple reference set	900000000000020002");
		bwd.append("\r\n");
		bwd.append("12	" + endDate + "	1	900000000000207008	6	en	900000000000003001	Inactivated descriptions for " + endDate + " release simple reference set (foundation metadata concept)	900000000000020002");
		bwd.append("\r\n");
		bwd.append("13	" + endDate + "	1	900000000000207008	7	en	900000000000013009	Old concepts new descriptions for " + endDate + " release simple reference set	900000000000020002");
		bwd.append("\r\n");
		bwd.append("14	" + endDate + "	1	900000000000207008	7	en	900000000000003001	Old concepts new descriptions for " + endDate + " release simple reference set (foundation metadata concept)	900000000000020002");
		bwd.append("\r\n");
		bwd.append("15	" + endDate + "	1	900000000000207008	8	en	900000000000013009	Reactivated descriptions for " + endDate + " release simple reference set	900000000000020002");
		bwd.append("\r\n");
		bwd.append("16	" + endDate + "	1	900000000000207008	8	en	900000000000003001	Reactivated descriptions for " + endDate + " release simple reference set (foundation metadata concept)	900000000000020002");
		bwd.append("\r\n");
		bwd.append("17	" + endDate + "	1	900000000000207008	9	en	900000000000013009	Acceptability changed on synonym for " + endDate + " release simple reference set	900000000000020002");
		bwd.append("\r\n");
		bwd.append("18	" + endDate + "	1	900000000000207008	9	en	900000000000003001	Acceptability changed on synonym for " + endDate + " release simple reference set (foundation metadata concept)	900000000000020002");
		bwd.append("\r\n");

		bwd.close();
		bwd=null;
		
		File rfile=new File(tmpdir,REFSET_RELATIONSHIPS_TXT);
		BufferedWriter bwr=getWriter(rfile);
		bwr.append("id	effectiveTime	active	moduleId	sourceId	destinationId	relationshipGroup	typeId	characteristicTypeId	modifierId");
		bwr.append("\r\n");
		bwr.append("1	" + endDate + "	1	900000000000012004	1	446609009	0	116680003	900000000000011006	900000000000451002");
		bwr.append("\r\n");
		bwr.append("2	" + endDate + "	1	900000000000012004	2	446609009	0	116680003	900000000000011006	900000000000451002");
		bwr.append("\r\n");
		bwr.append("3	" + endDate + "	1	900000000000012004	3	446609009	0	116680003	900000000000011006	900000000000451002");
		bwr.append("\r\n");
		bwr.append("4	" + endDate + "	1	900000000000012004	4	446609009	0	116680003	900000000000011006	900000000000451002");
		bwr.append("\r\n");
		bwr.append("5	" + endDate + "	1	900000000000012004	5	446609009	0	116680003	900000000000011006	900000000000451002");
		bwr.append("\r\n");
		bwr.append("6	" + endDate + "	1	900000000000012004	6	446609009	0	116680003	900000000000011006	900000000000451002");
		bwr.append("\r\n");
		bwr.append("7	" + endDate + "	1	900000000000012004	7	446609009	0	116680003	900000000000011006	900000000000451002");
		bwr.append("\r\n");
		bwr.append("8	" + endDate + "	1	900000000000012004	8	446609009	0	116680003	900000000000011006	900000000000451002");
		bwr.append("\r\n");
		bwr.append("9	" + endDate + "	1	900000000000012004	9	446609009	0	116680003	900000000000011006	900000000000451002");
		bwr.append("\r\n");

		bwr.close();
		bwr=null;
		
		File sfile=new File(tmpdir,REFSET_STATED_RELS_TXT);
		BufferedWriter bws=getWriter(sfile);
		bws.append("id	effectiveTime	active	moduleId	sourceId	destinationId	relationshipGroup	typeId	characteristicTypeId	modifierId");
		bws.append("\r\n");
		bws.append("11	" + endDate + "	1	900000000000012004	1	446609009	0	116680003	900000000000010007	900000000000451002");
		bws.append("\r\n");
		bws.append("12	" + endDate + "	1	900000000000012004	2	446609009	0	116680003	900000000000010007	900000000000451002");
		bws.append("\r\n");
		bws.append("13	" + endDate + "	1	900000000000012004	3	446609009	0	116680003	900000000000010007	900000000000451002");
		bws.append("\r\n");
		bws.append("14	" + endDate + "	1	900000000000012004	4	446609009	0	116680003	900000000000010007	900000000000451002");
		bws.append("\r\n");
		bws.append("15	" + endDate + "	1	900000000000012004	5	446609009	0	116680003	900000000000010007	900000000000451002");
		bws.append("\r\n");
		bws.append("16	" + endDate + "	1	900000000000012004	6	446609009	0	116680003	900000000000010007	900000000000451002");
		bws.append("\r\n");
		bws.append("17	" + endDate + "	1	900000000000012004	7	446609009	0	116680003	900000000000010007	900000000000451002");
		bws.append("\r\n");
		bws.append("18	" + endDate + "	1	900000000000012004	8	446609009	0	116680003	900000000000010007	900000000000451002");
		bws.append("\r\n");
		bws.append("19	" + endDate + "	1	900000000000012004	9	446609009	0	116680003	900000000000010007	900000000000451002");
		bws.append("\r\n");

		bws.close();
		bws=null;
		
		File lfile=new File(tmpdir,REFSET_LANGUAGE_TXT);
		BufferedWriter bwl=getWriter(lfile);
		bwl.append("id	effectiveTime	active	moduleId	refsetId	referencedComponentId	acceptabilityId");
		bwl.append("\r\n");
		bwl.append("18685dc7-30e4-573d-891c-dc8f9f3e6861	" + endDate + "	1	900000000000207008	900000000000509007	1	900000000000548007");
		bwl.append("\r\n");
		bwl.append("d2db27d2-2656-5c69-b1db-0587998439a2	" + endDate + "	1	900000000000207008	900000000000508004	1	900000000000548007");
		bwl.append("\r\n");
		bwl.append("768d3d74-d155-54c2-b918-87e668d27983	" + endDate + "	1	900000000000207008	900000000000509007	2	900000000000548007");
		bwl.append("\r\n");
		bwl.append("c46a7d65-f5ed-551d-a378-5bd6c9a77754	" + endDate + "	1	900000000000207008	900000000000508004	2	900000000000548007");
		bwl.append("\r\n");
		bwl.append("f8a120d7-6690-5a61-aa43-0821e965f4a5	" + endDate + "	1	900000000000207008	900000000000509007	3	900000000000548007");
		bwl.append("\r\n");
		bwl.append("d26ff35c-0be3-560b-8ba4-631d43f7c826	" + endDate + "	1	900000000000207008	900000000000508004	3	900000000000548007");
		bwl.append("\r\n");
		bwl.append("7265c5f7-38a7-58e0-826c-e9e555bc0dd7	" + endDate + "	1	900000000000207008	900000000000509007	4	900000000000548007");
		bwl.append("\r\n");
		bwl.append("31ac1b26-1f0e-5b4e-bd52-9d2a2385a848	" + endDate + "	1	900000000000207008	900000000000508004	4	900000000000548007");
		bwl.append("\r\n");
		bwl.append("397a28b4-b05a-5589-b55a-ad792d1d28a9	" + endDate + "	1	900000000000207008	900000000000509007	5	900000000000548007");
		bwl.append("\r\n");
		bwl.append("22b79af2-d628-5cfa-ae89-851787f22490	" + endDate + "	1	900000000000207008	900000000000508004	5	900000000000548007");
		bwl.append("\r\n");
		bwl.append("480b53c4-ed84-5236-9023-a15c9801fc1a	" + endDate + "	1	900000000000207008	900000000000509007	6	900000000000548007");
		bwl.append("\r\n");
		bwl.append("6d322dbe-e001-56ab-b369-2bf9fe19d2db	" + endDate + "	1	900000000000207008	900000000000508004	6	900000000000548007");
		bwl.append("\r\n");
		bwl.append("79e5d418-da92-572d-9eb3-22c2ccf567ec	" + endDate + "	1	900000000000207008	900000000000509007	7	900000000000548007");
		bwl.append("\r\n");
		bwl.append("44a63353-82b1-57d3-a41d-aad4d069bb8d	" + endDate + "	1	900000000000207008	900000000000508004	7	900000000000548007");
		bwl.append("\r\n");
		bwl.append("bad0ffda-804c-5c90-a20d-1908090257de	" + endDate + "	1	900000000000207008	900000000000509007	8	900000000000548007");
		bwl.append("\r\n");
		bwl.append("5f3da74c-195f-5ead-8a25-ba9c81da710f	" + endDate + "	1	900000000000207008	900000000000508004	8	900000000000548007");
		bwl.append("\r\n");
		bwl.append("6238a3ec-e1bf-5268-94a5-4ba74dd35211	" + endDate + "	1	900000000000207008	900000000000509007	9	900000000000548007");
		bwl.append("\r\n");
		bwl.append("f612bba4-6c81-53e9-a784-71b1098a6a22	" + endDate + "	1	900000000000207008	900000000000508004	9	900000000000548007");
		bwl.append("\r\n");
		bwl.append("f6a801b3-1707-520e-8ec5-8ba6a9b4d533	" + endDate + "	1	900000000000207008	900000000000509007	10	900000000000548007");
		bwl.append("\r\n");
		bwl.append("7c17b526-79dd-50f6-bdf2-c0639d558044	" + endDate + "	1	900000000000207008	900000000000508004	10	900000000000548007");
		bwl.append("\r\n");
		bwl.append("8c01ac48-02ae-5994-83de-ba52e5bd2255	" + endDate + "	1	900000000000207008	900000000000509007	11	900000000000548007");
		bwl.append("\r\n");
		bwl.append("78b9018d-e6cd-52ee-a3d1-db5362bec266	" + endDate + "	1	900000000000207008	900000000000508004	11	900000000000548007");
		bwl.append("\r\n");
		bwl.append("0d7056f1-8871-53ca-8348-9195a407aa77	" + endDate + "	1	900000000000207008	900000000000509007	12	900000000000548007");
		bwl.append("\r\n");
		bwl.append("89766327-6ff9-5e73-b03f-44761850ea88	" + endDate + "	1	900000000000207008	900000000000508004	12	900000000000548007");
		bwl.append("\r\n");
		bwl.append("165c957d-5216-5931-bfd4-983efa413e99	" + endDate + "	1	900000000000207008	900000000000509007	13	900000000000548007");
		bwl.append("\r\n");
		bwl.append("fe3fe207-f366-5110-8a50-da559205bd00	" + endDate + "	1	900000000000207008	900000000000508004	13	900000000000548007");
		bwl.append("\r\n");
		bwl.append("8c0636fd-72d5-524b-a952-8652d706f333	" + endDate + "	1	900000000000207008	900000000000509007	14	900000000000548007");
		bwl.append("\r\n");
		bwl.append("69871d91-b537-5de4-b980-876920769444	" + endDate + "	1	900000000000207008	900000000000508004	14	900000000000548007");
		bwl.append("\r\n");
		bwl.append("62facd0a-08da-5ed4-b69e-30694e8022aa	" + endDate + "	1	900000000000207008	900000000000509007	15	900000000000548007");
		bwl.append("\r\n");
		bwl.append("600caf82-decd-5831-b10f-ff65b62ff2bb	" + endDate + "	1	900000000000207008	900000000000508004	15	900000000000548007");
		bwl.append("\r\n");
		bwl.append("55ddcbe1-f861-5771-93f6-80e2830ec7cc	" + endDate + "	1	900000000000207008	900000000000509007	16	900000000000548007");
		bwl.append("\r\n");
		bwl.append("d8452236-4321-5ed4-8694-eeaa7e5667dd	" + endDate + "	1	900000000000207008	900000000000508004	16	900000000000548007");
		bwl.append("\r\n");
		bwl.append("559a729c-188d-593c-adcd-1765c3252dee	" + endDate + "	1	900000000000207008	900000000000509007	17	900000000000548007");
		bwl.append("\r\n");
		bwl.append("9d88128a-27ae-57fc-bdda-b9dea6e583ff	" + endDate + "	1	900000000000207008	900000000000508004	17	900000000000548007");
		bwl.append("\r\n");
		bwl.append("a20c5ccb-c406-5921-b9e9-39ec899b7111	" + endDate + "	1	900000000000207008	900000000000509007	18	900000000000548007");
		bwl.append("\r\n");
		bwl.append("afffd990-6011-5766-80ed-b934e0dd5222	" + endDate + "	1	900000000000207008	900000000000508004	18	900000000000548007");
		bwl.append("\r\n");
		
		bwl.close();
		bwl=null;
		

		File srfile=new File(tmpdir,SIMPLE_REFSET_TXT);
		bwsr=getWriter(srfile);

	}
	private void writeToRefsetFile(String componentId,String refsetId) throws IOException, NoSuchAlgorithmException {
		UUID id=Type5UuidFactory.get("900000000000207008" + refsetId + componentId );
		bwsr.append(id.toString());
		bwsr.append("\t");
		bwsr.append(releaseDate);
		bwsr.append("\t");
		bwsr.append("1");
		bwsr.append("\t");
		bwsr.append("900000000000207008");
		bwsr.append("\t");
		bwsr.append(refsetId);
		bwsr.append("\t");
		bwsr.append(componentId);
		bwsr.append("\r\n");		
		
	}
	private BufferedWriter getWriter(File file) throws FileNotFoundException, UnsupportedEncodingException {

		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		fos = new FileOutputStream(file);
		logger.info("Generating " + file);
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);

		return bw;
	}
	private ArrayList<Long> generateTargetDescriptionPointerToSource(
			Rf2DescriptionFile rf2DescFile,
			Rf2LanguageRefsetFile targetLangFile,
			ArrayList<Long> repcomponents) throws Exception {
//		FileOutputStream fos;
//		OutputStreamWriter osw;
//		BufferedWriter bw;
//		fos = new FileOutputStream(new File(outputDirectory, TARGET_POINTER_TO_CHANGED_SOURCE_DESCRIPTION));
		logger.info("Generating " + TARGET_POINTER_TO_CHANGED_SOURCE_DESCRIPTION);
//		osw = new OutputStreamWriter(fos, "UTF-8");
//		bw = new BufferedWriter(osw);
		ArrayList<Long> changedDesc = rf2DescFile.getChangedComponentIds(startDate, endDate);
//		int count=0;
//		boolean bPrim=true;
//		bw.append("[");
		for (Long long1 : changedDesc) {
			Rf2LanguageRefsetRow langRow = targetLangFile.getLastActiveRow(startDate, long1.toString());
			if (langRow!=null && langRow.getActive()==1){
				Rf2DescriptionRow rf2DescRow = rf2DescFile.getLastActiveRow(startDate,long1);
				if (!repcomponents.contains(rf2DescRow.getConceptId()) &&
						rf2DescRow.getActive()==0) {
					repcomponents.add(rf2DescRow.getConceptId());

					writeToRefsetFile(rf2DescRow.getConceptId().toString(), "5");
//					if (!bPrim){
//						bw.append(",");
//					}else{
//						bPrim=false;
//					}

//					Description desc=new Description(long1.toString(), rf2DescRow.getEffectiveTime() , String.valueOf(rf2DescRow.getActive()) , rf2DescRow.getConceptId().toString() ,
//							rf2DescRow.getLanguageCode() , rf2DescFile.getFsn(rf2DescRow.getTypeId()) , rf2DescRow.getTerm() ,
//							rf2DescFile.getFsn(rf2DescRow.getCaseSignificanceId()));
//					bw.append(gson.toJson(desc).toString());
//					desc=null;
//					bw.append(sep);
//					count++;
				}
			}
		}
//		bw.append("]");
//		bw.close();

//		addFileChangeReport(TARGET_POINTER_TO_CHANGED_SOURCE_DESCRIPTION,count,"Active language references to now inactive descriptions.");

		return repcomponents;		
	}

	private ArrayList<Long> generateDescriptionAcceptabilityChanges(
			Rf2LanguageRefsetFile sourceLangFile, Rf2DescriptionFile rf2DescFile,ArrayList<Long> repcomponents) throws Exception {
//		FileOutputStream fos;
//		OutputStreamWriter osw;
//		BufferedWriter bw;
//		fos = new FileOutputStream(new File(outputDirectory, SYN_ACCEPTABILITY_CHANGED));
		logger.info("Generating " + SYN_ACCEPTABILITY_CHANGED);
//		osw = new OutputStreamWriter(fos, "UTF-8");
//		bw = new BufferedWriter(osw);
		ArrayList<String> changedLang = sourceLangFile.getAcceptabilityIdChanged(startDate, endDate);
//		int count=0;
//		boolean bPrim=true;
//		bw.append("[");
		for (String string : changedLang) {
			Rf2DescriptionRow rf2DescRow = rf2DescFile.getLastActiveRow(startDate, Long.parseLong(string));
			if (!repcomponents.contains(rf2DescRow.getConceptId()) 
					&& rf2DescRow.getTypeId()!=900000000000003001L) {
				repcomponents.add(rf2DescRow.getConceptId());
				writeToRefsetFile(rf2DescRow.getConceptId().toString(), "9");
//				if (!bPrim){
//					bw.append(",");
//				}else{
//					bPrim=false;
//				}
//				Description desc=new Description(string, rf2DescRow.getEffectiveTime() , String.valueOf(rf2DescRow.getActive()) , rf2DescRow.getConceptId().toString() ,
//						rf2DescRow.getLanguageCode() , rf2DescFile.getFsn(rf2DescRow.getTypeId()) , rf2DescRow.getTerm() ,
//						rf2DescFile.getFsn(rf2DescRow.getCaseSignificanceId()));
//				bw.append(gson.toJson(desc).toString());
//				desc=null;
//				bw.append(sep);
//				count++;
			}
		}
//		bw.append("]");
//		bw.close();
//
//		addFileChangeReport(SYN_ACCEPTABILITY_CHANGED,count,"Acceptability changed in descriptions (no FSN)");

		return repcomponents;
	}

	private String getFilePath(ReleaseFileTypes descriptions,File inputDir) {
		String result = "";
		switch (descriptions) {
		case DESCRIPTION:
			result = getFilePathRecursive(inputDir, "description");
			break;
		case CONCEPT:
			result = getFilePathRecursive(inputDir, "concept");
			break;
		case RELATIONSHIP:
			result = getFilePathRecursive(inputDir, "_relationship");
			break;
		case ASSOCIATION_REFSET:
			result = getFilePathRecursive(inputDir, "associationreference");
			break;
		case ATTRIBUTE_VALUE_REFSET:
			result = getFilePathRecursive(inputDir, "attributevalue");
			break;
		case LANGUAGE_REFSET:
			result = getFilePathRecursive(inputDir, "refset_language");
			break;
		case SIMPLE_REFSET:
			result = getFilePathRecursive(inputDir, "_refset_simple");
			break;
		case STATED_RELATIONSHIP:
			result = getFilePathRecursive(inputDir, "_statedrelationship");
			break;
		case TEXT_DEFINITION:
			result = getFilePathRecursive(inputDir, "_textdefinition");
			break;
		case SIMPLE_MAP:
			result = getFilePathRecursive(inputDir, "_srefset_simplemap");
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

	
	
	private ArrayList<Long> reactivatedConceptsReport(Rf2DescriptionFile rf2DescFile, Rf2ConceptFile conceptFile) throws FileNotFoundException, UnsupportedEncodingException, IOException, NoSuchAlgorithmException {
//		FileOutputStream fos;
//		OutputStreamWriter osw;
//		BufferedWriter bw;
//		fos = new FileOutputStream(new File(outputDirectory, REACTIVATED_CONCEPTS_REPORT));
		logger.info("Generating " + REACTIVATED_CONCEPTS_REPORT);
//		osw = new OutputStreamWriter(fos, "UTF-8");
//		bw = new BufferedWriter(osw);
		ArrayList<Long> reactConcepts = conceptFile.getReactivatedComponents(startDate, endDate);

//		generateConceptReport(rf2DescFile, conceptFile, bw, reactConcepts);
		for (Long long1 : reactConcepts) {
			writeToRefsetFile(long1.toString(), "3");
		}
//		addFileChangeReport(REACTIVATED_CONCEPTS_REPORT,reactConcepts.size(),"Ractivated concepts");

		return reactConcepts;
	}

	private ArrayList<Long> generatingExistingConceptsNewDescriptions(Rf2DescriptionFile rf2DescFile, ArrayList<Long> repcomponents) throws FileNotFoundException, UnsupportedEncodingException, IOException, NoSuchAlgorithmException {
//		FileOutputStream fos;
//		OutputStreamWriter osw;
//		BufferedWriter bw;
//		fos = new FileOutputStream(new File(outputDirectory, OLD_CONCEPTS_NEW_DESCRIPTIONS_FILE));
		logger.info("Generating " + OLD_CONCEPTS_NEW_DESCRIPTIONS_FILE);
//		osw = new OutputStreamWriter(fos, "UTF-8");
//		bw = new BufferedWriter(osw);
		ArrayList<Long> newDescriptions = rf2DescFile.getNewComponentIds(startDate);
//		int count=0;
//		boolean bPrim=true;
//		bw.append("[");
		for (Long long1 : newDescriptions) {
			ArrayList<Rf2DescriptionRow> rf2DescRows = rf2DescFile.getAllRows(startDate, long1);
			for (Rf2DescriptionRow rf2DescRow : rf2DescRows) {
				if (!repcomponents.contains(rf2DescRow.getConceptId()) 
						&& rf2DescRow.getTypeId()!=900000000000003001L) {
					repcomponents.add(rf2DescRow.getConceptId());
					writeToRefsetFile(rf2DescRow.getConceptId().toString(), "7");
//					if (!bPrim){
//						bw.append(",");
//					}else{
//						bPrim=false;
//					}
//					Description desc=new Description(long1.toString() , rf2DescRow.getEffectiveTime() , String.valueOf(rf2DescRow.getActive()) , rf2DescRow.getConceptId().toString() ,
//							rf2DescRow.getLanguageCode() , rf2DescFile.getFsn(rf2DescRow.getTypeId()) , rf2DescRow.getTerm() ,
//							rf2DescFile.getFsn(rf2DescRow.getCaseSignificanceId()));
//					bw.append(gson.toJson(desc).toString());
//					desc=null;
//					bw.append(sep);
//					count++;
				}
			}
		}
//		bw.append("]");
//		bw.close();
//
//		addFileChangeReport(OLD_CONCEPTS_NEW_DESCRIPTIONS_FILE,count,"New descriptions (no FSN) in existing concepts");

		return repcomponents;
	}

	private ArrayList<Long> generatingChangedFSN(Rf2DescriptionFile rf2DescFile, ArrayList<Long> repcomponents) throws FileNotFoundException, UnsupportedEncodingException, IOException, NoSuchAlgorithmException {
//		FileOutputStream fos;
//		OutputStreamWriter osw;
//		BufferedWriter bw;
//		fos = new FileOutputStream(new File(outputDirectory, CHANGED_FSN));
		logger.info("Generating " + CHANGED_FSN);
//		osw = new OutputStreamWriter(fos, "UTF-8");
//		bw = new BufferedWriter(osw);
		ArrayList<Long> descriptions = rf2DescFile.getChangedComponentIds(startDate, endDate);
//		int count=0;
//		boolean bPrim=true;
//		bw.append("[");
		for (Long long1 : descriptions) {
			ArrayList<Rf2DescriptionRow> rf2DescRows = rf2DescFile.getAllRows(startDate, long1);
			for (Rf2DescriptionRow rf2DescRow : rf2DescRows) {
				if (!repcomponents.contains(rf2DescRow.getConceptId()) 
						&& rf2DescRow.getActive()==1 
						&& rf2DescRow.getTypeId()==900000000000003001L ) {
					repcomponents.add(rf2DescRow.getConceptId());
					writeToRefsetFile(rf2DescRow.getConceptId().toString(), "4");
//					if (!bPrim){
//						bw.append(",");
//					}else{
//						bPrim=false;
//					}
//					Description desc=new Description(long1.toString() , rf2DescRow.getEffectiveTime() , String.valueOf(rf2DescRow.getActive()) , rf2DescRow.getConceptId().toString() ,
//							rf2DescRow.getLanguageCode() , rf2DescFile.getFsn(rf2DescRow.getTypeId()) , rf2DescRow.getTerm() ,
//							rf2DescFile.getFsn(rf2DescRow.getCaseSignificanceId()));
//					bw.append(gson.toJson(desc).toString());
//					desc=null;
//					bw.append(sep);
//					count++;
				}
			}
		}
//		bw.append("]");
//		bw.close();
//
//		addFileChangeReport(CHANGED_FSN,count,"Changed FSNs");
		return repcomponents;
	}

	private ArrayList<Long> generateRetiredDescriptionsReport(Rf2DescriptionFile rf2DescFile, ArrayList<Long> repcomponents) throws FileNotFoundException, UnsupportedEncodingException, IOException, NoSuchAlgorithmException {
//		FileOutputStream fos;
//		OutputStreamWriter osw;
//		BufferedWriter bw;
//		fos = new FileOutputStream(new File(outputDirectory, RETIRED_DESCRIPTIONS_FILE));
		logger.info("Generating " + RETIRED_DESCRIPTIONS_FILE);
//		osw = new OutputStreamWriter(fos, "UTF-8");
//		bw = new BufferedWriter(osw);
		ArrayList<Long> retiredDescriptions = rf2DescFile.getRetiredComponents(startDate, endDate);
//		ArrayList<Long> filteredRetDesc=new ArrayList<Long>();
		for(Long retiredDesc:retiredDescriptions){

			ArrayList<Rf2DescriptionRow> rf2DescRows = rf2DescFile.getAllRows(startDate, retiredDesc);
			for (Rf2DescriptionRow rf2DescRow : rf2DescRows) {
				if (!repcomponents.contains(rf2DescRow.getConceptId())
						&& rf2DescRow.getActive()==0 
						&& rf2DescRow.getTypeId()!=900000000000003001L ) {
//					filteredRetDesc.add(retiredDesc);
					writeToRefsetFile(rf2DescRow.getConceptId().toString(), "6");
					repcomponents.add(rf2DescRow.getConceptId());
				}
			}
		}
//		int count=writeDescriptionsFile(rf2DescFile, bw, filteredRetDesc);
//
//		addFileChangeReport(RETIRED_DESCRIPTIONS_FILE,count,"Inactivated descriptions (no FSN)");

		return repcomponents;
	}

	private ArrayList<Long> generateReactivatedDescriptionsReport(Rf2DescriptionFile rf2DescFile, ArrayList<Long> repcomponents) throws FileNotFoundException, UnsupportedEncodingException, IOException, NoSuchAlgorithmException {
//		FileOutputStream fos;
//		OutputStreamWriter osw;
//		BufferedWriter bw;
//		fos = new FileOutputStream(new File(outputDirectory, REACTIVATED_DESCRIPTIONS_FILE));
		logger.info("Generating " + REACTIVATED_DESCRIPTIONS_FILE);
//		osw = new OutputStreamWriter(fos, "UTF-8");
//		bw = new BufferedWriter(osw);
		ArrayList<Long> reactivedDescriptions = rf2DescFile.getReactivatedComponents(startDate, endDate);
//		ArrayList<Long> filteredReactDesc=new ArrayList<Long>();
		for(Long retiredDesc:reactivedDescriptions){

			ArrayList<Rf2DescriptionRow> rf2DescRows = rf2DescFile.getAllRows(startDate, retiredDesc);
			for (Rf2DescriptionRow rf2DescRow : rf2DescRows) {
				if (!repcomponents.contains(rf2DescRow.getConceptId())
						&& rf2DescRow.getActive()==1 && rf2DescRow.getTypeId()!=900000000000003001L ) {
					repcomponents.add(rf2DescRow.getConceptId());
					writeToRefsetFile(rf2DescRow.getConceptId().toString(), "8");
//					filteredReactDesc.add(retiredDesc);
				}
			}
		}
//		int count=writeDescriptionsFile(rf2DescFile,  bw, filteredReactDesc);
//
//		addFileChangeReport(REACTIVATED_DESCRIPTIONS_FILE,count,"Reactivated descriptions");

		return repcomponents;
	}


	private ArrayList<Long> generatingRetiredConceptReasons(Rf2DescriptionFile rf2DescFile, Rf2ConceptFile conceptFile, Rf2AttributeValueRefsetFile attrValue, Rf2AssociationRefsetFile associationFile)
			throws FileNotFoundException, UnsupportedEncodingException, IOException, NoSuchAlgorithmException {
//		FileOutputStream fos;
//		OutputStreamWriter osw;
//		BufferedWriter bw;
		ArrayList<Long> retiredConcepts = conceptFile.getRetiredComponents(startDate, endDate);
//		fos = new FileOutputStream(new File(outputDirectory, RETIRED_CONCEPT_REASON_FILE));
		logger.info("Generating " + RETIRED_CONCEPT_REASON_FILE);
//		osw = new OutputStreamWriter(fos, "UTF-8");
//		bw = new BufferedWriter(osw);
//		int count=0;
//		boolean bPrim=true;
//		bw.append("[");
		for (Long long1 : retiredConcepts) {
//			Rf2AttributeValueRefsetRow refsetRow = attrValue.getRowByReferencedComponentId(long1);
//			Rf2AssociationRefsetRow associationRow = associationFile.getLastRowByReferencedComponentId(long1);
//
//			String fsn = rf2DescFile.getFsn(long1);
//			Pattern p = Pattern.compile("\\((.*?)\\)", Pattern.DOTALL);
//			String semanticTag = "";
//			if (fsn != null) {
//				Matcher matcher = p.matcher(fsn);
//				while (matcher.find()) {
//					semanticTag = matcher.group(1);
//				}
//			}
//			if (associationRow!=null) {
//				String assValue = associationRow.getTargetComponent();
//				if (!bPrim){
//					bw.append(",");
//				}else{
//					bPrim=false;
//				}
//				if (refsetRow != null) {
//					String value = refsetRow.getValueId();
//					RetiredConcept concept=new RetiredConcept(long1.toString() ,rf2DescFile.getFsn(conceptFile.getDefinitionStatusId(long1)) , fsn , semanticTag,
//							rf2DescFile.getFsn(Long.parseLong(value)),rf2DescFile.getFsn(Long.parseLong(associationRow.getRefsetId())),
//							rf2DescFile.getFsn(Long.parseLong(assValue)) ,String.valueOf(conceptFile.isNewComponent(long1, startDate)));
//					bw.append(gson.toJson(concept).toString());
//					concept=null;
//				} else {
//					RetiredConcept concept=new RetiredConcept(long1.toString() ,rf2DescFile.getFsn(conceptFile.getDefinitionStatusId(long1)) , fsn , semanticTag,
//							"no reason",rf2DescFile.getFsn(Long.parseLong(associationRow.getRefsetId())),
//							rf2DescFile.getFsn(Long.parseLong(assValue)) ,String.valueOf(conceptFile.isNewComponent(long1, startDate)));
//					bw.append(gson.toJson(concept).toString());
//					concept=null;
//				}
//				bw.append(sep);
//				count++;
//			} else {
//				if (!bPrim){
//					bw.append(",");
//				}else{
//					bPrim=false;
//				}
//				if (refsetRow != null) {
//					String value = refsetRow.getValueId();
//					RetiredConcept concept=new RetiredConcept(long1.toString() ,rf2DescFile.getFsn(conceptFile.getDefinitionStatusId(long1)) , fsn , semanticTag,
//							rf2DescFile.getFsn(Long.parseLong(value)),"no association","-" ,"-");
//					bw.append(gson.toJson(concept).toString());
//					concept=null;
//				} else {
//					RetiredConcept concept=new RetiredConcept(long1.toString() ,rf2DescFile.getFsn(conceptFile.getDefinitionStatusId(long1)) , fsn , semanticTag,
//							"no reason","no association","-" ,"-");
//					bw.append(gson.toJson(concept).toString());
//					concept=null;
//				}
//				bw.append(sep);
//			}
			writeToRefsetFile(long1.toString(), "2");
//			count++;
		}
//		bw.append("]");
//		bw.close();
//		attrValue.releasePreciousMemory();
//		associationFile.releasePreciousMemory();

//		addFileChangeReport(RETIRED_CONCEPT_REASON_FILE,count,"Inactivated concepts");

		return retiredConcepts;

	}



	private ArrayList<Long> generateNewConceptsReport(Rf2DescriptionFile rf2DescFile, Rf2ConceptFile conceptFile) throws FileNotFoundException, UnsupportedEncodingException, IOException, NoSuchAlgorithmException {
		logger.info("getting new conscpt ids");
		ArrayList<Long> newcomponents = conceptFile.getNewComponentIds(startDate);
//		FileOutputStream fos = new FileOutputStream(new File(outputDirectory, NEW_CONCEPTS_FILE));
		logger.info("Generating " + NEW_CONCEPTS_FILE);
//		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
//		BufferedWriter bw = new BufferedWriter(osw);
//		boolean bPrim=true;
//		bw.append("[");
		for (Long long1 : newcomponents) {
//			if (!bPrim){
//				bw.append(",");
//			}else{
//				bPrim=false;
//			}
//			String fsn = rf2DescFile.getFsn(long1);
//			Pattern p = Pattern.compile("\\((.*?)\\)", Pattern.DOTALL);
//			String semanticTag = "";
//			if (fsn != null) {
//				Matcher matcher = p.matcher(fsn);
//				while (matcher.find()) {
//					semanticTag = matcher.group(1);
//				}
//			}
//			Concept concept=new Concept(long1.toString() ,rf2DescFile.getFsn(conceptFile.getDefinitionStatusId(long1)) , fsn , semanticTag);
//			bw.append(gson.toJson(concept).toString());
			writeToRefsetFile(long1.toString(), "1");
//			concept=null;
//			bw.append(sep);
		}
//		bw.append("]");
//		bw.close();

//		addFileChangeReport(NEW_CONCEPTS_FILE,newcomponents.size(),"New concepts");

		return newcomponents;
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			if (!outputDirectory.exists()) {
				outputDirectory.mkdirs();
			}
			createRefsetConcepts();
			//			changeSummary=new ChangeSummary();
			logger.info("Loading descriptinos");
			Rf2DescriptionFile rf2DescFile = new Rf2DescriptionFile(getFilePath(ReleaseFileTypes.DESCRIPTION,inputFullDirectory), startDate);
			logger.info("Loading concepts");
			Rf2ConceptFile conceptFile = new Rf2ConceptFile(getFilePath(ReleaseFileTypes.CONCEPT,inputFullDirectory), startDate);
			logger.info("Loading attribute value refset");
			Rf2AttributeValueRefsetFile attrValue = new Rf2AttributeValueRefsetFile(getFilePath(ReleaseFileTypes.ATTRIBUTE_VALUE_REFSET,inputFullDirectory));
			logger.info("Loading association value refset");
			Rf2AssociationRefsetFile associationFile = new Rf2AssociationRefsetFile(getFilePath(ReleaseFileTypes.ASSOCIATION_REFSET,inputFullDirectory));
			logger.info("Loading source US language refset");
			String langPath=getFilePath(ReleaseFileTypes.LANGUAGE_REFSET,inputFullDirectory);
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
//			saveSummary();
			bwsr.close();
			bwsr=null;
			fileConsolidate();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
