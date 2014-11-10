package org.zen.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ProcExecutor {

	public int Execute(String processPath) {
		return Execute(processPath, null, null);
	}

	public int Execute(String processPath, String[] parameters) {
		return Execute(processPath, parameters, null);
	}

	public int Execute(String processPath, String[] parameters, String quote) {
		try {
			Process process;
			process = Runtime.getRuntime().exec(processPath);
			process.waitFor();

			OutputStream stdin = process.getOutputStream();
			InputStream stderr = process.getErrorStream();
			InputStream stdout = process.getInputStream();

			if (parameters != null && parameters.length > 0) {
				byte btSpace[] = (new String(" ")).getBytes();
				byte btQuote[] = (quote == null) ? null : quote.getBytes();
				for (String param : parameters) {
					if (btQuote != null)
						stdin.write(btSpace);
					stdin.write(param.getBytes());
					if (btQuote != null)
						stdin.write(btSpace);

					stdin.write(btSpace);
					stdin.flush();
				}
				stdin.close();
			}

			String line;
			BufferedReader brOutput = new BufferedReader(new InputStreamReader(stdout));
			while ((line = brOutput.readLine()) != null) {
				System.out.println("[Stdout] " + line);
			}
			brOutput.close();

			BufferedReader brStdErr = new BufferedReader(new InputStreamReader(stderr));
			while ((line = brStdErr.readLine()) != null) {
				System.out.println("[Stderr] " + line);
			}
			brStdErr.close();

			return process.exitValue();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return 1;
	}

}
