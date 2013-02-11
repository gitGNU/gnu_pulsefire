#
# Makefile for building PulseFire
#
# Its is bit messy down here.
#

# PulseFire vars
PF = pulsefire
TPATH = build

#---------------- Compiler Options C ----------------
# -g*: generate debugging information
# -O*: optimization level
# -f...: tuning, see GCC manual and avr-libc documentation
# -Wall...: warning level
# -Wa,...: tell GCC to pass this to the assembler.
# -adhlns...: create assembler listing
# -E -dM options instead of -c to list all define flags !!
CFLAGS = -std=gnu99
CFLAGS += -DF_CPU=$(F_CPU)
CFLAGS += -I./..
CFLAGS += -g$(DEBUG)
CFLAGS += $(CDEFS)
CFLAGS += -Os
CFLAGS += -funsigned-char
CFLAGS += -funsigned-bitfields
CFLAGS += -ffunction-sections
CFLAGS += -fpack-struct # warning on arm
CFLAGS += -fno-unit-at-a-time
CFLAGS += -fno-inline-small-functions
CFLAGS += -fno-split-wide-types
CFLAGS += -fno-tree-scev-cprop
CFLAGS += -Wall
CFLAGS += -Wstrict-prototypes
CFLAGS += -Wfloat-equal
CFLAGS += -Wunreachable-code
CFLAGS += -Wsign-compare

# Wrap flags to compiler
AVRFLAGS = $(CFLAGS)
ARMFLAGS = $(CFLAGS)

# Add compiler flags
AVRFLAGS += -mtiny-stack      # use 8 bit stack pointer
AVRFLAGS += -mcall-prologues  # make smaller code for stack frame saving.
AVRFLAGS += -fshort-enums     # gives packed warning on arm

#---------------- Linker Options ----------------
# -Wl,...: tell GCC to pass this to linker.
# -Map: create map file
# --cref: add cross reference to map file
# -lm use special avr math lib
LDFLAGS = -Wl,-Map=$(TPATH)/$(TARGET)/$(PF).map,--cref

#---------------- Programming Options (avrdude) ----------------
#
# Type: avrdude -c ?
# to get a full listing.
#
ISP_PORT = /dev/ttyACM0

#ISP_WRITE_FLASH = -U flash:w:$(TARGET).hex
#ISP_WRITE_EEPROM = -U eeprom:w:$(TARGET).eep

# Set to 8Mhz internel rc
#ISP_FUSE_ARGS = -U lfuse:w:0xd4:m -U hfuse:w:0xd9:m


# Uncomment for no verfify of the burned data.
ISP_NO_VERIFY = -V

# Increase verbosity level. Please use this when submitting bug
# reports about avrdude. See <http://savannah.nongnu.org/projects/avrdude> 
# to submit bug reports.
#ISP_VERBOSE = -v -v

# 
ISP_FLAGS = -p $(ISP_MCU) -P $(ISP_PORT) -c $(ISP_PROG) $(ISP_NO_VERIFY) $(ISP_VERBOSE)
ISP_FLAGS += $(ISP_NO_VERIFY)
ISP_FLAGS += $(ISP_VERBOSE)


#============================================================================


# Define programs and commands.
SHELL = bash
CC = avr-gcc
OBJCOPY = avr-objcopy
OBJDUMP = avr-objdump
SIZE = avr-size
NM = avr-nm
AVRDUDE = avrdude
REMOVE = rm -f
REMOVEDIR = rm -rf
COPY = cp
WINSHELL = cmd

ARM_CC = ~/bin/arm-gcc/bin/arm-none-eabi-gcc
ARM_OBJCOPY = ~/bin/arm-gcc/bin/arm-none-eabi-objcopy

# Define all src files.
SOURCE=./strings.c ./vars.c ./pulsefire.c ./chip.c ./freq.c ./adc.c ./dic.c ./sys.c ./lcd.c ./mal.c ./pwm.c ./ptc.c ./ptt.c ./stv.c ./vsc.c ./serial.c ./utils.c 

all: \
atmega328p-1601 \
atmega328p-1602 \
atmega328p-1603 \
atmega328p-1604 \
atmega328p-1691 \
atmega328p-2001 \
atmega328p-2002 \
atmega328p-2003 \
atmega328p-2004 \
atmega328p-2091 \
atmega168p-1691 \
atmega1280-1601 \
atmega1280-1602 \
atmega1280-1603 \
atmega1280-1604 \
atmega1280-1691 \
atmega2560-1601 \
atmega2560-1602 \
atmega2560-1603 \
atmega2560-1604 \
atmega2560-1691 

