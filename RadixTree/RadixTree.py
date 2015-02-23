# test if str1 is a prefix of str2
def isPrefix(str1, str2):
    return str1 == str2[:len(str1)]
class RadixTree:
    class Edge:
        def __init__(self, target, token):
            self.target = target
            self.token = token
    def __init__(self):
        self.edges = []
        self.items = []

    # itemTuple: (token, item)
    def insert(self, itemTuple):
        traversalEdge = None
        numFound = 0
        for edge in self.edges:
            if isPrefix(itemTuple[0], edge.token):
                traversalEdge = edge
                break
