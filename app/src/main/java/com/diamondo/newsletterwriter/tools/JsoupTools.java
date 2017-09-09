package com.diamondo.newsletterwriter.tools;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.diamondo.newsletterwriter.model.Feed;

public class JsoupTools {

	public static String selectStringFromElement(Element element, String selector, String attribute) {
		String selectedString = null;
		if (selector != null && selector.length() > 0) {
			try {
				Elements subElements = element.select(selector);
				if (attribute == null || attribute.length() == 0) {
					selectedString = subElements.first().text();
				} else {
					selectedString = subElements.first().attr(attribute);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return selectedString;
	}

	public static String titleTagFromElement(Element element) {
		String title = null;
		try {
			String elementAsString = element.toString();
			Document document = Jsoup.parse(elementAsString);
			title = document.title();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return title;
	}

	public static Document fetchHTMLDocumentFromURL(String url) {
		Document document = null;
		try {
			document = Jsoup.connect(url).userAgent(USER_AGENT).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;
	}

	public static Document fetchXMLDocumentFromURL(String url) {
		Document document = null;
		try {
			document = Jsoup.connect(url).userAgent(USER_AGENT).parser(Parser.xmlParser()).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;
	}

	public static String USER_AGENT = "Mozilla/5.0";

}
