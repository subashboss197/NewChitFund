/***************************************************************
 * 
 * Project Name : Chitfund
 * Class Name   : ChitDetails
 * DateTime     : 28-May-2014 10:15 AM 
 * Developer    : Durga Prasad B
 * Company      : Sagarsoft India Ltd.
 * 
 * Description  :
 * ------------------------------------------------------------- 
 * This class is used to display chit full details.
 * Here user will have the options differentiated by his/her role.
 * Admin will have Bidding,Members and Report Buttons.
 * Owner will have Bidding,Members and Payment History Buttons.
 * User will have Bidding button and Payment History button. 
 * 
 * On clicking Bidding button it redirects to Bidding List.
 * On clicking Members button it redirects to Members List.
 * On clicking Reports he/she will get the mail with chit details. 
 * On clicking Payment History button it redirects to Payment List.
 * -------------------------------------------------------------
 * 
 * *************************************************************/

package com.newchitfund.androidclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.newchitfund.businessobjects.Chit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChitDetails extends Activity{
	Chit chitDetails = new Chit();

	private Boolean fromChitHistory = false;
	private ImageView ivEdit;

	// To store userType shraredPreferences
	String userType,userID;

	String chitID,chitOwnerID;

	TextView chitName,ownerName,totalAmount,noOfMembers,noOfMonths,startDate,payDate,bidDate,ownerMonth;

	TextView chitNameLabel,ownerNameLabel,totalAmountLabel,noOfMembersLabel,noOfMonthsLabel;
	TextView startDateLabel,payDateLabel,bidDateLabel,ownerMonthLabel;

	ImageView biddingButton,membersButton,reportsButton,paymentsHistoryButton;

	ImageView biddingListCloseButton,membersListCloseButton;

	public boolean checkNet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.chit_details);

		chitNameLabel = (TextView)findViewById(R.id.tv_chitName_label);
		chitNameLabel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		ownerNameLabel = (TextView)findViewById(R.id.tv_ownerName_label);
		ownerNameLabel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		totalAmountLabel = (TextView)findViewById(R.id.tv_totalAmount_label);
		totalAmountLabel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		noOfMembersLabel = (TextView)findViewById(R.id.tv_noOfMembers_label);
		noOfMembersLabel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		noOfMonthsLabel = (TextView)findViewById(R.id.tv_noOfMonths_label);
		noOfMonthsLabel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		startDateLabel = (TextView)findViewById(R.id.tv_startDate_label);
		startDateLabel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		payDateLabel = (TextView)findViewById(R.id.tv_payDate_label);
		payDateLabel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		bidDateLabel = (TextView)findViewById(R.id.tv_bidDate_label);
		bidDateLabel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
		ownerMonthLabel = (TextView)findViewById(R.id.tv_ownerMonth_label);
		ownerMonthLabel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));

		chitName = (TextView)findViewById(R.id.tv_chitName);
		ownerName = (TextView)findViewById(R.id.tv_ownerName);
		totalAmount = (TextView)findViewById(R.id.tv_totalAmount);
		noOfMembers = (TextView)findViewById(R.id.tv_noOfMembers);
		noOfMonths = (TextView)findViewById(R.id.tv_noOfMonths);
		startDate = (TextView)findViewById(R.id.tv_startDate);
		payDate = (TextView)findViewById(R.id.tv_payDate);
		bidDate = (TextView)findViewById(R.id.tv_bidDate);
		ownerMonth = (TextView)findViewById(R.id.tv_ownerMonth);

		biddingButton = (ImageView)findViewById(R.id.iv_Bidding_button);
		membersButton= (ImageView)findViewById(R.id.iv_Members_button);
		reportsButton = (ImageView)findViewById(R.id.iv_Reports_button);
		paymentsHistoryButton = (ImageView)findViewById(R.id.iv_Payment_History_button);
		ivEdit = (ImageView)findViewById(R.id.iv_chit_details_edit_button);

		Bundle bundle = getIntent().getExtras();
		if(bundle!=null)
		{
			chitDetails = (Chit) bundle.getSerializable("chit");

			// If user came from Chits History then hide Edit button
			fromChitHistory = (Boolean) bundle.get("chitsHistory");

			if(fromChitHistory!=null && fromChitHistory){
				ivEdit.setVisibility(View.INVISIBLE);
			}


			TextView title = (TextView) findViewById(R.id.tv_chit_details_action_bar_title);
			title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));

			chitID = chitDetails.get_chitID();
			chitOwnerID = chitDetails.get_ownerID();
			chitName.setText(chitDetails.get_chitName());
			chitName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
			ownerName.setText(chitDetails.get_ownerName());
			ownerName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
			totalAmount.setText(getResources().getString(R.string.Rupee)+" "+chitDetails.get_chitAmount());
			totalAmount.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
			noOfMembers.setText(chitDetails.get_noOfMembers());
			noOfMembers.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
			noOfMonths.setText(chitDetails.get_noOfMonths());
			noOfMonths.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));

			payDate.setText(chitDetails.get_paymentDate());
			payDate.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
			bidDate.setText(chitDetails.get_bidDate());
			bidDate.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));

			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM");
			SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM-yyyy");

			Date date;
			String outPutStartDate = null;
			try
			{
				date = inputFormat.parse(chitDetails.get_startDate());
				outPutStartDate = outputFormat.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			startDate.setText(outPutStartDate);
			startDate.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
			ownerMonth.setText(chitDetails.get_ownerMonth());
			ownerMonth.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));

			//Storing sharedPreferences values into userType
			SharedPreferences sharedpreferences = getSharedPreferences(SessionStorage.ACCOUNT_PREFS_NAME, Context.MODE_PRIVATE);
			userType = sharedpreferences.getString("MEMBER_ROLE", null);
			userID = sharedpreferences.getString("MEMBER_ID", null);

			//userType = "ADMIN";

			if(!chitOwnerID.equals(userID) && !userType.equalsIgnoreCase("ADMIN"))
			{
				ivEdit.setVisibility(View.INVISIBLE);
			}

			if(userType.equalsIgnoreCase("ADMIN")){

				paymentsHistoryButton.setVisibility(View.GONE);
			}else if(userID.equals(chitOwnerID)){

				reportsButton.setVisibility(View.GONE);
			}else
			{
				membersButton.setVisibility(View.GONE);
				reportsButton.setVisibility(View.GONE);

			}
		}


		SimpleDateFormat inputFormat1 = new SimpleDateFormat("dd/MM/yy");
		SimpleDateFormat outputFormat1 = new SimpleDateFormat("dd");

		Date date;
		String outPutBidDate = "01";
		try
		{
			date = inputFormat1.parse(chitDetails.get_bidDate());
			outPutBidDate = outputFormat1.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String ChitStartDate = chitDetails.get_startDate()+"-"+outPutBidDate;

		//String today = new SimpleDateFormat("dd/MM/yy").format(new Date());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date strDate;

		try {
			strDate = sdf.parse(ChitStartDate);
			if (System.currentTimeMillis() < strDate.getTime()) {
				biddingButton.setVisibility(View.GONE);
				membersButton.setVisibility(View.GONE);
				paymentsHistoryButton.setVisibility(View.GONE);
				reportsButton.setVisibility(View.GONE);
			}		
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method Name : goBack
	 * Method Type : User defined 
	 * @param v    : View Object
	 * Return Type : void
	 * 
	 * Description : This method is used to redirect the screen
	 * to ViewChits Screen.
	 */
	public void goBack(View v)
	{
		// If user came from Chits History screen then show him the same
		if(fromChitHistory!=null && fromChitHistory){
			Intent intentChitsHistory = new Intent(ChitDetails.this, ChitsHistory.class);
			startActivity(intentChitsHistory);
			finish();
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		}else{
			Intent viewChitsIntent = new Intent(ChitDetails.this, ViewChits.class);
			startActivity(viewChitsIntent);
			finish();
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		}
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
		startActivity(homeActivityIntent);
		finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}

	/**
	 * Method Name : goToEdit 
	 * Method Type : User defined
	 * @param v    : View Object
	 * Return Type : void
	 * 
	 * Description : This method is used to redirect the screen
	 * to ChitDetails edit screen.
	 */
	public void goToEdit(View v)
	{
		checkNet = Utils.isInternetAvailable(ChitDetails.this);

		if(!checkNet)		
		{
			Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
			//CommonMethods.showErrorDialog("Please check network connection", ChitDetails.this);
		}
		else
		{
			Intent editChitIntent = new Intent(ChitDetails.this,ChitsCreation.class);
			editChitIntent.putExtra("chitID", chitDetails.get_chitID());
			editChitIntent.putExtra("chitDetails", chitDetails);
			startActivity(editChitIntent);
			finish();
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		}
	}

	/**
	 * Method Name : getBiddingDetails
	 * Method Type : User defined
	 * @param v    : View Object
	 * Return Type : void
	 * 
	 * Description : This method is used to redirect the screen
	 * to Bidding List Screen.
	 */
	public void getBiddingDetails(View v)
	{
		checkNet = Utils.isInternetAvailable(ChitDetails.this);

		if(!checkNet)		
		{
			Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
			//CommonMethods.showErrorDialog("Please check network connection", ChitDetails.this);
		}
		else
		{	
			Intent biddingListIntent = new Intent(ChitDetails.this,BiddingDetails.class);
			biddingListIntent.putExtra("chit", chitDetails);
			startActivity(biddingListIntent);
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		}
	}

	/**
	 * Method Name : getMembersDetails
	 * Method Type : User defined
	 * @param v    : View Object
	 * Return Type : void
	 * 
	 * Description : This method is used to redirect the screen
	 * to Members List Screen.
	 */
	public void getMembersDetails(View v)
	{
		checkNet = Utils.isInternetAvailable(ChitDetails.this);

		if(!checkNet)		
		{
			Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
			//CommonMethods.showErrorDialog("Please check network connection", ChitDetails.this);
		}
		else
		{
			Intent membersListIntent = new Intent(ChitDetails.this,MembersList.class);
			membersListIntent.putExtra("chit", chitDetails);
			startActivity(membersListIntent);
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		}
	}

	/**
	 * Method Name : getPaymentHistory
	 * Method Type : User defined 
	 * @param v    : View Object
	 * Return Type : void
	 * 
	 * Description : This method is used to redirect the screen
	 * to PaymentHistory Screen.
	 */
	public void getPaymentHistory(View v)
	{
		checkNet = Utils.isInternetAvailable(ChitDetails.this);

		if(!checkNet)		
		{
			Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
			//CommonMethods.showErrorDialog("Please check network connection", ChitDetails.this);
		}
		else
		{
			if(userID.equals(chitOwnerID))
			{
				Intent chitDetailsIntent = new Intent(ChitDetails.this,Payment.class);
				chitDetailsIntent.putExtra("chit", chitDetails);
				startActivity(chitDetailsIntent);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			}
			else
			{
				Intent paymentHistoryIntent = new Intent(ChitDetails.this,UserPayment.class);
				paymentHistoryIntent.putExtra("chit", chitDetails);
				startActivity(paymentHistoryIntent);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			}
		}
	}

	/**
	 * Method Name : getReport
	 * Method Type : User defined
	 * @param v    : View Object
	 * Return Type : void
	 * 
	 * Description : This method will send email with all the chit 
	 * details to admin mail id.
	 */
	public void getReport(View v)
	{
		checkNet = Utils.isInternetAvailable(ChitDetails.this);

		if(!checkNet)		
		{
			Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
			//CommonMethods.showErrorDialog("Please check network connection", ChitDetails.this);
		}
		else
		{
			new SendReport().execute();
		}
	}


	/**
	 * Method Name : onBackPressed
	 * Method Type : Override
	 * Return Type : void
	 * 
	 * Description : This method is used to handle device back button.
	 * When user clicks on device back button it redirect the screen
	 * to ViewChits Screen.
	 */

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		// If user came from Chits History screen then show him the same
		if(fromChitHistory!=null && fromChitHistory){
			Intent intentChitsHistory = new Intent(ChitDetails.this, ChitsHistory.class);
			startActivity(intentChitsHistory);
			finish();
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		}else{
			Intent viewChitsIntent = new Intent(ChitDetails.this,ViewChits.class);
			startActivity(viewChitsIntent);
			finish();
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		}
	}



	/***************************************************************
	 * 
	 * Project Name : Chitfund
	 * Class Name   : SendReport
	 * DateTime     : 27-May-2014 05:00 PM 
	 * Developer    : Durga Prasad B
	 * Company      : Sagarsoft India Ltd.
	 * 
	 * Description  :
	 * ------------------------------------------------------------- 
	 * This class is used to send chit report to admin using REST 
	 * webservice by extending AsyncTask.
	 * -------------------------------------------------------------
	 * 
	 * *************************************************************/

	class SendReport extends AsyncTask<Void, Void, Void>
	{
		private String Error = null;
		ProgressDialog pd,pd1;
		String res;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(ChitDetails.this);
			pd.setMessage("Sending report to register email id, Please wait...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {

				checkNet = Utils.isInternetAvailable(ChitDetails.this);

				if(checkNet)		
				{
					String url=getResources().getString(R.string.URL)+"?call=createReport&chitID="+chitID;

					try{
						HttpClient client = new DefaultHttpClient();
						HttpPost postAccept = new HttpPost(url);
						HttpResponse response = client.execute(postAccept);
						res = EntityUtils.toString(response.getEntity());
					}
					catch (Exception e) {
						Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
						//CommonMethods.showErrorDialog(e.getMessage(), ChitDetails.this);
					}
				}

			}catch (Exception e) {
				Error=e.getMessage();
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(pd.isShowing())
			{
				if(!checkNet)		
				{
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
					//CommonMethods.showErrorDialog("Please check network connection", ChitDetails.this);
				}
				else
				{

					if(Error!=null)
					{

					}
					else
					{
						try
						{
							JSONObject json = new JSONObject(res);
							JSONArray jsonArray =json.getJSONArray("Result");

						}catch (Exception e) {
							e.printStackTrace();
						}

					}
					pd.dismiss();

					Toast.makeText(getApplicationContext(), "Report Sent Successfully.", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}	

}
