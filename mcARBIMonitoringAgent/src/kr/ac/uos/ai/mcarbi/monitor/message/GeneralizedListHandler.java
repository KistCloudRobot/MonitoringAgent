package kr.ac.uos.ai.mcarbi.monitor.message;

import kr.ac.uos.ai.arbi.model.GLFactory;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.ParseException;

public class GeneralizedListHandler {
	
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

	private String removeQuotationMarks(Object input) {
		String data = input.toString();
		if (data.startsWith("\"")) {
			data = data.substring(1, data.length() - 1);
		}
		return data;
	}

}
