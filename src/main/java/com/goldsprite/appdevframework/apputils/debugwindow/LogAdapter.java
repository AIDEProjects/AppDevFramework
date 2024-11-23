package com.goldsprite.appdevframework.apputils.debugwindow;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import com.goldsprite.appdevframework.apputils.debugwindow.DebugWindow.LogEntry;

import com.goldsprite.appdevframework.R;
import android.view.*;


public class LogAdapter extends ArrayAdapter<LogEntry> {
	private final Context ctx;
	private final List<LogEntry> entries;

	public LogAdapter(Context ctx, int resource, List<LogEntry> entries){
		super(ctx, resource, entries);
		this.ctx = ctx;
		this.entries = entries;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			// 使用布局填充器将布局文件转换为视图
			convertView = LayoutInflater.from(ctx).inflate(R.layout.list_item_log, parent, false);
			holder = new ViewHolder();
			holder.logTextView = convertView.findViewById(R.id.logTextView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		LogEntry logEntry = entries.get(position);
		holder.logTextView.setText(logEntry.msg);

		// 根据日志级别设置文本颜色
		switch (logEntry.level) {
			case "e":
				holder.logTextView.setTextColor(Color.RED); // 错误级别 - 红色
				break;
			case "i":
				holder.logTextView.setTextColor(Color.GREEN); // 信息级别 - 绿色
				break;
			default:
				holder.logTextView.setTextColor(Color.WHITE); // 默认白色
				break;
		}

		return convertView;
	}

	// ViewHolder 用于提升性能
	private static class ViewHolder {
		TextView logTextView;
	}

}
