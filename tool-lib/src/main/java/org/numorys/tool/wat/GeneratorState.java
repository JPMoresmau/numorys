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
import org.numorys.tool.wat.WatInstruction.WatStringValueInstruction;

public class GeneratorState extends ASTVisitor {
	private Map<String, String> function2Wat=new HashMap<>();
	
	private Map<String, WatFunction> watFunctions=new HashMap<>();
	
	private Map<Pair<String,Type>, WatInstruction> watInstructions=new HashMap<>();
	
	private Map<Type, String> type2Wat=new HashMap<>();
	
	private Map<Type, Class<? extends WatStringValueInstruction>> type2Const=new HashMap<>();
	
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
		type2Wat.put(SimpleType.FLOAT_32, "f32");
		type2Wat.put(SimpleType.FLOAT_64, "f64");

		type2Const.put(SimpleType.INT_32, WatInstruction.WatI32Const.class);
		type2Const.put(SimpleType.INT_64, WatInstruction.WatI64Const.class);
		type2Const.put(SimpleType.FLOAT_32, WatInstruction.WatF32Const.class);
		type2Const.put(SimpleType.FLOAT_64, WatInstruction.WatF64Const.class);
			
		
		watInstructions.put(new Pair<>("+",new CompoundType(SimpleType.INT_32,SimpleType.INT_32,SimpleType.INT_32)), WatInstruction.WatI32Add);
		watInstructions.put(new Pair<>("+",new CompoundType(SimpleType.INT_64,SimpleType.INT_64,SimpleType.INT_64)), WatInstruction.WatI64Add);
		watInstructions.put(new Pair<>("+",new CompoundType(SimpleType.FLOAT_32,SimpleType.FLOAT_32,SimpleType.FLOAT_32)), WatInstruction.WatF32Add);
		watInstructions.put(new Pair<>("+",new CompoundType(SimpleType.FLOAT_64,SimpleType.FLOAT_64,SimpleType.FLOAT_64)), WatInstruction.WatF64Add);

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
			Class<? extends WatStringValueInstruction> wiC=type2Const.get(n.getType());
			if (wiC!=null) {
				try {
					WatStringValueInstruction wi=wiC.getConstructor(String.class).newInstance(n.getValue());
					currentFunction.getInstructions().add(wi);
				} catch (Exception e) {
					throw new GenerationException("Cannot create const instruction", e);
				}
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
