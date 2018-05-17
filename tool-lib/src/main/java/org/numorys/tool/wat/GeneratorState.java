package org.numorys.tool.wat;

import java.util.HashMap;
import java.util.Map;

import org.numorys.tool.ast.ASTVisitor;
import org.numorys.tool.ast.Binding;
import org.numorys.tool.ast.Expression;
import org.numorys.tool.ast.Invocation;
import org.numorys.tool.ast.Name;
import org.numorys.tool.ast.Number;
import org.numorys.tool.ast.Statement;
import org.numorys.tool.ast.Type;

public class GeneratorState extends ASTVisitor {
	private Map<String, String> function2Wat=new HashMap<>();
	
	private Map<String, WatFunction> watFunctions=new HashMap<>();
	
	private Map<String, WatInstruction> watInstructions=new HashMap<>();
	
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
		type2Wat.put(Type.INT_32, "i32");
		type2Wat.put(Type.INT_64, "i64");
		
		watInstructions.put("+", new WatInstruction.WatI32Add());
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
	
	public Map<String, WatInstruction> getWatInstructions() {
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
		currentFunction.getInstructions().add(new WatInstruction.WatI32Const(n.getValue()));		
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
				WatInstruction wi=watInstructions.get(n.getName());
				if (wi!=null) {
					currentFunction.getInstructions().add(wi);
				}
			}
		}
		
	}
	
	@Override
	public void visitInvocation(Invocation i) {
		Expression callTarget=null;
		for(Expression e:i.getExpressions()) {
			if (callTarget==null) {
				callTarget=e;
			} else {
				e.accept(this);
			}
		}
		callTarget.accept(this);
	}
}
