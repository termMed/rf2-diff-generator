package org.ihtsdo.changeanalyzer;

import org.apache.maven.doxia.markup.HtmlMarkup;
import org.apache.maven.doxia.module.xhtml.XhtmlSink;
import org.apache.maven.doxia.module.xhtml.XhtmlSinkFactory;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * Reports all new concepts in a release file.
 * 
 * @author Vahram
 * @goal report-concepts
 * @phase site
 */
public class ConceptReportSite extends AbstractMavenReport {

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
	 * Specifies the directory of report source files
	 * 
	 * @parameter
	 * @required
	 */
	private String conceptFileToReport;

	/**
	 * bundle
	 * 
	 * @parameter
	 * @required
	 */
	private String bundle;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private Renderer siteRenderer;

	private String conceptHeader;

	private int count;

	private HashMap<String, ArrayList<String>> semanticTagMap;

	@Override
	protected void executeReport(Locale arg0) throws MavenReportException {
		try {
			Sink sink = getSink();
			SinkUtilities.headAndTitle(sink, "Concepts Report");
			sink.body();

			File file = new File(inputDirectory, conceptFileToReport);
			FileInputStream sortedFis = new FileInputStream(file);
			InputStreamReader sortedIsr = new InputStreamReader(sortedFis, "UTF-8");
			BufferedReader br = new BufferedReader(sortedIsr);

			SinkUtilities.createGeneralInfoSection(sink, br, count);

			conceptHeader = br.readLine();

			// Create a map with concepts semantic tags and lines
			semanticTagMap = new HashMap<String, ArrayList<String>>();
			// CONCPET ID MODULE DEFINITION STATUS TERM SEMANTIC TAG
			count = 0;
			while (br.ready()) {
				String line = br.readLine();
				String[] splited = line.split("\\t");
				if (semanticTagMap.containsKey(splited[4])) {
					semanticTagMap.get(splited[4]).add(line);
				} else {
					ArrayList<String> lines = new ArrayList<String>();
					lines.add(line);
					semanticTagMap.put(splited[4], lines);
				}
				count++;
			}

			sink.section2();
			sink.sectionTitle2();
			sink.text("Concept Table");
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
		// Create concepts semantic tag table
		Set<String> semanticTags = semanticTagMap.keySet();

		// Semantic tag tables attributes
		SinkEventAttributes tableAttr = new SinkEventAttributeSet();
		tableAttr.addAttribute(SinkEventAttributes.ID, "results");
		tableAttr.addAttribute(SinkEventAttributes.CLASS,
				"bodyTable sortable-onload-3 no-arrow rowstyle-alt colstyle-alt paginate-20 max-pages-7 paginationcallback-callbackTest-calculateTotalRating paginationcallback-callbackTest-displayTextInfo sortcompletecallback-callbackTest-calculateTotalRating");

		// Semantic tag table
		sink.table(tableAttr);
		// Semantic tag tables header
		sink.tableRow();
		sink.tableHeaderCell();
		sink.text("Semantic Tag");
		sink.tableHeaderCell_();
		sink.tableHeaderCell();
		sink.text("New concepts count");
		sink.tableHeaderCell_();
		sink.tableRow_();
		// Semantic tag table rows
		if (semanticTags.isEmpty()) {
			sink.tableRow();
			sink.tableCell();
			sink.text("No concepts found");
			sink.tableCell_();
			sink.tableCell();
			sink.tableCell_();
			sink.tableRow_();
			sink.table_();
		} else {
			for (String semanticTag : semanticTags) {
				ArrayList<String> semTagConcepts = semanticTagMap.get(semanticTag);
				createConceptsFile(semTagConcepts, semanticTag);
				// Create Semantic tag line
				SinkEventAttributes findigLinkAttrs = new SinkEventAttributeSet();
				findigLinkAttrs.addAttribute("onclick", "javascript:showFindings(\"newRels/" + URLEncoder.encode(semanticTag.replaceAll(" ", ""), "UTF-8") + ".html\"" + ")");
				findigLinkAttrs.addAttribute("href", "javascript:linkme();");

				sink.tableRow();
				sink.tableCell();
				sink.unknown("a", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_START) }, findigLinkAttrs);
				sink.text(semanticTag);
				sink.unknown("a", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_END) }, null);
				sink.tableCell_();
				sink.tableCell();
				sink.text("" + semTagConcepts.size());
				sink.tableCell_();
				sink.tableRow_();
			}
			// Close semantic tag table
			sink.table_();
			sink.section3();
			sink.sectionTitle3();
			sink.text("Concpets");
			sink.sectionTitle3_();
			sink.lineBreak();
			SinkEventAttributes divAttrs = new SinkEventAttributeSet();
			divAttrs.addAttribute(SinkEventAttributes.CLASS, "findings");
			sink.unknown("div", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_START) }, divAttrs);
			sink.text("Click on a sematic tag to see the concepts");
			sink.unknown("div", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_END) }, null);

			sink.section3_();
		}

	}

	private void createConceptsFile(ArrayList<String> semTagConcepts, String semanticTag) throws IOException {
		// Relationship html file sink initialization
		File outputDir = new File(outputDirectory, "newRels");
		outputDir.mkdir();
		File relFile = new File(outputDir, URLEncoder.encode(semanticTag.replaceAll(" ", ""), "UTF-8") + ".html");
		FileOutputStream fos = new FileOutputStream(relFile);
		XhtmlSink conceptSink = (XhtmlSink) new XhtmlSinkFactory().createSink(fos, "UTF-8");
		conceptSink.head();
		conceptSink.title();
		conceptSink.text("Concpets");
		conceptSink.title_();
		conceptSink.head_();
		conceptSink.body();

		SinkEventAttributes tableAttr = new SinkEventAttributeSet();
		tableAttr.addAttribute(SinkEventAttributes.ID, "results2");
		tableAttr.addAttribute(SinkEventAttributes.CLASS,
				"bodyTable sortable-onload-3 no-arrow rowstyle-alt colstyle-alt paginate-15 max-pages-7 paginationcallback-callbackTest-calculateTotalRating paginationcallback-callbackTest-displayTextInfo sortcompletecallback-callbackTest-calculateTotalRating");

		conceptSink.table(tableAttr);
		conceptSink.tableRow();

		String[] conceptHeaderSplited = conceptHeader.split("\\t", -1);
		SinkEventAttributes headerAttrs = new SinkEventAttributeSet();
		for (int i = 0; i < conceptHeaderSplited.length; i++) {
			if (i >= 1) {
				headerAttrs.addAttribute(SinkEventAttributes.CLASS, "sortable-text fd-column-" + (i - 1));
			} else if (i == 0) {
				headerAttrs.addAttribute(SinkEventAttributes.CLASS, "sortable-numeric fd-column-" + (0));
			}
			conceptSink.tableHeaderCell(headerAttrs);
			conceptSink.text(conceptHeaderSplited[i]);
			conceptSink.tableHeaderCell_();
		}
		conceptSink.tableRow_();
		for (String string : semTagConcepts) {

			String[] ruleLineSplit = string.split("\\t", -1);

			conceptSink.tableRow();
			for (int i = 0; i < ruleLineSplit.length; i++) {
				conceptSink.tableCell();
				conceptSink.text(ruleLineSplit[i]);
				conceptSink.tableCell_();
			}
			conceptSink.tableRow_();
		}
		conceptSink.table_();
		conceptSink.body_();
		conceptSink.flush();
		conceptSink.close();
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
		return project.getArtifactId() + conceptFileToReport.replaceAll(".txt", "") + "-Site-Report";
	}

	private ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle(bundle);
	}

}
