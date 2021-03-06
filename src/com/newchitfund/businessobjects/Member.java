package com.newchitfund.businessobjects;

import java.sql.Blob;

public class Member {

	String _userID;
	String _name;
	String _email;
	String _profilePic;
	String _phone;
	String _role;
	String _membershipStatus;
	String _createdOn;

	public Member() {
		super();
	}

	public Member(String _name, String _phone) {
		super();
		this._name = _name;
		this._phone = _phone;

	}

	public Member(String _userID, String _name, String _phone) {
		super();
		this._userID = _userID;
		this._name = _name;
		this._phone = _phone;
	}

	/**
	 * @return the _userID
	 */
	public String get_userID() {
		return _userID;
	}

	/**
	 * @param _userID
	 *            the _userID to set
	 */
	public void set_userID(String _userID) {
		this._userID = _userID;
	}

	/**
	 * @return the _name
	 */
	public String get_name() {
		return _name;
	}

	/**
	 * @param _name
	 *            the _name to set
	 */
	public void set_name(String _name) {
		this._name = _name;
	}

	/**
	 * @return the _email
	 */
	public String get_email() {
		return _email;
	}

	/**
	 * @param _email
	 *            the _email to set
	 */
	public void set_email(String _email) {
		this._email = _email;
	}

	/**
	 * @return the _phone
	 */
	public String get_phone() {
		return _phone;
	}

	/**
	 * @param _phone
	 *            the _phone to set
	 */
	public void set_phone(String _phone) {
		this._phone = _phone;
	}

	/**
	 * @return the _role
	 */
	public String get_role() {
		return _role;
	}

	/**
	 * @param _role
	 *            the _role to set
	 */
	public void set_role(String _role) {
		this._role = _role;
	}

	/**
	 * @return the _membershipStatus
	 */
	public String get_membershipStatus() {
		return _membershipStatus;
	}

	/**
	 * @param _membershipStatus
	 *            the _membershipStatus to set
	 */
	public void set_membershipStatus(String _membershipStatus) {
		this._membershipStatus = _membershipStatus;
	}

	/**
	 * @return the _createdOn
	 */
	public String get_createdOn() {
		return _createdOn;
	}

	/**
	 * @param _createdOn
	 *            the _createdOn to set
	 */
	public void set_createdOn(String _createdOn) {
		this._createdOn = _createdOn;
	}

	/**
	 * @return the _profilePic
	 */
	public String get_profilePic() {
		return _profilePic;
	}

	/**
	 * @param _profilePic
	 *            the _profilePic to set
	 */
	public void set_profilePic(String _profilePic) {
		this._profilePic = _profilePic;
	}

}