#
# ---- Note on check of firmware upload size;
#
# atmega2560
#    has max upload size is 258048
#
# atmega1280
#    has max upload size is 126976
#
# atmega168p
#    has max upload size is 30720
#    Current image is 27312 (#1604)
#
# atmega168p
#    has max upload size is 14336
#    Current image is 14210 (#1691)
#    Test with 328 chip; make clean atmega168p-1691-isp ISP_PORT=/dev/ttyACM1 ISP_MCU=m328p
#

#
#====================================
#-------- 328p targets
#====================================
#

atmega328p-1601: MCU = atmega328p
atmega328p-1601: F_CPU = 16000000
atmega328p-1601: CFLAGS += -mmcu=$(MCU)
atmega328p-1601: PFLAGS += -DSF_ENABLE_PWM
atmega328p-1601: PFLAGS += -DSF_ENABLE_ADC
atmega328p-1601: PFLAGS += -DSF_ENABLE_PTC0
atmega328p-1601: PFLAGS += -DSF_ENABLE_PTT
atmega328p-1601: PFLAGS += -DSF_ENABLE_STV
atmega328p-1601: PFLAGS += -DSF_ENABLE_VFC
atmega328p-1601: PFLAGS += -DSF_ENABLE_VSC0
atmega328p-1601: PFLAGS += -DSF_ENABLE_MAL
atmega328p-1601: TARGET = atmega328p-1601
atmega328p-1601: $(TPATH)/atmega328p-1601/pulsefire.hex
$(TPATH)/atmega328p-1601/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-1601-isp: atmega328p-1601
atmega328p-1601-isp: TARGET = atmega328p-1601
atmega328p-1601-isp: ISP_MCU = m328p
atmega328p-1601-isp: ISP_PROG = arduino
atmega328p-1601-isp: avr-flash


atmega328p-1602: MCU = atmega328p
atmega328p-1602: F_CPU = 16000000
atmega328p-1602: CFLAGS += -mmcu=$(MCU)
atmega328p-1602: PFLAGS += -DSF_ENABLE_PWM
atmega328p-1602: PFLAGS += -DSF_ENABLE_ADC
atmega328p-1602: PFLAGS += -DSF_ENABLE_PTC0
atmega328p-1602: PFLAGS += -DSF_ENABLE_PTT
atmega328p-1602: PFLAGS += -DSF_ENABLE_STV
atmega328p-1602: PFLAGS += -DSF_ENABLE_VFC
atmega328p-1602: PFLAGS += -DSF_ENABLE_VSC0
atmega328p-1602: PFLAGS += -DSF_ENABLE_MAL
atmega328p-1602: PFLAGS += -DSF_ENABLE_SPI
atmega328p-1602: TARGET = atmega328p-1602
atmega328p-1602: $(TPATH)/atmega328p-1602/pulsefire.hex
$(TPATH)/atmega328p-1602/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-1602-isp: atmega328p-1602
atmega328p-1602-isp: TARGET = atmega328p-1602
atmega328p-1602-isp: ISP_MCU = m328p
atmega328p-1602-isp: ISP_PROG = arduino
atmega328p-1602-isp: avr-flash


atmega328p-1603: MCU = atmega328p
atmega328p-1603: F_CPU = 16000000
atmega328p-1603: CFLAGS += -mmcu=$(MCU)
atmega328p-1603: PFLAGS += -DSF_ENABLE_PWM
atmega328p-1603: PFLAGS += -DSF_ENABLE_ADC
atmega328p-1603: PFLAGS += -DSF_ENABLE_PTC0
atmega328p-1603: PFLAGS += -DSF_ENABLE_PTT
atmega328p-1603: PFLAGS += -DSF_ENABLE_STV
atmega328p-1603: PFLAGS += -DSF_ENABLE_VFC
atmega328p-1603: PFLAGS += -DSF_ENABLE_VSC0
atmega328p-1603: PFLAGS += -DSF_ENABLE_MAL
atmega328p-1603: PFLAGS += -DSF_ENABLE_LCD
atmega328p-1603: TARGET = atmega328p-1603
atmega328p-1603: $(TPATH)/atmega328p-1603/pulsefire.hex
$(TPATH)/atmega328p-1603/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-1603-isp: atmega328p-1603
atmega328p-1603-isp: TARGET = atmega328p-1603
atmega328p-1603-isp: ISP_MCU = m328p
atmega328p-1603-isp: ISP_PROG = arduino
atmega328p-1603-isp: avr-flash


