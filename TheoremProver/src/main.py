import sys
import KnowledgeBase as k

if __name__ == "__main__":
    f = open(sys.argv[1], "r")
    kb = k.KnowledgeBase()
    for line in f:
        literals = line.split()
        kb.add_clause(k.Clause(literals))
    print kb.is_valid()
    print len(kb.clauses)+len(kb.visitedClauses)
    for clause in kb.clauses:
        if clause.is_false():
            kb.print_resolution_tree(clause)
            break
