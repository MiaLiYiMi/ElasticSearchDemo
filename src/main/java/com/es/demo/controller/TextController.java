package com.es.demo.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.es.demo.model.entity.TextEntity;
import com.es.demo.model.enums.CarType;
import com.es.demo.model.enums.EventType;
import com.es.demo.services.TextService;

@RestController
@RequestMapping("dataCenter")
public class TextController {

	private Logger logger = LoggerFactory.getLogger(TextController.class);

	@Autowired
	private TextService textService;

	@RequestMapping(value = "init", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> init(
			@RequestParam(value = "carName", required = true) String carName,
			@RequestParam(value = "carType", required = true) CarType carType,
			@RequestParam(value = "carprice", required = true) BigDecimal carprice,
			@RequestParam(value = "carcolor", required = true) String carcolor,
			@RequestParam(value = "eventtype", required = true) EventType eventtype) {

		TextEntity point = new TextEntity();
		point.setId(new Date().getTime());
		point.setCarName(carName);
		point.setCarType(carType.name());
		point.setCarcolor(carcolor);
		point.setCarprice(carprice);
		point.setEventtype(eventtype.name());
		point.setEventtime(new Date());
		textService.save(point);
		return new ResponseEntity<String>("true", HttpStatus.OK);
	}

	@RequestMapping(value = "query", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> queryTextStatistics() {
		
		logger.info("浏览量统计...");
		Map<String, String> result = textService.getVisitCountByCarType();
		for(Map.Entry<String, String> entry:result.entrySet()){
			logger.info(entry.getKey()+":"+entry.getValue());
			}
		
		logger.info("交易额统计...");
		Map<String, BigDecimal> resultt=textService.getCarTypeDealAmount();
		for(Map.Entry<String, BigDecimal> entry:resultt.entrySet()){
			logger.info(entry.getKey()+"本周交易额:"+entry.getValue());
		}
		
		logger.info("SUV车型的销售统计：");
		Map<String, Double> resulttt=textService.getInfoByType();
		for(Map.Entry<String, Double> entry:resulttt.entrySet()){
			logger.info(entry.getKey()+":"+entry.getValue());
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
