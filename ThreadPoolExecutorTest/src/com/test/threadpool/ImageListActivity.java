package com.test.threadpool;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.RadioGroup;

public class ImageListActivity extends ListActivity implements
		RadioGroup.OnCheckedChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.imagelist);

		final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioGroup.setOnCheckedChangeListener(this);

		setListAdapter(new ImageAdapter(ImageListActivity.this));

	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {

	}
}
