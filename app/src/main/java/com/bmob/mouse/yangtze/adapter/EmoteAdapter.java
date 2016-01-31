package com.bmob.mouse.yangtze.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bmob.mouse.yangtze.R;
import com.bmob.mouse.yangtze.entity.FaceText;

import java.util.List;

public class EmoteAdapter extends BaseArrayListAdapter {
	private static String TAG="EmoteAdapter";
	public EmoteAdapter(Context context, List<FaceText> datas) {
		super(context, datas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_face_text, null);
			holder = new ViewHolder();
			holder.mIvImage = (ImageView) convertView
					.findViewById(R.id.v_face_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FaceText faceText = (FaceText) getItem(position);
		String key = faceText.text.substring(1);
		//Log.i(TAG,"emoji的名-->"+key);
		//Log.i(TAG,"包名--->"+mContext.getPackageName()+"");
		int drawableId = mContext.getResources().getIdentifier(key,"mipmap", mContext.getPackageName());
	//	Log.i(TAG, "drawableId-->" + drawableId + "");
		Drawable drawable = mContext.getResources().getDrawable(drawableId);
		//holder.mIvImage.setImageResource(drawableId);
		holder.mIvImage.setImageDrawable(drawable);
		return convertView;
	}

	class ViewHolder {
		ImageView mIvImage;
	}
}
