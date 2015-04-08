import sys
import KnowledgeBase as k

if __name__ == "__main__":
    f = open(sys.argv[1], "r")
    kb = k.KnowledgeBase()
    for line in f:
        literals = line.split()
        kb.add_clause(k.Clause(literals))
    valid = kb.is_valid()
    if valid:
        print "Failure"
    else:
        for clause in kb.clauses:
            if clause.is_false():
                kb.print_resolution_tree(clause)
                break
    print 'Size of final clause set: %(size)d' % \
          {"size": len(kb.clauses)+len(kb.visitedClauses)}