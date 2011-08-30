package net.ee.pfanalyzer.preferences;

import java.io.File;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.ui.dialog.SetupDialog;

public class PreferencesInitializer implements IPreferenceConstants {

	public static void checkForEmptyPreferences() {
		String matpowerDir = Preferences.getFirstProperty(PROPERTY_MATPOWER_DIRECTORY, true);
		if(matpowerDir != null && isMatpowerDir(matpowerDir))
			return;// do nothing
		new SetupDialog(PowerFlowAnalyzer.getInstance());
	}
	
	public static  boolean isMatpowerDir(String path) {
		File matpowerDir = new File(path);
		return matpowerDir.exists() 
				&& new File(matpowerDir, "define_constants.m").exists()
				&& new File(matpowerDir, "runpf.m").exists();
	}
}
