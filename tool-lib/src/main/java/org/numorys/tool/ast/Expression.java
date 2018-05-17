package org.numorys.tool.ast;

import java.util.LinkedList;
import java.util.List;

public abstract class Expression extends ASTNode {

	private List<Type> types=new LinkedList<>();
	
	public List<Type> getTypes() {
		return types;
	}
	
}
