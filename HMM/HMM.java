import java.util.Arrays;
import java.io.*;
public class HMM{
  private String[] symbols;
  private double[] initial;
  private double[][] transitions;
  private double[][] outputDistribution;

  public HMM(String[] symbols, double[] initial, double[][] transitions, double[][] outputDistribution){
    this.symbols = Arrays.copyOf(symbols, symbols.length);
    this.initial = Arrays.copyOf(initial, initial.length);
    this.transitions = new double[transitions.length][];
    for(int i = 0; i < transitions.length; i++){
      this.transitions[i] = Arrays.copyOf(transitions[i], transitions[i].length);
    }
    this.outputDistribution = new double[outputDistribution.length][];
    for(int i = 0; i < transitions.length; i++){
      this.outputDistribution[i] = Arrays.copyOf(outputDistribution[i], outputDistribution[i].length);
    }
  }
  private int getSymbolIndex(String observation){
    for(int i = 0; i < symbols.length; i++){
      if(symbols[i].equals(observation))
        return i;
    }
    return -1;
  }
  public int[] getViterbiDecoding(String[] observation){
    double[][] probs = new double[initial.length][observation.length];
    int[][] parents = new int[initial.length][observation.length-1];
    // build first column
    int index = getSymbolIndex(observation[0]);
    for(int i = 0; i < initial.length; i++){
      probs[i][0] = initial[i]*outputDistribution[i][index];
    }

    for(int k = 1; k < observation.length; k++){
      // current time stamp is k
      index = getSymbolIndex(observation[k]);
      for(int i = 0; i < initial.length; i++){
        // current state is i
        double currentMax = -1;
        int parent = -1;
        for(int j = 0; j < initial.length; j++){
          // checking state j as a potential parent
          double tempProb = probs[j][k-1]*transitions[j][i]*outputDistribution[i][index];
          if(tempProb > currentMax){
            currentMax = tempProb;
            parent = j;
          }
        }
        probs[i][k] = currentMax;
        parents[i][k-1] = parent;
      }
    }

    int[] states = new int[observation.length];
    int curState = 0;
    index = observation.length-1;
    // get last state
    for(int k = 1; k < initial.length; k++){
      if(probs[k][index] > probs[curState][index]){
        curState = k;
      }
    }
    states[index] = curState;
    for(int k = states.length-2; k >= 0; k--){
      states[k] = parents[states[k+1]][k];
    }
    return states;
  }
}