atmega328p-1604: MCU = atmega328p
atmega328p-1604: F_CPU = 16000000
atmega328p-1604: CFLAGS += -mmcu=$(MCU)
atmega328p-1604: PFLAGS += -DSF_ENABLE_PWM
atmega328p-1604: PFLAGS += -DSF_ENABLE_ADC
atmega328p-1604: PFLAGS += -DSF_ENABLE_PTC0
atmega328p-1604: PFLAGS += -DSF_ENABLE_PTT
atmega328p-1604: PFLAGS += -DSF_ENABLE_STV
atmega328p-1604: PFLAGS += -DSF_ENABLE_VFC
atmega328p-1604: PFLAGS += -DSF_ENABLE_VSC0
atmega328p-1604: PFLAGS += -DSF_ENABLE_MAL
atmega328p-1604: PFLAGS += -DSF_ENABLE_LCD
atmega328p-1604: PFLAGS += -DSF_ENABLE_SPI
atmega328p-1604: TARGET = atmega328p-1604
atmega328p-1604: $(TPATH)/atmega328p-1604/pulsefire.hex
$(TPATH)/atmega328p-1604/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-1604-isp: atmega328p-1604
atmega328p-1604-isp: TARGET = atmega328p-1604
atmega328p-1604-isp: ISP_MCU = m328p
atmega328p-1604-isp: ISP_PROG = arduino
atmega328p-1604-isp: avr-flash


atmega328p-1691: MCU = atmega328p
atmega328p-1691: F_CPU = 16000000
atmega328p-1691: CFLAGS += -mmcu=$(MCU)
atmega328p-1691: PFLAGS += -DSF_ENABLE_ADC
atmega328p-1691: PFLAGS += -DSF_ENABLE_MAL
atmega328p-1691: PFLAGS += -DSF_ENABLE_SPI
atmega328p-1691: TARGET = atmega328p-1691
atmega328p-1691: $(TPATH)/atmega328p-1691/pulsefire.hex
$(TPATH)/atmega328p-1691/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-1691-isp: atmega328p-1691
atmega328p-1691-isp: TARGET = atmega328p-1691
atmega328p-1691-isp: ISP_MCU = m328p
atmega328p-1691-isp: ISP_PROG = arduino
atmega328p-1691-isp: avr-flash


atmega328p-2001: MCU = atmega328p
atmega328p-2001: F_CPU = 20000000
atmega328p-2001: CFLAGS += -mmcu=$(MCU)
atmega328p-2001: PFLAGS += -DSF_ENABLE_PWM
atmega328p-2001: PFLAGS += -DSF_ENABLE_ADC
atmega328p-2001: PFLAGS += -DSF_ENABLE_PTC0
atmega328p-2001: PFLAGS += -DSF_ENABLE_PTT
atmega328p-2001: PFLAGS += -DSF_ENABLE_STV
atmega328p-2001: PFLAGS += -DSF_ENABLE_VFC
atmega328p-2001: PFLAGS += -DSF_ENABLE_VSC0
atmega328p-2001: PFLAGS += -DSF_ENABLE_MAL
atmega328p-2001: TARGET = atmega328p-2001
atmega328p-2001: $(TPATH)/atmega328p-2001/pulsefire.hex
$(TPATH)/atmega328p-2001/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-2001-isp: atmega328p-2001
atmega328p-2001-isp: TARGET = atmega328p-2001
atmega328p-2001-isp: ISP_MCU = m328p
atmega328p-2001-isp: ISP_PROG = arduino
atmega328p-2001-isp: avr-flash


