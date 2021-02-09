/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.microservice.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * Helper class for wrapping messages in a border.
 */
public class Boilerplate {

    /** Default message width for banners */
    public static final int DEFAULT_MESSAGE_WIDTH = 80;

    public static String boilerplate(String message) {
	return boilerplate(message, "*", DEFAULT_MESSAGE_WIDTH);
    }

    public static String boilerplate(String message, String character, int maxlength) {
	return boilerplate(new ArrayList<String>(Arrays.asList(new String[] { message })), character, maxlength);
    }

    public static String boilerplate(List<String> messages, String c) {
	return boilerplate(messages, c, DEFAULT_MESSAGE_WIDTH);
    }

    @SuppressWarnings("deprecation")
    public static String boilerplate(List<String> messages, String c, int maxlength) {
	int size;
	StringBuffer buf = new StringBuffer(messages.size() * maxlength);
	boolean charIsSpace = " ".equals(c);
	int trimLength = maxlength - (charIsSpace ? 2 : 4);

	for (int i = 0; i < messages.size(); i++) {
	    size = messages.get(i).toString().length();
	    if (size > trimLength) {
		String temp = messages.get(i).toString();
		int k = i;
		int x;
		int len;
		messages.remove(i);
		while (temp.length() > 0) {
		    len = (trimLength <= temp.length() ? trimLength : temp.length());
		    String msg = temp.substring(0, len);
		    x = msg.indexOf(SystemUtils.LINE_SEPARATOR);

		    if (x > -1) {
			msg = msg.substring(0, x);
			len = x + 1;
		    } else {
			x = msg.lastIndexOf(' ');
			if (x > -1 && len == trimLength) {
			    msg = msg.substring(0, x);
			    len = x + 1;
			}
		    }
		    if (msg.startsWith(" ")) {
			msg = msg.substring(1);
		    }

		    temp = temp.substring(len);
		    messages.add(k, msg);
		    k++;
		}
	    }
	}

	buf.append(SystemUtils.LINE_SEPARATOR);
	if (!charIsSpace) {
	    buf.append(StringUtils.repeat(c, maxlength));
	}

	for (int i = 0; i < messages.size(); i++) {
	    buf.append(SystemUtils.LINE_SEPARATOR);
	    if (!charIsSpace) {
		buf.append(c);
	    }
	    buf.append(" ");
	    buf.append(messages.get(i));

	    int padding;
	    padding = trimLength - messages.get(i).toString().getBytes().length;
	    if (padding > 0) {
		buf.append(StringUtils.repeat(" ", padding));
	    }
	    buf.append(' ');
	    if (!charIsSpace) {
		buf.append(c);
	    }
	}
	buf.append(SystemUtils.LINE_SEPARATOR);
	if (!charIsSpace) {
	    buf.append(StringUtils.repeat(c, maxlength));
	}
	return buf.toString();
    }
}