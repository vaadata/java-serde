JAVA := java
GENERATORS := $(wildcard src/test/resources/sh/arnaud/javaserde/units/*/Generator.java)
STREAM_BINARIES := $(GENERATORS:%/Generator.java=%/stream.bin)

.PHONY: all
all: $(STREAM_BINARIES)

%/stream.bin: %/Generator.java
	$(JAVA) $< > $@