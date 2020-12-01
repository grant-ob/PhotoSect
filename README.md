![banner](ps_logo.png)
# Welcome to PhotoSect 👋
![Version](https://img.shields.io/badge/version-1.00.00-blue.svg?cacheSeconds=2592000)

> A simple photo sharing application written in HTML/CSS/Javascript, Java, XML

## Usage

### Dependencies
Ensure that you have a recent version of Java installed on your system. The application has been tested with java version 1.8.0_271.

### File Structure required
```
<webapp>
	PhotoSectServer.java
	compileServer.sh
	compileServer.bat
	runServer.sh
	runServer.bat
	jetty-6.1.1.java
	jetty-util-6.1.8.java
	servlet-api-2.5-6.1.1.java
	<war>
		browse.HTML
		browse.js
		cookie-functions.js
		div-functions.js
		image-view.HTML
		image-view.js
		index.HTML
		index.js
		popupload.HTML
		style.css
		<backgrounds>
			footbg.png
			menu_bg.png
			whitebg.png
		<banners>
			banner_logo.png
			banner_logo1.png
			banner_logo2.png
			black_banner.png
		<images>
			*any images uploaded by users*
		<xml-content>
			comment-count.xml
			comments.xml
			img-tags.xml
			img-uploader.xml
			passwords.xml
			tags.xml
			usr-imgs.xml
			view-count.xml
		<WEB-INF>
			web.xml
			<classes>
			<lib>
	<src>
		AddChildNode.java
		DeletionServlet.java
		FileUpload.java
		GetAllChildNodeNames.java
		GetChildNodeValue.java
		NewImagesServlet.java
		SortOnCommentCount.java
		SortOnViewCount.java
		compileServlets.sh
		compileServlets.bat
		jetty-6.1.1.java
		jetty-util-6.1.8.java
		servlet-api-2.5-6.1.1.java
```
	
### Steps to compile
- In \<webapp\>, run compileServer.sh (linux) or compileServer.bat (windows) to create the class file for PhotoSectServer
- In \<src\>, run compileServlets.sh or compileServlets.bat to create the class files for the servlets in the correct folder

### Steps to run
- Run runServer.bat (windows) or runServer.sh (linux) in \<webapp\> to start the server
- Visit http://localhost:4004/
- While the application has been tested extensively and is working in Windows and Linux, we recommend using it in a Windows environment
- To ensure best viewing experience, use the latest stable version of Firefox or Chrome to view the application 

### Accessing Features
- From localhost:4004, one can access "login", "register", "about" and "browse" from the top menu. The msot recently added images appear at the bottom of the page
	
### Account Creation
- Choose "register" from the home page
- Enter a username and password following the instructions on the right of the page
- If the username is avilable and valid and the password is valid, the account will be created, otherwise you will have to try again
	
### Logging In
- Choose "login" from the homepage
- Enter your credentials
- When you are logged in, you are taken directly to the "myphotos" section, where you will see all the images you have uploaded

### Uploading an image
- From the "myphotos" section, scroll down to the upload section. click "choose file" and then select the image you want to upload. The application will	prevent you from uploading a file that is not an image
- Select "upload"; if it is successful you will see a popup window saying that it is successful

### Tagging an image
- Note: users can only tag images which they uploaded, not the images of other users. This is to limit inappropriate tagging
- Open the image you wish to tag by clicking on it from the "myphotos" section
- Below the image and to the right, select the tag you wish to add to the image and click "Tag Photo". You may add as many tags to any image as you wish, but keep them relevant

### Commenting on an image
- When you are signed in, you can comment on any image you wish by entering your comment on that image's view page and click "Post Comment!"
- If you are not signed in you cannot comment, you can only view others' comments
	
### Sharing an image
- At the bottom left of any image page, click "Share with friends!" to initiate your default mail client. The body of the email will automatically include the url of your current page
	
### Reporting an image
- If you think an image is overtly offensive or illegal, click "Report offensive image here" at the bottom left of any image page to create an email to PhotoSect with the inappropriate image URL included automatically
	
### Deleting an image
- You can delete an image by selecting "Delete Photo" that appears below the image. Note, however, this button will only appear if you are the original uploader of the image
	
### Tweeting about an image
- To share an image or your thoughts about PhotoSect through Twitter, simply click the "Tweet" button below the image to launch a twitter interface from which you can tweet
- Note, as this is currently being hosted locally, the URL will not appear automatically in your tweet. In the future when PhotoSect is online, this feature will be automatically enabled

### Browsing Images
- You can browse images through several different filters, which are:
  - Tags
  - User uploaded
  - Most views
  - Most comments
- These can be accesed through the browse button on the homepage

## Authors

👤 **Nicholas Noel, Grant O'Brien, Adam Sheppard**

*Originally written for Web Apps 3715 Course Project, Fall 2011 @ Memorial Univesity of Newfoundland 🎓*

## Show your support

Give a ⭐️ if this project helped you!

## Enjoy! 😎

***
_This README was generated with ❤️ by [readme-md-generator](https://github.com/kefranabg/readme-md-generator)_