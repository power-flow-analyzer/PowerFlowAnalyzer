package net.ee.pfanalyzer.preferences;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

public class Preferences {

	private final static String PREFERENCES_FILE = ".power-flow-analyzer.ini";
	private final static String DEFAULT_PREFERENCES_FILE = "/properties.ini";
	private final static String FILE_COMMENTS = "This file was automatically generated by Matpower User Interface";
	
	private static Properties properties = new Properties();
	private static Properties defaultProperties = new Properties();
	
	static {
		InputStream defaultIn = null;
		FileReader reader = null;
		try {
			// load default preferences
			defaultIn = Preferences.class.getResourceAsStream(DEFAULT_PREFERENCES_FILE);
			defaultProperties.load(defaultIn);
			// load user preferences
			File prefFile = getPreferencesFile();
			if(prefFile.exists() && prefFile.canRead()) {
				reader = new FileReader(prefFile);
				properties.load(reader);
				reader.close();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Could not load preferences: " + e, 
					"Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} finally {
			try {
				if(defaultIn != null)
					defaultIn.close();
				if(reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static File getPreferencesFile() {
		return new File(System.getProperty("user.home"), PREFERENCES_FILE);
	}
	
	public static void saveProperties() {
		try {
			FileWriter writer = new FileWriter(getPreferencesFile());
			properties.store(writer, FILE_COMMENTS);
			writer.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Could not save user preferences: " + e, 
					"Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	public static String getProperty(String name) {
		return getProperty(name, false);
	}
	
	public static String getProperty(String name, boolean isUserPreference) {
		String property = properties.getProperty(name);
		if(isUserPreference == false && property == null)
			return getDefaultProperty(name);
		return property;
	}
	
	public static String getFirstProperty(String name) {
		return getFirstProperty(name, false);
	}
	
	public static String getFirstProperty(String name, boolean isUserPreference) {
		String property = getProperty(name, isUserPreference);
		if(property == null)
			return null;
		String[] items = property.split(",");
		if(items == null || items.length == 0)
			return property;
		return items[0].trim();
	}
	
	public static String getDefaultProperty(String name) {
		return getDefaultProperty(name, true);
	}
	
	public static String getDefaultProperty(String name, boolean mustExist) {
		String property = defaultProperties.getProperty(name);
		if(mustExist && property == null) {
			System.err.println("Property has no default value: " + name);
			throw new IllegalArgumentException("Property has no default value: " + name);
		}
		return property;
	}
	
	public static boolean getBooleanProperty(String name) {
		return Boolean.parseBoolean(getProperty(name).trim());
	}
	
	public static int getIntProperty(String name) {
		return Integer.parseInt(getProperty(name).trim());
	}
	
	public static void setProperty(String name, String value) {
		// only write preferences differing from default preferences
		if(value == null || value.equals(getDefaultProperty(name, false)))
			properties.remove(name);
		else
			properties.setProperty(name, value);
	}
	
	public static void setProperty(String name, boolean value) {
		setProperty(name, Boolean.toString(value));
	}
	
	public static void setProperty(String name, int value) {
		setProperty(name, Integer.toString(value));
	}
}
