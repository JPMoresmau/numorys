package org.numorys.tool.ast;

import java.util.LinkedList;
import java.util.List;

public class Module extends ASTNode {
	
	public Module() {
		super();
		
	}
	
	

	public Module(String name) {
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
	
	private List<Binding> bindings=new LinkedList<>();
	
	public List<Binding> getBindings() {
		return bindings;
	}
	
	private List<Signature> signatures=new LinkedList<>();
	
	public List<Signature> getSignatures() {
		return signatures;
	}
	
	@Override
	public void addChildren(List<? extends ASTNode> children) {
		if (children!=null) {
			for (ASTNode n:children) {
				if (n instanceof Binding) {
					bindings.add((Binding)n);
				} else if (n instanceof Signature) {
					signatures.add((Signature)n);
				} else {
					throw new ASTException("A module can only contain bindings or signatures, not "+n.getClass().getSimpleName());
				}
			}
		}
	}

	@Override
	public void accept(ASTVisitor v) {
		v.visitModule(this);
	}
}
