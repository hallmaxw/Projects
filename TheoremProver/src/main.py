import sys
import KnowledgeBase as k

f = open(sys.argv[1], "r")

kb = k.KnowledgeBase()
for line in f:
    literals = line.split()
    kb.add_clause(k.Clause(literals))
print kb.is_valid()
print len(kb.clauses)
kb.print_resolution_tree(len(kb.clauses)-1)
