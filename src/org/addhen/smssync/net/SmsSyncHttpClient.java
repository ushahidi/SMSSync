/** 
 * Copyright (c) 2010 Addhen
 * All rights reserved
 * Contact: henry@addhen.org
 * Website: http://www.addhen.org/blog
 * 
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.	
 *	
 *
 * 
 **/

package org.addhen.smssync.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


public class SmsSyncHttpClient {
	
	/**
     * Upload sms to a webservice via HTTP POST
     * @param address
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * TODO Think through this method and make it more generic.
     */
    public static boolean postSmsToWebService(String URL, HashMap<String, String> params) throws IOException{
        ClientHttpRequest req = null;

        try {
             URL url = new URL(URL);
             req = new ClientHttpRequest(url);
             
             req.setParameter("api_key", params.get("api_key"));
             req.setParameter("message_from",params.get("message_from"));
             req.setParameter("message_description", params.get("message_description"));
             
             //InputStream serverInput = req.post();
             
             
             /*if( GetText(serverInput) ){
            	 
            	 return true;
             }*/
             
             //TODO determine for a successful sms post
             return true;
             
        } catch (MalformedURLException ex) {
        	//fall through and return false
        }
        return false;
   }
    
    public static String GetText(InputStream in) {
		String text = "";
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				in), 1024);
		final StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			text = sb.toString();
		} catch (final Exception ex) {
		} finally {
			try {
				in.close();
			} catch (final Exception ex) {
			}
		}
		return text;
	}
}
