package org.sdrc.rmnchadashboard.repository;

import java.util.Date;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.sdrc.rmnchadashboard.domain.AreaLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;

@Repository
public class AreaLevelDALImpl implements AreaLevelDAL{

//	private final Logger LOG = LoggerFactory.getLogger(getClass());

	// @Value("${elasticsearch.areaindex.name}")
	// private String indexName;

	@Value("${elasticsearch.index.name}")
	private String indexName;

	@Value("${elasticsearch.arealevel.type}")
	private String indicatorTypeName;

	@Autowired
	private ElasticsearchTemplate esTemplate;


	@Override
	public Iterable<AreaLevel> getAreaLevelAfterSlugId(int slugidarealevel) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.rangeQuery("slugidarealevel").gt(slugidarealevel)))
				.withPageable(new PageRequest(0, 1000))
				.withSort(SortBuilders.fieldSort("slugidarealevel").order(SortOrder.ASC)).build();
		List<AreaLevel> datas = esTemplate.queryForList(searchQuery, AreaLevel.class);
		System.out.println("Query::"+ searchQuery.getQuery());
		System.out.println("Query::"+ searchQuery.getFilter());
		if (datas!=null && !datas.isEmpty()) {
			return datas;
		}
		
		return null;
	}


	@Override
	public Iterable<AreaLevel> getAreaLevelAfterTimestamp(Date timestamp) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.rangeQuery("lastModified").gt(timestamp)))
				.withPageable(new PageRequest(0, 10000))
				.withSort(SortBuilders.fieldSort("slugidarea").order(SortOrder.ASC)).build();
		
		List<AreaLevel> datas = esTemplate.queryForList(searchQuery, AreaLevel.class);
		
		if (datas!=null && !datas.isEmpty()) {
			return datas;
		}
		return null;
	}


	@Override
	public AreaLevel findBySlugId(int slugidarealevel) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("slugidarealevel",slugidarealevel)))
				.build();
		List<AreaLevel> datas = esTemplate.queryForList(searchQuery, AreaLevel.class);
		if (!datas.isEmpty()) {
			return datas.get(0);
		}
		return null;
	}


	@Override
	public AreaLevel getAreaBySlugId(Integer slugidarealevel) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("slugidarealevel",slugidarealevel)))
				.build();
		List<AreaLevel> datas = esTemplate.queryForList(searchQuery, AreaLevel.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas.get(0);
		}
		return null;
	}
}
