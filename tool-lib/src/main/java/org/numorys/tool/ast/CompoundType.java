package org.numorys.tool.ast;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CompoundType extends Type {
	
	public CompoundType() {
		
	}
	
	public CompoundType(Type...types) {
		innerTypes.addAll(Arrays.asList(types));
	}
	
	public CompoundType(Collection<Type> types) {
		innerTypes.addAll(types);
	}
	
	private List<Type> innerTypes=new LinkedList<>();
	
	public List<Type> getInnerTypes() {
		return innerTypes;
	}
	
	
	@Override
	void addChildren(List<? extends ASTNode> children) {
		if (children!=null) {
			for (ASTNode n:children) {
				if (n instanceof Type) {
					innerTypes.add((Type)n);
				} else {
					throw new ASTException("A module can only contain bindings or signatures, not "+n.getClass().getSimpleName());
				}
			}
		}
		
	}

	@Override
	public void accept(ASTVisitor v) {
		v.visitCompoundType(this);
	}
	
	@Override
	public List<Type> getComponents() {
		return Collections.unmodifiableList(innerTypes);
	}
}
