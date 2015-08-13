package com.lh.exin.activity;
import android.support.v7.app.*;
import android.os.*;
import com.lh.exin.toolbar.*;
import android.text.*;
import com.lh.exin.*;
import android.widget.*;

public class AtyCheckProblem extends AppCompatActivity
{
	private ToolbarControl toolbarControl;
	private ProgressBar progressBar;
	private TextView tvProblem;
	private Button btnBeginCheck;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check);
		toolbarControl=new ToolbarControl(this);
		toolbarControl.initToolbarNoDrawer("故障诊断");
		progressBar=(ProgressBar) findViewById(R.id.check_progress);
		tvProblem=(TextView) findViewById(R.id.tv_check_problem);
		btnBeginCheck=(Button) findViewById(R.id.btn_begin_check);
	}
}
