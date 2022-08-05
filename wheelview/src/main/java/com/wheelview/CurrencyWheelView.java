package com.wheelview;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wheelview.wheel.AbstractWheelTextAdapter;
import com.wheelview.wheel.OnWheelChangedListener;
import com.wheelview.wheel.WheelView;

import java.util.ArrayList;
import java.util.List;

/** 通用三级联动view */
public class CurrencyWheelView extends LinearLayout {

	private Context context;

	private WheelView oneWheel; // 省
	private WheelView twoWheel; // 市
	private WheelView threeWheel; // 县
	private TextView oneWheelTV, twoWheelTV, threeWheelTV; // 三级对应头部textview

	private List<IData> oneList; // 省级列表数据
	private List<IData> twoList; // 市级列表数据
	private List<IData> threeList; // 县级列表数据

	private TextView okBtn;
	private IData data; // 用來保存选择的省市县数据
	private Object oneWheelId, twoWheelId, threeWheelId;
	private String oneWheelName, twoWheelName, threeWheelName;
	private int onePosition, twoPosition, threePosotion; // 记录三级列表选择的位置,以便下次打开设置这个位置

	private int type = 3; // 显示等级，默认显示最高级，即全部显示

	public CurrencyWheelView(Context context, IInitDataCallBack callBack) {
		super(context);
		initView(context, type, callBack);
	}

	/**
	 *
	 * @param context
	 * @param type // 显示等级，1 表示只显示一级, 2 表示显示2级, 3 表示显示3级
	 * @param callBack // 配置数据
     */
	public CurrencyWheelView(Context context, int type, IInitDataCallBack callBack) {
		super(context);
		initView(context, type, callBack);
	}

	private void initView(Context context, int type, final IInitDataCallBack callBack) {
		this.context = context;
		this.type = type;

		final View contentView = View.inflate(context, R.layout.view_wheel, this);
		oneWheel = (WheelView) contentView.findViewById(R.id.wheelcity_province); // 省
		twoWheel = (WheelView) contentView.findViewById(R.id.wheelcity_city); // 市
		threeWheel = (WheelView) contentView.findViewById(R.id.wheelcity_county); // 县
		oneWheelTV = (TextView) contentView.findViewById(R.id.oneWheelTV); // 县
		twoWheelTV = (TextView) contentView.findViewById(R.id.twoWheelTV); // 县
		threeWheelTV = (TextView) contentView.findViewById(R.id.threeWheelTV); // 县
		okBtn = (TextView) contentView.findViewById(R.id.okBtn);

		switch (type) {
		case 1:
			contentView.findViewById(R.id.twoWheelLL).setVisibility(View.GONE);
			contentView.findViewById(R.id.threeWheelLL).setVisibility(View.GONE);
			break;
		case 2:
			contentView.findViewById(R.id.threeWheelLL).setVisibility(View.GONE);
			break;
		case 3:
			break;
		default:
			break;
		}

		data = new IData() {
			@Override
			public String getOneWheelName() {
				return oneWheelName;
			}

			@Override
			public String getTwoWheelName() {
				return twoWheelName;
			}

			@Override
			public String getThreeWheelName() {
				return threeWheelName;
			}

			@Override
			public Object getOneWheelId() {
				return oneWheelId;
			}

			@Override
			public Object getTwoWheelId() {
				return twoWheelId;
			}

			@Override
			public Object getThreeWheelId() {
				return threeWheelId;
			}
		};
		oneList = callBack.initOneList();

		oneWheel.addChangingListener(new OnWheelChangedListener() {

			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				oneWheelId = oneList.get(newValue).getOneWheelId();
				oneWheelName = oneList.get(newValue).getOneWheelName();
				onePosition = newValue;
				if (CurrencyWheelView.this.type >= 2) {
					twoList = callBack.initTwoList(oneList.get(newValue), newValue);
					if (twoList.size() == 0) {
						twoList.add(new IData() {
							@Override
							public String getOneWheelName() {
								return null;
							}

							@Override
							public String getTwoWheelName() {
								return "--";
							}

							@Override
							public String getThreeWheelName() {
								return null;
							}

							@Override
							public Object getOneWheelId() {
								return null;
							}

							@Override
							public Object getTwoWheelId() {
								return null;
							}

							@Override
							public Object getThreeWheelId() {
								return null;
							}
						});
					}
					updateCities(twoWheel, twoList, 2);
				}
			}
		});

		twoWheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				contentView.findViewById(R.id.twoWheelLL).setVisibility(View.VISIBLE);
				twoWheelId = twoList.get(newValue).getTwoWheelId();
				twoWheelName = twoList.get(newValue).getTwoWheelName();
				twoPosition = newValue;
				if (CurrencyWheelView.this.type >= 3) {
					if (twoWheelId == null) {
						// 表示没有第2级数据
						threeList = new ArrayList<IData>();
					} else {
						threeList = callBack.initThreeList(twoList.get(newValue), newValue);
					}
					if (threeList.size() == 0) {
						threeList.add(new IData() {
							@Override
							public String getOneWheelName() {
								return null;
							}

							@Override
							public String getTwoWheelName() {
								return null;
							}

							@Override
							public String getThreeWheelName() {
								return "--";
							}

							@Override
							public Object getOneWheelId() {
								return null;
							}

							@Override
							public Object getTwoWheelId() {
								return null;
							}

							@Override
							public Object getThreeWheelId() {
								return null;
							}
						});
					}
					updateCities(threeWheel, threeList, 3);
				}

			}
		});

		threeWheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				contentView.findViewById(R.id.threeWheelLL).setVisibility(View.VISIBLE);
				threeWheelId = threeList.get(newValue).getThreeWheelId();
				threeWheelName = threeList.get(newValue).getThreeWheelName();
				threePosotion = newValue;
			}
		});

		// 设置滑轮的可见项
		oneWheel.setVisibleItems(5);
		twoWheel.setVisibleItems(5);
		threeWheel.setVisibleItems(5);

		// 设置默认选项
		oneWheel.setCurrentItem(onePosition);
		twoWheel.setCurrentItem(twoPosition);
		threeWheel.setCurrentItem(threePosotion);

		updateCities(oneWheel, oneList, 1);
	}

	/** 配置数据的接口 */
	public interface IInitDataCallBack{
		/** 设置一级数据 */
		public List<IData> initOneList();

		/**
		 * 设置二级数据
		 * @param data initOneList().get(position)
		 * @param position 省级选择的项
         * @return
         */
		public List<IData> initTwoList(IData data, int position);

		/**
		 * 设置三级数据
		 * @param data initTwoList().get(positon)
		 * @param position 市级数据所选项
         * @return
         */
		public List<IData> initThreeList(IData data, int position);
	}

	/**
	 * Updates the city wheel
	 */
	private void updateCities(WheelView city, List<IData> list, int type) {
		ArrayWheelAdapter adapter = new ArrayWheelAdapter(context, list, type);
		adapter.setItemResource(R.layout.item_wheel); // 设置轮子的item
		adapter.setItemTextResource(R.id.wheelTV);
//		adapter.setTextSize(14);
		city.setViewAdapter(adapter);
		city.setCurrentItem(0);
	}

	/** 联动需要继承AbstractWheelTextAdapter */
	private class ArrayWheelAdapter extends AbstractWheelTextAdapter {
		private List<IData> list;
		int type;
		/**
		 * Constructor
		 * @param context the current context
		 * @param list 数据
		 */
		public ArrayWheelAdapter(Context context, List<IData> list ,int type) {
			super(context);
			this.list=list;
			this.type=type;
		}

		@Override
		public CharSequence getItemText(int index) {
			CharSequence result="";
			if(type == 1){
				result=list.get(index).getOneWheelName();
			}
			else if(type == 2){
				result=list.get(index).getTwoWheelName();
			}
			else if(type == 3){
				result=list.get(index).getThreeWheelName();
			}
			return result;
		}

		@Override
		public int getItemsCount() {
			return list.size();
		}
	}

	/** 联动数据继承IData */
	public interface IData{
		/** 获取一及数据 */
		String getOneWheelName();
		/** 获取二级数据 */
		String getTwoWheelName();
		/** 获取三级数据 */
		String getThreeWheelName();

		Object getOneWheelId();
		Object getTwoWheelId();
		Object getThreeWheelId();
	}

	/** 获取所选择的AddressInfo数据 */
	public IData getData() {
		return data;
	}

	public TextView getOkBtn() {
		return okBtn;
	}

	/** 一级选项title */
	public TextView getOneWheelTV() {
		return oneWheelTV;
	}

	/** 二级选项title */
	public TextView getTwoWheelTV() {
		return twoWheelTV;
	}

	/** 三级选项title */
	public TextView getThreeWheelTV() {
		return threeWheelTV;
	}

	/** 设置滑轮的默认选项 */
	public void setOneWheelItem(int position) {
		this.oneWheel.setCurrentItem(position);
		onePosition = position;
	}
	/** 设置滑轮的默认选项 */
	public void setTwoWheelItem(int position) {
		this.twoWheel.setCurrentItem(position);
		twoPosition = position;
	}
	/** 设置滑轮的默认选项 */
	public void setThreeWheelItem(int position) {
		this.threeWheel.setCurrentItem(position);
		threePosotion = position;
	}

	public int getOnePosition() {
		return onePosition;
	}

	public int getTwoPosition() {
		return twoPosition;
	}

	public int getThreePosotion() {
		return threePosotion;
	}
}
