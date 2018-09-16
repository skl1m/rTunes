package com.rtunes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


/**
 * Class UserProfile
 *   
 * @author rTunes team
 */
public class UserProfile {

	//Constants
	//------------------------
	private final String fileName = "profile.txt";  
	private final String imageFile = "picture.png";
	
	//Attributes
	//------------------------
	private String username;
	private String email;
	private String uid;
	private String profilePath;

	
	//Methods
	//-----------------------------
	
	//Default constructor
	public UserProfile(String theProfilePath, String UID)
	{
		uid = UID;
		profilePath = theProfilePath;
		loadProfile();		
	}	

	//Gets
	//  methods to get each attribute
	public String getUsername()
	{
		return (username);
	}	
	
	public String getEmail()
	{
		return (email);
	}	
	
	public String getUid()
	{
		return (uid);
	}	
	
	public String getProfilePath()
	{
		return (profilePath);
	}
	
	public String getImagePath()
	{
		return (profilePath + imageFile);
	}
	
		
	//Load the profile
	//   read the information from the file stored in the user folder
	//   preconditions:
	//      - The user folder's name is equal to the UID
	//      - The user folder exists
	//      - The profile is stored in a text file
	//      - The file name is predefine (constant for this class)
	//      - Each attribute occupies the whole line  
	private void loadProfile()
	{						
		try {
			//Open the file
			File file = new File(getProfilePath() + fileName); 		  
			BufferedReader buffer = new BufferedReader(new FileReader(file));

			//load the attributes
			username = buffer.readLine();
			email  = buffer.readLine();

			//Close the file
			buffer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	
	// Implements the method to convert the object as an String
	@Override
	public String toString()
	{
		return ("Username: " + username + "\nEmail: " + email + "\nUID: " + uid + "\nPath: " + profilePath);
	}		

	
} //End of the Class
