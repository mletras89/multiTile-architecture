DIR_SRC=./multitile/tests
PACKAGE_TEST=multitile.tests

check_all: all run_all crossbar_check processor_check testWriteReadTransfers_check singleCoreBus_check singleCoreCrossbar2_check singleCoreCrossbar4_check testMemory_check testCompositeChannel_check DualCore_check QuadCore_check QuadCoreMemoryBound_check QuadCoreMemoryBound_check ModuloScheduling_check MemoryRelocation_check ArchitectureWithNoC_check ArchitectureWithNoCMulticast_check

all: crossbar  processor testWriteReadTransfers singleCoreBus singleCoreCrossbar2 singleCoreCrossbar4 testMemory testCompositeChannel DualCore QuadCore QuadCoreMemoryBound ModuloScheduling MemoryRelocation ArchitectureWithNoC ArchitectureWithNoCMulticast

run_all: crossbar_run processor_run testWriteReadTransfers_run singleCoreBus_run singleCoreCrossbar2_run singleCoreCrossbar4_run testMemory_run testCompositeChannel_run DualCore_run QuadCore_run QuadCoreMemoryBound_run ModuloScheduling_run MemoryRelocation_run ArchitectureWithNoC_run ArchitectureWithNoCMulticast_run

clean_all: crossbar_clean  processor_clean testWriteReadTransfers_clean singleCoreBus_clean singleCoreCrossbar2_clean singleCoreCrossbar4_clean testMemory_clean testCompositeChannel_clean DualCore_clean QuadCore_clean

distclean_all: crossbar_distclean 


ModuloSchedulingRecurrences:
	javac $(DIR_SRC)/testModuloSchedulingRecurrences.java

ModuloSchedulingRecurrences_run:
	java -ea $(PACKAGE_TEST).testModuloSchedulingRecurrences;
	./python/merge-csv-files.py processor-utilization-Tile1_Processor0.csv processor-utilization-Tile1_Processor1.csv crossbar-utilization-crossbar_Tile1.csv processor-utilization-Tile2_Processor0.csv processor-utilization-Tile2_Processor1.csv crossbar-utilization-crossbar_Tile2.csv NoC-utilization-NoC.csv -o testcase-architecture-Modulo-Scheduling-Recurrences.csv;
	./python/merge-csv-files.py memory-utilization-Tile1_Processor0_localMemory.csv memory-utilization-Tile1_Processor1_localMemory.csv memory-utilization-TileLocalMemory_Tile1.csv memory-utilization-Tile2_Processor0_localMemory.csv memory-utilization-Tile2_Processor1_localMemory.csv memory-utilization-TileLocalMemory_Tile2.csv memory-utilization-GLOBAL_MEMORY.csv -o testcase-architecture-mem-utilization-Modulo-Scheduling-Recurrences.csv;

ModuloSchedulingRecurrences_check:
	diff testcase-architecture-Modulo-Scheduling-Recurrences.csv golden-cases/testcase-architecture-Modulo-Scheduling-Recurrences-golden.csv;
	diff testcase-architecture-mem-utilization-Modulo-Scheduling-Recurrences.csv golden-cases/testcase-architecture-mem-utilization-Modulo-Scheduling-Recurrences-golden.csv;

ArchitectureWithNoCMulticast:
	javac $(DIR_SRC)/testModuloSchedulingWithNoCMergeMulticast.java

ArchitectureWithNoCMulticast_run:
	java -ea $(PACKAGE_TEST).testModuloSchedulingWithNoCMergeMulticast;
	./python/merge-csv-files.py processor-utilization-Tile1_Processor0.csv processor-utilization-Tile1_Processor1.csv crossbar-utilization-crossbar_Tile1.csv processor-utilization-Tile2_Processor0.csv processor-utilization-Tile2_Processor1.csv crossbar-utilization-crossbar_Tile2.csv NoC-utilization-NoC.csv -o testcase-architecture-with-NoC-Merge-Multicast.csv;
	./python/merge-csv-files.py memory-utilization-Tile1_Processor0_localMemory.csv memory-utilization-Tile1_Processor1_localMemory.csv memory-utilization-TileLocalMemory_Tile1.csv memory-utilization-Tile2_Processor0_localMemory.csv memory-utilization-Tile2_Processor1_localMemory.csv memory-utilization-TileLocalMemory_Tile2.csv memory-utilization-GLOBAL_MEMORY.csv -o testcase-architecture-with-NoC-mem-utilization-Merge-Multicast.csv;

ArchitectureWithNoCMulticast_check:
	diff testcase-architecture-with-NoC-Merge-Multicast.csv golden-cases/testcase-architecture-with-NoC-Merge-Multicast-golden.csv;
	diff testcase-architecture-with-NoC-mem-utilization-Merge-Multicast.csv golden-cases/testcase-architecture-with-NoC-mem-utilization-Merge-Multicast-golden.csv

ArchitectureWithNoC:
	javac $(DIR_SRC)/testModuloSchedulingWithNoC.java

