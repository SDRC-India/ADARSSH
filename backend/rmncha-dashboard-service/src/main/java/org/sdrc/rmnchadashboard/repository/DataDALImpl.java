package org.sdrc.rmnchadashboard.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.sdrc.rmnchadashboard.domain.Data;
import org.sdrc.rmnchadashboard.domain.Indicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;

@Repository
public class DataDALImpl implements DataDAL {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Value("${elasticsearch.index.name}")
	private String indexName;

	@Value("${elasticsearch.data.type}")
	private String dataTypeName;

	@Autowired
	private ElasticsearchTemplate esTemplate;

	@Override
	public List<Data> findAllData() {
		SearchQuery getAllQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery()).build();
		return esTemplate.queryForList(getAllQuery, Data.class);
	}

	@Override
	public List<Data> findByParentAreaCodeAndSourceAndTimePeriod(String code, String src, String tp) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.matchQuery("area.parentAreaCode", code))
				.withFilter(QueryBuilders.matchQuery("src.id", code)).withFilter(QueryBuilders.matchQuery("tp", tp))
				.build();
		List<Data> datas = esTemplate.queryForList(searchQuery, Data.class);
		if (!datas.isEmpty()) {
			return datas;
		}
		return null;
	}

	@Override
	public List<Data> findByIndicatorAndParentAreaCodeAndSourceAndTimePeriod(String indicator, String code, String src,
			String tp) {

		SearchQuery searchQuery = new NativeSearchQueryBuilder().withFilter(QueryBuilders.boolQuery()
				.must(QueryBuilders.nestedQuery("indicator", (QueryBuilders.matchQuery("indicator.iName", indicator))))
				.must(QueryBuilders.nestedQuery("area", (QueryBuilders.matchQuery("area.parentAreaCode", code))))
				.must(QueryBuilders.nestedQuery("src", (QueryBuilders.matchQuery("src.id", src))))
				.must(QueryBuilders.matchQuery("tp", tp))).withPageable(new PageRequest(0, 1000)).build();

		Page<Data> datas = esTemplate.queryForPage(searchQuery, Data.class);
		datas.getContent().stream().collect(Collectors.toList());
		List<Data> dataList = new ArrayList<>();
		Iterator<Data> itr = datas.iterator();
		while (itr.hasNext()) {
			dataList.add(itr.next());
		}
		return dataList.isEmpty() ? null : dataList;
	}

	public Iterable<Data> getInitalDataForIndicator(String iName) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.nestedQuery("indicator",
								(QueryBuilders.matchQuery("indicator.iName", iName))))
						.must(QueryBuilders.nestedQuery("area",
								(QueryBuilders.matchQuery("area.parentAreaCode", "IND")))))
				.withPageable(new PageRequest(0, 10000)).withSort(SortBuilders.fieldSort("tp").order(SortOrder.DESC))
				.build();
		List<Data> datas = esTemplate.queryForList(searchQuery, Data.class);
		if (!datas.isEmpty()) {
			return datas;
		}
		return null;

	}

	@Override
	public Iterable<Data> getDataAfterSlugId(Long slugiddata) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("slugiddata").gt(slugiddata)))
				.withPageable(new PageRequest(0, 1000))
				.withSort(SortBuilders.fieldSort("slugiddata").order(SortOrder.ASC)).build();
		List<Data> datas = esTemplate.queryForList(searchQuery, Data.class);
		if (datas != null && !datas.isEmpty()) {
			return datas;
		}
		return null;
	}

	@Override
	public Iterable<Data> getDataAfterTimestamp(Date timestamp) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.rangeQuery("lastModified").gt(timestamp.getTime())))
				.withSort(SortBuilders.fieldSort("slugiddata").order(SortOrder.ASC)).build();
//		System.out.println("Query::" + searchQuery.getQuery());
//		System.out.println("Query::" + searchQuery.getFilter());
		List<Data> datas = esTemplate.queryForList(searchQuery, Data.class);
		if (datas != null && !datas.isEmpty()) {
			return datas;
		}
		return null;
	}

	@Override
	public List<Data> findByIndicator(Indicator indicator) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.nestedQuery("indicator",
								(QueryBuilders.matchQuery("indicator.iName", indicator.getiName())))))
				.withPageable(new PageRequest(0, 1000000)).build();
		List<Data> datas = esTemplate.queryForList(searchQuery, Data.class);
		if (!datas.isEmpty()) {
			return datas;
		}
		return null;
	}

	@Override
	public Data getDataBySlugId(Long slugiddata) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("slugiddata", slugiddata))).build();
		List<Data> datas = esTemplate.queryForList(searchQuery, Data.class);
		if (datas != null && !datas.isEmpty()) {
			return datas.get(0);
		}
		return null;
	}


	@Override
	public Iterable<Data> getDataAfterTimestamp(List<Long> indicatorSlugIds, List<String> areaCodes, Integer searchType,
			Pageable request) {
		SearchQuery searchQuery =null;
		
		BoolQueryBuilder boolQueryBuilder =null;
				
		switch(searchType) {
		case 1:
			boolQueryBuilder = QueryBuilders.boolQuery()
			.must(QueryBuilders.nestedQuery("indicator",
							QueryBuilders.matchQuery("indicator.slugidindicator", indicatorSlugIds.get(0))));
			for(String areaCode : areaCodes) {
				boolQueryBuilder.should(QueryBuilders.nestedQuery("area",
						QueryBuilders.matchQuery("area.code", areaCode)));
			}
			boolQueryBuilder.minimumNumberShouldMatch(1);			
			searchQuery = new NativeSearchQueryBuilder()
			.withQuery(boolQueryBuilder)
			.withPageable(request)
			.withSort(SortBuilders.fieldSort("slugiddata").order(SortOrder.ASC)).build();
			break;
		case 2:
			boolQueryBuilder = QueryBuilders.boolQuery()
			.must(QueryBuilders.nestedQuery("area",
							QueryBuilders.matchQuery("area.code", areaCodes.get(0))));
			for(Long indicatorId : indicatorSlugIds) {
				boolQueryBuilder.should(QueryBuilders.nestedQuery("indicator",
						QueryBuilders.matchQuery("indicator.slugidindicator", indicatorId)));
			}
			boolQueryBuilder.minimumNumberShouldMatch(1);
			searchQuery = new NativeSearchQueryBuilder()
			.withQuery(boolQueryBuilder)
			.withPageable(request)
			.withSort(SortBuilders.fieldSort("slugiddata").order(SortOrder.ASC)).build();
			break;
			
		}

		System.out.println("Query::" + searchQuery.getQuery());
		System.out.println("Query::" + searchQuery.getFilter());
		List<Data> datas = esTemplate.queryForList(searchQuery, Data.class);
		if (datas != null && !datas.isEmpty()) {
			return datas;
		}
		return datas;
	}
	
	
}
