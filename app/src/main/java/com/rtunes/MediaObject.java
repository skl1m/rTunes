package com.rtunes;
/**
 * Class Media Object
 *   
 * @author rTunes team
 */
public class MediaObject {

	//Attributes
	//------------------------
	private String title;		//Name 
	private String genre;		//Rock, HipHop, Drama, Science, etc
	private String author;		//Artist(s), company, etc
	private String collection;	//Name of the album, or single, or LP, or Disc, etc
	private String fileName;	//file's name 
	private String tags;		//tags for searching
	
	//Methods
	//-----------------------------	

	//constructor
	public MediaObject(String attributes)
	{
		setAttributes(attributes);
	}

	
	//Gets
	//  methods to get each attribute
	public String getTitle()
	{
		return (title);
	}
	
	public String getGenre()
	{
		return (genre);
	}
	
	public String getAuthor()
	{
		return (author);
	}
	
	public String getCollection()
	{
		return (collection);
	}
	
	public String getFileName()
	{
		return (fileName);
	}
	
	public String getTags()
	{
		return (tags);
	}
	
		
	//Set the object's attributes from a string
	// the procedure parses the input string to get the data 
	// to fill up the attributes of the object
	//
	// precondition: the input string has the attributes
	// separated by commas as follows:
	//  author, title, genre, collection, fileName
	//
	public void setAttributes(String attributes)
	{			
		char delimiter = ',';
				
		//get the author
		int start = 0;
		int index = attributes.indexOf(delimiter);
		author = attributes.substring(start, index).trim();
		
		//get the title		
		start = index + 2;
		index = attributes.indexOf(delimiter, start);
		title= attributes.substring(start, index).trim();		

		//get the genre
		start = index + 2;
		index = attributes.indexOf(delimiter, start);
		genre = attributes.substring(start, index).trim();
				
		//get the collection
		start = index + 2;
		index = attributes.indexOf(delimiter, start);
		collection = attributes.substring(start, index).trim();
		
		//get the fileName
		start = index + 2;
		fileName = attributes.substring(start).trim();
		
		//get the tags
		tags = attributes;
	}
	

	
	// Search a coincidence in the tags
	//   return:
	//      true if there is a coincidence
	//      false: the tags does not contains the substring
	public boolean contains (String lookingFor)
	{		
		return (getTags().toUpperCase().indexOf(lookingFor.toUpperCase()) >= 0);
	}	
	
	
	
	// Implements the method to compare if two objects are equal
	@Override
	public boolean equals(Object x)
	{
		if (x == this)
			return (true);
		else
		{
			if (x == null || x.getClass() != this.getClass())
				return (false);
			else
			{	MediaObject ptr = (MediaObject) x;
				return (this.getTags().toUpperCase().equals(ptr.getTags().toUpperCase()));
			}				
		}
	}	
	
	// Implements the method to convert the object as an String
	@Override
	public String toString()
	{
		return ("Title: " + title + "\nAuthor: " + author + "\nGenre: " + genre + "\nCollection: " + 
	            collection + "\nFile: " + fileName + "\nTags: " + tags);
	}	
	

	
} //End of class
