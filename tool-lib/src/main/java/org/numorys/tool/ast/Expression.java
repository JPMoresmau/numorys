package org.numorys.tool.ast;

public abstract class Expression extends ASTNode {

	private Type type;
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
}
