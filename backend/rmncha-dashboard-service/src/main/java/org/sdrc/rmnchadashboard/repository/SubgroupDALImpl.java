package org.sdrc.rmnchadashboard.repository;

import java.util.Date;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.sdrc.rmnchadashboard.domain.Subgroup;
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
public class SubgroupDALImpl implements SubgroupDAL{

	private final Logger LOG = LoggerFactory.getLogger(getClass());


	@Value("${elasticsearch.index.name}")
	private String indexName;

    @Value("${elasticsearch.subgroup.type}")
    private String subgroupTypeName;

    @Autowired
    private ElasticsearchTemplate esTemplate;
	

	@Override
	public Subgroup findBySubgroupName(String subgroupName) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.matchQuery("subgroupName", subgroupName)).
                build();
        List<Subgroup> subgroups = esTemplate.queryForList(searchQuery, Subgroup.class);
    
        if(subgroups.size() > 1) {
        	throw new RuntimeException("Returning Multiple Indicators");
        }
        else if(!subgroups.isEmpty()) {
            return subgroups.get(0);
        }
		return null;
	}
	
	@Override
	public Iterable<Subgroup> getSubgroupAfterSlugId(Integer slugidsubgroup) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.rangeQuery("slugidsubgroup").gt(slugidsubgroup)))
				.withPageable(new PageRequest(0, 1000))
				.withSort(SortBuilders.fieldSort("slugidsubgroup").order(SortOrder.ASC)).build();
		List<Subgroup> datas = esTemplate.queryForList(searchQuery, Subgroup.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas;
		}
		return null;
	}

	@Override
	public Iterable<Subgroup> getSubgroupAfterTimestamp(Date timestamp) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.rangeQuery("lastModified").gt(timestamp)))
				.withPageable(new PageRequest(0, 1000))
				.withSort(SortBuilders.fieldSort("slugidsubgroup").order(SortOrder.ASC)).build();
		List<Subgroup> datas = esTemplate.queryForList(searchQuery, Subgroup.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas;
		}
		return null;
	}

	@Override
	public Subgroup getSubgroupBySlugId(Integer slugidsubgroup) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.matchQuery("slugidsubgroup", slugidsubgroup)).build();
		List<Subgroup> subgroups = esTemplate.queryForList(searchQuery, Subgroup.class);
		if (subgroups.size() > 1) {
			throw new RuntimeException("Returning Multiple Subgroups");
		} else if (!subgroups.isEmpty()) {
			return subgroups.get(0);
		}
		return null;
	}
}
