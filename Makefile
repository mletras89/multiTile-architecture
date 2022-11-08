check_all: all run_all crossbar_check  

all: crossbar 

run_all: crossbar_run

clean_all: crossbar_clean 

distclean_all: crossbar_distclean 

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