atmega328p-2002: MCU = atmega328p
atmega328p-2002: F_CPU = 20000000
atmega328p-2002: CFLAGS += -mmcu=$(MCU)
atmega328p-2002: PFLAGS += -DSF_ENABLE_PWM
atmega328p-2002: PFLAGS += -DSF_ENABLE_ADC
atmega328p-2002: PFLAGS += -DSF_ENABLE_PTC0
atmega328p-2002: PFLAGS += -DSF_ENABLE_PTT
atmega328p-2002: PFLAGS += -DSF_ENABLE_STV
atmega328p-2002: PFLAGS += -DSF_ENABLE_VFC
atmega328p-2002: PFLAGS += -DSF_ENABLE_VSC0
atmega328p-2002: PFLAGS += -DSF_ENABLE_MAL
atmega328p-2002: PFLAGS += -DSF_ENABLE_SPI
atmega328p-2002: TARGET = atmega328p-2002
atmega328p-2002: $(TPATH)/atmega328p-2002/pulsefire.hex
$(TPATH)/atmega328p-2002/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-2002-isp: atmega328p-2002
atmega328p-2002-isp: TARGET = atmega328p-2002
atmega328p-2002-isp: ISP_MCU = m328p
atmega328p-2002-isp: ISP_PROG = arduino
atmega328p-2002-isp: avr-flash


atmega328p-2003: MCU = atmega328p
atmega328p-2003: F_CPU = 20000000
atmega328p-2003: CFLAGS += -mmcu=$(MCU)
atmega328p-2003: PFLAGS += -DSF_ENABLE_PWM
atmega328p-2003: PFLAGS += -DSF_ENABLE_ADC
atmega328p-2003: PFLAGS += -DSF_ENABLE_PTC0
atmega328p-2003: PFLAGS += -DSF_ENABLE_PTT
atmega328p-2003: PFLAGS += -DSF_ENABLE_STV
atmega328p-2003: PFLAGS += -DSF_ENABLE_VFC
atmega328p-2003: PFLAGS += -DSF_ENABLE_VSC0
atmega328p-2003: PFLAGS += -DSF_ENABLE_MAL
atmega328p-2003: PFLAGS += -DSF_ENABLE_LCD
atmega328p-2003: TARGET = atmega328p-2003
atmega328p-2003: $(TPATH)/atmega328p-2003/pulsefire.hex
$(TPATH)/atmega328p-2003/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-2003-isp: atmega328p-2003
atmega328p-2003-isp: TARGET = atmega328p-2003
atmega328p-2003-isp: ISP_MCU = m328p
atmega328p-2003-isp: ISP_PROG = arduino
atmega328p-2003-isp: avr-flash


atmega328p-2004: MCU = atmega328p
atmega328p-2004: F_CPU = 20000000
atmega328p-2004: CFLAGS += -mmcu=$(MCU)
atmega328p-2004: PFLAGS += -DSF_ENABLE_PWM
atmega328p-2004: PFLAGS += -DSF_ENABLE_ADC
atmega328p-2004: PFLAGS += -DSF_ENABLE_PTC0
atmega328p-2004: PFLAGS += -DSF_ENABLE_PTT
atmega328p-2004: PFLAGS += -DSF_ENABLE_STV
atmega328p-2004: PFLAGS += -DSF_ENABLE_VFC
atmega328p-2004: PFLAGS += -DSF_ENABLE_VSC0
atmega328p-2004: PFLAGS += -DSF_ENABLE_MAL
atmega328p-2004: PFLAGS += -DSF_ENABLE_LCD
atmega328p-2004: PFLAGS += -DSF_ENABLE_SPI
atmega328p-2004: TARGET = atmega328p-2004
atmega328p-2004: $(TPATH)/atmega328p-2004/pulsefire.hex
$(TPATH)/atmega328p-2004/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-2004-isp: atmega328p-2004
atmega328p-2004-isp: TARGET = atmega328p-2004
atmega328p-2004-isp: ISP_MCU = m328p
atmega328p-2004-isp: ISP_PROG = arduino
atmega328p-2004-isp: avr-flash


atmega328p-2091: MCU = atmega328p
atmega328p-2091: F_CPU = 20000000
atmega328p-2091: CFLAGS += -mmcu=$(MCU)
atmega328p-2091: PFLAGS += -DSF_ENABLE_ADC
atmega328p-2091: PFLAGS += -DSF_ENABLE_MAL
atmega328p-2091: PFLAGS += -DSF_ENABLE_SPI
atmega328p-2091: TARGET = atmega328p-2091
atmega328p-2091: $(TPATH)/atmega328p-2091/pulsefire.hex
$(TPATH)/atmega328p-2091/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-2091-isp: atmega328p-2091
atmega328p-2091-isp: TARGET = atmega328p-2091
atmega328p-2091-isp: ISP_MCU = m328p
atmega328p-2091-isp: ISP_PROG = arduino
atmega328p-2091-isp: avr-flash


