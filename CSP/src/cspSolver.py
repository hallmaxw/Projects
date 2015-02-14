from __future__ import print_function
import ConstraintGraph as c
import sys

def recursiveBacktrack(graph):
    if graph.isComplete():
        graph.printAssignedState("success")
        return graph
    potentials = graph.getMostConstrainedVariables()
    variable = potentials[0] if len(potentials) == 1 else graph.getMostConstrainingVariable(potentials)
    del potentials
    values = graph.getValuesByConstrainingHeuristic(variable)
    for value in values:
        if graph.isValueConsistent(variable, value):
            temp = variable.domain
            variable.domain = [value]
            variable.assigned = True
            result = recursiveBacktrack(graph)
            if result != None:
                return result
            variable.domain = temp
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

    recursiveBacktrack(graph)
