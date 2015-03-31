# testing
class Clause:
    def __init__(self, literals = None, parents = None):
        self.literals = set()
        self.parents = parents
        if literals:
            self.literals |= set(literals)

    # check if a literal and its negation are both present
    def is_consistent(self):
        for literal in self.literals:
            if negate_literal(literal) in self.literals:
                return False
        return True

    def is_false(self):
        return len(self.literals) == 1 and "False" in self.literals

class KnowledgeBase:
    def __init__(self):
        self.clauses = []

    def is_valid(self):
        clause1Index = 0
        while clause1Index < len(self.clauses):
            for clause2Index in xrange(clause1Index+1, len(self.clauses)):
                newClause = self.attempt_resolution(clause1Index, clause2Index)
                if newClause and newClause.is_false():
                    return False
            clause1Index += 1
        return True

    def get_all_clauses(self):
        return self.clauses

    def add_clause(self, clause):
        self.clauses.append(clause)

    def attempt_resolution(self, clause1Index, clause2Index):
        newClause = None
        for literal in self.clauses[clause1Index].literals:
            for literal2 in self.clauses[clause2Index].literals:
                if negate_literal(literal2) == literal:
                    newClause = self.apply_resolution(clause1Index, clause2Index, literal)
                    break
        return newClause

    # apply resolution and return the new clause
    def apply_resolution(self, clause1Index, clause2Index, literal):
        clause1 = self.clauses[clause1Index]
        clause2 = self.clauses[clause2Index]
        negation = negate_literal(literal)
        literals = set()
        for lit in clause1.literals:
            if lit != literal:
                literals.add(lit)
        for lit in clause2.literals:
            if lit != negation:
                literals.add(lit)
        if len(literals) == 0:
            literals.add("False")
        # create the new clause
        newClause = Clause(literals, [clause1Index, clause2Index])

        if not newClause.is_consistent():
            newClause.literals = set(["False"])
        self.add_clause(newClause)
        return newClause

    def print_resolution_tree(self, clauseIndex):
        clause = self.clauses[clauseIndex]
        if clause.parents:
            self.print_resolution_tree(clause.parents[0])
            self.print_resolution_tree(clause.parents[1])
        print clause.literals

def negate_literal(literal):
    if literal[0] == "~":
        return literal[1:]
    else:
        return "~" + literal
