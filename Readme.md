PowerFlowAnalyzer
=================

PowerFlowAnalyzer (PFA) is a toolbox for the analysis of power systems, written in Java and Matlab. Its main focus is the analysis of network data derived from computations and/or external data sources.
PFA is free software: you can use it, modify it and redistribute it under the terms of the Apache License, Version 2.0.

Building from Source
--------------------
PFA can be build using the provided Maven POM-file. Simply execute `mvn package` (you need to have Maven installed).

A zip file containing a full distribution will be created at `target/distribution/PowerFlowAnalyzer-<VERSION>.zip`

Installation
------------
Download the zip file, unzip it. A new folder `PowerFlowAnalyzer-<VERSION>` will be created. The final name is version dependant. 

Alternatively, clone this git repository into your favourite IDE (e.g. Eclipse), create a `bin` directory in this folder and set it as output for the Java compiler of your IDE. Eclipse users may use the provided `.project` file to import PFA as a fully configured Eclipse project.

Start
-----
Run `pfa_start_application.m` in Matlab.

Alternatively, you can start PFA from from the classpath of an IDE. Assuming the compiled classes are located in the _bin_ folder of this directory, run `pfa_start_application_dev.m` in Matlab.

License
-------
PowerFlowAnalyzer is released under the Apache License, Version 2.0 (see `License` file).
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.