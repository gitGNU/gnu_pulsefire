
=== Welcome in PulseFire ===

PulseFire is a programmable PWM and bit sequencer build on the Arduino platform in C.
Targeted for free energy (re)search with full generic configurable mappings for analog/digital
inputs so it can operate HHO cells, magnetic motors and solid state coil shorting setups.
Some features; max 16 outputs,soft warm-up,timing per channel,serial console,lcd interface,etc.

=== Files in this package ===

authors.txt         - Contributors listing.
install.txt         - Install guide.
licence.txt         - The licence of PulseFire.
pom.xml             - Maven specific build file used by frontend IDE
readme.txt   				- This file.
todo.txt            - Small todo list.
versions.txt 	      - Versions history.
pulsefire-frontend	- Java packages for pulsefire gui
src                 - Arduino/site/schematic source

=== Licenced binary files ===

./src/schemetic/avr-ext-chips.sch
./src/site/resources/images/arduino-default.png
./src/site/resources/images/avr-ext-chips.png
./src/site/resources/images/banner-background.png
./src/site/resources/images/pulsefire-gui.png
./src/site/resources/images/pulsefire-left.png
./src/site/pulsefire.jks


The binary files in the above listing fall all under
the licence terms described in the licence.txt file.


=== Timing calucalations ===

T1 - 
T2 - 
T3 - 
T4 - foo*bar/to+do.

  +---------------T4--------------+    
  +----+                          +----+
D0|    |                          |    |
  |    |                          |    |
--+    +--------------------------+    +---
  +-T1-+-T2-+     
            +----+ 
Dx          |    |
            |    |
------------+    +-------------------------
                         +---T3---+
                    +----+ 
Dlast               |    |
                    |    |
--------------------+    +-----------------