#
#====================================
#-------- 168p targets
#====================================
#

atmega168p-1691: MCU = atmega168p
atmega168p-1691: F_CPU = 16000000
atmega168p-1691: CFLAGS += -mmcu=$(MCU)
atmega168p-1691: PFLAGS += -DSF_ENABLE_MAL
atmega168p-1691: PFLAGS += -DSF_ENABLE_ADC
atmega168p-1691: PFLAGS += -DSF_ENABLE_SPI
atmega168p-1691: TARGET = atmega168p-1691
atmega168p-1691: $(TPATH)/atmega168p-1691/pulsefire.hex
$(TPATH)/atmega168p-1691/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega168p-1691-isp: atmega168p-1691
atmega168p-1691-isp: TARGET = atmega168p-1691
atmega168p-1691-isp: ISP_MCU = m168p
atmega168p-1691-isp: ISP_PROG = arduino
atmega168p-1691-isp: avr-flash

#
#====================================
#-------- Mega 1280 targets
#====================================
#

atmega1280-1601: MCU = atmega1280
atmega1280-1601: F_CPU = 16000000
atmega1280-1601: CFLAGS += -mmcu=$(MCU)
atmega1280-1601: PFLAGS += -DSF_ENABLE_PWM
atmega1280-1601: PFLAGS += -DSF_ENABLE_CIP
atmega1280-1601: PFLAGS += -DSF_ENABLE_ADC
atmega1280-1601: PFLAGS += -DSF_ENABLE_PTC0
atmega1280-1601: PFLAGS += -DSF_ENABLE_PTC1
atmega1280-1601: PFLAGS += -DSF_ENABLE_PTT
atmega1280-1601: PFLAGS += -DSF_ENABLE_STV
atmega1280-1601: PFLAGS += -DSF_ENABLE_VFC
atmega1280-1601: PFLAGS += -DSF_ENABLE_VSC0
atmega1280-1601: PFLAGS += -DSF_ENABLE_VSC1
atmega1280-1601: PFLAGS += -DSF_ENABLE_MAL
atmega1280-1601: TARGET = atmega1280-1601
atmega1280-1601: $(TPATH)/atmega1280-1601/pulsefire.hex
$(TPATH)/atmega1280-1601/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega1280-1601-isp: atmega1280-1601
atmega1280-1601-isp: TARGET = atmega1280-1601
atmega1280-1601-isp: ISP_MCU = m1280
atmega1280-1601-isp: ISP_PROG = stk500
atmega1280-1601-isp: avr-flash

atmega1280-1602: MCU = atmega1280
atmega1280-1602: F_CPU = 16000000
atmega1280-1602: CFLAGS += -mmcu=$(MCU)
atmega1280-1602: PFLAGS += -DSF_ENABLE_PWM
atmega1280-1602: PFLAGS += -DSF_ENABLE_CIP
atmega1280-1602: PFLAGS += -DSF_ENABLE_ADC
atmega1280-1602: PFLAGS += -DSF_ENABLE_PTC0
atmega1280-1602: PFLAGS += -DSF_ENABLE_PTC1
atmega1280-1602: PFLAGS += -DSF_ENABLE_PTT
atmega1280-1602: PFLAGS += -DSF_ENABLE_STV
atmega1280-1602: PFLAGS += -DSF_ENABLE_VFC
atmega1280-1602: PFLAGS += -DSF_ENABLE_VSC0
atmega1280-1602: PFLAGS += -DSF_ENABLE_VSC1
atmega1280-1602: PFLAGS += -DSF_ENABLE_MAL
atmega1280-1602: PFLAGS += -DSF_ENABLE_SPI
atmega1280-1602: TARGET = atmega1280-1602
atmega1280-1602: $(TPATH)/atmega1280-1602/pulsefire.hex
$(TPATH)/atmega1280-1602/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega1280-1602-isp: atmega1280-1602
atmega1280-1602-isp: TARGET = atmega1280-1602
atmega1280-1602-isp: ISP_MCU = m1280
atmega1280-1602-isp: ISP_PROG = stk500
atmega1280-1602-isp: avr-flash


