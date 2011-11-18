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
CFLAGS = -std=gnu99
CFLAGS += -DF_CPU=$(F_CPU)
CFLAGS += -I./..
CFLAGS += -g$(DEBUG)
CFLAGS += $(CDEFS)
CFLAGS += -Os
CFLAGS += -mcall-prologues
#CFLAGS += -mshort-calls
CFLAGS += -funsigned-char
CFLAGS += -funsigned-bitfields
CFLAGS += -fpack-struct
CFLAGS += -fshort-enums
CFLAGS += -fno-unit-at-a-time
CFLAGS += -fno-inline-small-functions
CFLAGS += -fno-split-wide-types
CFLAGS += -fno-tree-scev-cprop
CFLAGS += -Wall
CFLAGS += -Wstrict-prototypes
#CFLAGS += -Wundef
CFLAGS += -Wfloat-equal
#CFLAGS += -Wconversion 
CFLAGS += -ffunction-sections
#CFLAGS += -Wunreachable-code
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


# Define Messages
# English
MSG_ERRORS_NONE = Errors: none
MSG_BEGIN = -------- begin --------
MSG_END = -------- end --------
MSG_SIZE_BEFORE = Size before: 
MSG_SIZE_AFTER = Size after:
MSG_COFF = Converting to AVR COFF:
MSG_EXTENDED_COFF = Converting to AVR Extended COFF:
MSG_FLASH = Creating load file for Flash:
MSG_EEPROM = Creating load file for EEPROM:
MSG_EXTENDED_LISTING = Creating Extended Listing:
MSG_SYMBOL_TABLE = Creating Symbol Table:
MSG_LINKING = Linking:
MSG_COMPILING = Compiling C:
MSG_COMPILING_CPP = Compiling C++:
MSG_ASSEMBLING = Assembling:
MSG_CLEANING = Cleaning project:
MSG_CREATING_LIBRARY = Creating library:

# Define all src files.
SOURCE=./strings.c ./vars.c ./pulsefire.c ./chip.c ./freq.c ./input.c ./lcd.c ./lpm.c ./mal.c ./output.c ./ptc.c ./serial.c ./utils.c 

all: atmega328p-001 \
atmega328p-002 \
atmega328p-003 \
atmega328p-004 \
atmega328p-005 \
atmega328p-006 \
atmega328p-007 \
atmega328p-020 \
atmega328p-021 \
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
atmega168p-801 


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
atmega328p-006: PFLAGS += -DSF_ENABLE_LPM
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


# Create final output files (.hex, .eep) from ELF output file.
#%.hex: %.elf
#	@echo
#	@echo $(MSG_FLASH) $@
#	$(OBJCOPY) -O ihex -R .eeprom $< $@
#%.eep: %.elf
#	@echo
#	@echo $(MSG_EEPROM) $@
#	$(OBJCOPY) -j .eeprom --set-section-flags=.eeprom="alloc,load" \
#		--change-section-lma .eeprom=0 -O $(FORMAT) $< $@

# Create extended listing file from ELF output file.
#%.lst: %.elf
#	@echo
#	@echo $(MSG_EXTENDED_LISTING) $@
#	$(OBJDUMP) -h -S $< > $@

# Create a symbol table from ELF output file.
#%.sym: %.elf
#	@echo
#	@echo $(MSG_SYMBOL_TABLE) $@
#	$(NM) -n $< > $@

# Link: create ELF output file from object files. was 
#%.elf: $(OBJ)
#	@echo
#	@echo $(MSG_LINKING) $@
#	$(CC) -lm -lc $(CFLAGS) $^ --output $@ $(LDFLAGS)

# Compile c code
#$(FULLOBJECTS): $(OBJDIR)/%.o : %.c
#	@echo
#	@echo $(MSG_COMPILING) $<
#	$(CC) -c $(CFLAGS) $(PFLAGS) $< -o $@


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


