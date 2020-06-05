package www.pide.com.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
		//SharedPreferences存储文件名，存储位置：/data/data/<package name>/shared_prefs目录下
//		private static String filename = "userInfo";

		/**
		 * community——写
		 * @param context
		 * @param name
		 * @param
		 */
		public static void write(Context context,String fileName,String name,String community){
			//获得SharedPreferences对象
			SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
			//获得SharedPreferences.Editor对象
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(name, community);
			editor.commit();
		}

		/**
		 * community——读
		 * @param context
		 * @param key
		 * @return
		 */
		public static String read(Context context,String fileName,String key){
			//获得SharedPreferences对象
			SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
			String s = sharedPreferences.getString(key, "");//第二个值是value的默认值
			return s;
		}


	/**
	 * community——读
	 * @param context

	 * @return
	 */
	public static void delete(Context context,String fileName){
		//获得SharedPreferences对象
		SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
		sharedPreferences.edit().clear().commit();
	}
}
