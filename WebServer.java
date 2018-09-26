/*
Title: Web Server
Files: 
Semester: 2018-01
Authors: Kenneth R. Young Castro, S00922530, Kevin M. Hernandez Rodriguez, 
         S00916526
Class: Telecomm CPEN 481
Section: 41
CRN: 1000
Profesor: Dr. Idaliles Vergara Laurens
Date: 14/January/2018

Description: Runs a web server which can be accessed from any browser. The 
            server connects to a browser client, the client writes 
            "localhost/filename" on the url, and the server establishes a
            persistent connection and sends it the file if it exists in the
            server, otherwise, sends an error 404 message.
 
 To run this code:
    1- Your terminal must have the path to the java jdk set. To do this, 
    run in the cmd PATH=%PATH%;"C:\Program Files\Java\jdk1.8.0_121\bin";
    2- CD into the correct folder.
    3- Compile the java class. "javac test.java" in this case.
    4- Run the class compiled. "java test".
 */

// Import java classes for sockets and input/output streams
import java.net.*;
import java.io.*;

public class WebServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        
        // Create server socket object, the one that will accept incoming clients
        ServerSocket serverSocket = null;
        // Establish Connection on port 80, the one used for HTTP connections and print that the server is running
        try {
            serverSocket = new ServerSocket(80); 
            System.out.println("Server up and running. Listening to clients.\n");
        // If Any error on establishing the server, print it out and close the server
        } catch (IOException e) {
            System.out.println("Server on port 80 could not be "
                     + "established");
            System.exit(1);
        }

        // Keep listening for incoming clients
        while(true){

   			// Make Connection to web client
            // Create object that will hold the socket connecting the server to the client
            Socket clientSocket = null; 
            try {
    			// The server returns the socket connecting the client and the server and store it in our socket object
                clientSocket = serverSocket.accept();
    			// If a socket connected, Print on server that a client has connected 
                if (clientSocket != null){                
                    System.out.println("A client has connected\n");
                }
    		// If any error happens,  print error
            } catch (IOException e) {
                 System.out.println("A client tried to connect but failed.\n");
            }
                     
    		// Create an object that receives the input in bytes of the client
            InputStream is = clientSocket.getInputStream();
            
            // Create an object so that you can send bytes to the client
            DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
            
    		// Use a buffer to make the code more efficient(returns character stream directly,
            // which is what we need to read the request from the client)
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            
    		//Make the socket stay connected till we say otherwise
            clientSocket.setKeepAlive(true);
           
            try {
            	// While the socket is still connected, get the file requested and send it
            	while(clientSocket.getKeepAlive()){
            		// Disconnect the socket after a certain period of time has passed, in this case
            		// 10 seconds
            		clientSocket.setSoTimeout(10000);
            		// Get the file from the request entered by the client
	            	File file = readFileFromRequest(br);
	            	// Send the file requested to the client
	            	sendFile(file, os);
            	}
	        } catch (Exception e){
	        	// If something goes wrong, print it
	        	System.out.println(e);
	        }


            // Close the buffered reader, the outputstream, and the client socket(this one should 
            // already be closed because if you close  the outputstream it closes the socket,
            // but just in case we closed it too
            if (br != null) br.close();
            if (os != null) os.close();
            if (clientSocket != null) clientSocket.close();
            
            // Print out that the client is no longer connected through a socket to the server
            System.out.println("Client has disconnected\n");
                
    	}
    }
    // This function is in charge of sending the file to the client. It requires
    // the file to send and the outputsteam where its going to send it
    static void sendFile(File myFile, DataOutputStream os) throws Exception{
    	// Create a byte array that will store the bytes of the file to send
	            byte[] buffer = new byte[1024 * 4];
	            // Index
	            int i = 0;
    			// if the file exists, create an object that can read the bytes from the file
	            if(myFile.exists() && !myFile.isDirectory()){
	            FileInputStream fis = new FileInputStream(myFile);
	    
	            // Check type of file
	                // If file is an html file, then send it
	                if(myFile.getName().endsWith(".html")){
	                    // Sends request succeeded to the client and sends back a response 
	                    os.writeBytes("HTTP/1.1 200 OK" + "\r\n");
	                    // Specifies the type of response you are sending to the client, in this case, html
	                    os.writeBytes("Content-Type: text/html" + "\r\n");
	                    
	                    // Send the length of the file so the client knows when to stop expecting
	                    // new inputstream and be able to send the new request
	                    os.writeBytes("Content-length: " + myFile.length() + "\r\n");

	                    // Write new lines so that you can indicate that what comes after 
	                    // is what you want to send
	                    os.writeBytes("\r\n");
	  
	                    // Reads data from the file input stream the size of the buffer and returns it to i. When
	                    // there is no more to read from the input s tream, returns -1 (which means while loop is over)
	                    while((i = fis.read(buffer)) != -1){
	                        // Send bytes into the clients output stream with an offset of 0
	                        // Writes i bytes from the buffer into the output stream, and increases the index i
	                        os.write(buffer, 0, i);
	                    }

	                    // If you are at this point, then the html file has been sent successfully and will print it out
	                    System.out.println("File " + myFile.getName() + " has been sent\n");

	                }   
	                // If file is jpg, then send it
	                else if (myFile.getName().endsWith(".jpg")){
	                    // Sends request succeeded to the client and sends back a response 
	                    os.writeBytes("HTTP/1.1 200 OK" + "\r\n");
	                    // Specifies the type of response you are sending to the client, in this case, jpg
	                    os.writeBytes("Content-Type: text/jpg" + "\r\n");

	                    // Send the length of the file so the client knows when to stop expecting
	                    // new inputstream and be able to send the new request
	                  	os.writeBytes("Content-length: " + myFile.length() + "\r\n");
	                    
                        // Write new lines so that you can indicate that what comes after 
	                    // is what you want to send
	                    os.writeBytes("\r\n");
	                     
	                    // Reads data from the file input stream the size of the buffer and returns it to i. When
	                    // there is no more to read from the input s tream, returns -1 (which means while loop is over)                  
	                    while((i = fis.read(buffer)) != -1){
	                        // Send bytes into the clients output stream with an offset of 0
	                        // Writes i bytes from the buffer into the output stream, and increases the index i
	                        os.write(buffer, 0, i);
	                    }

	                    // If you are at this point, then the jpg file has been sent successfully and will print it out
	                    System.out.println("File " + myFile.getName() + " has been sent\n");
	                }  
	                
	            }

	             // If file doesnt exist, or if user entered anything else
	            // other than an existing file, send error 404
	            else{
	                // Sends a response to the client saying the request cannot be completed because it did 
	                // not find anything anything that matches the clients request
	                os.writeBytes("HTTP/1.1 404 Not Found");
	                // Specifies the type of response you are sending to the client, in this case, html
	                os.writeBytes("Content-Type: text/html");
	                // Write new lines so that you can indicate that what comes after 
	                // is what you want to send
	                os.writeBytes("\r\n");
	                // Print out on clients webpage that the file was not found
	                os.writeBytes("<h1>Error 404</h1>");
	                os.writeBytes("<h3> File not Found  </h3>");

	                // Print out to the server that a file wasnt found
	                System.out.println("Client requested file and it was not found on the server\n");
	            }

    }
    // This function is in charge of reading the request sent by the client
    // and obtaining the file name from that request, and then returning
    // the file. It also empties the buffer so that we can read
    // the next request appropiately
    static File readFileFromRequest(BufferedReader br) throws Exception{

        System.out.println("Buffer ready: " + br.ready() + "\n");

	    // Reads a line from the input stream, which should contain the type 
	    // of HTTP method used(get or post), the input on the URL, and the 
    	// http protocol used from the buffer and save it on a string
    	String inputRequest = br.readLine();

        // Print what we read from the client request
    	System.out.println(inputRequest);

    	// Since you only want the input on the URL to know which file the 
	    // client is requesting, we split the line into different strings in an 
	    // Array
    	String[] userInput = inputRequest.split(" ");
        
        // Save the file requested into a string. Prepend a "."
        // so that it searches on the directory where the
        // server code is at
        String fileName = "." + userInput[1];
  	
  		// Print the requested file
  		System.out.println(fileName);

        // Create a file object with the info of the desired file to send
        File myFile = new File(fileName);
    
    	// This chunk of code is to keep reading every line of the buffer,
    	// so that you can empty it out and be able to read the next request,
    	// which is after all the extra information that it sends along after
    	// the first line(the one with the information we need to know which
    	// file was requested). This way, the next time we read, we will read
    	// the next request
    	String empty;
    	while(!(empty = br.readLine()).isEmpty()) {
            System.out.println(empty);
    	}

    	// Return the file object that was requested
    	return myFile;	
    }
}
