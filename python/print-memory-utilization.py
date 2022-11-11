#!/usr/bin/env python3

import argparse
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import random
from matplotlib.patches import Patch

parser = argparse.ArgumentParser()
#general arguments
parser.add_argument("file",
    help="Location of the csv file dataset!")

ARGS = parser.parse_args()


CSV_FILE = ARGS.file
df = pd.read_csv(CSV_FILE,sep='\t');

# start time
#start_row = df.Start.min()
# number of time units from start to task start
#df['start_num'] = (df.Start-start_row)
# number of time units from start to end of tasks
#df['end_num'] = (df.Finish-start_row)
# time units between start and end of each task
#df['days_start_to_end'] = df.end_num - df.start_num
#print(df)
#assing color

uniqueMemories = df['Memory'].unique()
color = ["#"+''.join([random.choice('0123456789ABCDEF') for j in range(6)])
             for i in range(len(uniqueMemories))]

dictColorMems = dict()
for i in range(len(uniqueMemories)):
    dictColorMems[uniqueMemories[i]] = color[i]

Colors = []
for index, row in df.iterrows():
    Colors.append(dictColorMems[row['Memory']])

df['Color'] = Colors

[fig,axes] = plt.subplots(len(uniqueMemories),1)
index=0

if not isinstance(axes, (np.ndarray, np.generic)):
	for memoryElement in uniqueMemories:
		currentMemoryData = df.loc[df['Memory']==memoryElement]
		axes.fill_between(currentMemoryData.When,currentMemoryData.Capacity,step="post",alpha=0.4,color=currentMemoryData.Color)
		line,= axes.step(currentMemoryData.When,currentMemoryData.Capacity,where="post",lw=3)
		#line, = axes.plot(currentMemoryData.When,currentMemoryData.Capacity,lw=3,drawstyle="steps")
		line.set_color(dictColorMems[memoryElement])
		axes.set_title(memoryElement)   
else:
	axes = list(axes)
	for memoryElement in uniqueMemories:
	    currentMemoryData = df.loc[df['Memory']==memoryElement]
	    axes[index].fill_between(currentMemoryData.When,currentMemoryData.Capacity,step="post",alpha=0.4,color=currentMemoryData.Color)
	    line, = axes[index].step(currentMemoryData.When,currentMemoryData.Capacity,lw=3,where="post")
	    line.set_color(dictColorMems[memoryElement])
	    axes[index].set_title(memoryElement)
	    index=index+1


legend_elements = [Patch(facecolor=dictColorMems[i], label=i)  for i in dictColorMems]
plt.legend(handles=legend_elements)

plt.show()


