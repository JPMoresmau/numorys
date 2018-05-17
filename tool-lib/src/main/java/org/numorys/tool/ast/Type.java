package org.numorys.tool.ast;

import java.util.List;

public class Type extends ASTNode {
	


	public static final Type INT_32 = new Type("Int32");
	public static final Type INT_64 = new Type("Int64");
	
	public static Type fromString(String s) {
		if (INT_32.getName().equals(s)) {
			return INT_32;
		}
		if (INT_64.getName().equals(s)) {
			return INT_64;
		}
		return new Type(s);
		
	}
	
	public Type() {
		super();
	}
	

	public Type(String name) {
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Type other = (Type) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	void addChildren(List<? extends ASTNode> children) {
		if (children!=null && children.size()>0) {
			throw new ASTException("Types have no children");
		}
		
	}
	
	@Override
	public void accept(ASTVisitor v) {
		v.visitType(this);
	}
}
