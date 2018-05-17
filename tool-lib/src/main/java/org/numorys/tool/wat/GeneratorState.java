package org.numorys.tool.wat;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.misc.Pair;
import org.numorys.tool.ast.ASTVisitor;
import org.numorys.tool.ast.Binding;
import org.numorys.tool.ast.CompoundType;
import org.numorys.tool.ast.Expression;
import org.numorys.tool.ast.Invocation;
import org.numorys.tool.ast.Name;
import org.numorys.tool.ast.Number;
import org.numorys.tool.ast.SimpleType;
import org.numorys.tool.ast.Statement;
import org.numorys.tool.ast.Type;

public class GeneratorState extends ASTVisitor {
	private Map<String, String> function2Wat=new HashMap<>();
	
	private Map<String, WatFunction> watFunctions=new HashMap<>();
	
	private Map<Pair<String,Type>, WatInstruction> watInstructions=new HashMap<>();
	
	private Map<Type, String> type2Wat=new HashMap<>();
	
	private Map<String, Integer> bindingLocals = new HashMap<>();
	
	private WatFunction currentFunction;
	
	public WatFunction getCurrentFunction() {
		return currentFunction;
	}

	public void setCurrentFunction(WatFunction currentFunction) {
		this.currentFunction = currentFunction;
	}

	public GeneratorState() {
		type2Wat.put(SimpleType.INT_32, "i32");
		type2Wat.put(SimpleType.INT_64, "i64");
		
		watInstructions.put(new Pair<>("+",new CompoundType(SimpleType.INT_32,SimpleType.INT_32,SimpleType.INT_32)), new WatInstruction.WatI32Add());
		watInstructions.put(new Pair<>("+",new CompoundType(SimpleType.INT_64,SimpleType.INT_64,SimpleType.INT_64)), new WatInstruction.WatI64Add());

	}

	public Map<String, String> getFunction2Wat() {
		return function2Wat;
	}
	
	public Map<Type, String> getType2Wat() {
		return type2Wat;
	}
	
	public Map<String, WatFunction> getWatFunctions() {
		return watFunctions;
	}
	
	public Map<Pair<String,Type>, WatInstruction> getWatInstructions() {
		return watInstructions;
	}
	
	@Override
	public void visitBinding(Binding b) {
		bindingLocals.clear();
		int ix=0;
		for (Name n:b.getParameters()) {
			bindingLocals.put(n.getName(), ix);
			ix++;
		}
		for (Statement s:b.getStatements()) {
			s.accept(this);
		}
		b.getExpression().accept(this);
	}
	
	@Override
	public void visitNumber(Number n) {
		if (n.getType()!=null) {
			WatInstruction wi=null;
			
			if (n.getType().equals(SimpleType.INT_32)) {
				wi=new WatInstruction.WatI32Const(n.getValue());
			} else if (n.getType().equals(SimpleType.INT_64)) {
				wi=new WatInstruction.WatI64Const(n.getValue());
			}
			if (wi!=null) {
				currentFunction.getInstructions().add(wi);
			}
		}
	}
	
	@Override
	public void visitName(Name n) {
		Integer i=bindingLocals.get(n.getName());
		if (i!=null) {
			currentFunction.getInstructions().add(new WatInstruction.WatGetLocal(i));
		} else {
			String wat=function2Wat.get(n.getName());
			if (wat!=null) {
				currentFunction.getInstructions().add(new WatInstruction.WatCall(wat));
			} else {
				WatInstruction wi=watInstructions.get(new Pair<>(n.getName(),n.getType()));
				if (wi!=null) {
					currentFunction.getInstructions().add(wi);
				}
			}
		}
		
	}
	
	@Override
	public void visitInvocation(Invocation i) {
		for(Expression e:i.getParametersExpressions()) {
			e.accept(this);
		}
		i.getCallExpression().accept(this);
	}
}
