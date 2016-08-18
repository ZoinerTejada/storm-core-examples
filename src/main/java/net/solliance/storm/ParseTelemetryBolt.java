package net.solliance.storm;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;


import backtype.storm.tuple.Values;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;


public class ParseTelemetryBolt extends BaseBasicBolt{

    private static final long serialVersionUID = 1L;

    public void execute(Tuple input, BasicOutputCollector collector) {

        String value = input.getString(0);


        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode telemetryObj = mapper.readTree(value);

            if (telemetryObj.has("temp")) //assume must be a temperature reading
            {
                Values values = new Values(
                        telemetryObj.get("temp").asDouble(),
                        telemetryObj.get("createDate").asText(),
                        telemetryObj.get("deviceId").asText()
                );

                collector.emit(values);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("temp","createDate", "deviceId"));
    }
}