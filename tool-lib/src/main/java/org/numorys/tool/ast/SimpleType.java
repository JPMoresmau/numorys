package org.numorys.tool.ast;

import java.util.List;

public class SimpleType extends Type {
	
	public static final SimpleType INT_32 = new SimpleType("Int32");
	public static final SimpleType INT_64 = new SimpleType("Int64");
	
	public SimpleType() {
		
	}
		

	public SimpleType(String name) {
		super();
		this.name = name;
	}



	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	@Override
	void addChildren(List<? extends ASTNode> children) {
		if (children!=null && children.size()>0) {
			throw new ASTException("Simple types have no children");
		}
		
	}
	
	@Override
	public void accept(ASTVisitor v) {
		v.visitSimpleType(this);
	}
}