atmega1280-1603: MCU = atmega1280
atmega1280-1603: F_CPU = 16000000
atmega1280-1603: CFLAGS += -mmcu=$(MCU)
atmega1280-1603: PFLAGS += -DSF_ENABLE_PWM
atmega1280-1603: PFLAGS += -DSF_ENABLE_CIP
atmega1280-1603: PFLAGS += -DSF_ENABLE_ADC
atmega1280-1603: PFLAGS += -DSF_ENABLE_PTC0
atmega1280-1603: PFLAGS += -DSF_ENABLE_PTC1
atmega1280-1603: PFLAGS += -DSF_ENABLE_PTT
atmega1280-1603: PFLAGS += -DSF_ENABLE_STV
atmega1280-1603: PFLAGS += -DSF_ENABLE_VFC
atmega1280-1603: PFLAGS += -DSF_ENABLE_VSC0
atmega1280-1603: PFLAGS += -DSF_ENABLE_VSC1
atmega1280-1603: PFLAGS += -DSF_ENABLE_MAL
atmega1280-1603: PFLAGS += -DSF_ENABLE_LCD
atmega1280-1603: TARGET = atmega1280-1603
atmega1280-1603: $(TPATH)/atmega1280-1603/pulsefire.hex
$(TPATH)/atmega1280-1603/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega1280-1603-isp: atmega1280-1603
atmega1280-1603-isp: TARGET = atmega1280-1603
atmega1280-1603-isp: ISP_MCU = m1280
atmega1280-1603-isp: ISP_PROG = stk500
atmega1280-1603-isp: avr-flash


atmega1280-1604: MCU = atmega1280
atmega1280-1604: F_CPU = 16000000
atmega1280-1604: CFLAGS += -mmcu=$(MCU)
atmega1280-1604: PFLAGS += -DSF_ENABLE_PWM
atmega1280-1604: PFLAGS += -DSF_ENABLE_CIP
atmega1280-1604: PFLAGS += -DSF_ENABLE_ADC
atmega1280-1604: PFLAGS += -DSF_ENABLE_PTC0
atmega1280-1604: PFLAGS += -DSF_ENABLE_PTC1
atmega1280-1604: PFLAGS += -DSF_ENABLE_PTT
atmega1280-1604: PFLAGS += -DSF_ENABLE_STV
atmega1280-1604: PFLAGS += -DSF_ENABLE_VFC
atmega1280-1604: PFLAGS += -DSF_ENABLE_VSC0
atmega1280-1604: PFLAGS += -DSF_ENABLE_VSC1
atmega1280-1604: PFLAGS += -DSF_ENABLE_MAL
atmega1280-1604: PFLAGS += -DSF_ENABLE_LCD
atmega1280-1604: PFLAGS += -DSF_ENABLE_SPI
atmega1280-1604: TARGET = atmega1280-1604
atmega1280-1604: $(TPATH)/atmega1280-1604/pulsefire.hex
$(TPATH)/atmega1280-1604/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega1280-1604-isp: atmega1280-1604
atmega1280-1604-isp: TARGET = atmega1280-1604
atmega1280-1604-isp: ISP_MCU = m1280
atmega1280-1604-isp: ISP_PROG = stk500
atmega1280-1604-isp: avr-flash


atmega1280-1691: MCU = atmega1280
atmega1280-1691: F_CPU = 16000000
atmega1280-1691: CFLAGS += -mmcu=$(MCU)
atmega1280-1691: PFLAGS += -DSF_ENABLE_CIP
atmega1280-1691: PFLAGS += -DSF_ENABLE_ADC
atmega1280-1691: PFLAGS += -DSF_ENABLE_MAL
atmega1280-1691: PFLAGS += -DSF_ENABLE_SPI
atmega1280-1691: TARGET = atmega1280-1691
atmega1280-1691: $(TPATH)/atmega1280-1691/pulsefire.hex
$(TPATH)/atmega1280-1691/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega1280-1691-isp: atmega1280-1691
atmega1280-1691-isp: TARGET = atmega1280-1691
atmega1280-1691-isp: ISP_MCU = m1280
atmega1280-1691-isp: ISP_PROG = stk500
atmega1280-1691-isp: avr-flash


