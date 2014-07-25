/**********************************************************************************************
*--DELETION SERVLET--
*
*--This deletes an image from the images directory and removes all references 
*		to the file from the system
*
*--Files modified:
*	view-count.xml
*	comment-count.xml
*	img-uploader.xml
*	usr-imgs.xml
*	tags.xml
*	img-tags.xml
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class DeletionServlet extends HttpServlet {
    protected void doGet(
        HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {   
		response.setContentType("text/xml");
		
		File[] fileList = new File[7];
		//a list of all the files that need to be modified when an image is deleted
		fileList[0] = new File("war/xml-content/view-count.xml");
		fileList[1] = new File("war/xml-content/comments.xml");
		fileList[2] = new File("war/xml-content/comment-count.xml");
		fileList[3] = new File("war/xml-content/img-uploader.xml");
		fileList[4] = new File("war/xml-content/usr-imgs.xml");
		fileList[5] = new File("war/xml-content/img-tags.xml");     
		fileList[6] = new File("war/xml-content/tags.xml");


        PrintWriter out = response.getWriter();	// output stream back to page
		String path = request.getPathInfo();
        path = path.substring(1);
        String[] args = path.split("/");  // get arguments
		String img = args[0].trim();
		File imgFile = new File("war/images/" + img + ".PNG");
		imgFile.delete(); // delete the img
        String imgName = "i" + args[0].trim();	// name of xml node referring to image
        String temp;
		
		//do simple ones first (i.e. just delete the img tag, we dont need the content)
		for(int i = 0; i < 3; i++){
			System.out.println("Attempting to delete child " + imgName + " from File:" + i);
			temp = removeChild(fileList[i], imgName);
		}
		
		// now do more the complicated ones (i.e. we need the content to delete something somewhere else)
		temp = removeChild(fileList[3], imgName);
		removeFromChild(fileList[4], temp, imgName.substring(1));

		temp = removeChild(fileList[5], imgName);
		String[] tags = temp.split("/");
		System.out.println("LENGTH: " + tags.length);
		for(int i = 0; i < tags.length; i++){
			removeFromChild(fileList[6], tags[i], imgName.substring(1));
		}
		
	}		

	public String removeChild(File deleteFrom, String child){

		String childContent = "";
		try{
			//set up document builder and transformer
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(deleteFrom);
			Transformer trans = TransformerFactory.newInstance().newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");	
			
			doc.getDocumentElement().normalize();
			Element element = (Element) doc.getDocumentElement();
			NodeList childEl = element.getElementsByTagName(child);
			if(childEl.getLength() != 0){ //if child exists
				childContent = childEl.item(0).getChildNodes().item(0).getNodeValue();
				element.removeChild(childEl.item(0)); //remove the child
				
				
				//re-write the document without the child
				StreamResult Result = new StreamResult(deleteFrom);
				DOMSource source = new DOMSource(doc);
				trans.transform(source, Result);
				System.out.println("Successful");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("Returned value: " + childContent);
		return childContent;
	}
	
	public void removeFromChild(File deleteFrom, String child, String imgNumber){
		String childContent = "";
		try{
			//set up document builder and transformer
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(deleteFrom);
			Transformer trans = TransformerFactory.newInstance().newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			
			doc.getDocumentElement().normalize();
			Element element = (Element) doc.getDocumentElement();
			NodeList childEl = element.getElementsByTagName(child);
			if(childEl.getLength() != 0){
				childContent = childEl.item(0).getChildNodes().item(0).getNodeValue();
				String newContent = childContent.replace("/"+imgNumber, ""); // replace the reference to the image with nothing
				Node tag = childEl.item(0);												// removing the reference
				NodeList nl = tag.getChildNodes();
				Node toBeReplaced = nl.item(0);
				Node newText = doc.createTextNode(newContent);
				tag.replaceChild(newText, toBeReplaced); //replace the old text node with the new one that doesnt have the reference to the image
			
			
				//write back to file
				StreamResult Result = new StreamResult(deleteFrom);
				DOMSource source = new DOMSource(doc);
				trans.transform(source, Result);
			
				System.out.println("Successful");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}
}