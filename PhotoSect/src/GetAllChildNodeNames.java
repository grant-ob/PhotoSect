
/**********************************************************************************************
*--GET ALL CHILD NODE NAMES SERVLET--
*
*--This servlet returns all the children of a specified node in an xml file
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

public class GetAllChildNodeNames extends HttpServlet {
    protected void doGet(
        HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {   
		response.setContentType("text/plain");
        PrintWriter out = response.getWriter();	// output stream back to page
		String path = request.getPathInfo();
        path = path.substring(1);
        String[] args = path.split("/");  // get arguments
        String file = args[0].trim();	// name of xml file
		System.out.println("Requesting name of child nodes from xml file '" + file + "'");
		String filePath = 
				"war/xml-content/" + file + ".xml"; 
		File xmlFile = new File(filePath); // xml file in question
		String output = "";
		// Get names of children in xml file
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			Element element = (Element) doc.getDocumentElement();
			NodeList nl = element.getChildNodes();
			output += nl.item(1).getNodeName();
			for(int i = 3; i < nl.getLength(); i+=2){		
				output += "/" + nl.item(i).getNodeName();
			}
			System.out.println("Returned value : " + output);
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