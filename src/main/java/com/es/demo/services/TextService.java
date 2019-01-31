package com.es.demo.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.InternalStats;
import org.elasticsearch.search.aggregations.metrics.stats.StatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import com.es.demo.model.entity.TextEntity;
import com.es.demo.model.enums.EventType;

@Service
public class TextService {

	@Autowired
	private ElasticsearchTemplate template;

	@Autowired
	private TextRepository tRepository;

	public void save(TextEntity textEntity) {
		tRepository.save(textEntity);
	}

	public void update() {
	}

	public void delete() {
	}

	// 求浏览量最高和最低的carType
	public Map<String, String> getVisitCountByCarType(DateTime start,DateTime end) {
		Map<String, String> map = new HashMap<>();
		// 构建查询
		NativeSearchQueryBuilder nsqBuilder = new NativeSearchQueryBuilder();
		// 构建查询条件
		BoolQueryBuilder builder = QueryBuilders.boolQuery();
		if (start!=null&&end!=null) {
			builder.must(
					QueryBuilders.rangeQuery("eventtime").from(start).to(end));
		}
		
		builder.must(QueryBuilders.matchPhraseQuery("eventtype", EventType.visit.name()));
		
		nsqBuilder.withQuery(builder);
		// 指定要查询的索引库的名称和类型，对应文档@Document中设置的indedName和type
		nsqBuilder.withIndices("onepiece").withTypes("textinfo");

		// 聚合 先按count，降序排
		TermsAggregationBuilder visitTb = AggregationBuilders.terms("visitCountGroup").field("carType")
				.order(BucketOrder.aggregation("visitCount", false));
		ValueCountAggregationBuilder visitCB = AggregationBuilders.count("visitCount").field("id");
		visitTb.subAggregation(visitCB);
		nsqBuilder.addAggregation(visitTb);

		// 构建查询对象
		NativeSearchQuery nativeSearchQuery = nsqBuilder.build();
		Aggregations aggregations = template.query(nativeSearchQuery, new ResultsExtractor<Aggregations>() {

			@Override
			public Aggregations extract(SearchResponse response) {
				return response.getAggregations();
			}

		});

		// 转换成map集合
		Map<String, Aggregation> aggregationMap = aggregations.asMap();
		Terms visitCountGroup = (StringTerms) aggregationMap.get("visitCountGroup");
		@SuppressWarnings("unchecked")
		List<Bucket> visitCountList = (List<Bucket>) visitCountGroup.getBuckets();
		if (visitCountList.size() > 0) {
			InternalValueCount maxVisitCount = (InternalValueCount) visitCountList.get(0).getAggregations().asMap()
					.get("visitCount");
			map.put("浏览量最高的carType", (visitCountList.get(0).getKey().toString()));
			map.put("最高浏览量", maxVisitCount.getValueAsString());
			InternalValueCount minVisitCount = (InternalValueCount) visitCountList.get(visitCountList.size() - 1)
					.getAggregations().asMap().get("visitCount");
			map.put("浏览量最低的carType", (visitCountList.get(visitCountList.size() - 1).getKey().toString()));
			map.put("最低浏览量", minVisitCount.getValueAsString());
		}

		return map;

	}

	// 求各种carType的交易总额
	public Map<String, BigDecimal> getCarTypeDealAmount() {
		Map<String, BigDecimal> map = new HashMap<>();
		BoolQueryBuilder builder = QueryBuilders.boolQuery();
		builder.must(QueryBuilders.rangeQuery("eventtime").lt(new DateTime()));
		builder.must(QueryBuilders.matchPhraseQuery("eventtype", EventType.deal.name()));

		NativeSearchQueryBuilder nsqBuild = new NativeSearchQueryBuilder();
		nsqBuild.withIndices("onepiece").withTypes("textinfo");
		nsqBuild.withQuery(builder);

		TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("carTypes").field("carType")
				.order(BucketOrder.aggregation("amount", false));
		SumAggregationBuilder sumAggregationBuilder = AggregationBuilders.sum("amount").field("carprice");
		termsAggregationBuilder.subAggregation(sumAggregationBuilder);
		nsqBuild.addAggregation(termsAggregationBuilder);

		// 构建查询对象
		NativeSearchQuery nativeSearchQuery = nsqBuild.build();
		Aggregations aggregations = template.query(nativeSearchQuery, new ResultsExtractor<Aggregations>() {

			@Override
			public Aggregations extract(SearchResponse response) {
				return response.getAggregations();
			}

		});
		Map<String, Aggregation> aggregationMap = aggregations.asMap();
		Terms carGroup = (StringTerms) aggregationMap.get("carTypes");
		@SuppressWarnings("unchecked")
		List<Bucket> carList = (List<Bucket>) carGroup.getBuckets();
		for (Bucket bucket : carList) {
			InternalSum dealAmount = (InternalSum) bucket.getAggregations().asMap().get("amount");
			map.put(bucket.getKeyAsString(),
					new BigDecimal(dealAmount.getValue()).setScale(2, BigDecimal.ROUND_HALF_UP));
		}
		return map;
	}

	// 特定车型的成交量，交易总额,平均交易额，最高交易额，最低交易额
	public Map<String, Double> getInfoByType() {
		Map<String, Double> map = new HashMap<>();
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("carType", "SUV"));
		boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventtype", EventType.deal.name()));

		NativeSearchQueryBuilder nsqBuild = new NativeSearchQueryBuilder();
		nsqBuild.withIndices("onepiece").withTypes("textinfo");
		nsqBuild.withQuery(boolQueryBuilder);

		TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("cardeal").field("carType");
		StatsAggregationBuilder stats = AggregationBuilders.stats("amountAgg").field("carprice");
		termsAggregationBuilder.subAggregation(stats);
		nsqBuild.addAggregation(termsAggregationBuilder);

		NativeSearchQuery nativeSearchQuery = nsqBuild.build();

		Aggregations aggregations = template.query(nativeSearchQuery, new ResultsExtractor<Aggregations>() {

			@Override
			public Aggregations extract(SearchResponse response) {
				return response.getAggregations();
			}

		});
		Map<String, Aggregation> aggregationMap = aggregations.asMap();
		Terms carGroup = (StringTerms) aggregationMap.get("cardeal");
		@SuppressWarnings("unchecked")
		List<Bucket> carList = (List<Bucket>) carGroup.getBuckets();
		InternalStats amountAgg = (InternalStats) carList.get(0).getAggregations().asMap().get("amountAgg");
		map.put("count", (double) amountAgg.getCount());
		map.put("min", amountAgg.getMin());
		map.put("min", amountAgg.getMin());
		map.put("avg", amountAgg.getAvg());
		map.put("sum", amountAgg.getSum());
		return map;
	}

}
