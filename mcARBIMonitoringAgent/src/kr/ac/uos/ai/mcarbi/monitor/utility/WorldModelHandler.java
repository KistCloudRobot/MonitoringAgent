package kr.ac.uos.ai.mcarbi.monitor.utility;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import kr.ac.uos.ai.arbi.model.GLFactory;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.ParseException;
import uos.ai.jam.Interpreter;
import uos.ai.jam.expression.Expression;
import uos.ai.jam.expression.Relation;
import uos.ai.jam.expression.Value;

public class WorldModelHandler {

	private Interpreter			interpreter;
	
	public WorldModelHandler(Interpreter interpreter) {
		this.interpreter = interpreter;
	}
	

	public void assertFact(String name, Object... args) {
		interpreter.getWorldModel().assertFact(name, args);
	}

	public void retractFact(String expression) {

		GeneralizedList gl = null;
		try {
			gl = GLFactory.newGLFromGLString(expression);
			String name = gl.getName();
			List<Expression> expList = new ArrayList<Expression>();

			for (int i = 0; i < gl.getExpressionsSize(); i++) {
				String expressionValue = gl.getExpression(i).toString();
				expList.add(new Value(expressionValue));
			}

			Relation r = interpreter.getWorldModel().newRelation(name, expList);
			interpreter.getWorldModel().retract(r, null);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
	
//	public void assertJSON(JSONObject data) {
//		String name = "";
//
//
//		try {
//			name = data.get("Action").toString();
//			Object[] expressionList = new Object[data.get()];
//				for (int i = 0; i < gl.getExpressionsSize(); i++) {
//					if (gl.getExpression(i).isGeneralizedList()) {
//						String glString = gl.getExpression(i).toString();
//						expressionList[i] = GLFactory.unescape(glString);
//					} else {
//						kr.ac.uos.ai.arbi.model.Value value = gl.getExpression(i).asValue();
//					if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.FLOAT) {
//						expressionList[i] = value.floatValue();
//					} else if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.INT) {
//						expressionList[i] = value.intValue();
//					} else if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.STRING) {
//						String glString = value.stringValue();
//						expressionList[i] = GLFactory.unescape(glString);
//					} else {
//						String glString = value.stringValue();
//						expressionList[i] = GLFactory.unescape(glString);
//					}
//				}
//			}
//
//			assertFact(name, expressionList);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		
//	}
	public void assertGL(String input) {
		String name = "";

		if (input.startsWith("(")) {

			try {
				GeneralizedList gl = GLFactory.newGLFromGLString(input);
				name = gl.getName();
				Object[] expressionList = new Object[gl.getExpressionsSize()];

				for (int i = 0; i < gl.getExpressionsSize(); i++) {
					if (gl.getExpression(i).isGeneralizedList()) {
						String glString = gl.getExpression(i).toString();
						expressionList[i] = GLFactory.unescape(glString);
					} else {
						kr.ac.uos.ai.arbi.model.Value value = gl.getExpression(i).asValue();
						if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.FLOAT) {
							expressionList[i] = value.floatValue();
						} else if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.INT) {
							expressionList[i] = value.intValue();
						} else if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.STRING) {
							String glString = value.stringValue();
							expressionList[i] = GLFactory.unescape(glString);
						} else {
							String glString = value.stringValue();
							expressionList[i] = GLFactory.unescape(glString);
						}
					}

				}

				assertFact(name, expressionList);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void updateGL(String context) {
		String oldContext = "";
		String newContext = "";
		if (context.startsWith("(")) {
			try {
				GeneralizedList gl = GLFactory.newGLFromGLString(context);
				oldContext = gl.getExpression(0).asGeneralizedList().toString();
				newContext = gl.getExpression(1).asGeneralizedList().toString();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		updateFact(oldContext, newContext);
	}
	
	public void updateFact(String string, String string2) {
		String name = "";

		if (string.startsWith("(")) {

			try {
				GeneralizedList gl = GLFactory.newGLFromGLString(string);
				name = gl.getName();
				gl = GLFactory.newGLFromGLString(string2);

				String[] expressionList = new String[gl.getExpressionsSize()];

				for (int i = 0; i < gl.getExpressionsSize(); i++) {
					expressionList[i] = gl.getExpression(i).toString();
				}

				this.retractFact(string);
				assertFact(name, expressionList);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
