
# PulseFire Building

## Requirements

* java sdk 1.6 or higher.
* maven 3 or higher.
* make/avr-gcc (apt-get install make gcc-avr avr-libc avrdude)
* for launch4j build on 64b os get ia32-libs installed.

## Create build artifacts

cd project-root/;
mvn -Ppf-build clean package;

Which results in zip/tar/gz archieves in the different build targets;
ls pulsefire-build/pulsefire-build-*/target/*

## Run via maven

cd project-root/;
mvn clean install;
cd pulsefire-java/pulsefire-device-ui/;
mvn exec:java -Dexec.mainClass="org.nongnu.pulsefire.device.ui.PulseFireUI"

## Build manual chip code

cd project-root/;
cd pulsefire-chip/src/main/c/;
make clean atmega328p-007;
or
make clean atmega328p-007-isp;
or 
make clean all;

note: firmwares manual build do not show in application.

## Run hardware debug

Hardware setup;
- mega is on ttyACM1
- uno is on ttyACM0
- wire uno RESET to GND
- wire uno TX-1 to mega TX3-14 

Flash mega with htx firmware;
cd pulsefire-chip/src/main/c/;
make clean atmega2560-todo-isp ISP_PORT=/dev/ttyACM1;

Start debug console;
stty -F /dev/ttyACM0 cs8 115200 ignbrk -brkint -icrnl -imaxbel -opost -onlcr -isig -icanon -iexten -echo -echoe -echok -echoctl -echoke noflsh -ixon -crtscts
cat < /dev/ttyACM0

## Test Flash code manual

cd project-root/;
java -Djava.library.path=TODOpulsefire-lib/pulsefire-rxtx/pulsefire-rxtx-linux-x86_64/src/main/jni/ \
	-cp "pulsefire-java/pulsefire-device-flash/target/pulsefire-device-flash-1.0.5-SNAPSHOT.jar:pulsefire-java/pulsefire-rxtx/pulsefire-rxtx-lib/target/pulsefire-rxtx-lib-1.0.5-SNAPSHOT.jar" \
	oorg.nongnu.pulsefire.lib.avr.flash.FlashManager \
	-P /dev/ttyACM0 -c arduino -f pulsefire-chip/src/main/c/build/atmega328p-007/pulsefire.hex


## Change pom.xml versions

cd project-root/;
mvn versions:set -DnewVersion=2.3.4-SNAPSHOT

## Make release build

cd project-root/;
mvn -Ppf-build clean package;
mvn -B -Dusername=(scm_username) clean install release:clean release:prepare release:perform;
src/build/gnu-up.sh (scm_username) (version)

## Make site

cd project-root/;
mvn -Ppf-build-site clean install site:site
Optional add -DstagingDirectory=/tmp/pf-fullsite
And then manual upload.

## Check for dependency-updates

cd project-root/;
mvn versions:display-plugin-updates|grep ">"|uniq;
mvn versions:display-dependency-updates|grep ">"|uniq;

## Eclipse Setup

* Download Eclipse Luna EE (4.4) from; http://eclipse.org/downloads/
* Install via marketplace "AVR Eclipse Plugin" for basic c/c++ editors support.
* Clone remote git repro to local.
* Import project into workspace from git working dir.
* Import the other modules by Import -> Maven/ Excisting maven projects and the select the git working dir.

- Goto pulsefire-device-ui and open class PulseFireUI.
- Run PulseFireUI it will start and give two warnings;
[WARNING] Could not load build info impl fallback to local one.
This is not fatal it only is for the build into in the title of frame and will go away
after doing a package or install of pulsefire project.
[WARNING] Could not init serial lib: gnu.io.CommPortIdentifier
To fix goto the eclipse "Run configurations" and select the PulseFireUI one.
Goto Arguments tab panel and fill in the program arguments
"-jni-cp" (without quotes)
And fill in the VM arguments;
"-Djava.library.path=." (without quotes)
Then apply and run and pulsefire should connect to serial correctly. 

