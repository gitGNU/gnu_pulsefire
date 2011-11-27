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
#CFLAGS += -mcall-prologues // AVR cc flag
CFLAGS += -funsigned-char
CFLAGS += -funsigned-bitfields
#CFLAGS += -fpack-struct // warning on arm
#CFLAGS += -fshort-enums // gives packed warning on arm
CFLAGS += -fno-unit-at-a-time
CFLAGS += -fno-inline-small-functions
CFLAGS += -fno-split-wide-types
CFLAGS += -fno-tree-scev-cprop
CFLAGS += -Wall
CFLAGS += -Wstrict-prototypes
CFLAGS += -Wfloat-equal
CFLAGS += -ffunction-sections
CFLAGS += -Wunreachable-code
CFLAGS += -Wsign-compare

# Minimalistic printf version
PRINTF_LIB_MIN = -Wl,-u,vfprintf -lprintf_min

# Minimalistic scanf version
SCANF_LIB_MIN = -Wl,-u,vfscanf -lscanf_min

#---------------- Linker Options ----------------
# -Wl,...: tell GCC to pass this to linker.
# -Map: create map file
# --cref: add cross reference to map file
# -lm use special avr math lib
LDFLAGS = -Wl,-Map=$(TPATH)/$(TARGET)/$(PF).map,--cref
LDFLAGS += $(PRINTF_LIB) $(SCANF_LIB)

#---------------- Programming Options (avrdude) ----------------
#
# Type: avrdude -c ?
# to get a full listing.
#
ISP_PORT = /dev/ttyACM0
#or /dev/ttyACM0

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

ARM_CC = ~/sat/bin/arm-none-eabi-gcc
ARM_OBJCOPY = ~/sat/bin/arm-none-eabi-objcopy

# Define all src files.
SOURCE=./strings.c ./vars.c ./pulsefire.c ./chip.c ./freq.c ./input.c ./lcd.c ./lpm.c ./mal.c ./pwm.c ./ptc.c ./stv.c ./serial.c ./utils.c 

all: atmega328p-001 \
atmega328p-002 \
atmega328p-003 \
atmega328p-004 \
atmega328p-005 \
atmega328p-006 \
atmega328p-007 \
atmega328p-020 \
atmega328p-021 \
atmega328p-022 \
atmega168p-001 \
atmega168p-002 \
atmega168p-003 \
atmega168p-004 \
atmega328p-2001 \
atmega328p-2002 \
atmega328p-2003 \
atmega328p-2004 \
atmega328p-2005 \
atmega328p-2006 \
atmega328p-2007 \
atmega328p-2008 \
atmega168p-2001 \
atmega168p-2002 \
atmega328p-801 \
atmega328p-802 \
atmega328p-803 \
atmega328p-804 \
atmega328p-805 \
atmega328p-806 \
atmega328p-807 \
atmega328p-808 \
atmega168p-801 \
atmega1280-1601 \
atmega1280-1602 \
atmega1280-1603 \
atmega1280-1604 \
atmega1280-1605 \
atmega1280-1606 \
atmega1280-1607 \
atmega1280-1608

#
#====================================
#-------- 16 Mhz targets
#====================================
#

atmega328p-001: MCU = atmega328p
atmega328p-001: F_CPU = 16000000
atmega328p-001: CFLAGS += -mmcu=$(MCU)
atmega328p-001: PFLAGS += -DSF_ENABLE_PWM
atmega328p-001: PFLAGS += -DSF_ENABLE_LPM
atmega328p-001: PFLAGS += -DSF_ENABLE_PPM
atmega328p-001: PFLAGS += -DSF_ENABLE_ADC
atmega328p-001: PFLAGS += -DSF_ENABLE_DIC
atmega328p-001: PFLAGS += -DSF_ENABLE_DOC
atmega328p-001: PFLAGS += -DSF_ENABLE_DEV
atmega328p-001: PFLAGS += -DSF_ENABLE_PTC
atmega328p-001: PFLAGS += -DSF_ENABLE_PTT
atmega328p-001: PFLAGS += -DSF_ENABLE_STV
atmega328p-001: PFLAGS += -DSF_ENABLE_VFC
atmega328p-001: PFLAGS += -DSF_ENABLE_FRQ
atmega328p-001: PFLAGS += -DSF_ENABLE_SWC
atmega328p-001: PFLAGS += -DSF_ENABLE_MAL
atmega328p-001: TARGET = atmega328p-001
atmega328p-001: $(TPATH)/atmega328p-001/pulsefire.hex
$(TPATH)/atmega328p-001/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-001-isp: atmega328p-001
atmega328p-001-isp: TARGET = atmega328p-001
atmega328p-001-isp: ISP_MCU = m328p
atmega328p-001-isp: ISP_PROG = arduino
atmega328p-001-isp: avr-flash

atmega328p-002: MCU = atmega328p
atmega328p-002: F_CPU = 16000000
atmega328p-002: CFLAGS += -mmcu=$(MCU)
atmega328p-002: PFLAGS += -DSF_ENABLE_PWM
atmega328p-002: PFLAGS += -DSF_ENABLE_LCD
atmega328p-002: PFLAGS += -DSF_ENABLE_LPM
atmega328p-002: PFLAGS += -DSF_ENABLE_PPM
atmega328p-002: PFLAGS += -DSF_ENABLE_ADC
atmega328p-002: PFLAGS += -DSF_ENABLE_DIC
atmega328p-002: PFLAGS += -DSF_ENABLE_DOC
atmega328p-002: PFLAGS += -DSF_ENABLE_DEV
atmega328p-002: PFLAGS += -DSF_ENABLE_PTC
atmega328p-002: PFLAGS += -DSF_ENABLE_PTT
atmega328p-002: PFLAGS += -DSF_ENABLE_STV
atmega328p-002: PFLAGS += -DSF_ENABLE_VFC
atmega328p-002: PFLAGS += -DSF_ENABLE_FRQ
atmega328p-002: PFLAGS += -DSF_ENABLE_SWC
atmega328p-002: PFLAGS += -DSF_ENABLE_MAL
atmega328p-002: TARGET = atmega328p-002
atmega328p-002: $(TPATH)/atmega328p-002/pulsefire.hex
$(TPATH)/atmega328p-002/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-002-isp: atmega328p-002
atmega328p-002-isp: TARGET = atmega328p-002
atmega328p-002-isp: ISP_MCU = m328p
atmega328p-002-isp: ISP_PROG = arduino
atmega328p-002-isp: avr-flash

atmega328p-003: MCU = atmega328p
atmega328p-003: F_CPU = 16000000
atmega328p-003: CFLAGS += -mmcu=$(MCU)
atmega328p-003: PFLAGS += -DSF_ENABLE_PWM
atmega328p-003: PFLAGS += -DSF_ENABLE_LCD
atmega328p-003: PFLAGS += -DSF_ENABLE_LPM
atmega328p-003: PFLAGS += -DSF_ENABLE_PPM
atmega328p-003: PFLAGS += -DSF_ENABLE_ADC
atmega328p-003: PFLAGS += -DSF_ENABLE_DIC
atmega328p-003: PFLAGS += -DSF_ENABLE_DOC
atmega328p-003: PFLAGS += -DSF_ENABLE_DEV
atmega328p-003: PFLAGS += -DSF_ENABLE_PTC
atmega328p-003: PFLAGS += -DSF_ENABLE_PTT
atmega328p-003: PFLAGS += -DSF_ENABLE_STV
atmega328p-003: PFLAGS += -DSF_ENABLE_VFC
atmega328p-003: PFLAGS += -DSF_ENABLE_FRQ
atmega328p-003: PFLAGS += -DSF_ENABLE_SWC
atmega328p-003: PFLAGS += -DSF_ENABLE_MAL
atmega328p-003: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega328p-003: TARGET = atmega328p-003
atmega328p-003: $(TPATH)/atmega328p-003/pulsefire.hex
$(TPATH)/atmega328p-003/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-003-isp: atmega328p-003
atmega328p-003-isp: TARGET = atmega328p-003
atmega328p-003-isp: ISP_MCU = m328p
atmega328p-003-isp: ISP_PROG = arduino
atmega328p-003-isp: avr-flash

