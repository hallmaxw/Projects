import java.util.*;
/**
 * Graph where the nodes are variables and the edges are constraints
 * Created by maxwell on 2/21/15.
 */
public class ConstraintGraph {
    private HashMap<Variable, ArrayList<Constraint>> variableDict;

    public ConstraintGraph(){
        variableDict = new HashMap<Variable, ArrayList<Constraint>>();
    }
    public ArrayList<Constraint> getConstraints(Variable var){
        return variableDict.get(var);
    }

    public void addVariable(Variable var){
        variableDict.put(var, new ArrayList<Constraint>());
    }

    public void addConstraint(Constraint con){
        variableDict.get(con.left).add(con);
        variableDict.get(con.right).add(con);
    }

    public boolean isComplete(){
        for(Variable v : variableDict.keySet()){
            if(!v.isAssigned)
                return false;
        }
        return true;
    }

    // check if a (variable, value) pair matches the variable's constraints
    public boolean isValueConsistent(Variable var, int val){
        for(Constraint con : variableDict.get(var)){
            if(!con.isValueConsistent(var, val))
                return false;
        }
        return true;
    }

    public HashMap<Variable, ArrayList<Integer>> forwardCheck(Variable var) throws Exception{
        HashMap<Variable, ArrayList<Integer>> dif = new HashMap<Variable, ArrayList<Integer>>();
        for(Constraint con: variableDict.get(var)){
            for(Map.Entry<Variable, ArrayList<Integer>> e : con.forwardCheck(var).entrySet()){
                dif.put(e.getKey(), e.getValue());
            }
        }
        return dif;
    }

    // get the most constrained variables of those that are not assigned
    private ArrayList<Variable> getMostConstrainedVariables(){
        ArrayList<Variable> vars = new ArrayList<Variable>();
        int currentLength = Integer.MAX_VALUE;
        for(Variable var : variableDict.keySet()){
            if(var.isAssigned)
                continue;
            if(var.domain.size() < currentLength){
                currentLength = var.domain.size();
                vars.clear();
                vars.add(var);
            }
            else if(var.domain.size() == currentLength){
                vars.add(var);
            }
        }
        return vars;
    }

    // get the number of variables this variable affects
    private int getConstrainingCount(Variable var){
        int count = 0;
        for(Constraint con : variableDict.get(var)){
            boolean isLeft = con.left == var;
            if(isLeft && !con.right.isAssigned)
                count++;
            else if(!isLeft && !con.left.isAssigned)
                count++;
        }
        return count;
    }

    // get the most constraining of the given variables
    private Variable getMostConstrainingVariable(List<Variable> vars){
        int current = -1;
        Variable curVar = null;
        for(Variable var : vars){
            int consCount = getConstrainingCount(var);
            if(consCount > current){
                current = consCount;
                curVar = var;
            }
        }
        return curVar;
    }

    private HashMap<Integer, Integer> getValueToHeuristic(Variable var){
        HashMap<Integer, Integer> valToHeuristic = new HashMap<Integer, Integer>();
        for(int val : var.domain){
            int sum = 0;
            for(Constraint con : variableDict.get(var)){
                int conVal = con.getValueHeuristic(var, val);
                sum += conVal;
            }
            valToHeuristic.put(val, -1*sum);
        }
        return valToHeuristic;
    }

    public Integer[] getValuesByConstrainingHeuristic(Variable var){
        // value to -1*heuristic
        final HashMap<Integer, Integer> valToHeuristic = getValueToHeuristic(var);
        Comparator<Integer> comp = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(valToHeuristic.get(o1), valToHeuristic.get(o2));
            }
        };
        Integer[] values = new Integer[var.domain.size()];
        var.domain.toArray(values);
        Arrays.sort(values, comp);
        return values;
    }

    public Variable getNextVariable(){
        List<Variable> potentials = getMostConstrainedVariables();
        if(potentials.size() == 1){
            return potentials.get(0);
        }
        return getMostConstrainingVariable(potentials);
    }

    public void printAssignedState(String msg){
        String state = "";
        for(Variable var : variableDict.keySet()){
            if(!var.isAssigned)
                continue;
            state += String.format("%s=%d, ", var.label, var.domain.get(0));
        }
        System.out.println(state.substring(0, state.length()-2) + " " + msg);
    }

    // utility function used in testing
    public void printDomains(){
        System.out.println("CONSTRAINT GRAPH:");
        for(Variable var : variableDict.keySet()){
            System.out.println(var);
        }
    }
}
