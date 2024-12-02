package com.goldsprite.appdevframework.apputils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.LinkedHashMap;
import java.util.Map;
import android.R;
import com.goldsprite.appdevframework.log.*;

public class SampleListManager
{
    private final Context context;
    private final ListView listView;

    public SampleListManager(Context context, ListView listView) {
        this.context = context;
        this.listView = listView;
    }

    public void initSampleList(Object... samples) {
        if (samples.length % 2 != 0) {
            throw new IllegalArgumentException("Arguments must be in pairs of String and Class.");
        }

        // 构建 Map
        final Map<String, Class<? extends Activity>> sampleMap = new LinkedHashMap<>();
        for (int i = 0; i < samples.length; i += 2) {
            String name = (String) samples[i];
			Class<? extends Activity> clazz = (Class<? extends Activity>) samples[i + 1];
            sampleMap.put(name, clazz);
        }

        // 提取选项
        String[] options = sampleMap.keySet().toArray(new String[0]);

        // 设置适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
			context, 
			android.R.layout.simple_list_item_1, 
			options);
        listView.setAdapter(adapter);

		// 设置点击事件
		var itemListener = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try{
					int i = 0;
					for (Class<? extends Activity> activityClazz : sampleMap.values()) {
						if (i++ == position) {
							context.startActivity(new Intent(context, activityClazz));
							break;
						}
					}
				}catch(Throwable e){
					AppLog.dialogE("SampleListItemOnClick异常", e);
				}
			}
		};
		listView.setOnItemClickListener(itemListener);
		
    }
}

