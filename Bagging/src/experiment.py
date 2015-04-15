import os
import re
import sys
# script used to experiment bagging with different data sets and bag sizes

# COMMAND LINE ARGUMENTS:
# 1: training file name
# 2: testing file name
# 3: Maximum Bagging size

# NOTE:
# main.py is expected to be located at ./src/main.py relative to the working
# directory when executed

# OUTPUT:
# Accuracies of bagging with every bag size from 1 to <maximum bag size>
# delimited by a new line.

if __name__ == "__main__":
    COMMAND = "python ./src/main.py {0:s} {1:s}".format(sys.argv[1], sys.argv[2])
    for count in xrange(1, int(sys.argv[3])+1):
        tempCmd = COMMAND + " " + str(count)
        os.system(tempCmd)
