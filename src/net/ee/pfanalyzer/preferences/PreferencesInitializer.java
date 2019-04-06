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
package net.ee.pfanalyzer.preferences;

import java.io.File;

public class PreferencesInitializer implements IPreferenceConstants {

	public static void checkForEmptyPreferences() {
//		String matpowerDir = Preferences.getFirstProperty(PROPERTY_MATPOWER_DIRECTORY, true);
//		if(matpowerDir != null && isMatpowerDir(matpowerDir))
//			return;// do nothing
//		new SetupDialog(PowerFlowAnalyzer.getInstance());
	}
	
	public static  boolean isMatpowerDir(String path) {
		File matpowerDir = new File(path);
		return matpowerDir.exists() 
				&& new File(matpowerDir, "define_constants.m").exists()
				&& new File(matpowerDir, "runpf.m").exists();
	}
}
