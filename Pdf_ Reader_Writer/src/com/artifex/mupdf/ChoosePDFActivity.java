//
//  Android PDF Writer
//  http://coderesearchlabs.com/androidpdfwriter
//
//  by Javier Santo Domingo (j-a-s-d@coderesearchlabs.com)
//

package com.artifex.mupdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.pdfwriter.PDFWriter;
import com.example.pdfwriter.PaperSize;
import com.example.pdfwriter.StandardFonts;
import com.example.pdfwriter.Transformation;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ChoosePDFActivity extends Activity
{
	private File    mDirectory;
	private File [] mFiles;
	TextView mText;
	final int PROGRESS_DIALOG=1;
	File newFile;
	Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void onClick(View v)
    {
    	switch(v.getId())
    	{
    	case R.id.button1:
    		new Ganeratetask().execute();
    		break;
    	case R.id.button2:
    		if(newFile!=null && newFile.exists())
    		{
    			Uri uri = Uri.parse(newFile.getPath());
    			Intent intent = new Intent(this,MuPDFActivity.class);
    			intent.setAction(Intent.ACTION_VIEW);
    			intent.setData(uri);
    			startActivity(intent);
    		}
    		break;
    	default:	
    		break;
    	}
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id)
    	{
    	case PROGRESS_DIALOG:
    		ProgressDialog progress =new ProgressDialog(this);
    		progress.setMessage(getString(R.string.progress_title));
    		return progress;
    	}
    	return super.onCreateDialog(id);
    }
    
    
    public class Ganeratetask extends AsyncTask<Void, Void, Void>
    {

    	@Override
    	protected void onPreExecute() {
    		showDialog(PROGRESS_DIALOG);
    		super.onPreExecute();
    	}
    	
		@Override
		protected Void doInBackground(Void... params)
		{
			String pdfcontent = generateHelloWorldPDF();
	        outputToFile("PdfSample.pdf",pdfcontent,"ISO-8859-1");
			return null;
		}
		
    	@Override
    	protected void onPostExecute(Void result) 
    	{
    		if(newFile!=null && newFile.exists() && newFile.isFile())
    		{
    			outputToScreen(R.id.text, newFile.getPath());
    		}
    		removeDialog(PROGRESS_DIALOG);
    		super.onPostExecute(result);
    	}
    }
	
	private String generateHelloWorldPDF() 
	{
		PDFWriter mPDFWriter = new PDFWriter(PaperSize.FOLIO_WIDTH, PaperSize.FOLIO_HEIGHT);

      
		try {
			// all image formates are supported
			Bitmap i1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);	
	        mPDFWriter.addImage(40, 60, i1, Transformation.DEGREES_315_ROTATION);
	        mPDFWriter.addImage(72, 72, i1);
	        mPDFWriter.addImage(200, 400, 135, 75, i1);
	        mPDFWriter.addImage(150, 300, 130, 70, i1);
	        mPDFWriter.addImageKeepRatio(100, 200, 50, 25, i1);
	        mPDFWriter.addImageKeepRatio(50, 100, 30, 25, i1, Transformation.DEGREES_270_ROTATION);
	        mPDFWriter.addImageKeepRatio(25, 50, 30, 25, i1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        mPDFWriter.setFont(StandardFonts.SUBTYPE, StandardFonts.TIMES_ROMAN);
        mPDFWriter.addRawContent("row content \n");
        mPDFWriter.addText(70, 50, 12, "hello world");
        mPDFWriter.setFont(StandardFonts.SUBTYPE, StandardFonts.COURIER, StandardFonts.WIN_ANSI_ENCODING);
        mPDFWriter.addRawContent("row content rg\n");
        mPDFWriter.addText(30, 90, 10, "� CRL", Transformation.DEGREES_270_ROTATION);
        
        mPDFWriter.newPage();
        mPDFWriter.addRawContent("[] 0 d\n");
        mPDFWriter.addRawContent("1 w\n");
        mPDFWriter.addRawContent("0 0 1 RG\n");
        mPDFWriter.addRawContent("0 1 0 rg\n");
        mPDFWriter.addRectangle(40, 50, 280, 50);
        mPDFWriter.addText(85, 75, 18, "Android Docs");
        
        mPDFWriter.newPage();
        mPDFWriter.setFont(StandardFonts.SUBTYPE, StandardFonts.COURIER_BOLD);
        mPDFWriter.addText(150, 150, 14, "http://coderesearchlabs.com");
        mPDFWriter.addLine(150, 140, 300, 140);
        
        String s = mPDFWriter.asString();
        return s;
	}
	
	private void outputToScreen(int viewID, String pdfContent) {
        mText = (TextView) this.findViewById(viewID);
        mText.setText(pdfContent);
	}
	
	private void outputToFile(String fileName, String pdfContent, String encoding) {
       if(isSDPresent)
       {
    	   
           try {
        	        File dir = new File(Environment.getExternalStorageDirectory() + "/pdf_Writer_Reader/");
		           	if (!dir.exists())
		           	{
						dir.mkdirs();
					}
		           	
		           	newFile = new File(dir,fileName);
        	   		if(!newFile.exists())
		           	{
		               	newFile.createNewFile();
		           	}
		           	
	               try
	               {
		               	FileOutputStream pdfFile = new FileOutputStream(newFile);
		               	pdfFile.write(pdfContent.getBytes(encoding));            	
		               	pdfFile.close();
	               }
	               catch(FileNotFoundException e) {
	               	Log.e("Ex==",""+e.toString());
	               }
           } 
           catch(IOException e)
           {
           	Log.e("Ex==",""+e.toString());
           }
       }
		
	}
	
}
