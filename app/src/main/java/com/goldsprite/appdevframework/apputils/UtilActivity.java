package com.goldsprite.appdevframework.apputils;
import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.view.*;
import com.goldsprite.appdevframework.*;
import com.goldsprite.appdevframework.log.*;
import java.io.*;
import java.util.*;
import com.goldsprite.appdevframework.io.*;


public abstract class UtilActivity extends Activity{
	private static UtilActivity ctx;
	public static UtilActivity Instance(){ return ctx; }
	private PermissionUtils perm;

	public final CFG cfg = new CFG();
	public class CFG{
		public boolean noTitle;
		public boolean requestExternalStoragePermission;
	}

	protected void initOptions(){
		cfg.noTitle = false;
		cfg.requestExternalStoragePermission = false;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
		try{
			initActivity();
		}catch (Exception e){
			AppLog.dialogE("启动主活动代码异常", e);
		}
    }
	//初始化主活动
	private void initActivity(){
		ctx = this;
		AppLog.setCtx(ctx);
		AppUtils.setCtx(ctx);
		Project.projName = getAppName(this);

		createOptions();

		initOptions();

		if (cfg.noTitle) requestWindowFeature(Window.FEATURE_NO_TITLE);

		Logcat.printAndClearCrashLogcat();

		if (cfg.requestExternalStoragePermission){
			String[] permissions = {
				Manifest.permission.WRITE_EXTERNAL_STORAGE, 
				Manifest.permission.MANAGE_EXTERNAL_STORAGE
			};
			int PERMISSION_REQUEST_CODE = 100;
			Runnable callback = new Runnable(){
				public void run(){
					//如果设置了hasCrash
					if (crashCheck()){
						AppLog.dialog("启用安全模式.", "");
						return;
					}
					onCreate0();
					Log.log("Activity启动完成.");
				}
			};
			PermissionUtils perm = new PermissionUtils(this, permissions, PERMISSION_REQUEST_CODE, callback);
			requestPerm(perm);
		}
	}
	
	protected abstract void onCreate0();

	protected void requestPerm(PermissionUtils perm){
		this.perm = perm;
		perm.requestAllPermission();
	}


	public void createOptions(){
		if(FilesTool.exists(Project.OptionsPath())) return;
		
		String optionsStr = ""
			+ "hasCrash = false"
			+ "";
		FilesTool.writeString(Project.OptionsPath(), optionsStr, true, false);
	}

	public boolean crashCheck(){
		File f = new File(Project.OptionsPath());
		if (f.exists()){
			try{
				List<String> lines = new ArrayList<>();
				String line="";
				BufferedReader br = new BufferedReader(new FileReader(f));
				while ((line = br.readLine()) != null){
					lines.add(line);
				}
				br.close();
				for (String i : lines){
					String hasCrash = i.trim();
					if (hasCrash.matches("hasCrash\\s*=\\s*true")) {
						return true;
					}
				}
			}catch (Throwable e){
				e.printStackTrace();
			}
		}

		return false;
	}


	//请求权限回调结果
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (perm != null)
			perm.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	//当从意图返回时
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);

		if (perm != null)
			perm.onActivityResult(requestCode, resultCode, data);
	}


	private String getAppName(Context context){
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try{
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            return packageManager.getApplicationLabel(applicationInfo).toString();
        }catch (PackageManager.NameNotFoundException e){
			AppLog.dialogE("获取appName失败", e);
            return null;
        }
    }

}
