import java.util.*;
import java.io.*;
public class Solution{
  public static void main(String[] args) throws Exception{
      Scanner data = new Scanner(new FileInputStream(args[0]));
      Scanner test = new Scanner(new FileInputStream(args[1]));

      int numStates = data.nextInt();
      data.nextLine();
      // process initial state data
      double[] initial = new double[numStates];
      for(int k = 0; k < numStates; k++){
        initial[k] = data.nextDouble();
      }
      data.nextLine();

      // process state transition data
      double[][] transitions = new double[numStates][numStates];
      for(int row = 0; row < numStates; row++){
        for(int col = 0; col < numStates; col++){
          transitions[row][col] = data.nextDouble();
        }
      }
      data.nextLine();

      // process symbol data
      int numSymbols = data.nextInt();
      data.nextLine();
      String[] symbols = new String[numSymbols];
      for(int k = 0; k < numSymbols; k++){
        symbols[k] = data.next();
      }
      data.nextLine();

      // process state output probability data
      double[][] outputDistribution = new double[numStates][numSymbols];
      for(int row = 0; row < numStates; row++){
        for(int col = 0; col < numSymbols; col++){
          outputDistribution[row][col] = data.nextDouble();
        }
      }
      data.close();

      // create the model
      HMM model = new HMM(symbols, initial, transitions, outputDistribution);

      // test the model
      while(test.hasNextLine()){
        String[] observation = test.nextLine().split(" ");
        if(observation.length == 0)
          break;
        for(int s : model.getViterbiDecoding(observation))
          System.out.format("%d ", s+1);
        System.out.println();
      }

  }
}
