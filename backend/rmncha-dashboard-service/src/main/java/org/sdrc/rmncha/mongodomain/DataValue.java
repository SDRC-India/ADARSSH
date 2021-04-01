package org.sdrc.rmncha.mongodomain;

import java.io.Serializable;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
@CompoundIndexes({
    @CompoundIndex(name = "data_val", def = "{'areaId' : 1, 'tp': 1, 'inid': 1}")
})
public class DataValue implements Serializable {

	private static final long serialVersionUID = -1636923412521819247L;
	private String id;
	private Integer areaId;
	private Double dataValue;
	private Integer tp;
	private String _case;
	private Integer inid;
	private String numerator;
	private String denominator;
	private Integer f1FacilityType;
	private Integer f1FacilityLevel;
}
