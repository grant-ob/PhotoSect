/**********************************************************************************************
*--GET ALL CHILD NODE NAMES SERVLET--
*
*--This servlet returns a "/" delimited string that contains the file name of the images in
	the new images folder in order from newst (most recently uploaded) to oldest.
*	
*
*--Date Nov 27, 2011
*
*************************************************************************************************/

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class NewImagesServlet extends HttpServlet {
    protected void doGet(
        HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		File imgFolder = new File("war/images");
		String output = "";
		if (imgFolder.list().length != 0){
		File[] listOfFiles = imgFolder.listFiles();
		ArrayList<Integer> fileNumbers = new ArrayList<Integer>();
		System.out.println("Requesting new images");
		
		for(File f : listOfFiles){			
			fileNumbers.add(Integer.parseInt(f.getName().split(".PNG")[0]));
		}
		Integer[] numArray = new Integer[fileNumbers.size()];
		for(int i = 0; i < fileNumbers.size(); i++){
			numArray[i] = fileNumbers.get(i);
		}
		Arrays.sort(numArray);
		output = numArray[numArray.length - 1].toString();
		int index = numArray.length - 2;
		int count = 0;
		while(count < 6){
			if(count < numArray.length && index >= 0){
				output += "/" + numArray[index];				
				index--;
				count++;
			}
			else break;
		}
		}
		System.out.println("Returned value: " + output);
		out.println(output);
    }
}