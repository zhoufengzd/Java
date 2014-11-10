package org.zen.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JasonWriter {
	   public static void main(String[] args) 
	   {
	      JSONObject obj = new JSONObject();

	      try {
			obj.put("name", "foo");
	      obj.put("num", new Integer(100));
	      obj.put("balance", new Double(1000.21));
	      obj.put("is_vip", new Boolean(true));

	      System.out.print(obj);
	      FileWriter.write("c:\\temp\\jason.txt", obj.toString());
	      
	   		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
}
