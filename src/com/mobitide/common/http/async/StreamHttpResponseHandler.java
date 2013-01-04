/**
 * @author alan
 * 2011-7-21
 */
package com.mobitide.common.http.async;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;

import android.os.Message;

/**
 * 
 * 异步加载流
 * 
 * 
 * @author alan
 *
 */
public class StreamHttpResponseHandler extends AsyncHttpResponseHandler {

	public StreamHttpResponseHandler() {
		super();
		
	}
	//ReWritten by Alan
	public void onSuccess(InputStream respInputStream) {}

	//ReWritten by Alan
    protected void sendSuccessMessage(InputStream respInputStream) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, respInputStream));
    }
    
	//ReWritten by Alan
    protected void handleSuccessMessage(InputStream respInputStream) {
        onSuccess(respInputStream);
    }


	//ReWritten by Alan
    protected void handleMessage(Message msg) {
        switch(msg.what) {
            case SUCCESS_MESSAGE:
                handleSuccessMessage((InputStream)msg.obj);
                break;
            case FAILURE_MESSAGE:
                handleFailureMessage((Throwable)msg.obj);
                break;
            case START_MESSAGE:
                onStart();
                break;
            case FINISH_MESSAGE:
                onFinish();
                break;
        }
    }


	//ReWritten by Alan
    void sendResponseMessage(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        if(status.getStatusCode() >= 300) {
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
        } else {
            try {
                HttpEntity entity = null;
                HttpEntity temp = response.getEntity();
                if(temp != null) {
                    entity = new BufferedHttpEntity(temp);
                }
                sendSuccessMessage(entity.getContent());
            } catch(IOException e) {
                sendFailureMessage(e);
            }
        }
    }

}
