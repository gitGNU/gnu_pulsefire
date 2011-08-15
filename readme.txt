
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
./src/docbook/images/logo-pulsefire.png
./src/docbook/images/pulsefire-overview.svg
./src/site/resources/images/arduino-default.png
./src/site/resources/images/avr-ext-chips.png
./src/site/resources/images/banner-background.png
./src/site/resources/images/pulsefire-gui.png
./src/site/resources/images/pulsefire-left.png
./src/site/resources/images/pulsefire-overview.png
./src/site/pulsefire.jks


The binary files in the above listing fall all under
the licence terms described in the licence.txt file.


=== External licenced binary files ===

./pulsefire-java/pulsefire-rxtx/pulsefire-rxtx-lib/src/main/lib/RXTXcomm.jar
./pulsefire-java/pulsefire-rxtx/pulsefire-rxtx-windows-x86/src/main/jni/rxtxSerial.dll
./pulsefire-java/pulsefire-rxtx/pulsefire-rxtx-windows-x86_64/src/main/jni/rxtxSerial.dll
./pulsefire-java/pulsefire-rxtx/pulsefire-rxtx-mac-10.5/src/main/jni/librxtxSerial.jnilib
./pulsefire-java/pulsefire-rxtx/pulsefire-rxtx-linux-armv71/src/main/jni/librxtxSerial.so
./pulsefire-java/pulsefire-rxtx/pulsefire-rxtx-linux-ppc32/src/main/jni/librxtxSerial.so
./pulsefire-java/pulsefire-rxtx/pulsefire-rxtx-linux-ppc64/src/main/jni/librxtxSerial.so
./pulsefire-java/pulsefire-rxtx/pulsefire-rxtx-linux-x86/src/main/jni/librxtxSerial.so
./pulsefire-java/pulsefire-rxtx/pulsefire-rxtx-linux-x86_64/src/main/jni/librxtxSerial.so
./pulsefire-java/pulsefire-rxtx/pulsefire-rxtx-openindiana-x86_64/src/main/jni/librxtxSerial.so

The binary files in the above listing fall all under the licence terms
described in the licence file of rxtx on http://rxtx.qbang.org/.

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




