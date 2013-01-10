package constants;

import android.content.Context;

public class SingletonContext {

	private static SingletonContext mSingletonContext;
	private Context mContext;
	
	private SingletonContext(Context context) {
		mContext = context;
	}
	
	public static void initializeContext(Context context) {
		if (mSingletonContext == null)
			mSingletonContext = new SingletonContext(context);
	}
	
	public static SingletonContext getInstance() {
		return mSingletonContext;
	}
	
	public Context getContext() {
		return mContext;
	}
}
