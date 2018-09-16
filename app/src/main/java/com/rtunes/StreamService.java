package com.rtunes;

import java.io.IOException;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/************************************************************************************
 * Media Player Service: execute a stream in the background by calling
 * startService() and using a connection with bindService() to control 
 * the execution from an activity.
 * Documentation:
 *    http://developer.android.com/guide/components/services.html
 *    http://developer.android.com/guide/topics/media/mediaplayer.html
 * **********************************************************************************
 * A service can essentially take two forms. This service uses both:
 * 
 * - Started:
 *   A service is "started" when an application component (such as an activity) 
 *   starts it by calling startService(). Once started, a service can run in the 
 *   background indefinitely, even if the component that started it is destroyed.
 *   Usually, a started service performs a single operation and does not return 
 *   a result to the caller. For example, it might download or upload a file 
 *   over the network. When the operation is done, the service should stop itself.
 *   
 * - Bound:
 *   A service is "bound" when an application component binds to it by calling 
 *   bindService(). A bound service offers a client-server interface that allows 
 *   components to interact with the service, send requests, get results, and even 
 *   do so across processes with interprocess communication (IPC). A bound service 
 *   runs only as long as another application component is bound to it. Multiple 
 *   components can bind to the service at once, but when all of them unbind, 
 *   the service is destroyed.
 *
 *
 * The Android system will force-stop a service only when memory is low and it must recover 
 * system resources for the activity that has user focus. If the service is bound to an activity 
 * that has user focus, then it's less likely to be killed, and if the service is declared to run
 * in the foreground (discussed later), then it will almost never be killed. Otherwise, if the 
 * service was started and is long-running, then the system will lower its position in the list 
 * of background tasks over time and the service will become highly susceptible to killing—if 
 * your service is started, then you must design it to gracefully handle restarts by the system. 
 * If the system kills your service, it restarts it as soon as resources become available again
 * (though this also depends on the value you return from onStartCommand()). 
 * For more information about when the system might destroy a service, see the Processes 
 * and Threading document.
 */

public class StreamService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, OnCompletionListener 
{
	//constants
	private static final int MAX_BUFFER_COUNT = 20;
	private static final int NOTIFICATION_ID = 13274;						//Identifier for Notification on foreground
	
	//attributes	
	private String url = Environment.getExternalStorageDirectory().toString() + "/rtunes/songs/marimba.mp3";														//Source of the music
	private final IBinder mBinder = new LocalBinder();						//Object that receives interactions from client.
	private Intent notificationIntent;										//Intent for returned activity when press the notification.
	private PendingIntent pendingIntent;									//Pending intent for foreground
	private MediaPlayer mPlayer;											//Object to connect and execute the stream
	private WifiLock wifiLock;												//To avoid WIFI sleep
	private int mpState = 0;												//Media Player State 0 = idle
	private int bufferCounter = 0;											//Counter of buffer times called without playing
//	private int currentSong = -1;											//Index to the song in the list
	
	//Interface pointers
	public LinearLayout ptrLayoutLoading = null;		//view object visible when Stream is loading
	public LinearLayout ptrLayoutControls = null;		//view object visible when Stream finish loading
	public LinearLayout ptrLayoutError = null;			//view object visible when an Error happen.
	public TextView     ptrTxtError = null;				//Text showed when an error happens
	
		
    /**********************************************************************************************
     * The system calls this method when the service is first created, to perform one-time 
     * setup procedures (before it calls either onStartCommand() or onBind()). If the service 
     * is already running, this method is not called.   
     */
	@Override
	public void onCreate()
	{		
		super.onCreate();
	}   


