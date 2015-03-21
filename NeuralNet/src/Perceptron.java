import java.lang.Math;
/**
 * Created by Maxwell Hall on 2/21/15.
 *
 * Implementation of a single layer perceptron with sigmoid unit values
 * and a binary classification
 */
public class Perceptron {
    private static final double THRESHOLD = 0.5;
    private double[] weights;
    private double learningRate;

    public Perceptron(int numInputs, double rate){
        weights = new double[numInputs];
        learningRate = rate;
    }

    // Get the dot product of weights * attributes
    private double dotproduct(Byte[] attributes){
        double sum = 0;
        for(int x = 0; x < attributes.length; x++){
            sum += weights[x]*attributes[x];
        }
        return sum;
    }

    // get the sigmoid unit output of the perceptron with the given inputs
    public double output(Byte[] attributes){
        return 1/(1+Math.exp(-1*dotproduct(attributes)));
    }

    // classify the instance with the given attributes
    public byte classify(Byte[] attributes){
        return (byte) (output(attributes) >= THRESHOLD ? 1 : 0);
    }

    // train the perceptron with the given training data
    public void train(Byte[] attributes, byte classification){
        double output = output(attributes);
        byte cls = (byte) (output >= THRESHOLD ? 1 : 0);
        if(cls != classification){
            for(int x = 0; x < weights.length; x++){
                weights[x] = weights[x] + learningRate*(classification-output)*output*(1-output)*attributes[x];
            }
        }
    }
}
