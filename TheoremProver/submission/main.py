import sys
import KnowledgeBase as k

# INPUT:    a path to a file with line delimited clauses for a knowledge base
#
# OUTPUT:   "Failure" if no contradiction is found
#           The proof tree if a contradiction is found
#           Number of clauses in either case

if __name__ == "__main__":
    if len(sys.argv) != 2:
        sys.exit("Usage: python main.py <knowledge base file>")

    f = None
    try:
        f = open(sys.argv[1], "r")
    except:
        sys.exit("Usage: python main.py <knowledge base file>")
    # build the knowledge base
    kb = k.KnowledgeBase()
    for line in f:
        literals = line.split()
        kb.add_clause(k.Clause(literals))

    # determine if there are any contradictions
    valid = kb.is_valid()
    if valid:
        print "Failure"
    else:
        # find the contradiction
        for clause in kb.clauses:
            if clause.is_false():
                kb.print_resolution_tree(clause)
                break
    print 'Size of final clause set: %(size)d' % \
          {"size": len(kb.clauses)+len(kb.visitedClauses)}
