/**********************************************************************************************
*--ADD CHILD NODE SERVLET--
*
*--This servlet adds a child to required xml documents when the photosect user makes a request
*	these requests are:
*			-creating new account(add to passwords.xml and usr-imgs.xml)
*			-new comments (add to comment-count.xml and comments.xml)
*			-adding new tags (add to tags.xml and img-tags.xml)
*			-adding new images (handled by file upload servlet)
*
*--Date Nov 27, 2011
*
*************************************************************************************************/


import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;
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
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;



public class AddChildNode extends HttpServlet {

public enum Task{
	DEFAULT, CREATE_USER, ADD_TAG, ADD_COMMENT, ADD_VIEW
	}

    protected void doPost(
        HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
		//get cookie so we know which user is requesting the addition
		Cookie[] cookie = request.getCookies();
		
		PrintWriter out = response.getWriter();
        String path = request.getPathInfo();
		String index = path.substring(1);
		String[] args = index.split("/");
		String task = args[0];

		BufferedReader rd = request.getReader();
		StringBuilder sb = new StringBuilder();
		int ch;
		while ( (ch=rd.read()) != -1 ) {
			sb.append( (char)ch);
		}
		String xmlLine = sb.toString();
		
		int first = xmlLine.indexOf(">");
		int last = xmlLine.lastIndexOf("<");
		String content = xmlLine.substring(first+1,last);
		
		
		//sort requested task into Enum
		Task job = Task.DEFAULT;
		if(task.equals("passwords")){
			job = Task.CREATE_USER;
		}else if(task.equals("tags")){
			job = Task.ADD_TAG;
		}else if(task.equals("comments")){
			job = Task.ADD_COMMENT; 
		}else if(task.equals("view-count")){
			job = Task.ADD_VIEW;
		}
			
		
		System.out.println(task);
		
		
		File taskFile = new File("war/xml-content/"+ task + ".xml");
		switch(job){
		case CREATE_USER :response.setContentType("text/plain");
						File usrImgs = new File("war/xml-content/usr-imgs.xml");
						String[] userPass = content.split("/");
						System.out.println(userPass[0] + " " + userPass[1]); //first part is username, second part is pwd
						try{
						
							//set up document buider and transformer
							DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
							DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
							Transformer trans = TransformerFactory.newInstance().newTransformer();
							trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
							trans.setOutputProperty(OutputKeys.INDENT, "yes");
							
							//set up xml docs for each file that needs to be changed
							Document pwds = dBuilder.parse(taskFile);
							Document usrImg = dBuilder.parse(usrImgs);
							pwds.getDocumentElement().normalize();
							usrImg.getDocumentElement().normalize();
							Element pwdRoot = pwds.getDocumentElement();
							Element imgRoot = usrImg.getDocumentElement();
							NodeList nl = pwds.getElementsByTagName(userPass[0]);
							if(nl.getLength() == 0){ // if username doesnt exist already
								Element newUser = pwds.createElement(userPass[0]);
								Text pwd = pwds.createTextNode(userPass[1]);
								newUser.appendChild(pwd);
								pwdRoot.appendChild(newUser); 
								//write to passwords.xml
								StreamResult pwdResult = new StreamResult(taskFile);
								DOMSource pwdSource = new DOMSource(pwds);
								trans.transform(pwdSource, pwdResult);
								
								Element newUserImg = usrImg.createElement(userPass[0]);
								Text space = usrImg.createTextNode(" ");
								newUserImg.appendChild(space);
								imgRoot.appendChild(newUserImg);
								
								//write to usr-imgs.xml
								StreamResult imgResult = new StreamResult(usrImgs);
								DOMSource imgSource = new DOMSource(usrImg);
								trans.transform(imgSource, imgResult);
								
								
								System.out.println("successful");
								out.println("successful");
							}else {
							out.println("duplicate"); // if username has already been taken
							}
						}catch(Exception e){
							e.printStackTrace();
							} 
			break;
		
		case ADD_TAG:	response.setContentType("text/plain");
						File imgtags = new File("war/xml-content/img-tags.xml");
						String[] taggedImg = content.split("/"); // first part is tag, second part is img
						System.out.println(taggedImg[0] + " " + taggedImg[1]);
						try{
							//set up document buider and transformer
							DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
							DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
							Transformer trans = TransformerFactory.newInstance().newTransformer();
							trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
							trans.setOutputProperty(OutputKeys.INDENT, "yes");
							
							Document tags = dBuilder.parse(taskFile);
							Document imgTags = dBuilder.parse(imgtags);
							tags.getDocumentElement().normalize();
							imgTags.getDocumentElement().normalize();
							Element tagRoot = tags.getDocumentElement();
							Element imgTagRoot = imgTags.getDocumentElement();
							NodeList nl = tags.getElementsByTagName(taggedImg[0]);
							System.out.println(nl.getLength());
							if(nl.getLength() == 0){
								System.out.println("GOT HERE");
								Element newTag = tags.createElement(taggedImg[0]);
								Text img = tags.createTextNode("/" + taggedImg[1]);
								newTag.appendChild(img);
								tagRoot.appendChild(newTag); 
								
								StreamResult tagResult = new StreamResult(taskFile);
								DOMSource tagSource = new DOMSource(tags);
								trans.transform(tagSource, tagResult);
							}else{
								NodeList nodel = tagRoot.getElementsByTagName(taggedImg[0]);
								Node tag = nodel.item(0);
								NodeList nodel2 = tag.getChildNodes();
								Node currentImgs = nodel2.item(0);
								String newTxtNode = currentImgs.getNodeValue() + "/" + taggedImg[1];
								Node allImgs = tags.createTextNode(newTxtNode.trim());
								tag.replaceChild(allImgs, currentImgs);
								
								//write to tags.xml
								StreamResult tagResult = new StreamResult(taskFile);
								DOMSource tagSource = new DOMSource(tags);
								trans.transform(tagSource, tagResult);
							}							
									
							NodeList nodel = imgTagRoot.getElementsByTagName("i"+taggedImg[1]);
							Node img = nodel.item(0);
							NodeList nodel2 = img.getChildNodes();
							Node currentTags = nodel2.item(0);
							System.out.println(nodel2.item(0).getNodeValue());
							String newTxtNode = currentTags.getNodeValue() + "/" + taggedImg[0];
							Node allTags = imgTags.createTextNode(newTxtNode.trim());
							img.replaceChild(allTags, currentTags);

							//write to img-tags
							StreamResult imgTagResult = new StreamResult(imgtags);
							DOMSource imgTagSource = new DOMSource(imgTags);
							trans.transform(imgTagSource, imgTagResult);
								
								
							System.out.println("successful");
							out.println("successful");

						}catch(Exception e){
							e.printStackTrace();
							} 
			break; 
		
	case ADD_COMMENT: 	response.setContentType("text/plain");
						File commentcount = new File("war/xml-content/comment-count.xml");
						try{
							//set up document buider and transformer
							DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
							DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
							Transformer trans = TransformerFactory.newInstance().newTransformer();
							trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
							trans.setOutputProperty(OutputKeys.INDENT, "yes");
							
							Document comments = dBuilder.parse(taskFile);
							Document commentCount = dBuilder.parse(commentcount);
							commentCount.getDocumentElement().normalize();
							comments.getDocumentElement().normalize();
							Element root = comments.getDocumentElement();
							NodeList nl = comments.getElementsByTagName("i" + args[1]);
							Node img = nl.item(0);
							Element user = comments.createElement(cookie[0].getValue());
							Text comment = comments.createTextNode(content);
							user.appendChild(comment);
							img.appendChild(user);
							
							NodeList nlCount = commentCount.getElementsByTagName("i" + args[1]);
							Node commentedImg = nlCount.item(0);
							NodeList Text = commentedImg.getChildNodes();
							Node oldCount = Text.item(0);
							int count = Integer.parseInt(oldCount.getNodeValue().trim());
							count++;
							Node newCount = commentCount.createTextNode(Integer.toString(count));
							commentedImg.replaceChild(newCount, oldCount);
							
							//write to commentcount.xml
							StreamResult commentCountResult = new StreamResult(commentcount);
							DOMSource commentCountSource = new DOMSource(commentCount);
							trans.transform(commentCountSource, commentCountResult);
							
							
							//write to comments.xml
							StreamResult commentResult = new StreamResult(taskFile);
							DOMSource commentSource = new DOMSource(comments);
							trans.transform(commentSource, commentResult);

							out.println(content);
						}catch(Exception e){
							e.printStackTrace();
						}		
			break;
			
		case ADD_VIEW:	response.setContentType("text/plain");
		
						try{
							//set up document buider and transformer
							DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
							DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
							Transformer trans = TransformerFactory.newInstance().newTransformer();
							trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
							trans.setOutputProperty(OutputKeys.INDENT, "yes");
						
							Document views = dBuilder.parse(taskFile);
							views.getDocumentElement().normalize();
							Element root = views.getDocumentElement(); 
							NodeList nl = views.getElementsByTagName("i" + args[1]);// get the viewed img tag
							Node img = nl.item(0);
							NodeList Text = img.getChildNodes();
							Node oldCount = Text.item(0);
							int count = Integer.parseInt(oldCount.getNodeValue().trim());
							count++;	//increase the view count
							Node newCount = views.createTextNode(Integer.toString(count));
							img.replaceChild(newCount, oldCount);//replace old count with the new one
							System.out.println(count);
							
							//write to view-count.xml
							StreamResult viewResult = new StreamResult(taskFile);
							DOMSource viewSource = new DOMSource(views);
							trans.transform(viewSource, viewResult);
							System.out.println("COUNT: " + Integer.toString(count));
							out.println(Integer.toString(count));
							
						}catch(Exception e){
							e.printStackTrace();
						}
		}
		
		
			
		
		}
		
		
		}
		

		
		
		
		