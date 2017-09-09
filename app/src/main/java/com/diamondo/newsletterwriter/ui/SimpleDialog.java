package com.diamondo.newsletterwriter.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

	public class SimpleDialog extends DialogFragment {
	
	private String title;
	private String message;
	private DialogListener listener;
	private int commandReference;
	
	public SimpleDialog(int commandReference, String title, String message, Context context) {
		this.commandReference = commandReference;
		this.title = title;
		this.message = message;
		this.listener = (DialogListener) context;
	}
	
	
	public interface DialogListener{
		public void onDialogPositiveClick(int commandReference);	
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		if(title!=null)
			builder.setTitle(title);
		builder.setMessage(message)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id){
					listener.onDialogPositiveClick(commandReference);
				}
			})
			.setNegativeButton("Cancel", null);
		return builder.create();
	}
}