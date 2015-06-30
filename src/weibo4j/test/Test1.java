package weibo4j.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test1 {
	public static void main(String[] args) throws IOException {
		// set up the command and parameter
		String pythonScriptPath = "example.py";
		String[] cmd = new String[2 + args.length];
		cmd[0] = "python";
		cmd[1] = pythonScriptPath;
		for(int i = 0; i < args.length; i++) {
		cmd[i+2] = args[i];
		}
		 
		// create runtime to execute external command
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(cmd);
		 
		// retrieve output from python script
		BufferedReader bfr = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line = "";
		while((line = bfr.readLine()) != null) {
		// display each output line form python script
		System.out.println(line);
		}
		}
}