    /**********************************************************************************************
     * The system calls this method when the service is no longer used and is being destroyed. 
     * This service destroy the Media Player Object to clean up any resources.
     * This is the last call the service receives.
     * 
     * It is not necessary a call to "stopForeground(true)" because "stopSelf()"
     * will close "notification" automatically
     * 
     * It is absolutely necessary to release media player resources before call stopSelf()
     * in other way the service will continue running (leaked)
     *  
     */
    @Override
    public void onDestroy() 
    {
    	super.onDestroy();       	
    	closeMediaPlayer();		  					// Release media mPlayer resources
    	stopSelf(); 			  					// remove the service, automatically close notification    	
    }
    
    
    /**********************************************************************************************
     * The system calls this method when another component, such as an activity, requests that
     *  the service be started, by calling startService(). Once this method executes, the service
     *  is started and can run in the background indefinitely. 
     *  It is client responsibility to stop the service when its work is done, by calling stopSelf()
     *  or stopService(). 
     * 
     * START_STICKY:
     *   If the system kills the service after onStartCommand() returns, recreate the service and 
     *   call onStartCommand(), but do not redeliver the last intent. Instead, the system calls 
     *   onStartCommand() with a null intent, unless there were pending intents to start the service,
     *   in which case, those intents are delivered. This is suitable for media players (or similar
     *   services) that are not executing commands, but running indefinitely and waiting for a job.
     */
    @Override
	public int onStartCommand(Intent intent, int flags, int startId) 
    {   	
		initializeMediaPlayer();  //Initializing the media mPlayer object
    	//doForeGround();			  //Put on the notification
    	return START_STICKY;		        
    }    
    
    

    /**********************************************************************************************
     * Running a Service in the Foreground:
     * 
     *    A foreground service is a service that's considered to be something the user is actively
     *    aware of and thus not a candidate for the system to kill when low on memory. A foreground 
     *    service must provide a notification for the status bar, which is placed under the "Ongoing" 
     *    heading, which means that the notification cannot be dismissed unless the service is either 
     *    stopped or removed from the foreground.
     *    
     *    For example, a music mPlayer that plays music from a service should be set to run in the 
     *    foreground, because the user is explicitly aware of its operation. The notification in the 
     *    status bar might indicate the current song and allow the user to launch an activity to 
     *    interact with the music mPlayer.
     *    
     *    To request that your service run in the foreground, call startForeground(). This method 
     *    takes two parameters: an integer that uniquely identifies the notification and the 
     *    Notification for the status bar. For example:
     *    
     *    Notification notification = new Notification(R.drawable.icon, getText(R.string.ticker_text),System.currentTimeMillis());
     *    Intent notificationIntent = new Intent(this, ExampleActivity.class);
     *    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
     *    notification.setLatestEventInfo(this, getText(R.string.notification_title),getText(R.string.notification_message), pendingIntent);
     *    startForeground(ONGOING_NOTIFICATION_ID, notification);
     */
	 
