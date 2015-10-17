package com.junyuan.neuralnetwork;

import com.junyuan.matrix.Matrix;

public class SupportFunction {

	/**
	 * Take a simple double array and turn it into a matrix that can be used to
	 * calculate the results of the input array. Also takes into account the
	 * bias.
	 */
	static Matrix createInputMatrix(final double input[]) {
		final Matrix matrixInputs = new Matrix(1, input.length + 1);
		for (int i = 1; i <= input.length; i++) {
			matrixInputs.set(0, i, input[i - 1]);
		}
		matrixInputs.set(0, 0, 1);
		return matrixInputs;
	}

	//binary sigmoid
	static double sigmoid(double input) {
		return 1.0 / (1 + Math.pow(Math.E, -1.0 * input));
	}

	static double derivativesigmoid(double input) {
		return input * (1.0 - input);
	}

	//bipolar sigmoid
//	static double sigmoid(double input) {
//		return (2.0 / (1.0 + Math.pow(Math.E, -1.0 * input))) - 1.0;
//	}

//	public static double derivativesigmoid(double input) {
//		return 0.5 * (1.0 + input) * (1.0 - input);
//	}

}
