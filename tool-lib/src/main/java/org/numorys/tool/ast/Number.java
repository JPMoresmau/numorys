package org.numorys.tool.ast;

import java.util.List;

public class Number extends Expression {
	private String value;
	
	
	
	public Number() {
		super();
	}

	public Number(String value) {
		super();
		this.value = value;
	}



	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}



	@Override
	void addChildren(List<? extends ASTNode> children) {
		if (children!=null && children.size()>0) {
			throw new ASTException("Numbers have no children");
		}
	}
	
	@Override
	public void accept(ASTVisitor v) {
		v.visitNumber(this);
		
	}

}
