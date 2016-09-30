package com.diamondo.newsletterwriter.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.diamondo.newsletterwriter.R;
import com.diamondo.newsletterwriter.model.Item;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ItemListAdapter extends ArrayAdapter<Item> {

	private static final int XML_RESOURCE = R.layout.itemlistview;
	private static final int MENU_OPEN_DISTANCE = 900;
	private static final int MIN_DISTANCE_FOR_ACTION = 250;
	private static final int MIN_DISTANCE_FOR_SWIPING = 30;
	
	private Map<Item, Integer> itemLayoutIndices = new HashMap<Item, Integer>();
	private boolean isSwiping;
	private ListView listView;
	private ItemEventsInferface itemEventsInterface;
	
	public interface ItemEventsInferface {
		boolean onItemSwipeLeft(Item item);

		boolean onItemMarkedPersonal(Item item);

		boolean onItemMarkedWork(Item item);
		
		boolean onItemClicked(Item item);
	}

	public ItemListAdapter(Context context, List<Item> items, ListView listView) {
		super(context, XML_RESOURCE, items);
		this.listView = listView;
		this.itemEventsInterface = (ItemEventsInferface) context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(getItem(position) == null) {
			return null;
		} else {
			ListItemViewBundle itemView = new ListItemViewBundle(convertView, position);
			return itemView.wholeInflatedView;
		}
	}
	
	@Override
	public void remove(Item item){
		itemLayoutIndices.remove(item.id());
		super.remove(item);
		this.notifyDataSetChanged();
		Toast.makeText(getContext(), item.id() + "removed", Toast.LENGTH_SHORT).show();
		
	}

	private Integer getLayoutIndex(Item item) {
		if(itemLayoutIndices.containsKey(item)){
			return itemLayoutIndices.get(item);
		} else {
			return 1;
		}
	}
	
	private void setLayoutIndex(Item item, int index){
		itemLayoutIndices.put(item, index);
	}

	private static void setMargins(View view, int margin) {
		RelativeLayout.LayoutParams params;
		params = (RelativeLayout.LayoutParams) view.getLayoutParams();
		params.leftMargin = margin;
		params.rightMargin = -margin;
		view.setLayoutParams(params);
	}

	private static int getMargin(View view) {
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
		return params.leftMargin;
	}

	private void itemSwipedLeft(int position) {
		if (itemEventsInterface.onItemSwipeLeft(getItem(position)))
			remove(getItem(position));
	}

	private void itemMarkedPersonal(int position) {
		if (itemEventsInterface.onItemMarkedPersonal(getItem(position)))
			remove(getItem(position));
	}

	private void itemClicked(int position){
		if (itemEventsInterface.onItemClicked(getItem(position)))
			remove(getItem(position));	
	}
	
	private void itemMarkedWork(int position) {
		if (itemEventsInterface.onItemMarkedWork(getItem(position)))
			remove(getItem(position));
	}

	private class ListItemViewBundle implements View.OnClickListener, View.OnTouchListener {

		public View wholeInflatedView;
		public int listPosition;

		public RelativeLayout mainLayout;
		public LinearLayout contentLayout;
		public LinearLayout menuLayout;
		public TextView feedNameView;
		public TextView titleView;
		public TextView dateView;
		public TextView descriptionView;
		public TextView urlView;
		public Button workButton;
		public Button personalButton;
		public View rectangleView;
		
		private float downX, upX;
		private int mainLayoutOriginalX = 0, contentLayoutOriginalX = 0;

		public ListItemViewBundle(View view, int listPosition) {
			if (view == null) {
				LayoutInflater viewInflator;
				viewInflator = LayoutInflater.from(getContext());
				view = viewInflator.inflate(XML_RESOURCE, null);
			}
			this.listPosition = listPosition;
			this.wholeInflatedView = view;
			connectViews();
			initialiseLayout();
			initialiseViews();
			initialiseListeners();
		}

		private void connectViews() {
			feedNameView = (TextView) wholeInflatedView.findViewById(R.id.tvFeedName);
			titleView = (TextView) wholeInflatedView.findViewById(R.id.tvTitle);
			dateView = (TextView) wholeInflatedView.findViewById(R.id.tvDate);
			descriptionView = (TextView) wholeInflatedView.findViewById(R.id.tvDescription);
			urlView = (TextView) wholeInflatedView.findViewById(R.id.tvUrl);

			workButton = (Button) wholeInflatedView.findViewById(R.id.bWork);
			personalButton = (Button) wholeInflatedView.findViewById(R.id.bPersonal);

			rectangleView = (View) wholeInflatedView.findViewById(R.id.vRectangle);
			
			mainLayout = (RelativeLayout) wholeInflatedView.findViewById(R.id.rlMainLayout);
			contentLayout = (LinearLayout) wholeInflatedView.findViewById(R.id.llContent);
			menuLayout = (LinearLayout) wholeInflatedView.findViewById(R.id.llMenu);
			
		}

		private void initialiseViews() {
			Item item = getItem(listPosition);
			if (feedNameView != null) {
				int hashcode = item.feed.name.hashCode();
				String hexString = Integer.toHexString(hashcode) + "eeeeee";
				String hexStringSmall = hexString.substring(0, 6);
				String hexColour = "#" + hexStringSmall;
				rectangleView.setBackgroundColor(Color.parseColor(hexColour));
				menuLayout.setBackgroundColor(Color.parseColor(hexColour));
				feedNameView.setText(item.feed.name);
			}
			if (titleView != null) {
				titleView.setText(item.getTitle());
			}
			if (dateView != null) {
				dateView.setText(item.getDisplayDate());
			}
			if (descriptionView != null) {
				descriptionView.setText(item.getParagraph());
			}
			if (urlView != null) {
				urlView.setText(item.getUrl());
			}
		}

		private void initialiseListeners() {
			contentLayout.setOnTouchListener(this);
			contentLayout.setOnClickListener(this);
			workButton.setOnClickListener(this);
			personalButton.setOnClickListener(this);
		}

		private void initialiseLayout() {
			changeLayoutPosition(0);
		}

		private void changeLayoutPosition(int direction) {
			switch (getLayoutIndex(getItem(listPosition))+direction) {
				case 0:
					itemSwipedLeft(listPosition);
					break;
				case 1: 
					setDefaultLayout();
					break;
				case 2: 
					setOpenMenuLayout();
					break;
				case 3: 
					setOpenMenuLayout();
					break;
			}		
		}

		private void setDefaultLayout() {
			setAlpha(1);
			setMargins(mainLayout, 0);
			setMargins(rectangleView, 0);
			
			setMargins(contentLayout, 0);
//			wholeInflatedView.setHasTransientState(false);
			setLayoutIndex(getItem(listPosition), 1);
		}

		private void setOpenMenuLayout() {
			setAlpha(1);
			setMargins(mainLayout, 0);
			setMargins(rectangleView, 0);
			setMargins(contentLayout, MENU_OPEN_DISTANCE);
//			wholeInflatedView.setHasTransientState(true);
			setLayoutIndex(getItem(listPosition), 2);
		}

		private void setAlpha(float alpha) {
			feedNameView.setAlpha(alpha);
			titleView.setAlpha(alpha);
			dateView.setAlpha(alpha);
			descriptionView.setAlpha(alpha);
			urlView.setAlpha(alpha);
			mainLayout.setAlpha(alpha);
		}

		public void onClick(View clickedView) {
			switch (clickedView.getId()) {
			
			case R.id.llContent:
				itemClicked(listPosition);
			break;

			case R.id.bWork:
				itemMarkedWork(listPosition);
				break;

			case R.id.bPersonal:
				itemMarkedPersonal(listPosition);
				break;

			}

		}
		
		@Override	
		public boolean onTouch(View touchedView, MotionEvent event) {
			switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN: {
				downX = event.getRawX();
				mainLayoutOriginalX = getMargin(mainLayout);
				contentLayoutOriginalX = getMargin(contentLayout);
				isSwiping = false;
				return true; // allow other events like Click to be processed
			}

			case MotionEvent.ACTION_MOVE: {
				upX = event.getRawX();
				float deltaX = upX - downX;
				if (Math.abs(deltaX) > MIN_DISTANCE_FOR_SWIPING && listView != null && !isSwiping) {
					isSwiping = true;
					listView.requestDisallowInterceptTouchEvent(isSwiping);
				}
				if(isSwiping)
				{
					drawSwipe((int) deltaX);
				}
				return true;
			}

			case MotionEvent.ACTION_UP: {
				upX = event.getRawX();
				float deltaX = upX - downX;
				if (isSwiping) {
					actionSwipe((int) deltaX);
				} else {
					changeLayoutPosition(0);
					touchedView.performClick();
				}

				if (listView != null) {
					isSwiping = false;
					listView.requestDisallowInterceptTouchEvent(false);
				}
				return false;
			}
			case MotionEvent.ACTION_CANCEL: {
				return false;
			}
			}

			return true;
		}

		private void drawSwipe(int distance) {
			if (contentLayoutOriginalX > 0) {
				int newPosition = contentLayoutOriginalX + distance;
				if (newPosition < 0)
					newPosition = 0;
				setMargins(contentLayout, newPosition);

			} else {
				if (mainLayoutOriginalX + distance < 0) {
					setMargins(mainLayout, mainLayoutOriginalX + distance);
					setMargins(rectangleView, mainLayoutOriginalX + distance);
					setMargins(contentLayout, 0);
					double opacity = -(mainLayoutOriginalX + distance);
					opacity = opacity / MIN_DISTANCE_FOR_ACTION;
					if (opacity > 0 && opacity < 1)
						setAlpha((float) (1 - opacity));
				} else {
					setMargins(mainLayout, 0);
					setMargins(rectangleView, 0);
					setMargins(contentLayout, contentLayoutOriginalX + distance);
					setAlpha(1);
				}
			}
		}

		private void actionSwipe(int distance) {
			int direction = 0;
			if (distance > MIN_DISTANCE_FOR_ACTION){
				direction = 1;
			} else if (distance < -MIN_DISTANCE_FOR_ACTION) {
				direction = -1;
			}
			changeLayoutPosition(direction);

	}
	}


}
