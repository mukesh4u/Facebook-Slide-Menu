package com.android.facebookslider;


import java.util.ArrayList;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.android.adapter.MenuAdapter;
import com.android.facebookslider.FacebookSlideView;
import com.android.facebookslider.FacebookSlideView.SizeCallback;
import com.android.model.WebAddress;
import com.android.utilities.Config;
import com.example.facebookslider.R;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class FacebookSlider extends Activity {
    FacebookSlideView scrollView;
    MenuAdapter menuAdapter;
    View menu;
    View app;
    Button btnSlide;
    static boolean menuOut = false;
    boolean isScan = false;
    Handler handler = new Handler();
    int btnWidth;
    ArrayList<WebAddress> address = new ArrayList<WebAddress>();
    WebView webView;
    ProgressDialog mProgress;
    boolean loadingFinished = true;
    boolean redirect = false;
    AlertDialog.Builder alert;
    boolean isWebHistory = false;

    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if( Build.VERSION.SDK_INT >= 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy); 
        }
        
        LayoutInflater inflater = LayoutInflater.from(this);
        scrollView = (FacebookSlideView) inflater.inflate(R.layout.screen_scroll_with_list_menu, null);
        setContentView(scrollView);

        final Stack stack=new Stack();
        menu = inflater.inflate(R.layout.horz_scroll_menu, null);
        app = inflater.inflate(R.layout.screen_facebook_slider, null);
        webView =(WebView) app.findViewById(R.id.webView);
        ViewGroup tabBar = (ViewGroup) app.findViewById(R.id.tabBar);
        
        address = Config.createAddress();
        menuAdapter = new MenuAdapter(this,R.layout.link, address);
        ListView listView = (ListView) menu.findViewById(R.id.list);
        //ViewUtils.initListView(this, listView, "Menu ", 8, android.R.layout.simple_list_item_1);
        listView.setAdapter(menuAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext();
                isWebHistory = true;
                webView.setVisibility(0);
                webView.getSettings().setJavaScriptEnabled(true);
                menuOut = true;
                scrollWebviw(scrollView, menu);
        		// force web view to open inside application
                mProgress = ProgressDialog.show(FacebookSlider.this, "Loading", "Please wait for a moment...");
        		webView.setWebViewClient(new MyWebViewClient());
        		menuOut = false;
        	    stack.push(address.get(position).getName());
        	    Log.d("The contents of Stack is" , stack.toString());
        		openURL(address.get(position).getUrl());
        		webView.requestFocus(View.FOCUS_DOWN);
    		    webView.setOnTouchListener(new View.OnTouchListener() {
    		       	@Override
					public boolean onTouch(View v, MotionEvent event) {
    		       		switch (event.getAction()) {
    		            case MotionEvent.ACTION_DOWN:
    		            case MotionEvent.ACTION_UP:
    		                if (!v.hasFocus()) {
    		                	v.requestFocus();
    		                }
    		                break;
    		        }
    		        return false;
					}
    		    });
        		
            }

        });
        
        btnSlide = (Button) tabBar.findViewById(R.id.BtnSlide);
       
        btnSlide.setOnClickListener(new ClickListenerForScrolling(scrollView, menu));

        final View[] children = new View[] { menu, app };

        // Scroll to app (view[1]) when layout finished.
        int scrollToViewIdx = 1;
        
        scrollView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu(btnSlide));
    }
    
    

     /**
     * Helper for examples with a HSV that should be scrolled by a menu View's width.
     */
    static class ClickListenerForScrolling implements OnClickListener {
        FacebookSlideView scrollView;
        View menu;
        /**
         * Menu must NOT be out/shown to start with.
         */
        //boolean menuOut = false;

        public ClickListenerForScrolling(FacebookSlideView scrollView, View menu) {
            super();
            this.scrollView = scrollView;
            this.menu = menu;
        }

        @Override
        public void onClick(View v) {
            Context context = menu.getContext();
            
            int menuWidth = menu.getMeasuredWidth();

            // Ensure menu is visible
            menu.setVisibility(View.VISIBLE);

            if (!menuOut) {
                // Scroll to 0 to reveal menu
            	Log.d("===slide==","Scroll to right");
            	Log.d("===clicked==","clicked");
                int left =20;
                scrollView.smoothScrollTo(left, 0);
            } else {
                // Scroll to menuWidth so menu isn't on screen.
            	Log.d("===slide==","Scroll to left");
            	Log.d("===clicked==","clicked");
                int left = menuWidth;
                scrollView.smoothScrollTo(left, 0);
            }
            menuOut = !menuOut;
        }
    }

    /**
     * Helper that remembers the width of the 'slide' button, so that the 'slide' button remains in view, even when the menu is
     * showing.
     */
    static class SizeCallbackForMenu implements SizeCallback {
        int btnWidth;
        View btnSlide;

        public SizeCallbackForMenu(View btnSlide) {
            super();
            this.btnSlide = btnSlide;
        }

        @Override
        public void onGlobalLayout() {
            btnWidth = btnSlide.getMeasuredWidth();
            System.out.println("btnWidth=" + btnWidth);
        }

        @Override
        public void getViewSize(int idx, int w, int h, int[] dims) {
            dims[0] = w;
            dims[1] = h;
            final int menuIdx = 0;
            if (idx == menuIdx) {
                dims[0] = w - btnWidth;
            }
        }
    }
    
       
    private void openURL(String url) {
    	webView.loadUrl(url);
	}
    
    private class MyWebViewClient extends WebViewClient {
    	@Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
         // TODO Auto-generated method stub
         super.onPageStarted(view, url, favicon);
         loadingFinished = false;
         menuOut = false;
         //mProgress.show();
        }

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			 if (!loadingFinished) {
		          redirect = true;
		       }

		    loadingFinished = false;
			view.loadUrl(url);
			return true;
		}
		
		// when finish loading page
    	public void onPageFinished(WebView view, String url) {
    		if(!redirect){
    	          loadingFinished = true;
    	       }

    	       if(loadingFinished && !redirect){
    	    	   if(null !=mProgress) {
    	    		   if(mProgress.isShowing()) {
    		    			mProgress.dismiss();
    		    			menuOut = false;
    		    		}
    	    		}
    	    	   
    	       } else{
    	          redirect = false; 
    	       }
    	}
	}
    
    //scroll the page and open the webview
    private void scrollWebviw(FacebookSlideView scrollView, View menu) {
    	 Context context = menu.getContext();
         
         int menuWidth = menu.getMeasuredWidth();

         // Ensure menu is visible
         menu.setVisibility(View.VISIBLE);

         if (!menuOut) {
             // Scroll to 0 to reveal menu
         	Log.d("===slide==","Scroll to right");
             int left = 0;
             scrollView.smoothScrollTo(left, 0);
         } else {
             // Scroll to menuWidth so menu isn't on screen.
         	Log.d("===slide==","Scroll to left");
             int left = menuWidth;
             scrollView.smoothScrollTo(left, 0);
            
         }
         menuOut = false;
    }
    
      
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode)
            {
            case KeyEvent.KEYCODE_BACK:
                if(webView.canGoBack() == true){
                        webView.goBack();
                }else if(isWebHistory && webView.canGoBack() == false){
                	isWebHistory = false;
                	Intent menu = new Intent(FacebookSlider.this, FacebookSlider.class);
        			startActivity(menu);
        			webView.clearHistory();
        			
                }else{
                	webView.clearCache(true);
                	moveTaskToBack(true);
                    FacebookSlider.this.finish();
                }
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    
        
    @Override
    protected void onResume() {
    	super.onResume();
    	
    }
}
