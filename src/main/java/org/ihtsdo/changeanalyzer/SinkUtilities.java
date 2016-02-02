package org.ihtsdo.changeanalyzer;

import org.apache.maven.doxia.markup.HtmlMarkup;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.SinkEventAttributes;

import java.io.BufferedReader;
import java.io.IOException;

public class SinkUtilities {
	public static void headAndTitle(Sink sink, String title) {
		sink.head();
		sink.title();
		sink.text(title);
		sink.title_();

		SinkEventAttributeSet jsatts = new SinkEventAttributeSet();
		jsatts.addAttribute(SinkEventAttributes.TYPE, "text/javascript");
		jsatts.addAttribute(SinkEventAttributes.SRC, "js/jquery.js");
		sink.unknown("script", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_START) }, jsatts);
		sink.unknown("script", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_END) }, null);

		SinkEventAttributeSet pagerAttr = new SinkEventAttributeSet();
		pagerAttr.addAttribute(SinkEventAttributes.TYPE, "text/javascript");
		pagerAttr.addAttribute(SinkEventAttributes.SRC, "js/jquery.pajinate.js");
		sink.unknown("script", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_START) }, pagerAttr);
		sink.unknown("script", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_END) }, null);

		SinkEventAttributeSet sorterAttr = new SinkEventAttributeSet();
		sorterAttr.addAttribute(SinkEventAttributes.TYPE, "text/javascript");
		sorterAttr.addAttribute(SinkEventAttributes.SRC, "js/tablesort.js");
		sink.unknown("script", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_START) }, sorterAttr);
		sink.unknown("script", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_END) }, null);

		SinkEventAttributeSet atts = new SinkEventAttributeSet();
		atts.addAttribute(SinkEventAttributes.TYPE, "text/javascript");
		atts.addAttribute(SinkEventAttributes.SRC, "js/page.js");
		sink.unknown("script", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_START) }, atts);
		sink.unknown("script", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_END) }, null);

		sink.head_();
	}

	public static void createFilterSection(Sink sink) {
		SinkEventAttributeSet filterInput = new SinkEventAttributeSet();
		filterInput.addAttribute(SinkEventAttributes.TYPE, "text");
		filterInput.addAttribute(SinkEventAttributes.NAME, "filterText");
		filterInput.addAttribute(SinkEventAttributes.ID, "filterText");
		filterInput.addAttribute(SinkEventAttributes.SIZE, "100");
		filterInput.addAttribute("value", "");
		sink.unknown("input", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_START) }, filterInput);
		sink.unknown("input", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_END) }, null);

		sink.nonBreakingSpace();
		sink.nonBreakingSpace();
		sink.nonBreakingSpace();

		SinkEventAttributeSet submitFilter = new SinkEventAttributeSet();
		submitFilter.addAttribute(SinkEventAttributes.TYPE, "button");
		submitFilter.addAttribute("onclick", "javascript:simulateFilter()");
		sink.unknown("button", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_START) }, submitFilter);
		sink.text("Filter Table");
		sink.unknown("button", new Object[] { new Integer(HtmlMarkup.TAG_TYPE_END) }, null);
		sink.paragraph();
		sink.paragraph_();
	}

	public static void createGeneralInfoSection(Sink sink, BufferedReader br, int count) throws IOException {
		if (br.ready()) {
			String defHeader = br.readLine();
			sink.section1();
			sink.sectionTitle1();
			sink.text("Report Information");
			sink.sectionTitle1_();

			String[] splitedDefinition = defHeader.split("\\t", -1);
			String def = br.readLine();
			String[] defSplited = def.split("\\t", -1);
			sink.definitionList();
			for (int i = 0; i < defSplited.length; i++) {
				sink.definedTerm();
				sink.text(splitedDefinition[i]);
				sink.definedTerm_();

				sink.definition();
				sink.text(defSplited[i]);
				sink.definition_();
			}
			sink.definedTerm();
			sink.text("Size");
			sink.definedTerm_();

			sink.definition();
			sink.text(count+"");
			sink.definition_();
			sink.definitionList_();
			sink.section1_();
		}
	}

	public static void createConceptsTable(Sink sink, BufferedReader br, String conceptHeader) throws IOException {
		SinkEventAttributes tableAttr = new SinkEventAttributeSet();
		tableAttr.addAttribute(SinkEventAttributes.ID, "results");
		tableAttr.addAttribute(SinkEventAttributes.CLASS,
				"bodyTable sortable-onload-3 no-arrow rowstyle-alt colstyle-alt paginate-20 max-pages-7 paginationcallback-callbackTest-calculateTotalRating paginationcallback-callbackTest-displayTextInfo sortcompletecallback-callbackTest-calculateTotalRating");

		sink.table(tableAttr);
		sink.tableRow();

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

}
