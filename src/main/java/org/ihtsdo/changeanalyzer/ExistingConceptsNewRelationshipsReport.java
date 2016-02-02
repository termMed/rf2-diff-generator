package org.ihtsdo.changeanalyzer;

import org.apache.log4j.Logger;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Reports all new concepts in a release file.
 * 
 * @author Vahram
 * @goal existing-concepts-new-rels
 * @phase site
 */
public class ExistingConceptsNewRelationshipsReport extends AbstractMavenReport {

	private static final Logger logger = Logger.getLogger(ExistingConceptsNewRelationshipsReport.class);

	/**
	 * The Maven Project Object
	 * 
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * Specifies the directory where the report will be generated
	 * 
	 * @parameter default-value="${project.reporting.outputDirectory}"
	 * @required
	 */
	private String outputDirectory;

	/**
	 * Specifies the directory of report source files
	 * 
	 * @parameter default-value="${project.reporting.outputDirectory}"
	 * @required
	 */
	private String inputDirectory;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private Renderer siteRenderer;

	@Override
	protected void executeReport(Locale arg0) throws MavenReportException {
		try {
			Sink sink = getSink();
			SinkUtilities.headAndTitle(sink, "Existing Concepts New Relationships");
			sink.body();

			File file = new File(inputDirectory, ReleaseFilesReportPlugin.OLD_CONCEPTS_NEW_RELATIONSHIPS_FILE);
			FileInputStream sortedFis = new FileInputStream(file);
			InputStreamReader sortedIsr = new InputStreamReader(sortedFis, "UTF-8");
			BufferedReader br = new BufferedReader(sortedIsr);
			SinkUtilities.createGeneralInfoSection(sink, br,0);

			sink.section2();
			sink.sectionTitle2();
			sink.text("Relationship Table");
			sink.sectionTitle2_();

			SinkUtilities.createFilterSection(sink);

			createConceptsTable(sink, br);

			sink.section2_();

			sink.body_();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createConceptsTable(Sink sink, BufferedReader br) throws IOException {
		SinkEventAttributes tableAttr = new SinkEventAttributeSet();
		tableAttr.addAttribute(SinkEventAttributes.ID, "results");
		tableAttr.addAttribute(SinkEventAttributes.CLASS,
				"bodyTable sortable-onload-3 no-arrow rowstyle-alt colstyle-alt paginate-20 max-pages-7 paginationcallback-callbackTest-calculateTotalRating paginationcallback-callbackTest-displayTextInfo sortcompletecallback-callbackTest-calculateTotalRating");

		sink.table(tableAttr);
		sink.tableRow();

		String conceptHeader = br.readLine();

		String[] conceptHeaderSplited = conceptHeader.split("\\t", -1);
		SinkEventAttributes headerAttrs = new SinkEventAttributeSet();
		for (int i = 0; i < conceptHeaderSplited.length; i++) {
			if (i >= 1) {
				headerAttrs.addAttribute(SinkEventAttributes.CLASS, "sortable-text fd-column-" + (i - 1));
			} else if (i == 0) {
				headerAttrs.addAttribute(SinkEventAttributes.CLASS, "sortable-numeric fd-column-" + (0));
			}
			sink.tableHeaderCell(headerAttrs);
			sink.text(conceptHeaderSplited[i]);
			sink.tableHeaderCell_();
		}
		sink.tableRow_();
		while (br.ready()) {
			String conceptLine = br.readLine();
			String[] ruleLineSplit = conceptLine.split("\\t", -1);

			sink.tableRow();
			for (int i = 0; i < ruleLineSplit.length; i++) {
				sink.tableCell();
				sink.text(ruleLineSplit[i]);
				sink.tableCell_();
			}
			sink.tableRow_();
		}
		sink.table_();
	}

	@Override
	protected String getOutputDirectory() {
		return outputDirectory;
	}

	@Override
	protected MavenProject getProject() {
		return project;
	}

	@Override
	protected Renderer getSiteRenderer() {
		return siteRenderer;
	}

	@Override
	public String getDescription(Locale locale) {
		return getBundle(locale).getString("report.description");
	}

	@Override
	public String getName(Locale locale) {
		return getBundle(locale).getString("report.name");
	}

	@Override
	public String getOutputName() {
		return project.getArtifactId() + "-ExistingConceptsNewRels-Report";
	}

	private ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle("existing-concepts-new-rels");
	}

}
