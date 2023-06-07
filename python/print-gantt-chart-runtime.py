#!/usr/bin/env python

#import argparse
from optparse import OptionParser
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import random
from matplotlib.patches import Patch

if __name__ == '__main__':
    help='''usage: %prog [options] <tsv files>", version=1.0'''
    parser = OptionParser(usage=help)

    (options,ARGS) = parser.parse_args()

    if len(ARGS) == 0:
        parser.parse_args(["-h"])
    
    #for csv_file in ARGS:
    #    df = pd.read_csv(csv_file,sep='\t')

    # Read each CSV file into DataFrame
    # This creates a list of dataframes
    df_list = (pd.read_csv(csv_file,sep='\t') for csv_file in ARGS)

    # Concatenate all DataFrames
    df   = pd.concat(df_list, ignore_index=True)
    

    # start time
    #start_row = df.Start.min()
    # number of time units from start to task start
    #df['start_num'] = (df.Start-start_row)
    df['start_num'] = df.Start
    # number of time units from start to end of tasks
    #df['end_num'] = (df.Finish-start_row)
    df['end_num'] = df.Finish
    # time units between start and end of each task
    df['days_start_to_end'] = df.end_num - df.start_num
    #print(df)
    #assing color
    uniqueJobs = df['Job'].unique()
    random.seed(1)
    color = ["#"+''.join([random.choice('0123456789ABCDEF') for j in range(6)])
#    color = ["#"+''.join(['0' for j in range(6)])
                 for i in range(len(uniqueJobs))]
    #print(uniqueJobs)
    #print(color)
    
    dictColorJobs = dict()
    for i in range(len(uniqueJobs)):
        dictColorJobs[uniqueJobs[i]] = color[i]
    
    #print(dictColorJobs)
    
    Colors = []
    for index, row in df.iterrows():
        Colors.append(dictColorJobs[row['Job']])
    
    df['Color'] = Colors
    
    #print(df)
    
    #print(len(df.keys()))
    #df['Color'] = color
    
    
    [fig, ax] = plt.subplots(1, figsize=(16,6))
    ax.barh(df.Resource, df.days_start_to_end, left=df.start_num,color=df.Color)
    
    legend_elements = [Patch(facecolor=dictColorJobs[i], label=i)  for i in dictColorJobs]
    plt.legend(handles=legend_elements)
    plt.xlim(0,max(df.end_num))
    plt.show()
    
    #print("maximum value")
    #print(max(df.end_num))
