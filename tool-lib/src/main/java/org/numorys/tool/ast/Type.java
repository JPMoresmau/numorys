package org.numorys.tool.ast;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Type extends ASTNode {
	
	public Type() {
		super();
	}

	@JsonIgnore
	public List<Type> getComponents(){
		return Collections.singletonList(this);
	}
}
