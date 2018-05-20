package org.numorys.tool.ast;

import java.util.LinkedList;
import java.util.List;

public class Binding extends ASTNode {
	
	
	
	public Binding(String name) {
		super();
		this.name = name;
	}

	public Binding() {
		super();
		
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private List<Expression> parameters=new LinkedList<>();
	
	public List<Expression> getParameters() {
		return parameters;
	}
	
	private List<Statement> statements=new LinkedList<>();
	
	public List<Statement> getStatements() {
		return statements;
	}
	
	private Expression expression;

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}


	
	@Override
	void addChildren(List<? extends ASTNode> children) {
		if (children!=null) {
			for (ASTNode n:children) {
				if (n instanceof Statement) {
					statements.add((Statement)n);
				} else if (n instanceof Expression) {
					if (expression!=null) {
						if (expression instanceof Invocation) {
							((Invocation)expression).getExpressions().add((Expression)n);
						} else {
							Invocation i=new Invocation(expression,(Expression)n);
							expression=i;
						}
					} else {
						expression=(Expression)n;
					}
				} else {
					throw new ASTException("A binding can only contain statements, not "+n.getClass().getSimpleName());
				}
			}
		}

	}

	
	@Override
	public void accept(ASTVisitor v) {
		v.visitBinding(this);
		/*for (Name n:this.getParameters()) {
			n.accept(v);
		}
		for (Statement s:this.getStatements()) {
			s.accept(v);
		}
		expression.accept(v);*/
	}
}
