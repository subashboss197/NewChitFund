/***************************************************************
 * 
 * Project Name : Chitfund
 * Class Name   : ChitDetails
 * DateTime     : 02-June-2014 11:15 AM 
 * Developer    : Durga Prasad B
 * Company      : Sagarsoft India Ltd.
 * 
 * Description  :
 * ------------------------------------------------------------- 
 * This class is used to display chit bidding details.
 * Here the screen functionality differentiated by his/her role.
 * Admin will get highest bid details with approve option. He/she
 * can view all other highest bid details of previous months.
 * User will get highest bid details with an option to bid the 
 * amount and he/she can view all other highest bid details of 
 * previous months.
 * Owner can see the previous months highest bid details but can't
 * bid and can't approve.  
 * -------------------------------------------------------------
 * 
 * *************************************************************/

package com.newchitfund.androidclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.newchitfund.businessobjects.Bids;
import com.newchitfund.businessobjects.Chit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BiddingDetails extends Activity {

	String chitID,chitBidDate,chitMemberID,chitOwnerID,chitOwnerMonth;

	Chit chitDetails = new Chit();

	// To store userType,userID shraredPreferences
	String userName,userType,userID;

	private JSONObject jsonResponse;

	ListView bidListView;

	// Storing all chits details into chits_data array for later retrieval.
	ArrayList<Bids> bids_data = new ArrayList<Bids>();

	JSONArray highestBidData;

	String inputBidDate,outputBidDate;

	RelativeLayout adminHighestBidLayout,userHighestBidLayout,userHighestBidLayout1; 
	TextView highestBidMemberName,highestBidAmount,highestBidDate;
	TextView MemberName,MemberName1,BidDate,BidAmount;

	TextView errorMsg;

	ImageView doneButton,bidButton;
	ImageView approveBidCheckBox;
	EditText newBidAmount;

	Button generatePaymentsBtn;

	Boolean isChecked = false;

	String bid_amount,user_id,bid_id;

	Long chitTotAmount,highBidAmount,enteredBidAmount;

	public boolean checkNet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bidding_list);

		errorMsg = (TextView) findViewById(R.id.tv_error_msg);
		errorMsg.setVisibility(View.GONE);

		Bundle bundle = getIntent().getExtras();
		if(bundle!=null)
		{
			chitDetails = (Chit) bundle.getSerializable("chit");
		}

		chitID = chitDetails.get_chitID();
		chitOwnerID = chitDetails.get_ownerID();

		SimpleDateFormat chitOwnerMonthInputFormat = new SimpleDateFormat("MMMM-yyyy");
		SimpleDateFormat chitOwnerMonthOutputFormat = new SimpleDateFormat("yyyy-MM");

		chitOwnerMonth = chitDetails.get_ownerMonth();

		try {
			Date date = chitOwnerMonthInputFormat.parse(chitDetails.get_ownerMonth());
			chitOwnerMonth = chitOwnerMonthOutputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		chitTotAmount = Long.parseLong(chitDetails.get_chitAmount().toString());

		SimpleDateFormat chitDateInputFormat = new SimpleDateFormat("d/M/yyyy");
		SimpleDateFormat chitDateOutputFormat = new SimpleDateFormat("dd/MM/yy");

		chitBidDate = chitDetails.get_bidDate();

		try {
			Date date = chitDateInputFormat.parse(chitDetails.get_bidDate());
			chitBidDate = chitDateOutputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		bidListView = (ListView)findViewById(R.id.lv_bidding_list);
		highestBidMemberName = (TextView)findViewById(R.id.tv_highest_bid_member_name);
		highestBidMemberName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		highestBidDate = (TextView)findViewById(R.id.tv_highest_bid_date);
		highestBidDate.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		highestBidAmount = (TextView)findViewById(R.id.tv_highest_bid_amount);
		highestBidAmount.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));

		MemberName = (TextView)findViewById(R.id.tv_highest_bid_memberName);
		MemberName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		BidAmount = (TextView)findViewById(R.id.tv_highest_bidAmount);
		BidAmount.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		BidDate = (TextView)findViewById(R.id.tv_highest_bidDate);
		BidDate.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		MemberName1 = (TextView)findViewById(R.id.tv_user_highest_bid_memberName);
		MemberName1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));

		adminHighestBidLayout = (RelativeLayout)findViewById(R.id.rl_admin_highest_bid_block);
		userHighestBidLayout = (RelativeLayout)findViewById(R.id.rl_user_highest_bid_block);
		userHighestBidLayout1 = (RelativeLayout)findViewById(R.id.rl_user_highest_bid_block_1);

		doneButton = (ImageView)findViewById(R.id.iv_bidding_list_done_button);
		bidButton = (ImageView)findViewById(R.id.iv_user_highest_bid_submit_button);
		generatePaymentsBtn = (Button)findViewById(R.id.btn_generate_payments);

		newBidAmount = (EditText)findViewById(R.id.et_user_highest_bidAmount);
		approveBidCheckBox = (ImageView)findViewById(R.id.chk_approve_bid);

		userHighestBidLayout.setVisibility(View.GONE);
		userHighestBidLayout1.setVisibility(View.GONE);
		adminHighestBidLayout.setVisibility(View.GONE);
		doneButton.setVisibility(View.GONE);
		generatePaymentsBtn.setVisibility(View.GONE);


		TextView actionBartitle = (TextView) findViewById(R.id.tv_bidding_list_action_bar_title);
		actionBartitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));

		TextView title = (TextView) findViewById(R.id.tv_bidding_list_title);
		title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));

		//Storing sharedPreferences values into userType,userID
		SharedPreferences sharedpreferences = getSharedPreferences(SessionStorage.ACCOUNT_PREFS_NAME, Context.MODE_PRIVATE);
		userType = sharedpreferences.getString("MEMBER_ROLE", null);
		userID = sharedpreferences.getString("MEMBER_ID", null);
		userName = sharedpreferences.getString("MEMBER_NAME", "");

		// userType = "USER";

		doneButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				checkNet = Utils.isInternetAvailable(BiddingDetails.this);

				if(!checkNet)
				{
					Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Please check network connection", BiddingDetails.this);
				}
				else
				{
					if((userType!=null && userType.equalsIgnoreCase("ADMIN")) || (userType!=null && userType.equalsIgnoreCase("USER") && userID.equals(chitOwnerID)))
					{
						if(isChecked)
						{

							AlertDialog.Builder dlgAlert = new AlertDialog.Builder(BiddingDetails.this);
							dlgAlert.setMessage("Are you sure you want approve the bid?");
							dlgAlert.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {

									JSONObject bidDataObj;

									try {
										bidDataObj = highestBidData.getJSONObject(0);

										if(bidDataObj.length()>0)
										{
											bid_id = bidDataObj.getString("id");

											new ApproveBid().execute();
											//Toast.makeText(getApplicationContext(), "MemberID "+bidDataObj.getString("memberID")+"  Bid Id "+bidDataObj.getString("id"), Toast.LENGTH_SHORT).show();
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}

								}
							});
							dlgAlert.setNegativeButton("Cancel", null);
							dlgAlert.setCancelable(true);
							dlgAlert.create().show();
						}
						else
						{
							//Toast.makeText(getApplicationContext(), "Select checkbox to approve.", Toast.LENGTH_SHORT).show();
							CommonMethods.showErrorDialog("Please select the bid to be approved.",BiddingDetails.this);
						}

					}
					else if(userType!=null && userType.equalsIgnoreCase("USER"))
					{
						//Toast.makeText(getApplicationContext(), "User", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		approveBidCheckBox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!isChecked)
				{
					approveBidCheckBox.setImageResource(R.drawable.checkbox_checked);
					isChecked = true;
				}
				else
				{
					approveBidCheckBox.setImageResource(R.drawable.checkbox_unchecked);
					isChecked = false;
				}
			}
		});

		bidButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				hideSoftKeyboard(BiddingDetails.this);

				checkNet = Utils.isInternetAvailable(BiddingDetails.this);

				if(!checkNet)
				{
					Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Please check network connection", BiddingDetails.this);
				}
				else
				{
					bid_amount = newBidAmount.getText().toString();

					if(bid_amount==null || bid_amount.equals(""))
						enteredBidAmount = Long.parseLong("0");
					else
						enteredBidAmount = Long.parseLong(newBidAmount.getText().toString());

					//Toast.makeText(getApplicationContext(), "BidAmount "+enteredBidAmount, Toast.LENGTH_SHORT).show();

					if(bid_amount==null || bid_amount.equals(""))
					{
						//Toast.makeText(getApplicationContext(), "Amount should not empty", Toast.LENGTH_SHORT).show();
						CommonMethods.showErrorDialog("Bid amount should not empty",BiddingDetails.this);
					}
					else if(enteredBidAmount<=highBidAmount)
					{
						//Toast.makeText(getApplicationContext(), "Amount should greaterthan "+getResources().getString(R.string.Rupee)+" "+highBidAmount, Toast.LENGTH_SHORT).show();
						CommonMethods.showErrorDialog("Bid amount should be greater than the highest bid.("+getResources().getString(R.string.Rupee)+" "+highBidAmount+")",BiddingDetails.this);
					}
					else if(enteredBidAmount>=chitTotAmount)
					{
						//Toast.makeText(getApplicationContext(), "Amount should lessthan "+getResources().getString(R.string.Rupee)+" "+chitTotAmount, Toast.LENGTH_SHORT).show();
						CommonMethods.showErrorDialog("Bid amount should be less than the chit amount.("+getResources().getString(R.string.Rupee)+" "+chitTotAmount+")",BiddingDetails.this);
					}
					else
					{
						new UpdateBidAmount().execute();
					}
				}
			}
		});

		generatePaymentsBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new GeneratePayments().execute();
			}
		});

		new GetAllBids().execute();
	}

	public void closeList(View v){
		finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}

	public void goBack(View v){
		finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}

	public void refresh(View v) {
		/*
		Intent refreshActivityIntent = new Intent(this,BiddingDetails.class);
		refreshActivityIntent.putExtra("chit", chitDetails);
		startActivity(refreshActivityIntent);
		finish();
		*/
		new GetAllBids().execute();
	}

	/**
	 * Method Name : goHome 
	 * Method Name : User defined
	 * @param v    : View Object
	 * Return Type : void
	 * 
	 * Description : This method is used to redirect the screen
	 * to Categories Screen.
	 */
	public void goHome(View v)
	{
		Intent homeActivityIntent = new Intent(this,Categories.class);
		homeActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(homeActivityIntent);
		finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}

	/* Method to hide keyboard start */
	public static void hideSoftKeyboard(Activity activity) {

		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (inputMethodManager.isAcceptingText()) {
			inputMethodManager.hideSoftInputFromWindow(activity
					.getCurrentFocus().getWindowToken(), 0);
		}

	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}

	/***************************************************************
	 * 
	 * Project Name : Chitfund
	 * Class Name   : GetAllChits
	 * DateTime     : 27-May-2014 05:00 PM 
	 * Developer    : Durga Prasad B
	 * Company      : Sagarsoft India Ltd.
	 * 
	 * Description  :
	 * ------------------------------------------------------------- 
	 * This class is used to get all chits from REST webservice by
	 * extending AsyncTask.
	 * 
	 * Depends upon user role service url will be changed and the
	 * list will fetch from webservice and display in listview. 
	 * -------------------------------------------------------------
	 * 
	 * *************************************************************/

	class GetAllBids extends AsyncTask<Void, Void, String>
	{
		private String Error = null;
		ProgressDialog pd;
		String res;
		String sStatus=null, sMessage=null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(BiddingDetails.this);
			pd.setMessage("Loading bid details, Please wait..");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {

				checkNet = Utils.isInternetAvailable(BiddingDetails.this);

				if(checkNet){
					String url;

					if(userType.equalsIgnoreCase("USER"))
						url= getResources().getString(R.string.URL)+"?call=getBidDetails&chitID="+chitID+"&userID="+userID;
					else
						url= getResources().getString(R.string.URL)+"?call=getBidDetails&chitID="+chitID;

					try {
						HttpClient client = new DefaultHttpClient();
						HttpPost postAccept = new HttpPost(url);
						HttpResponse response = client.execute(postAccept);
						res = EntityUtils.toString(response.getEntity());
					}
					catch (Exception e) {
						Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
						//CommonMethods.showErrorDialog(e.getMessage(), BiddingDetails.this);
					}

					jsonResponse = new JSONObject(res);

					sStatus = jsonResponse.getString("Status");
					sMessage = jsonResponse.getString("Message");

					if(sStatus.equalsIgnoreCase("Success")) {
						return "success";
					}

					if(!sMessage.equalsIgnoreCase("OK")) {
						return sMessage;
					}
				}
				else
				{
					return "NO_INTERNET";
				}

			}catch (Exception e) {
				Error=e.getMessage();
			}
			return "SERVICE_ERROR";
		}

		@Override
		protected void onPostExecute(String result) {

			if(pd.isShowing())
			{
				if(!checkNet)
				{
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Please check network connection", BiddingDetails.this);
					finish();
				}

				JSONObject resultObject;
				JSONArray jsonArray = null;

				try {
					resultObject = jsonResponse.getJSONObject("Result");
					jsonArray =resultObject.getJSONArray("approvedBid");
					highestBidData =resultObject.getJSONArray("highestBid");

					if(resultObject.has("chitMemberID"))
						chitMemberID = resultObject.getString("chitMemberID").toString();
					else
						chitMemberID = "0";
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}


				if(result.equalsIgnoreCase("success")) {

					String today = new SimpleDateFormat("dd/MM/yy").format(new Date());
					String thisMonth = new SimpleDateFormat("yyyy-MM").format(new Date());

					//Toast.makeText(getApplicationContext(), "TD: "+today+" BD:"+chitBidDate, Toast.LENGTH_LONG).show();

					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy");

					try
					{
						bids_data.clear();
						
						String bidApproveStatus = "0";
						
						if(highestBidData.length()>0)
						{
							if(userType!=null && userType.equalsIgnoreCase("ADMIN"))
							{
								if(today.equals(chitBidDate) && !thisMonth.equals(chitOwnerMonth))
								{
									adminHighestBidLayout.setVisibility(View.VISIBLE);
									doneButton.setVisibility(View.VISIBLE);
								}
								else
								{
									adminHighestBidLayout.setVisibility(View.GONE);
									doneButton.setVisibility(View.GONE);
								}
							}
							else if(userType!=null && userType.equalsIgnoreCase("USER"))
							{
								doneButton.setVisibility(View.GONE);

								if(today.equals(chitBidDate) && !thisMonth.equals(chitOwnerMonth))
								{
									userHighestBidLayout.setVisibility(View.VISIBLE);
									userHighestBidLayout1.setVisibility(View.VISIBLE);

									if(userID.equals(chitOwnerID))
									{
										userHighestBidLayout.setVisibility(View.GONE);
										adminHighestBidLayout.setVisibility(View.VISIBLE);
										doneButton.setVisibility(View.VISIBLE);
									}
								}
								else
								{
									userHighestBidLayout.setVisibility(View.GONE);
									userHighestBidLayout1.setVisibility(View.GONE);

									if(userID.equals(chitOwnerID))
									{
										generatePaymentsBtn.setVisibility(View.VISIBLE);
									}
								}
							}

							JSONObject highBidObj = highestBidData.getJSONObject(0);
							
							bidApproveStatus = highBidObj.getString("bidStatus");

							highestBidMemberName.setText(highBidObj.getString("name"));
							MemberName.setText("("+highBidObj.getString("name")+")");
							MemberName1.setText(userName);

							String highBidDate= highBidObj.getString("bidDate");

							try {
								Date date = inputFormat.parse(highBidDate);
								highBidDate = outputFormat.format(date);
							} catch (ParseException e) {
								e.printStackTrace();
							}

							highestBidDate.setText("Bid Date: "+highBidDate);
							BidDate.setText("Bid Date: "+highBidDate);

							highestBidAmount.setText(getResources().getString(R.string.Rupee)+" "+highBidObj.getString("bidAmount"));
							BidAmount.setText("Highest Bid: "+getResources().getString(R.string.Rupee)+" "+highBidObj.getString("bidAmount"));

							highBidAmount = Long.parseLong(highBidObj.getString("bidAmount").toString());
							
							if(bidApproveStatus.equalsIgnoreCase("APPROVED"))
							{
								adminHighestBidLayout.setVisibility(View.GONE);
								userHighestBidLayout.setVisibility(View.GONE);
								userHighestBidLayout1.setVisibility(View.GONE);
								doneButton.setVisibility(View.GONE);
							}
						}
						else
						{
							adminHighestBidLayout.setVisibility(View.GONE);
							userHighestBidLayout.setVisibility(View.GONE);
							if(today.equals(chitBidDate) && !thisMonth.equals(chitOwnerMonth))
							{
								if(userType!=null && userType.equalsIgnoreCase("USER"))
								{
									userHighestBidLayout1.setVisibility(View.VISIBLE);
									highBidAmount = Long.parseLong("0");
									MemberName1.setText(userName);
								}
							}
							else if(today.equals(chitBidDate) && thisMonth.equals(chitOwnerMonth))
							{
								if(userType!=null && userType.equalsIgnoreCase("ADMIN"))
								{
									new CheckGenerateButton().execute();
								}
								else if(userType!=null && userType.equalsIgnoreCase("USER") && userID.equals(chitOwnerID))
								{
									new CheckGenerateButton().execute();
								}

								userHighestBidLayout1.setVisibility(View.GONE);
							}
							else
								userHighestBidLayout1.setVisibility(View.GONE);

							doneButton.setVisibility(View.GONE);
						}

						//Toast.makeText(getApplicationContext(), "ChitOwnerID: "+chitOwnerID+" userID: "+userID, Toast.LENGTH_SHORT).show();

						if(chitMemberID.equals("0") || userID.equals(chitOwnerID))
							userHighestBidLayout1.setVisibility(View.GONE);



						//Log.v("Result Set:", jsonArray.toString());

						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject resultObj = jsonArray.getJSONObject(i);
							Bids bid = new Bids();

							bid.set_bidID(resultObj.getString("id"));
							bid.set_memberID(resultObj.getString("memberID"));
							bid.set_memberName(resultObj.getString("name"));
							bid.set_bidAmount(resultObj.getString("bidAmount"));

							inputBidDate= resultObj.getString("bidDate");
							outputBidDate = resultObj.getString("bidDate");

							try {
								Date date = inputFormat.parse(inputBidDate);
								outputBidDate = outputFormat.format(date);
							} catch (ParseException e) {
								e.printStackTrace();
							}

							bid.set_bidDate(outputBidDate);
							bid.set_bidStatus(resultObj.getString("bidStatus"));

							bids_data.add(bid);
						}
					}catch (Exception e) {
						e.printStackTrace();
					}

					Bids_Adapter bidsAdapter = new Bids_Adapter(BiddingDetails.this, R.layout.custom_bid_list,
							bids_data);
					bidListView.setAdapter(bidsAdapter);
					bidsAdapter.notifyDataSetChanged();

					pd.dismiss();
				}
				else if(result.equalsIgnoreCase("NO_INTERNET")) {
					adminHighestBidLayout.setVisibility(View.GONE);
					userHighestBidLayout.setVisibility(View.GONE);
					userHighestBidLayout1.setVisibility(View.GONE);

					doneButton.setVisibility(View.GONE);

					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Please check network connection", BiddingDetails.this);				
				}
				else if(result.equalsIgnoreCase("SERVICE_ERROR")) {
					adminHighestBidLayout.setVisibility(View.GONE);
					userHighestBidLayout.setVisibility(View.GONE);
					userHighestBidLayout1.setVisibility(View.GONE);

					doneButton.setVisibility(View.GONE);

					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Service error,please contact administrator", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Service error,please contact administrator", BiddingDetails.this);				
				}
				else if(result.equalsIgnoreCase("Bid not found"))
				{
					adminHighestBidLayout.setVisibility(View.GONE);
					userHighestBidLayout.setVisibility(View.GONE);
					
					errorMsg.setText("No bidding details found.");

					String today = new SimpleDateFormat("dd/MM/yy").format(new Date());
					String thisMonth = new SimpleDateFormat("yyyy-MM").format(new Date());

					if(today.equals(chitBidDate) && !thisMonth.equals(chitOwnerMonth))
					{
						if(userType!=null && userType.equalsIgnoreCase("USER"))
						{
							userHighestBidLayout1.setVisibility(View.VISIBLE);
							highBidAmount = Long.parseLong("0");
							MemberName1.setText(userName);
						}
					}
					else if(today.equals(chitBidDate) && thisMonth.equals(chitOwnerMonth))
					{
						if(userType!=null && userType.equalsIgnoreCase("ADMIN"))
							new CheckGenerateButton().execute();
						else if(userType!=null && userType.equalsIgnoreCase("USER") && userID.equals(chitOwnerID))
							new CheckGenerateButton().execute();
					}
					else
					{
						userHighestBidLayout1.setVisibility(View.GONE);
					}

					doneButton.setVisibility(View.GONE);

					if(chitMemberID.equals("0"))
					{
						userHighestBidLayout1.setVisibility(View.GONE);
						pd.dismiss();

						if(userType!=null && userType.equalsIgnoreCase("ADMIN") && !thisMonth.equals(chitOwnerMonth))
						{
							errorMsg.setText("No bid for apporve / No bids data found.");
							//Toast.makeText(getApplicationContext(), "No bid for apporve / No bids data found.", Toast.LENGTH_SHORT).show();
							//CommonMethods.showErrorDialog("No bid for apporve / No bids data found.", BiddingDetails.this);
						}
					}
					else if(userID.equals(chitOwnerID))
					{
						userHighestBidLayout1.setVisibility(View.GONE);
						pd.dismiss();
						errorMsg.setText("No bidding details found.");
						//Toast.makeText(getApplicationContext(), "No bids data found.", Toast.LENGTH_SHORT).show();
						//CommonMethods.showErrorDialog("No bids data found.", BiddingDetails.this);
					}

					errorMsg.setVisibility(View.VISIBLE);

					pd.dismiss();

				}
				else {
					adminHighestBidLayout.setVisibility(View.GONE);
					userHighestBidLayout.setVisibility(View.GONE);
					userHighestBidLayout1.setVisibility(View.GONE);

					doneButton.setVisibility(View.GONE);

					TextView errorMsg = (TextView) findViewById(R.id.tv_error_msg);
					errorMsg.setText(result);

					pd.dismiss();

					//CommonMethods.showErrorDialog(result, BiddingDetails.this);
					//Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();				
				}
			}

			super.onPostExecute(result);
		}
	}


	public class Bids_Adapter extends ArrayAdapter<Bids> {
		Activity activity;
		int layoutResourceId;
		Bids bid;
		ArrayList<Bids> data = new ArrayList<Bids>();

		public Bids_Adapter(Activity act, int layoutResourceId,
				ArrayList<Bids> data) {
			super(act, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.activity = act;
			this.data = data;
			//notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			BidHolder holder = null;

			if (row == null) {
				LayoutInflater inflater = LayoutInflater.from(activity);
				row = inflater.inflate(layoutResourceId, parent, false);

				holder = new BidHolder();
				holder.tv_memberName = (TextView) row.findViewById(R.id.tv_custom_bid_list_member_name);
				holder.tv_memberName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
				holder.tv_bidDate = (TextView) row.findViewById(R.id.tv_custom_bid_list_bid_date);
				holder.tv_bidDate.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
				holder.tv_bidAmount = (TextView) row.findViewById(R.id.tv_custom_bid_list_high_amount);
				holder.tv_bidAmount.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));

				row.setTag(holder);
			} else {
				holder = (BidHolder) row.getTag();
			}
			bid = data.get(position);

			holder.tv_memberName.setText(bid.get_memberName());
			holder.tv_bidDate.setText("Bid Date: "+bid.get_bidDate());
			holder.tv_bidAmount.setText(getResources().getString(R.string.Rupee)+" "+bid.get_bidAmount());

			return row;

		}

		class BidHolder {
			TextView tv_memberName;
			TextView tv_bidDate;
			TextView tv_bidAmount;
		}

	}

	class ApproveBid extends AsyncTask<Void, Void, String>
	{
		private String Error = null;
		ProgressDialog pd;
		String res;
		String sStatus=null, sMessage=null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(BiddingDetails.this);
			pd.setMessage("Bid approving...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {

				checkNet = Utils.isInternetAvailable(BiddingDetails.this);

				if(checkNet){

					String url;

					url= getResources().getString(R.string.URL)+"?call=approveBid&bidID="+bid_id;

					try {
						HttpClient client = new DefaultHttpClient();
						HttpPost postAccept = new HttpPost(url);
						HttpResponse response = client.execute(postAccept);
						res = EntityUtils.toString(response.getEntity());
					}
					catch (Exception e) {
						Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
						//CommonMethods.showErrorDialog(e.getMessage(), BiddingDetails.this);
					}

					jsonResponse = new JSONObject(res);

					sStatus = jsonResponse.getString("Status");
					sMessage = jsonResponse.getString("Message");

					if(sStatus.equalsIgnoreCase("Success")) {
						return "success";
					}

					if(!sMessage.equalsIgnoreCase("OK")) {
						return sMessage;
					}
				}
				else
				{
					return "NO_INTERNET";
				}

			}catch (Exception e) {
				Error=e.getMessage();
			}
			return "SERVICE_ERROR";
		}

		@Override
		protected void onPostExecute(String result) {

			if(pd.isShowing())
			{
				if(!checkNet)
				{
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Please check network connection.", BiddingDetails.this);
					finish();
				}

				if(result.equalsIgnoreCase("success")) {

					//Intent refreshIntent = new Intent(BiddingDetails.this, BiddingDetails.class);
					//refreshIntent.putExtra("chit", chitDetails);
					//startActivity(refreshIntent);
					finish();
					overridePendingTransition(R.anim.fadein, R.anim.fadeout);
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Bid approved successfully.", Toast.LENGTH_LONG).show();
				}
				else if(result.equalsIgnoreCase("NO_INTERNET")) {
					pd.dismiss();
					doneButton.setVisibility(View.GONE);
					Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Please check network connection", BiddingDetails.this);				
				}
				else if(result.equalsIgnoreCase("SERVICE_ERROR")) {
					pd.dismiss();
					doneButton.setVisibility(View.GONE);
					Toast.makeText(getApplicationContext(), "Service error,please contact administrator", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Service error,please contact administrator", BiddingDetails.this);				
				}
				else {
					pd.dismiss();
					doneButton.setVisibility(View.GONE);
					Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
					//Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();				
				}
			}

			super.onPostExecute(result);
		}
	}

	class UpdateBidAmount extends AsyncTask<Void, Void, String>
	{
		private String Error = null;
		ProgressDialog pd;
		String res;
		String sStatus=null, sMessage=null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(BiddingDetails.this);
			pd.setMessage("Submitting Bid Amount,Please wait...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {

				checkNet = Utils.isInternetAvailable(BiddingDetails.this);

				if(checkNet){

					String url;

					String todayBidDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					String thisMonth = new SimpleDateFormat("yyyy-MM").format(new Date());

					url= getResources().getString(R.string.URL)+"?call=addBid&chitID="+chitID+"&memberID="+userID+"&chitMemberID="
							+chitMemberID+"&amount="+enteredBidAmount+"&bidDate="+todayBidDate;

					try {
						HttpClient client = new DefaultHttpClient();
						HttpPost postAccept = new HttpPost(url);
						HttpResponse response = client.execute(postAccept);
						res = EntityUtils.toString(response.getEntity());
					}
					catch (Exception e) {
						Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
						//CommonMethods.showErrorDialog(e.getMessage(), BiddingDetails.this);
					}

					jsonResponse = new JSONObject(res);

					sStatus = jsonResponse.getString("Status");
					sMessage = jsonResponse.getString("Message");

					if(sStatus.equalsIgnoreCase("Success")) {
						return "success";
					}

					if(!sMessage.equalsIgnoreCase("OK")) {
						return sMessage;
					}
				}
				else
				{
					return "NO_INTERNET";
				}

			}catch (Exception e) {
				Error=e.getMessage();
			}
			return "SERVICE_ERROR";
		}

		@Override
		protected void onPostExecute(String result) {

			if(pd.isShowing())
			{
				if(!checkNet)
				{
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Please check network connection", BiddingDetails.this);
					finish();
				}

				if(result.equalsIgnoreCase("success")) {

					//Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_LONG).show();

					//Intent refreshIntent = new Intent(BiddingDetails.this, BiddingDetails.class);
					//refreshIntent.putExtra("chit", chitDetails);
					//startActivity(refreshIntent);
					//finish();

					pd.dismiss();
					newBidAmount.setText("");
					new GetAllBids().execute();
				}
				else if(result.equalsIgnoreCase("NO_INTERNET")) {
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Please check network connection", BiddingDetails.this);				
				}
				else if(result.equalsIgnoreCase("SERVICE_ERROR")) {
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Service error,please contact administrator", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Service error,please contact administrator", BiddingDetails.this);				
				}
				else if(result.contains("greater"))
				{
					new GetAllBids().execute();
					pd.dismiss();
					Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
									
				}
				else {
					pd.dismiss();
					Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();				
				}
			}

			super.onPostExecute(result);
		}
	}	

	class CheckGenerateButton extends AsyncTask<Void, Void, String>
	{
		private String Error = null;
		ProgressDialog pd;
		String res;
		String sStatus=null, sMessage=null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(BiddingDetails.this);
			pd.setMessage("Please wait...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {

				checkNet = Utils.isInternetAvailable(BiddingDetails.this);

				if(checkNet){

					String url;

					url= getResources().getString(R.string.URL)+"?call=isChitPaymentExist&chitID="+chitID;

					try {
						HttpClient client = new DefaultHttpClient();
						HttpPost postAccept = new HttpPost(url);
						HttpResponse response = client.execute(postAccept);
						res = EntityUtils.toString(response.getEntity());
					}
					catch (Exception e) {
						Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
						//CommonMethods.showErrorDialog(e.getMessage(), BiddingDetails.this);
					}

					jsonResponse = new JSONObject(res);

					sStatus = jsonResponse.getString("Status");
					sMessage = jsonResponse.getString("Message");

					if(sStatus.equalsIgnoreCase("Success")) {
						return "success";
					}

					if(!sMessage.equalsIgnoreCase("OK")) {
						return sMessage;
					}
				}
				else
				{
					return "NO_INTERNET";
				}

			}catch (Exception e) {
				Error=e.getMessage();
			}
			return "SERVICE_ERROR";
		}

		@Override
		protected void onPostExecute(String result) {

			if(pd.isShowing())
			{
				if(!checkNet)
				{
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Please check network connection", BiddingDetails.this);
					finish();
				}

				if(result.equalsIgnoreCase("success")) {

					try {
						JSONObject existResult = jsonResponse.getJSONObject("Result");
						String buttonExists =existResult.getString("exist");

						if(buttonExists.equals("0"))
							generatePaymentsBtn.setVisibility(View.VISIBLE);

					} catch (JSONException e) {
						e.printStackTrace();
					}

					pd.dismiss();
				}
				else if(result.equalsIgnoreCase("NO_INTERNET")) {
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Please check network connection", BiddingDetails.this);				
				}
				else if(result.equalsIgnoreCase("SERVICE_ERROR")) {
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Service error,please contact administrator", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Service error,please contact administrator", BiddingDetails.this);				
				}
				else {
					pd.dismiss();
					Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();				
				}
			}

			super.onPostExecute(result);
		}
	}	

	class GeneratePayments extends AsyncTask<Void, Void, String>
	{
		private String Error = null;
		ProgressDialog pd;
		String res;
		String sStatus=null, sMessage=null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(BiddingDetails.this);
			pd.setMessage("Generating payments, Please wait...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {

				checkNet = Utils.isInternetAvailable(BiddingDetails.this);

				if(checkNet){

					String url;

					String todayBidDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					String thisMonth = new SimpleDateFormat("yyyy-MM").format(new Date());

					url= getResources().getString(R.string.URL)+"?call=generateOwnerMonthPayments&chitID="+chitID;

					try {
						HttpClient client = new DefaultHttpClient();
						HttpPost postAccept = new HttpPost(url);
						HttpResponse response = client.execute(postAccept);
						res = EntityUtils.toString(response.getEntity());
					}
					catch (Exception e) {
						Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
						//CommonMethods.showErrorDialog(e.getMessage(), BiddingDetails.this);
					}

					jsonResponse = new JSONObject(res);

					sStatus = jsonResponse.getString("Status");
					sMessage = jsonResponse.getString("Message");

					if(sStatus.equalsIgnoreCase("Success")) {
						return "success";
					}

					if(!sMessage.equalsIgnoreCase("OK")) {
						return sMessage;
					}
				}
				else
				{
					return "NO_INTERNET";
				}

			}catch (Exception e) {
				Error=e.getMessage();
			}
			return "SERVICE_ERROR";
		}

		@Override
		protected void onPostExecute(String result) {

			if(pd.isShowing())
			{
				if(!checkNet)
				{
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Please check network connection", BiddingDetails.this);
					finish();
				}

				if(result.equalsIgnoreCase("success")) {

					/*
					Intent refreshIntent = new Intent(BiddingDetails.this, Payment.class);
					refreshIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					refreshIntent.putExtra("chit", chitDetails);
					startActivity(refreshIntent);
					finish();
					 */

					generatePaymentsBtn.setVisibility(View.GONE);
					CommonMethods.showErrorDialog("Payments generated successfully.",BiddingDetails.this);
					pd.dismiss();
				}
				else if(result.equalsIgnoreCase("NO_INTERNET")) {
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Please check network connection", BiddingDetails.this);				
				}
				else if(result.equalsIgnoreCase("SERVICE_ERROR")) {
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Service error,please contact administrator", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Service error,please contact administrator", BiddingDetails.this);				
				}
				/*
				else if(result.equalsIgnoreCase("Today is not bid date."))
				{
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Device date/time changed.", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Device date/time changed.", BiddingDetails.this);				
				}
				 */
				else {
					pd.dismiss();
					Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();				
				}
			}

			super.onPostExecute(result);
		}
	}	

}
