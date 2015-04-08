import heapq


class Clause:
    def __init__(self, literals = None, parents = None):
        self.literals = set()
        self.parents = parents
        if literals:
            self.literals |= set(literals)

    # comparison for use in the heap
    def __cmp__(self, other):
        if len(self.literals) > len(other.literals):
            return 1
        elif len(self.literals) < len(other.literals):
            return -1
        else:
            return 0

    def __str__(self):
        string = ""
        for literal in self.literals:
            string += literal + " "
        return string

    def is_false(self):
        return len(self.literals) == 1 and "False" in self.literals

    def is_true(self):
        for literal in self.literals:
            if negate_literal(literal) in self.literals:
                return True
        return False


class KnowledgeBase:
    def __init__(self):
        self.clauses = []
        self.visitedClauses = []

    def is_valid(self):
        while len(self.clauses) > 0:
            clause1 = heapq.heappop(self.clauses)
            self.visitedClauses.append(clause1)
            for clause2 in self.clauses[:]:
                newClause = self.attempt_resolution(clause1, clause2)
                if newClause and newClause.is_false():
                    return False
        return True

    def get_all_clauses(self):
        return self.clauses.extend(self.resolvedClauses)

    def add_clause(self, clause):
        heapq.heappush(self.clauses, clause)

    def attempt_resolution(self, clause1, clause2):
        newClause = None
        for literal in clause1.literals:
            for literal2 in clause2.literals:
                if negate_literal(literal2) == literal:
                    newClause = self.apply_resolution(clause1, clause2, literal)
                    break
        return newClause

    # apply resolution and return the new clause
    def apply_resolution(self, clause1, clause2, literal):
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
        newClause = Clause(literals, [clause1, clause2])
        self.add_clause(newClause)
        return newClause

    def print_resolution_tree(self, clause, depth = 0):
        print " |"*depth + str(clause)
        if clause.parents:
            self.print_resolution_tree(clause.parents[0], depth+1)
            self.print_resolution_tree(clause.parents[1], depth+1)


def negate_literal(literal):
    if literal[0] == "~":
        return literal[1:]
    else:
        return "~" + literal
