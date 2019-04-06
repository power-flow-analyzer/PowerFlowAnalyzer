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
package net.ee.pfanalyzer.ui.viewer;

import java.util.List;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.data.DataViewerDialogData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ParameterSupport;

public class DataViewerDialog extends ParameterSupport {

	private DataViewerDialogData data;
	private ModelData dialogModel;
	
	public DataViewerDialog(String dialogID) {
		data = new DataViewerDialogData();
		data.setModelID(dialogID);
		dialogModel = PowerFlowAnalyzer.getConfiguration().getConfiguration(dialogID);
	}
	
	public DataViewerDialog(DataViewerDialogData data) {
		this.data = data;
		dialogModel = PowerFlowAnalyzer.getConfiguration().getConfiguration(data.getModelID());
	}
	
	public String getDialogID() {
		return data.getModelID();
	}
	
	public ModelData getDialogModel() {
		return dialogModel;
	}

	@Override
	public List<NetworkParameter> getParameterList() {
		return data.getParameter();
	}
	
	public DataViewerDialogData getDialogData() {
		return data;
	}
}
