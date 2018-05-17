package org.numorys.tool.ast;

import java.util.List;

public class Signature extends ASTNode {
	
	public Signature() {
		super();
		
	}

	public Signature(String name) {
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
	
	private Type type;
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	@Override
	void addChildren(List<? extends ASTNode> children) {
		if (children!=null) {
			for (ASTNode n:children) {
				if (n instanceof Type) {
					if (type==null) {
						type=(Type)n;
					} else {
						throw new ASTException("A signature can only contain one type");
					}
				} else {
					throw new ASTException("A signature can only contain one type, not "+n.getClass().getSimpleName());
				}
			}
		}
		
	}
	
	@Override
	public void accept(ASTVisitor v) {
		v.visitSignature(this);
		/*for(Type t:parameterTypes) {
			t.accept(v);
		}*/
	}
	
}
