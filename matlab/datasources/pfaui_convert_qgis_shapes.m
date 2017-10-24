function pfaui_convert_qgis_shapes( pfa_network )
%PFAUI_CONVERT_QGIS_SHAPES Summary of this function goes here
%   Detailed explanation goes here

datasource = pfa_param_text(pfa_network, 'QGIS_CONFIG');
pfa_convert_qgis_shapes(datasource);

% close progress dialog in PFA
pfa_hide_progress;

end

