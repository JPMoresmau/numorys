package org.numorys.tool.ast;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.numorys.tool.parser.NumorysBaseVisitor;
import org.numorys.tool.parser.NumorysLexer;
import org.numorys.tool.parser.NumorysParser.BindingContext;
import org.numorys.tool.parser.NumorysParser.ExprContext;
import org.numorys.tool.parser.NumorysParser.ModuleContext;
import org.numorys.tool.parser.NumorysParser.NumContext;
import org.numorys.tool.parser.NumorysParser.SignatureContext;
import org.numorys.tool.parser.NumorysParser.Simple_typeContext;
import org.numorys.tool.parser.NumorysParser.StatementContext;
import org.numorys.tool.parser.NumorysParser.TypeContext;
import org.numorys.tool.parser.NumorysParser.VarContext;

public class ASTBuilder extends NumorysBaseVisitor<List<ASTNode>> {

	@Override
	public List<ASTNode> visitModule(ModuleContext ctx) {
		String name=checkModuleName(ctx.getChild(1));
		Module m=new Module(name);
		m.addChildren(visitChildren(ctx));
		
		return singleResult(m);
	}
	
	private String checkModuleName(ParseTree c) {
		String name=c.getText();
		char first=name.charAt(0);
		if (first=='_') {
			if (name.length()==1) {
				throw new ASTException("Module names cannot be a single underscore",c.getSourceInterval());
			}
			first=name.charAt(1);
		}
		if (!Character.isUpperCase(first)) {
			throw new ASTException("Module names should start with an upper case letter or a underscore and a upper case letter",c.getSourceInterval());
		}
		return name;
	}
	
	private String checkFunctionName(ParseTree c) {
		String name=c.getText();
		char first=name.charAt(0);
		if (first=='_') {
			if (name.length()==1) {
				throw new ASTException("Function names cannot be a single underscore",c.getSourceInterval());
			}
			first=name.charAt(1);
		}
		if (!Character.isLowerCase(first)) {
			throw new ASTException("Function names should start with an lower case letter or a underscore and a lower case letter",c.getSourceInterval());
			
		}
		return name;
	}
	
	@Override
	public List<ASTNode> visitSignature(SignatureContext ctx) {
		Signature fs=new Signature(checkFunctionName(ctx.getChild(0)));
		fs.addChildren(visitChildren(ctx));
		return singleResult(fs);
	}
	
	@Override
	public List<ASTNode> visitBinding(BindingContext ctx) {
		Binding b=new Binding(checkFunctionName(ctx.getChild(0)));
		int n = ctx.getChildCount();
		List<ASTNode> result = defaultResult();
		
		boolean param=true;
		for (int i=1; i<n; i++) {
			if (!shouldVisitNextChild(ctx, result)) {
				break;
			}
			
			ParseTree c = ctx.getChild(i);
			if (c.getPayload() instanceof Token) {
				Token t=(Token)c.getPayload();
				if (t.getType()==NumorysLexer.EQUALS) {
					param=false;
				}
			}
			List<ASTNode> childResult = c.accept(this);
			if (param) {
				if (childResult.size()!=1) {
					throw new ASTException("Binding parameters should be single expressions",c.getSourceInterval());
				}
				for (ASTNode cn:childResult) {
					if (!(cn instanceof Name)) {
						throw new ASTException("Binding parameters should be names",c.getSourceInterval());
					}
					b.getParameters().add((Name)cn);
				}
			} else {
				result = aggregateResult(result, childResult);
			}
		}
		b.addChildren(result);
		return singleResult(b);
	}
	
	
	@Override
	public List<ASTNode> visitStatement(StatementContext ctx) {
		Statement st=new Statement();
		st.setName(ctx.getChild(0).getText());
		
		st.addChildren(visitChildren(ctx));
		return singleResult(st);
	}
	
	@Override
	public List<ASTNode> visitExpr(ExprContext ctx) {
		if (ctx.getChildCount()==1) {
			return super.visitExpr(ctx);
		}
		if (ctx.getChild(0).getText()=="(") {
			int n = ctx.getChildCount();
			List<ASTNode> result = defaultResult();
			
			for (int i=1; i<n-1; i++) {
				if (!shouldVisitNextChild(ctx, result)) {
					break;
				}
				ParseTree c = ctx.getChild(i);
				List<ASTNode> childResult = c.accept(this);
				result = aggregateResult(result, childResult);
			}
			if (result.size()>1) {
				Invocation i=new Invocation();
				for (ASTNode cn:result) {
					if (!(cn instanceof Expression)) {
						throw new ASTException("Bracketed expression should contain expressions only",ctx.getSourceInterval());
					}
					i.getExpressions().add((Expression)cn);
				}
				return singleResult(i);
			}
			return result;
		} else {
			List<ASTNode> result=visitChildren(ctx);
			if (result.size()!=2) {
				throw new ASTException("Infix expression should contain throw children",ctx.getSourceInterval());
			}
			Invocation i=new Invocation();
			i.getExpressions().add(new Name(ctx.getChild(1).getText()));
			for (ASTNode cn:result) {
				if (!(cn instanceof Expression)) {
					throw new ASTException("Infix expression should contain expressions only",ctx.getSourceInterval());
				}
				i.getExpressions().add((Expression)cn);
			}
			i.setInfix(true);
			return singleResult(i);
		}

			
	}

	
	@Override
	public List<ASTNode> visitType(TypeContext ctx) {
		List<ASTNode> result=visitChildren(ctx);
		if (result.size()==1) {
			return result;
		}
		CompoundType ct=new CompoundType();
		ct.addChildren(result);
		return singleResult(ct);
	}
	
	@Override
	public List<ASTNode> visitSimple_type(Simple_typeContext ctx) {
		String type=ctx.getText();
		return singleResult(new SimpleType(type));
	}
	
	@Override
	public List<ASTNode> visitVar(VarContext ctx) {
		Name n=new Name(ctx.getChild(0).toString());
		return singleResult(n);
	}
	
	@Override
	public List<ASTNode> visitNum(NumContext ctx) {
		Number n=new Number(ctx.getChild(0).toString());
		return singleResult(n);
	}
	
	protected List<ASTNode> singleResult(ASTNode n) {
		List<ASTNode> l=defaultResult();
		l.add(n);
		return l;
	}
	
	@Override
	protected List<ASTNode> defaultResult() {
		return new LinkedList<>();
	}
	
	@Override
	protected List<ASTNode> aggregateResult(List<ASTNode> aggregate, List<ASTNode> nextResult) {
		aggregate.addAll(nextResult);
		return aggregate;
	}
}
