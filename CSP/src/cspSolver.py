from __future__ import print_function
import ConstraintGraph as c
import sys

def recursiveBacktrack(graph, forwardCheck):
    if graph.isComplete():
        graph.printAssignedState("success")
        return graph
    potentials = graph.getMostConstrainedVariables()
    variable = potentials[0] if len(potentials) == 1 else graph.getMostConstrainingVariable(potentials)
    del potentials
    values = graph.getValuesByConstrainingHeuristic(variable)
    for value in values:
        if graph.isValueConsistent(variable, value):
            variable.assigned = True
            changes = {variable: [x for x in variable.domain if x != value]}
            if forwardCheck:
                constraints = graph.getConstraints(variable)
                print(variable.label, value)
                for constraint in constraints:
                    isLeft = constraint.left is variable
                    affectedVar = None
                    oldDomain = None
                    newDomain = None
                    if isLeft:
                        affectedVar = constraint.right
                        oldDomain = constraint.right.domain
                        constraint.leftForwardCheck()
                        newDomain = constraint.right.domain
                    else:
                        affectedVar = constraint.left
                        oldDomain = constraint.left.domain
                        constraint.rightForwardCheck()
                        newDomain = constraint.left.domain
                    print(affectedVar.label, oldDomain, newDomain)
                    dif = [x for x in oldDomain if x not in newDomain]
                    if affectedVar not in changes:
                        changes[affectedVar] = []
                    map(lambda x: changes[affectedVar].append(x), dif)
            result = recursiveBacktrack(graph, forwardCheck)
            if result != None:
                return result
            for variable, dif in changes.items():
                map(lambda x: variable.domain.append(x), dif)
            variable.assigned = False
    graph.printAssignedState("failure")
    return None


if __name__ == "__main__":
    labelToVar = {}
    f = open(sys.argv[1], "r")
    for line in f:
        x = line.find(":")
        label = line[:x]
        domain = [int(y) for y in line[x+1:].split()]
        labelToVar[label] = c.Variable(label, domain)
    graph = c.ConstraintGraph()
    map(lambda x: graph.addVariable(x), labelToVar.values())
    f.close()
    f = open(sys.argv[2], "r")
    for line in f:
        left, op, right = line.split()
        con = c.Constraint(labelToVar[left], labelToVar[right], op)
        graph.addConstraint(con)
    del labelToVar

    recursiveBacktrack(graph, True)
