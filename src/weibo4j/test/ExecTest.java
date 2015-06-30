package weibo4j.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExecTest {
	public static void main(String[] args) {

		try {
			Runtime rt = Runtime.getRuntime();
			// Process pr = rt.exec("cmd /c dir");
			Process pr = rt
					.exec("python example.py");

			BufferedReader input = new BufferedReader(new InputStreamReader(
					pr.getInputStream()));

			String line = null;

			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}

			int exitVal = pr.waitFor();
			System.out.println("Exited with error code " + exitVal);

		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}
}
