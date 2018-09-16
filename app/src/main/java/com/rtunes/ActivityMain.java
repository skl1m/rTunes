package com.rtunes;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityMain extends Activity {
	
	//Attributes
	//------------------------------
	
	//Working with Stream Service
	private Intent serviceIntent;
	private StreamService mBoundService;
	private AudioManager audioManager;

	//View pointers to StreamService Class
	private LinearLayout layoutLoading;
	private LinearLayout layoutControls;
	private LinearLayout layoutError;	
	private TextView     txtError;	
	
	//View object
	private SeekBar skbVolume;
	public  ImageButton  btnControl;
	private int imgPlay, imgPause;	
	private EditText edtSearch;
	private Button btnSearch;
	private Button btnProfile;
	private ListView lvwSearch;
	private TextView tvwCurrentSongName;
	private TextView tvwCurrentAuthor;	
	private AdapterPlaylist adapter;
	private MediaObjectList searchResult;
	private MediaObjectList catalog;	
	
	//Methods
	//------------------------------	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Assigning layout pointers
		layoutLoading = (LinearLayout)findViewById(R.id.layoutLoading);
		layoutControls = (LinearLayout)findViewById(R.id.layoutControls);
		layoutError = (LinearLayout)findViewById(R.id.layoutError);		
		
		//Assigning interface objects
		imgPlay  = getResources().getIdentifier( "drawable/play_state", null,  getPackageName());
		imgPause  = getResources().getIdentifier( "drawable/pause_state", null,  getPackageName());				
		btnControl = (ImageButton)findViewById(R.id.btnControl);
		txtError = (TextView)findViewById(R.id.txtError);	
		edtSearch = (EditText) findViewById(R.id.edtSearch);
		lvwSearch = (ListView) findViewById(R.id.lvwSearch);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnProfile = (Button) findViewById(R.id.btnProfile);
		tvwCurrentSongName = (TextView) findViewById(R.id.tvwCurrentSongName);
		tvwCurrentAuthor = (TextView) findViewById(R.id.tvwCurrentAuthor);
		
		//Link the Button actions
		btnSearch.setOnClickListener(onClickSearch);
		btnProfile.setOnClickListener(onClickProfile);
		
		//Load the information
		catalog = new MediaObjectList("catalog");
		catalog.load(Session.storagePath());
		
		//show the full catalog by default
		searchResult = catalog.filter("");
	    adapter = new AdapterPlaylist (this, searchResult); 
	    lvwSearch.setAdapter(adapter);
	    lvwSearch.setOnItemClickListener(listItemClick);	    
	    
		//Setting up 
		setVolume();		
		serviceIntent = new Intent(this, StreamService.class);		
				
		if (isMyServiceRunning(StreamService.class))
			bindService(serviceIntent, mConnection, 0); //Context.BIND_AUTO_CREATE);
			    
	}


	//OnClick listener for the btnSearch button. 
	Button.OnClickListener onClickSearch = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {			
			MediaObjectList tmp = catalog.filter(edtSearch.getText().toString().trim());
			adapter.clear();
			adapter.addAll(tmp.getContainer()); 			
		}		
	};			

	//OnClick listener for the btnProfile button. 
	Button.OnClickListener onClickProfile = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			startActivity(new Intent(ActivityMain.this, ActivityProfile.class));			
		}		
	};			
	


	//Logic for list's listener
	ListView.OnItemClickListener listItemClick = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
			MediaObject song = adapter.getItem(position);
			tvwCurrentSongName.setText(song.getTitle());
			tvwCurrentAuthor.setText(song.getAuthor());
			play (song);
							
		}//end onItmeClick method
		
	};//end listener
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected void onDestroy() 
	{
	    // Detach our existing connection.
    	if (mBoundService!= null) 
    		unbindService(mConnection);
    	super.onDestroy();
	}

	@Override
	protected void onPause() 
	{
    	super.onPause();		
    	if (mBoundService!= null) mBoundService.doForeGround();
	}

	
	/**********************************************************************************************
     * Source:
     * http://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-in-android
     * 
     * This works reliably because it is based on the information about running 
     * services provided by the Android operating system through 
     * ActivityManager#getRunningServices.
     * 
     * All the approaches using onDestroy or onSometing events or Binders or static
     *  variables will not work reliably because as a developer you never know, 
     *  when Android decides to kill you process or which of the mentioned 
     *  callbacks are called or not. Please note the "killable" column 
     *  in the lifecycle events table in Android documentation
     */
	
	private boolean isMyServiceRunning(Class<?> serviceClass) 
	{
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) 
	        {
	            return true;
	        }
	    }
	    return false;
	}

	
	/**********************************************************************************************
    *
    **/	
	private ServiceConnection mConnection = new ServiceConnection() 
	{
	    public void onServiceConnected(ComponentName className, IBinder service) 
	    {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	    	
	        mBoundService = ((StreamService.LocalBinder)service).getService();
	        mBoundService.ptrLayoutLoading = layoutLoading;
	        mBoundService.ptrLayoutControls = layoutControls;
	        mBoundService.ptrLayoutError = layoutError;
	        mBoundService.ptrTxtError = txtError;
	        	        
			//If is playing, show pause button
			if (mBoundService.currentState()==3) 
			{
				btnControl.setImageResource(imgPause);
			}
			
			//If it is initializing, show layout loading
			if (mBoundService.currentState()==1) 
			{
				//Set layouts with controls before/after  prepared
				layoutLoading.setVisibility(1); //visible
				layoutControls.setVisibility(4); //invisible
				btnControl.setImageResource(imgPause);				
			}
	        
	    }

	    public void onServiceDisconnected(ComponentName className) 
	    {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        unbindService(this);
	        mBoundService = null;	        
	    }
	};

	
	
    /*********************************************************************************************
     * 
    */		
	public void startPlaying(View view) 
	{		
		if (isMyServiceRunning(StreamService.class)) 
		{							
			layoutControls.setVisibility(1); //visible			
			layoutLoading.setVisibility(4); //invisible			
		
			//If is playing show pause and vice versa
			if (mBoundService.isPlaying())
			{
				mBoundService.pausePlaying();
				btnControl.setImageResource(imgPlay);
			}
			else
			{			
				mBoundService.continuePlaying();
				btnControl.setImageResource(imgPause);
			}
		}
	}
		

	/**********************************************************************************************
    *
    **/			
	public void errorAction(View view) 
	{
		//Set layouts with controls before  prepared
		btnControl.setImageResource(imgPlay);		
		layoutLoading.setVisibility(4); //invisible
		layoutError.setVisibility(4); //invisible
		layoutControls.setVisibility(1); //visible
		txtError.setText("");		
	}
	
    
    /**********************************************************************************************
    *
    **/			
	public void stop(View view) 
	{
		// remove the service from background
		if (serviceIntent != null) stopService(serviceIntent);
		finish();		
	}

	
	public void play(MediaObject song)
	{		
		//set the song required
		Session.currentSong = song;
		layoutControls.setVisibility(1); //invisible			
		layoutLoading.setVisibility(4); //visible
		btnControl.setImageResource(imgPause);		
		
		if (!isMyServiceRunning(StreamService.class)) 
		{
			startService (serviceIntent);
			bindService(serviceIntent, mConnection, 0); //Context.BIND_AUTO_CREATE);			
		}
		else
		{
			mBoundService.initializeMediaPlayer();			
		}		
	}
	
	
	/***********************************************************************************************************
	 * Volume control
	 * source:
	 * 		http://android-er.blogspot.com/2010/12/implement-seekbar-to-control-volume-of.html
	 */
	
	private void setVolume()
	{	
		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		skbVolume = (SeekBar)findViewById(R.id.skbVolume);						
		skbVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		skbVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
		skbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() 
		{
			 @Override
			 public void onStopTrackingTouch(SeekBar arg0) {
			  // TODO Auto-generated method stub
			 }

			 @Override
			 public void onStartTrackingTouch(SeekBar arg0) {
			  // TODO Auto-generated method stub
			 }

			 @Override
			 public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			  // TODO Auto-generated method stub
			  audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
			 }
		});
	}	
	
}
