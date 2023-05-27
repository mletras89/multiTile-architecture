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

    parser.add_option("-o", "--output", type="string", help="output CSV file <OUTPUT>")
    (options,ARGS) = parser.parse_args()

    if not options.output:
        print("%s: missing --output option!" % PROG, file=sys.stderr)
        exit(-1)

    if len(ARGS) == 0:
        parser.parse_args(["-h"])


    #pd.options.display.float_format = '${:,.12f}'.format

    #for csv_file in ARGS:
    #    df = pd.read_csv(csv_file,sep='\t')

    # Read each CSV file into DataFrame
    # This creates a list of dataframes
    df_list = (pd.read_csv(csv_file,sep='\t') for csv_file in ARGS)

    # Concatenate all DataFrames
    df   = pd.concat(df_list, ignore_index=True)

    [rows, cols] = df.shape
    column_names = df.columns.values.tolist()
    for col in column_names:
      for i in range(rows):
        val = str((df.at[i,col])).replace(',','.')
        df.at[i,col] = val
      #print("start "+str(val))
#    print("rows"+str( rows ))

    # save data frame to csv
    #df.reset_index(drop)
    df.to_csv(options.output, index=False,sep='\t', float_format='%.12f')
