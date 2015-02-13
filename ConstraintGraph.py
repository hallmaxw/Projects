from __future__ import print_function
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

class ConstraintGraph:
    def __init__(self):
        self.variableDict = {}

    def addVar(self, var):
        self.variableDict[var] = []

    def addConstraint(self, con):
        self.variableDict[con.left].append(con)
        self.variableDict[con.right].append(con)

    def isComplete(self):
        for variable in self.variableDict.keys():
            if not variable.assigned:
                return False
        return True

    def isValueConsistent(self, var, val):
        temp = var.domain
        var.domain = [val]
        for constraint in self.getConstraints(var):
            isLeft = var is constraint.left
            if isLeft:
                if len(constraint.filterRightDomain()) == 0:
                    var.domain = temp
                    return False
            else:
                if len(constraint.filterLeftDomain()) == 0:
                    var.domain = temp
                    return False
        var.domain = temp
        return True

    def getConstraints(self, var):
        return self.variableDict[var]

    # returns a list of the most constrained variables
    def getMostConstrainedVariables(self):
        variables = filter(lambda x: not x.assigned, self.variableDict.keys())
        curLength = len(variables[0].domain)
        curVariables = [variables[0]]

        for variable in variables[1:]:
            if len(variable.domain) < curLength:
                curVariables = [variable]
                curLength = len(variable.domain)
            elif len(variable.domain) == curLength:
                curVariables.append(variable)
        return curVariables

    # return the most constraining variable out of the list of variables
    def getMostConstrainingVariable(self, varList):
        curVar = varList[0]
        # neither sides of the constraint should be assigned
        curCount = len([constraint for constraint in self.variableDict[curVar]
            if not constraint.left.assigned and not constraint.right.assigned])
        for variable in varList[1:]:
            count = len([constraint for constraint in self.variableDict[variable]
            if not constraint.left.assigned and not constraint.right.assigned])
            if count > curCount:
                curVar = variable
                curCount = count
        return curVar

    def getValuesByConstrainingHeuristic(self, variable):
        # a value's constraining factor will be the maximum amount of
        # values it would remove from any of the effected variables
        constraints = self.getConstraints(variable)
        curDomain = variable.domain
        def getConstrainingFactor(val):
            curMax = 0
            variable.domain = [val]
            for constraint in constraints:
                isLeft = variable is constraint.left
                if isLeft:
                    curSize = len(constraint.right.domain)
                    dif = curSize - len(constraint.filterRightDomain())
                else:
                    curSize = len(constraint.left.domain)
                    dif = curSize - len(constraint.filterLeftDomain())
                    if dif > curMax:
                        curMax = dif
            return curMax
        # want to sort by constraining factor and break ties by value
        curDomain.sort()
        curDomain.sort(key=getConstrainingFactor)
        variable.domain = curDomain
        return curDomain

    def printAssignedState(self, msg):
        variables = filter(lambda x: x.assigned, self.variableDict.keys())
        for variable in variables[:-1]:
            print(variable.label, "=",variable.domain[0],", ", sep="", end="")
        print(variables[-1].label, "=", variable.domain[0], " ", sep="", end="")
        print(msg)
