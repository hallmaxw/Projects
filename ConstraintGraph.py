class Variable:

    # label: string identifier
    # domain: list of potential integer values
    def __init__(self, label, domain):
        self.label = label
        self.domain = domain

class Constraint:

    # left: left Variable
    # right: right Variable
    # operator: string binary operator (left operator right)
    def __init__(self, left, right, operator):
        self.left = left
        self.right = right
        self.operator = operator

    # attempt to reduce the domain of the right
    def leftForwardCheck(self):
        # left should just have one value at this point
        value = self.left.domain[0]
        if self.operator == ">":
            self.right.domain = filter(lambda x: x < value, self.right.domain)
        elif self.operator == "<":
            self.right.domain = filter(lambda x: x > value, self.right.domain)
        elif self.operator == "!":
            self.right.domain = filter(lambda x: x != value, self.right.domain)
        elif self.operator == "=":
            self.right.domain = filter(lambda x: x == value, self.right.domain)
    # attempt to reduce the domain of the left
    def rightForwardCheck(self):
        # right should just have one value at this point
        value = self.right.domain[0]
        if self.operator == ">":
            self.left.domain = filter(lambda x: x > value, self.left.domain)
        elif self.operator == "<":
            self.left.domain = filter(lambda x: x < value, self.left.domain)
        elif self.operator == "!":
            self.left.domain = filter(lambda x: x != value, self.left.domain)
        elif self.operator == "=":
            self.left.domain = filter(lambda x: x == value, self.left.domain)

def ConstraintGraph:

    def __init__(self):
        self.variableDict = {}

    def addVar(self, var):
        self.variableDict[var] = []

    def addConstraint(self, con):
        self.variableDict[con.left].append(con)
        self.variableDict[con.right].append(con)

    def getConstraints(self, var):
        return self.variableDict[var]
