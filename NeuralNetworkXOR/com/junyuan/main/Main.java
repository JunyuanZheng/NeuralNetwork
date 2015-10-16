package com.junyuan.main;

import com.junyuan.neuralnetwork.NeuralNetwork;

public class Main {

	public static double XOR_INPUT[][] = { { 0.0, 0.0 }, { 1.0, 0.0 }, { 0.0, 1.0 }, { 1.0, 1.0 } };
	public static double XOR_IDEAL[][] = { {0.0}, {1.0}, {1.0}, {0.0} };
	static double epoch = 0;

	public static void main(final String args[]) {
		NeuralNetwork XOR = new NeuralNetwork(2,4,1,0.2,0,XOR_INPUT, XOR_IDEAL);
		do {
			XOR.train();
			epoch++;
			} while (epoch < 10000);
		XOR.train();
					
	}
}
