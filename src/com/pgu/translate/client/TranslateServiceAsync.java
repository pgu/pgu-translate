package com.pgu.translate.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface TranslateServiceAsync {

    void translate(String text, String source, AsyncCallback<HashMap<String, String>> asyncCallback);

}
