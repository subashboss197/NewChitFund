/*************************************************************** 

Project Name : EasyChit

 **************************************************************** 

Class Name : UserApproveStatus

 Date : 2 June 2014

 Developer : Deepthi

 Description : To Approve/disapprove a member

 

 **************************************************************** 

Change History : 

Date : 

Developer : 

Change : 
 

 ****************************************************************/
package com.newchitfund.androidclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.newchitfund.androidclient.SwipeListView.SwipeListViewCallback;
import com.newchitfund.businessobjects.Member;
import com.newchitfund.imagecaching.ImageLoader;

public class UserApproveStatus extends Activity implements SectionIndexer,
		SwipeListViewCallback {

	ImageView ivApproveMemberBackBtn;
	TextView tvApproveMemberhead;
	EditText etApproveMemberSearch;
	ListView llApproveMemberList;
	ImageView ivApproveMemberDone;
	ImageView ivUserApproveHomeBtn;
	ArrayList<Member> member_data = new ArrayList<Member>();
	ArrayList<Member> search_data = new ArrayList<Member>();
	ImageLoader imgLoader;
	String searchName;
	MemberList_Adapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userapprovestatus);

		ivUserApproveHomeBtn = (ImageView) findViewById(R.id.userApproveHomeBtn);
		ivApproveMemberBackBtn = (ImageView) findViewById(R.id.ivapproveMemberBackBtn);
		tvApproveMemberhead = (TextView) findViewById(R.id.tvapproveMemberhead);
		tvApproveMemberhead.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Light.ttf"));
		etApproveMemberSearch = (EditText) findViewById(R.id.etapproveMemberSearch);
		llApproveMemberList = (ListView) findViewById(R.id.approvememberList);
		// ivApproveMemberDone = (ImageView)
		// findViewById(R.id.ivapproveMemberDone);

		SwipeListView slView = new SwipeListView(this, this);
		slView.exec();

		imgLoader = new ImageLoader(this);

		/* to list member in listview */
		new ApproveMemberList().execute();

		/* Click to search by member name */
		etApproveMemberSearch.addTextChangedListener(new TextWatcher() {

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

		/*Click to move to categories page*/
		ivApproveMemberBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent menu = new Intent(UserApproveStatus.this,
						Categories.class);
				startActivity(menu);
				finish();
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			}
		});
		
		/*Click to move to categories page*/
		tvApproveMemberhead.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent menu = new Intent(UserApproveStatus.this,
						Categories.class);
				startActivity(menu);
				finish();		
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			}
		});		
	
		/*Click to move to categories page*/
		ivUserApproveHomeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent menu = new Intent(UserApproveStatus.this,
						Categories.class);
				startActivity(menu);
				finish();	
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			}
		});
	}

	/* method to search member by name */
	private void searchByNameCode() {
		searchName = etApproveMemberSearch.getText().toString();
		int textLength = searchName.length();

		search_data.clear();

		for (int i = 0; i < member_data.size(); i++) {
			String memberMainName = member_data.get(i).get_name();
			if (textLength <= memberMainName.length()) {
				// compare the String in EditText with Names in the ArrayList
				if (searchName.equalsIgnoreCase(memberMainName.substring(0,
						textLength))) {
					search_data.add(member_data.get(i));
				}

			}
		}

		mAdapter = new MemberList_Adapter(UserApproveStatus.this,
				R.layout.approvememberrow, search_data);
		llApproveMemberList.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Class Name : ApproveMemberList Purpose : To get data from service in
	 * background and show them in listview
	 */
	class ApproveMemberList extends AsyncTask<Void, Void, Void> {
		private String Error = null;
		ProgressDialog pd;
		String res;
		Boolean checkNet;

		@Override
		protected void onPreExecute() {

			pd = new ProgressDialog(UserApproveStatus.this);
			pd.setIndeterminate(true);
			pd.setMessage("Loading member details, Please wait..");
			pd.setCancelable(false);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				checkNet = Utils.isInternetAvailable(UserApproveStatus.this);

				if (checkNet) {
					String URL = getResources().getString(R.string.URL);
					String adminID = getResources().getString(R.string.adminID);

					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(URL);
					MultipartEntity multiEntity = new MultipartEntity();

					multiEntity
							.addPart("call", new StringBody("getAllMembers"));
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
				if (!checkNet) {
					pd.dismiss();
					Toast.makeText(UserApproveStatus.this,"Please check network connection",Toast.LENGTH_SHORT).show();
					return;
				}
				if (Error != null) {
					Toast.makeText(UserApproveStatus.this,Error,Toast.LENGTH_SHORT).show();
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
							member.set_profilePic(resultObj
									.getString("profilePic"));
							member.set_membershipStatus(resultObj
									.getString("membershipStatus"));
							member.set_userID(resultObj.getString("userID"));

							member_data.add(member);
							member=null;
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

					mAdapter = new MemberList_Adapter(UserApproveStatus.this,
							R.layout.approvememberrow, member_data);
					llApproveMemberList.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();

				}
				pd.dismiss();
			}
			super.onPostExecute(result);
		}
	}

	/**
	 * Class Name : MemberList_Adapter Purpose : Adapter to set data for holder
	 * in listview
	 * 
	 */
	public class MemberList_Adapter extends ArrayAdapter<Member> {
		Activity activity;
		int layoutResourceId;
		Member member;
		int memberArrayPosition;
		String userId;

		ImageView ivApprove;
		ImageView ivDisapprove;
		ImageView ivMemberDelete;

		ArrayList<Member> data = new ArrayList<Member>();
		// TextView label;
		View row;

		private final int INVALID = -1;
		protected int DELETE_POS = -1;

		public MemberList_Adapter(Activity act, int layoutResourceId,
				ArrayList<Member> data) {
			super(act, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.activity = act;
			this.data = data;
			notifyDataSetChanged();
		}

		public void onSwipeItem(boolean isRight, int position) {
			// TODO Auto-generated method stub
			//Log.v("UserApproveStatus", "DELETE_POS=====" + DELETE_POS+ "====isRight====" + isRight);
			if (isRight == false) {
				DELETE_POS = position;
			} else if (DELETE_POS == position) {
				DELETE_POS = INVALID;
			}
			//
			notifyDataSetChanged();
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater
						.inflate(R.layout.approvememberrow, parent, false);

				/* creating linearlayout and textviews for data */

				LinearLayout L1_memberrow = (LinearLayout) row
						.findViewById(R.id.alphabetapproveHeader);
				TextView header = new TextView(getApplicationContext());

				TextView memberName = (TextView) row
						.findViewById(R.id.tvapprovemembername);
				memberName.setTypeface(Typeface.createFromAsset(getAssets(),
						"fonts/Roboto-Light.ttf"));

				TextView memberPhone = (TextView) row
						.findViewById(R.id.tvapprovemembermobile);
				memberPhone.setTypeface(Typeface.createFromAsset(getAssets(),
						"fonts/Roboto-Light.ttf"));

				ImageView ivMemberProfile = (ImageView) row
						.findViewById(R.id.ivapprovememberprofile);

				ivMemberDelete = (ImageView) row
						.findViewById(R.id.memberdelete);

				ivApprove = (ImageView) row.findViewById(R.id.ivapprove);
				ivDisapprove = (ImageView) row.findViewById(R.id.ivdisapprove);

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

				if (data.get(position).get_membershipStatus()
						.equals("DEACTIVATED")) {
					ivDisapprove.setVisibility(View.VISIBLE);
					ivApprove.setVisibility(View.GONE);

					ivApprove.setTag(position);
					ivDisapprove.setTag(position);
				} else {
					ivDisapprove.setVisibility(View.GONE);
					ivApprove.setVisibility(View.VISIBLE);

					ivApprove.setTag(position);
					ivDisapprove.setTag(position);
				}

				/* delete button visible code */

				if (DELETE_POS == position) {
					ivMemberDelete.setVisibility(View.VISIBLE);
					ivMemberDelete.setTag(position);
					ivApprove.setVisibility(View.GONE);
					ivDisapprove.setVisibility(View.GONE);
					
				} else {
					ivMemberDelete.setVisibility(View.GONE);
					ivMemberDelete.setTag(position);
				}
				/* delete button visible code ends */

				if (data.get(position).get_profilePic().equals("")) {

				} else {
					imgLoader.DisplayImage(data.get(position).get_profilePic(),
							ivMemberProfile);
				}

				/*Click to delete a member if member not in active chit*/
				ivMemberDelete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						memberArrayPosition = Integer.parseInt(v.getTag()
								.toString());

						if (etApproveMemberSearch.length() > 0) {
							userId = search_data.get(memberArrayPosition)
									.get_userID();
						} else {
							userId = member_data.get(memberArrayPosition)
									.get_userID();
						}

						AlertDialog.Builder adb = new AlertDialog.Builder(
								UserApproveStatus.this);
						adb.setTitle(null);
						adb.setMessage("Do you want to delete this member?");
						adb.setNegativeButton("No",
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										DELETE_POS = INVALID;
										mAdapter.notifyDataSetChanged();
									}
								});
						adb.setPositiveButton("Yes",
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										DELETE_POS = INVALID;
										mAdapter.notifyDataSetChanged();
										new DeleteMemberAsynTask(userId,
												memberArrayPosition,
												ivMemberDelete).execute();
									}
								});
						adb.show();

					}
				});

				/*Click to Disapprove a member*/
				ivDisapprove.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						memberArrayPosition = Integer.parseInt(v.getTag()
								.toString());

						if (etApproveMemberSearch.length() > 0) {
							userId = search_data.get(memberArrayPosition)
									.get_userID();
						} else {
							userId = member_data.get(memberArrayPosition)
									.get_userID();
						}

						AlertDialog.Builder adb = new AlertDialog.Builder(
								UserApproveStatus.this);
						adb.setTitle(null);
						adb.setMessage("Do you want to approve this member?");
						adb.setNegativeButton("No",
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										mAdapter.notifyDataSetChanged();
									}
								});
						adb.setPositiveButton("Yes",
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										ivApprove.setVisibility(View.VISIBLE);
										ivDisapprove.setVisibility(View.GONE);
										new ChangeMemberStatusAsynTask(userId,
												"APPROVED", memberArrayPosition)
												.execute();
									}
								});
						adb.show();

					}
				});

				/*Click to approve a member*/
				ivApprove.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						memberArrayPosition = Integer.parseInt(v.getTag()
								.toString());

						if (etApproveMemberSearch.length() > 0) {
							userId = search_data.get(memberArrayPosition)
									.get_userID();
						} else {
							userId = member_data.get(memberArrayPosition)
									.get_userID();
						}

						AlertDialog.Builder adb = new AlertDialog.Builder(
								UserApproveStatus.this);
						adb.setTitle(null);
						adb.setMessage("Do you want to disapprove this member?");
						adb.setNegativeButton("No",
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										mAdapter.notifyDataSetChanged();
									}
								});
						adb.setPositiveButton("Yes",
								new AlertDialog.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										ivDisapprove
												.setVisibility(View.VISIBLE);
										ivApprove.setVisibility(View.GONE);
										new ChangeMemberStatusAsynTask(userId,
												"DEACTIVATED",
												memberArrayPosition).execute();
									}
								});
						adb.show();

					}
				});

			} catch (Exception e) {

			}
			return row;
		}

	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub

		for (int i = 0; i < member_data.size(); i++) {
			String l = member_data.get(i).get_name();
			char firstChar = l.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	private void setSection(LinearLayout llmemberrow, String label) {

		TextView tv_header = new TextView(getBaseContext());
		tv_header.setTextColor(getResources().getColor(R.color.AZindex_color));
		tv_header.setText(label.substring(0, 1).toUpperCase());
		tv_header.setTextSize(getResources().getDimension(R.dimen.addMemberAZIndex));		
		tv_header.setPadding(15, 0, 0, 6);
		tv_header.setGravity(Gravity.CENTER_HORIZONTAL);
		llmemberrow.addView(tv_header);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		Intent menu = new Intent(UserApproveStatus.this, Categories.class);
		startActivity(menu);
		finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
		super.onBackPressed();
	}

	/**
	 * Class Name : ChangeMemberStatusAsynTask Purpose : Class to Change member
	 * status on background
	 */
	class ChangeMemberStatusAsynTask extends AsyncTask<Void, Void, String> {
		ProgressDialog pd;

		String mStatus, mMessage;
		String memberId, checkStatus;
		int position;
		Boolean checkNet;

		public ChangeMemberStatusAsynTask(String memberid, String checkStatus,
				int position) {
			this.memberId = memberid;
			this.checkStatus = checkStatus;
			this.position = position;
			// Log.v("position---", "" + position);
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(UserApproveStatus.this);
			pd.setIndeterminate(true);
			pd.setMessage("Changing status , please wait !!! ...");
			pd.setCancelable(false);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {

			try {

				checkNet = Utils.isInternetAvailable(UserApproveStatus.this);

				if (checkNet) {
					String URL = getResources().getString(R.string.URL);

					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(URL);
					MultipartEntity multiEntity = new MultipartEntity();

					multiEntity.addPart("call", new StringBody(
							"changeMembershipStatus"));

					multiEntity
							.addPart("userID", new StringBody(this.memberId));
					multiEntity.addPart("membershipStatus", new StringBody(
							this.checkStatus));

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
				if (!checkNet) {
					pd.dismiss();
					Toast.makeText(UserApproveStatus.this,"Please check network connection",Toast.LENGTH_SHORT).show();
					return;
				}
				if (result.equalsIgnoreCase("success")) {
					if (etApproveMemberSearch.length() > 0) {
						search_data.get(position).set_membershipStatus(
								this.checkStatus);
					} else {
						member_data.get(position).set_membershipStatus(
								this.checkStatus);
					}
					mAdapter.notifyDataSetChanged();

				} else if (result
						.equalsIgnoreCase("Member is in active chit not possible to deactivate.")) {
					// CommonMethods.showErrorDialog("Member is in active chit not possible to deactivate.",UserApproveStatus.this);

					AlertDialog.Builder adb = new AlertDialog.Builder(
							UserApproveStatus.this);
					adb.setTitle(null);
					adb.setMessage("Member is in active chit. Are you sure to deactivate?");
					adb.setNegativeButton("No",
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									mAdapter.notifyDataSetChanged();
								}
							});
					adb.setPositiveButton("Yes",
							new AlertDialog.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									new forceDisApprove(memberId, position)
											.execute();
								}
							});
					adb.show();

				} else {
					Toast.makeText(getApplicationContext(), mMessage,
							Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
			}
			super.onPostExecute(result);
		}
	}

	/**
	 * Class Name : ChangeMemberStatusAsynTask Purpose : Class to Change member
	 * status on background
	 */
	class forceDisApprove extends AsyncTask<Void, Void, String> {
		ProgressDialog pd;

		String mStatus, mMessage;
		String memberId;
		int position;
		Boolean checkNet;

		public forceDisApprove(String memberid, int position) {
			this.memberId = memberid;
			this.position = position;
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(UserApproveStatus.this);
			pd.setIndeterminate(true);
			pd.setMessage("Changing status forcily, please wait !!! ...");
			pd.setCancelable(false);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {

			try {
				checkNet = Utils.isInternetAvailable(UserApproveStatus.this);

				if (checkNet) {

					String URL = getResources().getString(R.string.URL);

					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(URL);
					MultipartEntity multiEntity = new MultipartEntity();

					multiEntity.addPart("call", new StringBody(
							"forceDisapproveMember"));
					multiEntity
							.addPart("userID", new StringBody(this.memberId));

					post.setEntity(multiEntity);
					HttpResponse response = client.execute(post);
					String res = EntityUtils.toString(response.getEntity());

					Log.v("responce", "Response===" + res);
					JSONObject json1 = new JSONObject(res);

					mStatus = json1.getString("Status");
					mMessage = json1.getString("Message");

					if (mStatus.equalsIgnoreCase("Success")) {
						if (etApproveMemberSearch.length() > 0) {
							search_data.get(position).set_membershipStatus(
									"DEACTIVATED");
						} else {
							member_data.get(position).set_membershipStatus(
									"DEACTIVATED");
						}

						return "success";
					}
					if (!mMessage.equalsIgnoreCase("OK")) {

						if (etApproveMemberSearch.length() > 0) {
							search_data.get(position).set_membershipStatus(
									"APPROVED");
						} else {
							member_data.get(position).set_membershipStatus(
									"APPROVED");
						}
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
				if (!checkNet) {
					pd.dismiss();
					Toast.makeText(UserApproveStatus.this,"Please check network connection",Toast.LENGTH_SHORT).show();
					return;
				}
				if (result.equalsIgnoreCase("success")) {
					mAdapter.notifyDataSetChanged();
				} else {
					Toast.makeText(getApplicationContext(), mMessage,
							Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
			}
			super.onPostExecute(result);
		}
	}

	/**
	 * Class Name : ChangeMemberStatusAsynTask Purpose : Class to Change member
	 * status on background
	 */
	class DeleteMemberAsynTask extends AsyncTask<Void, Void, String> {
		ProgressDialog pd;

		String mStatus, mMessage;
		String memberId;
		int position;
		ImageView ivMemberDelete;
		Boolean checkNet;

		public DeleteMemberAsynTask(String memberid, int position,
				ImageView ivMemberDelete) {
			this.memberId = memberid;
			this.position = position;
			this.ivMemberDelete = ivMemberDelete;
			// Log.v("position---", "" + position);
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(UserApproveStatus.this);
			pd.setIndeterminate(true);
			pd.setMessage("Deleting member , please wait !!! ...");
			pd.setCancelable(false);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {

			try {

				checkNet = Utils.isInternetAvailable(UserApproveStatus.this);

				if (checkNet) {
					String URL = getResources().getString(R.string.URL);

					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(URL);
					MultipartEntity multiEntity = new MultipartEntity();

					multiEntity.addPart("call", new StringBody("deleteMember"));
					multiEntity
							.addPart("userID", new StringBody(this.memberId));

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
				if (!checkNet) {
					pd.dismiss();
					Toast.makeText(UserApproveStatus.this,"Please check network connection",Toast.LENGTH_SHORT).show();
					return;
				}
				if (result.equalsIgnoreCase("success")) {
					if (etApproveMemberSearch.length() > 0) {
						search_data.remove(position);
					} else {
						member_data.remove(position);
					}
					Toast.makeText(getApplicationContext(),
							"Member deleted successfully", Toast.LENGTH_LONG)
							.show();					
					mAdapter.notifyDataSetChanged();
				} else if (result
						.equalsIgnoreCase("Member is in active chit not possible to delete.")) {
					
					AlertDialog.Builder adb = new AlertDialog.Builder(
							UserApproveStatus.this);
					adb.setTitle(null);
					adb.setMessage(mMessage);
					adb.setNegativeButton("Ok",null);						
					adb.show();
					
					ivMemberDelete.setVisibility(View.GONE);
				} else {
					Toast.makeText(getApplicationContext(), mMessage,
							Toast.LENGTH_LONG).show();
					ivMemberDelete.setVisibility(View.GONE);
				}
				pd.dismiss();
			}
			super.onPostExecute(result);
		}
	}

	/*Swipe Implemented method*/
	@Override
	public ListView getListView() {
		// TODO Auto-generated method stub
		return llApproveMemberList;
	}

	/*Swipe Implemented method*/
	@Override
	public void onSwipeItem(boolean isRight, int position) {
		// TODO Auto-generated method stub
		//Log.v("onSwipeItem", "onSwipeItem====");
		mAdapter.onSwipeItem(isRight, position);
	}

	/*Swipe Implemented method*/
	@Override
	public void onItemClickListener(ListAdapter adapter, int position) {
		// TODO Auto-generated method stub

	}

}
