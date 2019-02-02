package com.rt.simulator.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rt.simulator.model.Vehicle;
import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;
import org.apache.log4j.Logger;

public class ObjectEncoder implements Encoder<Vehicle> {

    private static final Logger logger = Logger.getLogger(ObjectEncoder.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    public ObjectEncoder(VerifiableProperties verifiableProperties) {
    }

    @Override
    public byte[] toBytes(Vehicle vehicle) {
        try{
            String message = objectMapper.writeValueAsString(vehicle);
            logger.info(message);
            return message.getBytes();
        }catch (JsonProcessingException e) {
            logger.error("Error in serialization", e);
            return null;
        }
    }
}
