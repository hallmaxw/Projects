import heapq


def negate_literal(literal):
    if literal[0] == "~":
        return literal[1:]
    else:
        return "~" + literal


class Clause:
    def __init__(self, literals=None, parents=None):
        self.literals = set()
        self.parents = parents
        if literals:
            self.literals |= set(literals)

    # two clauses are equivalent if they have the same literals
    def __eq__(self, other):
        intersection = self.literals & other.literals
        if len(intersection) == len(self.literals) and len(intersection) == len(other.literals):
            return True
        return False

    # two clauses are not equivalent if they do not have the same literals
    def __ne__(self, other):
        intersection = self.literals & other.literals
        if len(intersection) == len(self.literals) and len(intersection) == len(other.literals):
            return False
        return True

    # order clauses by number of literals
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
        return string.rstrip()

    # a clause is false if it has a single literal, "False"
    def is_false(self):
        return len(self.literals) == 1 and "False" in self.literals


# Representation of a Knowledge Base made up of a conjunction of clauses
class KnowledgeBase:
    # clauses: min-heap of clauses
    # visitedClauses: list of clauses that have been tested against the KB for resolution potential
    def __init__(self):
        self.clauses = []
        self.visitedClauses = []

    def pop_clause(self):
        return heapq.heappop(self.clauses)

    def add_clause(self, clause):
        heapq.heappush(self.clauses, clause)

    # check if the KB is valid
    # the KB is valid if there are no contradictions
    # remember the generated clauses
    def is_valid(self):
        while len(self.clauses) > 0:
            clause1 = self.pop_clause()
            self.visitedClauses.append(clause1)
            # iterate over the current clauses (no new clauses considered)
            for clause2 in self.clauses[:]:
                newClause = self.attempt_resolution(clause1, clause2)
                # if a new clause was generated and it's false, there was a contradiction
                if newClause and newClause.is_false():
                    return False
        return True

    def __contains__(self, clause):
        return clause in self.clauses or clause in self.visitedClauses

    # If resolution is possible, get the result of resolution
    # Else, return None
    def attempt_resolution(self, clause1, clause2):
        newClause = None
        for literal in clause1.literals:
            for literal2 in clause2.literals:
                if negate_literal(literal2) == literal:
                    newClause = self.apply_resolution(clause1, clause2, literal)
                    break
        return newClause

    # resolve clause1 and clause2
    # it is assumed that literal is a literal in clause1
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
        # store the new clause if it hasn't been stored already
        if newClause not in self:
            self.add_clause(newClause)
        return newClause

    def print_resolution_tree(self, clause, depth=0):
        print " |"*depth + str(clause)
        if clause.parents:
            self.print_resolution_tree(clause.parents[0], depth+1)
            self.print_resolution_tree(clause.parents[1], depth+1)
