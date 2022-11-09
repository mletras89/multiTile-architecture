check_all: all run_all crossbar_check processor_check testWriteReadTransfers_check singleCoreBus_check

all: crossbar  processor testWriteReadTransfers singleCoreBus

run_all: crossbar_run processor_run testWriteReadTransfers_run singleCoreBus_run

clean_all: crossbar_clean  processor_clean testWriteReadTransfers_clean singleCoreBus_clean

distclean_all: crossbar_distclean 

singleCoreBus:
	javac testTileSingleCoreBus.java

singleCoreBus_run:
	java -ea testTileSingleCoreBus;
	./merge-csv-files.py processor-utilization-TileSingleCoreBus1_Processor0.csv crossbar-utilization-crossbar_TileSingleCoreBus1.csv -o TileSingleCoreBus1.csv
singleCoreBus_check:
	diff TileSingleCoreBus1.csv golden-cases/TileSingleCoreBus1-golden.csv;
	
singleCoreBus_clean:
	echo "Cleaning testTileSingleCoreBus"; ./clean.sh

processor:
	javac testProcessor.java

processor_run:
	java -ea testProcessor

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
	javac testCrossbar.java

crossbar_run: 
	java -ea testCrossbar

crossbar_check:
	diff crossbar-utilization-Crossbar.csv golden-cases/crossbar-utilization-Crossbar-golden.csv;

crossbar_clean:
	echo "Cleaning crossbar"; ./clean.sh

crossbar_distclean: crossbar_clean singleCore_clean

testWriteReadTransfers:
	javac testWriteReadTransfers.java

testWriteReadTransfers_run:
	java -ea testWriteReadTransfers;
	./merge-csv-files.py crossbar-utilization-crossbar_TileReadWrite.csv processor-utilization-TileReadWrite_Processor0.csv  -o testWriteReadTransfers.csv
testWriteReadTransfers_check:
	diff testWriteReadTransfers.csv golden-cases/testWriteReadTransfers-golden.csv

testWriteReadTransfers_clean:
	echo "Cleaning testWriteReadTransfers"; ./clean.sh 