atmega2560-1601: MCU = atmega2560
atmega2560-1601: F_CPU = 16000000
atmega2560-1601: CFLAGS += -mmcu=$(MCU)
atmega2560-1601: PFLAGS += -DSF_ENABLE_PWM
atmega2560-1601: PFLAGS += -DSF_ENABLE_CIP
atmega2560-1601: PFLAGS += -DSF_ENABLE_ADC
atmega2560-1601: PFLAGS += -DSF_ENABLE_PTC0
atmega2560-1601: PFLAGS += -DSF_ENABLE_PTC1
atmega2560-1601: PFLAGS += -DSF_ENABLE_PTT
atmega2560-1601: PFLAGS += -DSF_ENABLE_STV
atmega2560-1601: PFLAGS += -DSF_ENABLE_VFC
atmega2560-1601: PFLAGS += -DSF_ENABLE_VSC0
atmega2560-1601: PFLAGS += -DSF_ENABLE_VSC1
atmega2560-1601: PFLAGS += -DSF_ENABLE_MAL
atmega2560-1601: TARGET = atmega2560-1601
atmega2560-1601: $(TPATH)/atmega2560-1601/pulsefire.hex
$(TPATH)/atmega2560-1601/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega2560-1601-isp: atmega2560-1601
atmega2560-1601-isp: TARGET = atmega2560-1601
atmega2560-1601-isp: ISP_MCU = m2560
atmega2560-1601-isp: ISP_PROG = stk500v2
atmega2560-1601-isp: avr-flash


atmega2560-1602: MCU = atmega2560
atmega2560-1602: F_CPU = 16000000
atmega2560-1602: CFLAGS += -mmcu=$(MCU)
atmega2560-1602: PFLAGS += -DSF_ENABLE_PWM
atmega2560-1602: PFLAGS += -DSF_ENABLE_CIP
atmega2560-1602: PFLAGS += -DSF_ENABLE_ADC
atmega2560-1602: PFLAGS += -DSF_ENABLE_PTC0
atmega2560-1602: PFLAGS += -DSF_ENABLE_PTC1
atmega2560-1602: PFLAGS += -DSF_ENABLE_PTT
atmega2560-1602: PFLAGS += -DSF_ENABLE_STV
atmega2560-1602: PFLAGS += -DSF_ENABLE_VFC
atmega2560-1602: PFLAGS += -DSF_ENABLE_VSC0
atmega2560-1602: PFLAGS += -DSF_ENABLE_VSC1
atmega2560-1602: PFLAGS += -DSF_ENABLE_MAL
atmega2560-1602: PFLAGS += -DSF_ENABLE_SPI
atmega2560-1602: TARGET = atmega2560-1602
atmega2560-1602: $(TPATH)/atmega2560-1602/pulsefire.hex
$(TPATH)/atmega2560-1602/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega2560-1602-isp: atmega2560-1602
atmega2560-1602-isp: TARGET = atmega2560-1602
atmega2560-1602-isp: ISP_MCU = m2560
atmega2560-1602-isp: ISP_PROG = stk500v2
atmega2560-1602-isp: avr-flash


atmega2560-1603: MCU = atmega2560
atmega2560-1603: F_CPU = 16000000
atmega2560-1603: CFLAGS += -mmcu=$(MCU)
atmega2560-1603: PFLAGS += -DSF_ENABLE_PWM
atmega2560-1603: PFLAGS += -DSF_ENABLE_CIP
atmega2560-1603: PFLAGS += -DSF_ENABLE_ADC
atmega2560-1603: PFLAGS += -DSF_ENABLE_PTC0
atmega2560-1603: PFLAGS += -DSF_ENABLE_PTC1
atmega2560-1603: PFLAGS += -DSF_ENABLE_PTT
atmega2560-1603: PFLAGS += -DSF_ENABLE_STV
atmega2560-1603: PFLAGS += -DSF_ENABLE_VFC
atmega2560-1603: PFLAGS += -DSF_ENABLE_VSC0
atmega2560-1603: PFLAGS += -DSF_ENABLE_VSC1
atmega2560-1603: PFLAGS += -DSF_ENABLE_MAL
atmega2560-1603: PFLAGS += -DSF_ENABLE_LCD
atmega2560-1603: TARGET = atmega2560-1603
atmega2560-1603: $(TPATH)/atmega2560-1603/pulsefire.hex
$(TPATH)/atmega2560-1603/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega2560-1603-isp: atmega2560-1603
atmega2560-1603-isp: TARGET = atmega2560-1603
atmega2560-1603-isp: ISP_MCU = m2560
atmega2560-1603-isp: ISP_PROG = stk500v2
atmega2560-1603-isp: avr-flash


