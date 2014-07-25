/**********************************************************************************************
*--File Upload--
*
*--This servlet is enacted by the upload action and saves a copy of the image to the "images" folder 
*-- It also initiates the image in all the XML files			
*
*--Date Nov 27, 2011
*
*************************************************************************************************/

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList; 
import org.w3c.dom.Node;
import org.w3c.dom.Text;


@SuppressWarnings( "serial" )
public class FileUpload extends HttpServlet
{

	@SuppressWarnings( "unchecked" )
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
	{
		Cookie[] cookie = req.getCookies();
		Scanner scanner = null;
		String[] line = new String[1000];
		
		File usrImages = new File("war/xml-content/usr-imgs.xml");
		File imgTags = new File("war/xml-content/img-tags.xml");
		File commentTags = new File("war/xml-content/comments.xml");
		File commentCountTags = new File("war/xml-content/comment-count.xml");
		File viewCountTags = new File("war/xml-content/view-count.xml");
		File uploaderTags = new File("war/xml-content/img-uploader.xml");
		
		
		try {
			scanner = new Scanner(new File("count.txt"));									//loads in image count  
			int in = 0;
			while (scanner.hasNextLine()) {														
				line[in] = scanner.nextLine();  
				in++;
			}
		}
		catch (FileNotFoundException fe) {
		   System.out.println("Error: " + fe.getMessage());
		}
		finally {
		scanner.close();
		}
		PrintWriter outp = resp.getWriter();
		ArrayList files = (ArrayList) req.getAttribute( "org.mortbay.servlet.MultiPartFilter.files" );	//retrieves the image that was uploaded
		for ( int x = 0; x < files.size(); x++ )
		{
			File file1 = (File) files.get( x );
			File outputFile = new File( "outputfile" + ( x + 1 ) );
			file1.renameTo( outputFile );
		}
		StringBuffer buff = new StringBuffer();
		File file1 = (File) req.getAttribute( "userfile1" );		//grabs the uploaded image file
			
		if( file1 == null || !file1.exists() )
		{
			buff.append( "File does not exist" );
		}
		else if( file1.isDirectory())
		{
			buff.append( "File is a directory" );
		}
		else
		{
			String dest = ("war/images/" + line[0] + ".PNG");
			copyfile(file1, dest);
			int index = Integer.parseInt(line[0]);
			index++;
			String in = Integer.toString(index);
			FileWriter outFile = new FileWriter("count.txt");						
			PrintWriter hout = new PrintWriter(outFile);
			hout.println(in);
			hout.close();
				
			try{
			
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();	//adds an instance for image tags and comments, updates XML files
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document usrImg = dBuilder.parse(usrImages);
				Document imgtags = dBuilder.parse(imgTags);
				Document commenttags = dBuilder.parse(commentTags);
				Document commentcounttags = dBuilder.parse(commentCountTags);
				Document viewcounttags = dBuilder.parse(viewCountTags);
				Document uploader = dBuilder.parse(uploaderTags);
				
				
				imgtags.getDocumentElement().normalize();
				usrImg.getDocumentElement().normalize();
				commenttags.getDocumentElement().normalize();
				commentcounttags.getDocumentElement().normalize();
				viewcounttags.getDocumentElement().normalize();
				uploader.getDocumentElement().normalize();
				
				
				Element tagRoot = imgtags.getDocumentElement();
				Element imgRoot = usrImg.getDocumentElement();
				Element commentRoot = commenttags.getDocumentElement();
				Element commentCountRoot = commentcounttags.getDocumentElement();
				Element viewcountRoot = viewcounttags.getDocumentElement();
				Element uploaderRoot = uploader.getDocumentElement();
				
				
				NodeList nl = imgRoot.getElementsByTagName(cookie[0].getValue());
				Node user = nl.item(0);
				NodeList nl2 = user.getChildNodes();
				Node currentImgs = nl2.item(0);
				System.out.println(nl2.item(0).getNodeValue());
				String newTxtNode = currentImgs.getNodeValue() + "/" + line[0];
				Node allImgs = usrImg.createTextNode(newTxtNode.trim());
				user.replaceChild(allImgs, currentImgs);
				
				Element newImg = imgtags.createElement("i"+line[0]);
				Text space = imgtags.createTextNode(" ");
				
				Element newCommentImg = commenttags.createElement("i"+line[0]);
				Text space1 = commenttags.createTextNode(" ");
				Element newViewCountImg = viewcounttags.createElement("i"+line[0]);
				Text space2 = viewcounttags.createTextNode("0");
				Element newCommentCountImg = commentcounttags.createElement("i"+line[0]);
				Text space5 = commentcounttags.createTextNode("0");
				Element newUploadedImg = uploader.createElement("i"+line[0]);
				Text space3 = uploader.createTextNode(cookie[0].getValue());
			
				
				newImg.appendChild(space);
				tagRoot.appendChild(newImg);
				newCommentImg.appendChild(space1);
				commentRoot.appendChild(newCommentImg);
				newCommentCountImg.appendChild(space5);
				commentCountRoot.appendChild(newCommentCountImg);
				newViewCountImg.appendChild(space2);
				viewcountRoot.appendChild(newViewCountImg);
				
				
				
				newUploadedImg.appendChild(space3);
				uploaderRoot.appendChild(newUploadedImg);
				
				Transformer trans = TransformerFactory.newInstance().newTransformer();
				trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				trans.setOutputProperty(OutputKeys.INDENT, "yes");
				
				
				//write to usr-imgs
				StreamResult ImgResult = new StreamResult(usrImages);
				DOMSource source = new DOMSource(usrImg);
				trans.transform(source, ImgResult);
				
				//write to img-tags
				StreamResult TagResult = new StreamResult(imgTags);
				DOMSource TagSource = new DOMSource(imgtags);
				trans.transform(TagSource, TagResult);
				
				//write to comments
				StreamResult CommentResult = new StreamResult(commentTags);
				DOMSource CommentSource = new DOMSource(commenttags);
				trans.transform(CommentSource, CommentResult);
				//write to comment count
				StreamResult CommentCountResult = new StreamResult(commentCountTags);
				DOMSource CommentCountSource = new DOMSource(commentcounttags);
				trans.transform(CommentCountSource, CommentCountResult);
				//write to view count
				StreamResult ViewCountResult = new StreamResult(viewCountTags);
				DOMSource ViewSource = new DOMSource(viewcounttags);
				trans.transform(ViewSource, ViewCountResult);

				//write uploader
				StreamResult UploaderResult = new StreamResult(uploaderTags);
				DOMSource UploaderSource = new DOMSource(uploader);
				trans.transform(UploaderSource, UploaderResult);
				
				
				buff.append( "File successfully uploaded." );
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		//final output is HTML code for the finished page, if there is an error it will be displayed here
		outp.write( "<html>" );
		outp.write( "<head><title>FileUpload page</title>" );
		outp.write( "<script type=\"text/javascript\">" );
		outp.write( "function onClose(){" );
		outp.write( "window.opener.location.reload(true);}" );
		outp.write( "</script>" );
		outp.write( "</head>" );
		outp.write( "<body style=\"background-color: #606061\" onunload=\"onClose()\">" );
		outp.write( "<h2 style=\"color:white;\" align=\"center\">" + buff.toString() + "</h2>" );
		outp.write("<FORM align=\"center\"><INPUT TYPE=\"BUTTON\" VALUE=\"Close\"  ONCLICK=\"window.close()\"></FORM>");
		outp.write( "</body>" );
		outp.write( "</html>" );
	}
	
	private static void copyfile(File srFile, String dtFile){
		  try{
		  File f1 = srFile;
		  File f2 = new File(dtFile);
		  InputStream in = new FileInputStream(f1);
		  
		  //For Append the file.
		//  OutputStream out = new FileOutputStream(f2,true);

		  //For Overwrite the file.
		  OutputStream out = new FileOutputStream(f2);

		  byte[] buf = new byte[1024];
		  int len;
		  while ((len = in.read(buf)) > 0){
		  out.write(buf, 0, len);
		  }
		  in.close();
		  out.close();
		  System.out.println("File copied.");
		  }
		  catch(FileNotFoundException ex){
		  System.out.println(ex.getMessage() + " in the specified directory.");
		  System.exit(0);
		  }
		  catch(IOException e){
		  System.out.println(e.getMessage());  
		  }
		  }
	
}
