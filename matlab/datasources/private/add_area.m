function [] = add_area( jnetwork, area_id, area_name, model_id, ...
    relative_csvPath)
%IMPORT_AREAS Summary of this function goes here
%   Detailed explanation goes here

% add outline definition for area to DB
addOutline(jnetwork, area_id, area_name, relative_csvPath);
% add area element to network
addArea(jnetwork, area_id, model_id, area_name);

end

function [joutline] = addOutline(jnetwork, outline_id, label, csvPath)
joutline = net.ee.pfanalyzer.model.data.ModelData();
joutline.setID(outline_id);
joutline.setLabel(label);
joutline.getParameter().add(createParameter('CSV_DATA_FILE', csvPath));
jnetwork.getModelDB().getOutlineClass().getModel().add(joutline);
joutline.setParent(jnetwork.getModelDB().getOutlineClass());
jnetwork.getModelDB().fireElementChanged(...
    net.ee.pfanalyzer.model.DatabaseChangeEvent(0, joutline)); % 0 means ADDED
end

function [jarea] = addArea(jnetwork, area_id, model_id, label)
jarea = net.ee.pfanalyzer.model.NetworkElement(jnetwork);
jarea.setName(label);
jarea.setModelID(model_id);
jarea.setParameter('OUTLINE', area_id);
% jarea.getParameterList(createParameter('OUTLINE', id));
jnetwork.addElement(jarea);
jnetwork.fireNetworkChanged();
end

function [jparam] = createParameter(id, value)
jparam = net.ee.pfanalyzer.model.data.NetworkParameter;
jparam.setID(id);
jparam.setValue(value);
end