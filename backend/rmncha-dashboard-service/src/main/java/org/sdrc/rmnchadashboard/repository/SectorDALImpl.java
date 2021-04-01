package org.sdrc.rmnchadashboard.repository;

import java.util.Date;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.sdrc.rmnchadashboard.domain.Sector;
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
public class SectorDALImpl implements SectorDAL{

	private final Logger LOG = LoggerFactory.getLogger(getClass());

//    @Value("${elasticsearch.sectorindex.name}")
//    private String indexName;
	
	@Value("${elasticsearch.index.name}")
	private String indexName;

    @Value("${elasticsearch.sector.type}")
    private String sectorTypeName;

    @Autowired
    private ElasticsearchTemplate esTemplate;
	
	
	@Override
	public Sector findBySectorName(String name) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.matchQuery("sectorName", name)).
                build();
        List<Sector> sectors = esTemplate.queryForList(searchQuery, Sector.class);
        if(!sectors.isEmpty()) {
            return sectors.get(0);
        }
        return null;
	}


	@Override
	public Iterable<Sector> getSectorAfterSlugId(Integer slugidsector) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.rangeQuery("slugidsector").gt(slugidsector)))
				.withPageable(new PageRequest(0, 1000))
				.withSort(SortBuilders.fieldSort("slugidsector").order(SortOrder.ASC)).build();
		List<Sector> datas = esTemplate.queryForList(searchQuery, Sector.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas;
		}
		return null;
	}


	@Override
	public Iterable<Sector> getSectorAfterTimestamp(Date timestamp) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.rangeQuery("lastModified").gt(timestamp)))
				.withPageable(new PageRequest(0, 1000))
				.withSort(SortBuilders.fieldSort("slugidsector").order(SortOrder.ASC)).build();
		List<Sector> datas = esTemplate.queryForList(searchQuery, Sector.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas;
		}
		return null;
	}


	@Override
	public Sector getSectorBySlugId(Integer slugidsector) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("slugidsector",slugidsector)))
				.build();
		List<Sector> datas = esTemplate.queryForList(searchQuery, Sector.class);
		if (datas!=null && !datas.isEmpty()) {
			return datas.get(0);
		}
		return null;
	}
}
