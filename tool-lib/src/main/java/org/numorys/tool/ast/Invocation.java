package org.numorys.tool.ast;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Invocation extends Expression {
	
	public Invocation() {
		super();
	}
	
	public Invocation(Expression...expressions) {
		super();
		if (expressions!=null && expressions.length>0) {
			this.expressions.addAll(Arrays.asList(expressions));
		}
	}
	
	private boolean infix;
	

	public boolean isInfix() {
		return infix;
	}

	public void setInfix(boolean infix) {
		this.infix = infix;
	}

	private List<Expression> expressions=new LinkedList<>();
	
	public List<Expression> getExpressions() {
		return expressions;
	}
	
	@Override
	void addChildren(List<? extends ASTNode> children) {
		if (children!=null) {
			for (ASTNode n:children) {
				if (n instanceof Expression) {
					expressions.add((Expression)n);
				} else {
					throw new ASTException("An invocation can only contain expressions, not "+n.getClass().getSimpleName());
				}
			}
		}
	}
	
	@Override
	public void accept(ASTVisitor v) {
		v.visitInvocation(this);
		/*for (Expression e:expressions) {
			e.accept(v);
		}*/
	}

}