atmega328p-004: MCU = atmega328p
atmega328p-004: F_CPU = 16000000
atmega328p-004: CFLAGS += -mmcu=$(MCU)
atmega328p-004: PFLAGS += -DSF_ENABLE_PWM
atmega328p-004: PFLAGS += -DSF_ENABLE_LCD
atmega328p-004: PFLAGS += -DSF_ENABLE_LPM
atmega328p-004: PFLAGS += -DSF_ENABLE_PPM
atmega328p-004: PFLAGS += -DSF_ENABLE_ADC
atmega328p-004: PFLAGS += -DSF_ENABLE_DIC
atmega328p-004: PFLAGS += -DSF_ENABLE_DOC
atmega328p-004: PFLAGS += -DSF_ENABLE_DEV
atmega328p-004: PFLAGS += -DSF_ENABLE_PTC
atmega328p-004: PFLAGS += -DSF_ENABLE_PTT
atmega328p-004: PFLAGS += -DSF_ENABLE_STV
atmega328p-004: PFLAGS += -DSF_ENABLE_VFC
atmega328p-004: PFLAGS += -DSF_ENABLE_FRQ
atmega328p-004: PFLAGS += -DSF_ENABLE_SWC
atmega328p-004: PFLAGS += -DSF_ENABLE_MAL
atmega328p-004: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-004: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega328p-004: TARGET = atmega328p-004
atmega328p-004: $(TPATH)/atmega328p-004/pulsefire.hex
$(TPATH)/atmega328p-004/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-004-isp: atmega328p-004
atmega328p-004-isp: TARGET = atmega328p-004
atmega328p-004-isp: ISP_MCU = m328p
atmega328p-004-isp: ISP_PROG = arduino
atmega328p-004-isp: avr-flash

atmega328p-005: MCU = atmega328p
atmega328p-005: F_CPU = 16000000
atmega328p-005: CFLAGS += -mmcu=$(MCU)
atmega328p-005: PFLAGS += -DSF_ENABLE_PWM
atmega328p-005: PFLAGS += -DSF_ENABLE_LCD
atmega328p-005: PFLAGS += -DSF_ENABLE_LPM
atmega328p-005: PFLAGS += -DSF_ENABLE_PPM
atmega328p-005: PFLAGS += -DSF_ENABLE_ADC
atmega328p-005: PFLAGS += -DSF_ENABLE_DIC
atmega328p-005: PFLAGS += -DSF_ENABLE_DOC
atmega328p-005: PFLAGS += -DSF_ENABLE_DEV
atmega328p-005: PFLAGS += -DSF_ENABLE_PTC
atmega328p-005: PFLAGS += -DSF_ENABLE_PTT
atmega328p-005: PFLAGS += -DSF_ENABLE_STV
atmega328p-005: PFLAGS += -DSF_ENABLE_VFC
atmega328p-005: PFLAGS += -DSF_ENABLE_FRQ
atmega328p-005: PFLAGS += -DSF_ENABLE_SWC
atmega328p-005: PFLAGS += -DSF_ENABLE_MAL
atmega328p-005: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-005: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega328p-005: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega328p-005: TARGET = atmega328p-005
atmega328p-005: $(TPATH)/atmega328p-005/pulsefire.hex
$(TPATH)/atmega328p-005/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-005-isp: atmega328p-005
atmega328p-005-isp: TARGET = atmega328p-005
atmega328p-005-isp: ISP_MCU = m328p
atmega328p-005-isp: ISP_PROG = arduino
atmega328p-005-isp: avr-flash

atmega328p-006: MCU = atmega328p
atmega328p-006: F_CPU = 16000000
atmega328p-006: CFLAGS += -mmcu=$(MCU)
atmega328p-006: PFLAGS += -DSF_ENABLE_PWM
atmega328p-006: PFLAGS += -DSF_ENABLE_LCD
atmega328p-006: PFLAGS += -DSF_ENABLE_LPM
atmega328p-006: PFLAGS += -DSF_ENABLE_PPM
atmega328p-006: PFLAGS += -DSF_ENABLE_ADC
atmega328p-006: PFLAGS += -DSF_ENABLE_DIC
atmega328p-006: PFLAGS += -DSF_ENABLE_DOC
atmega328p-006: PFLAGS += -DSF_ENABLE_DEV
atmega328p-006: PFLAGS += -DSF_ENABLE_PTC
atmega328p-006: PFLAGS += -DSF_ENABLE_PTT
atmega328p-006: PFLAGS += -DSF_ENABLE_STV
atmega328p-006: PFLAGS += -DSF_ENABLE_VFC
atmega328p-006: PFLAGS += -DSF_ENABLE_FRQ
atmega328p-006: PFLAGS += -DSF_ENABLE_SWC
atmega328p-006: PFLAGS += -DSF_ENABLE_MAL
atmega328p-006: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-006: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega328p-006: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega328p-006: PFLAGS += -DSF_ENABLE_EXT_LCD_DIC
atmega328p-006: TARGET = atmega328p-006
atmega328p-006: $(TPATH)/atmega328p-006/pulsefire.hex
$(TPATH)/atmega328p-006/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-006-isp: atmega328p-006
atmega328p-006-isp: TARGET = atmega328p-006
atmega328p-006-isp: ISP_MCU = m328p
atmega328p-006-isp: ISP_PROG = arduino
atmega328p-006-isp: avr-flash


atmega328p-007: MCU = atmega328p
atmega328p-007: F_CPU = 16000000
atmega328p-007: CFLAGS += -mmcu=$(MCU)
atmega328p-007: PFLAGS += -DSF_ENABLE_PWM
atmega328p-007: PFLAGS += -DSF_ENABLE_LCD
atmega328p-007: PFLAGS += -DSF_ENABLE_LPM
atmega328p-007: PFLAGS += -DSF_ENABLE_PPM
atmega328p-007: PFLAGS += -DSF_ENABLE_ADC
atmega328p-007: PFLAGS += -DSF_ENABLE_DIC
atmega328p-007: PFLAGS += -DSF_ENABLE_DOC
atmega328p-007: PFLAGS += -DSF_ENABLE_DEV
atmega328p-007: PFLAGS += -DSF_ENABLE_PTC
atmega328p-007: PFLAGS += -DSF_ENABLE_PTT
atmega328p-007: PFLAGS += -DSF_ENABLE_STV
atmega328p-007: PFLAGS += -DSF_ENABLE_VFC
atmega328p-007: PFLAGS += -DSF_ENABLE_FRQ
atmega328p-007: PFLAGS += -DSF_ENABLE_SWC
atmega328p-007: PFLAGS += -DSF_ENABLE_MAL
atmega328p-007: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-007: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega328p-007: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega328p-007: PFLAGS += -DSF_ENABLE_EXT_LCD_DIC
atmega328p-007: PFLAGS += -DSF_ENABLE_EXT_LCD_DOC
atmega328p-007: TARGET = atmega328p-007
atmega328p-007: $(TPATH)/atmega328p-007/pulsefire.hex
$(TPATH)/atmega328p-007/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-007-isp: atmega328p-007
atmega328p-007-isp: TARGET = atmega328p-007
atmega328p-007-isp: ISP_MCU = m328p
atmega328p-007-isp: ISP_PROG = arduino
atmega328p-007-isp: avr-flash


atmega328p-020: MCU = atmega328p
atmega328p-020: F_CPU = 16000000
atmega328p-020: CFLAGS += -mmcu=$(MCU)
atmega328p-020: PFLAGS += -DSF_ENABLE_PWM
atmega328p-020: PFLAGS += -DSF_ENABLE_LPM
atmega328p-020: PFLAGS += -DSF_ENABLE_PPM
atmega328p-020: PFLAGS += -DSF_ENABLE_ADC
atmega328p-020: PFLAGS += -DSF_ENABLE_DIC
atmega328p-020: PFLAGS += -DSF_ENABLE_DOC
atmega328p-020: PFLAGS += -DSF_ENABLE_DEV
atmega328p-020: PFLAGS += -DSF_ENABLE_PTC
atmega328p-020: PFLAGS += -DSF_ENABLE_PTT
atmega328p-020: PFLAGS += -DSF_ENABLE_STV
atmega328p-020: PFLAGS += -DSF_ENABLE_VFC
atmega328p-020: PFLAGS += -DSF_ENABLE_FRQ
atmega328p-020: PFLAGS += -DSF_ENABLE_SWC
atmega328p-020: PFLAGS += -DSF_ENABLE_MAL
atmega328p-020: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-020: TARGET = atmega328p-020
atmega328p-020: $(TPATH)/atmega328p-020/pulsefire.hex
$(TPATH)/atmega328p-020/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-020-isp: atmega328p-020
atmega328p-020-isp: TARGET = atmega328p-020
atmega328p-020-isp: ISP_MCU = m328p
atmega328p-020-isp: ISP_PROG = arduino
atmega328p-020-isp: avr-flash

