package org.numorys.tool.type;

import java.util.LinkedList;
import java.util.List;

import org.numorys.tool.ast.ASTItem;
import org.numorys.tool.ast.ASTVisitor;
import org.numorys.tool.ast.Function;
import org.numorys.tool.ast.Module;

public class TypecheckedModule extends ASTItem {
	
	
	
	public TypecheckedModule() {
		super();
	}

	public TypecheckedModule(Module module) {
		super();
		this.module = (Module)module.clone();
	}

	private Module module;
	
	public Module getModule() {
		return module;
	}
	
	public void setModule(Module module) {
		this.module = module;
	}
	
	private List<Function> functions=new LinkedList<>();
	
	public List<Function> getFunctions() {
		return functions;
	}
	
	private List<TypeCheckError> errors=new LinkedList<>();
	
	public List<TypeCheckError> getErrors() {
		return errors;
	}
	
	@Override
	public void accept(ASTVisitor v) {
		v.visitTypecheckedModule(this);
		for (Function f:functions) {
			f.accept(v);
		}
	}
}
