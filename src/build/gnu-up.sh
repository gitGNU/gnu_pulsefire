#!/bin/sh
#
# Copyright (c) 2012, Willem Cazander
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without modification, are permitted provided
# that the following conditions are met:
#
# * Redistributions of source code must retain the above copyright notice, this list of conditions and the
#   following disclaimer.
# * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
#   the following disclaimer in the documentation and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
# EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
# MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
# THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
# OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
# HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
# TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#
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

