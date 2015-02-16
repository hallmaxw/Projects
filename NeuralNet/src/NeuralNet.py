class NeuralNet:
    def __init__(self, size):
        self.weights = [0]*size
    def train(self, sample):
        classification = sample[-1]
        total = 0
        for ind, weight in enumerate(self.weights):
            total += weight*sample[ind]
        testClassification = 1 if total >= 0.5 else 0
        
