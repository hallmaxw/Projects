from __future__ import print_function
from math import log
# CONTENTS
# This file contains the source code for the ID3 decision tree data structure.
# There is also a data structure for each attribute that is used to keep track
# of the label, number of possible values, and the seen values.


class Attribute:
    def __init__(self, label, count):
        self.label = label
        self.count = count
        self.values = set()

# test if all instances are the same class
# data is a list of tuples of attribute values
def isClassUnique(data):
    first = data[0][-1]
    for index in xrange(1, len(data)):
        # test if class is the same
        if data[index][-1] != first:
            return False
    return True

# test if all data has the same attribute value
# data is a list of tuples of attribute values
def isAttributeUnique(data):
    first = data[0][:-1]
    for index in xrange(1, len(data)):
        if data[index][:-1] != first:
            return False
    return True

# log base 2 modified to return 0 for log(0)
def log2(x):
    if x == 0:
        return 0
    else:
        return log(x, 2)

# Classify the data as the class with the highest proportion
def classifyByProportion(data):
    clsCounts = {1: 0, 0: 0}
    # count the number of occurrences of each class
    for instance in data:
        clsCounts[instance[-1]] += 1
    count = clsCounts[0] + clsCounts[1]
    return 1 if clsCounts[1] > clsCounts[0] else 0

# get the entropy and number of instances using the given instance generator
def getEntropyAndCount(instanceGen):
    clsCounts = {1: 0, 0: 0}
    # count the number of occurrences of each class
    for instance in instanceGen:
        clsCounts[instance[-1]] += 1
    count = clsCounts[0] + clsCounts[1]
    if count == 0:
        return (0, 0)
    p1 = clsCounts[1]/float(count)
    p0 = clsCounts[0]/float(count)
    entropy = -1*p1*log2(p1) - p0*log2(p0)
    return (entropy, count)

# ID3 Decision Tree data structure. The data structure uses training data and
# a list of attributes to build an unpruned decision tree.
#
# INPUT:
# attributes - tuple of Attributes that have not yet been used to split a node
# data - List of tuples of attribute values
#
# INSTANCE VARIABLES:
# children - dictionary of attribute value (integer) to DecisionTree pairs
# attributeTested - Attribute that is used to split this node
# attrInd - The index of attributeTested in the data tuples
# classification - The classification of this node. If this node is not a leaf,
#     the classification is determined by the majority class.

class DecisionTree:
    def __init__(self, attributes, data):
        # all instances have the same class
        if isClassUnique(data):
            self.children = None
            self.attrInd = -1
            self.attributeTested = None
            self.classification = data[0][-1]
            return
        # all instances have the same attribute value
        elif isAttributeUnique(data):
            self.children = None
            self.attributeTested = None
            self.attrInd = -1
            self.classification = classifyByProportion(data)
            return

        self.classification = classifyByProportion(data)
        instanceGen = (instance for instance in data)
        entropy = getEntropyAndCount(instanceGen)[0]

        curAttr = 0
        maxIG = 0
        # find the attribute with the highest IG
        for ind, attr in enumerate(attributes):
            condEntropy = 0
            # iterate over all attribute values and build conditional entropy
            for val in attr.values:
                instanceGen = (instance for instance in data if instance[ind] == val)
                childTuple = getEntropyAndCount(instanceGen)
                # no instances with this attribute value
                if childTuple[1] == 0:
                    continue
                childEntropy = childTuple[0]
                proportion = childTuple[1]/float(len(data))
                condEntropy += childEntropy*proportion

            IG = entropy - condEntropy
            if IG > maxIG:
                maxIG = IG
                curAttr = ind
        # the current index of the selected attribute is used later when classifying data
        self.attrInd = curAttr
        self.attributeTested = attributes[curAttr]
        self.children = {}
        # remove the used attribute
        childAttributes = attributes[0:curAttr] + attributes[curAttr+1:]
        # add a child for each attribute value only if there are instances with that value
        for val in self.attributeTested.values:
            childData = [instance[0:curAttr] + instance[curAttr+1:] for instance in data if instance[curAttr] == val]
            if len(childData) > 0:
                self.children[val] = DecisionTree(childAttributes, childData)

    # a node is a leaf if it has no children
    def isLeaf(self):
        return self.children == None

    # Classify the given instance
    # If no leaf node matches the given instance, use the lowest depth node
    # that the instance matches.
    def classify(self, instance):
        if not self.isLeaf() and instance[self.attrInd] in self.children:
            attr = self.attrInd
            child = self.children[instance[self.attrInd]]
            return child.classify(instance[:attr] + instance[attr+1:])
        return self.classification

    # print the decision tree recursively
    def printTree(self, depth=0):
        for x in self.attributeTested.values:
            if x not in self.children:
                continue
            child = self.children[x]
            if  child.isLeaf():
                # child is a leaf
                print("| "*depth, end="")
                print("attr" + self.attributeTested.label + " = " + str(x) + " : " + str(self.children[x].classification))
            else:
                # child is not a leaf
                print("| "*depth, end="")
                print("attr" + self.attributeTested.label + " = " + str(x) + " :")
                self.children[x].printTree(depth+1)
