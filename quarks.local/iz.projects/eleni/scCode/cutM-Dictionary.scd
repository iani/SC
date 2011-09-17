//:cutMotion Dictionary
// Aris Bezas Fri, 29 July 2011, 15:12
//:--------------------
//:Syntax
OF.image(imageID, xPos, yPos, width, height);
OF.image(imageID, xPos, yPos, width, height, rotX,rotY,rotZ);
OF.image(imageID, xPos, yPos, width, height, translateX, translateY, translateZ, rotX,rotY,rotZ);
OF.image(image
	ID, 
	xPos, yPos, 
	width, height, 
	translateX, translateY, translateZ, 
	scaleX, scaleY, scaleZ,
	rotX,rotY,rotZ);

//:Examples
//:--------
OF.rect(0,0,1280, 1024);
OF.loadImage(0,"/Users/fou/images/pixxilation/0000.JPG");
OF.img(0, 0, 0, 400, 400,110,0,0,0,0,0);
OF.img(0, 0, 0, 1280, 1024, 0, 0, 0, rrand, 0, 0, 0);
OF.img(0, 0, 0, 400, 400);

//:---------
