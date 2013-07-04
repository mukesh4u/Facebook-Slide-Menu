package com.android.utilities;

import java.util.ArrayList;

import com.android.model.WebAddress;




public class Config {
	
	static ArrayList<WebAddress> address = null;

	public static ArrayList<WebAddress> createAddress() {
		address = new ArrayList<WebAddress>();
		address.add(new WebAddress("Google","http://www.google.com"));
		address.add(new WebAddress("Yahoo","http://www.yahoo.co.in"));
		address.add(new WebAddress("Gmail","http://www.gmail.com"));
		address.add(new WebAddress("Facebook","http://www.facebook.com"));
		address.add(new WebAddress("My Blog","http://mukeshyadav4u.blogspot.com"));
		return address;

	}

}