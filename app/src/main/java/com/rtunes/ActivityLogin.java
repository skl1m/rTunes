package com.rtunes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivityLogin extends Activity {


	//Attributes
	//------------------------------
	AccessControl myApp = new AccessControl();
	EditText edtUsername;
	EditText edtPassword;
	TextView tvwMessage;
	Button btnLogin;
		

	//Methods
	//------------------------------
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_login);
	
	    //Linking the objects
		edtUsername = (EditText) findViewById(R.id.edtUsername);
		edtPassword = (EditText) findViewById(R.id.edtPassword);		
		tvwMessage = (TextView) findViewById(R.id.tvwMessage);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(onClickLogin);
	}
	

	//OnClick listener for the btnMarimba button. 
	Button.OnClickListener onClickLogin = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			
			//Attempt to login 
			String result = myApp.login(edtUsername.getText().toString(), edtPassword.getText().toString());
			if (result.isEmpty())
			{
				tvwMessage.setText("Wrong username or password. Try again");
				edtPassword.setText("");
				edtUsername.requestFocus();
			}
			else
			{
				tvwMessage.setText("Welcome!!");
				edtUsername.setText("");
				edtPassword.setText("");
				edtUsername.requestFocus();
				finish();
				startActivity(new Intent(ActivityLogin.this, ActivityMain.class));				
			}
		}		
	};		


}
