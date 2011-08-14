#
# Copyright (c) 2011, Willem Cazander
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

# Config variables
JAVA="java";
JAVA_OPTS="-Xms64m -Xmx256m";
MAIN_CLASS="org.nongnu.pulsefire.device.ui.PulseFireUI";
JNI_LIB="pulsefire-rxtx-linux-i686.jar";
#CP=`dir lib/*.jar`;

# Check 64 bit
IF EXISTS "%ProgramFiles(x86)%" (
   JNI_LIB=`dir jni/pulsefire-rxtx-windows-x86-*`;
) ELSE (
   JNI_LIB=`dir jni/pulsefire-rxtx-windows-x86-*`;
)

# Launch application 
$JAVA $JAVA_OPTS -cp $CP:$JNI_LIB -Djava.library.path=. $MAIN_CLASS -jni-cp;

# EOF