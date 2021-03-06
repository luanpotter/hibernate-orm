/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.dialect;

import org.hibernate.cfg.Environment;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.Oracle12cIdentityColumnSupport;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.SQL2008StandardLimitHandler;

/**
 * An SQL dialect for Oracle 12c.
 *
 * @author zhouyanming (zhouyanming@gmail.com)
 */
public class Oracle12cDialect extends Oracle10gDialect {
	public Oracle12cDialect() {
		super();
		getDefaultProperties().setProperty( Environment.BATCH_VERSIONED_DATA, "true" );
	}

	@Override
	protected void registerDefaultProperties() {
		super.registerDefaultProperties();
		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "true" );
	}

	@Override
	public LimitHandler getLimitHandler() {
		return SQL2008StandardLimitHandler.INSTANCE;
	}

	@Override
	public IdentityColumnSupport getIdentityColumnSupport() {
		return new Oracle12cIdentityColumnSupport();
	}
}
