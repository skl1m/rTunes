package com.rtunes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AdapterPlaylist extends ArrayAdapter<MediaObject>{


	//Attributes
	//------------------------------	
	private Context context;
	private Boolean trashButtonVisible = false;
	private Boolean addButtonVisible = true;
	private MediaObjectList playlist;
	private String pickedPlaylist;
	
	//Methods
	//------------------------------		
	public AdapterPlaylist(Context context, MediaObjectList thePlaylist) {
		super(context, 0, thePlaylist.getContainer());
		this.playlist = thePlaylist;		
		this.context = context;
	}

	//Sets
	public void setTrashButtonVisible(Boolean isVisible)
	{
		trashButtonVisible = isVisible;
	}

	public void setAddButtonVisible(Boolean isVisible)
	{
		addButtonVisible = isVisible;
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		 // Get the data item for this position
		MediaObject song = getItem(position);   
	       
	    if (convertView == null) {
	    	convertView = LayoutInflater.from(getContext()).inflate(R.layout.itemlistview_song, parent, false);
	    } 

	    //Handle TextView 
	    TextView tvwSongName = (TextView)convertView.findViewById(R.id.tvwCurrentSongName); 
	    TextView tvwSongArtist = (TextView)convertView.findViewById(R.id.tvwSongArtist);
	    TextView tvwSongAlbum = (TextView)convertView.findViewById(R.id.tvwSongAlbum);
	    	    
	    //Display string from the list
	    tvwSongName.setText(song.getTitle()); 
	    tvwSongArtist.setText(song.getAuthor());
	    tvwSongAlbum.setText("Album: " + song.getCollection());	    

	    //Handle buttons
	    Button btnRemoveSong = (Button)convertView.findViewById(R.id.btnRemoveSong);
	    if (!trashButtonVisible) btnRemoveSong.setVisibility(View.GONE); //hide the button
	    
	    Button btnAddSong = (Button)convertView.findViewById(R.id.btnAddSong);
	    if (!addButtonVisible) btnAddSong.setVisibility(View.GONE); //hide the button

	    //Add onClickListeners
	    btnAddSong.setOnClickListener(new View.OnClickListener(){
	        @Override
	        public void onClick(View v) {
	        	//Get the data
	        	final MediaObject song = getItem(position);
	        	
	        	//At least one playlist is required
	        	if (Session.playLists.getContainer().size() <=0)
	        	{
	        		Toast.makeText(context, "First create at least one playlist in your profile", Toast.LENGTH_SHORT).show();
	        	}
	        	else
	        	{
		        	//Show the library in a dialog box
	        		pickedPlaylist = "";
		     	    final String[] items = Session.playLists.toArrayStrings();	        	
		       	    AlertDialog.Builder builder = new AlertDialog.Builder(context);
		     	    builder.setTitle("Pick a playlist");
		     	    builder.setItems(items, new DialogInterface.OnClickListener() {
		     	       public void onClick(DialogInterface dialog, int which) {
		     	    	   
		     	    	  //Add the song to the playlist 
		     	    	  pickedPlaylist = Session.playLists.getContainer().get(which);
			  	          Session.playLists.append(pickedPlaylist, song.getTags());
			  	          Toast.makeText(context, "Added: " + song.getTitle() + " to " + pickedPlaylist, Toast.LENGTH_SHORT).show();			  	          
		     	       }
		     	    });
		            AlertDialog alert = builder.create();
		            alert.show();	    		            
	        	}	        		        		        
	        	
	            notifyDataSetChanged();
	        }
	    });

	    btnRemoveSong.setOnClickListener(new View.OnClickListener(){
	        @Override
	        public void onClick(View v) { 
	            //do something
	        	final MediaObject song = getItem(position);
	        	
	        	//Show up the Yes/No message box					
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder
	    			.setTitle("Remove a Song")
	    			.setMessage("Confirm to remove " + song.getTitle())
	    			.setIcon(android.R.drawable.ic_dialog_alert)
	    			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    				
	    					public void onClick(DialogInterface dialog, int which) {			    						
	    						//Yes button clicked, send SMS restore factory
	    			        	remove(song);
	    			        	playlist.save(Session.playLists.getLibraryPath());	        	
	    			        	Toast.makeText(context, "Removed: " + song.getTitle() , Toast.LENGTH_SHORT).show();	    						
	    					}
	    			})//End positive button
	    			.setNegativeButton("No", null)	//Do nothing
	    			.show();	 	        	
	        		        	
	            notifyDataSetChanged();
	        }
	    });
	    
	    
	    return convertView; 
	} 	
	

}
