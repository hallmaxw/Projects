class Clause:
    def __init__(self, parents = None):
        self.literals = []
        self.parents = parents


class KnowledgeBase:
    def __init__(self):
        self.clauses = {}

    def getAllClauses(self):
        return set(self.clauses.values())

    def getAllLiterals(self):
        return set(self.clauses.keys())

    def addClause(self, clause):
        for literal in clause.literals:
            if literal not in self.clauses:
                self.clauses[literal] = []
            self.clauses[literal].add(clause)

    def isValid(self):
        while self.attemptResolution():
            continue
        if "False" in self.clauses:
            return False
        return True


    def attemptResolution(self):
        for clause in self.getAllClauses():
            for literal in clause.literals:
                negation = negateLiteral(literal)
                for negClause in self.clauses(negation):
                    if negClause is not clause:
                        # found a clause
                        self.applyResolution(clause, negClause, literal)
                        return True
        return False

    def applyResolution(self, clause1, clause2, literal):
        negation = negateLiteral(literal)
        literals = set()
        for lit in clause1.literals:
            if lit != literal:
                literals.add(lit)
        for lit in clause2.literals:
            if lit != negation:
                literals.add(lit)
        if len(literals) == 0:
            literals.add("False")
        newClause = Clause([clause1, clause2])
        newClause.literals.extend(literals)
        self.addClause(newClause)


def negateLiteral(literal):
    if literal[0] == '~':
        return literal[1:]
    else:
        return '~' + literal