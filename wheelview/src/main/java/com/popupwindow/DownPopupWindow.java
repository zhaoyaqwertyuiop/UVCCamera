package com.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.wheelview.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 从底部弹出popWin
 */
public class DownPopupWindow extends PopupWindow {

	private View mContentView;
	private View mDownView; // 下方弹出的view
	private Activity context;

	private boolean isDismissing = false; // 标记是否正在执行关闭动画,防止重复执行

	private float alphaRatio = 0.75f; // 透明度比例 (1.0f 到 0.0f 之间, 值越大,弹出时背景透明度越大)

	public DownPopupWindow(Activity context) {
		super(context);
		this.context = context;
	}

	public void initView(IInitView callBack){
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);

		ColorDrawable dw = new ColorDrawable(0x000000);
		this.setBackgroundDrawable(dw);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContentView = inflater.inflate(R.layout.dpw_pop_view_popwindow_down, null, true); // 内部调用，外部实现
		mDownView = callBack.initDownView(inflater);
		FrameLayout frameLayout = (FrameLayout) mContentView.findViewById(R.id.pop_layout);
		frameLayout.addView(mDownView);

		this.setContentView(mContentView);

		//mContentView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mContentView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = mDownView.getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	}

	public interface IInitView {
		/**
		 * 配置下方弹出的view
         * @return
         */
		View initDownView(LayoutInflater inflater);
	}

	@Override
	public void showAtLocation(final View parent, final int gravity, final int x, final int y) {
//		if (!canShow) {
//			return;
//		}
		super.showAtLocation(parent, gravity, x, y);
		final AnimationSet set = new AnimationSet(true);
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.set_in_down);
		set.addAnimation(animation);
		set.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(final Animation animation) {
				if (timer != null) { // 开始动画还没完
					timer.cancel();
					timer = null;
				}
				timer = new Timer();
				final long period = 1;
				final TimerTask timerTask = new TimerTask() {
					long time = animation.getDuration();
					long currentTime = 0;
					@Override
					public void run() {

						handler.post(new Runnable() {
							@Override
							public void run() {
								currentTime += period;
								if (currentTime >= time) {
									if(timer != null) {
										timer.cancel();
										timer = null;
									}
								} else {
									float alpha = 1.0f - currentTime * alphaRatio / time; // 区间从1.0f到0.3f
//								Log.d("TAG", "alpha=" + alpha + ",time=" + time + ",currentTime=" + currentTime);
									if (backgroundListener == null) {
										backgroundAlpha(alpha);
									} else {
										backgroundListener.backgroundChange(alpha);
									}
								}
							}
						});
					}
				};
				timer.schedule(timerTask, 0, 1);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		mDownView.startAnimation(set);
	}

	private Timer timer;

	static Handler handler = new Handler(Looper.getMainLooper());

	@Override
	public void dismiss() {
		if (isDismissing) {
			return;
		}
		isDismissing = true;
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.set_out_down);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(final Animation animation) {
				if (timer != null) { // 开始动画还没完
					timer.cancel();
					timer = null;
				}
				timer = new Timer();
				final long period = 1;
				final TimerTask timerTask = new TimerTask() {
					long time = animation.getDuration();
					long currentTime = 0;
					WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
					float startAlpha = lp.alpha;
					@Override
					public void run() {

						handler.post(new Runnable() {
							@Override
							public void run() {
								currentTime += period;
								if (currentTime >= time) {
									if (timer != null) {
										timer.cancel();
										timer = null;
									}
								} else {
									float alpha = startAlpha + currentTime * alphaRatio / time; // 区间从1.0f到0.3f
//								Log.d("TAG", "alpha=" + alpha + ",time=" + time + ",currentTime=" + currentTime);
									if (alpha > 1) {
										alpha = 1;
									}
									if (backgroundListener == null) {
										backgroundAlpha(alpha);
									} else {
										backgroundListener.backgroundChange(alpha);
									}
								}
							}
						});
					}
				};
				timer.schedule(timerTask, 0, 1);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mDownView.post(new Runnable() {
					@Override
					public void run() {
						DownPopupWindow.super.dismiss();
						if (onDismissListener != null) {
							onDismissListener.onDismiss();
						}
					}
				});

				isDismissing = false;
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		mDownView.startAnimation(animation);
	}

	private OnDismissListener onDismissListener;
	@Override
	public void setOnDismissListener(OnDismissListener onDismissListener) {
		this.onDismissListener = onDismissListener;
	}

	// 设置屏幕透明度
	public void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = context.getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0~1.0
		context.getWindow().setAttributes(lp);
	}

	/** 从屏幕下方弹出 */
	public void showPop() {
		View view = context.getWindow().getDecorView();
		if (view != null) {
			this.showAtLocation(view , Gravity.BOTTOM, 0, 0);
		}
	}

	// 背景变化监听
	private BackgroundListener backgroundListener = null;

	public void setBackgroundListener(BackgroundListener backgroundListener) {
		this.backgroundListener = backgroundListener;
	}

	public interface BackgroundListener{
		public void backgroundChange(float bgAlpha);
	}
}
