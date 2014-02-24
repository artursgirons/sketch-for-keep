package lv.piip.sketchforkeep;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class PrerequisiteActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_prerequisite);
	    appsInstalledCheck();
	}
	
	public Boolean appsInstalledCheck(){
		android.content.pm.PackageManager mPm = getPackageManager();
		
		try
		{
			PackageInfo info_keep = mPm.getPackageInfo("com.google.android.keep", 0);
			findViewById(R.id.buttonKeep).setEnabled(!(info_keep!=null));
		}
		catch(android.content.pm.PackageManager.NameNotFoundException ex) {
			return false;
		}
		
		try
		{
			PackageInfo info_handrite = mPm.getPackageInfo("com.handrite.sketch", 0);
			findViewById(R.id.buttonHandrite).setEnabled(!(info_handrite!=null));
		}
		catch(android.content.pm.PackageManager.NameNotFoundException ex) {
			return false;
		}
		
		return true;
	}
	
	
	public void installKeep(View view) {
		install("com.google.android.keep");
	}	

	public void installHandrite(View view) {
		install("com.handrite.sketch");
	}
	
	public void install(String appName){
		try {
		    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appName)));
		} catch (android.content.ActivityNotFoundException anfe) {
		    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+appName)));
		}
	}
	
	@Override
    public void onBackPressed() {
		setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
        this.finish();		
    }

	@Override
    public void onResume() {
        super.onResume();
		if(appsInstalledCheck()){
			setResult(Activity.RESULT_OK);
	        this.finish();		
		}
	}
}
