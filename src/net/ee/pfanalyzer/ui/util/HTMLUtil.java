package net.ee.pfanalyzer.ui.util;

public class HTMLUtil {

	public static String removeHTMLTags(String text) {
		if(text == null)
			return null;
		return text.replace("<html>", "").replace("</html>", "");
	}
}
