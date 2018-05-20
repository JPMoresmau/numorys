package org.numorys.tool.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Invocation extends Expression {
	
	public Invocation() {
		super();
	}
	
	public Invocation(Expression...expressions) {
		super();
		if (expressions!=null && expressions.length>0) {
			this.expressions.addAll(Arrays.asList(expressions));
		}
	}
	
	private boolean infix;
	

	public boolean isInfix() {
		return infix;
	}

	public void setInfix(boolean infix) {
		this.infix = infix;
	}

	private List<Expression> expressions=new LinkedList<>();
	
	public List<Expression> getExpressions() {
		return expressions;
	}
	
	@Override
	void addChildren(List<? extends ASTNode> children) {
		if (children!=null) {
			for (ASTNode n:children) {
				if (n instanceof Expression) {
					expressions.add((Expression)n);
				} else {
					throw new ASTException("An invocation can only contain expressions, not "+n.getClass().getSimpleName());
				}
			}
		}
	}
	
	@Override
	public void accept(ASTVisitor v) {
		v.visitInvocation(this);
		/*for (Expression e:expressions) {
			e.accept(v);
		}*/
	}

	@JsonIgnore
	public Expression getCallExpression() {
		return expressions.get(0);
	}
	
	@JsonIgnore
	public List<Expression> getParametersExpressions(){
		return expressions.subList(1, expressions.size());
	}
	
	@Override
	public void setType(Type type) {
		super.setType(type);
		if (expressions.size()>0) {
			Expression e=getCallExpression();
			if (e.getType()==null) {
				boolean arith=isArithmetic(e);
				List<Type> params=new LinkedList<>();
				int withType=0;
				int withoutType=0;
				Type lastNonNull=null;
				for (Expression p:getParametersExpressions()) {
					params.add(p.getType());
					if (p.getType()==null) {
						withoutType++;
					} else {
						lastNonNull=p.getType();
						withType++;
					}
				}
				if (withoutType>0 && arith && withType>0) {
					List<Type> params2=new LinkedList<>();
					for (Expression p:getParametersExpressions()) {
						Type t=p.getType();
						if (t==null) {
							p.setType(lastNonNull);
							params2.add(lastNonNull);
							withType++;
							withoutType--;
						} else {
							params2.add(t);
						}
					}
					params=params2;
				}
				if (withoutType==0) {
					params.add(type);
					e.setType(new CompoundType(params));
				}
			}
		}
	}
	
	private Set<String> arith=new HashSet<>(Arrays.asList("+","-","*","/"));
	private boolean isArithmetic(Expression e) {
		if (e instanceof Name) {
			String n=((Name)e).getName();
			return arith.contains(n);
		}
		return false;
	}
}
