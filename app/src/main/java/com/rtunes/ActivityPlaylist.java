package com.rtunes;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityPlaylist extends Activity {
	
	//Attributes
	//------------------------------
	TextView tvwListName;
	Button btnPlaylist;
	ListView lvwPlaylist;
	AdapterPlaylist adapter;
	MediaObjectList playlist;
	
	//Methods
	//------------------------------
		
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_playlist);
	
	    //Linking the objects
		tvwListName = (TextView) findViewById(R.id.tvwListName);		
		lvwPlaylist = (ListView) findViewById(R.id.lvwPlaylist);
		btnPlaylist = (Button) findViewById(R.id.btnPlaylist);

		//Load the information from the session
		tvwListName.setText(Session.parameter);
		playlist = new MediaObjectList(Session.parameter); 
		playlist.load(Session.playLists.getLibraryPath());
		
		//Show The list
		adapter = new AdapterPlaylist (this, playlist);
	    adapter.setAddButtonVisible(false);
	    adapter.setTrashButtonVisible(true);
	    lvwPlaylist.setAdapter(adapter);	    
	}
}
