package com.es.demo.services;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import com.es.demo.model.entity.TextEntity;
@EnableElasticsearchRepositories
public interface TextRepository extends ElasticsearchRepository<TextEntity, Long>{

}
