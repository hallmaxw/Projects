# testing
class Clause:
    def __init__(self, literals = None, parents = None):
        self.literals = set()
        self.resolved = False
        self.parents = parents
        if literals:
            self.literals.add(literals)

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
        self.clauses = {}

    def get_all_clauses(self):
        return set(self.clauses.values())

    def get_all_literals(self):
        return set(self.clauses.keys())

    def add_clause(self, clause):
        for literal in clause.literals:
            if literal not in self.clauses:
                self.clauses[literal] = []
            self.clauses[literal].add(clause)

    def is_valid(self):
        while self.attempt_resolution():
            continue
        if "False" in self.clauses:
            return False
        return True


    def attempt_resolution(self):
        for clause in self.get_all_clauses():
            for literal in clause.literals:
                negation = negate_literal(literal)
                for negClause in self.clauses[negation]:
                    if negClause is not clause:
                        # found a clause
                        newClause = self.apply_rsetesolution(clause, negClause, literal)
                        return not newClause.is_false()
        return False

    # apply resolution and return the new clause
    def apply_resolution(self, clause1, clause2, literal):
        clause1.resolved = True
        clause2.resolved = True
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
        newClause = Clause([clause1, clause2], literals)
        if not newClause.is_consistent():
            newClause.literals = set([False])
        self.add_clause(newClause)
        return newClause


def negate_literal(literal):
    if literal[0] == '~':
        return literal[1:]
    else:
        return '~' + literal
