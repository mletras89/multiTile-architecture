DIR_SRC=./src/multitile/tests
PACKAGE_TEST=src.multitile.tests

check_all: all run_all crossbar_check processor_check testWriteReadTransfers_check singleCoreBus_check singleCoreCrossbar2_check singleCoreCrossbar4_check testMemory_check

all: crossbar  processor testWriteReadTransfers singleCoreBus singleCoreCrossbar2 singleCoreCrossbar4 testMemory

run_all: crossbar_run processor_run testWriteReadTransfers_run singleCoreBus_run singleCoreCrossbar2_run singleCoreCrossbar4_run testMemory_run

clean_all: crossbar_clean  processor_clean testWriteReadTransfers_clean singleCoreBus_clean singleCoreCrossbar2_clean singleCoreCrossbar4_clean testMemory_clean

distclean_all: crossbar_distclean 


testMemory:
	javac $(DIR_SRC)/testMemory.java

testMemory_run:
	java -ea $(PACKAGE_TEST).testMemory;

testMemory_check:
	diff testMemory.csv golden-cases/testMemory-golden.csv
	
testMemory_clean:
	echo "Cleaning Test Memory"; ./clean.sh


singleCoreCrossbar4:
	javac $(DIR_SRC)/testTileSingleCoreCrossbar4.java

singleCoreCrossbar4_run:
	java -ea $(PACKAGE_TEST).testTileSingleCoreCrossbar4;
	./python/merge-csv-files.py processor-utilization-TileSingleCoreCrossbar4_1_Processor0.csv crossbar-utilization-crossbar_TileSingleCoreCrossbar4_1.csv -o testTileSingleCoreCrossbar4.csv

singleCoreCrossbar4_check:
	diff testTileSingleCoreCrossbar4.csv golden-cases/testTileSingleCoreCrossbar4-golden.csv;
	
singleCoreCrossbar4_clean:
	echo "Cleaning testTileSingleCoreCrossbar4"; ./clean.sh

singleCoreCrossbar2:
	javac $(DIR_SRC)/testTileSingleCoreCrossbar2.java

singleCoreCrossbar2_run:
	java -ea $(PACKAGE_TEST).testTileSingleCoreCrossbar2;
	./python/merge-csv-files.py processor-utilization-TileSingleCoreCrossbar2_1_Processor0.csv crossbar-utilization-crossbar_TileSingleCoreCrossbar2_1.csv -o testTileSingleCoreCrossbar2.csv

singleCoreCrossbar2_check:
	diff testTileSingleCoreCrossbar2.csv golden-cases/testTileSingleCoreCrossbar2-golden.csv;
	
singleCoreCrossbar2_clean:
	echo "Cleaning testTileSingleCoreCrossbar2"; ./clean.sh

singleCoreBus:
	javac $(DIR_SRC)/testTileSingleCoreBus.java

singleCoreBus_run:
	java -ea  $(PACKAGE_TEST).testTileSingleCoreBus;
	./python/merge-csv-files.py processor-utilization-TileSingleCoreBus1_Processor0.csv crossbar-utilization-crossbar_TileSingleCoreBus1.csv -o TileSingleCoreBus1.csv
singleCoreBus_check:
	diff TileSingleCoreBus1.csv golden-cases/TileSingleCoreBus1-golden.csv;
	
singleCoreBus_clean:
	echo "Cleaning testTileSingleCoreBus"; ./clean.sh

processor:
	javac $(DIR_SRC)/testProcessor.java

processor_run:
	java -ea $(PACKAGE_TEST).testProcessor

processor_check:
	diff processor-utilization-cpu1.csv golden-cases/processor-utilization-cpu1-golden.csv

processor_clean:
	echo "Cleaning processor"; ./clean.sh

singleCore:
	javac testSingleCore.java

singleCore_run:
	java -ea testSingleCore

singleCore_check:
	
singleCore_clean:
	echo "Cleaning singleCore"; ./clean.sh

crossbar:
	javac $(DIR_SRC)/testCrossbar.java

crossbar_run: 
	java -ea $(PACKAGE_TEST).testCrossbar

crossbar_check:
	diff crossbar-utilization-Crossbar.csv golden-cases/crossbar-utilization-Crossbar-golden.csv;

crossbar_clean:
	echo "Cleaning crossbar"; ./clean.sh

crossbar_distclean: crossbar_clean singleCore_clean

testWriteReadTransfers:
	javac $(DIR_SRC)/testWriteReadTransfers.java

testWriteReadTransfers_run:
	java -ea $(PACKAGE_TEST).testWriteReadTransfers;
	./python/merge-csv-files.py crossbar-utilization-crossbar_TileReadWrite.csv processor-utilization-TileReadWrite_Processor0.csv  -o testWriteReadTransfers.csv
testWriteReadTransfers_check:
	diff testWriteReadTransfers.csv golden-cases/testWriteReadTransfers-golden.csv

testWriteReadTransfers_clean:
	echo "Cleaning testWriteReadTransfers"; ./clean.sh 
