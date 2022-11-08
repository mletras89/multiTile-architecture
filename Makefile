check_all: all run_all test01_check 

all: crossbar 

run_all: crossbar_run

clean_all: crossbar_clean 

distclean_all: crossbar_distclean 

crossbar:
	javac testCrossbar.java

crossbar_run: 
	java -ea testCrossbar

crossbar_check:
	diff crossbar-utilization-Crossbar.csv golden-cases/crossbar-utilization-Crossbar-golden.csv;

crossbar_clean:
	echo "Cleaning crossbar"; ./clean.sh

crossbar_distclean: crossbar_clean
