package org.numorys.tool.ast;

import java.util.LinkedList;
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
	
	private List<Type> parameterTypes=new LinkedList<>();
	
	public List<Type> getParameterTypes() {
		return parameterTypes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parameterTypes == null) ? 0 : parameterTypes.hashCode());
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
		Signature other = (Signature) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parameterTypes == null) {
			if (other.parameterTypes != null)
				return false;
		} else if (!parameterTypes.equals(other.parameterTypes))
			return false;
		return true;
	}
	
	@Override
	void addChildren(List<? extends ASTNode> children) {
		if (children!=null) {
			for (ASTNode n:children) {
				if (n instanceof Type) {
					parameterTypes.add((Type)n);
				} else {
					throw new ASTException("A signature can only contain types, not "+n.getClass().getSimpleName());
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
