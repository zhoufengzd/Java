package org.zen.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 
 * @author F.Zhou Writes script using template file and predefined tokens. All
 *         tokens must be enclosed with 'Token'
 *
 */
public class ToKenScriptBuilder {
	static final String Token = "$$";
	static final int TokenSize = Token.length();
	static final String lineSeparator = System.getProperty("line.separator");

	public ToKenScriptBuilder(String templateFilePath) {
		_templateFilePath = templateFilePath;
	}

	public void buildScript(HashMap<String, String> tokenValues, String outputFile) throws IOException {
		if (_markerLines == null) {
			_templateLines = new ArrayList<String>();
			_markerLines = new HashSet<Integer>();
			_tokens = new HashSet<String>();

			processFile(_templateFilePath);
		}

		StringBuilder buffer = new StringBuilder();
		String line = null;
		for (int lineIndex = 0; lineIndex < _templateLines.size(); lineIndex++) {
			line = _templateLines.get(lineIndex);

			if (!_markerLines.contains(lineIndex)) {
				buffer.append(line);
				buffer.append(lineSeparator);
			}
			else {
				for (String tk : _tokens) {
					String tkValue = tokenValues.get(tk);
					if (tkValue != null)
						line = line.replace(tk, tkValue);
				}
				buffer.append(line);
				buffer.append(lineSeparator);
			}
		}

		FileWriter.write(outputFile, buffer.toString());
	}

	private void processFile(String templateFilePath) throws IOException {
		BufferedReader br = new BufferedReader(IOReaderBuilder.buildInReader(templateFilePath));

		String line = null;
		int lineIndex = 0;
		while ((line = br.readLine()) != null) {
			_templateLines.add(line);

			int iTokenRight = 0;
			int iTokenLeft = line.indexOf(Token, iTokenRight);

			while (iTokenLeft > -1) {
				iTokenRight = line.indexOf(Token, iTokenLeft + TokenSize);
				if (iTokenRight < 0)
					break;

				_tokens.add(line.substring(iTokenLeft, iTokenRight + TokenSize));
				iTokenLeft = line.indexOf(Token, iTokenRight + TokenSize);

				_markerLines.add(lineIndex);
			}

			lineIndex++;
		}

		br.close();
	}

	private String _templateFilePath;
	private ArrayList<String> _templateLines;
	private HashSet<String> _tokens;
	private HashSet<Integer> _markerLines;
}