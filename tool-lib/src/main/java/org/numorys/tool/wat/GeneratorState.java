package org.numorys.tool.wat;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
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
	
	private Deque<WatBlock> blocks=new LinkedList<>();
	
	

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

		watInstructions.put(new Pair<>("-",new CompoundType(SimpleType.INT_32,SimpleType.INT_32,SimpleType.INT_32)), WatInstruction.WatI32Sub);
		watInstructions.put(new Pair<>("-",new CompoundType(SimpleType.INT_64,SimpleType.INT_64,SimpleType.INT_64)), WatInstruction.WatI64Sub);
		watInstructions.put(new Pair<>("-",new CompoundType(SimpleType.FLOAT_32,SimpleType.FLOAT_32,SimpleType.FLOAT_32)), WatInstruction.WatF32Sub);
		watInstructions.put(new Pair<>("-",new CompoundType(SimpleType.FLOAT_64,SimpleType.FLOAT_64,SimpleType.FLOAT_64)), WatInstruction.WatF64Sub);
		
		watInstructions.put(new Pair<>("*",new CompoundType(SimpleType.INT_32,SimpleType.INT_32,SimpleType.INT_32)), WatInstruction.WatI32Mul);
		watInstructions.put(new Pair<>("*",new CompoundType(SimpleType.INT_64,SimpleType.INT_64,SimpleType.INT_64)), WatInstruction.WatI64Mul);
		watInstructions.put(new Pair<>("*",new CompoundType(SimpleType.FLOAT_32,SimpleType.FLOAT_32,SimpleType.FLOAT_32)), WatInstruction.WatF32Mul);
		watInstructions.put(new Pair<>("*",new CompoundType(SimpleType.FLOAT_64,SimpleType.FLOAT_64,SimpleType.FLOAT_64)), WatInstruction.WatF64Mul);


		watInstructions.put(new Pair<>("==",new CompoundType(SimpleType.INT_32,SimpleType.INT_32,SimpleType.INT_32)), WatInstruction.WatI32Eq);
		watInstructions.put(new Pair<>("==",new CompoundType(SimpleType.INT_64,SimpleType.INT_64,SimpleType.INT_64)), WatInstruction.WatI64Eq);
		watInstructions.put(new Pair<>("==",new CompoundType(SimpleType.FLOAT_32,SimpleType.FLOAT_32,SimpleType.FLOAT_32)), WatInstruction.WatF32Eq);
		watInstructions.put(new Pair<>("==",new CompoundType(SimpleType.FLOAT_64,SimpleType.FLOAT_64,SimpleType.FLOAT_64)), WatInstruction.WatF64Eq);

	}

	public WatBlock getCurrentBlock() {
		return blocks.peek();
	}

	public void addInstruction(WatInstruction wi) {
		getCurrentBlock().addInstruction(wi);
	}
	
	public Deque<WatBlock> getBlocks() {
		return blocks;
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
	
	public WatInstruction getWatInstruction(String name, Type type) {
		return watInstructions.get(new Pair<>(name, type));
	}
	
	@Override
	public void visitBinding(Binding b) {
		bindingLocals.clear();
		int ix=0;
		int depth=0;
		for (Expression p:b.getParameters()) {
			if (p instanceof Name) {
				bindingLocals.put(((Name)p).getName(), ix);
			} else if (p instanceof Number) {
				addInstruction(new WatInstruction.WatGetLocal(ix));
				((Number)p).accept(this);
				Type eqType=new CompoundType(p.getType(),p.getType(),p.getType());
				WatInstruction wi=getWatInstruction("==", eqType);
				if (wi!=null) {
					addInstruction(wi);
					WatInstruction.WatIf wif=new WatInstruction.WatIf(type2Wat.get(b.getExpression().getType()));
					addInstruction(wif);
					blocks.push(wif.getBlock());
					depth++;
				} else {
					throw new GenerationException("Cannot create == instruction for "+p.getType());
				}
			}
			ix++;
		}
		for (Statement s:b.getStatements()) {
			s.accept(this);
		}
		b.getExpression().accept(this);
		for (int a=0;a<depth;a++) {
			blocks.pop();
			if (a==depth-1) {
				WatInstruction.WatElse we=new WatInstruction.WatElse();
				addInstruction(we);
				blocks.push(we.getBlock());
			}
		}
		
	}
	
	@Override
	public void visitNumber(Number n) {
		if (n.getType()!=null) {
			Class<? extends WatStringValueInstruction> wiC=type2Const.get(n.getType());
			if (wiC!=null) {
				try {
					WatStringValueInstruction wi=wiC.getConstructor(String.class).newInstance(n.getValue());
					addInstruction(wi);
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
			getCurrentBlock().getInstructions().add(new WatInstruction.WatGetLocal(i));
		} else {
			String wat=function2Wat.get(n.getName());
			if (wat!=null) {
				getCurrentBlock().getInstructions().add(new WatInstruction.WatCall(wat));
			} else {
				WatInstruction wi=watInstructions.get(new Pair<>(n.getName(),n.getType()));
				if (wi!=null) {
					getCurrentBlock().getInstructions().add(wi);
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
