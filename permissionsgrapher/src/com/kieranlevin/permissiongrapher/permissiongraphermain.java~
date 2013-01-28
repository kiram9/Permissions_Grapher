/************************

This is a visualization application that allows you to see potential interactions 
between multiple android applications - Eg. this way if you wanted to be sure that 
no information could leak from one part of your android phone to the internet 
you could run this to find all potential paths through multiple applications that 
would allow information to leak from one protected resource. 

For Example - your address book may be very important to you - and an enemy 
wishes to read this information. They may write an application that has 
permission to read your address book and also connect to the internet. 
Using this application they can fetch your addresses and upload them to 
their command and control server. 

Now lets say you are very smart - and know they will do this - so you never 
install a program that can read your address book and also connect to the internet.
Well your enemy needs to come up with a better plan. They invent a way to do this. 

They can share the information through a shared resource such as your file system
and use two applications to sneakily leak data through a shared resource. 

Eg: 
Application1 has access to address book and SD card. 
Applcation2 has access to SD card and internet. 

Now your addresses can move from Address book -> SD card -> Internet.

Permissions grapher is used to figure out any potential route from a protected 
resource on your phone to the internet through a graphical map. Enjoy!


Copyright 2007 Kieran Levin 


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
//Some of this code is based on the tutorial for drawing graphics at 
//http://www.droidnova.com/playing-with-graphics-in-android-part-i,147.html
//
package com.kieranlevin.permissiongrapher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import java.lang.Math;


public class permissiongraphermain extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new Panel(this));

    }
    //All the main permission graphing and node generating stuff goes on in here
    class Panel extends SurfaceView implements SurfaceHolder.Callback {
        private TutorialThread _thread;
        //private ArrayList<GraphicObject> _graphics = new ArrayList<GraphicObject>();
        private ArrayList<ApplicationObject> _appicons = new ArrayList<ApplicationObject>();
        //private GraphicObject _currentGraphic = null;
        private int offsetx = 0, offsety = 0, _radius = 0, secondradius = 600; 
        private ArrayList<String> pgroups = new ArrayList<String>(); 
        private String _target = ""; 
        private ApplicationLink _rootoutlink = new ApplicationLink();
        public Panel(Context context) {
            super(context);
            
            //read applications
            ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */
            
            //read in the list,,, add to lists
            //find how many packages have permissions
            float count = 0;
            for (PInfo packinfoIndx : apps){
            	if (packinfoIndx.permissions != null){
            		count++;
            	}
            }
            //generate a circle of primary linked apps around target resource
            double theta = 2*3.14 / (count);
            //reuse count
            _radius = 10*(int)count;
            offsetx = -_radius+50;
            offsety = -_radius+50; 
            count = 0; 
            
            for (PInfo packinfoIndx : apps){
            	
            	if (packinfoIndx.permissions != null){
                	count++; 
                	float x = (float)(_radius*Math.cos(theta*count) + _radius);
                	float y = (float)(_radius*Math.sin(theta*count)+ _radius);
	            	try {
			//add app object to our list of app icons
	            	_appicons.add(new ApplicationObject(packinfoIndx.icon,
	            				packinfoIndx.appname,x,y,packinfoIndx.permissions,_appicons));
	            	}catch (Exception ex ){
	            		
	            	}
            	}
            }
            
            //setup relationships between packages 
            
            Mydialog();

            getHolder().addCallback(this);
            _thread = new TutorialThread(getHolder(), this);
            setFocusable(true);
            
            
        }
        private int oldx = 0, oldy = 0;
        //handle touch events this interfaces with canvas translate to move our view around, this updates the offsets in translate()
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            synchronized (_thread.getSurfaceHolder()) {
                //GraphicObject graphic = null;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                	oldx = (int)event.getX();
                	oldy = (int)event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			//update relative position dy dx
                	offsetx += (int)event.getX() - oldx;
                	offsety += (int)event.getY() - oldy;
                	oldx = (int)event.getX();
                	oldy = (int)event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //nothing for now
                }
                return true;
            }
        }
        //initial dialog presented to choose permission target, and generate all the apps that link to it 
        public void Mydialog(){
        	
        	//generate a list of all the permissions fields 
        	
        	for (ApplicationObject appicon : _appicons) {
        		String [] apppermissions = appicon.getPermissions(); 
        		for (String thispermission : apppermissions){
        			if (false == pgroups.contains(thispermission)){
        				pgroups.add(thispermission); 
        			}
        		}
        	}
        	//add all the groups to a nice charseq so we can use it in our dialog 
        	CharSequence[] items = new CharSequence[pgroups.size()]; 
        	for (int i = 0; i < pgroups.size(); i++){
        		items[i] = pgroups.get(i).toString();
        	}
        	AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        	builder.setTitle("Pick Attack Target");
        	builder.setItems(items, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	    	setTarget(pgroups.get(item).toString());
        	    	
        	    }
        	});
        	AlertDialog alert = builder.create();
        	alert.show();
        	
        }
        //once the user has selected a target this actually builds the applications that touch the target and adds them to a ll
        public void setTarget(String target){
        	_target = target; 
        	int count = 0; 
        	for (ApplicationObject appicon : _appicons) {
        		if (Arrays.binarySearch(appicon.getPermissions(),target) >= 0){ //we have a match 
        			_rootoutlink.addelement(_appicons.indexOf(appicon));//add our match
        			count++;
	            	appicon.setTraversed(true);
	            	appicon.incTraversedlevel();
        		}
        		else appicon.setTraversed(false);
        		
        	}
        	//make an inner circle of accepted applications
            double theta = 2*3.14 / (count);
            //reuse count
            int radius = 10*(int)count;
            count = 0; 
            if (radius < 150) radius = 150; 
            secondradius = 2*_radius;
            //reassign values for coordinates of inner circle
            for (ApplicationObject appicon : _appicons) {
            	
            	if (Arrays.binarySearch(appicon.getPermissions(),target) >= 0){
                	count++; 
                	float x = (float)(radius*Math.cos(theta*count) + _radius);
                	float y = (float)(radius*Math.sin(theta*count)+ _radius);
	            	try {

	            	appicon._coordinates.setX((int)x);
	            	appicon._coordinates.setY((int)y);

	            	}catch (Exception ex ){
	            		
	            	}
            	}
            }
            //for (int k = 1; k < 3; k++){
            	//iterate over all values and build node graph 
            	for (ApplicationObject appicon : _appicons) {

            		if (appicon.getTraversedlevel() == 1){
            			String[] permissions = appicon.getPermissions();

            			for (int j = 0; j < _appicons.size(); j++) {//possibletarget links 
            				String[] targetpermissions = _appicons.get(j).getPermissions();
            				for (int i = 0; i < permissions.length; i++){
            					String CurrentSource = permissions[i];
            					//conditionals for one way links on graph so we follow them properly 
            					if (CurrentSource.equals(Manifest.permission.WRITE_CALENDAR))
            						CurrentSource = Manifest.permission.READ_CALENDAR;

            					if (CurrentSource.equals(Manifest.permission.WRITE_CONTACTS))
            						CurrentSource = Manifest.permission.READ_CONTACTS;

            					if (CurrentSource.equals(Manifest.permission.WRITE_HISTORY_BOOKMARKS))
            						CurrentSource = Manifest.permission.READ_HISTORY_BOOKMARKS; 

            					if (CurrentSource.equals(Manifest.permission.WRITE_SMS))
            						CurrentSource = Manifest.permission.READ_SMS;

            					if (CurrentSource.equals(Manifest.permission.WRITE_SYNC_SETTINGS))
            						CurrentSource = Manifest.permission.READ_SYNC_SETTINGS;

            					if (Arrays.binarySearch(targetpermissions, CurrentSource) >= 0){
            						//we have a match 
            						if (appicon.getTraversedlevel() > _appicons.get(j).getTraversedlevel()){ //make sure we dont link ourselves with ourselves 
            							appicon.Outlinks.addelement(j);	
            							_appicons.get(j).setTraversed(true);
            						}

            					}
            				}

            			}
            			appicon.setTraversed(true);
            			appicon.incTraversedlevel();	//will increment to 2
            			
            		}

            	}
            //}
            	
            	
            	//iterate over all values and build node graph to public facing resources
            	for (ApplicationObject appicon : _appicons) {

            		if ((appicon.getTraversedlevel() == 0) && appicon.isTraversed()){
            			String[] permissions = appicon.getPermissions();
            			float zonex = 0;
                    	float zoney = 0;
            			boolean isvalid = false; 
            			for (int j = 0; j < permissions.length; j++) {//possibletarget links 
            				String targetpermissions = permissions[j];
					//build a big circle with different dangerous permissions so we can place apps at these points 
            				if (targetpermissions.equals(Manifest.permission.INTERNET.toString())){
            					isvalid = true; 
            					zonex = (float)(secondradius*Math.cos(2*3.14 / (5)*0) + _radius);
            					zoney = (float)(secondradius*Math.sin(2*3.14 / (5)*0)+ _radius);
            				}
            				else if (targetpermissions.equals(Manifest.permission.CALL_PHONE.toString())){
            					isvalid = true; 
            					zonex = (float)(secondradius*Math.cos(2*3.14 / (5)*1) + _radius);
            					zoney = (float)(secondradius*Math.sin(2*3.14 / (5)*1)+ _radius);
            				}
            				else if (targetpermissions.equals(Manifest.permission.CALL_PRIVILEGED.toString())){
            					isvalid = true; 
            					zonex = (float)(secondradius*Math.cos(2*3.14 / (5)*2) + _radius);
            					zoney = (float)(secondradius*Math.sin(2*3.14 / (5)*2)+ _radius);
            				}
            				else if (targetpermissions.equals(Manifest.permission.BLUETOOTH_ADMIN.toString())){
            					isvalid = true; 
            					zonex = (float)(secondradius*Math.cos(2*3.14 / (5)*3) + _radius);
            					zoney = (float)(secondradius*Math.sin(2*3.14 / (5)*3)+ _radius);
            				}
            				else if (targetpermissions.equals(Manifest.permission.SEND_SMS.toString())){
            					isvalid = true; 
            					zonex = (float)(secondradius*Math.cos(2*3.14 / (5)*4) + _radius);
            					zoney = (float)(secondradius*Math.sin(2*3.14 / (5)*4)+ _radius);
            				}

            			}
            			if (isvalid){
	            			appicon.setTraversed(true);
	            			appicon.incTraversedlevel();
	            			appicon._coordinates.setX((int)zonex);
	            			appicon._coordinates.setY((int)zoney);
	            			
            			}
            			else appicon.setTraversed(false);
            		}

            	}
            	movement = 2000;
            //}
        	
        }
        private int movement = 0;
        //used so if icons are too close we push them away from each other to make the graph look readable 
        public void updatePhysics() {
        	if (movement > 10){
        		movement = 0;
        	Random myRandom = new Random();
            for (ApplicationObject appicon : _appicons) {
            	//update the variables in appicon here
            	int mydeltaX = 0; 
            	int mydeltaY = 0; 
            	
            	int myx = appicon._coordinates.getX();
            	int myy = appicon._coordinates.getY();
            	//iterate over all icons that are being displayed and move them if necessary 
            	for (ApplicationObject othericon : _appicons) {
            		int otherx = othericon._coordinates.getX();
                	int othery = othericon._coordinates.getY();
                	//if they are on top of another icon, move them away slightly 
                	if (appicon.getName().equals(othericon.getName()) == false){
	                	if ((otherx - myx) == 0){
	                		mydeltaX += myRandom.nextInt(10)-5;
	                		movement++;
	                	} 
	                	if ((othery - myy) == 0){
	                		mydeltaY += myRandom.nextInt(10)-5;
	                		movement++;
	                	}
	                	{
	                		//if they are still too close, move them away in a vector pointing away from the average to other icons
		                	if (Math.abs(otherx - myx) <= 8 && (otherx - myx) != 0){
		                		mydeltaX += -16/(otherx - myx);
		                		movement++;
		                	}
		                	if (Math.abs(othery - myy) <= 8 && (othery - myy) != 0){
		                		mydeltaY += -16/(othery - myy);
		                		movement++;
		                	}
	                	}
	                	
	                }
            	}
            	//actually move them 
            	appicon._coordinates.setX(mydeltaX+myx);
            	appicon._coordinates.setY(mydeltaY+myy);
            }
        	}
        }
 
        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawColor(Color.BLACK);
            //do all the drawing here! TODO: 
            canvas.save();
        	canvas.translate(offsetx, offsety);
            //draw the different apps
            for (ApplicationObject appicon : _appicons) {
            	if (appicon.isTraversed() == true)
                appicon.DrawApp(canvas);
            }
            
            //draw target (center) 
        	Paint paint = new Paint();
        	paint.setColor(Color.DKGRAY);
        	paint.setStyle(Style.FILL);
        	canvas.drawCircle(_radius, _radius, 100, paint);

        	//draw zones for different zones 
        	paint.setColor(Color.RED);
        	paint.setTextSize(20);
        	int zonenum = 0;
			canvas.drawText(Manifest.permission.INTERNET.toString(), (float)(secondradius*Math.cos(2*3.14 / (5)*zonenum) + _radius), (float)(secondradius*Math.sin(2*3.14 / (5)*zonenum)+ _radius), paint);
			zonenum++;
			canvas.drawText(Manifest.permission.CALL_PHONE.toString(), (float)(secondradius*Math.cos(2*3.14 / (5)*zonenum) + _radius), (float)(secondradius*Math.sin(2*3.14 / (5)*zonenum)+ _radius), paint);
			zonenum++;
			canvas.drawText(Manifest.permission.CALL_PRIVILEGED.toString(), (float)(secondradius*Math.cos(2*3.14 / (5)*zonenum) + _radius), (float)(secondradius*Math.sin(2*3.14 / (5)*zonenum)+ _radius), paint);
			zonenum++;
			canvas.drawText(Manifest.permission.BLUETOOTH_ADMIN.toString(), (float)(secondradius*Math.cos(2*3.14 / (5)*zonenum) + _radius), (float)(secondradius*Math.sin(2*3.14 / (5)*zonenum)+ _radius), paint);
			zonenum++;
			canvas.drawText(Manifest.permission.SEND_SMS.toString(), (float)(secondradius*Math.cos(2*3.14 / (5)*zonenum) + _radius), (float)(secondradius*Math.sin(2*3.14 / (5)*zonenum)+ _radius), paint);
			
        	//draw root outdegree lines to other nodes.... 
        	for (int i = 0; i < _rootoutlink.nodepointer.size(); i++){
        		int coordx = _appicons.get(_rootoutlink.nodepointer.get(i)).getCoordinates().getX();
        		int coordy = _appicons.get(_rootoutlink.nodepointer.get(i)).getCoordinates().getY();
        		
        		canvas.drawLine(_radius, _radius, coordx+20, coordy+20, paint);
        	}
        	
        	paint.setColor(Color.BLACK);
        	paint.setTextSize(10);
        	canvas.drawText(_target, _radius-95, _radius, paint);
        	
            canvas.restore();

        }
 
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // TODO Auto-generated method stub
        }
 
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            _thread.setRunning(true);
            _thread.start();
        }
 
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // simply copied from sample application LunarLander:
            // we have to tell thread to shut down & wait for it to finish, or else
            // it might touch the Surface after we return and explode
            boolean retry = true;
            _thread.setRunning(false);
            while (retry) {
                try {
                    _thread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    // we will try it again and again...
                }
            }
        }
    }
 
    class TutorialThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private Panel _panel;
        private boolean _run = false;
 
        public TutorialThread(SurfaceHolder surfaceHolder, Panel panel) {
            _surfaceHolder = surfaceHolder;
            _panel = panel;
        }
 
        public void setRunning(boolean run) {
            _run = run;
        }
 
        public SurfaceHolder getSurfaceHolder() {
            return _surfaceHolder;
        }
 
        @Override
        public void run() {
            Canvas c;
            while (_run) {
                c = null;
                try {
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {
                        _panel.updatePhysics();
                        _panel.onDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }
    //simple class that holds info about each app
    class PInfo {  
    	public String appname = "";  
    	public String pname = "";  
    	public String versionName = "";  
    	public int versionCode = 0;  
    	public Drawable icon;  
    	public String [] permissions = null; 

    }  
    //geneartes a nice arraylist of all installed packages 
    private void listPackages() {  
    	ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */  
    	final int max = apps.size();  
    	for (int i=0; i<max; i++) {  
    		//apps.get(i).prettyPrint();  
    	}  
    }  
	//builds list of all installed packages
    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {  
    	ArrayList<PInfo> res = new ArrayList<PInfo>();          
    	List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);  
    	for(int i=0;i<packs.size();i++) {  
    		PackageInfo p = packs.get(i);  
    		if ((!getSysPackages) && (p.versionName == null)) {  
    			continue ;  
    		}  
    		PInfo newInfo = new PInfo();  
    		//loads name
    		newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();  
    		newInfo.pname = p.packageName;  
    		newInfo.versionName = p.versionName;  
    		newInfo.versionCode = p.versionCode;  
    		//to get permissions we have to manually call the getPackageInfo otherwise this info does not get populated
    		PackageManager pack = getPackageManager();
    		try{
    		PackageInfo info = pack.getPackageInfo(p.packageName, PackageManager.GET_PERMISSIONS);
    		newInfo.permissions = info.requestedPermissions;
    		Arrays.sort(newInfo.permissions);
    		}catch(Exception ex){
    			
    		}
    		newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());  
    		res.add(newInfo);  
    	}  
    	return res;   
    }  
    //has a list of pointers from current node to the index of ther nodes so we can easily walk through the list.
    class ApplicationLink {
    	public ArrayList<Integer> nodepointer = new ArrayList<Integer>();
    	public void addelement(Integer element){
    		nodepointer.add(element);
    	}
    }
    //all the data about each application is stored here, including its display position, 
    //links to other nodes, outdegrees, etc... using some of the classes above
    class ApplicationObject {

        /**
         * Contains the coordinates of the graphic.
         */
    	private int _x = 10;
        private int _y = 0;
        public class Coordinates {
            
 
            public int getX() {
                return _x ;
            }
 
            public void setX(int value) {
                _x = value;
            }
 
            public int getY() {
                return _y;
            }
 
            public void setY(int value) {
                _y = value;
            }
 
            public String toString() {
                return "Coordinates: (" + _x + "/" + _y + ")";
            }
        }
 
        private Drawable _icon;
        public Coordinates _coordinates;
        private String _name; 
        private String [] _permissions; 
        private boolean _traversed; 
        private int _traverselevel = 0; 
        private ArrayList<ApplicationObject> _appicons;
        public ApplicationLink Outlinks = new ApplicationLink();  
        //Constructor 
        public ApplicationObject(Drawable icon, String name, float x, float y,String[] permissions ,ArrayList<ApplicationObject> appicons) {
            _coordinates = new Coordinates();
            _name = name; 
            _icon = icon; 
            _x = (int)x; 
            _y = (int)y;
            _permissions = permissions; 
            _icon.setBounds(0,0,_icon.getIntrinsicWidth(),_icon.getIntrinsicHeight());
            _appicons = appicons; 
        }
        //if we have walked this node to genearte the graph we set this true so we dont try and come back to it and walk it again 
        public boolean isTraversed(){
        	return _traversed; 
        }
        public void setTraversed(boolean traversed){
        	_traversed = traversed; 
        }
        //we set the traversed level so we only walk from higher levels to lower levels...
        public int getTraversedlevel(){
        	return _traverselevel; 
        }
        public void incTraversedlevel(){
        	_traverselevel++;
        }
        //return the icon 
        public Drawable getGraphic() {
            return _icon;
        }
 	//return xy coords
        public Coordinates getCoordinates() {
            return _coordinates;
        }
    	//return app name   
        public String getName(){
        	return _name; 
        }
        //return the permissions as an array of strings 
        public String [] getPermissions(){
        	return _permissions; 
        }
        //draws the application at its position on the canvas and then draws the name and outdegrees to other apps 
        public void DrawApp(Canvas canvas){
        	Paint paint = new Paint();
        	paint.setColor(Color.WHITE);
        	paint.setStyle(Style.FILL);
        
        	//save current settings to canvas
        	
        	paint.setColor(Color.YELLOW);
        	paint.setAlpha(100);
        	//draw outdegrees 
        	for (int i = 0; i < Outlinks.nodepointer.size(); i++){
        		if (_appicons.get(Outlinks.nodepointer.get(i)).isTraversed() == true){
	        		int coordx = _appicons.get(Outlinks.nodepointer.get(i)).getCoordinates().getX();
	        		int coordy = _appicons.get(Outlinks.nodepointer.get(i)).getCoordinates().getY();
	        		canvas.drawLine(_coordinates.getX()+20, _coordinates.getY()+20, coordx+20, coordy+20, paint);
        		}
        	}
        	paint.setColor(Color.WHITE);
        	paint.setAlpha(255);
        	//we push a relative position onto the stack so we can move there do relative draws and then pop to go back to the origional position
        	canvas.save();
        	canvas.translate(_coordinates.getX(), _coordinates.getY());
        	_icon.draw(canvas);
        	canvas.drawText(_name, 0, _icon.getIntrinsicHeight() + 10, paint);
        	canvas.restore(); //restore previous canvas coordinates to canvas
        	
        	
        }
    }


}
