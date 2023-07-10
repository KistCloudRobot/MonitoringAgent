package kr.ac.uos.ai.mcarbi.monitor.utility;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kr.ac.uos.ai.arbi.model.GLFactory;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.ParseException;
import uos.ai.jam.Interpreter;
import uos.ai.jam.expression.Expression;
import uos.ai.jam.expression.Relation;
import uos.ai.jam.expression.Symbol;
import uos.ai.jam.expression.SymbolTable;
import uos.ai.jam.expression.Value;
import uos.ai.jam.expression.Variable;

public class WorldModelHandler {

	private Interpreter			interpreter;
	
	public WorldModelHandler(Interpreter interpreter) {
		this.interpreter = interpreter;
	}
	

	public void assertObject(String name, Object... args) {
		interpreter.getWorldModel().assertFact(newRelation(name, args), null);
	}

	public void assertGL(String input) {
		interpreter.getWorldModel().assertFact(newRelationFromGL(input), null);

	}
	public void retractGL(String input) {
		interpreter.getWorldModel().retract(newRelationFromGL(input), null);
	}

	public void updateGL(String context) {
		String oldContext = "";
		String newContext = "";
//		System.out.println(context);
		if (context.startsWith("(")) {
			try {
				GeneralizedList gl = GLFactory.newGLFromGLString(context);
				oldContext = gl.getExpression(0).asGeneralizedList().toString();
				newContext = gl.getExpression(1).asGeneralizedList().toString();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

//		System.out.println("retract");
		this.retractGL(oldContext);
//		System.out.println("assert");
		this.assertGL(newContext);
//		System.out.println("asserted");
	}
	
	private Relation newRelation(String name, Object... args) {

		LinkedList<Expression> expList = new LinkedList<Expression>();
		if(args != null) {
			for (Object o : args) {
				Value v = null;
				if(o instanceof String){
					String argument = (String)o;
					v = new Value(argument);
				}else if(o instanceof Float) {
					Float a = (Float)o;
					v = new Value(a.doubleValue());
				}else if(o instanceof Integer){
					Integer i = (Integer)o;
					v = new Value(i.intValue());
				}else {
					v = new Value(o);
				}
				expList.add(v);
			}
		}
		
		Relation r = interpreter.getWorldModel().newRelation(name, expList);

		return r;
	}

	private Relation newRelationFromGL(String stringGL) {
		String name = "";
		LinkedList<Expression> expList = new LinkedList<Expression>();
		
		if (stringGL.startsWith("(")) {

			try {
				GeneralizedList gl = GLFactory.newGLFromGLString(stringGL);
				name = gl.getName();
				
				for (int i = 0; i < gl.getExpressionsSize(); i++) {
					kr.ac.uos.ai.arbi.model.Expression expression = gl.getExpression(i);
					Expression e = null;
					
					if (expression.isGeneralizedList()) {
						String glString = gl.getExpression(i).toString();
						String argument = GLFactory.unescape(glString);
						e = new Value(argument);
					} else if (expression.isValue()){
						kr.ac.uos.ai.arbi.model.Value value = expression.asValue();
						if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.FLOAT) {
							Float a = value.floatValue();
							e = new Value(a.doubleValue());
						} else if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.INT) {
							Integer intVal = value.intValue();
							e = new Value(intVal.intValue());
						} else if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.STRING) {
							String argument = GLFactory.unescape(value.stringValue());
							e = new Value(argument);
						} 
					} else if (expression.isVariable()) {
						kr.ac.uos.ai.arbi.model.Variable variable = expression.asVariable();
						e = new Variable(new Symbol(variable.getName()));
					} else if (expression.isFunction()) {
						//TODO GL Function
						System.out.println("GL Funtion should not assert to worldmodel : " + expression);
						String argument = GLFactory.unescape(expression.toString());
						e = new Value(argument);
					} else {
						e = new Value(expression);
					}
					expList.add(e);
				}
//				System.out.println("assert relation " + name + " " + expList);
//				interpreter.getWorldModel().assertFact(name, expList);

				return interpreter.getWorldModel().newRelation(name, expList);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
//	public void assertJSON(JSONObject data) {
//	String name = "";
//
//
//	try {
//		name = data.get("Action").toString();
//		Object[] expressionList = new Object[data.get()];
//			for (int i = 0; i < gl.getExpressionsSize(); i++) {
//				if (gl.getExpression(i).isGeneralizedList()) {
//					String glString = gl.getExpression(i).toString();
//					expressionList[i] = GLFactory.unescape(glString);
//				} else {
//					kr.ac.uos.ai.arbi.model.Value value = gl.getExpression(i).asValue();
//				if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.FLOAT) {
//					expressionList[i] = value.floatValue();
//				} else if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.INT) {
//					expressionList[i] = value.intValue();
//				} else if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.STRING) {
//					String glString = value.stringValue();
//					expressionList[i] = GLFactory.unescape(glString);
//				} else {
//					String glString = value.stringValue();
//					expressionList[i] = GLFactory.unescape(glString);
//				}
//			}
//		}
//
//		assertFact(name, expressionList);
//	} catch (ParseException e) {
//		e.printStackTrace();
//	}
//	
//}
	
	private String removeQuotationMarks(Object input) {
		String data = input.toString();
		if (data.startsWith("\"")) {
			data = data.substring(1, data.length() - 1);
		}
		return data;
	}
}
