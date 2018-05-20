package org.numorys.tool.type;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.numorys.tool.ast.ASTNode;
import org.numorys.tool.ast.ASTVisitor;
import org.numorys.tool.ast.Binding;
import org.numorys.tool.ast.CompoundType;
import org.numorys.tool.ast.Expression;
import org.numorys.tool.ast.Function;
import org.numorys.tool.ast.Invocation;
import org.numorys.tool.ast.Name;
import org.numorys.tool.ast.Number;
import org.numorys.tool.ast.Statement;
import org.numorys.tool.ast.Type;

public class BindingChecker extends ASTVisitor {
	private ModuleState moduleState;
	private TypecheckedModule module;
	private Function function;
	private Binding binding;
	
	private Map<String,Type> typeByName=new HashMap<>();
	
	private Map<ASTNode,Boolean> unresolved=new IdentityHashMap<>();
	
	public BindingChecker(ModuleState moduleState, TypecheckedModule module, Function function, Binding binding) {
		super();
		this.moduleState = moduleState;
		this.module = module;
		this.function = function;
		this.binding = binding;
		
		for (Function f:module.getFunctions()) {
			if (f.getSignature()!=null) {
				typeByName.put(f.getName(), f.getSignature().getType());
			}
		}
	}
	
	@Override
	public void visitBinding(Binding b) {
		unresolved.clear();
		List<Type> types=null;
		if (function.getSignature()!=null) {
			types=function.getSignature().getType().getComponents();
			Iterator<Type> it=types.iterator();
			for (Expression e:b.getParameters()) {
				e.setType(it.next());
				if (e instanceof Name) {
					typeByName.put(((Name)e).getName(), e.getType());
				}
			}
			List<Type> remaining=new LinkedList<>();
			while (it.hasNext()) {
				remaining.add(it.next());
			}
			if (remaining.size()==1) {
				b.getExpression().setType(remaining.get(0));
			} else {
				b.getExpression().setType(new CompoundType(remaining));
			}
			
			for (Statement st:b.getStatements()) {
				st.accept(this);
			}
			
			b.getExpression().accept(this);
			
		}
	}
	
	@Override
	public void visitName(Name n) {
		if (n.getType()==null) {
			Type t=typeByName.get(n.getName());
			if (t!=null) {
				unresolved.remove(n);
				n.setType(t);
			} else {
				unresolved.put(n,Boolean.TRUE);
			}
		}
	}

	@Override
	public void visitNumber(Number n) {
		if (n.getType()==null) {
			unresolved.put(n,Boolean.TRUE);
		}
	}
	
	@Override
	public void visitInvocation(Invocation i) {
		int cnt=unresolved.size();
		for (Expression e:i.getExpressions()) {
			e.accept(this);
		}
		if (i.getType()!=null && i.getCallExpression().getType()==null) {
			resolveCallWithParameters(i.getCallExpression(),i.getType(),i.getParametersExpressions());
		} else if (i.getCallExpression().getType()!=null && unresolved.size()>cnt) {
			List<Type> types=i.getCallExpression().getType().getComponents();
			Iterator<Type> it=types.iterator();
			for (Expression e:i.getParametersExpressions()) {
				e.setType(it.next());
				unresolved.remove(e);
			}
			List<Type> remaining=new LinkedList<>();
			while (it.hasNext()) {
				remaining.add(it.next());
			}
			if (remaining.size()==1) {
				i.setType(remaining.get(0));
			} else {
				i.setType(new CompoundType(remaining));
			}
			unresolved.remove(i);
		}
	}
	
	public Set<ASTNode> getUnresolved() {
		return unresolved.keySet();
	}
	
	public void resolveCallWithParameters(Expression call, Type result, List<Expression> params) {
		if (params.size()==0) {
			call.setType(result);
			return;
		} 
		
		CompoundType ct=new CompoundType();
		for (Expression e:params) {
			if (e.getType()==null) {
				unresolved.put(e,Boolean.TRUE);
				unresolved.put(call,Boolean.TRUE);
				return;
			}
			ct.getInnerTypes().add(e.getType());
		}
		ct.getInnerTypes().add(result);
		call.setType(ct);
		
		
	}
}