atmega328p-021: MCU = atmega328p
atmega328p-021: F_CPU = 16000000
atmega328p-021: CFLAGS += -mmcu=$(MCU)
atmega328p-021: PFLAGS += -DSF_ENABLE_PWM
atmega328p-021: PFLAGS += -DSF_ENABLE_LPM
atmega328p-021: PFLAGS += -DSF_ENABLE_PPM
atmega328p-021: PFLAGS += -DSF_ENABLE_ADC
atmega328p-021: PFLAGS += -DSF_ENABLE_DIC
atmega328p-021: PFLAGS += -DSF_ENABLE_DOC
atmega328p-021: PFLAGS += -DSF_ENABLE_DEV
atmega328p-021: PFLAGS += -DSF_ENABLE_PTC
atmega328p-021: PFLAGS += -DSF_ENABLE_PTT
atmega328p-021: PFLAGS += -DSF_ENABLE_STV
atmega328p-021: PFLAGS += -DSF_ENABLE_VFC
atmega328p-021: PFLAGS += -DSF_ENABLE_FRQ
atmega328p-021: PFLAGS += -DSF_ENABLE_SWC
atmega328p-021: PFLAGS += -DSF_ENABLE_MAL
atmega328p-021: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-021: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega328p-021: TARGET = atmega328p-021
atmega328p-021: $(TPATH)/atmega328p-021/pulsefire.hex
$(TPATH)/atmega328p-021/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-021-isp: atmega328p-021
atmega328p-021-isp: TARGET = atmega328p-021
atmega328p-021-isp: ISP_MCU = m328p
atmega328p-021-isp: ISP_PROG = arduino
atmega328p-021-isp: avr-flash



atmega328p-022: MCU = atmega328p
atmega328p-022: F_CPU = 16000000
atmega328p-022: CFLAGS += -mmcu=$(MCU)
atmega328p-022: PFLAGS += -DSF_ENABLE_LPM
atmega328p-022: PFLAGS += -DSF_ENABLE_ADC
atmega328p-022: PFLAGS += -DSF_ENABLE_DIC
atmega328p-022: PFLAGS += -DSF_ENABLE_DOC
atmega328p-022: PFLAGS += -DSF_ENABLE_DEV
atmega328p-022: PFLAGS += -DSF_ENABLE_PTC
atmega328p-022: PFLAGS += -DSF_ENABLE_PTT
atmega328p-022: PFLAGS += -DSF_ENABLE_STV
atmega328p-022: PFLAGS += -DSF_ENABLE_VFC
atmega328p-022: PFLAGS += -DSF_ENABLE_SWC
atmega328p-022: PFLAGS += -DSF_ENABLE_MAL
atmega328p-022: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-022: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega328p-022: TARGET = atmega328p-022
atmega328p-022: $(TPATH)/atmega328p-022/pulsefire.hex
$(TPATH)/atmega328p-022/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-022-isp: atmega328p-022
atmega328p-022-isp: TARGET = atmega328p-022
atmega328p-022-isp: ISP_MCU = m328p
atmega328p-022-isp: ISP_PROG = arduino
atmega328p-022-isp: avr-flash


atmega168p-001: MCU = atmega168p
atmega168p-001: F_CPU = 16000000
atmega168p-001: CFLAGS += -mmcu=$(MCU)
atmega168p-001: PFLAGS += -DSF_ENABLE_PWM
atmega168p-001: PFLAGS += -DSF_ENABLE_ADC
atmega168p-001: PFLAGS += -DSF_ENABLE_DIC
atmega168p-001: PFLAGS += -DSF_ENABLE_DOC
atmega168p-001: PFLAGS += -DSF_ENABLE_DEV
atmega168p-001: PFLAGS += -DSF_ENABLE_PTC
atmega168p-001: PFLAGS += -DSF_ENABLE_PTT
atmega168p-001: PFLAGS += -DSF_ENABLE_STV
atmega168p-001: PFLAGS += -DSF_ENABLE_VFC
atmega168p-001: PFLAGS += -DSF_ENABLE_SWC
atmega168p-001: TARGET = atmega168p-001
atmega168p-001: $(TPATH)/atmega168p-001/pulsefire.hex
$(TPATH)/atmega168p-001/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega168p-001-isp: atmega168p-001
atmega168p-001-isp: TARGET = atmega168p-001
atmega168p-001-isp: ISP_MCU = m168p
atmega168p-001-isp: ISP_PROG = arduino
atmega168p-001-isp: avr-flash

atmega168p-002: MCU = atmega168p
atmega168p-002: F_CPU = 16000000
atmega168p-002: CFLAGS += -mmcu=$(MCU)
atmega168p-002: PFLAGS += -DSF_ENABLE_PWM
atmega168p-002: PFLAGS += -DSF_ENABLE_PPM
atmega168p-002: PFLAGS += -DSF_ENABLE_ADC
atmega168p-002: PFLAGS += -DSF_ENABLE_DIC
atmega168p-002: PFLAGS += -DSF_ENABLE_DEV
atmega168p-002: PFLAGS += -DSF_ENABLE_PTC
atmega168p-002: PFLAGS += -DSF_ENABLE_STV
atmega168p-002: PFLAGS += -DSF_ENABLE_VFC
atmega168p-002: TARGET = atmega168p-002
atmega168p-002: $(TPATH)/atmega168p-002/pulsefire.hex
$(TPATH)/atmega168p-002/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega168p-002-isp: atmega168p-002
atmega168p-002-isp: TARGET = atmega168p-002
atmega168p-002-isp: ISP_MCU = m168p
atmega168p-002-isp: ISP_PROG = arduino
atmega168p-002-isp: avr-flash


atmega168p-003: MCU = atmega168p
atmega168p-003: F_CPU = 16000000
atmega168p-003: CFLAGS += -mmcu=$(MCU)
atmega168p-003: PFLAGS += -DSF_ENABLE_PWM
atmega168p-003: PFLAGS += -DSF_ENABLE_PPM
atmega168p-003: PFLAGS += -DSF_ENABLE_ADC
atmega168p-003: PFLAGS += -DSF_ENABLE_DIC
atmega168p-003: PFLAGS += -DSF_ENABLE_DEV
atmega168p-003: PFLAGS += -DSF_ENABLE_PTC
atmega168p-003: PFLAGS += -DSF_ENABLE_STV
atmega168p-003: PFLAGS += -DSF_ENABLE_VFC
atmega168p-003: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega168p-003: TARGET = atmega168p-003
atmega168p-003: $(TPATH)/atmega168p-003/pulsefire.hex
$(TPATH)/atmega168p-003/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega168p-003-isp: atmega168p-003
atmega168p-003-isp: TARGET = atmega168p-003
atmega168p-003-isp: ISP_MCU = m168p
atmega168p-003-isp: ISP_PROG = arduino
atmega168p-003-isp: avr-flash


atmega168p-004: MCU = atmega168p
atmega168p-004: F_CPU = 16000000
atmega168p-004: CFLAGS += -mmcu=$(MCU)
atmega168p-004: PFLAGS += -DSF_ENABLE_PWM
atmega168p-004: PFLAGS += -DSF_ENABLE_PPM
atmega168p-004: PFLAGS += -DSF_ENABLE_ADC
atmega168p-004: PFLAGS += -DSF_ENABLE_DIC
atmega168p-004: PFLAGS += -DSF_ENABLE_DEV
atmega168p-004: PFLAGS += -DSF_ENABLE_PTC
atmega168p-004: PFLAGS += -DSF_ENABLE_STV
atmega168p-004: PFLAGS += -DSF_ENABLE_VFC
atmega168p-004: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega168p-004: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega168p-004: TARGET = atmega168p-004
atmega168p-004: $(TPATH)/atmega168p-004/pulsefire.hex
$(TPATH)/atmega168p-004/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega168p-004-isp: atmega168p-004
atmega168p-004-isp: TARGET = atmega168p-004
atmega168p-004-isp: ISP_MCU = m168p
atmega168p-004-isp: ISP_PROG = arduino
atmega168p-004-isp: avr-flash

#
#====================================
#-------- 20 Mhz targets
#====================================
#

