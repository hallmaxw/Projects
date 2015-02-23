import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by maxwell on 2/21/15.
 */
public class Constraint {
    public Variable left;
    public Variable right;
    public char op;

    public Constraint(Variable l, Variable r, char o){
        left = l;
        right = r;
        op = o;
    }

    // filter the right variable
    // return the portion of the domain removed
    private ArrayList<Integer> leftForwardCheck() throws Exception{
        if(left.domain.size() != 1){
            throw new Exception("Forward check on variable with more than one value.\n");
        }
        ArrayList<Integer> dif = new ArrayList<Integer>();
        switch(op){
            case '>':
                for(int x = 0; x < right.domain.size(); x++){
                    if(right.domain.get(x) <= left.domain.get(0)){
                        dif.add(right.domain.remove(x));
                        x--;
                    }
                }
                break;
            case '<':
                for(int x = 0; x < right.domain.size(); x++){
                    if(right.domain.get(x) >= left.domain.get(0)){
                        dif.add(right.domain.remove(x));
                        x--;
                    }
                }
                break;
            case '=':
                for(int x = 0; x < right.domain.size(); x++){
                    if(right.domain.get(x) != left.domain.get(0)){
                        dif.add(right.domain.remove(x));
                        x--;
                    }
                }
                break;
            case '!':
                for(int x = 0; x < right.domain.size(); x++){
                    if(right.domain.get(x) == left.domain.get(0)){
                        dif.add(right.domain.remove(x));
                        x--;
                    }
                }
                break;
        }
        return dif;
    }

    // filter the left variable
    // return the portion of the domain that was removed
    private ArrayList<Integer> rightForwardCheck() throws Exception{
        if(right.domain.size() != 1){
            throw new Exception("Forward check on variable with more than one value.\n");
        }
        ArrayList<Integer> dif = new ArrayList<Integer>();
        switch(op){
            case '>':
                for(int x = 0; x < left.domain.size(); x++){
                    if(right.domain.get(0) >= left.domain.get(x)){
                        dif.add(left.domain.remove(x));
                        x--;
                    }
                }
                break;
            case '<':
                for(int x = 0; x < left.domain.size(); x++){
                    if(right.domain.get(0) <= left.domain.get(x)){
                        dif.add(left.domain.remove(x));
                        x--;
                    }
                }
                break;
            case '=':
                for(int x = 0; x < left.domain.size(); x++){
                    if(right.domain.get(0) != left.domain.get(x)){
                        dif.add(left.domain.remove(x));
                        x--;
                    }
                }
                break;
            case '!':
                for(int x = 0; x < left.domain.size(); x++){
                    if(right.domain.get(0) == left.domain.get(x)){
                        dif.add(left.domain.remove(x));
                        x--;
                    }
                }
                break;
        }
        return dif;
    }

    public HashMap<Variable, ArrayList<Integer>> forwardCheck(Variable var) throws Exception{
        boolean isLeft = var == left;
        HashMap<Variable, ArrayList<Integer>> dif = new HashMap<Variable, ArrayList<Integer>>();
        if(isLeft)
            dif.put(right, leftForwardCheck());
        else
            dif.put(left, rightForwardCheck());
        return dif;
    }
    // a value is consistent if it doesn't remove all of the opposing
    // variable's domain
    public boolean isValueConsistent(Variable var, int value){
        boolean isLeft = var == left;
        if(isLeft)
            return right.domain.size()-getLeftValueHeuristic(value) > 0;

        else
            return left.domain.size()-getRightValueHeuristic(value) > 0;

    }

    // a value's heuristic is the number of values it would remove from the opposing
    // variable's domain
    public int getValueHeuristic(Variable var, int value){
        boolean isLeft = var == left;
        if(isLeft)
            return getRightValueHeuristic(value);
        else
            return getLeftValueHeuristic(value);
        
    }

    // a value's heuristic is the number of values it would remove from the left domain
    private int getRightValueHeuristic(int value){
        int removedCount = 0;
        switch(op){
            case '>':
                for(int x = 0; x < left.domain.size(); x++){
                    if(value >= left.domain.get(x)){
                        removedCount++;
                    }
                }
                break;
            case '<':
                for(int x = 0; x < left.domain.size(); x++){
                    if(value <= left.domain.get(x)){
                        removedCount++;
                    }
                }
                break;
            case '=':
                for(int x = 0; x < left.domain.size(); x++){
                    if(value != left.domain.get(x)){
                        removedCount++;
                    }
                }
                break;
            case '!':
                for(int x = 0; x < left.domain.size(); x++){
                    if(value == left.domain.get(x)){
                        removedCount++;
                    }
                }
                break;
        }
        return removedCount;
    }

    // a value's heuristic is the number of values it would remove from the right domain
    private int getLeftValueHeuristic(int value){
        int removedCount = 0;
        switch(op){
            case '>':
                for(int x = 0; x < right.domain.size(); x++){
                    if(right.domain.get(x) <= value){
                        removedCount++;
                    }
                }
                break;
            case '<':
                for(int x = 0; x < right.domain.size(); x++){
                    if(right.domain.get(x) >= value){
                        removedCount++;
                    }
                }
                break;
            case '=':
                for(int x = 0; x < right.domain.size(); x++){
                    if(right.domain.get(x) != value){
                        removedCount++;
                    }
                }
                break;
            case '!':
                for(int x = 0; x < right.domain.size(); x++){
                    if(right.domain.get(x) == value){
                        removedCount++;
                    }
                }
                break;
        }
        return removedCount;
    }
}
