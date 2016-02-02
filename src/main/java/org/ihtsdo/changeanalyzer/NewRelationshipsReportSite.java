package org.ihtsdo.changeanalyzer;

import org.apache.log4j.Logger;
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
 * @goal report-new-rels
 * @phase site
 */
public class NewRelationshipsReportSite extends AbstractMavenReport {

	private static final Logger logger = Logger.getLogger(NewRelationshipsReportSite.class);

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
			SinkUtilities.headAndTitle(sink, "New Relationships Report");
			sink.body();

			File file = new File(inputDirectory, ReleaseFilesReportPlugin.NEW_RELATIONSHIPS_FILE);
			FileInputStream sortedFis = new FileInputStream(file);
			InputStreamReader sortedIsr = new InputStreamReader(sortedFis, "UTF-8");
			BufferedReader br = new BufferedReader(sortedIsr);
			SinkUtilities.createGeneralInfoSection(sink, br, 0);

			sink.section2();
			sink.sectionTitle2();
			sink.text("Relationship Table");
			sink.sectionTitle2_();

			SinkUtilities.createFilterSection(sink);

			createRelationshipTable(sink, br);

			sink.section2_();

			sink.body_();
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createRelationshipTable(Sink sink, BufferedReader br) throws IOException {
		HashMap<String, ArrayList<String>> conceptMap = new HashMap<String, ArrayList<String>>();

		String header = br.readLine();
		// Id Active Source Destination Type Characteristic Type Modifier
		while (br.ready()) {
			String line = br.readLine();
			String[] splited = line.split("\\t");
			if (conceptMap.containsKey(splited[2])) {
				conceptMap.get(splited[2]).add(line);
			} else {
				ArrayList<String> value = new ArrayList<String>();
				value.add(line);
				conceptMap.put(splited[2], value);
			}
		}

		String[] conceptHeaderSplited = header.split("\\t", -1);

		// Concept table creation
		SinkEventAttributes conceptTableAttr = new SinkEventAttributeSet();
		conceptTableAttr.addAttribute(SinkEventAttributes.ID, "results");
		conceptTableAttr.addAttribute(SinkEventAttributes.CLASS,
				"bodyTable sortable-onload-3 no-arrow rowstyle-alt colstyle-alt paginate-20 max-pages-7 paginationcallback-callbackTest-calculateTotalRating paginationcallback-callbackTest-displayTextInfo sortcompletecallback-callbackTest-calculateTotalRating");

		sink.table(conceptTableAttr);
		// Concept Table Header
		sink.tableRow();
		SinkEventAttributes headerAttrs = new SinkEventAttributeSet();
		headerAttrs.addAttribute(SinkEventAttributes.CLASS, "sortable-text fd-column-" + (0));
		sink.tableHeaderCell(headerAttrs);
		sink.text("Source Concept");
		sink.tableHeaderCell_();
		headerAttrs.addAttribute(SinkEventAttributes.CLASS, "sortable-numeric fd-column-" + (1));
		sink.tableHeaderCell(headerAttrs);
		sink.text("Relationships");
		sink.tableHeaderCell_();
		sink.tableRow_();
		Set<String> keyset = conceptMap.keySet();
		for (String string : keyset) {
			// Create Concept Row with link to the html file.
			sink.tableRow();
			sink.tableCell();

			SinkEventAttributes findigLinkAttrs = new SinkEventAttributeSet();
			findigLinkAttrs.addAttribute("onclick", "javascript:showFindings(\"newRels/" + URLEncoder.encode(string.replaceAll(" ", ""), "UTF-8") + ".html\"" + ")");
			findigLinkAttrs.addAttribute("href", "javascript:linkme();");
			sink.unknown("a", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_START) }, findigLinkAttrs);
			sink.text(string);
			sink.unknown("a", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_END) }, null);
			sink.tableCell_();
			sink.tableCell();
			sink.text("" + conceptMap.get(string).size());
			sink.tableCell_();
			sink.tableRow_();

			// Relationship html file sink initialization
			File outputDir = new File(outputDirectory, "newRels");
			outputDir.mkdir();
			File relFile = new File(outputDir, URLEncoder.encode(string.replaceAll(" ", ""), "UTF-8") + ".html");
			FileOutputStream fos = new FileOutputStream(relFile);
			XhtmlSink conceptSink = (XhtmlSink) new XhtmlSinkFactory().createSink(fos, "UTF-8");
			conceptSink.head();
			conceptSink.title();
			conceptSink.text("Concept Details");
			conceptSink.title();
			conceptSink.head_();
			conceptSink.body();
			ArrayList<String> rels = conceptMap.get(string);
			// Relationshp table and table header
			conceptSink.table();
			conceptSink.tableRow();
			for (int i = 0; i < conceptHeaderSplited.length; i++) {
				conceptSink.tableHeaderCell(headerAttrs);
				conceptSink.text(conceptHeaderSplited[i]);
				conceptSink.tableHeaderCell_();
			}
			conceptSink.tableRow_();
			// Create rel rows
			for (String string2 : rels) {
				String[] ruleLineSplit = string2.split("\\t", -1);
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
		sink.table_();

		sink.section3();
		sink.sectionTitle3();
		sink.text("Relationships");
		sink.sectionTitle3_();
		sink.lineBreak();
		SinkEventAttributes divAttrs = new SinkEventAttributeSet();
		divAttrs.addAttribute(SinkEventAttributes.CLASS, "findings");
		sink.unknown("div", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_START) }, divAttrs);
		sink.text("Click on a concept to see the new relationships.");
		sink.unknown("div", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_END) }, null);

		sink.section3_();

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
		return project.getArtifactId() + "-NewRels-Report";
	}

	private ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle("report-new-rels");
	}

}
