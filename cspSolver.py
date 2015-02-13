from __future__ import print_function
import ConstraintGraph as c

def recursiveBacktrack(graph):
    if graph.isComplete():
        variables = filter(lambda x: x.assigned, graph.variableDict.keys())
        for variable in variables[:-1]:
            print(variable.label, "=",variable.domain[0],", ", sep="", end="")
        print(variables[-1].label, "=", variable.domain[0], " ", sep="", end="")
        print("success")
        return graph
    potentials = graph.getMostConstrainedVariables()
    variable = potentials[0] if len(potentials) == 1 else graph.getMostConstrainingVariable(potentials)
    graph.variableDict[variable]
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
    variables = filter(lambda x: x.assigned, graph.variableDict.keys())
    for variable in variables[:-1]:
        print(variable.label, "=",variable.domain[0],", ", sep="", end="")
    print(variables[-1].label, "=", variable.domain[0], " ", sep="", end="")
    print("failure")
    return None


if __name__ == "__main__":
    labelToVar = {}
    f = open("ex1.var", "r")
    for line in f:
        x = line.find(":")
        label = line[:x]
        domain = [int(y) for y in line[x+1:].split()]
        labelToVar[label] = c.Variable(label, domain)
    graph = c.ConstraintGraph()
    map(lambda x: graph.addVar(x), labelToVar.values())
    f.close()
    f = open("ex1.con", "r")
    for line in f:
        left, op, right = line.split()
        con = c.Constraint(labelToVar[left], labelToVar[right], op)
        graph.addConstraint(con)
    del labelToVar

    for variable in recursiveBacktrack(graph).variableDict.keys():
        print(variable.label, " : ", variable.domain[0])
