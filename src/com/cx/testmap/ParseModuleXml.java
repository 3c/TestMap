package com.cx.testmap;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author CX parse config xml
 */
public class ParseModuleXml extends DefaultHandler {
	private String tagName;
	private ArrayList<ParseModule> listParse;
	private ParseModule parseModule;

	public ArrayList<ParseModule> getParseResult() {

		return listParse;
	}

	@Override
	public void startDocument() throws SAXException {
		listParse = new ArrayList<ParseModule>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		this.tagName = localName;
		if (this.tagName.equals("class")) {
			parseModule = new ParseModule();
			if (attributes.getLength() == 2) {
				if (attributes.getQName(0).equals("name")) {
					parseModule.moduleClass = attributes.getValue(0);
				}
				if (attributes.getQName(1).equals("parse")) {
					parseModule.parseClass = attributes.getValue(1);
				}
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals("class")) {
			listParse.add(parseModule);
		}
		tagName = "";
	}
}
