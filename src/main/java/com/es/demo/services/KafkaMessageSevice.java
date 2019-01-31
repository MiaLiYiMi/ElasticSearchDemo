package com.es.demo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.es.demo.model.entity.TextEntity;

@Service
public class KafkaMessageSevice {
	
	private Logger logger = LoggerFactory.getLogger(KafkaMessageSevice.class);
	
	//kafkaTemplate是整个生产者的生产消息的入口
	@Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
	
	@Autowired
	private TextService textService;
	
	public void sender(String topic,String key,String message) {
		kafkaTemplate.send(topic, key, message);
	}
	
	//用listener的方式，去listener对应的topic的produce的消息获取
	@KafkaListener(topics={"SUV","MPV","RV"})
	public void receiver(String msg) {
		logger.info(msg);
		textService.save(JSONObject.parseObject(msg, TextEntity.class));
		
	}

}
