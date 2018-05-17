package org.numorys.tool.ast;

import java.util.List;

public class Statement extends ASTNode {
	private String name;
	
		
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}

	private Expression expression;


	public Expression getExpression() {
		return expression;
	}



	public void setExpression(Expression expression) {
		this.expression = expression;
	}



	@Override
	void addChildren(List<? extends ASTNode> children) {
		if (children!=null && children.size()>0) {
			throw new ASTException("Statements have no children");
		}

	}

	@Override
	public void accept(ASTVisitor v) {
		v.visitStatement(this);
		//expression.accept(v);
	}
}
