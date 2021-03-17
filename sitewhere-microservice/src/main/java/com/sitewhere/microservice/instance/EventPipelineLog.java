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
package com.sitewhere.microservice.instance;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.primitives.Longs;
import com.sitewhere.spi.microservice.instance.EventPipelineLogLevel;
import com.sitewhere.spi.microservice.instance.IEventPipelineLog;

/**
 * Model for event pipeline log entry.
 */
@JsonInclude(Include.NON_NULL)
public class EventPipelineLog implements IEventPipelineLog {

    // Map keys for storing values.
    public static final String TIMESTAMP = "tst";
    public static final String SOURCE = "src";
    public static final String DEVICE_TOKEN = "dev";
    public static final String LEVEL = "lvl";
    public static final String MICROSERVICE = "msv";
    public static final String MESSAGE = "msg";
    public static final String DETAIL = "dtl";

    /** Event timestamp */
    private long timestamp;

    /** Event source information */
    private String source;

    /** Unique device token */
    private String deviceToken;

    /** Log level */
    private EventPipelineLogLevel level;

    /** Microservice */
    private String microservice;

    /** Log message */
    private String message;

    /** Detail related to entry */
    private String detail;

    /*
     * @see com.sitewhere.spi.microservice.instance.IEventPipelineLog#getTimestamp()
     */
    @Override
    public long getTimestamp() {
	return timestamp;
    }

    public void setTimestamp(long timestamp) {
	this.timestamp = timestamp;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IEventPipelineLog#getSource()
     */
    @Override
    public String getSource() {
	return source;
    }

    public void setSource(String source) {
	this.source = source;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.instance.IEventPipelineLog#getDeviceToken()
     */
    @Override
    public String getDeviceToken() {
	return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
	this.deviceToken = deviceToken;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IEventPipelineLog#getLevel()
     */
    @Override
    public EventPipelineLogLevel getLevel() {
	return level;
    }

    public void setLevel(EventPipelineLogLevel level) {
	this.level = level;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.instance.IEventPipelineLog#getMicroservice()
     */
    @Override
    public String getMicroservice() {
	return microservice;
    }

    public void setMicroservice(String microservice) {
	this.microservice = microservice;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IEventPipelineLog#getMessage()
     */
    @Override
    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IEventPipelineLog#getDetail()
     */
    @Override
    public String getDetail() {
	return detail;
    }

    public void setDetail(String detail) {
	this.detail = detail;
    }

    /**
     * Convert to map for Redis messaging.
     * 
     * @return
     */
    public Map<String, byte[]> toMap() {
	Map<String, byte[]> message = new HashMap<>();
	message.put(EventPipelineLog.TIMESTAMP, Longs.toByteArray(getTimestamp()));
	message.put(EventPipelineLog.SOURCE, getSource().getBytes(StandardCharsets.UTF_8));
	message.put(EventPipelineLog.LEVEL, getLevel().name().getBytes(StandardCharsets.UTF_8));
	message.put(EventPipelineLog.DEVICE_TOKEN, getDeviceToken().getBytes(StandardCharsets.UTF_8));
	if (getMicroservice() != null) {
	    message.put(EventPipelineLog.MICROSERVICE, getMicroservice().getBytes(StandardCharsets.UTF_8));
	}
	if (getMessage() != null) {
	    message.put(EventPipelineLog.MESSAGE, getMessage().getBytes(StandardCharsets.UTF_8));
	}
	if (getDetail() != null) {
	    message.put(EventPipelineLog.DETAIL, getDetail().getBytes(StandardCharsets.UTF_8));
	}
	return message;
    }

    /**
     * Create {@link EventPipelineLog} from map of fields.
     * 
     * @param map
     * @return
     */
    public static EventPipelineLog fromMap(Map<String, byte[]> map) {
	EventPipelineLog log = new EventPipelineLog();
	log.setTimestamp(Longs.fromByteArray(map.get(EventPipelineLog.TIMESTAMP)));
	log.setSource(new String(map.get(EventPipelineLog.SOURCE)));
	log.setLevel(EventPipelineLogLevel.getLevelForPrefix(new String(map.get(EventPipelineLog.LEVEL))));
	log.setDeviceToken(new String(map.get(EventPipelineLog.DEVICE_TOKEN)));
	if (map.get(EventPipelineLog.MICROSERVICE) != null) {
	    log.setMicroservice(new String(map.get(EventPipelineLog.MICROSERVICE)));
	}
	if (map.get(EventPipelineLog.MESSAGE) != null) {
	    log.setMessage(new String(map.get(EventPipelineLog.MESSAGE)));
	}
	if (map.get(EventPipelineLog.DETAIL) != null) {
	    log.setDetail(new String(map.get(EventPipelineLog.DETAIL)));
	}
	return log;
    }
}
