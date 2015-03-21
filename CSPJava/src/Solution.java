import java.io.FileInputStream;
import java.util.*;

/**
 * Created by maxwell on 2/21/15.
 */
public class Solution {
    static int count = 0;
    public static boolean recursiveBackTrack(ConstraintGraph graph, boolean forwardCheck) throws Exception{
        if(graph.isComplete()){
            graph.printAssignedState("Success");
            return true;
        }
        Variable var = graph.getNextVariable();
        // iterate over the potential values
        for(Integer val : graph.getValuesByConstrainingHeuristic(var)){
            // assign the value and build change map
            HashMap<Variable, ArrayList<Integer>> changes = new HashMap<Variable, ArrayList<Integer>>();
            var.domain.remove(val);
            changes.put(var, var.domain);
            var.domain = new ArrayList<Integer>();
            var.domain.add(val);
            var.isAssigned = true;
            if(graph.isValueConsistent(var, val)) {
                if (forwardCheck) {
                    HashMap<Variable, ArrayList<Integer>> temp = graph.forwardCheck(var);
                    temp.put(var, changes.get(var));
                    changes = temp;
                }
                boolean success = recursiveBackTrack(graph, forwardCheck);
                if (success)
                    return true;
            }
            else{
                if(count < 30) {
                    count++;
                    graph.printAssignedState("Failure");
                }
            }
            // add back the changes
            for(Map.Entry<Variable, ArrayList<Integer>> change : changes.entrySet()){
                for(int value : change.getValue()){
                    change.getKey().domain.add(value);
                }
            }
            var.isAssigned = false;
        }
        return false;
    }

    public static void main(String[] args){
        try {
            if(args.length != 3)
                throw new Exception();
            Scanner varScan = new Scanner(new FileInputStream(args[0]));
            Scanner conScan = new Scanner(new FileInputStream(args[1]));
            HashMap<String, Variable> vars = new HashMap<String, Variable>();
            Scanner line = null;
            // parse variable file
            while (varScan.hasNextLine()) {
                line = new Scanner(varScan.nextLine());
                if (!line.hasNext()) {
                    line.close();
                    break;
                }
                String label = line.next().replace(":", "");
                ArrayList<Integer> domain = new ArrayList<Integer>();
                while (line.hasNextInt())
                    domain.add(line.nextInt());
                line.close();
                Variable var = new Variable(domain, label);
                vars.put(label, var);
            }
            varScan.close();
            ArrayList<Constraint> cons = new ArrayList<Constraint>();

            // parse constraints file
            while (conScan.hasNextLine()) {
                line = new Scanner(conScan.nextLine());
                if (!line.hasNext()) {
                    line.close();
                    break;
                }
                String left = line.next();
                char op = line.next().charAt(0);
                String right = line.next();
                Variable varLeft = vars.get(left);
                Variable varRight = vars.get(right);
                Constraint con = new Constraint(vars.get(left), vars.get(right), op);
                cons.add(con);
                line.close();
            }

            // build constraint graph
            ConstraintGraph graph = new ConstraintGraph();
            for (Variable var : vars.values())
                graph.addVariable(var);
            for (Constraint con : cons)
                graph.addConstraint(con);

            // set forward checking bit
            boolean forwardCheck = false;
            if(args[2].equals("none")){
                forwardCheck = false;
            }
            else if(args[2].equals("fc")){
                forwardCheck = true;
            }
            else{
                throw new Exception();
            }
            // attempt to solve the problem
            recursiveBackTrack(graph, forwardCheck);
        }
        catch(Exception e){
            System.out.println("Usage: java Solution <variable file> <constraint file> [none|fc]");
            System.exit(-1);
        }
    }
}
