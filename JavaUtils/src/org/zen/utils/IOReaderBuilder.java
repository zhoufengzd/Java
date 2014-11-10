package org.zen.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;

public class IOReaderBuilder
{
	private static final int BOM_SIZE = 4;

	// Construct InputStreamReader instance by detecting and skipping BOM (Byte Order Mark, or file encoding mark)
	//   Caller is responsible to close the reader after use.
	public static InputStreamReader buildInReader(String filePath) throws IOException
	{
		byte bom[] = new byte[BOM_SIZE];

		PushbackInputStream pushbackStream = new PushbackInputStream(new FileInputStream(filePath), BOM_SIZE);
		int bytesReadCount = pushbackStream.read(bom, 0, bom.length);

		String encoding = null;
		int pushbackCount;

		// Read ahead four bytes and check for BOM marks.
		if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF))
		{
			encoding = "UTF-8";
			pushbackCount = bytesReadCount - 3;
		}
		else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF))
		{
			encoding = "UTF-16BE";
			pushbackCount = bytesReadCount - 2;
		}
		else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE))
		{
			encoding = "UTF-16LE";
			pushbackCount = bytesReadCount - 2;
		}
		else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF))
		{
			encoding = "UTF-32BE";
			pushbackCount = bytesReadCount - 4;
		}
		else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00))
		{
			encoding = "UTF-32LE";
			pushbackCount = bytesReadCount - 4;
		}
		else
		{
			pushbackCount = bytesReadCount;
		}

		// Unread bytes if necessary and skip BOM marks.
		if (pushbackCount > 0)
		{
			pushbackStream.unread(bom, (bytesReadCount - pushbackCount), pushbackCount);
		}
		else if (pushbackCount < -1)
		{
			pushbackStream.unread(bom, 0, 0);
		}

		return (encoding == null) ? new InputStreamReader(pushbackStream) : new InputStreamReader(pushbackStream, encoding);
	}
}