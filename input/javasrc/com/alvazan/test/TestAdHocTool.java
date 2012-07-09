package com.alvazan.test;

import org.junit.Test;

import com.alvazan.orm.api.spi.db.NoSqlRawSession;
import com.alvazan.orm.api.spi.index.IndexReaderWriter;
import com.alvazan.orm.api.spi.layer2.NoSqlSession;
import com.alvazan.orm.impl.meta.data.MetaQuery;
import com.alvazan.orm.impl.meta.query.MetaColumnDbo;
import com.alvazan.orm.impl.meta.query.MetaDatabase;
import com.alvazan.orm.impl.meta.query.MetaTableDbo;
import com.alvazan.orm.impl.meta.scan.ScannerForQuery;
import com.alvazan.orm.layer2.nosql.cache.NoSqlReadCacheImpl;
import com.alvazan.orm.layer2.nosql.cache.NoSqlWriteCacheImpl;
import com.alvazan.orm.layer3.spi.db.inmemory.InMemorySession;
import com.alvazan.orm.layer3.spi.index.inmemory.MemoryIndexWriter;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;

public class TestAdHocTool implements Module {

	@SuppressWarnings("rawtypes")
	@Test
	public void testBasic() {
		Injector injector = Guice.createInjector(this);
		ScannerForQuery scanner = injector.getInstance(ScannerForQuery.class);
		String sql = "select * FROM MyEntity e WHERE e.cat >= :hello";
		
		MetaQuery metaQuery = scanner.parseQuery(sql);
		
		NoSqlSession instance = injector.getInstance(NoSqlSession.class);
		
		String indexName = metaQuery.getIndexName();
		
		//SpiQueryAdapter spiQueryAdapter = metaQuery.createSpiMetaQuery(indexName);
		//AdhocQueryAdapter adapter = injector.getInstance(AdhocQueryAdapter.class);
		//adapter.setup(metaQuery, spiQueryAdapter);

		//List<Row> rows = adapter.getResultList();
		
	}

	@Override
	public void configure(Binder binder) {
		MetaDatabase map = new MetaDatabase();
		addMetaClassDbo(map, "MyEntity", "cat", "mouse", "dog");
		addMetaClassDbo(map, "OtherEntity", "id", "dean", "declan", "pet", "house");
		
		binder.bind(MetaDatabase.class).toInstance(map);
		binder.bind(IndexReaderWriter.class).to(MemoryIndexWriter.class).asEagerSingleton();
		binder.bind(NoSqlRawSession.class).to(InMemorySession.class);
	
		binder.bind(NoSqlSession.class).annotatedWith(Names.named("writecachelayer")).to(NoSqlWriteCacheImpl.class);
		binder.bind(NoSqlSession.class).annotatedWith(Names.named("readcachelayer")).to(NoSqlReadCacheImpl.class);		
	}

	private void addMetaClassDbo(MetaDatabase map, String entityName, String ... fields) {
		MetaTableDbo meta = new MetaTableDbo();
		meta.setTableName(entityName);
		map.addMetaClassDbo(meta);
		
		for(String field : fields) {
			MetaColumnDbo fieldDbo = new MetaColumnDbo();
			fieldDbo.setColumnName(field);
			meta.addField(fieldDbo);
		}
	}
}