atmega328p-2001: MCU = atmega328p
atmega328p-2001: F_CPU = 20000000
atmega328p-2001: CFLAGS += -mmcu=$(MCU)
atmega328p-2001: PFLAGS += -DSF_ENABLE_PWM
atmega328p-2001: PFLAGS += -DSF_ENABLE_LPM
atmega328p-2001: PFLAGS += -DSF_ENABLE_PPM
atmega328p-2001: PFLAGS += -DSF_ENABLE_ADC
atmega328p-2001: PFLAGS += -DSF_ENABLE_DIC
atmega328p-2001: PFLAGS += -DSF_ENABLE_DOC
atmega328p-2001: PFLAGS += -DSF_ENABLE_DEV
atmega328p-2001: PFLAGS += -DSF_ENABLE_PTC
atmega328p-2001: PFLAGS += -DSF_ENABLE_PTT
atmega328p-2001: PFLAGS += -DSF_ENABLE_STV
atmega328p-2001: PFLAGS += -DSF_ENABLE_VFC
atmega328p-2001: PFLAGS += -DSF_ENABLE_SWC
atmega328p-2001: PFLAGS += -DSF_ENABLE_MAL
atmega328p-2001: TARGET = atmega328p-2001
atmega328p-2001: $(TPATH)/atmega328p-2001/pulsefire.hex
$(TPATH)/atmega328p-2001/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
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
atmega328p-2002: PFLAGS += -DSF_ENABLE_LCD
atmega328p-2002: PFLAGS += -DSF_ENABLE_LPM
atmega328p-2002: PFLAGS += -DSF_ENABLE_PPM
atmega328p-2002: PFLAGS += -DSF_ENABLE_ADC
atmega328p-2002: PFLAGS += -DSF_ENABLE_DIC
atmega328p-2002: PFLAGS += -DSF_ENABLE_DOC
atmega328p-2002: PFLAGS += -DSF_ENABLE_DEV
atmega328p-2002: PFLAGS += -DSF_ENABLE_PTC
atmega328p-2002: PFLAGS += -DSF_ENABLE_PTT
atmega328p-2002: PFLAGS += -DSF_ENABLE_STV
atmega328p-2002: PFLAGS += -DSF_ENABLE_VFC
atmega328p-2002: PFLAGS += -DSF_ENABLE_SWC
atmega328p-2002: PFLAGS += -DSF_ENABLE_MAL
atmega328p-2002: TARGET = atmega328p-2002
atmega328p-2002: $(TPATH)/atmega328p-2002/pulsefire.hex
$(TPATH)/atmega328p-2002/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
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
atmega328p-2003: PFLAGS += -DSF_ENABLE_LCD
atmega328p-2003: PFLAGS += -DSF_ENABLE_LPM
atmega328p-2003: PFLAGS += -DSF_ENABLE_PPM
atmega328p-2003: PFLAGS += -DSF_ENABLE_ADC
atmega328p-2003: PFLAGS += -DSF_ENABLE_DIC
atmega328p-2003: PFLAGS += -DSF_ENABLE_DOC
atmega328p-2003: PFLAGS += -DSF_ENABLE_DEV
atmega328p-2003: PFLAGS += -DSF_ENABLE_PTC
atmega328p-2003: PFLAGS += -DSF_ENABLE_PTT
atmega328p-2003: PFLAGS += -DSF_ENABLE_STV
atmega328p-2003: PFLAGS += -DSF_ENABLE_VFC
atmega328p-2003: PFLAGS += -DSF_ENABLE_SWC
atmega328p-2003: PFLAGS += -DSF_ENABLE_MAL
atmega328p-2003: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega328p-2003: TARGET = atmega328p-2003
atmega328p-2003: $(TPATH)/atmega328p-2003/pulsefire.hex
$(TPATH)/atmega328p-2003/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
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
atmega328p-2004: PFLAGS += -DSF_ENABLE_LCD
atmega328p-2004: PFLAGS += -DSF_ENABLE_LPM
atmega328p-2004: PFLAGS += -DSF_ENABLE_PPM
atmega328p-2004: PFLAGS += -DSF_ENABLE_ADC
atmega328p-2004: PFLAGS += -DSF_ENABLE_DIC
atmega328p-2004: PFLAGS += -DSF_ENABLE_DOC
atmega328p-2004: PFLAGS += -DSF_ENABLE_DEV
atmega328p-2004: PFLAGS += -DSF_ENABLE_PTC
atmega328p-2004: PFLAGS += -DSF_ENABLE_PTT
atmega328p-2004: PFLAGS += -DSF_ENABLE_STV
atmega328p-2004: PFLAGS += -DSF_ENABLE_VFC
atmega328p-2004: PFLAGS += -DSF_ENABLE_SWC
atmega328p-2004: PFLAGS += -DSF_ENABLE_MAL
atmega328p-2004: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-2004: TARGET = atmega328p-2004
atmega328p-2004: $(TPATH)/atmega328p-2004/pulsefire.hex
$(TPATH)/atmega328p-2004/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-2004-isp: atmega328p-2004
atmega328p-2004-isp: TARGET = atmega328p-2004
atmega328p-2004-isp: ISP_MCU = m328p
atmega328p-2004-isp: ISP_PROG = arduino
atmega328p-2004-isp: avr-flash

atmega328p-2005: MCU = atmega328p
atmega328p-2005: F_CPU = 20000000
atmega328p-2005: CFLAGS += -mmcu=$(MCU)
atmega328p-2005: PFLAGS += -DSF_ENABLE_PWM
atmega328p-2005: PFLAGS += -DSF_ENABLE_LCD
atmega328p-2005: PFLAGS += -DSF_ENABLE_LPM
atmega328p-2005: PFLAGS += -DSF_ENABLE_PPM
atmega328p-2005: PFLAGS += -DSF_ENABLE_ADC
atmega328p-2005: PFLAGS += -DSF_ENABLE_DIC
atmega328p-2005: PFLAGS += -DSF_ENABLE_DOC
atmega328p-2005: PFLAGS += -DSF_ENABLE_DEV
atmega328p-2005: PFLAGS += -DSF_ENABLE_PTC
atmega328p-2005: PFLAGS += -DSF_ENABLE_PTT
atmega328p-2005: PFLAGS += -DSF_ENABLE_STV
atmega328p-2005: PFLAGS += -DSF_ENABLE_VFC
atmega328p-2005: PFLAGS += -DSF_ENABLE_SWC
atmega328p-2005: PFLAGS += -DSF_ENABLE_MAL
atmega328p-2005: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-2005: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega328p-2005: TARGET = atmega328p-2005
atmega328p-2005: $(TPATH)/atmega328p-2005/pulsefire.hex
$(TPATH)/atmega328p-2005/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-2005-isp: atmega328p-2005
atmega328p-2005-isp: TARGET = atmega328p-2005
atmega328p-2005-isp: ISP_MCU = m328p
atmega328p-2005-isp: ISP_PROG = arduino
atmega328p-2005-isp: avr-flash

atmega328p-2006: MCU = atmega328p
atmega328p-2006: F_CPU = 20000000
atmega328p-2006: CFLAGS += -mmcu=$(MCU)
atmega328p-2006: PFLAGS += -DSF_ENABLE_PWM
atmega328p-2006: PFLAGS += -DSF_ENABLE_LCD
atmega328p-2006: PFLAGS += -DSF_ENABLE_LPM
atmega328p-2006: PFLAGS += -DSF_ENABLE_PPM
atmega328p-2006: PFLAGS += -DSF_ENABLE_ADC
atmega328p-2006: PFLAGS += -DSF_ENABLE_DIC
atmega328p-2006: PFLAGS += -DSF_ENABLE_DOC
atmega328p-2006: PFLAGS += -DSF_ENABLE_DEV
atmega328p-2006: PFLAGS += -DSF_ENABLE_PTC
atmega328p-2006: PFLAGS += -DSF_ENABLE_PTT
atmega328p-2006: PFLAGS += -DSF_ENABLE_STV
atmega328p-2006: PFLAGS += -DSF_ENABLE_VFC
atmega328p-2006: PFLAGS += -DSF_ENABLE_SWC
atmega328p-2006: PFLAGS += -DSF_ENABLE_MAL
atmega328p-2006: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-2006: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega328p-2006: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega328p-2006: TARGET = atmega328p-2006
atmega328p-2006: $(TPATH)/atmega328p-2006/pulsefire.hex
$(TPATH)/atmega328p-2006/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-2006-isp: atmega328p-2006
atmega328p-2006-isp: TARGET = atmega328p-2006
atmega328p-2006-isp: ISP_MCU = m328p
atmega328p-2006-isp: ISP_PROG = arduino
atmega328p-2006-isp: avr-flash

atmega328p-2007: MCU = atmega328p
atmega328p-2007: F_CPU = 20000000
atmega328p-2007: CFLAGS += -mmcu=$(MCU)
atmega328p-2007: PFLAGS += -DSF_ENABLE_PWM
atmega328p-2007: PFLAGS += -DSF_ENABLE_LCD
atmega328p-2007: PFLAGS += -DSF_ENABLE_LPM
atmega328p-2007: PFLAGS += -DSF_ENABLE_PPM
atmega328p-2007: PFLAGS += -DSF_ENABLE_ADC
atmega328p-2007: PFLAGS += -DSF_ENABLE_DIC
atmega328p-2007: PFLAGS += -DSF_ENABLE_DOC
atmega328p-2007: PFLAGS += -DSF_ENABLE_DEV
atmega328p-2007: PFLAGS += -DSF_ENABLE_PTC
atmega328p-2007: PFLAGS += -DSF_ENABLE_PTT
atmega328p-2007: PFLAGS += -DSF_ENABLE_STV
atmega328p-2007: PFLAGS += -DSF_ENABLE_VFC
atmega328p-2007: PFLAGS += -DSF_ENABLE_SWC
atmega328p-2007: PFLAGS += -DSF_ENABLE_MAL
atmega328p-2007: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-2007: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega328p-2007: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega328p-2007: PFLAGS += -DSF_ENABLE_EXT_LCD_DIC
atmega328p-2007: TARGET = atmega328p-2007
atmega328p-2007: $(TPATH)/atmega328p-2007/pulsefire.hex
$(TPATH)/atmega328p-2007/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-2007-isp: atmega328p-2007
atmega328p-2007-isp: TARGET = atmega328p-2007
atmega328p-2007-isp: ISP_MCU = m328p
atmega328p-2007-isp: ISP_PROG = arduino
atmega328p-2007-isp: avr-flash

