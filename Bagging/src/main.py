from __future__ import print_function
import sys

from DecisionTree import Attribute
from Bag import DecisionTreeBag

# Author: Maxwell Hall

# CONTENTS:
# This file contains the main method of a Decision Tree Bagging testing program.
# The attributes of the decision tree are assumed to be discrete and the
# classifier is assumed to be binary.



# INPUT:
# This program takes a training data file name, testing data file name, and
# number of bags as command line arguments.

# OUTPUT:
# The accuracy of the decision trees against the testing data to standard output.

# FUNCTION:
# The program parses the training data file and generates decision trees
# using the ID3 decision tree algorithm. The decision trees' accuracy is then
# tested against the training and testing data

# MAIN PROGRAM
f = None
try:
    f = open(sys.argv[1], "r")
except IOError:
    sys.exit("Unable to open training file: " + sys.argv[1])
attrList = f.readline().split()

# create the attributes from the attribute line input
attributes = []
for x in xrange(0, len(attrList), 2):
    attributes.append(Attribute(attrList[x], int(attrList[x+1])))
attributes = tuple(attributes)

# parse and store the training data
data = []
for line in f:
    temp = tuple([int(string) for string in line.split()])
    data.append(temp)
f.close()

# Update the attributes data structure with the used values for each attribute.
# We can't assume the values are base-0 because some test cases are base-1.
# This allows the program to accept any set of attribute values.
for instance in data:
    for ind in xrange(len(attributes)):
        attributes[ind].values.add(instance[ind])

d = DecisionTreeBag(attributes, data, int(sys.argv[3]))


# test the decision trees against the training data
correct = 0
total = 0
for instance in data:
    x = d.classify(instance[:-1])
    y = instance[-1]
    total += 1
    if x == y:
        correct += 1
#print("Accuracy on training set ({0:d} instances) : {1:.2f}%".format(len(data), correct/float(total)*100))

try:
    f = open(sys.argv[2], "r")
except IOError:
    sys.exit("Unable to open test file: " + sys.argv[2])

f.readline() # skip the attribute line
# test the decision trees against the training data
correct = 0
total = 0
for line in f:
    temp = tuple([int(string) for string in line.split()])
    x = d.classify(temp[:-1])
    y = temp[-1]
    total += 1
    if x == y:
        correct += 1
#print("Accuracy on test set ({0:d} instances) : {1:.2f}%".format(total, correct/float(total)*100))
print("{0:.2f}".format(correct/float(total)*100))
f.close()
