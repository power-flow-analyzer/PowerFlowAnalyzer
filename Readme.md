PowerFlowAnalyzer
=================

PowerFlowAnalyzer (PFA) is a toolbox for the analysis of power systems, written in Java and Matlab. Its main focus is the analysis of network data derived from computations and/or external data sources.
PFA is free software: you can use it, modify it and redistribute it under the terms of the Apache License, Version 2.0.

For Users
---------
### Installation
Download the zip file `PowerFlowAnalyzer-<VERSION>.jar`, unzip it. A new folder `PowerFlowAnalyzer-<VERSION>` will be created. The final name is version dependant (e.g. `PowerFlowAnalyzer-2.3.0`).

### Start
Run `pfa_start_application.m` in Matlab.

For Developers
--------------

### Building from Source
PFA can be build using the provided Maven POM-file. 
Simply execute the following two steps:
 - Clone this git repository
 - Create a build using `mvn package` (you need to have Maven installed)
 
For MATPOWER support in your builds, download a MATPOWER release or clone a git repository.
The MATPOWER files must reside in a `matpower` folder inside this folder. In general, a `matpower`
folder (if existing) will be copied to the distribution folder and added to Matlab's search path.

A zip file containing a full distribution will be created at `target/PowerFlowAnalyzer-<VERSION>.zip`

The distribution will be assembled in `target/distribution` before zipping.

### Installation
Clone this git repository and build a release from source (see above). 

### Start
Run `pfa_start_application_dev.m` in Matlab.
You can start PFA from from the (dynamic) output folder of your maven toolchain (e.g. command line 
Maven or embedded Maven runtime in Eclipse). The script assumes the compiled classes to be located 
in the _target/classes_ folder of this directory (as this is the case for the provided Maven build).

For the Java classes to be reloaded in Matlab after changes in those classes, stop PFA and run the start script again.

PFA can also be started outside Matlab without any Matlab interface (for special purposes mainly). Start the Java application directly:
 - from an IDE: main class: `net.ee.pfanalyzer.PowerFlowAnalyzer`
 - from the JAR file: `java -jar PowerFlowAnalyzer-<VERSION>.jar`

License
-------
PowerFlowAnalyzer is released under the Apache License, Version 2.0 (see `LICENSE` file).
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

This product includes [MATPOWER](http://www.pserc.cornell.edu/matpower) 
[(Github)](https://github.com/MATPOWER/matpower "MATPOWER on Github"), 
[MigLayout](http://miglayout.com) [(Github)](https://github.com/mikaelgrev/miglayout "MigLayout on Github") and [Fatcow Hosting Icons](http://www.fatcow.com) 
[(Github)](https://github.com/ioBroker/ioBroker.icons-fatcow-hosting "Fatcow Hosting Icons on Github").
See `NOTICE` file for more information. 
