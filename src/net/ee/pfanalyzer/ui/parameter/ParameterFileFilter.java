package net.ee.pfanalyzer.ui.parameter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ParameterFileFilter extends FileFilter {

	private String description;
	private String[] fileSuffixes;

	public ParameterFileFilter(String description, String[] fileSuffixes) {
		this.description = description;
		this.fileSuffixes = fileSuffixes;
	}
	
	@Override
	public boolean accept(File f) {
		if(f.isDirectory())
			return true;
		for (int i = 0; i < fileSuffixes.length; i++) {
			if(f.getName().endsWith("." + fileSuffixes[i]))
				return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		String result = description + " (";
		for (int i = 0; i < fileSuffixes.length; i++) {
			result += "*." + fileSuffixes[i];
			if(i < fileSuffixes.length - 1)
				result += ", ";
		}
		result += ")";
		return result;
	}
	
	public String getDefaultFileSuffix() {
		if(fileSuffixes.length > 0)
			return fileSuffixes[0];
		else
			return "";
	}
	
	public String getNormalizedFileName(String path) {
		boolean hasSuffix = false;
		for (int i = 0; i < fileSuffixes.length; i++) {
			if(path.endsWith("." + fileSuffixes[i])) {
				hasSuffix = true;
				break;
			}
		}
		if(hasSuffix == false)
			path += "." + getDefaultFileSuffix();
		return path;
	}
}
