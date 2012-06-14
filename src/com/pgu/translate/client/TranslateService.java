package com.pgu.translate.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("translate")
public interface TranslateService extends RemoteService {

    HashMap<String, String> translate(String text, String source);

}
