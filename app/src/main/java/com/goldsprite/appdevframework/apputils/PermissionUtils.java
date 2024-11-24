package com.goldsprite.appdevframework.apputils;
import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.widget.*;
import com.goldsprite.appdevframework.*;
import com.goldsprite.appdevframework.log.*;
import java.io.*;
import java.util.*;

public class PermissionUtils{
	private Activity ctx;

	private String[] permissions;

	public static int PERMISSION_REQUEST_CODE = 100;

	private Runnable callback;

	private Runnable exitToast = new Runnable(){public void run(){AppLog.finishWithToast("未获得授权，程序将退出.");}};


	public PermissionUtils(Activity context, String[] permissions, int code, Runnable callback){
		this.ctx = context;
		this.permissions = permissions;
		PERMISSION_REQUEST_CODE = code;
		this.callback = callback;
	}


	//请求文件权限
	public void requestAllPermission(){
		List<String> retPerms = new ArrayList<>();
		if (!isStoragePermissionDeclared(ctx, retPerms)){
			AppLog.dialog("申请权限失败", "AndroidManifest清单未声明以下权限: \n" + String.join("\n", retPerms), exitToast, exitToast);
			return;
		}
		if (hasExternalStoragePermission()){
			if (callback != null) callback.run();
		}
		else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
			AppLog.dialog("申请权限", "应用需要此权限以维持日志系统运转，否则应用将无法正常调试.", 
				new Runnable(){
					public void run(){
						Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
						intent.setData(Uri.parse("package:" + ctx.getPackageName()));
						ctx.startActivityForResult(intent, PERMISSION_REQUEST_CODE);
					}
				}, 
				new Runnable(){public void run(){AppLog.finishWithToast("未获得授权，程序将退出.");}}
			);
		}
		else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			ctx.requestPermissions(permissions, PERMISSION_REQUEST_CODE);
		}
	}

	//请求权限回调结果
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
		//如果请求代码不为此应用
		if (requestCode != PERMISSION_REQUEST_CODE) return;

		//返回结果数据长度>0且第一个为同意
		if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
			AppLog.toast("存读权限申请通过");
			if (callback != null) callback.run();
			return;
		}

		//否则继续处理失败后
		//sdk23及以上
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			//如果被拒绝(此方法首次申请以及普通被拒返回true，只有拒绝且不再询问时返回false)
			if (ctx.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){

				AppLog.dialog("权限申请失败", "应用需要此权限以维持日志系统运转，否则应用将无法正常调试.", 
					new Runnable(){public void run(){requestAllPermission();}}, 
					new Runnable(){public void run(){AppLog.finishWithToast("未获得授权，程序将退出.");}}
				);
				//用户拒绝且不再询问
			}
			else{
				//dialog弹窗引导
				permissionDialog();
			}
		}
	}

	//当从意图返回时
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		//如果为权限申请
		if (requestCode == PERMISSION_REQUEST_CODE){
			//还未授权
			if (!hasExternalStoragePermission()){
				AppLog.finishWithToast("未获得授权，程序将退出.");
			}
			else{
				AppLog.toast("已成功获得授权.");
				if (callback != null) callback.run();
			}
		}
	}

	public void permissionDialog(){
		try{
			AlertDialog.Builder b = new AlertDialog.Builder(ctx);
			//设置不可取消
			b.setCancelable(false);
			b.setTitle("权限申请已被禁止，你需要手动设置");
			//设置引导内容布局
			{
				//声明布局
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 
					LinearLayout.LayoutParams.WRAP_CONTENT
				);
				LinearLayout ll = new LinearLayout(ctx);
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.setLayoutParams(lp);

				//定义引导图片路径
				String pngsDir = Res.path_user_helper;
				String[] pngsPath = ctx.getAssets().list(pngsDir);
				int i=1;
				//遍历添加引导文字与图片步骤
				for (String pngPath : pngsPath){
					TextView tv = new TextView(ctx);
					tv.setText(String.format("第%d步", i++));
					tv.setTextColor(Color.BLACK);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
					tv.setLayoutParams(lp);
					ll.addView(tv);

					InputStream is = ctx.getAssets().open(pngsDir + pngPath);
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					ImageView image = new ImageView(ctx);
					image.setImageBitmap(bitmap);
					image.setLayoutParams(lp);
					ll.addView(image);

					is.close();//释放流资源
				}
				ScrollView scroll = new ScrollView(ctx);
				scroll.addView(ll);
				b.setView(scroll);
			}
			b.setPositiveButton("确定", 
				new AlertDialog.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						// 跳转应用权限设置
						Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
						Uri uri = Uri.fromParts("package", ctx.getPackageName(), null);
						intent.setData(uri);
						ctx.startActivityForResult(intent, PERMISSION_REQUEST_CODE);
					}
				}
			);
			b.setNegativeButton("取消", 
				new AlertDialog.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						AppLog.finishWithToast("未获得授权，程序将退出.");
					}
				}
			);
			b.create().show();
		}catch (Exception e){
			AppLog.dialog("权限申请dialog代码异常: ", e.getMessage());
		}
	}

	public boolean hasExternalStoragePermission(){
		boolean ret = true;
		try{
			String path = "/sdcard/" + System.currentTimeMillis() + "_" + (int)(new Random().nextDouble() * 10000) + ".txt";
			File file = new File(path);
			file.createNewFile();
			file.delete();
		}catch (Exception e){
			ret = false;
		}
		return ret;
	}

	public boolean isStoragePermissionDeclared(Context context, List<String> retPerms){
		String errMsg = "";
		errMsg += ("检查权限清单声明");
		errMsg += "\n";
		retPerms.addAll(Arrays.asList(permissions));
		errMsg += String.format("permissions共计%s个权限, 加入retPerms待检查列表: %s", permissions.length, retPerms.size());
		errMsg += "\n";
		try{
			// 获取应用的 PackageInfo
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
			// 遍历所有已声明的权限
			errMsg += ("开始权限对比");
			errMsg += "\n";
			for (String permission : packageInfo.requestedPermissions){
				for (String i : permissions){
					if (permission.equals(i)){
						retPerms.remove(i);
						errMsg += String.format("权限匹配，retPerms:%s已检查，%s/%s", i, permissions.length - retPerms.size(), permissions.length);
						errMsg += "\n";
					}
					else{

						errMsg += ("权限不匹配，略过");
						errMsg += "\n";
					}
				}
			}
		}catch (PackageManager.NameNotFoundException e){
			e.printStackTrace();
		}
		errMsg += String.format("权限清单比对完成，权限是否皆已声明: %s", retPerms.size() == 0);
		errMsg += "\n";
		//AppLog.dialog("检查权限清单声明", errMsg);
		return retPerms.size() == 0;
	}


}
