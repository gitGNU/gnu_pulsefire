::
:: Copyright (c) 2011, Willem Cazander
:: All rights reserved.
::
:: Redistribution and use in source and binary forms, with or without modification, are permitted provided
:: that the following conditions are met:
::
:: * Redistributions of source code must retain the above copyright notice, this list of conditions and the
::   following disclaimer.
:: * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
::   the following disclaimer in the documentation and/or other materials provided with the distribution.
::
:: THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
:: EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
:: MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
:: THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
:: SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
:: OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
:: HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
:: TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
:: SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
::
@echo off
setlocal enableextensions

:: Run in app dir
cd /d %~dp0

:: Config variables
set JAVA_OPTS=-Xms64m -Xmx256m
set MAIN_CLASS=org.nongnu.pulsefire.device.ui.PulseFireUI
set CP=lib\*

:: Check 64 bit for serial
set ARCH=x86_64
IF "%PROCESSOR_ARCHITECTURE%"=="x86" set ARCH=x86
for /f "tokens=*" %%a in (
'dir /B jni\pulsefire-rxtx-windows-%ARCH%-*'
) do (
set JNI_CP=%%a
)

:: Launch application
java %JAVA_OPTS% -cp "%CP%;jni\%JNI_CP%" -Djava.library.path=. %MAIN_CLASS% -jni-cp

endlocal 
:: EOF
