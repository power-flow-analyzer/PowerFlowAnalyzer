function edit_parameters( input_file )
%EDIT_PARAMETERS Summary of this function goes here
%   Detailed explanation goes here

if exist('input_file', 'var') == 0
   input_file = ''; 
end

net.ee.pfanalyzer.ui.db.ModelDBDialog.startAsApplication({input_file, pwd}, false);

end

