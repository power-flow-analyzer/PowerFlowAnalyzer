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

A zip file containing a full distribution will be created at `target/PowerFlowAnalyzer-<VERSION>.zip`

The distribution will be assembled in `target/distribution` before zipping.

### Installation
Clone this git repository and build a release from source (see above). 

### Start
You can start PFA from from the (dynamic) output folder of your maven toolchain (e.g. command line Maven and embedded Maven runtime in Eclipse). Assuming the compiled classes are located in the _target/classes_ folder of this directory (as this is the case for the Maven build), run `pfa_start_application_dev.m` in Matlab.

For the Java classes to be reloaded in Matlab after changes in those classes, stop PFA and run the start script again.

PFA can also be started outside Matlab without any Matlab interface (for special purposes mainly). Start the Java application directly:
 - from an IDE: main class: `net.ee.pfanalyzer.PowerFlowAnalyzer`
 - from the JAR file: `java -jar PowerFlowAnalyzer-<VERSION>.jar`

License
-------
PowerFlowAnalyzer is released under the Apache License, Version 2.0 (see `License` file).
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.