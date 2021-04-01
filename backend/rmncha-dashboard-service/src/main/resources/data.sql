INSERT INTO  synchronization_date_master  ( synchronization_date_master_id ,  last_modified_date ,  last_synchronized ,  table_name ) 
VALUES (1, (select max (last_modified) from area), (select max (last_modified) from area), 'area') ON CONFLICT DO NOTHING;
INSERT INTO  synchronization_date_master  ( synchronization_date_master_id ,  last_modified_date ,  last_synchronized ,  table_name ) 
VALUES (2, (select max (last_modified) from arealevel), (select max (last_modified) from arealevel), 'arealevel') ON CONFLICT DO NOTHING;
INSERT INTO  synchronization_date_master  ( synchronization_date_master_id ,  last_modified_date ,  last_synchronized ,  table_name ) 
VALUES (3, (select max (last_modified) from unit), (select max (last_modified) from unit), 'unit') ON CONFLICT DO NOTHING;
INSERT INTO  synchronization_date_master  ( synchronization_date_master_id ,  last_modified_date ,  last_synchronized ,  table_name )
 VALUES (4, (select max (last_modified) from indicator), (select max (last_modified) from indicator), 'indicator') ON CONFLICT DO NOTHING;
INSERT INTO  synchronization_date_master  ( synchronization_date_master_id ,  last_modified_date ,  last_synchronized ,  table_name )
 VALUES (5, (select max (last_modified) from subgroup), (select max (last_modified) from subgroup), 'subgroup') ON CONFLICT DO NOTHING;
INSERT INTO  synchronization_date_master  ( synchronization_date_master_id ,  last_modified_date ,  last_synchronized ,  table_name )
 VALUES (6, (select max (last_modified) from sector), (select max (last_modified) from sector), 'sector') ON CONFLICT DO NOTHING;
INSERT INTO  synchronization_date_master  ( synchronization_date_master_id ,  last_modified_date ,  last_synchronized ,  table_name ) 
VALUES (7, (select max (last_modified) from source), (select max (last_modified) from source), 'source') ON CONFLICT DO NOTHING;
INSERT INTO  synchronization_date_master  ( synchronization_date_master_id ,  last_modified_date ,  last_synchronized ,  table_name ) 
VALUES (8, (select max (last_modified) from data), (select max (last_modified) from data), 'data') ON CONFLICT DO NOTHING;