atmega328p-2008: MCU = atmega328p
atmega328p-2008: F_CPU = 20000000
atmega328p-2008: CFLAGS += -mmcu=$(MCU)
atmega328p-2008: PFLAGS += -DSF_ENABLE_PWM
atmega328p-2008: PFLAGS += -DSF_ENABLE_LCD
atmega328p-2008: PFLAGS += -DSF_ENABLE_LPM
atmega328p-2008: PFLAGS += -DSF_ENABLE_PPM
atmega328p-2008: PFLAGS += -DSF_ENABLE_ADC
atmega328p-2008: PFLAGS += -DSF_ENABLE_DIC
atmega328p-2008: PFLAGS += -DSF_ENABLE_DOC
atmega328p-2008: PFLAGS += -DSF_ENABLE_DEV
atmega328p-2008: PFLAGS += -DSF_ENABLE_PTC
atmega328p-2008: PFLAGS += -DSF_ENABLE_PTT
atmega328p-2008: PFLAGS += -DSF_ENABLE_STV
atmega328p-2008: PFLAGS += -DSF_ENABLE_VFC
atmega328p-2008: PFLAGS += -DSF_ENABLE_SWC
atmega328p-2008: PFLAGS += -DSF_ENABLE_MAL
atmega328p-2008: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-2008: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega328p-2008: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega328p-2008: PFLAGS += -DSF_ENABLE_EXT_LCD_DIC
atmega328p-2008: PFLAGS += -DSF_ENABLE_EXT_LCD_DOC
atmega328p-2008: TARGET = atmega328p-2008
atmega328p-2008: $(TPATH)/atmega328p-2008/pulsefire.hex
$(TPATH)/atmega328p-2008/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-2008-isp: atmega328p-2008
atmega328p-2008-isp: TARGET = atmega328p-2008
atmega328p-2008-isp: ISP_MCU = m328p
atmega328p-2008-isp: ISP_PROG = arduino
atmega328p-2008-isp: avr-flash


atmega168p-2001: MCU = atmega168p
atmega168p-2001: F_CPU = 20000000
atmega168p-2001: CFLAGS += -mmcu=$(MCU)
atmega168p-2001: PFLAGS += -DSF_ENABLE_PWM
atmega168p-2001: PFLAGS += -DSF_ENABLE_ADC
atmega168p-2001: PFLAGS += -DSF_ENABLE_DIC
atmega168p-2001: PFLAGS += -DSF_ENABLE_DOC
atmega168p-2001: PFLAGS += -DSF_ENABLE_DEV
atmega168p-2001: PFLAGS += -DSF_ENABLE_PTC
atmega168p-2001: PFLAGS += -DSF_ENABLE_PTT
atmega168p-2001: PFLAGS += -DSF_ENABLE_STV
atmega168p-2001: PFLAGS += -DSF_ENABLE_VFC
atmega168p-2001: PFLAGS += -DSF_ENABLE_SWC
atmega168p-2001: TARGET = atmega168p-2001
atmega168p-2001: $(TPATH)/atmega168p-2001/pulsefire.hex
$(TPATH)/atmega168p-2001/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega168p-2001-isp: atmega168p-2001
atmega168p-2001-isp: TARGET = atmega168p-2001
atmega168p-2001-isp: ISP_MCU = m168p
atmega168p-2001-isp: ISP_PROG = arduino
atmega168p-2001-isp: avr-flash

atmega168p-2002: MCU = atmega168p
atmega168p-2002: F_CPU = 20000000
atmega168p-2002: CFLAGS += -mmcu=$(MCU)
atmega168p-2002: PFLAGS += -DSF_ENABLE_PWM
atmega168p-2002: PFLAGS += -DSF_ENABLE_PPM
atmega168p-2002: PFLAGS += -DSF_ENABLE_ADC
atmega168p-2002: PFLAGS += -DSF_ENABLE_DIC
atmega168p-2002: PFLAGS += -DSF_ENABLE_DEV
atmega168p-2002: PFLAGS += -DSF_ENABLE_PTC
atmega168p-2002: PFLAGS += -DSF_ENABLE_STV
atmega168p-2002: PFLAGS += -DSF_ENABLE_VFC
atmega168p-2002: TARGET = atmega168p-2002
atmega168p-2002: $(TPATH)/atmega168p-2002/pulsefire.hex
$(TPATH)/atmega168p-2002/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega168p-2002-isp: atmega168p-2002
atmega168p-2002-isp: TARGET = atmega168p-2002
atmega168p-2002-isp: ISP_MCU = m168p
atmega168p-2002-isp: ISP_PROG = arduino
atmega168p-2002-isp: avr-flash

#
#====================================
#-------- 8 Mhz targets
#====================================
#

atmega328p-801: MCU = atmega328p
atmega328p-801: F_CPU = 8000000
atmega328p-801: CFLAGS += -mmcu=$(MCU)
atmega328p-801: PFLAGS += -DSF_ENABLE_PWM
atmega328p-801: PFLAGS += -DSF_ENABLE_LPM
atmega328p-801: PFLAGS += -DSF_ENABLE_PPM
atmega328p-801: PFLAGS += -DSF_ENABLE_ADC
atmega328p-801: PFLAGS += -DSF_ENABLE_DIC
atmega328p-801: PFLAGS += -DSF_ENABLE_DOC
atmega328p-801: PFLAGS += -DSF_ENABLE_DEV
atmega328p-801: PFLAGS += -DSF_ENABLE_PTC
atmega328p-801: PFLAGS += -DSF_ENABLE_PTT
atmega328p-801: PFLAGS += -DSF_ENABLE_STV
atmega328p-801: PFLAGS += -DSF_ENABLE_VFC
atmega328p-801: PFLAGS += -DSF_ENABLE_SWC
atmega328p-801: PFLAGS += -DSF_ENABLE_MAL
atmega328p-801: TARGET = atmega328p-801
atmega328p-801: $(TPATH)/atmega328p-801/pulsefire.hex
$(TPATH)/atmega328p-801/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-801-isp: atmega328p-801
atmega328p-801-isp: TARGET = atmega328p-801
atmega328p-801-isp: ISP_MCU = m328p
atmega328p-801-isp: ISP_PROG = arduino
atmega328p-801-isp: avr-flash


atmega328p-802: MCU = atmega328p
atmega328p-802: F_CPU = 8000000
atmega328p-802: CFLAGS += -mmcu=$(MCU)
atmega328p-802: PFLAGS += -DSF_ENABLE_PWM
atmega328p-802: PFLAGS += -DSF_ENABLE_LPM
atmega328p-802: PFLAGS += -DSF_ENABLE_PPM
atmega328p-802: PFLAGS += -DSF_ENABLE_ADC
atmega328p-802: PFLAGS += -DSF_ENABLE_DIC
atmega328p-802: PFLAGS += -DSF_ENABLE_DOC
atmega328p-802: PFLAGS += -DSF_ENABLE_DEV
atmega328p-802: PFLAGS += -DSF_ENABLE_PTC
atmega328p-802: PFLAGS += -DSF_ENABLE_PTT
atmega328p-802: PFLAGS += -DSF_ENABLE_STV
atmega328p-802: PFLAGS += -DSF_ENABLE_VFC
atmega328p-802: PFLAGS += -DSF_ENABLE_SWC
atmega328p-802: PFLAGS += -DSF_ENABLE_MAL
atmega328p-802: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-802: TARGET = atmega328p-802
atmega328p-802: $(TPATH)/atmega328p-802/pulsefire.hex
$(TPATH)/atmega328p-802/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega328p-802-isp: atmega328p-802
atmega328p-802-isp: TARGET = atmega328p-802
atmega328p-802-isp: ISP_MCU = m328p
atmega328p-802-isp: ISP_PROG = arduino
atmega328p-802-isp: avr-flash


