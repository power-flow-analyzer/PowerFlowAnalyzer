
PowerFlowAnalyzer Installation Instructions
===========================================

For a first-time installation see the first section I.
For updating an existing installation see the second section II.


I. First-Time Installtion of PowerFlowAnalyzer
----------------------------------------------

1. Install PowerFlowAnalyzer
   Unzip the file to an arbitrary location. The Zip file contains a directory
   "PowerFlowAnalyzer" which will be created in the destination folder.
   The newly created folder will be designated as "<path-to-PowerFlowAnalyzer>" in
   the further reading.
   
2. Start Matlab in Administrator mode (only on Linux, Windows Vista, Windows 7)
   Note: Matlab must have been started before with your user account; otherwise it will create
         files with improper read permissions on the first start.
   Right-click on Matlab icon and select "Run as Administrator"

3. Add the required scripts to Matlab's search path:
   a. Go to "File -> Set Path..."
   b. Click "Add with Subfolders..." and select the folder "<path-to-PowerFlowAnalyzer>/matlab"
   c. Click "Add Folder..." and select the folder "<path-to-matpower>"
      (the location where Matpower is on your system)
   d. Click "Save"
   e. Close this window via "Close"
   
4. Add MatpowerGUI to Matlab's classpath:
   a. Type "edit classpath.txt" in Matlab`s console to open the file in an editor
   b. Append the following lines (slashes may be mixed on Windows):
          <path-to-PowerFlowAnalyzer>/java/pfanalyzer.jar
          <path-to-PowerFlowAnalyzer>/java/fatcow-hosting-icons-2000.zip
          <path-to-PowerFlowAnalyzer>/java/miglayout-3.7-swing.jar

You will need to restart Matlab afterwards.

After the installation procedure you can start Matlab the usual way, i.e. 
without using the adminstrator mode.


II. Update an existing PowerFlowAnalyzer installation
-----------------------------------------------------

1. Close Matlab
   This step is required to ensure that all files can be updated properly.

2. Install MatpowerGUI
   You have to use the same location as in former installations.
   See section I-1 above for more information on how to install PowerFlowAnalyzer.
   You do not have to change any paths as you reuse the old location.
   You may delete the old installation before.
   
3. Start Matlab
   The update is complete.


III. Set Memory of MATLAB
-------------------------

Larger networks may require increasing the memory for MATLAB. Copy the file
   java.opts
into an operating system dependant folder of MATLAB:
- Windows XP  (32 bit): C:\Programme\MATLAB\R2011a\bin\win32
- Win Vista/7 (32 bit): C:\Program Files (x86)\MATLAB\R2009b\bin\win32
- Win Vista/7 (64 bit): C:\Program Files\MATLAB\R2009b\bin\win64

These folders may be different on some systems.

IV. Branding
------------
You can embed an individual logo inside PowerFlowAnalyzer. This setting must be done
in an internal file which will be created after the first start of PowerFlowAnalyzer.
Close PowerFlowAnalyzer and open the file 
   .power-flow-analyzer.ini
in a text editor. It is located in your home folder:
 - Windows XP : C:\Dokumente und Einstellungen\<your_name>
 - Win Vista/7: C:\Benutzer\<your_name>

Add the following line to this file:
   branding.image=<logo_file>
where <logo_file> is the name (or a relative path) of an image file.
The image should have an height of 32 pixel and must exist in your home folder.


FIN