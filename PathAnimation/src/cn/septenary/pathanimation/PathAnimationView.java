package cn.septenary.pathanimation;

import android.animation.ObjectAnimator;
import android.animation.TypeConverter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

public class PathAnimationView extends FrameLayout {

	private static final Path sFixedPath;

	private static float TRAVERSE_PATH_SIZE = 7.0f;

	private Path mPath = new Path();

	private Paint mPaint = new Paint();

	Matrix mPathMatrix = new Matrix();

	private View mAnchor;

	static {
		sFixedPath = new Path();
		sFixedPath.moveTo(3.5f, 3.5f);
		sFixedPath.arcTo(new RectF(0, 0, 7f, 7f), -90 - 45, 270 + 45);
		sFixedPath.lineTo(3.5f, 3.5f);
	}

	public PathAnimationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setWillNotDraw(false);
		mPaint.setStrokeWidth(2);
		mPaint.setAntiAlias(true);
		mPaint.setColor(0xffff4444);
		mPaint.setStyle(Paint.Style.STROKE);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			mAnchor = findViewById(R.id.anchor);
			int w = getWidth() - mAnchor.getWidth();
			int h = getHeight() - mAnchor.getHeight();
			float scaleWidth = w / TRAVERSE_PATH_SIZE;
			float scaleHeight = h / TRAVERSE_PATH_SIZE;
			mPathMatrix.reset();
			mPathMatrix.setScale(scaleWidth, scaleHeight);
			mPathMatrix.postTranslate(mAnchor.getWidth() / 2, mAnchor.getHeight() / 2);
			sFixedPath.transform(mPathMatrix, mPath);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		canvas.drawPath(mPath, mPaint);
	}

	private void start(ValueAnimator animator) {
		animator.setDuration(2000);
		animator.setRepeatCount(ObjectAnimator.INFINITE);
		animator.setRepeatMode(ObjectAnimator.RESTART);
		animator.start();
	}

	// ///////////////////////////////by ofFloat//////////////////////////////////////////////

	public void anim1() {
		ObjectAnimator animator = ObjectAnimator.ofFloat(this, "anchorX", "anchorY", mPath);
		start(animator);
	}

	public void setAnchorX(float x) {
		mAnchor.setX(x - mAnchor.getWidth() / 2);
	}

	public void setAnchorY(float y) {
		mAnchor.setY(y - mAnchor.getHeight() / 2);
	}

	// ///////////////////////////////by ofFloat////////////////////////////////////////////////

	public void anim2() {
		ObjectAnimator animator = ObjectAnimator.ofFloat(mAnchor, View.X, View.Y, mPath);
		start(animator);
	}

	// ///////////////////////////////by ofMultiInt///////////////////////////////////////////

	public void anim3() {
		ObjectAnimator animator = ObjectAnimator.ofMultiInt(this, "setCoordinates", mPath);
		start(animator);
	}

	public void setCoordinates(int x, int y) {
		mAnchor.setX(x - mAnchor.getWidth() / 2);
		mAnchor.setY(y - mAnchor.getHeight() / 2);
	}

	// ///////////////////////////////by ofMultiFloat//////////////////////////////////////////

	public void anim4() {
		ObjectAnimator animator = ObjectAnimator.ofMultiFloat(this, "setCoordinatesF", mPath);
		start(animator);
	}

	public void setCoordinatesF(float x, float y) {
		mAnchor.setX(x - mAnchor.getWidth() / 2);
		mAnchor.setY(y - mAnchor.getHeight() / 2);
	}

	// /////////////////////////////////by ofObject////////////////////////////////////////////
	public void anim5() {
		ObjectAnimator animator = ObjectAnimator.ofObject(this, "point", null, mPath);
		start(animator);
	}

	public void setPoint(PointF point) {
		mAnchor.setX(point.x - mAnchor.getWidth() / 2);
		mAnchor.setY(point.y - mAnchor.getHeight() / 2);
	}

	// //////////////////////////////////by ofObject/////////////////////////////////////////////
	public void anim6() {
		final Property<PathAnimationView, Point> pointProperty = new Property<PathAnimationView, Point>(Point.class, "point") {
			@Override
			public Point get(PathAnimationView object) {
				View v = mAnchor;
				return new Point(Math.round(v.getX()), Math.round(v.getY()));
			}

			@Override
			public void set(PathAnimationView object, Point value) {
				object.setCoordinates(value.x, value.y);
			}
		};
		ObjectAnimator animator = ObjectAnimator.ofObject(this, pointProperty, new PointFToPointConverter(), mPath);
		start(animator);
	}

	private static class PointFToPointConverter extends TypeConverter<PointF, Point> {
		Point mPoint = new Point();

		public PointFToPointConverter() {
			super(PointF.class, Point.class);
		}

		@Override
		public Point convert(PointF value) {
			mPoint.set(Math.round(value.x), Math.round(value.y));
			return mPoint;
		}
	}

	// //////////////////////////////by AnimatorUpdateListener/////////////////////////////////////////
	// Android 3.0 - 5.0使用 
	public void anim7() {
		final PathMeasure pathMeasure = new PathMeasure(mPath, false);
		final float[] position = new float[2];

		ValueAnimator aimator = ValueAnimator.ofFloat(0, 1f);
		aimator.setDuration(250);
		aimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float interpolatedTime = (Float) animation.getAnimatedValue();
				pathMeasure.getPosTan(pathMeasure.getLength() * interpolatedTime, position, null);
				setCoordinatesF(position[0], position[1]);
			}
		});
		start(aimator);
	}

	// //////////////////////////////by Animation//////////////////////////////////////////////
	// Android 3.0 之前 使用
	public void anim8() {
		final PathMeasure pathMeasure = new PathMeasure(mPath, false);
		final float[] position = new float[2];
		Animation anim = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				pathMeasure.getPosTan(pathMeasure.getLength() * interpolatedTime, position, null);
				setCoordinatesF(position[0], position[1]);
			}
		};
		anim.setDuration(2000);
		anim.setRepeatCount(Animation.INFINITE);
		anim.setRepeatMode(Animation.RESTART);
		startAnimation(anim);
	}

}