atmega328p-803: MCU = atmega328p
atmega328p-803: F_CPU = 8000000
atmega328p-803: CFLAGS += -mmcu=$(MCU)
atmega328p-803: PFLAGS += -DSF_ENABLE_PWM
atmega328p-803: PFLAGS += -DSF_ENABLE_LPM
atmega328p-803: PFLAGS += -DSF_ENABLE_PPM
atmega328p-803: PFLAGS += -DSF_ENABLE_ADC
atmega328p-803: PFLAGS += -DSF_ENABLE_DIC
atmega328p-803: PFLAGS += -DSF_ENABLE_DOC
atmega328p-803: PFLAGS += -DSF_ENABLE_DEV
atmega328p-803: PFLAGS += -DSF_ENABLE_PTC
atmega328p-803: PFLAGS += -DSF_ENABLE_PTT
atmega328p-803: PFLAGS += -DSF_ENABLE_STV
atmega328p-803: PFLAGS += -DSF_ENABLE_VFC
atmega328p-803: PFLAGS += -DSF_ENABLE_SWC
atmega328p-803: PFLAGS += -DSF_ENABLE_MAL
atmega328p-803: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-803: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega328p-803: TARGET = atmega328p-803
atmega328p-803: $(TPATH)/atmega328p-803/pulsefire.hex
$(TPATH)/atmega328p-803/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex
	
atmega328p-803-isp: atmega328p-803
atmega328p-803-isp: TARGET = atmega328p-803
atmega328p-803-isp: ISP_MCU = m328p
atmega328p-803-isp: ISP_PROG = arduino
atmega328p-803-isp: avr-flash


atmega328p-804: MCU = atmega328p
atmega328p-804: F_CPU = 8000000
atmega328p-804: CFLAGS += -mmcu=$(MCU)
atmega328p-804: PFLAGS += -DSF_ENABLE_PWM
atmega328p-804: PFLAGS += -DSF_ENABLE_LCD
atmega328p-804: PFLAGS += -DSF_ENABLE_LPM
atmega328p-804: PFLAGS += -DSF_ENABLE_PPM
atmega328p-804: PFLAGS += -DSF_ENABLE_ADC
atmega328p-804: PFLAGS += -DSF_ENABLE_DIC
atmega328p-804: PFLAGS += -DSF_ENABLE_DOC
atmega328p-804: PFLAGS += -DSF_ENABLE_DEV
atmega328p-804: PFLAGS += -DSF_ENABLE_PTC
atmega328p-804: PFLAGS += -DSF_ENABLE_PTT
atmega328p-804: PFLAGS += -DSF_ENABLE_STV
atmega328p-804: PFLAGS += -DSF_ENABLE_VFC
atmega328p-804: PFLAGS += -DSF_ENABLE_SWC
atmega328p-804: PFLAGS += -DSF_ENABLE_MAL
atmega328p-804: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega328p-804: TARGET = atmega328p-804
atmega328p-804: $(TPATH)/atmega328p-804/pulsefire.hex
$(TPATH)/atmega328p-804/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex
	
atmega328p-804-isp: atmega328p-804
atmega328p-804-isp: TARGET = atmega328p-804
atmega328p-804-isp: ISP_MCU = m328p
atmega328p-804-isp: ISP_PROG = arduino
atmega328p-804-isp: avr-flash

atmega328p-805: MCU = atmega328p
atmega328p-805: F_CPU = 8000000
atmega328p-805: CFLAGS += -mmcu=$(MCU)
atmega328p-805: PFLAGS += -DSF_ENABLE_LCD
atmega328p-805: PFLAGS += -DSF_ENABLE_LPM
atmega328p-805: PFLAGS += -DSF_ENABLE_PPM
atmega328p-805: PFLAGS += -DSF_ENABLE_ADC
atmega328p-805: PFLAGS += -DSF_ENABLE_DIC
atmega328p-805: PFLAGS += -DSF_ENABLE_DOC
atmega328p-805: PFLAGS += -DSF_ENABLE_DEV
atmega328p-805: PFLAGS += -DSF_ENABLE_PTC
atmega328p-805: PFLAGS += -DSF_ENABLE_PTT
atmega328p-805: PFLAGS += -DSF_ENABLE_STV
atmega328p-805: PFLAGS += -DSF_ENABLE_VFC
atmega328p-805: PFLAGS += -DSF_ENABLE_SWC
atmega328p-805: PFLAGS += -DSF_ENABLE_MAL
atmega328p-805: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-805: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega328p-805: TARGET = atmega328p-805
atmega328p-805: $(TPATH)/atmega328p-805/pulsefire.hex
$(TPATH)/atmega328p-805/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex
	
atmega328p-805-isp: atmega328p-805
atmega328p-805-isp: TARGET = atmega328p-805
atmega328p-805-isp: ISP_MCU = m328p
atmega328p-805-isp: ISP_PROG = arduino
atmega328p-805-isp: avr-flash


atmega328p-806: MCU = atmega328p
atmega328p-806: F_CPU = 8000000
atmega328p-806: CFLAGS += -mmcu=$(MCU)
atmega328p-806: PFLAGS += -DSF_ENABLE_LCD
atmega328p-806: PFLAGS += -DSF_ENABLE_LPM
atmega328p-806: PFLAGS += -DSF_ENABLE_PPM
atmega328p-806: PFLAGS += -DSF_ENABLE_ADC
atmega328p-806: PFLAGS += -DSF_ENABLE_DIC
atmega328p-806: PFLAGS += -DSF_ENABLE_DOC
atmega328p-806: PFLAGS += -DSF_ENABLE_DEV
atmega328p-806: PFLAGS += -DSF_ENABLE_PTC
atmega328p-806: PFLAGS += -DSF_ENABLE_PTT
atmega328p-806: PFLAGS += -DSF_ENABLE_STV
atmega328p-806: PFLAGS += -DSF_ENABLE_VFC
atmega328p-806: PFLAGS += -DSF_ENABLE_SWC
atmega328p-806: PFLAGS += -DSF_ENABLE_MAL
atmega328p-806: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-806: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega328p-806: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega328p-806: TARGET = atmega328p-806
atmega328p-806: $(TPATH)/atmega328p-806/pulsefire.hex
$(TPATH)/atmega328p-806/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex
	
atmega328p-806-isp: atmega328p-806
atmega328p-806-isp: TARGET = atmega328p-806
atmega328p-806-isp: ISP_MCU = m328p
atmega328p-806-isp: ISP_PROG = arduino
atmega328p-806-isp: avr-flash


atmega328p-807: MCU = atmega328p
atmega328p-807: F_CPU = 8000000
atmega328p-807: CFLAGS += -mmcu=$(MCU)
atmega328p-807: PFLAGS += -DSF_ENABLE_LCD
atmega328p-807: PFLAGS += -DSF_ENABLE_LPM
atmega328p-807: PFLAGS += -DSF_ENABLE_PPM
atmega328p-807: PFLAGS += -DSF_ENABLE_ADC
atmega328p-807: PFLAGS += -DSF_ENABLE_DIC
atmega328p-807: PFLAGS += -DSF_ENABLE_DOC
atmega328p-807: PFLAGS += -DSF_ENABLE_DEV
atmega328p-807: PFLAGS += -DSF_ENABLE_PTC
atmega328p-807: PFLAGS += -DSF_ENABLE_PTT
atmega328p-807: PFLAGS += -DSF_ENABLE_STV
atmega328p-807: PFLAGS += -DSF_ENABLE_VFC
atmega328p-807: PFLAGS += -DSF_ENABLE_SWC
atmega328p-807: PFLAGS += -DSF_ENABLE_MAL
atmega328p-807: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-807: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega328p-807: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega328p-807: PFLAGS += -DSF_ENABLE_EXT_LCD_DIC
atmega328p-807: TARGET = atmega328p-807
atmega328p-807: $(TPATH)/atmega328p-807/pulsefire.hex
$(TPATH)/atmega328p-807/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex
	
atmega328p-807-isp: atmega328p-807
atmega328p-807-isp: TARGET = atmega328p-807
atmega328p-807-isp: ISP_MCU = m328p
atmega328p-807-isp: ISP_PROG = arduino
atmega328p-807-isp: avr-flash

atmega328p-808: MCU = atmega328p
atmega328p-808: F_CPU = 8000000
atmega328p-808: CFLAGS += -mmcu=$(MCU)
atmega328p-808: PFLAGS += -DSF_ENABLE_LCD
atmega328p-808: PFLAGS += -DSF_ENABLE_LPM
atmega328p-808: PFLAGS += -DSF_ENABLE_PPM
atmega328p-808: PFLAGS += -DSF_ENABLE_ADC
atmega328p-808: PFLAGS += -DSF_ENABLE_DIC
atmega328p-808: PFLAGS += -DSF_ENABLE_DOC
atmega328p-808: PFLAGS += -DSF_ENABLE_DEV
atmega328p-808: PFLAGS += -DSF_ENABLE_PTC
atmega328p-808: PFLAGS += -DSF_ENABLE_PTT
atmega328p-808: PFLAGS += -DSF_ENABLE_STV
atmega328p-808: PFLAGS += -DSF_ENABLE_VFC
atmega328p-808: PFLAGS += -DSF_ENABLE_SWC
atmega328p-808: PFLAGS += -DSF_ENABLE_MAL
atmega328p-808: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega328p-808: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega328p-808: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega328p-808: PFLAGS += -DSF_ENABLE_EXT_LCD_DIC
atmega328p-808: PFLAGS += -DSF_ENABLE_EXT_LCD_DOC
atmega328p-808: TARGET = atmega328p-808
atmega328p-808: $(TPATH)/atmega328p-808/pulsefire.hex
$(TPATH)/atmega328p-808/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex
	
