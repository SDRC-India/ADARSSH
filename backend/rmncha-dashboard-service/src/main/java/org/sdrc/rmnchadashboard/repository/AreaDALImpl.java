package org.sdrc.rmnchadashboard.repository;

import java.util.Date;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.sdrc.rmnchadashboard.domain.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;

@Repository
public class AreaDALImpl implements AreaDAL {

//	private final Logger LOG = LoggerFactory.getLogger(getClass());

	// @Value("${elasticsearch.areaindex.name}")
	// private String indexName;

	@Value("${elasticsearch.index.name}")
	private String indexName;

	@Value("${elasticsearch.area.type}")
	private String indicatorTypeName;

	@Autowired
	private ElasticsearchTemplate esTemplate;

	@Override
	public Area findByCode(String code) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withFilter(QueryBuilders.matchQuery("code", code))
				.build();
		List<Area> areas = esTemplate.queryForList(searchQuery, Area.class);

		if (areas.size() > 1) {
			throw new RuntimeException("Returning Multiple Areas");
		} else if (!areas.isEmpty()) {
			return areas.get(0);
		}
		return null;
	}
	@Override
	public Iterable<Area> getAreaAfterSlugId(Integer slugidarea) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.rangeQuery("slugidarea").gt(slugidarea)))
				.withPageable(new PageRequest(0, 10000))
				.withSort(SortBuilders.fieldSort("slugidarea").order(SortOrder.ASC)).build();
		List<Area> datas = esTemplate.queryForList(searchQuery, Area.class);
		
		if (datas!=null && !datas.isEmpty()) {
			return datas;
		}
		return null;
	}
	@Override
	public Iterable<Area> getAreaAfterTimestamp(Date timestamp) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.rangeQuery("lastModified").gt(timestamp)))
				.withPageable(new PageRequest(0, 10000))
				.withSort(SortBuilders.fieldSort("slugidarea").order(SortOrder.ASC)).build();
		List<Area> datas = esTemplate.queryForList(searchQuery, Area.class);
		
		if (datas!=null && !datas.isEmpty()) {
			return datas;
		}
		return null;
	}
	@Override
	public Area getAreaBySlugId(Integer slugidarea) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("slugidarea",slugidarea)))
				.build();
		List<Area> datas = esTemplate.queryForList(searchQuery, Area.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas.get(0);
		}
		return null;
	}
	
	
	
	
}
