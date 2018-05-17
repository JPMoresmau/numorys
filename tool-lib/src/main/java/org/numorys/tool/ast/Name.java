package org.numorys.tool.ast;

import java.util.List;

public class Name extends Expression {
	private String name;
	
	public Name() {
		super();
	}



	public Name(String name) {
		super();
		this.name = name;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	@Override
	void addChildren(List<? extends ASTNode> children) {
		if (children!=null && children.size()>0) {
			throw new ASTException("Names have no children");
		}

	}

	@Override
	public void accept(ASTVisitor v) {
		v.visitName(this);
	}
}
