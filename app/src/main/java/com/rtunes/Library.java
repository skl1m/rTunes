package com.rtunes;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class Library
 *   
 * @author rTunes team
 */
public class Library {

	//Attributes
	//------------------------
	private ArrayList<String> container;	//List of lists
	private String libraryPath;				//location in disk
	
	
	//Methods
	//-----------------------------	

	//constructor	
	public Library(String path)
	{
		libraryPath = path;
		container = new ArrayList<String>();
		load();
	}
	
	//Gets
	//  methods to get each attribute
	public String getLibraryPath()
	{
		return (libraryPath);
	}
	
	public ArrayList<String> getContainer()
	{
		return (container);
	}

	// Return the container's content converted to a array of Strings 
	public String[] toArrayStrings()
	{
		String[] data = new String[container.size()];
		
		for (int i=0; i < container.size(); i++)
		{
			data[i] = container.get(i); 
		}
		return (data);
	}
	

	//Verifies if the list contains an element
	public boolean contains(String element)
	{						
		return (container.indexOf(element) >= 0);
	}	
	
	
	//Load the elements from a folder
	//   preconditions:
	//      - The folder contains only text files of MediaObjectList
	//      - Each file ends with the extension ".txt"
	private void load()
	{						
		//remove previous elements in the list
		container.clear();
						
		//Get the folder's contain
		File folder = new File(libraryPath); 		
		String[] listOfFiles = folder.list();

		//load only names of files
		for (int i=0; i < listOfFiles.length; i++)
		{
			int index = listOfFiles[i].indexOf(".txt");
			if (index >=0) 
				container.add(listOfFiles[i].substring(0, index).trim());			
		}
	}
	

	
	//Creates an element in the folder
	//   Creates a text file 
	//   preconditions:
	//      - The folder exists
	//   postconditions:
	//      - The file is created empty in the folder of the library 
	public void create(String element)
	{						
		try {			
				//Prepares the file for writing
				File file = new File(libraryPath + element + ".txt");
				if (!file.exists()){					
					BufferedWriter buffer = new BufferedWriter(new FileWriter(file, false));
					buffer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	
	//Delete an element from the library
	//   deletes the text file 
	//   preconditions:
	//      - The folder exists
	//   postconditions:
	//      - The file is removed from the folder of the library 
	public void delete(String element)
	{				
		File file = new File(libraryPath + element + ".txt"); 		
		if (file.exists()) 
				file.delete();	
	}
		

	//Add an element to the list
	//   Add the element to the container and creates a text file 
	//   preconditions:
	//      - The folder exists
	//   postconditions:
	//      - The file is created empty in the folder of the library 
	public void add(String element)
	{						
		//Only if the element does not exist
		if (container.indexOf(element) < 0)
		{								
			container.add(element);
			create (element);
		}
	}
				

	//Remove an element from the library
	//   removes the element and deletes the text file 
	//   preconditions:
	//      - The folder exists
	//   postconditions:
	//      - The file is removed from the folder of the library 
	public void remove(String element)
	{				
		//Only if the element exists in the container
		if (container.remove(element))
		{
			delete (element);
		}
	}

	
	//Append a MediaObject String to an element of the library
	//   Append the information in a text file
	//   preconditions:
	//      - The file exists
	public void append(String element, String mediaObjectString)
	{						
		try {
				//Only if the element exists
				if (container.indexOf(element) >= 0)
				{
					//Prepares the file for writing
					File file = new File(libraryPath + element + ".txt"); 		
					BufferedWriter buffer = new BufferedWriter(new FileWriter(file, true));
					buffer.write(mediaObjectString + "\r\n");

					//Close the file
					buffer.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	

	
	// Implements the method to convert the object as an String
	@Override
	public String toString()
	{	String data = "";
	
		for (int i=0; i < container.size(); i++)
		{
			data = data + "   - " + container.get(i) + "\n"; 
		}
		return ("\nLibrary: \n" + data);
	}		

					
} // End of the Class
