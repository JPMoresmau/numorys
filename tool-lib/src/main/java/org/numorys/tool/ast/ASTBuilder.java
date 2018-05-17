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
import org.numorys.tool.parser.NumorysParser.StatementContext;
import org.numorys.tool.parser.NumorysParser.TypeContext;
import org.numorys.tool.parser.NumorysParser.VarContext;

public class ASTBuilder extends NumorysBaseVisitor<List<ASTNode>> {

	@Override
	public List<ASTNode> visitModule(ModuleContext ctx) {
		Module m=new Module(ctx.getChild(1).getText());
		m.addChildren(visitChildren(ctx));
		
		return singleResult(m);
	}
	
	@Override
	public List<ASTNode> visitSignature(SignatureContext ctx) {
		Signature fs=new Signature(ctx.getChild(0).getText());
		fs.addChildren(visitChildren(ctx));
		return singleResult(fs);
	}
	
	@Override
	public List<ASTNode> visitBinding(BindingContext ctx) {
		Binding b=new Binding(ctx.getChild(0).getText());
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
					throw new ASTException("Binding parameters should be single expressions");
				}
				for (ASTNode cn:childResult) {
					if (!(cn instanceof Name)) {
						throw new ASTException("Binding parameters should be names");
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
						throw new ASTException("Bracketed expression should contain expressions only");
					}
					i.getExpressions().add((Expression)cn);
				}
				return singleResult(i);
			}
			return result;
		} else {
			List<ASTNode> result=visitChildren(ctx);
			if (result.size()!=2) {
				throw new ASTException("Infix expression should contain throw children");
			}
			Invocation i=new Invocation();
			i.getExpressions().add(new Name(ctx.getChild(1).getText()));
			for (ASTNode cn:result) {
				if (!(cn instanceof Expression)) {
					throw new ASTException("Infix expression should contain expressions only");
				}
				i.getExpressions().add((Expression)cn);
			}
			i.setInfix(true);
			return singleResult(i);
		}

			
	}

	
	@Override
	public List<ASTNode> visitType(TypeContext ctx) {
		String type=ctx.getText();
		return singleResult(Type.fromString(type));
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