ArchitectureWithNoC_run:
	java -ea $(PACKAGE_TEST).testModuloSchedulingWithNoC;
	./python/merge-csv-files.py processor-utilization-Tile1_Processor0.csv processor-utilization-Tile1_Processor1.csv crossbar-utilization-crossbar_Tile1.csv processor-utilization-Tile2_Processor0.csv processor-utilization-Tile2_Processor1.csv crossbar-utilization-crossbar_Tile2.csv NoC-utilization-NoC.csv -o testcase-architecture-with-NoC.csv;
	./python/merge-csv-files.py memory-utilization-Tile1_Processor0_localMemory.csv memory-utilization-Tile1_Processor1_localMemory.csv memory-utilization-TileLocalMemory_Tile1.csv memory-utilization-Tile2_Processor0_localMemory.csv memory-utilization-Tile2_Processor1_localMemory.csv memory-utilization-TileLocalMemory_Tile2.csv memory-utilization-GLOBAL_MEMORY.csv -o testcase-architecture-with-NoC-mem-utilization.csv;

ArchitectureWithNoC_check:
	diff testcase-architecture-with-NoC.csv golden-cases/testcase-architecture-with-NoC-golden.csv;
	diff testcase-architecture-with-NoC-mem-utilization.csv golden-cases/testcase-architecture-with-NoC-mem-utilization-golden.csv;

MemoryRelocation:
	javac $(DIR_SRC)/testModuloSchedulingMemoryRelocation.java

MemoryRelocation_run:
	java -ea $(PACKAGE_TEST).testModuloSchedulingMemoryRelocation;
	./python/merge-csv-files.py processor-utilization-MemoryRelocation_Processor0.csv processor-utilization-MemoryRelocation_Processor1.csv processor-utilization-MemoryRelocation_Processor2.csv processor-utilization-MemoryRelocation_Processor3.csv crossbar-utilization-crossbar_MemoryRelocation.csv -o testcase-architecture-util-memory-relocation.csv;
	./python/merge-csv-files.py memory-utilization-MemoryRelocation_Processor0_localMemory.csv memory-utilization-MemoryRelocation_Processor1_localMemory.csv memory-utilization-MemoryRelocation_Processor2_localMemory.csv memory-utilization-MemoryRelocation_Processor3_localMemory.csv memory-utilization-TileLocalMemory_MemoryRelocation.csv -o testcase-memory-relocation-mem-utilization.csv;

MemoryRelocation_check:
	diff testcase-architecture-util-memory-relocation.csv golden-cases/testcase-architecture-util-memory-relocation-golden.csv;
	diff testcase-memory-relocation-mem-utilization.csv golden-cases/testcase-memory-relocation-mem-utilization-golden.csv

ModuloScheduling:
	javac $(DIR_SRC)/testQuadCoreModuloScheduling.java

ModuloScheduling_run:
	java -ea $(PACKAGE_TEST).testQuadCoreModuloScheduling;
	./python/merge-csv-files.py processor-utilization-ModuloSchedulingQuad_Processor0.csv processor-utilization-ModuloSchedulingQuad_Processor1.csv processor-utilization-ModuloSchedulingQuad_Processor2.csv processor-utilization-ModuloSchedulingQuad_Processor3.csv crossbar-utilization-crossbar_ModuloSchedulingQuad.csv -o testcase-ModuloSchedulingQuad.csv;
	./python/merge-csv-files.py processor-utilization-ModuloSchedulingSingle_Processor0.csv crossbar-utilization-crossbar_ModuloSchedulingSingle.csv -o testcase-ModuloSchedulingSingle.csv;
	./python/merge-csv-files.py processor-utilization-ModuloSchedulingDual_Processor0.csv processor-utilization-ModuloSchedulingDual_Processor1.csv crossbar-utilization-crossbar_ModuloSchedulingDual.csv -o testcase-ModuloSchedulingDual.csv;
	./python/merge-csv-files.py memory-utilization-ModuloSchedulingQuad_Processor0_localMemory.csv memory-utilization-ModuloSchedulingQuad_Processor1_localMemory.csv memory-utilization-ModuloSchedulingQuad_Processor2_localMemory.csv memory-utilization-ModuloSchedulingQuad_Processor3_localMemory.csv memory-utilization-TileLocalMemory_ModuloSchedulingQuad.csv -o memory-utilization-ModuloSchedulingQuad.csv;
	./python/merge-csv-files.py memory-utilization-ModuloSchedulingDual_Processor0_localMemory.csv memory-utilization-ModuloSchedulingDual_Processor1_localMemory.csv memory-utilization-TileLocalMemory_ModuloSchedulingDual.csv -o memory-utilization-ModuloSchedulingDual.csv;
	./python/merge-csv-files.py memory-utilization-ModuloSchedulingSingle_Processor0_localMemory.csv memory-utilization-TileLocalMemory_ModuloSchedulingSingle.csv -o  memory-utilization-ModuloSchedulingSingle.csv

