package kr.ac.uos.ai.mcarbi.monitor.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import uos.ai.jam.Interpreter;
import uos.ai.jam.parser.JAMParser;

public class JAMPlanLoader {
	
	private Interpreter interpreter;
	
	public JAMPlanLoader(Interpreter interpreter) {
		this.interpreter = interpreter;
	}
	
	public void loadPlan(String path) {
		File plan = new File(path);
		if (!plan.exists()) {
			System.out.println("Plan File is not found");
			return;
		}
		if (!plan.isFile()) {
			System.out.println("This Path is not a File");
			return;
		}
		
		JAMParser.parseFile(interpreter, new File(path));
	}

	public void parsePlan(String plan) {
		JAMParser.parseString(interpreter, plan);
	}

	public String readFile(String input) {
		BufferedReader br = null;
		FileReader fr = null;
		StringBuilder builder = new StringBuilder();
		try {
			fr = new FileReader(input);

			br = new BufferedReader(fr);

			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				builder.append(currentLine);
				builder.append("\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return builder.toString();
	}

	public void loadPlanPackage(String folder) {
		File directory = new File(folder);
		if (!directory.exists()) {
			System.out.println("Plan Package is not found");
			return;
		}
		if (directory.isFile()) {
			System.out.println("This Path is not a Package");
			return;
		}
		ArrayList<File> files = new ArrayList<File>();
		

		findPath(files, directory);
		
		
		for (File file : files) {
			JAMParser.parseFile(interpreter, file);
		}
	}

	private void findPath(ArrayList<File> files, File directory) {
		for (File childFile : directory.listFiles()) {
			if (childFile.isDirectory()) {
				findPath(files, childFile);
			}else{
				files.add(childFile);
			}
		}

	}

}
