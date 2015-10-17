package com.junyuan.neuralnetwork;

import com.junyuan.matrix.Matrix;
import com.junyuan.matrix.MatrixMath;

public class NeuralNetwork {

	// Arguments Constructor Function
	private final int argNumInputs; // Input Neural Number
	private final int argNumHiddens; // Hidden Neural Number
	private final int argNumOutputs; // Output Neural Number
	private final double argLearningRate; // Learning Rate
	private final double argMomentum; // Momentum
	private final double inputPatterns[][]; // Input Patterns 2D array
	private final double idealOutputs[][]; // IdealOutputs
	private double totalError;

	// Argument about output
	private double idealOutput;
	private double actualOutput;
	// Argument about error
	private double errorOutput;
	private double errorHidden;

	// Input Signal to the Neural Network
	private Matrix matrix_InputLayerInputSignal;
	// Weight Between Input and Hidden layer
	private Matrix martix_weight_InputHidden;
	// Backpropagation weight delta
	private Matrix matrix_weight_delta_InputHidden;
	// Backpropagation weight privious delta
	private Matrix matrix_weight_privious_delta_InputHidden;
	// Hidden Layer's input
	private Matrix matrix_HiddenLayerInputSignal;
	// Hidden Layer's output
	private Matrix matrix_HiddenLayerOutputSignal;
	// Weight Between Hidden and Output Layer
	private Matrix matrix_weight_HiddenOutput;
	// delta Weight Between Hidden and Output Layer
	private Matrix matrix_weight_delta_HiddenOutput;
	// delta Weight Between Hidden and Output Layer privious
	private Matrix matrix_weight_privious_delta_HiddenOutput;
	// Output Layer's Input Signal
	private Matrix matrix_OutputLayerInputSignal;

	public NeuralNetwork(final int argNumInputs, final int argNumHiddens, final int argNumOutputs,
			final double argLearningRate, final double argMomentum, final double inputPatterns[][],
			final double idelOutputs[][]) {

		// Arguments
		this.argNumInputs = argNumInputs;
		this.argNumHiddens = argNumHiddens;
		this.argNumOutputs = argNumOutputs;
		this.argLearningRate = argLearningRate;
		this.argMomentum = argMomentum;
		this.inputPatterns = inputPatterns;
		this.idealOutputs = idelOutputs;
		this.totalError = 0;

		// Matrixes
		// Input Layer
		this.matrix_InputLayerInputSignal = new Matrix(1, this.argNumInputs + 1);
		this.martix_weight_InputHidden = new Matrix(this.argNumInputs + 1, this.argNumHiddens);
		this.matrix_weight_delta_InputHidden = new Matrix(this.argNumInputs + 1, this.argNumHiddens);
		this.matrix_weight_privious_delta_InputHidden = new Matrix(this.argNumInputs + 1, this.argNumHiddens);
		// Hidden Layer
		this.matrix_HiddenLayerInputSignal = new Matrix(1, this.argNumInputs);
		this.matrix_HiddenLayerOutputSignal = new Matrix(1, this.argNumHiddens + 1);
		this.matrix_weight_HiddenOutput = new Matrix(this.argNumHiddens + 1, this.argNumOutputs);
		this.matrix_weight_delta_HiddenOutput = new Matrix(this.argNumHiddens + 1, this.argNumOutputs);
		this.matrix_weight_privious_delta_HiddenOutput = new Matrix(this.argNumHiddens + 1, this.argNumOutputs);
		// Output Layer
		this.matrix_OutputLayerInputSignal = new Matrix(1, this.argNumOutputs);

		// Initialize Weights
		this.martix_weight_InputHidden.ramdomize(-0.5, 0.5);
		this.matrix_weight_HiddenOutput.ramdomize(-0.5, 0.5);
	}

	public void train() {
		
		for (int j = 0; j < inputPatterns.length; j++) {
			idealOutput = idealOutputs[j][0];
			FeedForwardCalculation(j);
			BackpropagationCalculation(j);
			learn();
			totalErrorCalculate(j);
		}
		System.out.println(totalError);
	}

	private void FeedForwardCalculation(int j) {

		// [0,0] add a bias at the front [1,0,0]
		matrix_InputLayerInputSignal = SupportFunction.createInputMatrix(inputPatterns[j]);
		// input multiply with weight matrix
		matrix_HiddenLayerInputSignal = MatrixMath.multiply(matrix_InputLayerInputSignal, martix_weight_InputHidden);
		// pass the Hidden Layers input through the activation function and add
		// a bias at the front
		for (int i = 0; i < matrix_HiddenLayerInputSignal.getCols(); i++) {
			matrix_HiddenLayerOutputSignal.set(0, i + 1,
					SupportFunction.sigmoid(matrix_HiddenLayerInputSignal.get(0, i)));
			matrix_HiddenLayerOutputSignal.set(0, 0, 1); // add bias at the
															// front
		}
		// get output result
		matrix_OutputLayerInputSignal = MatrixMath.multiply(matrix_HiddenLayerOutputSignal, matrix_weight_HiddenOutput);
		// pass the output result through a activation function
		actualOutput = SupportFunction.sigmoid(matrix_OutputLayerInputSignal.get(0, 0));
	}

	private void BackpropagationCalculation(int j) {

		errorOutput = (idealOutput - actualOutput) * SupportFunction.derivativesigmoid(actualOutput);

		for (int i = 0; i < matrix_weight_delta_HiddenOutput.getRows(); i++) {
			matrix_weight_delta_HiddenOutput.set(i, 0,
					argLearningRate * errorOutput * matrix_HiddenLayerOutputSignal.get(0, i));
		}

		for (int i = 1; i < matrix_weight_HiddenOutput.getRows(); i++) {
			errorHidden = (errorOutput * matrix_weight_HiddenOutput.get(i, 0))
					* SupportFunction.derivativesigmoid(matrix_HiddenLayerOutputSignal.get(0, i));
			for (int k = 0; k < matrix_weight_delta_InputHidden.getRows(); k++) {
				matrix_weight_delta_InputHidden.set(k, i - 1,
						errorHidden * argLearningRate * matrix_InputLayerInputSignal.get(0, k));
			}
		}

	}

	private void learn() {
		weightMatrixUpdate();
	}
	
	private void totalErrorCalculate(int j) {
		if(j == 0)
			totalError = 0.0;
		totalError = totalError + (idealOutput - actualOutput) * (idealOutput - actualOutput);
		if(j == inputPatterns.length -1 )
			totalError = Math.sqrt(totalError/inputPatterns.length);
	}

	private void weightMatrixUpdate() {

		martix_weight_InputHidden = MatrixMath.add(martix_weight_InputHidden,
				MatrixMath.multiply(matrix_weight_privious_delta_InputHidden, argMomentum));
		martix_weight_InputHidden = MatrixMath.add(martix_weight_InputHidden, matrix_weight_delta_InputHidden);
		matrix_weight_HiddenOutput = MatrixMath.add(matrix_weight_HiddenOutput,
				MatrixMath.multiply(matrix_weight_privious_delta_HiddenOutput, argMomentum));
		matrix_weight_HiddenOutput = MatrixMath.add(matrix_weight_HiddenOutput, matrix_weight_delta_HiddenOutput);
		matrix_weight_privious_delta_InputHidden = matrix_weight_delta_InputHidden;
		matrix_weight_privious_delta_HiddenOutput = matrix_weight_delta_HiddenOutput;
	}
	
	
	public double gettotalError() {
		return this.totalError;
	}
}
