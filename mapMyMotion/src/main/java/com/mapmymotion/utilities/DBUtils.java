package com.mapmymotion.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mapmymotion.R;
import com.mapmymotion.sql.MotionDataSource;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.sax.StartElementListener;
import android.widget.Toast;

public class DBUtils {
	static Context sContext;
	static String mFileName;

	// import data
	public static long importData(Context context, String fileName)
			throws IOException {
		sContext = context;
		mFileName = fileName;
//		ExecutorService executor = Executors.newSingleThreadExecutor();
//		new ImportDataTask().executeOnExecutor(executor, null);
//		executor.shutdown();
//		long len = lineCounter(fileName);
//Utils.setToast(sContext, Long.toString(len), Toast.LENGTH_LONG);
		 MotionDataSource mds;
		 mds = new MotionDataSource(context);
		 mds.open();
		 File fileToImport = new
		 File(Environment.getExternalStorageDirectory()
		 + "/mapmymotion" + "/" + fileName + ".sql");
		 //InputStream is = new FileInputStream(fileToImport);
		 // int size = is.available();
		 //BufferedReader reader = new BufferedReader(new InputStreamReader(
		 //is, "iso-8859-1"), 8);
			BufferedReader reader = new BufferedReader(new FileReader(fileToImport));
		 // StringBuilder sb = new StringBuilder();
		 String line;
		 long lineCount = 0;
		//mds.startTransaction();
		 while ((line = reader.readLine()) != null) {
		 mds.importSQL(line);
		 lineCount++;
		 }
		 //mds.endTransaction();
		 reader.close();
		 mds.close();
		 Utils.setToast(sContext, Long.toString(lineCount), Toast.LENGTH_LONG);
		return lineCount;
	}

	
	static long lineCounter (String fileName) throws IOException {
		 MotionDataSource mds;
		 mds = new MotionDataSource(sContext);
		 mds.open();

        int lineCount = 0;
		 File fileToImport = new
		 File(Environment.getExternalStorageDirectory()
		 + "/mapmymotion" + "/" + fileName + ".sql");

			BufferedReader br = new BufferedReader(new FileReader(fileToImport));
		    String line;
		    while ((line = br.readLine()) != null) {
		        // do something with line.
		        System.out.println(line);
		        lineCount++;
		    }
		    br.close();
		return lineCount;

    }
	// importing database
	public static void importDB(Context context) {

		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String databasePath = "//data//" + "com.mapmymotion"
						+ "//databases//" + "mapmymotion.db";
				String backupDBPath = "/mapmymotion/mapmymotion.db";
				File dataPath = new File(data, databasePath);
				File backupPath = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(backupPath).getChannel();
				FileChannel dst = new FileOutputStream(dataPath).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Utils.setToast(context, databasePath.toString(),
						Toast.LENGTH_LONG);
			}
		} catch (Exception e) {
			Utils.setToast(context, e.getLocalizedMessage(), Toast.LENGTH_LONG);
		}
	}

	// exporting database
	public static void exportDB(Context context) {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "//data//" + "com.mapmymotion"
						+ "//databases//" + "mapmymotion.db";
				String backupDBPath = "/mapmymotion/mapmymotion.db";
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Utils.setToast(context, backupDB.toString(), Toast.LENGTH_LONG);
			}
		} catch (Exception e) {
			Utils.setToast(context, e.getLocalizedMessage(), Toast.LENGTH_LONG);
		}
	}

	private static class ImportDataTask extends AsyncTask<Void, Void, String> {
		final ProgressDialog mPd = new ProgressDialog(sContext);

		@Override
		protected void onPreExecute() {
			mPd.setIcon(R.drawable.appicon);
			mPd.setTitle(R.string.app_name);
			mPd.setIndeterminate(true);
			mPd.setMessage(sContext.getString(R.string.working));
			mPd.setCancelable(false);
			mPd.show();
//			File fileToImport = new File(
//					Environment.getExternalStorageDirectory() + "/mapmymotion"
//							+ "/" + mFileName + ".sql");
//			Utils.setToast(sContext, "Length: "+fileToImport.length(), Toast.LENGTH_SHORT);

		}

		@Override
		protected void onPostExecute(String message) {
			super.onPostExecute(message);
			mPd.dismiss();
		}

		@Override
		protected String doInBackground(Void... params) {
			MotionDataSource mds;
			mds = new MotionDataSource(sContext);
			mds.open();

			File fileToImport = new File(
					Environment.getExternalStorageDirectory() + "/mapmymotion"
							+ "/" + mFileName + ".sql");

			try {
				InputStream is = new FileInputStream(fileToImport);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				String line = null;
				mds.startTransaction();
				while ((line = reader.readLine()) != null) {
					mds.importSQL(line);
				}
				mds.endTransaction();
				is.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// int size = is.available();
			// BufferedReader reader = new BufferedReader(new InputStreamReader(
			// is, "iso-8859-1"), 8);
			// StringBuilder sb = new StringBuilder();
			// String line = null;

			// while ((line = reader.readLine()) != null) {
			// mds.importSQL(line);
			// }
			// is.close();
			mds.close();
			return null;
		}
	}

}
