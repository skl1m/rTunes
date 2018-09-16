package com.rtunes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;


/**
 * Class AccessControl
 *   
 * @author rTunes team
 */
public class AccessControl {
	
	//Constants
	//------------------------
	private final String accountsFile = "accounts.txt";  

	//Attributes
	//------------------------
	private ArrayList<String> accounts;	//list of valid accounts
	private ArrayList<String> uids;		//list of valid UIDs

	//Methods
	//-----------------------------
	
	//Default constructor
	public AccessControl()
	{
		accounts = new ArrayList<String>();
		uids = new ArrayList<String>();		
		Session.rootPath = Environment.getExternalStorageDirectory().toString();
		loadAccounts();		
	}
		
	
	//Read the file of user accounts
	private void loadAccounts()
	{ 
		try {
			//Initialize variables
			String line = "";
			accounts.clear();	//remove previous elements in the list
			uids.clear();		//remove previous elements in the list
						
			//Open the file for reading
			File file = new File(Session.storagePath() + accountsFile); 		  
			BufferedReader buffer = new BufferedReader(new FileReader(file));
			
			//load the media object			
			while ((line = buffer.readLine()) != null)
			{
				char delimiter = ';';
				
				int index = line.indexOf(delimiter); 	//the username				
				index = line.indexOf(delimiter, index+1); //the password
				accounts.add(line.substring(0, index).trim());
				uids.add(line.substring(index+1).trim());
			}

			//Close the file
			buffer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	//Encrypt a String that is used as a password
	private String encrypt(String plainText)
	{
		//Here add the code that encrypts the plain text
		String cipherText = plainText;
		return cipherText;
	}
	
	//attempts to login a user
	// returns the UID if login has success, otherwise an empty string
	public String login(String username, String password)
	{
		String uid = "";	//in case of unsuccessful login
		String attempt = username.trim() + ";" + encrypt(password.trim()); //possible account	
		int index = accounts.indexOf(attempt);	//gets an index if the user logged
		if (index >= 0) {
			uid = uids.get(index);	//gets the UID if user logged
			Session.profile = new UserProfile(Session.usersPath() + uid + "/", uid);
			Session.playLists = new Library (Session.usersPath() + uid + "/playlists/");
			Session.currentPlaylist = new MediaObjectList("Current Playlist");
		}
		
		return(uid);
	}
					
	
}
