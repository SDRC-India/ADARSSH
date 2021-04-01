package org.sdrc.rmnchadashboard.repository;

import java.util.Date;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.sdrc.rmnchadashboard.domain.Indicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;

@Repository
public class IndicatorDALImpl implements IndicatorDAL{

	private final Logger LOG = LoggerFactory.getLogger(getClass());

//    @Value("${elasticsearch.indicatorindex.name}")
//    private String indexName;
	
	@Value("${elasticsearch.index.name}")
	private String indexName;

    @Value("${elasticsearch.indicator.type}")
    private String indicatorTypeName;

    @Autowired
    private ElasticsearchTemplate esTemplate;
	
	@Override
	public List<Indicator> findAll() {
		 SearchQuery getAllQuery = new NativeSearchQueryBuilder()
	                .withQuery(QueryBuilders.matchAllQuery()).build();
	        return esTemplate.queryForList(getAllQuery, Indicator.class);
	}

	@Override
	public Indicator findByName(String name) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.matchQuery("iName", name)).
                build();
//		System.out.println("Searching indicator with name::"+name);
        List<Indicator> indicators = esTemplate.queryForList(searchQuery, Indicator.class);
//        System.out.println(indicators);
        if(indicators.size() > 1) {
        	System.out.println(indicators);
        	throw new RuntimeException("Returning Multiple Indicators");
        }
        else if(!indicators.isEmpty()) {
            return indicators.get(0);
        }
        return null;
	}

	@Override
	public Iterable<Indicator> getIndicatorsAfterSlugId(Integer slugidindicator) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.rangeQuery("slugidindicator").gt(slugidindicator)))
				.withPageable(new PageRequest(0, 1000))
				.withSort(SortBuilders.fieldSort("slugidindicator").order(SortOrder.ASC)).build();
		List<Indicator> datas = esTemplate.queryForList(searchQuery, Indicator.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas;
		}
		return null;
	}

	@Override
	public Iterable<Indicator> getIndicatorsAfterTimestamp(Date timestamp) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.rangeQuery("lastModified").gt(timestamp)))
				.withPageable(new PageRequest(0, 1000))
				.withSort(SortBuilders.fieldSort("slugidindicator").order(SortOrder.ASC)).build();
		List<Indicator> datas = esTemplate.queryForList(searchQuery, Indicator.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas;
		}
		return null;
	}

	@Override
	public Indicator getIndicatorBySlugId(Integer slugidindicator) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("slugidindicator",slugidindicator)))
				.build();
		List<Indicator> datas = esTemplate.queryForList(searchQuery, Indicator.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas.get(0);
		}
		return null;
	}
	
	
}
