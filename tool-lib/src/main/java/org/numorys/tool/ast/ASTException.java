package org.numorys.tool.ast;

import org.antlr.v4.runtime.misc.Interval;

public class ASTException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3680037119112314357L;

	private Interval sourceInterval;
	
	
	public ASTException(String arg0, Interval sourceInterval, Throwable arg1) {
		super(arg0, arg1);
		this.sourceInterval=sourceInterval;
	}

	public ASTException(String arg0,Interval sourceInterval) {
		super(arg0);
		this.sourceInterval=sourceInterval;
	}

	public ASTException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ASTException(String arg0) {
		super(arg0);
	}

	
	public Interval getSourceInterval() {
		return sourceInterval;
	}
	
	@Override
	public String toString() {
		String s= super.toString();
		if (sourceInterval!=null) {
			s+=" ("+sourceInterval+")";
		}
		return s;
	}
}
