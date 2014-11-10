package org.zen.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjSerializer {
	public static void write(String filePath, Object obj) throws IOException {
		File fl = new File(filePath);
		fl.getParentFile().mkdirs();

		ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(fl));
		writer.writeObject(obj);
		writer.close();
	}

	@SuppressWarnings("unchecked")
	public static <T> T read(String filePath) throws IOException, ClassNotFoundException {
		File fl = new File(filePath);
		if (!fl.exists())
			return null;

		ObjectInputStream reader = new ObjectInputStream(new FileInputStream(fl));
		T obj = (T) reader.readObject();
		reader.close();

		return obj;
	}
}