atmega2560-1604: MCU = atmega2560
atmega2560-1604: F_CPU = 16000000
atmega2560-1604: CFLAGS += -mmcu=$(MCU)
atmega2560-1604: PFLAGS += -DSF_ENABLE_PWM
atmega2560-1604: PFLAGS += -DSF_ENABLE_CIP
atmega2560-1604: PFLAGS += -DSF_ENABLE_ADC
atmega2560-1604: PFLAGS += -DSF_ENABLE_PTC0
atmega2560-1604: PFLAGS += -DSF_ENABLE_PTC1
atmega2560-1604: PFLAGS += -DSF_ENABLE_PTT
atmega2560-1604: PFLAGS += -DSF_ENABLE_STV
atmega2560-1604: PFLAGS += -DSF_ENABLE_VFC
atmega2560-1604: PFLAGS += -DSF_ENABLE_VSC0
atmega2560-1604: PFLAGS += -DSF_ENABLE_VSC1
atmega2560-1604: PFLAGS += -DSF_ENABLE_MAL
atmega2560-1604: PFLAGS += -DSF_ENABLE_LCD
atmega2560-1604: PFLAGS += -DSF_ENABLE_SPI
atmega2560-1604: TARGET = atmega2560-1604
atmega2560-1604: $(TPATH)/atmega2560-1604/pulsefire.hex
$(TPATH)/atmega2560-1604/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega2560-1604-isp: atmega2560-1604
atmega2560-1604-isp: TARGET = atmega2560-1604
atmega2560-1604-isp: ISP_MCU = m2560
atmega2560-1604-isp: ISP_PROG = stk500v2
atmega2560-1604-isp: avr-flash


atmega2560-1691: MCU = atmega2560
atmega2560-1691: F_CPU = 16000000
atmega2560-1691: CFLAGS += -mmcu=$(MCU)
atmega2560-1691: PFLAGS += -DSF_ENABLE_ADC
atmega2560-1691: PFLAGS += -DSF_ENABLE_CIP
atmega2560-1691: PFLAGS += -DSF_ENABLE_MAL
atmega2560-1691: PFLAGS += -DSF_ENABLE_SPI
atmega2560-1691: TARGET = atmega2560-1691
atmega2560-1691: $(TPATH)/atmega2560-1691/pulsefire.hex
$(TPATH)/atmega2560-1691/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(AVRFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega2560-1691-isp: atmega2560-1691
atmega2560-1691-isp: TARGET = atmega2560-1691
atmega2560-1691-isp: ISP_MCU = m2560
atmega2560-1691-isp: ISP_PROG = stk500v2
atmega2560-1691-isp: avr-flash


arm-lpc1343-7201: MCU = cortex-m3
arm-lpc1343-7201: F_CPU = 72000000
arm-lpc1343-7201: CFLAGS += -mcpu=$(MCU) -mthumb
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_PWM
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_ADC
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_PTC0
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_PTC1
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_PTT
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_STV
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_VFC
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_VSC0
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_VSC1
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_MAL
arm-lpc1343-7201: TARGET = arm-lpc1343-7201
arm-lpc1343-7201: $(TPATH)/arm-lpc1343-7201/pulsefire.hex
$(TPATH)/arm-lpc1343-7201/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(ARM_CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(ARMFLAGS) $(PFLAGS) $(LDFLAGS)
	$(ARM_OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex




clean:
	$(REMOVE) -r $(TPATH)/atmega*

# AVRDUDE commands
avr-flash:
	$(AVRDUDE) $(ISP_FLAGS) -U flash:w:$(TPATH)/$(TARGET)/$(PF).hex

avr-fuse:
	$(AVRDUDE) $(AVRDUDE_FLAGS) $(AVRDUDE_FUSE_ARGS)

avr-clean:
	$(AVRDUDE) $(AVRDUDE_FLAGS) -e 

avr-testisp:
	$(AVRDUDE) $(AVRDUDE_FLAGS) -v 


