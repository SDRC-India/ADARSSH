package org.sdrc.rmnchadashboard.repository;

import java.util.Date;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.sdrc.rmnchadashboard.domain.Unit;
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
public class UnitDALImpl implements UnitDAL{
	private final Logger LOG = LoggerFactory.getLogger(getClass());

//    @Value("${elasticsearch.unitindex.name}")
//    private String indexName;
	
	@Value("${elasticsearch.index.name}")
	private String indexName;

    @Value("${elasticsearch.unit.type}")
    private String indicatorTypeName;

    @Autowired
    private ElasticsearchTemplate esTemplate;
	
	
	@Override
	public Unit findByUnitName(String name) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.matchQuery("unitName", name)).
                build();
        List<Unit> units = esTemplate.queryForList(searchQuery, Unit.class);
        if(!units.isEmpty()) {
            return units.get(0);
        }
        return null;
	}
	
	@Override
	public Iterable<Unit> getUnitAfterSlugId(Integer slugidunit) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.rangeQuery("slugidunit").gt(slugidunit)))
				.withPageable(new PageRequest(0, 1000))
				.withSort(SortBuilders.fieldSort("slugidunit").order(SortOrder.ASC)).build();
		List<Unit> datas = esTemplate.queryForList(searchQuery, Unit.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas;
		}
		return null;
	}

	@Override
	public Iterable<Unit> getUnitAfterTimestamp(Date timestamp) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.rangeQuery("lastModified").gt(timestamp)))
				.withPageable(new PageRequest(0, 1000))
				.withSort(SortBuilders.fieldSort("slugidunit").order(SortOrder.ASC)).build();
		List<Unit> datas = esTemplate.queryForList(searchQuery, Unit.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas;
		}
		return null;
	}

	@Override
	public Unit getUnitBySlugId(Integer slugidunit) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("slugidunit",slugidunit)))
				.build();
		List<Unit> datas = esTemplate.queryForList(searchQuery, Unit.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas.get(0);
		}
		return null;
	}
}
