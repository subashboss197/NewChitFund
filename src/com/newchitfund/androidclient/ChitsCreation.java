/*************************************************************** 

Project Name : NewChitFund

 **************************************************************** 

Class Name : ChitsCreation

 Date : 27/05/2014

 Developer : Veera manikanta reddy. G

 Description : this class will handle chits creation 



 **************************************************************** 

Change History : 

Date : 

Developer : 

Change : 

 ****************************************************************/
package com.newchitfund.androidclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.newchitfund.businessobjects.Chit;
import com.newchitfund.businessobjects.Member;
import com.newchitfund.imagecaching.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ChitsCreation extends Activity{


	Chit chitDetails = new Chit(); 
	/* Variables declaration start*/
	int totalMembers;
	int totalMembers2;
	int fromFlag = 0;
	int fromFlagChangeMember = 0;
	String currentmemberId = null;
	String chitMemberId =null;
	Boolean backBtnFlag = false;
	Boolean backBtnFlagEdit = false;
	Boolean checkNullMembers = false;
	Boolean checkNet = false;
	Boolean chitEdit = false;
	Boolean chitEditStartChit = false;
	Boolean chitEditStartNoMem = false;
	Boolean onList= false;
	int chitMembersSaved = 0;
	int editTotalMembers = 0;
	String searchName;
	String chitName= null;
	String chitAmount= null;
	String chitMonths= null;
	String chittMembers = null;
	String chitBidDay= null;
	String chitPayDay= null;
	String chitOwnerMonth= null;
	String chitOwnerMonthFormated =null;
	String ownwerId = null;
	int global_position =0;
	String chitId = "0";
	ArrayList<Member> member_data = new ArrayList<Member>();
	ArrayList<Member> search_data = new ArrayList<Member>();
	ArrayList<Member> selected_member_data = new ArrayList<Member>();
	ArrayList<Member> selected_member_data_done = new ArrayList<Member>();
	ArrayList<String> monthSpinnerList = new ArrayList<String>();
	ArrayList<Integer> bidSpinnerList = new ArrayList<Integer>();
	ArrayList<Integer> paySpinnerList = new ArrayList<Integer>();
	ArrayAdapter<String> monthSpinnerAdapter;
	ArrayAdapter<Integer> bidSpinnerAdapter;
	ArrayAdapter<Integer> paySpinnerAdapter;
	AddMemberList_Adapter maddAdapter;
	MemberList_Adapter mAdapter;
	String selected_member_ids;
	ImageLoader imgLoader;
	/* Variables declaration end*/

	/* view items declarations start*/

	EditText et_ChitName;
	EditText et_ChitAmount;
	EditText et_ChitMembers;
	EditText et_ChitMonths;
	EditText et_ChitOwnerMonth;
	EditText et_MemberSearch;
	Spinner spn_ChitPay;
	Spinner spn_ChitBid;
	Spinner spn_OwnerMonth;
	TextView tv_ChitStartDate;
	TextView tv_ChitOwner;
	TextView tv_subTitle;
	TextView tv_chitHeading;
	TextView tv_chitName;
	TextView tv_chitOwnerLabel;
	TextView tv_chitAmount;
	TextView tv_chitMembers;
	TextView tv_chitMonths;
	TextView tv_chitStart;
	TextView tv_chitBidSpiner;
	TextView tv_chitPaySpiner;
	TextView tv_ownerMonthSpiner;
	TextView tv_chitMembersDetails;
	RelativeLayout rl_owner;
	RelativeLayout rl_memberView1;
	ScrollView sv_AddView;
	ListView lv_memberList;
	ListView lv_addMembersList;
	ImageView iv_newChitDoneBtn;
	ImageView iv_chitMembersDetails;
	ImageView iv_addDoneMember;

	/* view items declarations end*/

	/* onCreate start*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chitcreation);

		rl_owner = (RelativeLayout)findViewById(R.id.addOwnerLayout);
		rl_memberView1 = (RelativeLayout)findViewById(R.id.addMeamberView);
		sv_AddView = (ScrollView)findViewById(R.id.mainChitAddView);
		et_ChitName = (EditText)findViewById(R.id.chitName);
		tv_ChitOwner = (TextView)findViewById(R.id.chitOwner);
		et_ChitAmount = (EditText)findViewById(R.id.chitAmount);
		et_ChitMembers = (EditText)findViewById(R.id.chitMembers);
		et_ChitMembers.setSelection(et_ChitMembers.getText().length());
		et_ChitMonths = (EditText)findViewById(R.id.chitMonths);
		et_ChitMonths.setSelection(et_ChitMonths.getText().length());
		tv_ChitStartDate = (TextView)findViewById(R.id.chitStart);
		et_MemberSearch = (EditText)findViewById(R.id.addMemberSearch);
		iv_newChitDoneBtn = (ImageView)findViewById(R.id.newChitDoneBtn);
		iv_chitMembersDetails= (ImageView)findViewById(R.id.chitMembersDetails);
		iv_addDoneMember= (ImageView)findViewById(R.id.addDoneMember);

		/* Click to search by member name */
		et_MemberSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				searchByNameCode();
			}
		});
		/* auto fill members and months */
		et_ChitMembers.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					et_ChitMonths.setText(et_ChitMembers.getText().toString());
				}
			}
		});
		et_ChitMonths.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					et_ChitMembers.setText(et_ChitMonths.getText().toString());
				}
			}
		});


		imgLoader = new ImageLoader(this);

		paySpinnerList.clear();
		bidSpinnerList.clear();
		for(int i = 1;i<=28; i++){
			paySpinnerList.add(i);
			bidSpinnerList.add(i);
		}
		spn_ChitPay = (Spinner)findViewById(R.id.chitPaySpiner);
		paySpinnerAdapter = new ArrayAdapter<Integer>(ChitsCreation.this, android.R.layout.simple_spinner_item, paySpinnerList);
		spn_ChitPay.setAdapter(paySpinnerAdapter);
		paySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn_ChitPay.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				((TextView)parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.Gray20));
				((TextView)parent.getChildAt(0)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		spn_ChitBid = (Spinner)findViewById(R.id.chitBidSpiner);
		bidSpinnerAdapter = new ArrayAdapter<Integer>(ChitsCreation.this, android.R.layout.simple_spinner_item, bidSpinnerList);
		spn_ChitBid.setAdapter(bidSpinnerAdapter);
		bidSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn_ChitBid.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				((TextView)parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.Gray20));
				((TextView)parent.getChildAt(0)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		spn_OwnerMonth = (Spinner)findViewById(R.id.ownerMonthSpiner);
		spn_OwnerMonth.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				((TextView)parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.Gray20));
				((TextView)parent.getChildAt(0)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		spn_OwnerMonth.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(monthSpinnerList.size()==0)
					{
						Toast.makeText(getApplicationContext(), "Select start month", Toast.LENGTH_SHORT).show();
						return true;
					}
				}
				return false;
			}
		});
		tv_subTitle = (TextView)findViewById(R.id.subScreenTitle);
		tv_chitHeading = (TextView)findViewById(R.id.tv_newChitHeading);
		tv_chitName = (TextView)findViewById(R.id. tv_chitName);
		tv_chitOwnerLabel =(TextView)findViewById(R.id. tv_chitOwner);
		tv_chitAmount =(TextView)findViewById(R.id. tv_chitAmount);
		tv_chitMembers=(TextView)findViewById(R.id. tv_chitMembers);
		tv_chitMonths =(TextView)findViewById(R.id. tv_chitMonths);
		tv_chitStart =(TextView)findViewById(R.id. tv_chitStart);
		tv_chitBidSpiner = (TextView)findViewById(R.id. tv_chitBidSpiner);
		tv_chitPaySpiner = (TextView)findViewById(R.id. tv_chitPaySpiner);
		tv_ownerMonthSpiner =(TextView)findViewById(R.id. tv_ownerMonthSpiner);
		tv_chitMembersDetails=(TextView)findViewById(R.id. tv_chitMembersDetails);
		lv_memberList = (ListView)findViewById(R.id.membersList);
		lv_addMembersList = (ListView)findViewById(R.id.addmembersList);
		/** Defining the ArrayAdapter to set items to Spinner Widget */
		monthSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, monthSpinnerList);
		spn_OwnerMonth.setAdapter(monthSpinnerAdapter);
		monthSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//spn_OwnerMonth.setEnabled(false);
		et_ChitMonths.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				setOwnerMonths();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {

			}


		});
		//declaring font 
		tv_chitHeading.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		et_ChitName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		et_ChitName.setTextColor(getResources().getColor(R.color.Gray20));
		tv_chitName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		tv_ChitOwner.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		tv_ChitOwner.setTextColor(getResources().getColor(R.color.Gray20));
		tv_chitOwnerLabel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		tv_chitAmount.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf")); 
		et_ChitAmount.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf")); 
		et_ChitAmount.setTextColor(getResources().getColor(R.color.Gray20));
		tv_chitMembers.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		et_ChitMembers.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		et_ChitMembers.setTextColor(getResources().getColor(R.color.Gray20));
		tv_chitMonths.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		et_ChitMonths.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		et_ChitMonths.setTextColor(getResources().getColor(R.color.Gray20));
		tv_chitStart.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		tv_ChitStartDate.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		tv_ChitStartDate.setTextColor(getResources().getColor(R.color.Gray20));
		tv_chitBidSpiner.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		tv_chitPaySpiner.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		tv_ownerMonthSpiner.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		tv_subTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		tv_chitMembersDetails.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));


		//Enabling all the fields.
		tv_ChitStartDate.setClickable(true);
		tv_ChitOwner.setClickable(true);
		et_ChitName.setFocusable(true);
		et_ChitAmount.setFocusable(true);
		et_ChitMembers.setFocusable(true);
		et_ChitMonths.setFocusable(true);
		spn_ChitBid.setEnabled(true);
		spn_ChitPay.setEnabled(true);
		iv_newChitDoneBtn.setVisibility(View.VISIBLE);
		iv_chitMembersDetails.setClickable(true);
		tv_chitHeading.setText("Create New Chit");

		Bundle bundle = getIntent().getExtras();
		if(bundle!= null){
			chitId = bundle.getString("chitID");
			chitDetails = (Chit) bundle.getSerializable("chitDetails");
			backBtnFlagEdit = true;
		}
		//edit chit code
		if(!chitId.equalsIgnoreCase("0")){
			tv_chitHeading.setText("Edit Chit");
			new EditChitAsynTask().execute();
		}
		//showSoftKeyboard(ChitsCreation.this);
	}
	/* onCreate end*/

	/* method to search member by name */
	private void searchByNameCode() {
		searchName = et_MemberSearch.getText().toString();
		int textLength = searchName.length();

		search_data.clear();

		for (int i = 0; i < member_data.size(); i++) {
			String memberMainName = member_data.get(i).get_name();
			if (textLength <= memberMainName.length()) {
				// compare the String in EditText with Names in the ArrayList
				if (searchName.equalsIgnoreCase(memberMainName.substring(0,	textLength))) {
					search_data.add(member_data.get(i));
				}

			}
		}
		MemberList_Adapter mAdapter = new MemberList_Adapter(ChitsCreation.this,R.layout.addmemberrow, search_data);
		lv_memberList.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	/* this is for date dialog ---- start */

	static final int DATE_DIALOG_ID = 0;
	// variables to save user selected date and time
	public int year = 0, month = -1, day, hour, minute;
	private int mYear, mMonth, mDay;

	//default constructor
	public ChitsCreation() {
		// Assign current Date and Time Values to Variables
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

	}

	// Register DatePickerDialog listener
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		// the callback received when the user "sets" the Date in the
		// DatePickerDialog
		public void onDateSet(DatePicker view, int yearSelected,int monthOfYear, int dayOfMonth) {
			year = yearSelected;
			month = monthOfYear;
			day = dayOfMonth;
			if(day < 10 && month+1 < 10){
				tv_ChitStartDate.setText("0"+(month+1) + "/"+(year)); //"0"+day+ "/" + 
			}
			else if(day < 10 && month+1 >= 10){

				tv_ChitStartDate.setText((month+1) + "/"+(year));//"0"+day+ "/" + 

			}else if(day >= 10 && month+1 < 10){

				tv_ChitStartDate.setText("0"+(month+1) + "/"+(year));//day+ "/" + 
			}
			else{
				tv_ChitStartDate.setText( (month+1) + "/"+(year));//day+ "/" +
			}
			// Set the Selected Date in Select date Button
			//tv_ChitStartDate.setText(year+ "-" + (month+1) + "-"+ day);
			setOwnerMonths();


			//setting the bid date and pay date spinners values if the user selected current month as starting month
			if(mMonth == month){
				bidSpinnerList.clear();
				for(int i = day;i<=28; i++){
					bidSpinnerList.add(i);
				}
				bidSpinnerAdapter.notifyDataSetChanged();

				paySpinnerList.clear();
				for(int i = (day+1);i<=28; i++){
					paySpinnerList.add(i);
				}
				paySpinnerAdapter.notifyDataSetChanged();
				spn_ChitPay.setSelection(0);
				spn_ChitBid.setSelection(0);
			}else{
				Object payTemp = spn_ChitPay.getSelectedItem();
				Object bidTemp = spn_ChitBid.getSelectedItem();
				paySpinnerList.clear();	
				bidSpinnerList.clear();
				for(int i = 1;i<=28; i++){
					paySpinnerList.add(i);
					bidSpinnerList.add(i);
				}
				spn_ChitPay.setSelection(paySpinnerAdapter.getPosition((Integer)payTemp ));
				spn_ChitBid.setSelection(bidSpinnerAdapter.getPosition((Integer) bidTemp));
				paySpinnerAdapter.notifyDataSetChanged();
				bidSpinnerAdapter.notifyDataSetChanged();
			}

		}


	};
	/* method to build owner month spinner value start*/
	public  void setOwnerMonths() {

		if(chitId.equalsIgnoreCase("0")){
			if(et_ChitMonths.getText().toString().trim().length() > 0){
				int noOfMons = Integer.parseInt(et_ChitMonths.getText().toString().trim());
				if(month >=0){
					int tempMonth = month;
					int tempYear = year;
					monthSpinnerList.clear();
					for(int i=0; i<noOfMons;i++){
						if(tempMonth >= 12){
							tempMonth = 0;
							tempYear++;
						}
						String monthName = getMonthNameText(tempMonth);
						monthSpinnerList.add(monthName +" "+ tempYear);
						tempMonth++;
					}
					if(!monthSpinnerList.isEmpty()){
						spn_OwnerMonth.setEnabled(true);
					}
					else{
						spn_OwnerMonth.setEnabled(false);
					}
					monthSpinnerAdapter.notifyDataSetChanged();
					spn_OwnerMonth.setSelection(1);
				}
			}
		}
	}
	/* method to build owner month spinner value end*/



	/* method to return month name start */
	private String getMonthNameText(int tempMonth) {
		String retVal = null;
		switch(tempMonth){
		case 0:
			retVal = "January";
			break;
		case 1:
			retVal = "February";
			break;
		case 2:
			retVal = "March";
			break;
		case 3:
			retVal = "April";
			break;
		case 4:
			retVal = "May";
			break;
		case 5:
			retVal = "June";
			break;
		case 6:
			retVal = "July";
			break;
		case 7:
			retVal = "August";
			break;
		case 8:
			retVal = "September";
			break;
		case 9:
			retVal = "October";
			break;
		case 10:
			retVal = "November";
			break;
		case 11:
			retVal = "December";
			break;
		}

		return retVal;
	}
	/* method to return month name end */

	// Method automatically gets Called when you call showDialog() method
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:

			// create a new DatePickerDialog with values you want to show
			/*DatePickerDialog da =new DatePickerDialog(this, mDateSetListener, mYear, mMonth,mDay);*/
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, 1);
			Date newDate = c.getTime();
			DatePickerDialog da = this.customDatePicker();
			da.setCanceledOnTouchOutside(false);
			da.setCancelable(false);
			//da.updateDate(newDate.getYear(), newDate.getMonth(), newDate.getDate());
			da.getDatePicker().setMinDate(newDate.getTime());
			return da;
		}
		return null;
	}
	private DatePickerDialog customDatePicker() {
		final DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);		  

		dpd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// showDialog(DATE_DIALOG_ID);
				dialog.dismiss();
				dpd.updateDate(mYear, mMonth, mDay);

			}
		});

		try {

			Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
			for (Field datePickerDialogField : datePickerDialogFields) {
				if (datePickerDialogField.getName().equals("mDatePicker")) {
					datePickerDialogField.setAccessible(true);
					DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
					Field datePickerFields[] = datePickerDialogField.getType().getDeclaredFields();
					for (Field datePickerField : datePickerFields) {
						if ("mDayPicker".equals(datePickerField.getName()) || "mDaySpinner".equals(datePickerField.getName())) {
							datePickerField.setAccessible(true);
							Object dayPicker = new Object();
							dayPicker = datePickerField.get(datePicker);
							((View) dayPicker).setVisibility(View.GONE);
						}
					}
				}

			}
		} catch (Exception ex) {
		}
		return dpd;

	}
	/* this is for date dialog ---- end */


	/*Method to hide keyboard start*/
	public static void hideSoftKeyboard(Activity activity) {


		InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (inputMethodManager.isAcceptingText()) {
			inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		}

	}
	/*Method to hide keyboard end*/

	/*Method to show keyboard start*/
	public static void showSoftKeyboard(Activity activity) {


		InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (inputMethodManager.isAcceptingText()) {
			inputMethodManager.showSoftInput((View) activity.getCurrentFocus().getWindowToken(), 0);
		}

	}
	/*Method to show keyboard end*/

	/* method to show owner adding layout start*/
	@SuppressWarnings("deprecation")
	public void showOwnerLayout(View v)
	{
		hideSoftKeyboard(ChitsCreation.this);
		iv_newChitDoneBtn.setVisibility(View.GONE);
		sv_AddView.setVisibility(View.GONE);//hiding main chit creation scrollview
		rl_owner.setBackgroundDrawable(getResources().getDrawable(R.drawable.add_own_back));//changing background
		tv_subTitle.setText("Add Owner");//changing heading
		rl_memberView1.setVisibility(View.GONE);//hiding adding member list
		rl_owner.setVisibility(View.VISIBLE);//showing owner layout
		lv_memberList.setVisibility(View.VISIBLE);//showing members list to add owner
		et_MemberSearch.setVisibility(View.VISIBLE);
		et_MemberSearch.setText("");
		fromFlag = 1;
		backBtnFlag = true;
		new MemberList().execute();
	}
	/* method to show owner adding layout end*/

	/* method to show main layout start*/
	public void showMainLayout(View v)
	{
		Member men;
		sv_AddView.setVisibility(View.VISIBLE);//showing main chit creation scrollview
		rl_owner.setVisibility(View.GONE);//hiding owner layout
		if(chitEditStartChit){
			iv_newChitDoneBtn.setVisibility(View.GONE);
		}else{
			iv_newChitDoneBtn.setVisibility(View.VISIBLE);
		}
		iv_addDoneMember.setVisibility(View.GONE);
		backBtnFlag = false;
		boolean clearFlag = false;
		if(selected_member_data_done.isEmpty()){
			selected_member_data.clear();
		}
		else{
			for(int i=0;i<selected_member_data_done.size();i++){
				men = selected_member_data_done.get(i);
				if(men!= null){
					selected_member_data.set(i,men);
				}else{
					clearFlag = true;
				}
			}
		}
		if(clearFlag){
			selected_member_data.clear();
		}
	}
	/* method to show main layout end*/

	/* method to save selected members ids start*/
	public void saveMemberIds(View v)
	{
		Member men;
		if(!selected_member_data.isEmpty()){
			for(int i=0;i<selected_member_data.size();i++){
				men = selected_member_data.get(i);
				if(men!= null){
					selected_member_data_done.set(i,men);
				}
			}
		}

		sv_AddView.setVisibility(View.VISIBLE);//showing main chit creation scrollview
		rl_owner.setVisibility(View.GONE);//hiding owner layout
		iv_newChitDoneBtn.setVisibility(View.VISIBLE);
		iv_addDoneMember.setVisibility(View.GONE);
		backBtnFlag = false;
	}
	/*  method to save selected members ids end*/


	/* method to show added member layout start*/
	public void chitMembersAdd(View v)
	{

		checkNet = Utils.isInternetAvailable(ChitsCreation.this);
		if(!checkNet){
			Toast.makeText(ChitsCreation.this, "Please check network connection", Toast.LENGTH_SHORT).show();
			return;
		}
		totalMembers = 0;
		if(et_ChitMembers.getText().toString().trim().length() > 0){
			totalMembers = Integer.parseInt(et_ChitMembers.getText().toString().trim());
		}
		// checking for edit or create
		if(chitEdit){
			if(!validateEditChit()){
				return;
			};
			if(chitEditStartNoMem && chitEditStartChit){
				Toast.makeText(ChitsCreation.this, "Chit already started and no members to change", Toast.LENGTH_SHORT).show();
				return;
			}
			if(editTotalMembers != totalMembers){
				new AlertDialog.Builder(this)
				//.setTitle("Delete members")
				.setMessage("Total members are changed so add all members again")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 
						// continue with delete
						fromFlag = 2;
						hideShowList();
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 
						// do nothing
					}
				})
				//.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
				return;
			}
			else{
				fromFlag = 3;
				fromFlagChangeMember =1;
				hideShowList();
				if(chitMembersSaved ==1){
					checkNullMembers = true;
				}
				if(chitMembersSaved !=1){
					new GetChitMembersAsynTask().execute();
				}
			}
		}
		else{
			hideSoftKeyboard(ChitsCreation.this);
			if(!validateCreation()){
				return;
			};
			fromFlag = 2;
			hideShowList();
		}
		iv_newChitDoneBtn.setVisibility(View.GONE);

	}
	/* method to show added member layout end*/


	//common method to show hide full member list  
	private void hideShowList() {
		// TODO Auto-generated method stub
		//selected_member_data.clear();
		totalMembers = totalMembers -1;
		if(selected_member_data.size()==0 || selected_member_data.size()!=totalMembers){
			selected_member_data.clear();
			//selected_member_data_done.clear();
			for(int i=0;i<totalMembers;i++)
			{
				selected_member_data.add(null);
			}
		}
		if(selected_member_data_done.size()==0 || (selected_member_data_done.size()!=totalMembers && selected_member_data_done.size()<totalMembers)){
			selected_member_data_done.clear();
			for(int i=0;i<totalMembers;i++)
			{
				selected_member_data_done.add(null);
			}
		}	
		backBtnFlag = true;
		//selected_member_data.clear();
		lv_addMembersList.setDivider(null);
		maddAdapter = new AddMemberList_Adapter(ChitsCreation.this, R.layout.add_members_list);
		lv_addMembersList.setAdapter(maddAdapter);
		sv_AddView.setVisibility(View.GONE);//hiding main chit creation scrollview
		lv_addMembersList.setVisibility(View.VISIBLE);
		rl_owner.setBackgroundDrawable(getResources().getDrawable(R.drawable.add_mem_back));//changing background
		tv_subTitle.setText("Add Members");//changing heading
		rl_memberView1.setVisibility(View.VISIBLE);//showing adding member list
		rl_owner.setVisibility(View.VISIBLE);//hiding owner layout
		lv_memberList.setVisibility(View.GONE);//showing members list to add owner
		et_MemberSearch.setVisibility(View.GONE);
	}



	/* method to show date dialog start*/
	@SuppressWarnings("deprecation")
	public void showDateDialog(View v)
	{
		hideSoftKeyboard(ChitsCreation.this);
		showDialog(DATE_DIALOG_ID); 
	}
	/* method to show date dialog end*/

	/*home button click function start*/
	public void homeButton(View v){
		if(chitEditStartChit){
			Intent chitsCreation = new Intent(ChitsCreation.this, Categories.class);
			startActivity(chitsCreation);
			finish();
		}else{
			new AlertDialog.Builder(ChitsCreation.this)
			//.setTitle("Alert")
			.setMessage("Changes done will not be saved")
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 
					Intent chitsCreation = new Intent(ChitsCreation.this, Categories.class);
					startActivity(chitsCreation);
					finish();
				}
			})
			.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 
					// do nothing
				}
			})
			//.setIcon(android.R.drawable.ic_dialog_alert)
			.show();
		}

	}
	/*home button click function end*/

	/* method for back button start */
	public void backButtonMethod(View v)
	{
		if(onList){
			lv_memberList.setVisibility(View.GONE);
			et_MemberSearch.setVisibility(View.GONE);
			rl_memberView1.setVisibility(View.VISIBLE);
			onList = false;
		}else if(backBtnFlag){
			sv_AddView.setVisibility(View.VISIBLE);//showing main chit creation scrollview
			rl_owner.setVisibility(View.GONE);//hiding owner layout
			backBtnFlag = false;
			if(chitEditStartChit){
				iv_newChitDoneBtn.setVisibility(View.GONE);
			}else{
				iv_newChitDoneBtn.setVisibility(View.VISIBLE);
			}

		} else if(backBtnFlagEdit){
			backBtnFlagEdit = false;
			Intent chitsDetails = new Intent(ChitsCreation.this, ChitDetails.class);
			chitsDetails.putExtra("chit", chitDetails);
			startActivity(chitsDetails);
			finish();
		}else{
			Intent chitsCreation = new Intent(ChitsCreation.this, Categories.class);
			startActivity(chitsCreation);
			finish();
		}
	}
	/* method for back button end */

	/*method for done button start */
	public void doneButtonMethod(View v)
	{
		hideSoftKeyboard(ChitsCreation.this);
		chitName = et_ChitName.getText().toString().trim();
		chitAmount = et_ChitAmount.getText().toString().trim();
		chitMonths = et_ChitMonths.getText().toString().trim();
		chittMembers = et_ChitMembers.getText().toString().trim();
		chitPayDay = spn_ChitPay.getSelectedItem().toString();
		chitBidDay = spn_ChitBid.getSelectedItem().toString();
		//check if it is edit or creating chit
		if(chitEdit){
			if(!validateEditChit()){
				return;
			};
			int intChitMonths = Integer.parseInt(chitMonths);
			int intChitMembers = Integer.parseInt(chittMembers);
			totalMembers2 =intChitMonths;
			if(editTotalMembers != intChitMonths || checkNullMembers){
				Member men;
				selected_member_ids = null;
				Boolean allChekFlag = false;
				for(int i=0;i<intChitMembers-1;i++){
					if(!selected_member_data_done.isEmpty()){
						men = selected_member_data_done.get(i);
						if(men!= null){
							if(selected_member_ids == null){
								selected_member_ids = men.get_userID();
							}else{
								selected_member_ids += "," + men.get_userID();
							}
						}else{
							allChekFlag = true;
						}
					}else{
						allChekFlag = true;
					}
				}
				if(allChekFlag && selected_member_ids != null){
					Toast.makeText(ChitsCreation.this, "Please select all chit members", Toast.LENGTH_SHORT).show();
					return;
				}
			}


			new EditSaveChitAsynTask().execute();

		}
		else{

			if(!validateCreation()){
				return;
			};
			int intChitMembers = Integer.parseInt(chittMembers);
			Member men;
			selected_member_ids = null;
			Boolean allChekFlag = true;
			for(int i=0;i<intChitMembers-1;i++){
				if(!selected_member_data_done.isEmpty()){
					men = selected_member_data_done.get(i);
					if(men!= null){
						if(selected_member_ids == null){
							selected_member_ids = men.get_userID();
						}else{
							selected_member_ids += "," + men.get_userID();
						}
					}else{
						allChekFlag = false;
					}
				}
			}
			if(!allChekFlag && selected_member_ids != null){
				Toast.makeText(ChitsCreation.this, "Please select all chit members", Toast.LENGTH_SHORT).show();
				return;
			}
			new AddChitAsynTask().execute();
		}


	}
	/*method for done button end */

	private boolean validateEditChit() {
		chitName = et_ChitName.getText().toString().trim();
		chitAmount = et_ChitAmount.getText().toString().trim();
		chitMonths = et_ChitMonths.getText().toString().trim();
		chittMembers = et_ChitMembers.getText().toString().trim();
		chitPayDay = spn_ChitPay.getSelectedItem().toString();
		chitBidDay = spn_ChitBid.getSelectedItem().toString();

		if(chitAmount.length() <= 0){
			Toast.makeText(ChitsCreation.this, "Please enter chit amount", Toast.LENGTH_SHORT).show();
			return false;
		}
		int tempAmount = Integer.parseInt(chitAmount);
		if(tempAmount <50000 || tempAmount > 5000000){
			Toast.makeText(ChitsCreation.this, "Chit amount should be between 50,000 and 50,00,000", Toast.LENGTH_SHORT).show();
			et_ChitAmount.setText("");
			return false;
		}

		if(chittMembers.length() <= 0){
			Toast.makeText(ChitsCreation.this, "Please enter chit total members", Toast.LENGTH_SHORT).show();
			return false;
		}
		int intChitMembers = Integer.parseInt(chittMembers);

		if(intChitMembers < 5 || intChitMembers > 20){
			Toast.makeText(ChitsCreation.this, "Chit total members minimum 5 and maximun 20", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(chitMonths.length() <= 0){
			Toast.makeText(ChitsCreation.this, "Please enter chit total months", Toast.LENGTH_SHORT).show();
			return false;
		}
		int intChitMonths = Integer.parseInt(chitMonths);
		if(intChitMonths < 5 || intChitMonths > 20){
			Toast.makeText(ChitsCreation.this, "Chit total months minimum 5 and maximun 20", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(intChitMonths != intChitMembers){
			Toast.makeText(ChitsCreation.this, "Chit total months and members should be equal", Toast.LENGTH_SHORT).show();
			return false;
		}

		if(chitBidDay.length() <= 0){
			Toast.makeText(ChitsCreation.this, "Please select chit bid day", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(chitPayDay.length() <= 0){
			Toast.makeText(ChitsCreation.this, "Please select chit pay day", Toast.LENGTH_SHORT).show();
			return false;
		}
		int tempPayDay =Integer.parseInt(chitPayDay);
		int tempBidDay =Integer.parseInt(chitBidDay);
		if(tempPayDay <= tempBidDay){
			Toast.makeText(ChitsCreation.this, "Chit pay day should be after the bid day", Toast.LENGTH_SHORT).show();
			spn_ChitPay.setSelection(0);
			return false;
		}
		return true;
	}

	private boolean validateCreation() {
		chitName = et_ChitName.getText().toString().trim();
		chitAmount = et_ChitAmount.getText().toString().trim();
		chitMonths = et_ChitMonths.getText().toString().trim();
		chittMembers = et_ChitMembers.getText().toString().trim();
		chitPayDay = spn_ChitPay.getSelectedItem().toString();
		chitBidDay = spn_ChitBid.getSelectedItem().toString();

		if(spn_OwnerMonth.getSelectedItem() != null){
			chitOwnerMonth = spn_OwnerMonth.getSelectedItem().toString();
		}
		if(chitName.length() <= 0){
			Toast.makeText(ChitsCreation.this, "Please enter chit name", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(ownwerId == null || ownwerId.length() <= 0 ){
			Toast.makeText(ChitsCreation.this, "Please select chit owner", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(chitAmount.length() <= 0){
			Toast.makeText(ChitsCreation.this, "Please enter chit amount", Toast.LENGTH_SHORT).show();
			return false;
		}
		int tempAmount = Integer.parseInt(chitAmount);
		if(tempAmount <50000 || tempAmount > 5000000){
			Toast.makeText(ChitsCreation.this, "Chit amount should be between 50,000 and 50,00,000", Toast.LENGTH_SHORT).show();
			et_ChitAmount.setText("");
			return false;
		}

		if(chittMembers.length() <= 0){
			Toast.makeText(ChitsCreation.this, "Please enter chit total members", Toast.LENGTH_SHORT).show();
			return false;
		}
		int intChitMembers = Integer.parseInt(chittMembers);

		if(intChitMembers < 5 || intChitMembers > 20){
			Toast.makeText(ChitsCreation.this, "Chit total members minimum 5 and maximun 20", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(chitMonths.length() <= 0){
			Toast.makeText(ChitsCreation.this, "Please enter chit total months", Toast.LENGTH_SHORT).show();
			return false;
		}
		int intChitMonths = Integer.parseInt(chitMonths);
		if(intChitMonths < 5 || intChitMonths > 20){
			Toast.makeText(ChitsCreation.this, "Chit total months minimum 5 and maximun 20", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(intChitMonths != intChitMembers){
			Toast.makeText(ChitsCreation.this, "Chit total months and members should be equal", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(month == -1 || year == 0){
			Toast.makeText(ChitsCreation.this, "Please select chit start date", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(chitBidDay.length() <= 0){
			Toast.makeText(ChitsCreation.this, "Please select chit bid day", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(chitPayDay.length() <= 0){
			Toast.makeText(ChitsCreation.this, "Please select chit pay day", Toast.LENGTH_SHORT).show();
			return false;
		}
		int tempPayDay =Integer.parseInt(chitPayDay);
		int tempBidDay =Integer.parseInt(chitBidDay);
		if(tempPayDay <= tempBidDay){
			Toast.makeText(ChitsCreation.this, "Chit pay day should be after the bid day", Toast.LENGTH_SHORT).show();
			spn_ChitPay.setSelection(0);
			return false;
		}
		if(chitOwnerMonth.length() <= 0 || chitOwnerMonth == null){
			Toast.makeText(ChitsCreation.this, "Please select chit owner month", Toast.LENGTH_SHORT).show();
			return false;
		}
		String[] tempOwnerMonth = chitOwnerMonth.split(" ");
		String monthName = getMonthName(tempOwnerMonth[0]);
		chitOwnerMonthFormated = tempOwnerMonth[1] + "-" + monthName;
		return true;
	}

	/* method to return months names start*/
	private String getMonthName(String abc) {
		String retval = null;

		if(abc.equals("January")){
			retval = "01";
		}
		else if(abc.equals("February")){
			retval = "02";
		}
		else if(abc.equals("March")){
			retval = "03";
		}
		else if(abc.equals("April")){
			retval = "04";
		}
		else if(abc.equals("May")){
			retval = "05";
		}
		else if(abc.equals("June")){
			retval = "06";
		}
		else if(abc.equals("July")){
			retval = "07";
		}
		else if(abc.equals("August")){
			retval = "08";
		}
		else if(abc.equals("September")){
			retval = "09";
		}
		else if(abc.equals("October")){
			retval = "10";
		}
		else if(abc.equals("November")){
			retval = "11";
		}
		else if(abc.equals("December")){
			retval = "12";
		}
		return retval;
	}
	/* method to return months names end*/
	/*************************************************************** 

	Project Name : NewChitFund

	 **************************************************************** 

	Class Name : MemberList

	 Date : 28/05/2014

	 Developer : Veera manikanta reddy. G

	 Description : To get data from service in background and show them in listview


	 **************************************************************** 

	Change History : 

	Date : 

	Developer : 

	Change : 

	 ****************************************************************//**/

	class MemberList extends AsyncTask<Void, Void, Void> {
		private String Error = null;
		ProgressDialog pd;
		String res;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(ChitsCreation.this);
			pd.setMessage("Loading members, Please wait..");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				checkNet = Utils.isInternetAvailable(ChitsCreation.this);
				if(checkNet){
					String URL = getResources().getString(R.string.URL);
					String adminID = getResources().getString(R.string.adminID);

					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(URL);
					MultipartEntity multiEntity = new MultipartEntity();
					multiEntity.addPart("call", new StringBody("getAllMembers"));
					multiEntity.addPart("adminID", new StringBody(adminID));
					post.setEntity(multiEntity);

					HttpResponse response = client.execute(post);
					res = EntityUtils.toString(response.getEntity());

					Log.v("responce", "Response===" + res.toString());
				}
			} catch (Exception e) {
				Error = e.getMessage();
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				if(!checkNet){
					pd.dismiss();
					Toast.makeText(ChitsCreation.this, "Please check network connection", Toast.LENGTH_SHORT).show();
					return;
				}
				if (Error != null) {

				} else {
					try {
						member_data.clear();
						JSONObject json = new JSONObject(res);
						JSONArray jsonArray = json.getJSONArray("Result");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject resultObj = jsonArray.getJSONObject(i);
							Member member = new Member();

							member.set_name(resultObj.getString("name"));
							member.set_phone(resultObj.getString("phone"));
							member.set_userID(resultObj.getString("userID"));
							member.set_profilePic(resultObj.getString("profilePic"));							

							member_data.add(member);
						}
					} catch (Exception e) {
						Error = e.getMessage();
						e.printStackTrace();
					}
					//apply adapter to members list
					mAdapter = new MemberList_Adapter(ChitsCreation.this, R.layout.addmemberrow, member_data);
					lv_memberList.setAdapter(mAdapter);
					lv_memberList.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
							final Member emp = (Member) lv_memberList.getItemAtPosition(arg2);
							onList = false;
							if(fromFlag == 2){
								selected_member_data.set(global_position,emp);
								lv_memberList.setVisibility(View.GONE);
								et_MemberSearch.setVisibility(View.GONE);
								rl_memberView1.setVisibility(View.VISIBLE);
								maddAdapter.notifyDataSetChanged();
								checkForDoneShow();
							}
							else if(fromFlag == 1){
								ownwerId = emp.get_userID();
								tv_ChitOwner.setText(emp.get_name());
								sv_AddView.setVisibility(View.VISIBLE);
								rl_owner.setVisibility(View.GONE);
								et_ChitAmount.requestFocus();
							}
							else if(fromFlag == 3){
								if(checkNullMembers){
									//checkNullMembers = false;
									selected_member_data.set(global_position,emp);
									lv_memberList.setVisibility(View.GONE);
									et_MemberSearch.setVisibility(View.GONE);
									rl_memberView1.setVisibility(View.VISIBLE);
									maddAdapter.notifyDataSetChanged();
									checkForDoneShow();
								}else{
									new AlertDialog.Builder(ChitsCreation.this)
									//.setTitle("Replace member")
									.setMessage("Are you sure you wan to replace the member?")
									.setPositiveButton(getResources().getString(R.string.yestext), new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) { 
											currentmemberId = selected_member_data.get(global_position).get_userID();
											chitMemberId = selected_member_data.get(global_position).get_phone();
											fromFlagChangeMember =3;
											emp.set_phone(selected_member_data.get(global_position).get_phone());
											new GetChitMembersAsynTask().execute();
											selected_member_data.set(global_position,emp);
											lv_memberList.setVisibility(View.GONE);
											et_MemberSearch.setVisibility(View.GONE);
											rl_memberView1.setVisibility(View.VISIBLE);
											maddAdapter.notifyDataSetChanged();
										}
									})
									.setNegativeButton(getResources().getString(R.string.notext), new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) { 
											// do nothing
										}
									})
									//.setIcon(android.R.drawable.ic_dialog_alert)
									.show();
								}

							}

						}

						private void checkForDoneShow() {
							//checking for showing done button
							int intChitMembers = Integer.parseInt(chittMembers);
							Member men;
							selected_member_ids = null;
							Boolean allChekFlag = true;
							for(int i=0;i<intChitMembers-1;i++){
								if(!selected_member_data.isEmpty()){
									men = selected_member_data.get(i);
									if(men!= null){
										if(selected_member_ids == null){
											selected_member_ids = men.get_userID();
										}else{
											selected_member_ids += "," + men.get_userID();
										}
									}else{
										allChekFlag = false;
									}
								}
							}
							if(allChekFlag && selected_member_ids != null){
								iv_addDoneMember.setVisibility(View.VISIBLE);
							}

						}
					});
					mAdapter.notifyDataSetChanged();

				}
				pd.dismiss();
			}
			super.onPostExecute(result);
		}
	}
	/* members list class end */


	/*************************************************************** 

	Project Name : NewChitFund

	 **************************************************************** 

	Class Name : MemberList_Adapter

	 Date : 28/05/2014

	 Developer : Veera manikanta reddy. G

	 Description : to show members in the list


	 **************************************************************** 

	Change History : 

	Date : 

	Developer : 

	Change : 

	 ****************************************************************//**/
	public class MemberList_Adapter extends ArrayAdapter<Member> {
		Activity activity;
		int layoutResourceId;
		Member member;

		ArrayList<Member> data = new ArrayList<Member>();
		// TextView label;
		View row;

		public MemberList_Adapter(Activity act, int layoutResourceId,
				ArrayList<Member> data) {
			super(act, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.activity = act;
			this.data = data;
			notifyDataSetChanged();
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.addmemberrow, parent, false);

				/* creating linearlayout and textviews for data */

				LinearLayout L1_memberrow = (LinearLayout) row.findViewById(R.id.alphabetHeader);
				TextView header = new TextView(getApplicationContext());

				TextView memberName =(TextView) row.findViewById(R.id.tvmembername);
				memberName.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf"));

				TextView memberPhone = (TextView) row.findViewById(R.id.tvmembermobile);
				memberPhone.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf"));

				ImageView ivMemberProfile = (ImageView) row.findViewById(R.id.ivmemberprofile);

				String label1 = data.get(position).get_name();
				char firstChar = label1.toUpperCase().charAt(0);

				if (position == 0) {
					setSection(L1_memberrow, label1);
				} else {
					String preLabel = data.get(position - 1).get_name();
					char preFirstChar = preLabel.toUpperCase().charAt(0);
					if (firstChar != preFirstChar) {
						setSection(L1_memberrow, label1);
					} else {
						header.setVisibility(View.GONE);
					}
				}

				memberName.setText(data.get(position).get_name());
				memberPhone.setText("+91 "+data.get(position).get_phone());

				if(data.get(position).get_profilePic().equals("")){

				}else{
					imgLoader.DisplayImage(data.get(position).get_profilePic(), ivMemberProfile);
				}


			} catch (Exception e) {

			}
			return row;
		}	

	}


	private void setSection(LinearLayout llmemberrow, String label) {
		TextView tv_header = new TextView(getBaseContext());
		// header.setBackgroundColor(0xffaabbcc);
		tv_header.setTextColor(getResources().getColor(R.color.AZindex_color));
		tv_header.setText(label.substring(0, 1).toUpperCase());
		tv_header.setTextSize(getResources().getDimension(R.dimen.addMemberAZIndex));
		tv_header.setPadding(15, 0, 0, 0);
		tv_header.setGravity(Gravity.CENTER_VERTICAL);
		tv_header.setBackgroundColor(getResources().getColor(
				R.color.listheaderbg));
		llmemberrow.addView(tv_header);
	}


	/*************************************************************** 

	Project Name : NewChitFund

	 **************************************************************** 

	Class Name : AddMemberList_Adapter

	 Date : 28/05/2014

	 Developer : Veera manikanta reddy. G

	 Description : adapter to add textviews dynamically to the list to add members


	 **************************************************************** 

	Change History : 

	Date : 

	Developer : 

	Change : 

	 ****************************************************************//**/

	public class AddMemberList_Adapter extends BaseAdapter {
		Activity activity;
		int layoutResourceId;
		Member member;

		public AddMemberList_Adapter(Activity act, int layoutResourceId) {

			this.layoutResourceId = layoutResourceId;
			this.activity = act;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			MemberHolderList holder = null;

			if (row == null) {
				LayoutInflater inflater = LayoutInflater.from(activity);
				row = inflater.inflate(layoutResourceId, parent, false);

				holder = new MemberHolderList();
				holder.tv_member_name = (TextView) row.findViewById(R.id.memberName);
				holder.img_addmember = (ImageView) row.findViewById(R.id.addButton);

				row.setTag(holder);
			} else {
				holder = (MemberHolderList) row.getTag();
			}
			holder.img_addmember.setTag(position);
			member = selected_member_data.get(position);
			//member = selected_member_data_done.get(position);
			if(member != null)
			{
				holder.tv_member_name.setText(member.get_name());
			}else
			{
				holder.tv_member_name.setText("");
			}

			holder.img_addmember.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					global_position = (Integer) v.getTag();
					sv_AddView.setVisibility(View.GONE);
					rl_memberView1.setVisibility(View.GONE);
					lv_memberList.setVisibility(View.VISIBLE);
					et_MemberSearch.setVisibility(View.VISIBLE);
					et_MemberSearch.setText("");
					new MemberList().execute();
					onList = true;
				}
			});
			return row;

		}

		class MemberHolderList {
			TextView tv_member_name;
			ImageView img_addmember;
		}

		@Override
		public int getCount() {
			return totalMembers;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

	}
	/* class to handle add members listview adapter end*/


	/*************************************************************** 

		Project Name : NewChitFund

	 **************************************************************** 

		Class Name : AddChitAsynTask

		 Date : 29/05/2014

		 Developer : Veera manikanta reddy. G

		 Description : saving the new chit data to server


	 **************************************************************** 

		Change History : 

		Date : 

		Developer : 

		Change : 

	 ****************************************************************//**//**
	 * Class Name : AddMemberAsynTask Purpose : Class to add member on
	 * background
	 */
	class AddChitAsynTask extends AsyncTask<Void, Void, String> {
		ProgressDialog pd;

		String mStatus, mMessage;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(ChitsCreation.this);
			pd.setIndeterminate(true);
			pd.setMessage("Adding chit...");
			pd.setCancelable(false);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {

			try {
				checkNet = Utils.isInternetAvailable(ChitsCreation.this);
				if(checkNet){
					String URL = getResources().getString(R.string.URL);
					String adminID = getResources().getString(R.string.adminID);

					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(URL);
					MultipartEntity multiEntity = new MultipartEntity();

					multiEntity.addPart("call", new StringBody("createChit"));
					multiEntity.addPart("name", new StringBody(chitName));
					multiEntity.addPart("ownerID", new StringBody(ownwerId));
					multiEntity.addPart("amount", new StringBody(chitAmount));
					multiEntity.addPart("noOfMembers", new StringBody(chittMembers));
					multiEntity.addPart("noOfMonths", new StringBody(chitMonths));
					multiEntity.addPart("bidDate", new StringBody(chitBidDay));
					multiEntity.addPart("paymentDate", new StringBody(chitPayDay));
					if( month+1 < 10){
						multiEntity.addPart("startingMonth", new StringBody(year+ "-" + "0"+(month+1))); // + "-"+ day
					}else{
						multiEntity.addPart("startingMonth", new StringBody(year+ "-" + (month+1))); // + "-"+ day
					}
					multiEntity.addPart("adminID", new StringBody(adminID));
					multiEntity.addPart("ownerMonth", new StringBody(chitOwnerMonthFormated));
					if(selected_member_ids != null){
						multiEntity.addPart("memberIDs", new StringBody(selected_member_ids));
					}

					post.setEntity(multiEntity);
					HttpResponse response = client.execute(post);
					String res = EntityUtils.toString(response.getEntity());

					Log.v("responce", "Response===" + res);
					JSONObject json1 = new JSONObject(res);

					mStatus = json1.getString("Status");
					mMessage = json1.getString("Message");

					if (mStatus.equalsIgnoreCase("Success")) {
						return "success";
					}
					if (!mMessage.equalsIgnoreCase("OK")) {
						return mMessage;
					}
				}



			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (pd.isShowing()) {
				if(!checkNet){
					pd.dismiss();
					Toast.makeText(ChitsCreation.this, "Please check network connection", Toast.LENGTH_SHORT).show();
					return;
				}
				if (result.equalsIgnoreCase("success")) {

					Toast.makeText(getApplicationContext(),"Chit added successfully", Toast.LENGTH_LONG).show();
					Intent chitsCreation = new Intent(ChitsCreation.this, ViewChits.class);
					startActivity(chitsCreation);
					finish();

				} 
				else {
					Toast.makeText(getApplicationContext(), mMessage,Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
			}
			super.onPostExecute(result);
		}
	}

	/*************************************************************** 

	Project Name : NewChitFund

	 **************************************************************** 

	Class Name : EditChitAsynTask

	 Date : 03/06/2014

	 Developer : Veera manikanta reddy. G

	 Description : get data from server and set to fields to edit


	 **************************************************************** 

	Change History : 

	Date : 

	Developer : 

	Change : 

	 ****************************************************************//**//**
	 * Class Name : AddMemberAsynTask Purpose : Class to add member on
	 * background
	 */
	class EditChitAsynTask extends AsyncTask<Void, Void, String> {
		ProgressDialog pd;

		String mStatus, mMessage;
		private JSONObject jsonResponse;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(ChitsCreation.this);
			pd.setIndeterminate(true);
			pd.setMessage("Loading chit data...");
			pd.setCancelable(false);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {

			try {

				checkNet = Utils.isInternetAvailable(ChitsCreation.this);
				if(checkNet){
					String URL = getResources().getString(R.string.URL);

					URL= getResources().getString(R.string.URL)+"?call=getChitDetails&chitID="+chitId;

					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(URL);
					HttpResponse response = client.execute(post);
					String res = EntityUtils.toString(response.getEntity());

					Log.v("responce", "Response===" + res);
					JSONObject json1 = new JSONObject(res);

					mStatus = json1.getString("Status");
					mMessage = json1.getString("Message");

					if (mStatus.equalsIgnoreCase("Success")) {
						jsonResponse = new JSONObject(res);
						return "success";
					}
					if (!mMessage.equalsIgnoreCase("OK")) {
						return mMessage;
					}
				}


			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(String result) {
			if (pd.isShowing()) {
				if(!checkNet){
					pd.dismiss();
					Toast.makeText(ChitsCreation.this, "Please check network connection", Toast.LENGTH_SHORT).show();
					return;
				}
				if (result.equalsIgnoreCase("success")) {

					try {
						JSONArray jsonArray =jsonResponse.getJSONArray("Result");
						JSONObject resultObj = jsonArray.getJSONObject(0);
						et_ChitName.setText(resultObj.getString("chitName"));
						et_ChitName.setFocusable(false);
						tv_ChitOwner.setText(resultObj.getString("ownerName"));
						tv_ChitOwner.setClickable(false);
						et_ChitAmount.setText(resultObj.getString("chitAmount"));
						et_ChitMembers.setText(resultObj.getString("noOfMembers"));
						editTotalMembers = Integer.parseInt(resultObj.getString("noOfMembers"));
						et_ChitMonths.setText(resultObj.getString("noOfMonths"));
						chitMembersSaved = resultObj.getInt("totalNoOfMembers");
						chitEdit = true;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MM/yyyy");

						String inputStartDate = resultObj.getString("startDate");
						Date date = inputFormat.parse(inputStartDate);
						tv_ChitStartDate.setText(outputFormat.format(date));
						tv_ChitStartDate.setClickable(false);
						//checking for admin or owner
						SharedPreferences sharedpreferences = getSharedPreferences(SessionStorage.ACCOUNT_PREFS_NAME, Context.MODE_PRIVATE);
						String userType = sharedpreferences.getString("MEMBER_ROLE", null);
						if(!userType.equalsIgnoreCase("ADMIN")){
							et_ChitAmount.setFocusable(false);
							et_ChitMembers.setFocusable(false);
							et_ChitMonths.setFocusable(false);
							spn_ChitBid.setEnabled(false);
							spn_ChitPay.setEnabled(false);
						}

						//comparing start date with todays date
						Date now = new Date(System.currentTimeMillis());
						int compareDateVal = now.compareTo(date);
						if(compareDateVal > 0){
							et_ChitAmount.setFocusable(false);
							et_ChitMembers.setFocusable(false);
							et_ChitMonths.setFocusable(false);
							spn_ChitBid.setEnabled(false);
							spn_ChitPay.setEnabled(false);
							iv_newChitDoneBtn.setVisibility(View.GONE);
							chitEditStartChit = true;
							if(resultObj.getInt("totalNoOfMembers") <= 1){
								//iv_chitMembersDetails.setClickable(false);
								chitEditStartNoMem = true;
							}
						}

						SimpleDateFormat inputFormat1 = new SimpleDateFormat("dd/MM/yyyy");
						SimpleDateFormat outputFormat1 = new SimpleDateFormat("dd");

						String inputBidDate = resultObj.getString("bidDate");
						Date bidDay = inputFormat1.parse(inputBidDate);
						spn_ChitBid.setSelection(Integer.parseInt(outputFormat1.format(bidDay))-1);

						String inputPayDate = resultObj.getString("payDate");
						Date payDay = inputFormat1.parse(inputPayDate);
						spn_ChitPay.setSelection(Integer.parseInt(outputFormat1.format(payDay))-1);

						SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM");
						SimpleDateFormat outputFormat2 = new SimpleDateFormat("MMMM");
						SimpleDateFormat outputFormat3 = new SimpleDateFormat("yyyy");
						String inputOwnerMonth = resultObj.getString("ownerMonth");
						Date ownerMoth = inputFormat2.parse(inputOwnerMonth);
						monthSpinnerList.clear();
						monthSpinnerList.add(outputFormat2.format(ownerMoth).toString()+ " "+outputFormat3.format(ownerMoth).toString());
						spn_OwnerMonth.setEnabled(false);
						monthSpinnerAdapter.notifyDataSetChanged();


					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}



				} 
				else {
					Toast.makeText(getApplicationContext(), mMessage,Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
			}
			super.onPostExecute(result);
		}
	}



	/*************************************************************** 

	Project Name : NewChitFund

	 **************************************************************** 

	Class Name : EditSaveChitAsynTask

	 Date : 3/05/2014

	 Developer : Veera manikanta reddy. G

	 Description : saving the edit chit data to server


	 **************************************************************** 

	Change History : 

	Date : 

	Developer : 

	Change : 

	 ****************************************************************//**/

	class EditSaveChitAsynTask extends AsyncTask<Void, Void, String> {
		ProgressDialog pd;

		String mStatus, mMessage;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(ChitsCreation.this);
			pd.setIndeterminate(true);
			pd.setMessage("Saving chit...");
			pd.setCancelable(false);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {

			try {
				checkNet = Utils.isInternetAvailable(ChitsCreation.this);
				if(checkNet){
					String URL = getResources().getString(R.string.URL);

					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(URL);
					MultipartEntity multiEntity = new MultipartEntity();

					multiEntity.addPart("call", new StringBody("editChit"));
					multiEntity.addPart("chitID", new StringBody(chitId));
					multiEntity.addPart("amount", new StringBody(chitAmount));
					multiEntity.addPart("noOfMembers", new StringBody(chittMembers));
					multiEntity.addPart("noOfMonths", new StringBody(chitMonths));
					multiEntity.addPart("bidDate", new StringBody(chitBidDay));
					multiEntity.addPart("paymentDate", new StringBody(chitPayDay));
					//if total members change then we send all fresh ids
					if((editTotalMembers) != totalMembers2 || checkNullMembers){
						if(selected_member_ids != null){
							multiEntity.addPart("newMemberIDs", new StringBody(selected_member_ids));
						}
						checkNullMembers = false;
					}

					post.setEntity(multiEntity);
					HttpResponse response = client.execute(post);
					String res = EntityUtils.toString(response.getEntity());

					Log.v("responce", "Response===" + res);
					JSONObject json1 = new JSONObject(res);

					mStatus = json1.getString("Status");
					mMessage = json1.getString("Message");

					if (mStatus.equalsIgnoreCase("Success")) {
						return "success";
					}
					if (!mMessage.equalsIgnoreCase("OK")) {
						return mMessage;
					}
				}


			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (pd.isShowing()) {
				if(!checkNet){
					pd.dismiss();
					Toast.makeText(ChitsCreation.this, "Please check network connection", Toast.LENGTH_SHORT).show();
					return;
				}
				if(result!=null)
				{
					if (result.equalsIgnoreCase("success")) {

						Toast.makeText(getApplicationContext(),"Chit saved successfully", Toast.LENGTH_LONG).show();
						Intent chitsCreation = new Intent(ChitsCreation.this, ViewChits.class);
						startActivity(chitsCreation);
						finish();

					} 
					else {
						Toast.makeText(getApplicationContext(), mMessage,Toast.LENGTH_LONG).show();
					}
				}
				pd.dismiss();
			}
			super.onPostExecute(result);
		}
	}
	/*************************************************************** 

Project Name : NewChitFund

	 **************************************************************** 

Class Name : GetChitMembersAsynTask

 Date : 4/05/2014

 Developer : Veera manikanta reddy. G

 Description : saving the edit chit data to server


	 **************************************************************** 

Change History : 

Date : 

Developer : 

Change : 

	 ****************************************************************//**/

	class GetChitMembersAsynTask extends AsyncTask<Void, Void, String> {
		ProgressDialog pd;

		String mStatus, mMessage;
		private JSONObject jsonResponseMembers;
		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(ChitsCreation.this);
			pd.setIndeterminate(true);
			pd.setMessage("Loading chit members...");
			pd.setCancelable(false);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {

			try {
				checkNet = Utils.isInternetAvailable(ChitsCreation.this);
				if(checkNet){
					String URL = getResources().getString(R.string.URL);

					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(URL);
					MultipartEntity multiEntity = new MultipartEntity();
					if(fromFlagChangeMember == 3){
						multiEntity.addPart("call", new StringBody("editChitMembers"));
						multiEntity.addPart("chitID", new StringBody(chitId));
						multiEntity.addPart("oldMemberID", new StringBody(currentmemberId));
						multiEntity.addPart("chitMemberID", new StringBody(chitMemberId));
						multiEntity.addPart("newMemberID", new StringBody(selected_member_data.get(global_position).get_userID()));
					}
					else{
						multiEntity.addPart("call", new StringBody("getChitMembers"));
						multiEntity.addPart("chitID", new StringBody(chitId));
					}
					post.setEntity(multiEntity);
					HttpResponse response = client.execute(post);
					String res = EntityUtils.toString(response.getEntity());

					Log.v("responce", "Response===" + res);
					JSONObject json1 = new JSONObject(res);

					mStatus = json1.getString("Status");
					mMessage = json1.getString("Message");

					if (mStatus.equalsIgnoreCase("Success")) {
						jsonResponseMembers = new JSONObject(res);
						return "success";
					}
					if (!mMessage.equalsIgnoreCase("OK")) {
						return mMessage;
					}
				}


			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (pd.isShowing()) {
				if(!checkNet){
					pd.dismiss();
					Toast.makeText(ChitsCreation.this, "Please check network connection", Toast.LENGTH_SHORT).show();
					return;
				}
				if (result.equalsIgnoreCase("success")) {

					if(fromFlagChangeMember == 3){
						Toast.makeText(getApplicationContext(), "Member changed successfully",Toast.LENGTH_LONG).show();
					}else{
						try {
							selected_member_data.clear();
							JSONArray jsonArrayMembers = jsonResponseMembers.getJSONArray("Result");
							for (int i = 0; i < jsonArrayMembers.length(); i++) {
								JSONObject memberObj = jsonArrayMembers.getJSONObject(i);
								Member member = new Member();
								if(!memberObj.getString("isOwner").equalsIgnoreCase("Yes") ){
									member.set_name(memberObj.getString("name"));
									member.set_userID(memberObj.getString("memberID"));
									member.set_phone(memberObj.getString("chitMemberID"));//saving chit member id in phone field
									selected_member_data.add(member);
								}
							}
							if(selected_member_data.isEmpty()){
								int curTotalNum = Integer.parseInt(et_ChitMembers.getText().toString().trim());
								for(int i=0;i<(curTotalNum-1);i++)
								{
									selected_member_data.add(null);
								}
								checkNullMembers = true;
							}
							maddAdapter.notifyDataSetChanged();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} 
				else {
					Toast.makeText(getApplicationContext(), mMessage,Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
			}
			super.onPostExecute(result);
		}
	}
	public void onBackPressed() {
		if(onList){
			lv_memberList.setVisibility(View.GONE);
			et_MemberSearch.setVisibility(View.GONE);
			rl_memberView1.setVisibility(View.VISIBLE);
			onList = false;
		}else if(backBtnFlag){
			sv_AddView.setVisibility(View.VISIBLE);//showing main chit creation scrollview
			rl_owner.setVisibility(View.GONE);//hiding owner layout
			backBtnFlag = false;
			if(chitEditStartChit){
				iv_newChitDoneBtn.setVisibility(View.GONE);
			}else{
				iv_newChitDoneBtn.setVisibility(View.VISIBLE);
			}
		} else if(backBtnFlagEdit){
			backBtnFlagEdit = false;
			Intent chitsDetails = new Intent(ChitsCreation.this, ChitDetails.class);
			chitsDetails.putExtra("chit", chitDetails);
			startActivity(chitsDetails);
			finish();
		}else{
			Intent chitsCreation = new Intent(ChitsCreation.this, Categories.class);
			startActivity(chitsCreation);
			finish();
		}
	}
}

