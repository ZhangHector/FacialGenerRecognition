Student Name:	Rui Li & Hector Zhang
Student Number:	100987686 & 100947935
Professor Name:	Tony White
Due Date:		Apri 16, 2018
Course Name:	COMP 4601

Testing Environment:
	OS:			Max OSX
	IDE:		Eclipse Neon
	Device:		Andiod Studio
	Library:	Java 1.8

To run the application:
	- Update SERVER_HOST, CLIENT_HOST with conmputer ip and mobile ip in Server.py
	- Start serevr first by call "python Server" in conmputer
	- Update mIPAddress with computer ip in ClientConnection.java
	- Start android application
	- Click 'Take' to take picture in android device
	- Click 'Analyze' to analyze the picture

	* ClientConnection.java: ./FacialGenderRecognition/Detector/app/src/main/java/edu/carleton/comp4601/detector
	* Server.py: ./FacialGenderRecognition

Note that the trained model is too big, it is not include in the folder.
Therefore, please change the MODEL_FILE in Server.py