atmega328p-808-isp: atmega328p-808
atmega328p-808-isp: TARGET = atmega328p-808
atmega328p-808-isp: ISP_MCU = m328p
atmega328p-808-isp: ISP_PROG = arduino
atmega328p-808-isp: avr-flash


atmega168p-801: MCU = atmega168p
atmega168p-801: F_CPU = 8000000
atmega168p-801: CFLAGS += -mmcu=$(MCU)
atmega168p-801: PFLAGS += -DSF_ENABLE_PWM
atmega168p-801: PFLAGS += -DSF_ENABLE_ADC
atmega168p-801: PFLAGS += -DSF_ENABLE_DIC
atmega168p-801: PFLAGS += -DSF_ENABLE_DOC
atmega168p-801: PFLAGS += -DSF_ENABLE_DEV
atmega168p-801: PFLAGS += -DSF_ENABLE_PTC
atmega168p-801: PFLAGS += -DSF_ENABLE_PTT
atmega168p-801: PFLAGS += -DSF_ENABLE_STV
atmega168p-801: PFLAGS += -DSF_ENABLE_VFC
atmega168p-801: PFLAGS += -DSF_ENABLE_SWC
atmega168p-801: TARGET = atmega168p-801
atmega168p-801: $(TPATH)/atmega168p-801/pulsefire.hex
$(TPATH)/atmega168p-801/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega168p-801-isp: atmega168p-801
atmega168p-801-isp: TARGET = atmega168p-801
atmega168p-801-isp: ISP_MCU = m168p
atmega168p-801-isp: ISP_PROG = arduino
atmega168p-801-isp: avr-flash

atmega1280-1601: MCU = atmega1280
atmega1280-1601: F_CPU = 16000000
atmega1280-1601: CFLAGS += -mmcu=$(MCU)
atmega1280-1601: PFLAGS += -DSF_ENABLE_LCD
atmega1280-1601: PFLAGS += -DSF_ENABLE_LPM
atmega1280-1601: PFLAGS += -DSF_ENABLE_PPM
atmega1280-1601: PFLAGS += -DSF_ENABLE_PWM
atmega1280-1601: PFLAGS += -DSF_ENABLE_ADC
atmega1280-1601: PFLAGS += -DSF_ENABLE_DIC
atmega1280-1601: PFLAGS += -DSF_ENABLE_DOC
atmega1280-1601: PFLAGS += -DSF_ENABLE_DEV
atmega1280-1601: PFLAGS += -DSF_ENABLE_PTC
atmega1280-1601: PFLAGS += -DSF_ENABLE_PTT
atmega1280-1601: PFLAGS += -DSF_ENABLE_STV
atmega1280-1601: PFLAGS += -DSF_ENABLE_VFC
atmega1280-1601: PFLAGS += -DSF_ENABLE_SWC
atmega1280-1601: PFLAGS += -DSF_ENABLE_MAL
atmega1280-1601: TARGET = atmega1280-1601
atmega1280-1601: $(TPATH)/atmega1280-1601/pulsefire.hex
$(TPATH)/atmega1280-1601/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega1280-1601-isp: atmega1280-1601
atmega1280-1601-isp: TARGET = atmega1280-1601
atmega1280-1601-isp: ISP_MCU = m1280
atmega1280-1601-isp: ISP_PROG = stk500v2
atmega1280-1601-isp: avr-flash

atmega1280-1602: MCU = atmega1280
atmega1280-1602: F_CPU = 16000000
atmega1280-1602: CFLAGS += -mmcu=$(MCU)
atmega1280-1602: PFLAGS += -DSF_ENABLE_LPM
atmega1280-1602: PFLAGS += -DSF_ENABLE_PPM
atmega1280-1602: PFLAGS += -DSF_ENABLE_PWM
atmega1280-1602: PFLAGS += -DSF_ENABLE_ADC
atmega1280-1602: PFLAGS += -DSF_ENABLE_DIC
atmega1280-1602: PFLAGS += -DSF_ENABLE_DOC
atmega1280-1602: PFLAGS += -DSF_ENABLE_DEV
atmega1280-1602: PFLAGS += -DSF_ENABLE_PTC
atmega1280-1602: PFLAGS += -DSF_ENABLE_PTT
atmega1280-1602: PFLAGS += -DSF_ENABLE_STV
atmega1280-1602: PFLAGS += -DSF_ENABLE_VFC
atmega1280-1602: PFLAGS += -DSF_ENABLE_SWC
atmega1280-1602: PFLAGS += -DSF_ENABLE_MAL
atmega1280-1602: TARGET = atmega1280-1602
atmega1280-1602: $(TPATH)/atmega1280-1602/pulsefire.hex
$(TPATH)/atmega1280-1602/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega1280-1602-isp: atmega1280-1602
atmega1280-1602-isp: TARGET = atmega1280-1602
atmega1280-1602-isp: ISP_MCU = m1280
atmega1280-1602-isp: ISP_PROG = stk500v2
atmega1280-1602-isp: avr-flash


atmega1280-1603: MCU = atmega1280
atmega1280-1603: F_CPU = 16000000
atmega1280-1603: CFLAGS += -mmcu=$(MCU)
atmega1280-1603: PFLAGS += -DSF_ENABLE_LCD
atmega1280-1603: PFLAGS += -DSF_ENABLE_LPM
atmega1280-1603: PFLAGS += -DSF_ENABLE_PPM
atmega1280-1603: PFLAGS += -DSF_ENABLE_PWM
atmega1280-1603: PFLAGS += -DSF_ENABLE_ADC
atmega1280-1603: PFLAGS += -DSF_ENABLE_DIC
atmega1280-1603: PFLAGS += -DSF_ENABLE_DOC
atmega1280-1603: PFLAGS += -DSF_ENABLE_DEV
atmega1280-1603: PFLAGS += -DSF_ENABLE_PTC
atmega1280-1603: PFLAGS += -DSF_ENABLE_PTT
atmega1280-1603: PFLAGS += -DSF_ENABLE_STV
atmega1280-1603: PFLAGS += -DSF_ENABLE_VFC
atmega1280-1603: PFLAGS += -DSF_ENABLE_SWC
atmega1280-1603: PFLAGS += -DSF_ENABLE_MAL
atmega1280-1603: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega1280-1603: TARGET = atmega1280-1603
atmega1280-1603: $(TPATH)/atmega1280-1603/pulsefire.hex
$(TPATH)/atmega1280-1603/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega1280-1603-isp: atmega1280-1603
atmega1280-1603-isp: TARGET = atmega1280-1603
atmega1280-1603-isp: ISP_MCU = m1280
atmega1280-1603-isp: ISP_PROG = stk500v2
atmega1280-1603-isp: avr-flash


atmega1280-1604: MCU = atmega1280
atmega1280-1604: F_CPU = 16000000
atmega1280-1604: CFLAGS += -mmcu=$(MCU)
atmega1280-1604: PFLAGS += -DSF_ENABLE_LCD
atmega1280-1604: PFLAGS += -DSF_ENABLE_LPM
atmega1280-1604: PFLAGS += -DSF_ENABLE_PPM
atmega1280-1604: PFLAGS += -DSF_ENABLE_PWM
atmega1280-1604: PFLAGS += -DSF_ENABLE_ADC
atmega1280-1604: PFLAGS += -DSF_ENABLE_DIC
atmega1280-1604: PFLAGS += -DSF_ENABLE_DOC
atmega1280-1604: PFLAGS += -DSF_ENABLE_DEV
atmega1280-1604: PFLAGS += -DSF_ENABLE_PTC
atmega1280-1604: PFLAGS += -DSF_ENABLE_PTT
atmega1280-1604: PFLAGS += -DSF_ENABLE_STV
atmega1280-1604: PFLAGS += -DSF_ENABLE_VFC
atmega1280-1604: PFLAGS += -DSF_ENABLE_SWC
atmega1280-1604: PFLAGS += -DSF_ENABLE_MAL
atmega1280-1604: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega1280-1604: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega1280-1604: TARGET = atmega1280-1603
atmega1280-1604: $(TPATH)/atmega1280-1603/pulsefire.hex
$(TPATH)/atmega1280-1603/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega1280-1604-isp: atmega1280-1603
atmega1280-1604-isp: TARGET = atmega1280-1603
atmega1280-1604-isp: ISP_MCU = m1280
atmega1280-1604-isp: ISP_PROG = stk500v2
atmega1280-1604-isp: avr-flash


