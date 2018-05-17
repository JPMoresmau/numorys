package org.numorys.tool.ast;

import java.util.LinkedList;
import java.util.List;

public class Function extends ASTNode {
	
	
	
	public Function() {
		super();

	}

	
	
	public Function(String name) {
		super();
		this.name = name;
	}



	public Function(String name, Signature signature) {
		super();
		this.name = name;
		this.signature = signature;
	}



	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private Signature signature;



	public Signature getSignature() {
		return signature;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}

	
	private List<Binding> bindings=new LinkedList<>();
	
	public List<Binding> getBindings() {
		return bindings;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((signature == null) ? 0 : signature.hashCode());
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
		Function other = (Function) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (signature == null) {
			if (other.signature != null)
				return false;
		} else if (!signature.equals(other.signature))
			return false;
		return true;
	}
	
	@Override
	void addChildren(List<? extends ASTNode> children) {
		if (children!=null && children.size()>0) {
			throw new ASTException("Functions have no generic children");
		}
	}
	
	@Override
	public void accept(ASTVisitor v) {
		v.visitFunction(this);
		/*if (signature!=null) {
			signature.accept(v);
		}
		for (Binding b:getBindings()) {
			b.accept(v);
		}*/
	}
	
}
