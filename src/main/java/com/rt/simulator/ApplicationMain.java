package com.rt.simulator;

import com.rt.simulator.model.Vehicle;
import com.rt.simulator.producer.VehicleProducer;
import com.rt.simulator.util.PropertyFileReader;
import kafka.javaapi.producer.Producer;
import org.apache.log4j.Logger;
import kafka.producer.ProducerConfig;

import java.io.IOException;
import java.util.Properties;

public class ApplicationMain {

    private static final Logger logger = Logger.getLogger(ApplicationMain.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        Properties properties = PropertyFileReader.readPropertyFile();
        String zookeeper = properties.getProperty("com.rt.simulator.zookeeper");
        String brokerList = properties.getProperty("com.rt.simulator.brokerlist");
        String topic = properties.getProperty("com.rt.simulator.topic");
        logger.info("Using Zookeeper=" + zookeeper + " ,Broker-list=" + brokerList + " and topic " + topic);

        // set producer properties
        Properties producerProperties = new Properties();
        producerProperties.put("zookeeper.connect", zookeeper);
        producerProperties.put("metadata.broker.list", brokerList);
        producerProperties.put("request.required.acks","1");
        producerProperties.put("serializer.class","com.rt.simulator.util.ObjectEncoder");

        Producer<String, Vehicle> producer = new Producer<>(new ProducerConfig(producerProperties));
        VehicleProducer vehicleProducer = new VehicleProducer();
        vehicleProducer.generateVehicleEvent(producer, topic);
    }


}
