/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010 by Red Hat Inc and/or its affiliates or by
 * third-party contributors as indicated by either @author tags or express
 * copyright attribution statements applied by the authors.  All
 * third-party contributions are distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.metamodel.spi.relational;

import java.util.Arrays;
import java.util.List;

import org.hibernate.dialect.Dialect;

/**
 * Models a SQL <tt>INDEX</tt> defined as UNIQUE
 *
 * @author Gavin King
 * @author Steve Ebersole
 * @author Brett Meyer
 */
public class UniqueKey extends AbstractConstraint {

	public UniqueKey() {
		super( null, null );
	}

	protected UniqueKey(Table table, String name) {
		super( table, name );
	}

	@Override
	public String[] sqlCreateStrings(Dialect dialect) {
		return new String[] { dialect.getUniqueDelegate().getAlterTableToAddUniqueKeyCommand( this ) };
	}

	@Override
	public String[] sqlDropStrings(Dialect dialect) {
		return new String[] { dialect.getUniqueDelegate().getAlterTableToDropUniqueKeyCommand( this ) };
	}

	@Override
    public String sqlConstraintStringInAlterTable(Dialect dialect) {
		// not used
		return "";
	}
	
	/**
	 * If a constraint is not explicitly named, this is called to generate
	 * a unique hash using the table and column names.
	 * Static so the name can be generated prior to creating the Constraint.
	 * They're cached, keyed by name, in multiple locations.
	 * 
	 * @param table
	 * @param columnNames
	 * @return String The generated name
	 */
	public static String generateName(TableSpecification table, String... columnNames) {
		// Use a concatenation that guarantees uniqueness, even if identical names
		// exist between all table and column identifiers.

		StringBuilder sb = new StringBuilder( "table`" + table.getLogicalName().getText() + "`" );

		// Ensure a consistent ordering of columns, regardless of the order
		// they were bound.
		// Clone the list, as sometimes a set of order-dependent Column
		// bindings are given.
		String[] alphabeticalColumns = columnNames.clone();
		Arrays.sort( alphabeticalColumns );
		for ( String columnName : alphabeticalColumns ) {
			sb.append( "column`" + columnName + "`" );
		}
		return "UK_" + hashedName( sb.toString() );
	}

	/**
	 * Helper method for {@link #generateName(String, TableSpecification, String...)}.
	 * 
	 * @param table
	 * @param columns
	 * @return String The generated name
	 */
	public static String generateName(TableSpecification table, List<Column> columns) {
		String[] columnNames = new String[columns.size()];
		for ( int i = 0; i < columns.size(); i++ ) {
			columnNames[i] = columns.get( i ).getColumnName().getText();
		}
		return generateName( table, columnNames );
	}

	/**
	 * Helper method for {@link #generateName(String, TableSpecification, String...)}.
	 * Generates and sets a name for this existing constraint.
	 */
	public void generateName() {
		setName( generateName( getTable(), getColumns() ) );
	}
	
	@Override
	public String getExportIdentifier() {
		StringBuilder sb = new StringBuilder( getTable().getLoggableValueQualifier() );
		sb.append( ".UK" );
		for ( Column column : getColumns() ) {
			sb.append( '_' ).append( column.getColumnName().getText() );
		}
		return sb.toString();
	}
}