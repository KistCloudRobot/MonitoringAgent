package kr.ac.uos.ai.mcarbi.monitor.message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import kr.ac.uos.ai.arbi.model.GLFactory;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.ParseException;
import uos.ai.jam.Interpreter;
import uos.ai.jam.exception.AgentRuntimeException;
import uos.ai.jam.expression.Expression;
import uos.ai.jam.expression.Relation;
import uos.ai.jam.expression.Value;
import uos.ai.jam.plan.action.AchieveGoalAction;

public class MessageHandler {
	private Interpreter interpreter;
	
	
	public MessageHandler(Interpreter interpreter) {
		this.interpreter = interpreter;
	}
	
	public int toInteger(String input) {
		double doub = Double.parseDouble(input);
		int result = (int) doub;

		return result;
	}
	
	public String contains(String input, String text) {
		if(input.contains(text)) {
			return "true";
		}
		
		return "false";
	}
	
	public void removePlan(String planID) {
		interpreter.getPlanLibrary().removePlan(planID);
	}
	
	public MessageHandler() {
	}

	public String retrieveElement(String input) {

		GeneralizedList list = null;
		String result = "";
		try {
			list = GLFactory.newGLFromGLString(input);

			for (int i = 0; i < list.getExpressionsSize(); i++) {
				result += removeQuotationMarks(list.getExpression(i).toString()) + ", ";
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	
	public String removeQuotationMarks(Object input) {
		String data = input.toString();
		if (data.startsWith("\"")) {
			data = data.substring(1, data.length() - 1);
		}
		return data;
	}
	
	public String retrieveGLName(String glString) {
		String result = "";
		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(glString);
			result = gl.getName();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		return result;
	}
	
	
	public int retrieveExpressionSize(String expression) {
		int result = 0;

		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(expression);
			result = gl.getExpressionsSize();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public int getUtility(int value) {
		System.out.println("utility value : " + value);

		return value;
	}

	
	public String retrieveGLExpression(String input, int i) {
		String result = "";
		
		//System.out.println("why? : " + input);
		if (input.startsWith("\"")) {
			input = removeQuotationMarks(input);
		}
		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(input);

			if(gl.getExpression(i).isValue()) {

				result = gl.getExpression(i).asValue().stringValue();
			} else if (gl.getExpression(i).isGeneralizedList()) {
				result = gl.getExpression(i).asGeneralizedList().toString();
			}
				

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//result = this.removeQuotationMarks(result);
		return result;
	}

	public String retrieveNonGLExpression(String input, int i) {
		String result = "";
		result = input.split(" ")[i];

		return result;
	}

	public String retrieveGLGoal(String input) {
		String result = "";

		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(input);
			result = gl.getName();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
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
	
	public void assertJSON(JSONObject data) {
		String name = "";


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
		
	}
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
	
	public void updateContextGL(String context) {
		String oldContext = "";
		if (context.startsWith("(")) {
			try {
				GeneralizedList gl = GLFactory.newGLFromGLString(context);
				oldContext = "(" + gl.getName();
				for(int i = 0; i < gl.getExpressionsSize(); i++) {
					oldContext = oldContext + " $v";
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		oldContext = oldContext + ")";
		updateFact(oldContext, context);
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
	
	public String escapeGL(String gl) {
//		System.out.println("start escape : " + gl);
		String result = GLFactory.escape(gl);
		System.out.println(result);
		return result;
	}
	public String unescapeGL(Object input) {

//		System.out.println("unescape GL ?????? " + input.getClass().getSimpleName());
		String gl = input.toString();
		
		return GLFactory.unescape(gl);
	}


}
