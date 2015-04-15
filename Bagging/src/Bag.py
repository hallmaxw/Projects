from DecisionTree import DecisionTree
import random

# implementation of bagging using a bag of decision trees
class DecisionTreeBag:
    def __init__(self, attributes, data, classifierCount):
        self.classifiers = []
        # create each classifier
        for x in xrange(classifierCount):
            # select the training data randomly from the provided data
            usedData = [random.choice(data) for temp in xrange(len(data))]
            self.classifiers.append(DecisionTree(attributes, usedData))

    # the classification of an instance is determined by the majority vote
    # of the classifiers
    def classify(self, instance):
        zeroCount = 0
        oneCount = 0
        for classifier in self.classifiers:
            if classifier.classify(instance) == 0:
                zeroCount += 1
            else:
                oneCount += 1
        if zeroCount > oneCount:
            return 0
        elif oneCount > zeroCount:
            return 1
        else:
            return random.choice([0, 1])
