import java.util.ArrayList;

/**
 * Created by maxwell on 2/21/15.
 */
public class Variable {
    public boolean isAssigned;
    public String label;
    public ArrayList<Integer> domain;

    public Variable(ArrayList<Integer> d, String token){
        domain = d;
        isAssigned = false;
        label = token;
    }

    public String toString(){
        String msg = String.format("%s: %s", label, domain);
        return msg;
    }
}
