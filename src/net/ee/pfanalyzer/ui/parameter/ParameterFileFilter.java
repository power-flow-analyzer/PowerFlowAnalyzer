/*******************************************************************************
 * Copyright 2019 Markus Gronau
 * 
 * This file is part of PowerFlowAnalyzer.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
