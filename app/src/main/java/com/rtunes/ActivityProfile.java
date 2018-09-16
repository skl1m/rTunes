package com.rtunes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityProfile extends Activity {

	//Attributes
	//------------------------------
	EditText edtNewPlaylist;
	TextView tvwUsername;
	TextView tvwEmail;
	ImageView imgUser;
	Button btnAdd;
	ListView lvwPlaylists;
	AdapterLibrary adapter;

	
	//Methods
	//------------------------------
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);	    
	
	    //Linking the objects
		tvwUsername = (TextView) findViewById(R.id.tvwUsername);		
		tvwEmail = (TextView) findViewById(R.id.tvwEmail);		
		edtNewPlaylist = (EditText) findViewById(R.id.edtNewPlaylist);
		imgUser = (ImageView) findViewById(R.id.imgUser);		
		lvwPlaylists = (ListView) findViewById(R.id.lvwPlaylists);
		btnAdd = (Button) findViewById(R.id.btnAdd);
		
		//Link the Button actions
		btnAdd.setOnClickListener(onClickAdd);
		
		//Load the information from the session
		tvwUsername.setText(Session.profile.getUsername());
		tvwEmail.setText(Session.profile.getEmail());
		imgUser.setImageBitmap(BitmapFactory.decodeFile(Session.profile.getImagePath()));
		
		//Show the list
		adapter = new AdapterLibrary (this, Session.playLists);
	    adapter.setTrashButtonVisible(true);
	    lvwPlaylists.setAdapter(adapter);	
	    lvwPlaylists.setOnItemClickListener(listItemClick);
	}


	//OnClick listener for the btnSearch button. 
	Button.OnClickListener onClickAdd = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			//If some name was typed
			String element = edtNewPlaylist.getText().toString().trim(); 
			if (!element.isEmpty())
			{
				if (Session.playLists.contains(element))
				{
					Toast.makeText(ActivityProfile.this, "Playlist " + element + " already exists.", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Session.playLists.create(element);
					adapter.add(element);
					edtNewPlaylist.setText("");					
					Toast.makeText(ActivityProfile.this, "Added playlist " + element, Toast.LENGTH_SHORT).show();	
				}
			}
		}		
	};			

	
	//Logic for list's listener
	ListView.OnItemClickListener listItemClick = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
			Session.parameter = adapter.getItem(position);
			startActivity(new Intent(ActivityProfile.this, ActivityPlaylist.class));
							
		}//end onItmeClick method
		
	};//end listener
		
	
}