	@SuppressWarnings("deprecation")
	public void doForeGround()
	{
		Notification notification = new Notification(R.drawable.ic_launcher, getText(R.string.app_name),System.currentTimeMillis());
	    notificationIntent = new Intent(this, ActivityMain.class);
	    pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);		
	    notification.setLatestEventInfo(this, getText(R.string.app_name),getText(R.string.rtunes_slogan), pendingIntent);	    
	    startForeground(NOTIFICATION_ID, notification);	    
	}
		
		
	/**********************************************************************************************
     * Class for clients to access the service.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder 
    {
    	StreamService getService() 
    	{
            return StreamService.this;
        }
    }

    
    /**********************************************************************************************
     * The system calls this method when another component wants to bind with the service, 
     * by calling bindService(). Return an IBinder as interface that clients use to communicate 
     * with the service.
     * 
     * mBinder is an instance of LocalBinder Class 
     */ 
    @Override
    public IBinder onBind(Intent intent) 
    {
        return mBinder;
    }
    
    
    /**********************************************************************************************
     * When designing applications that play media in the background, the device may 
     * go to sleep while your service is running. Because the Android system tries to
     * conserve battery while the device is sleeping, the system tries to shut off any 
     * of the phone's features that are not necessary, including the CPU and the WiFi 
     * hardware. However, if your service is playing or streaming music, you want to 
     * prevent the system from interfering with your playback.
     * 
     * Any application using a WifiLock must request the android.permission.WAKE_LOCK
     * permission in an <uses-permission> element of the application's manifest
     */
    private void setWakeLocks()
    {
    	//avoid CPU sleep: the MediaPlayer holds the specified lock while playing and
    	//releases the lock when paused or stopped
    	mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    	
    	//avoid WIFI sleep
    	wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
    	wifiLock.acquire();   //avoid WIFI sleep
    }

        
    /**********************************************************************************************
     * The service runs in the main thread of its hosting process, for that reason we prepare
     * the media mPlayer to run in Async mode using a separate thread, reducing the risk of Application 
     * Not Responding (ANR).
     */
    public void initializeMediaPlayer()
    {    	
		//start the object as a stream music type
		if (mPlayer == null) mPlayer = new MediaPlayer();
		mPlayer.reset();											// Release Media Player resources 
		mpState = 1;												// Media Player mpState 1 = initializing    	        	
		setWakeLocks();												// Avoid sleep
    	if (Session.currentSong != null)
    	{
    		//Run Media Player Async
    		try 
    		{
    			setUrl (Session.currentSong);
        		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);    	//will play a stream
   				mPlayer.setDataSource(url);					    		//set the address of the stream        		
        		mPlayer.setLooping(false);									//onCompletion method is called when playback completes
                mPlayer.setOnErrorListener(this);		        			//set the listener of error method
                mPlayer.setOnBufferingUpdateListener(this);     			//set the listener of buffering method
                mPlayer.setOnPreparedListener(this);						//set the listener of prepared method
                mPlayer.setOnCompletionListener(this);	
    			mPlayer.prepareAsync(); 									//prepare async to not block main thread                			
    		} 
    		catch (IllegalArgumentException e) 
    		{
    			e.printStackTrace();
    		} 
    		catch (IllegalStateException e) 
    		{
    			e.printStackTrace();
    		} 
    		catch (IOException e) 
    		{
    			e.printStackTrace();
    		}
    	}
    }
    

    /**********************************************************************************************
     * 
     */
	@Override
	public void onPrepared(MediaPlayer mPlayer) 
	{
		//Reproduce music
		mpState = 3;			//Media Player Sate 2 = Prepared
		mPlayer.start();
		
		//Set layouts with controls after prepared
		if (ptrTxtError != null) ptrTxtError.setText("");
		if (ptrLayoutError != null) ptrLayoutError.setVisibility(4); //invisible
		if (ptrLayoutLoading != null) ptrLayoutLoading.setVisibility(4); //invisible
		if (ptrLayoutControls != null) ptrLayoutControls.setVisibility(1); //visible
		
	}


    /**********************************************************************************************
     * 
     */
	@Override
	public void onCompletion(MediaPlayer mp) {	
		
	}
	
   /**********************************************************************************************
    * 
    */	
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) 
	{
		
		//Set layouts with error message
		if (ptrLayoutLoading != null) ptrLayoutLoading.setVisibility(4); //invisible
		if (ptrLayoutControls != null) ptrLayoutControls.setVisibility(4); //invisible
		if (ptrLayoutError != null) ptrLayoutError.setVisibility(1); //visible
		if (ptrTxtError != null)
			if(extra==MediaPlayer.MEDIA_ERROR_IO) 
				ptrTxtError.setText("Error connecting streaming. Check Internet Connection.");
			else			
				ptrTxtError.setText("Error connecting streaming. Send us error code: ");

		onDestroy();
		return false;
	}

	/**********************************************************************************************
	 * 
	 */		
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int arg1) {

		if (mp.isPlaying() || mpState==4)  //if is playing or in pause, reset counter
		{
			if (ptrLayoutLoading != null) ptrLayoutLoading.setVisibility(4); //invisible
			if (ptrLayoutControls != null) ptrLayoutControls.setVisibility(1); //visible			
			bufferCounter = 0;
		}
		else
		{
			//show loading layout
			if (ptrLayoutControls != null) ptrLayoutControls.setVisibility(4); //invisible
			if (ptrLayoutLoading != null) ptrLayoutLoading.setVisibility(1); //visible

			//if persist, send an error
			if(++bufferCounter==MAX_BUFFER_COUNT)
			{
				//Set layouts with error message
				bufferCounter = 0;
				if (ptrLayoutLoading != null) ptrLayoutLoading.setVisibility(4); //invisible
				if (ptrLayoutControls != null) ptrLayoutControls.setVisibility(4); //invisible
				if (ptrLayoutError != null) ptrLayoutError.setVisibility(1); //visible
				if (ptrTxtError != null) ptrTxtError.setText("Time out connecting streaming. Check Internet Connection. ");
		
				onDestroy();
			}
		}
	}
	
    
    /**********************************************************************************************
     * Stop playing and release Media Player resources before service is stopped
     *   
     *  Do not use Release() because it produces an error calling isPlaying(), stop(), etc
     *  Do not use "mPlayer = null because" it produces an error when service stop itself: stopSelf()
     */
    public void closeMediaPlayer() 
	{
    	//Only if Media Player Object was initialized	
    	if (mPlayer != null)
    	{
    		stopPlaying();					// Stop streaming
    		mPlayer.reset();				// release Media Player resources. Do not use release() 
    		mpState = 0;				  	// Media Player mpState 0 = idle
    	}
    		
        // Release WiFi if it was locked to avoid sleep
        if(wifiLock.isHeld()) 
           wifiLock.release();		
    }
    

    /**********************************************************************************************
     * 
     */
    public void stopPlaying() 
	{
    		if (mPlayer.isPlaying())
    		{
    			mPlayer.stop();
    		}
    }
    
    
    /**********************************************************************************************
     * 
     */
    public void pausePlaying() 
   	{
           if (mPlayer.isPlaying())
           {
               mPlayer.pause();
               mpState = 4; 	// Media Player State 4 = Paused
           }
    }

    /**********************************************************************************************
     * 
     */
    public void continuePlaying() 
   	{
           if (mpState==4)  //if paused
           {
        	   mPlayer.start();
        	   mpState = 3;  //Media Player state 3 = playing
           }
           else
        	   if (mpState==0)  //Not initialized
        		   initializeMediaPlayer();
    }

    
    /**********************************************************************************************
     * 
     */
    public boolean isPlaying() 
   	{
           return mPlayer.isPlaying();
    }



    /**********************************************************************************************
     * 
     */
    public int currentState() 
   	{
           return mpState;
    }

    /**********************************************************************************************
     * 
     */
    public void play (MediaObject song)
    {
        initializeMediaPlayer();
    }
    
    
    public void setUrl (MediaObject song)
    {
    	url = Session.musicPath() + song.getFileName();
    }

    
    /*
    
      
    public void initializeMediaPlayer()
    {    	
    		//start the object as a stream music type
    		if (mPlayer == null) mPlayer = new MediaPlayer();
    		mPlayer.reset();			// Release Media Player resources 
    		mpState = 1;				// Media Player mpState 1 = initializing    	        	
    		setWakeLocks();				// Avoid sleep
	
    		//Run Media Player Async
    		try 
    		{        	        	
        		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);    	//will play a stream        	
        		mPlayer.setDataSource(url);					    		//set the address of the stream
        		mPlayer.setLooping(false);									//onCompletion method is called when playback completes
                mPlayer.setOnErrorListener(this);		        			//set the listener of error method
                mPlayer.setOnBufferingUpdateListener(this);     			//set the listener of buffering method
                mPlayer.setOnPreparedListener(this);						//set the listener of prepared method
                mPlayer.setOnCompletionListener(this);
 	            //mPlayer.prepareAsync(); 									//prepare async to not block main thread
    		} 
    		catch (IllegalArgumentException e) 
    		{
    			e.printStackTrace();
    		} 
    		catch (IllegalStateException e) 
    		{
    			e.printStackTrace();
    		} 
    		catch (IOException e) 
    		{
    			e.printStackTrace();
    		}
    }

    
     */
    
    
}
