package org.numorys.tool.ast;

import java.util.List;


public abstract class ASTNode extends ASTItem{

	abstract void addChildren(List<? extends ASTNode> children);

	
}
