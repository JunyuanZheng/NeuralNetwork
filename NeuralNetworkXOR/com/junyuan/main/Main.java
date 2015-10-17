package com.junyuan.main;

import com.junyuan.neuralnetwork.NeuralNetwork;
import com.junyuan.neuralnetwork.SupportFunction;

public class Main {

	/**
	  public static double XOR_INPUT[][] = { { 0, 0 }, { 1.0, 0 }, { 0, 1.0 },
	  { 1.0, 1.0 } }; public static double XOR_IDEAL[][] = { { 0 }, { 1.0 }, {
	  1.0 }, { 0 } };
	  */

	public static double XOR_INPUT[][] = { { -1.0, -1.0 } , { -1.0, 1.0 }, { 1.0, -1.0 }, { 1.0, 1.0 }};
	public static double XOR_IDEAL[][] = { { -1.0 }, { 1.0 }, { 1.0 }, { -1.0 }};
	public static double epch = 0.0;

	public static void main(final String args[]) {
		NeuralNetwork XOR = new NeuralNetwork(2, 4, 1, 0.2, 0.9, XOR_INPUT, XOR_IDEAL);
		
		do {
			XOR.train();
			epch++;
		} while ((XOR.gettotalError() > 0.05) && (epch < 30000));
		XOR.train();
	}
}
