package com.rtunes;


/**
 * Class Session
 *   
 * @author rTunes team
 */
public class Session {

	//Attributes
	//------------------------	
	public static UserProfile profile; 				//user that logged successfully
	public static Library playLists;    			//set of play lists created by the user
	public static MediaObjectList currentPlaylist;  //list is playing. By default is the catalog
	public static MediaObject currentSong;          //song playing
	public static String parameter;     			//Variable used to pass a parameter
	public static String rootPath;					//location of the root disk
	
	
	//Methods
	public static String storagePath()
	{
		return (rootPath + "/rtunes/");
	}
	
	public static String musicPath()
	{
		return (storagePath() + "songs/");
	}
	
	public static String usersPath()
	{
		return (storagePath() + "users/");
	}
	
}


