package com.junyuan.exception;

public class MatrixError extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1703654246570456786L;
	
	/**
	 * Construct this exception with a message.
	 * @param t The other exception.
	 */
	public MatrixError(String message) {
		super(message);
	}

}
