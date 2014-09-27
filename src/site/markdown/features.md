<!--
Copyright (c) 2011, Willem Cazander
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided
that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list of conditions and the
  following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
  the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-->

# PulseFire Features

## Generic

* Savable config in eeprom for standalone usage.
* All diffent chip data is resetable.
* Supports most text lcds, size and speed is configable.
* Generic pin connection modes on some pins.
* Multiple extented connection modes with extra chips.
* Two programmable step timers which can change any config variable.
* Multiple trigger step timers to change config variables.
* Virtual feedback mapping from/to variables.

## Software pwm

* 1 to 16 outputs
* 5 different pulse modes
* Internal and External trigging with delays.
* Pulse direction in both ways and auto reverse.
* Per output masking and inverting.
* Per output on/off time step value.
* Two banks of config data.

## Programmable Pulse Modulator

* Variable bit pattern length max 16 bits.
* Per output bit pattern.

## Inputs

* Analog and Digital inputs.
* Per input action is mappable to a internal variables.
* Per input value range adjustment.
* Analog value jitter removal.
* Auto push data to ui for realtime viewing.

## Safety

* Maximum and minimum treshold values.
* Dynamic assign trashold values to variables.
* Dynamic action based on warning or error.
* Minimal time delay in safety mode.
* LCD message when entering safety mode.

## PulseFire-UI

* Easy modification of almost any config variable.
* Pulled readout of all internal variables.
* Console log to see all messages to and from pulsefire chip.
* All mappable steps made easy with drop downs.
* Realtime viewing al input data.
* Most variable are automatic graphed over time.
* Log data variables to file.
* Custom nightly work compatible color themes.
* Comes with prebuild pulsefire chip firmwares.
* Comes with simple flash dialog to flash firmware to device.

