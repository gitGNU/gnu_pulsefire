
# PulseFire

Copyright 1997-2014 Willem Cazander

License: BSD 2-Clause license, see license.txt

## Introduction

You are currently reading the README file for the Pulsefire project.
This project is hosted under http://www.nongnu.org/pulsefire/
Please refer to these pages for updated information and the latest version of pulsefire.

Pulsefire is an open-source software package for controlling pulse generators, implemented in 
the programming language Java(tm) for interface and plain C for pulse code.

PulseFire currently targets the Arduino(tm) platform as basis for the software programmable PWM 
and bit sequencer generators.

  +----+                          +----+
--+    +--------------------------+    +---
            +----+ 
------------+    +-------------------------
                    +----+ 
--------------------+    +-----------------

## Files in root folder

authors.md          - Contributors listing.
build.md            - Developer build guide.
licence.txt         - The licence of PulseFire.
pom.xml             - Maven specific build file.
readme.md           - This file.
todo.md             - Small todo list.
versions.md         - Versions history.
pulsefire-*         - Maven Java/Chip packages for pulsefire.
src                 - site/schematic source.

## Licensed binary files

./src/schemetic/avr-ext-chips.sch
./src/schemetic/pf-wfc-driver.sch
./src/docbook/images/logo-pulsefire.png
./src/docbook/images/pulsefire-overview.svg
./src/site/resources/images/arduino-default.png
./src/site/resources/images/avr-ext-chips.png
./src/site/resources/images/banner-background.png
./src/site/resources/images/pulsefire-gui.png
./src/site/resources/images/pulsefire-left.png
./src/site/resources/images/pulsefire-overview.png
./src/site/resources/images/pulsefire-ui-flash.png
./src/site/resources/images/pulsefire-ui-inputs.png
./src/site/resources/images/pulsefire-ui-pwm.png
./src/site/resources/images/pulsefire-ui-vars.png
./src/site/resources/images/pf-wfc-driver.png
./src/site/pulsefire.jks

The binary files in the above listing fall all under
the licence terms described in the licence.txt file.


## External licensed binary files

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


