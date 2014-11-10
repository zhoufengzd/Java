package org.zen.utils;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlLineReader {

	public HashMap<String, String> readAttributes(String xmlLine) {
		XmlLineReader.XHandler h = new XHandler();

		StringBuilder buffer = new StringBuilder();
		int bLength = 0;
		for (int i = 0; i < xmlLine.length(); i++) {
			char c = xmlLine.charAt(i);

			if (Character.isSpaceChar(c)) {
				if (bLength == 0) // leading space
					continue;

				/*
				if (i == xmlLine.length())
					continue;

				if (Character.isSpaceChar(xmlLine.charAt(i + 1))) {
					System.out.println(xmlLine.charAt(i + 1));
					continue;
				}
				*/
			}

			buffer.append(c);
			bLength++;
		}

		if (buffer.charAt(bLength - 1) != '>')
			buffer.append("/>");
		else if (buffer.charAt(bLength - 2) != '/')
			buffer.insert(bLength - 1, '/');

		try {
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			saxParser.parse((new InputSource(new StringReader(buffer.toString()))), h);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return h.getAttributes();
	}

	private class XHandler extends DefaultHandler {

		public XHandler() {
			_attributes = new LinkedHashMap<String, String>();
		}

		public HashMap<String, String> getAttributes() {
			return _attributes;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			int length = attributes.getLength();
			for (int i = 0; i < length; i++) {
				String attNae = attributes.getQName(i);
				String attValue = attributes.getValue(i);

				_attributes.put(attNae, attValue);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
		}

		private LinkedHashMap<String, String> _attributes;
	}

}