ModuloScheduling_check:
	diff testcase-ModuloSchedulingQuad.csv  golden-cases/testcase-ModuloSchedulingQuad-golden.csv;
	diff testcase-ModuloSchedulingSingle.csv golden-cases/testcase-ModuloSchedulingSingle-golden.csv;
	diff testcase-ModuloSchedulingDual.csv golden-cases/testcase-ModuloSchedulingDual-golden.csv
	diff memory-utilization-ModuloSchedulingSingle.csv golden-cases/memory-utilization-ModuloSchedulingSingle-golden.csv
	diff memory-utilization-ModuloSchedulingDual.csv golden-cases/memory-utilization-ModuloSchedulingDual-golden.csv
	diff memory-utilization-ModuloSchedulingQuad.csv golden-cases/memory-utilization-ModuloSchedulingQuad-golden.csv

#QuadCoreMemoryBound_clean:
#	echo "Cleaning Test QuadCoreMemoryBound"; ./clean.sh

QuadCoreMemoryBound:
	javac $(DIR_SRC)/testMemoryBoundQuadCore.java

QuadCoreMemoryBound_run:
	java -ea $(PACKAGE_TEST).testMemoryBoundQuadCore;
	./python/merge-csv-files.py crossbar-utilization-crossbar_Tile_testQuadCoreMemoryBound.csv processor-utilization-Tile_testQuadCoreMemoryBound_Processor3.csv processor-utilization-Tile_testQuadCoreMemoryBound_Processor2.csv processor-utilization-Tile_testQuadCoreMemoryBound_Processor1.csv  processor-utilization-Tile_testQuadCoreMemoryBound_Processor0.csv -o testQuadCore-bounded-memory.csv;
	./python/merge-csv-files.py memory-utilization-Tile_testQuadCoreMemoryBound_Processor0_localMemory.csv memory-utilization-Tile_testQuadCoreMemoryBound_Processor1_localMemory.csv memory-utilization-Tile_testQuadCoreMemoryBound_Processor2_localMemory.csv memory-utilization-Tile_testQuadCoreMemoryBound_Processor3_localMemory.csv  -o testQuadCore-memory-utilization.csv;

QuadCoreMemoryBound_check:
	diff testQuadCore-bounded-memory.csv golden-cases/testQuadCore-bounded-memory-golden.csv;
	diff testQuadCore-memory-utilization.csv golden-cases/testQuadCore-memory-utilization-golden.csv
        
QuadCoreMemoryBound_clean:
	echo "Cleaning Test QuadCoreMemoryBound"; ./clean.sh


DualCore:
	javac $(DIR_SRC)/testDualCoreImplementation.java

DualCore_run:
	java -ea $(PACKAGE_TEST).testDualCoreImplementation;
	./python/merge-csv-files.py processor-utilization-Tile_testDualCore_Processor0.csv processor-utilization-Tile_testDualCore_Processor1.csv crossbar-utilization-crossbar_Tile_testDualCore.csv -o testDualCore-unbounded-memory.csv

DualCore_check:
	diff testDualCore-unbounded-memory.csv golden-cases/testDualCore-unbounded-memory-golden.csv;
            
DualCore_clean:
	echo "Cleaning Test DualCore"; ./clean.sh

QuadCore:
	javac $(DIR_SRC)/testQuadCoreImplementation.java

QuadCore_run:
	java -ea $(PACKAGE_TEST).testQuadCoreImplementation;
	./python/merge-csv-files.py crossbar-utilization-crossbar_Tile_testQuadCore.csv processor-utilization-Tile_testQuadCore_Processor3.csv processor-utilization-Tile_testQuadCore_Processor2.csv processor-utilization-Tile_testQuadCore_Processor1.csv  processor-utilization-Tile_testQuadCore_Processor0.csv -o testQuadCore-unbounded-memory.csv

QuadCore_check:
	diff testQuadCore-unbounded-memory.csv golden-cases/testQuadCore-unbounded-memory-golden.csv;
        
QuadCore_clean:
	echo "Cleaning Test QuadCore"; ./clean.sh

testCompositeChannel:
	javac $(DIR_SRC)/testCompositeChannel.java

testCompositeChannel_run:
	java -ea $(PACKAGE_TEST).testCompositeChannel;
	./python/merge-csv-files.py crossbar-utilization-crossbar_Tile_testComposite.csv processor-utilization-Tile_testComposite_Processor0.csv -o test-before-MRB-insertion.csv;
	./python/merge-csv-files.py crossbar-utilization-crossbar_Tile_testCompositeAfterMerging.csv processor-utilization-Tile_testCompositeAfterMerging_Processor0.csv  -o test-after-MRB-insertion.csv

testCompositeChannel_check:
	diff test-before-MRB-insertion.csv golden-cases/test-before-MRB-insertion-golden.csv;
	diff test-after-MRB-insertion.csv golden-cases/test-after-MRB-insertion-golden.csv
	
testCompositeChannel_clean:
	echo "Cleaning Test Memory"; ./clean.sh

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
