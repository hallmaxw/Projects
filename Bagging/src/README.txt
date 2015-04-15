AUTHOR: Maxwell Hall
PLATFORM: Mac OS X 10.10

HOW IS BAGGING DONE?

Bagging was implemented using my decision tree classifier from homework 1.
The data used for the experiment was also provided from homework 1. The specific
data sets were train-2.dat, test-2.dat, train-3.dat, and test-3.dat. The implementation
creates a list of decision trees by training each decision tree on its own randomized
set of training data derived from the provided training set. The classification of
a given test instance is determined by the majority vote of the list of decision
trees.

HOW WERE THE EXPERIMENTS DONE?

Two scripts were used for the experiment. main.py takes a training file, testing file,
and bag size as arguments and prints the accuracy for that bag size. experiment.py
takes a training file, testing file, and maximum bag size as arguments and calls
main.py once for each bag size from 1 to maximum bag size. I used experiment.py
with the two different data sets and plotted the results on a graph.

main.py utilizes Bag.py which implements the bagging algorithm.
