package com.es.demo.model.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "onepiece",type = "textinfo")
public class TextEntity {

	@Field(type=FieldType.Long)
	private long id;
	
	@Field(type=FieldType.Keyword)
	private String carName;
	
	@Field(type=FieldType.Keyword)
	private String carType;
	
	@Field(type=FieldType.Double)
	private BigDecimal carprice;
	
	@Field(type=FieldType.Keyword)
	private String carcolor;
	
	@Field(type=FieldType.Keyword)
	private String eventtype;
	
	@Field(type=FieldType.Date)
	private Date eventtime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCarName() {
		return carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public BigDecimal getCarprice() {
		return carprice;
	}

	public void setCarprice(BigDecimal carprice) {
		this.carprice = carprice;
	}

	public String getCarcolor() {
		return carcolor;
	}

	public void setCarcolor(String carcolor) {
		this.carcolor = carcolor;
	}

	public String getEventtype() {
		return eventtype;
	}

	public void setEventtype(String eventtype) {
		this.eventtype = eventtype;
	}

	public Date getEventtime() {
		return eventtime;
	}

	public void setEventtime(Date eventtime) {
		this.eventtime = eventtime;
	}
	
	
}
