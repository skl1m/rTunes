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

public class AdapterLibrary extends ArrayAdapter<String>{

	//Attributes
	//------------------------------	
	private Context context;
	private Boolean trashButtonVisible = false;
	private Library library;
	
	//Methods
	//------------------------------		
	public AdapterLibrary(Context context, Library theLibrary) {
		super(context, 0, theLibrary.getContainer());
		this.library = theLibrary;		
		this.context = context;
	}

	//Sets
	public void setTrashButtonVisible(Boolean isVisible)
	{
		trashButtonVisible = isVisible;
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		 // Get the data item for this position
		String item = getItem(position);   
	       
	    if (convertView == null) {
	    	convertView = LayoutInflater.from(getContext()).inflate(R.layout.itemlistview_playlist, parent, false);
	    } 

	    //Handle TextView 
	    TextView tvwPlaylistName = (TextView)convertView.findViewById(R.id.tvwPlaylistName); 
	    tvwPlaylistName.setText(item); 

	    //Handle buttons
	    Button btnRemovePlaylist = (Button)convertView.findViewById(R.id.btnRemovePlaylist);
	    if (!trashButtonVisible) btnRemovePlaylist.setVisibility(View.GONE); //hide the button
	    
	    btnRemovePlaylist.setOnClickListener(new View.OnClickListener(){
	        @Override
	        public void onClick(View v) { 
	        	
	        	//Show up the Yes/No message box					
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder
    			.setTitle("Delete a Playlist")
    			.setMessage("Confirm to delete " + getItem(position))
    			.setIcon(android.R.drawable.ic_dialog_alert)
    			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    				
    					public void onClick(DialogInterface dialog, int which) {			    						
    						//Yes button clicked, send SMS restore factory
    			        	String item = getItem(position);
    			        	remove(item);
    			        	library.delete(item);
    			        	Toast.makeText(context, "Deleted: " + item , Toast.LENGTH_SHORT).show();	    	        	
    			            notifyDataSetChanged();
    						
    					}
    			})//End positive button
    			.setNegativeButton("No", null)	//Do nothing
    			.show();	        	
	        		        	
	        }
	    });
	    	    
	    return convertView; 
	} 	
		
}
