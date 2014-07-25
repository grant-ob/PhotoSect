/**********************************************************************************************
*--SORT ON COMMENT COUNT SERVLET--
*
*--This servlet returns a "/" delimited string that contains the file name of the images in
	the new images folder in order from most comments to least
*	
*
*--Date Nov 27, 2011
*
*************************************************************************************************/

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class SortOnCommentCount extends HttpServlet {
    protected void doGet(
        HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		Hashtable<Integer, String> counts = new Hashtable<Integer, String>();
		File xmlFile = new File("war/xml-content/comment-count.xml");
		System.out.println("Requesting images sorted by comment count");
		String output = "";
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			Element element = (Element) doc.getDocumentElement();
			NodeList nl = element.getChildNodes();
			String strKey = nl.item(1).getChildNodes().item(0).getNodeValue();
			System.out.println("Key is: " + strKey);
			int key;
			if(!strKey.equals(" ")){
				key = Integer.parseInt(strKey);
			}
			else{
				key = 0;
			}			
			String value = nl.item(1).getNodeName().split("i")[1];
			System.out.println("Adding (" + key + "," + value + ") to hashtable");
			counts.put(key, value);
			output += Integer.toString(key);
			for(int i = 3; i < nl.getLength(); i+=2){				
				strKey = nl.item(i).getChildNodes().item(0).getNodeValue();				
				System.out.println("Key is: " + strKey);
				if(!strKey.equals(" ")){
					key = Integer.parseInt(strKey);
				}
				else{
					key = 0;
				}
				value = nl.item(i).getNodeName().split("i")[1];
				if(counts.containsKey(key)){
					value = counts.get(key) + "/" + value;
				}
				System.out.println("Adding (" + key + "," + value + ") to hashtable");
				counts.put(key, value);
				output += "/" + Integer.toString(key);
			}
			if(output != ""){
				System.out.println("Output is: " + output);				
				String[] strArray = output.split("/");
				Integer[] intArray = new Integer[strArray.length];
				for(int i = 0; i < strArray.length; i++){
					System.out.println("strArray[" + i + "] is: " + strArray[i]);
					intArray[i] = Integer.parseInt(strArray[i]);
				}
				Arrays.sort(intArray);
				int check = -1;
				output = "";
				for(int i = intArray.length - 1; i >= 0; i--){
					if(intArray[i] != check){
						check = intArray[i];
						output += "/" + counts.get(check); 						
					}
				}
				System.out.println("Returned value : " + output);
				out.println(output);	// send value back to page
			}
		}
		catch(ParserConfigurationException e){
			e.printStackTrace();
		}
		catch(SAXException e){
			e.printStackTrace();
		}
    }
}