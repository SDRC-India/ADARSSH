package org.sdrc.rmnchadashboard.repository;

import java.util.Date;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.sdrc.rmnchadashboard.domain.Source;
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
public class SourceDALImpl implements SourceDAL {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

//	@Value("${elasticsearch.sourceindex.name}")
//	private String indexName;
	
	@Value("${elasticsearch.index.name}")
	private String indexName;

	@Value("${elasticsearch.source.type}")
	private String sourceTypeName;

	@Autowired
	private ElasticsearchTemplate esTemplate;

	@Override
	public Source findBySourceName(String source) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.matchQuery("sourceName", source)).build();
		List<Source> sources = esTemplate.queryForList(searchQuery, Source.class);
//		System.out.println(sources);
		if (sources.size() > 1) {
			throw new RuntimeException("Returning Multiple Indicators");
		} else if (!sources.isEmpty()) {
			return sources.get(0);
		}
		return null;
	}
	
	@Override
	public Iterable<Source> getSourceAfterSlugId(Integer slugidsource) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.rangeQuery("slugidsource").gt(slugidsource)))
				.withPageable(new PageRequest(0, 1000))
				.withSort(SortBuilders.fieldSort("slugidsource").order(SortOrder.ASC)).build();
		List<Source> datas = esTemplate.queryForList(searchQuery, Source.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas;
		}
		return null;
	}

	@Override
	public Iterable<Source> getSourceAfterTimestamp(Date timestamp) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.rangeQuery("lastModified").gt(timestamp)))
				.withPageable(new PageRequest(0, 1000))
				.withSort(SortBuilders.fieldSort("slugidsource").order(SortOrder.ASC)).build();
		List<Source> datas = esTemplate.queryForList(searchQuery, Source.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas;
		}
		return null;
	}

	@Override
	public Source getSourceBySlugId(Integer slugidsource) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("slugidsource",slugidsource)))
				.build();
		List<Source> datas = esTemplate.queryForList(searchQuery, Source.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas.get(0);
		}
		return null;
	}

}
