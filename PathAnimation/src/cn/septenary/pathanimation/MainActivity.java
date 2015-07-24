package cn.septenary.pathanimation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	private PathAnimationView mPathAnimationView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mPathAnimationView = (PathAnimationView) findViewById(R.id.pathView);
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn1:
				mPathAnimationView.anim1();
				break;
			case R.id.btn2:
				mPathAnimationView.anim2();
				break;
			case R.id.btn3:
				mPathAnimationView.anim3();
				break;
			case R.id.btn4:
				mPathAnimationView.anim4();
				break;
			case R.id.btn5:
				mPathAnimationView.anim5();
				break;
			case R.id.btn6:
				mPathAnimationView.anim6();
				break;
			case R.id.btn7:
				mPathAnimationView.anim7();
				break;
			case R.id.btn8:
				mPathAnimationView.anim8();
				break;

			default:
				break;
		}
	}

}
