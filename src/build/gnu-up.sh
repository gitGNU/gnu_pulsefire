#!/bin/sh
#
# Small script to sign and upload files to savannah
#

if [ "" == "$1" ]; then
	echo "No username given to upload.";
	exit 1;
fi;
if [ "" == "$2" ];then
	echo "No upload dir given.";
	exit 1;
fi;

# Goto project root;
cd `dirname $0`/../..;

# Copy to one new dir.
mkdir -p target/gnu-up/$2;
mv pulsefire-build/pulsefire-build-ui-launch4j/target/*.zip target/gnu-up/$2;
mv pulsefire-build/pulsefire-build-ui-dist/target/*-bin.* target/gnu-up/$2;
mv pulsefire-build/pulsefire-build-project-src/target/*-dist.* target/gnu-up/$2;

# Sign per file we want to upload.
for FILE in `ls target/gnu-up/$2/*`; do
	gpg -b --use-agent $FILE;
done;

# Make sure readable
chmod 644 target/gnu-up/$2/*;

# And copy with new dir to gnu
scp -r target/gnu-up/$2 $1@dl.sv.nongnu.org:/releases/pulsefire/;

echo "Done";
exit 0;