atmega1280-1605: MCU = atmega1280
atmega1280-1605: F_CPU = 16000000
atmega1280-1605: CFLAGS += -mmcu=$(MCU)
atmega1280-1605: PFLAGS += -DSF_ENABLE_LCD
atmega1280-1605: PFLAGS += -DSF_ENABLE_LPM
atmega1280-1605: PFLAGS += -DSF_ENABLE_PPM
atmega1280-1605: PFLAGS += -DSF_ENABLE_PWM
atmega1280-1605: PFLAGS += -DSF_ENABLE_ADC
atmega1280-1605: PFLAGS += -DSF_ENABLE_DIC
atmega1280-1605: PFLAGS += -DSF_ENABLE_DOC
atmega1280-1605: PFLAGS += -DSF_ENABLE_DEV
atmega1280-1605: PFLAGS += -DSF_ENABLE_PTC
atmega1280-1605: PFLAGS += -DSF_ENABLE_PTT
atmega1280-1605: PFLAGS += -DSF_ENABLE_STV
atmega1280-1605: PFLAGS += -DSF_ENABLE_VFC
atmega1280-1605: PFLAGS += -DSF_ENABLE_SWC
atmega1280-1605: PFLAGS += -DSF_ENABLE_MAL
atmega1280-1605: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega1280-1605: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega1280-1605: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega1280-1605: TARGET = atmega1280-1605
atmega1280-1605: $(TPATH)/atmega1280-1605/pulsefire.hex
$(TPATH)/atmega1280-1605/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega1280-1605-isp: atmega1280-1605
atmega1280-1605-isp: TARGET = atmega1280-1605
atmega1280-1605-isp: ISP_MCU = m1280
atmega1280-1605-isp: ISP_PROG = stk500v2
atmega1280-1605-isp: avr-flash


atmega1280-1606: MCU = atmega1280
atmega1280-1606: F_CPU = 16000000
atmega1280-1606: CFLAGS += -mmcu=$(MCU)
atmega1280-1606: PFLAGS += -DSF_ENABLE_LCD
atmega1280-1606: PFLAGS += -DSF_ENABLE_LPM
atmega1280-1606: PFLAGS += -DSF_ENABLE_PPM
atmega1280-1606: PFLAGS += -DSF_ENABLE_PWM
atmega1280-1606: PFLAGS += -DSF_ENABLE_ADC
atmega1280-1606: PFLAGS += -DSF_ENABLE_DIC
atmega1280-1606: PFLAGS += -DSF_ENABLE_DOC
atmega1280-1606: PFLAGS += -DSF_ENABLE_DEV
atmega1280-1606: PFLAGS += -DSF_ENABLE_PTC
atmega1280-1606: PFLAGS += -DSF_ENABLE_PTT
atmega1280-1606: PFLAGS += -DSF_ENABLE_STV
atmega1280-1606: PFLAGS += -DSF_ENABLE_VFC
atmega1280-1606: PFLAGS += -DSF_ENABLE_SWC
atmega1280-1606: PFLAGS += -DSF_ENABLE_MAL
atmega1280-1606: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega1280-1606: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega1280-1606: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega1280-1606: PFLAGS += -DSF_ENABLE_EXT_LCD_DOC
atmega1280-1606: TARGET = atmega1280-1606
atmega1280-1606: $(TPATH)/atmega1280-1606/pulsefire.hex
$(TPATH)/atmega1280-1606/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega1280-1606-isp: atmega1280-1606
atmega1280-1606-isp: TARGET = atmega1280-1606
atmega1280-1606-isp: ISP_MCU = m1280
atmega1280-1606-isp: ISP_PROG = stk500v2
atmega1280-1606-isp: avr-flash


atmega1280-1607: MCU = atmega1280
atmega1280-1607: F_CPU = 16000000
atmega1280-1607: CFLAGS += -mmcu=$(MCU)
atmega1280-1607: PFLAGS += -DSF_ENABLE_LCD
atmega1280-1607: PFLAGS += -DSF_ENABLE_LPM
atmega1280-1607: PFLAGS += -DSF_ENABLE_PPM
atmega1280-1607: PFLAGS += -DSF_ENABLE_PWM
atmega1280-1607: PFLAGS += -DSF_ENABLE_ADC
atmega1280-1607: PFLAGS += -DSF_ENABLE_DIC
atmega1280-1607: PFLAGS += -DSF_ENABLE_DOC
atmega1280-1607: PFLAGS += -DSF_ENABLE_DEV
atmega1280-1607: PFLAGS += -DSF_ENABLE_PTC
atmega1280-1607: PFLAGS += -DSF_ENABLE_PTT
atmega1280-1607: PFLAGS += -DSF_ENABLE_STV
atmega1280-1607: PFLAGS += -DSF_ENABLE_VFC
atmega1280-1607: PFLAGS += -DSF_ENABLE_SWC
atmega1280-1607: PFLAGS += -DSF_ENABLE_MAL
atmega1280-1607: PFLAGS += -DSF_ENABLE_EXT_OUT
atmega1280-1607: PFLAGS += -DSF_ENABLE_EXT_OUT_16BIT
atmega1280-1607: PFLAGS += -DSF_ENABLE_EXT_LCD
atmega1280-1607: PFLAGS += -DSF_ENABLE_EXT_LCD_DOC
atmega1280-1607: PFLAGS += -DSF_ENABLE_EXT_LCD_DOC_16BIT
atmega1280-1607: TARGET = atmega1280-1607
atmega1280-1607: $(TPATH)/atmega1280-1607/pulsefire.hex
$(TPATH)/atmega1280-1607/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega1280-1607-isp: atmega1280-1607
atmega1280-1607-isp: TARGET = atmega1280-1607
atmega1280-1607-isp: ISP_MCU = m1280
atmega1280-1607-isp: ISP_PROG = stk500v2
atmega1280-1607-isp: avr-flash


atmega1280-1608: MCU = atmega1280
atmega1280-1608: F_CPU = 16000000
atmega1280-1608: CFLAGS += -mmcu=$(MCU)
atmega1280-1608: PFLAGS += -DSF_ENABLE_LCD
atmega1280-1608: PFLAGS += -DSF_ENABLE_GLCD
atmega1280-1608: PFLAGS += -DSF_ENABLE_LPM
atmega1280-1608: PFLAGS += -DSF_ENABLE_PPM
atmega1280-1608: PFLAGS += -DSF_ENABLE_PWM
atmega1280-1608: PFLAGS += -DSF_ENABLE_ADC
atmega1280-1608: PFLAGS += -DSF_ENABLE_DIC
atmega1280-1608: PFLAGS += -DSF_ENABLE_DOC
atmega1280-1608: PFLAGS += -DSF_ENABLE_DEV
atmega1280-1608: PFLAGS += -DSF_ENABLE_PTC
atmega1280-1608: PFLAGS += -DSF_ENABLE_PTT
atmega1280-1608: PFLAGS += -DSF_ENABLE_STV
atmega1280-1608: PFLAGS += -DSF_ENABLE_VFC
atmega1280-1608: PFLAGS += -DSF_ENABLE_SWC
atmega1280-1608: PFLAGS += -DSF_ENABLE_MAL
atmega1280-1608: TARGET = atmega1280-1608
atmega1280-1608: $(TPATH)/atmega1280-1608/pulsefire.hex
$(TPATH)/atmega1280-1608/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
	$(OBJCOPY) -O ihex -R .eeprom $(TPATH)/$(TARGET)/$(PF).elf $(TPATH)/$(TARGET)/$(PF).hex

atmega1280-1608-isp: atmega1280-1608
atmega1280-1608-isp: TARGET = atmega1280-1608
atmega1280-1608-isp: ISP_MCU = m1280
atmega1280-1608-isp: ISP_PROG = stk500v2
atmega1280-1608-isp: avr-flash


arm-lpc1343-7201: MCU = cortex-m3
arm-lpc1343-7201: F_CPU = 72000000
arm-lpc1343-7201: CFLAGS += -mcpu=$(MCU) -mthumb
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_LCD
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_LPM
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_PPM
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_PWM
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_ADC
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_DIC
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_DOC
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_DEV
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_PTC
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_PTT
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_STV
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_VFC
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_SWC
arm-lpc1343-7201: PFLAGS += -DSF_ENABLE_MAL
arm-lpc1343-7201: TARGET = arm-lpc1343-7201
arm-lpc1343-7201: $(TPATH)/arm-lpc1343-7201/pulsefire.hex
$(TPATH)/arm-lpc1343-7201/pulsefire.hex:
	mkdir -p $(TPATH)/$(TARGET)
	$(ARM_CC) $(SOURCE) -o $(TPATH)/$(TARGET)/$(PF).elf $(CFLAGS) $(PFLAGS) $(LDFLAGS)
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


