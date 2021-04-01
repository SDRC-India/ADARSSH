package org.sdrc.rmnchadashboard.utils;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL94Dialect;

import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;

public class RmnchaPostgresDialect  extends PostgreSQL94Dialect {

	public RmnchaPostgresDialect() {
		super();
		this.registerHibernateType(
	            Types.OTHER, JsonNodeBinaryType.class.getName());
	}

}
