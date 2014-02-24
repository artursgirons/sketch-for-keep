package lv.piip.sketchforkeep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;

public class MainActivity extends Activity {

	private String keepPath = "/Android/data/lv.piip.sketchforkeep/Sketch";
	private Uri fileUri = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Boolean appsExist = false; 
		
		try
		{
			android.content.pm.PackageManager mPm = getPackageManager();
			
			PackageInfo info_handrite = mPm.getPackageInfo("com.handrite.sketch", 0);
			PackageInfo info_keep = mPm.getPackageInfo("com.google.android.keep", 0);
			
			appsExist = (info_handrite !=null && info_keep!=null);
		}
		catch(android.content.pm.PackageManager.NameNotFoundException ex) {
		    Intent prerequisiteIntent = new Intent(this, PrerequisiteActivity.class);
		    startActivityForResult(prerequisiteIntent,1);
		    
		    prerequisiteIntent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
		}
		
	    if(appsExist)
	    {
	    	startSketch();
	    }
	}
	
	public void startSketch(){
        Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
        intent.setClassName("com.handrite.sketch", "com.handrite.sketch.activity.SketchActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        
        File path = new File(Environment.getExternalStorageDirectory()+keepPath);
        if (!path.exists()) {
        	path.mkdir();
        	
        	File nom = new File(path, ".nomedia");
        	if(!nom.exists())
        	{
        		try {
					nom.createNewFile();
				} catch (IOException e) {
				}
        	}
        }
        
        File file = new File(path,  "sketch.temp");
        
        Uri imageUri = Uri.fromFile(file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        
        startActivityForResult(intent, 2);
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == Activity.RESULT_OK)
        {
	        switch (requestCode) {
	        	case 1:
	        	{
	        		startSketch();
	        	}
	        	break;
		        case 2:
		        {
		        	ArrayList<Uri> images = new ArrayList<Uri>();
		        	fileUri = data.getData();
		        	images.add(fileUri);
		        	
	                Intent intent= new Intent();
	                intent.setClassName("com.google.android.keep", "com.google.android.keep.activities.EditorActivity");

	                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
	                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, images);
	                intent.setType("image/*");
	    	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	                startActivityForResult(intent, 0);
	            }
	            break;
		        default:
		        {
		        	cleanup();
		        	finish();
		        }
	        }
        }
        else
        {
        	cleanup();
        	finish();
        }
    }
    
    public void cleanup(){
        File path = new File(Environment.getExternalStorageDirectory()+keepPath);
        deleteRecursive(path);

        try
        {
	        if(fileUri!=null)
	        {
	        	getContentResolver().delete(fileUri, null, null);
	        }
        }
        catch(Exception ex)
        {
        }
    }
    
    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
        {
            for (File child : fileOrDirectory.listFiles())
            {
                deleteRecursive(child);
            }
        }
        else
        {
        	if(!fileOrDirectory.getName().equals(".nomedia"))
        	{
        		fileOrDirectory.delete();
        	}
        }
    }    
}
