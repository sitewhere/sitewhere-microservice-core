/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.RetriableException;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Base class for components that produce messages that are forwarded to a Kafka
 * topic.
 */
public abstract class MicroserviceKafkaProducer extends TenantEngineLifecycleComponent
	implements IMicroserviceKafkaProducer {

    /** Producer */
    private KafkaProducer<String, byte[]> producer;

    /** Kafka acknowledgement policy */
    private AckPolicy ackPolicy;

    /** Indicator for whether Kafka is available */
    private CountDownLatch kafkaAvailable;

    /** Executor service for waiter thread */
    ExecutorService waiterService;

    public MicroserviceKafkaProducer(AckPolicy ackPolicy) {
	this.ackPolicy = ackPolicy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLogger().info("Producer connecting to Kafka: " + KafkaUtils.getBootstrapServers(getMicroservice()));
	getLogger().info("Will be producing messages for: " + getTargetTopicName());
	this.kafkaAvailable = new CountDownLatch(1);
	this.waiterService = Executors.newSingleThreadExecutor();
	getWaiterService().execute(new KafkaWaiter(this, getTargetTopicName()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getProducer() != null) {
	    getProducer().close();
	}
	if (getWaiterService() != null) {
	    getWaiterService().shutdown();
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#send(java.
     * lang.String, byte[])
     */
    @Override
    public Future<RecordMetadata> send(String key, byte[] message) throws SiteWhereException {
	while (true) {
	    ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(getTargetTopicName(), key,
		    message);
	    try {
		if (getKafkaAvailable().getCount() != 0) {
		    getLogger().info("Producer waiting on Kafka to become available...");
		    getKafkaAvailable().await();
		}
		if (getProducer() == null) {
		    this.producer = new KafkaProducer<String, byte[]>(buildConfiguration());
		}
		return getProducer().send(record);
	    } catch (RetriableException e) {
		// Wait before attempting to send again.
		try {
		    getLogger().info(
			    String.format("Got retriable exception [%s] while sending Kafka payload. Waiting to retry.",
				    e.getMessage()));
		    Thread.sleep(5000);
		} catch (InterruptedException e1) {
		    getLogger().info("Interrupted while waiting to send Kafka payload.");
		}
	    } catch (InterruptedException e) {
		throw new SiteWhereException("Producer interrupted while waiting for Kafka.", e);
	    } catch (IllegalStateException e) {
		throw new SiteWhereException("Producer unable to send record.", e);
	    } catch (Throwable e) {
		throw new SiteWhereException("Unhandled exception in producer while sending record.", e);
	    }
	}
    }

    /**
     * Build configuration settings used by producer.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected Properties buildConfiguration() throws SiteWhereException {
	Properties config = new Properties();
	config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaUtils.getBootstrapServers(getMicroservice()));
	config.put(ProducerConfig.ACKS_CONFIG, getAckPolicy().getConfig());
	config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
	config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
	return config;
    }

    /**
     * Thread that waits for Kafka to become available.
     */
    private class KafkaWaiter extends KafkaTopicWaiter {

	public KafkaWaiter(ITenantEngineLifecycleComponent component, String topicName) {
	    super(component, topicName);
	}

	/*
	 * @see com.sitewhere.microservice.kafka.KafkaTopicWaiter#onTopicAvailable()
	 */
	@Override
	protected void onTopicAvailable() {
	    getKafkaAvailable().countDown();
	}
    }

    protected KafkaProducer<String, byte[]> getProducer() {
	return producer;
    }

    protected void setProducer(KafkaProducer<String, byte[]> producer) {
	this.producer = producer;
    }

    protected AckPolicy getAckPolicy() {
	return ackPolicy;
    }

    protected void setAckPolicy(AckPolicy ackPolicy) {
	this.ackPolicy = ackPolicy;
    }

    protected CountDownLatch getKafkaAvailable() {
	return kafkaAvailable;
    }

    protected void setKafkaAvailable(CountDownLatch kafkaAvailable) {
	this.kafkaAvailable = kafkaAvailable;
    }

    protected ExecutorService getWaiterService() {
	return waiterService;
    }

    protected void setWaiterService(ExecutorService waiterService) {
	this.waiterService = waiterService;
    }
}