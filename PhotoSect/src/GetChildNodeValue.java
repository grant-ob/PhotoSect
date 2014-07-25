/**********************************************************************************************
*--GET CHILD NODE VALUE SERVLET--
*
*--This servlet returns the string text that is contained in a specific node in an xml doc
*	
*
*--Date Nov 27, 2011
*
*************************************************************************************************/

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class GetChildNodeValue extends HttpServlet {
    protected void doGet(
        HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {   
		response.setContentType("text/xml");
        PrintWriter out = response.getWriter();	// output stream back to page
		String path = request.getPathInfo();
        path = path.substring(1);
        String[] args = path.split("/");  // get arguments
        String file = args[0].trim();	// name of xml file
		if(args.length > 1){
		String child = args[1].trim();	// name of child element
		System.out.println("Requesting value of child '" + child + "' from xml file '" + file + "'");
		String filePath = 
				"war/xml-content/" + file + ".xml"; 
		File xmlFile = new File(filePath); // xml file in question
		
		// Get stored value of child in xml file
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			Element element = (Element) doc.getDocumentElement();
			NodeList childEl = element.getElementsByTagName(child);
			String output = "";
			if(childEl.getLength() != 0){
			NodeList nl = childEl.item(0).getChildNodes();			
			output = "<?xml version='1.0'?><" + file + "><" + child + ">";			
			if(nl.getLength() > 1){				
				String name = nl.item(1).getNodeName();
				System.out.println("<" + name + ">" + nl.item(1).getChildNodes().item(0).getNodeValue() + "</" + name + ">");
				output += "<" + name + ">" + nl.item(1).getChildNodes().item(0).getNodeValue() + "</" + name + ">";
				for(int i = 3; i < nl.getLength(); i+= 2){
					name = nl.item(i).getNodeName();
					System.out.println("<" + name + ">" + nl.item(i).getChildNodes().item(0).getNodeValue() + "</" + name + ">");
					output += "<" + name + ">" + nl.item(i).getChildNodes().item(0).getNodeValue() + "</" + name + ">";
				}
			}
			else{
				System.out.println(nl.item(0).getNodeValue());
				output += nl.item(0).getNodeValue();
			}
			output += "</" + child + "></" + file + ">";
			System.out.println("Returned value : " + output);
			}
			out.println(output);	// send value back to page
		}
		catch(ParserConfigurationException e){
			e.printStackTrace();
		}
		catch(SAXException e){
			e.printStackTrace();
		}
		}		
    }		
}