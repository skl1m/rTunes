package com.rtunes;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Class MediaObjectList
 *   
 * @author rTunes team
 */
public class MediaObjectList {

	//Attributes
	//------------------------
	private String name;						//Name of the list
	private ArrayList<MediaObject> container;	//List of objects
		
		
	//Methods
	//-----------------------------

	//constructor	
	public MediaObjectList (String containerName)
	{
			name = containerName;
			container = new ArrayList<MediaObject>();
	}
				
		
	//Gets
	//  methods to get each attribute
	public String getName()
	{
		return (name);
	}			
	
	public ArrayList<MediaObject> getContainer()
	{
		return (container);
	}
		
		
	//Search an Object in the container
	//  returns the index if the object is in the list
	//  otherwise -1
	public int search (MediaObject theObject)
	{
		return(container.indexOf(theObject));
	}
		
	
	//Search a Object in the Container by a tag string
	//  returns the index if the object is in the container
	//  otherwise -1
	public int search (String tags)
	{
		MediaObject theObject = new MediaObject(tags);
		return search(theObject);
	}

		
	//Adds an object to the container
	//  postcondition: the object is not added if it already exists in the container
	public int add(MediaObject newObject)
	{		
		int index = search(newObject);
		
		//Added if the search returns a negative index
		if (index < 0)
		{
			container.add(newObject);
			index = container.size()-1;
		}
		
		return (index);
	}
		
	//Adds an object to the container by the string that represents the object
	//  postcondition: the object is not added if it already exists in the container
	public int add(String tags)
	{		
		MediaObject newObject = new MediaObject(tags);
		int index = add(newObject);
		return (index);
	}
		
	//Remove an Object from the container
	public void remove(MediaObject theObject)
	{
		container.remove(theObject);
	}
		
	//Remove a object from the container by the String that represents it
	public void remove(String tags)
	{
		MediaObject theObject = new MediaObject(tags);
		remove(theObject);
	}
	

	//Get the objects that contains a certain string (filter string)
	//  postconditions:
	//      The name of the list returned is the filter string      
	public MediaObjectList filter(String lookingFor)
	{
		MediaObjectList result = new MediaObjectList(lookingFor);
		
		for (int i=0; i <container.size(); i++)
		{
			if (container.get(i).contains(lookingFor))
			{
				result.add(container.get(i));
			}
		}
		return(result);
	}
	
	
	//Load the List
	//   read the information from a text file
	//   preconditions:
	//      - The file should exist
	//   postconditions
	//      - Duplicated objects are loaded just once
	public void load(String path)
	{						
		try {
			//Initialize variables
			String info = "";
			container.clear();		//remove previous elements in the list
						
			//Open the file for reading
			File file = new File(path + name + ".txt"); 		  
			BufferedReader buffer = new BufferedReader(new FileReader(file));
			
			//load the media object			
			while ((info = buffer.readLine()) != null)
			{
				add(info);	//Add the object to the list				
			}

			//Close the file
			buffer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		

	//Save the List
	//   Save the information in a text file
	//   preconditions:
	//      - The file will be overwrite if it exists
	public void save(String path)
	{						
		try {
			
			//Prepares the file for writing
			File file = new File(path + name + ".txt"); 		
			if (!file.exists()) file.createNewFile();			
			BufferedWriter buffer = new BufferedWriter(new FileWriter(file, false));
			
			//save each object represented only by the tag
			for (int i=0; i<container.size(); i++)
			{
				buffer.write(container.get(i).getTags()+"\r\n");
			}

			//Close the file
			buffer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
			
		
	//Converts the list of Objects to a list of Strings
	public ArrayList<String> toArrayListString()
	{
		ArrayList<String> list = new ArrayList<String>();
		for (int i=0; i <container.size(); i++)
		{
			list.add(container.get(i).getTitle());
		}
		
		return (list);
	}
	
	//Print out the list of objects contained in the container
	//  postcondition: uses the standard console
	public void print()
	{
		System.out.println("List: " + getName() + "\n");
		for (int i=0; i <container.size(); i++)
		{
			System.out.println(container.get(i) + "\n");
		}
	}
	
} //End of the Class
