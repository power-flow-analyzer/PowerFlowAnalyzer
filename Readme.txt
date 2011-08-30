
MatpowerGUI Installation Instructions
=====================================

For a first-time installation see the first section I.
For updating an existing installation see the second section II.


I. First-Time Installtion of MatpowerGUI
----------------------------------------

1. Install MatpowerGUI
   Unzip the file to an arbitrary location. The Zip file contains a directory
   "MatpowerGUI" which will be created in the destination folder.
   The newly created folder will be designated as "<path-to-MatpowerGUI>" in
   the further reading.

2. Add the MatpowerGUI scripts to Matlab's search path:
   a. Go to "File -> Set Path..."
   b. Select "Add with Subfolders..."
   c. Select the folder "<path-to-MatpowerGUI>/matlab"
   d. Click "Save"
   e. Close this window via "Close"
   
3. Add MatpowerGUI to Matlab's classpath:
   a. Type "edit classpath.txt" in Matlab`s console to open the file in an editor
   b. Append the following lines (slashes may be mixed on Windows):
          <path-to-MatpowerGUI>/java/pfviewer.jar
          <path-to-MatpowerGUI>/java/fatcow-hosting-icons-2000.zip

You will need to restart Matlab afterwards.


II. Update an existing MatpowerGUI installation
-----------------------------------------------

1. Close Matlab
   This step is required to ensure that all files can be updated properly.

2. Install MatpowerGUI
   You have to use the same location as in former installations.
   See section I-1 above for more information on how to install MatpowerGUI.
   You do not have to change any paths as you reuse the old location.
   You may delete the old installation before.
   
3. Start Matlab
   The update is complete.


FIN
