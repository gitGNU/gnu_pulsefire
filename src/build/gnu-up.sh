#!/bin/sh
#
# Small script to sign and upload files to savannah
#

if ["" == "$1"]; then
	echo "No username given to upload.";
	exit 1;
fi;
if ["" == "$2"];then
	echo "No upload dir given.";
	exit 1;
fi;

# Goto project root;
cd `dirname $0`/../..;

# Sign files we want to upload.
#gpg -b --use-agent pulsefire-build/pulsefire-build-ui-launch4j/*.zip;
#gpg -b --use-agent pulsefire-build/pulsefire-build-ui-dist/target/*-bin.*;
#gpg -b --use-agent pulsefire-build/pulsefire-build-project-src/target/*-dist.*;

# Make sure readable
chmod 644 pulsefire-build/pulsefire-build-ui-launch4j/*;
chmod 644 pulsefire-build/pulsefire-build-ui-dist/target/*;
chmod 644 pulsefire-build/pulsefire-build-project-src/target/*;

mkdir -p target/gnu-up/$2;
mv pulsefire-build/pulsefire-build-ui-launch4j/target/*.zip target/gnu-up/$2;
mv pulsefire-build/pulsefire-build-ui-launch4j/target/*.zip.sig target/gnu-up/$2;
mv pulsefire-build/pulsefire-build-ui-dist/target/*-bin.* target/gnu-up/$2;
mv pulsefire-build/pulsefire-build-project-src/target/*-dist.* target/gnu-up/$2;

scp -r target/gnu-up/$2 $1@dl.sv.nongnu.org:/releases/pulsefire/;

