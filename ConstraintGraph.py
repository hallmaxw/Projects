class Variable:

    # label: string identifier
    # domain: list of potential integer values
    def __init__(self, label, domain):
        self.label = label
        self.domain = domain
        self.assigned = False

class Constraint:

    # left: left Variable
    # right: right Variable
    # operator: string binary operator (left operator right)
    def __init__(self, left, right, operator):
        self.left = left
        self.right = right
        self.operator = operator

    # return the domain of the right variable filtered by the constraint
    def filterRightDomain(self):
        # left should just have one value at this point
        value = self.left.domain[0]
        if self.operator == ">":
            return filter(lambda x: x < value, self.right.domain)
        elif self.operator == "<":
            return filter(lambda x: x > value, self.right.domain)
        elif self.operator == "!":
            return filter(lambda x: x != value, self.right.domain)
        elif self.operator == "=":
            return filter(lambda x: x == value, self.right.domain)

    # return the domain of the left variable filtered by the constraint
    def filterLeftDomain(self):
        # right should just have one value at this point
        value = self.right.domain[0]
        if self.operator == ">":
            return filter(lambda x: x > value, self.left.domain)
        elif self.operator == "<":
            return filter(lambda x: x < value, self.left.domain)
        elif self.operator == "!":
            return filter(lambda x: x != value, self.left.domain)
        elif self.operator == "=":
            return filter(lambda x: x == value, self.left.domain)

    # attempt to reduce the domain of the right
    def leftForwardCheck(self):
        self.right.domain = self.filterRightDomain()

    # attempt to reduce the domain of the left
    def rightForwardCheck(self):
        self.left.domain = self.filterLeftDomain()

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

    # returns a list of the most constrained variables
    def getMostConstrainedVariables(self):
        variables = filter(lambda x: not x.assigned, self.variableDict.keys())
        curLength = len(variables[0].domain)
        variables = [variables[0]]

        for variable in variables[1:]:
            if len(variable.domain) < curLength:
                variables = [variable]
                curLength = len(variable.domain)
            elif len(variable.domain) == curLength:
                variables.append(variable)
        return variables

    # return the most constraining variable out of the list of variables
    def getMostConstainingVariable(self, varList):
        curVar = varList[0]
        # neither sides of the constraint should be assigned
        curCount = len([constraint for constraint in self.variableDict[curVar]
            if not constraint.left.assigned and not constraint.right.assigned])
