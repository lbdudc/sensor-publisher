/*% if (feature.DM_DS_Address || feature.DM_A_G_Batch) { %*/
\echo =========================================================================
\echo  Creating schema
\echo =========================================================================

CREATE SCHEMA IF NOT EXISTS geonames;
-- Para ver/cambiar el esquema activo
SET search_path TO geonames;

\echo =========================================================================
\echo  Creating temporary tables
\echo =========================================================================

-- Crear tablas
CREATE TABLE geoname_tmp (geonameid int, name text, asciiname text, alternatenames text, latitude float not null, longitude float not null, fclass char(1), fcode text, country text, cc2 text, admin1 text, admin2 text, admin3 text, admin4 text, population bigint, elevation int, gtopo30 int, timezone text, moddate date );
CREATE TABLE alternatename_tmp (alternatenameid int, geonameid int, isolanguage text, alternatename text, ispreferredname boolean, isshortname boolean, iscolloquial boolean, ishistoric boolean );

-- Importar los datos a las tablas
-- Los datos se descargan desde: http://download.geonames.org/export/dump/
-- Ficheros necedarios: allCountries.zip, alternateNames.zip
-- Se deben descomprimir los ficheros para permitir que las siguientes sentencias encuentren los .txt
-- Nota: Si se quieren cargar los 'geonames' de ciertos paises, se deben emplear los ficheros XX.zip, para cada uno de ellos se debe hacer una importación equivalente a la de 'geoname'
\echo INFO: Inserting data into temporary table geoname_tmp
\COPY geoname_tmp (geonameid,name,asciiname,alternatenames,latitude,longitude,fclass,fcode,country,cc2,admin1,admin2,admin3,admin4,population,elevation,gtopo30,timezone,moddate) FROM 'allCountries.txt' NULL AS '' ENCODING 'utf-8';
\echo INFO: Inserting data into temporary table alternatename_tmp
\COPY alternatename_tmp (alternatenameid,geonameid,isolanguage,alternatename, ispreferredname,isshortname,iscolloquial,ishistoric) FROM 'alternateNames.txt' NULL AS '' ENCODING 'utf-8';

-- ==============================
-- Eliminamos columnas no usadas
-- ==============================
\echo INFO: Removing unused columns
ALTER TABLE geoname_tmp
	DROP COLUMN asciiname, DROP COLUMN alternatenames, DROP COLUMN fclass, DROP COLUMN cc2, DROP COLUMN population,
	DROP COLUMN elevation, DROP COLUMN gtopo30, DROP COLUMN timezone, DROP COLUMN moddate, DROP COLUMN latitude, DROP COLUMN longitude;

ALTER TABLE alternatename_tmp
	DROP COLUMN iscolloquial,  DROP COLUMN ishistoric;

-- ===============
-- Tablas finales
-- ===============

\echo =========================================================================
\echo Creating geonames tables
\echo =========================================================================
-- Tabla geonames
\echo INFO: Creating table geoname
CREATE TABLE geoname AS
select g.*
from geoname_tmp g
where
	    g.fcode IN ('ADM1', 'ADM2', 'ADM3', 'ADM4', 'PPL', 'PPLA', 'PPLA2', 'PPLA3', 'PPLA4', 'PPLC', 'PPLF', 'PPLG', 'PPLL', 'PPLR', 'PPLS', 'PCLI')
	AND g.country IS NOT NULL;

\echo INFO: Adding discriminator column 'type' in geoname table
ALTER TABLE geoname ADD type TEXT;
COMMENT ON COLUMN geoname.type IS E'Discriminator column';
UPDATE geoname set type = 'COUNTRY' WHERE fcode LIKE 'PCLI';
UPDATE geoname SET type = 'ADM1' WHERE fcode LIKE 'ADM1';
UPDATE geoname SET type = 'ADM2' WHERE fcode LIKE 'ADM2';
UPDATE geoname SET type = 'ADM3' WHERE fcode LIKE 'ADM3';
UPDATE geoname SET type = 'ADM4' WHERE fcode LIKE 'ADM4';
UPDATE geoname SET type = 'TOWN' WHERE fcode IN ('PPL', 'PPLA', 'PPLA2', 'PPLA3', 'PPLA4', 'PPLC', 'PPLF', 'PPLG', 'PPLF', 'PPLL','PPLR', 'PPLS');
ALTER TABLE geoname ALTER COLUMN type SET NOT NULL;

-- Tabla alternatename final (A partir de los datos insertados en geoname)
\echo INFO: Creating table alternatename
CREATE TABLE alternatename AS
SELECT a.*
FROM alternatename_tmp a
INNER JOIN geoname g on g.geonameid = a.geonameid
WHERE isolanguage NOT IN ('link', 'post', 'iata', 'icao', 'faac', 'fr_1793', 'abbr');
-- Puede haber varios nombres por idioma
-- Puede haber entradas sin idioma

-- ===========================
-- Claves primarias y foraneas
-- ===========================
\echo INFO: Creating PK and FK (3)

ALTER TABLE ONLY geoname       ADD CONSTRAINT pk_geoname_geonameid               PRIMARY KEY (geonameid);

ALTER TABLE ONLY alternatename ADD CONSTRAINT pk_alternatenameid_alternatenameid PRIMARY KEY (alternatenameid);
ALTER TABLE ONLY alternatename ADD CONSTRAINT fk_alternatename_geoname           FOREIGN KEY (geonameid) REFERENCES geoname(geonameid);

-- ===========================
-- Indices
-- ===========================
-- Nota: En las queries se debe consultar siempre con lower(...) para que se apliquen los indices

-- Tipos de indices:
-- btree........: Comparacion mediante <, >, <=, >=, =. También se aplica sobre ordenaciones, BETWEEN, IN, IS NULL o IS NOT NULL.
--                LIKE sobre una constante o un patrón en donde se conoce el principio (FOO%)
-- hash.........: Comparacion de igualdad
-- gist, sp-gist: Infraestructura de indices. Validos para LIKE %FOO%
-- gin..........: Indices inversos validos para más de una clave.
--
-- https://www.postgresql.org/docs/9.2/static/indexes-types.html
-- https://niallburkley.com/blog/index-columns-for-like-in-postgres/

\echo INFO: Creating Indexes (13)

CREATE INDEX alternatename_geonameid_idx		ON alternatename USING btree(geonameid);		-- alternatename -> geonameid
CREATE INDEX alternatename_isolanguage_idx      ON alternatename USING btree(isolanguage);		-- alternatename -> isolanguage
CREATE INDEX alternatename_ispreferredname_idx  ON alternatename USING btree(ispreferredname);	-- alternatename -> ispreferredname
CREATE INDEX alternatename_isshortname_idx      ON alternatename USING btree(isshortname);		-- alternatename -> isshortname

CREATE INDEX geoname_country_idx 				ON geoname USING btree(country); 				-- geoname -> country
CREATE INDEX geoname_fcode_idx   				ON geoname USING btree(fcode);					-- geoname -> fcode
CREATE INDEX geoname_adm1_idx    				ON geoname USING btree(admin1);					-- geoname -> admin1
CREATE INDEX geoname_adm2_idx    				ON geoname USING btree(admin2);					-- geoname -> admin2
CREATE INDEX geoname_adm3_idx    				ON geoname USING btree(admin3);					-- geoname -> admin3
CREATE INDEX geoname_adm4_idx  					ON geoname USING btree(admin4);					-- geoname -> admin4
CREATE INDEX geoname_type_idx                   ON geoname USING btree(type);					-- geoname -> type

-- Para optimizar obtener listado de paises por idioma
CREATE INDEX alternatename_isolanguage_ispreferredname_idx ON alternatename (isolanguage, ispreferredname);

-- Para optimizar busquedas por CCAA/Provincias (Nombre corto)
CREATE INDEX alternatename_isolanguage_isshortname_idx ON alternatename (isolanguage, isshortname);


\echo =========================================================================
\echo INFO: Dropping temporary tables
\echo =========================================================================
DROP TABLE geoname_tmp;
DROP TABLE alternatename_tmp;


\echo =========================================================================
\echo INFO: Fixing erroneous data
\echo =========================================================================
-- Descripcion de feature codes (fcode) => http://www.geonames.org/export/codes.html

-- Todos los geonames deberian tener siempre un admin anterior cubierto y su fcode debe corresponderse con su ultimo admin code cubierto
-- Tabla de geonames validos:

-- id | admin1 | admin2 | admin3 | admin4 |
--  A |   xx   |   xx   |   xx   |   xx   | => Ok
--  B |   xx   |   xx   |   xx   |   --   | => Ok
--  C |   xx   |   xx   |   --   |   xx   | => No validos => Cambiar admin4 a admin3 y fcode a ADM3
--  D |   xx   |   xx   |   --   |   --   | => Ok
--  E |   xx   |   --   |   xx   |   xx   | => No validos => Cambiar admin3 a admin2 y admin4 a admin3 y fcode a ADM3
--  F |   xx   |   --   |   xx   |   --   | => No validos => Cambiar admin3 a admin1 y fcode a ADM2
--  G |   xx   |   --   |   --   |   xx   | => No validos => Cambiar admin4 a admin2 y fcode a ADM2
--  H |   xx   |   --   |   --   |   --   | => Ok
--  I |   --   |   xx   |   xx   |   xx   | => No validos => Cambiar admin2 a admin1 y admin3 a admin2 y admin4 a admin3 y fcode a ADM3
--  J |   --   |   xx   |   xx   |   --   | => No validos => Cambiar admin2 a admin1 y admin3 a admin2 y foche a ADM2
--  K |   --   |   xx   |   --   |   xx   | => No validos => Cambiar admin2 a admin1 y admin4 a admin2 y fcode a ADM2
--  L |   --   |   xx   |   --   |   --   | => No validos => Cambiar admin2 a admin1 y fcode a ADM1
--  M |   --   |   --   |   xx   |   xx   | => No validos => Cambiar admin3 a admin1 y admin4 a admin2 y fcode a ADM2
--  N |   --   |   --   |   xx   |   --   | => No validos => Cambiar admin3 a admin1 y fcode a ADM1
--  O |   --   |   --   |   --   |   xx   | => No validos => Cambiar admin4 a admin1 y fcode a ADM1
--  P |   --   |   --   |   --   |   --   | => No validos => Borrar

-- Bloque para comprobar los cambios aplicados por las queries:
-- ============================================================
-- create table backup as select * from geoname where <where condition>;
-- Realizar update
-- select t.fcode, t.admin1, t.admin2, t.admin3, t.admin4, '--->', g.fcode, g.admin1, g.admin2, g.admin3, g.admin4
-- from geoname g
-- inner join caso_e t on t.geonameid = g.geonameid;



-- Correccion de codigos:
-- ======================
--  A |   xx   |   xx   |   xx   |   xx   | => Ok
--  B |   xx   |   xx   |   xx   |   --   | => Ok

--  C |   xx   |   xx   |   --   |   xx   | => No validos => Cambiar admin4 a admin3 y fcode a ADM3
-- select *               from geoname where admin1 is not null and admin2 is not null and admin3 is null and admin4 is not null;
-- select fcode, count(*) from geoname where admin1 is not null and admin2 is not null and admin3 is null and admin4 is not null group by fcode;
-- => Tuplas: 786 -> ADM4 (783) y PPL (3)
update geoname set admin3=admin4, admin4 = null, fcode = 'ADM3' where admin1 is not null and admin2 is not null and admin3 is null and admin4 is not null and fcode like 'ADM%';
update geoname set admin3=admin4, admin4 = null                 where admin1 is not null and admin2 is not null and admin3 is null and admin4 is not null and fcode IN ('PPL', 'PPLA', 'PPLA2', 'PPLA3', 'PPLA4', 'PPLC', 'PPLF', 'PPLG', 'PPLL', 'PPLR', 'PPLS', 'PCLI');

--  D |   xx   |   xx   |   --   |   --   | => Ok

--  E |   xx   |   --   |   xx   |   xx   | => No validos => Cambiar admin3 a admin2 y admin4 a admin3 y fcode a ADM3
-- select *               from geoname where admin1 is not null and admin2 is null and admin3 is not null and admin4 is not null;
-- select fcode, count(*) from geoname where admin1 is not null and admin2 is null and admin3 is not null and admin4 is not null group by fcode;
-- ==> Tuplas: 16 ADM4
update geoname set admin2=admin3, admin3=admin4, admin4 = null, fcode = 'ADM3' where admin1 is not null and admin2 is null and admin3 is not null and admin4 is not null and fcode like 'ADM%';
update geoname set admin2=admin3, admin3=admin4, admin4 = null                 where admin1 is not null and admin2 is null and admin3 is not null and admin4 is not null and fcode IN ('PPL', 'PPLA', 'PPLA2', 'PPLA3', 'PPLA4', 'PPLC', 'PPLF', 'PPLG', 'PPLL', 'PPLR', 'PPLS', 'PCLI');

--  F |   xx   |   --   |   xx   |   --   | => No validos => Cambiar admin3 a admin1 y fcode a ADM2
-- select * from geoname               where admin1 is not null and admin2 is null and admin3 is not null and admin4 is null;
-- select fcode, count(*) from geoname where admin1 is not null and admin2 is null and admin3 is not null and admin4 is null group by fcode;
-- ==> Tuplas: 17.738 -> ADM3 (17.733) y PPL (5)
update geoname set admin2=admin3, admin3 = null, fcode = 'ADM2' where admin1 is not null and admin2 is null and admin3 is not null and admin4 is null and fcode like 'ADM%';
update geoname set admin2=admin3, admin3 = null                 where admin1 is not null and admin2 is null and admin3 is not null and admin4 is null and fcode IN ('PPL', 'PPLA', 'PPLA2', 'PPLA3', 'PPLA4', 'PPLC', 'PPLF', 'PPLG', 'PPLL', 'PPLR', 'PPLS', 'PCLI');

--  G |   xx   |   --   |   --   |   xx   | => No validos => Cambiar admin4 a admin2 y fcode a ADM2
-- select * from geoname               where admin1 is not null and admin2 is null and admin3 is null and admin4 is not null;
-- select fcode, count(*) from geoname where admin1 is not null and admin2 is null and admin3 is null and admin4 is not null group by fcode;
-- Tuplas: 40.518 -> ADM4(40.516) y PPL (2)
update geoname set admin2=admin4, admin4 = null, fcode = 'ADM2' where admin1 is not null and admin2 is null and admin3 is null and admin4 is not null and fcode like 'ADM%';
update geoname set admin2=admin4, admin4 = null                 where admin1 is not null and admin2 is null and admin3 is null and admin4 is not null and fcode IN ('PPL', 'PPLA', 'PPLA2', 'PPLA3', 'PPLA4', 'PPLC', 'PPLF', 'PPLG', 'PPLL', 'PPLR', 'PPLS', 'PCLI');

--  H |   xx   |   --   |   --   |   --   | => Ok

--  I |   --   |   xx   |   xx   |   xx   | => No validos => Cambiar admin2 a admin1 y admin3 a admin2 y admin4 a admin3 y fcode a ADM3
-- select * from geoname where admin1 is null and admin2 is not null and admin3 is not null and admin4 is not null;
-- Tuplas: 0

update geoname set admin1=admin2, admin2=admin3, admin3 = admin4, admin4 = null, fcode='ADM3' where admin1 is null and admin2 is not null and admin3 is not null and admin4 is not null and fcode like 'ADM%';
update geoname set admin1=admin2, admin2=admin3, admin3 = admin4, admin4 = null               where admin1 is null and admin2 is not null and admin3 is not null and admin4 is not null and fcode IN ('PPL', 'PPLA', 'PPLA2', 'PPLA3', 'PPLA4', 'PPLC', 'PPLF', 'PPLG', 'PPLL', 'PPLR', 'PPLS', 'PCLI');

--  J |   --   |   xx   |   xx   |   --   | => No validos => Cambiar admin2 a admin1 y admin3 a admin2 y fcode a ADM2
-- select fcode, count(*) from geoname where admin1 is null and admin2 is not null and admin3 is not null and admin4 is null group by fcode;
-- Tuplas: 2 (ADM3)
-- select * from geoname where admin1 is null and admin2 is not null and admin3 is not null and admin4 is null and fcode like 'ADM3';
update geoname set admin1=admin2, admin2=admin3, admin3 = null, fcode='ADM2' where admin1 is null and admin2 is not null and admin3 is not null and admin4 is null and fcode like 'ADM%';
update geoname set admin1=admin2, admin2=admin3, admin3 = null               where admin1 is null and admin2 is not null and admin3 is not null and admin4 is null and fcode IN ('PPL', 'PPLA', 'PPLA2', 'PPLA3', 'PPLA4', 'PPLC', 'PPLF', 'PPLG', 'PPLL', 'PPLR', 'PPLS', 'PCLI');

--  K |   --   |   xx   |   --   |   xx   | => No validos => Cambiar admin2 a admin1 y admin4 a admin2 y fcode a ADM2
-- select *               from geoname where admin1 is null and admin2 is not null and admin3 is null and admin4 is not null;
-- select fcode, count(*) from geoname where admin1 is null and admin2 is not null and admin3 is null and admin4 is not null group by fcode;
-- Tuplas: 0
update geoname set admin1=admin2, admin2=admin4, admin4 = null, fcode='ADM2' where admin1 is null and admin2 is not null and admin3 is null and admin4 is not null and fcode like 'ADM%';
update geoname set admin1=admin2, admin2=admin4, admin4 = null               where admin1 is null and admin2 is not null and admin3 is null and admin4 is not null and fcode IN ('PPL', 'PPLA', 'PPLA2', 'PPLA3', 'PPLA4', 'PPLC', 'PPLF', 'PPLG', 'PPLL', 'PPLR', 'PPLS', 'PCLI');

--  L |   --   |   xx   |   --   |   --   | => No validos => Cambiar admin2 a admin1 y fcode a ADM1
-- select *               from geoname where admin1 is null and admin2 is not null and admin3 is null and admin4 is null;
-- select fcode, count(*) from geoname where admin1 is null and admin2 is not null and admin3 is null and admin4 is null group by fcode;
-- Tuplas: 5 PPL
update geoname set admin1=admin2, admin2=null, fcode='ADM1' where admin1 is null and admin2 is not null and admin3 is null and admin4 is null and fcode like 'ADM%';
update geoname set admin1=admin2, admin2=null               where admin1 is null and admin2 is not null and admin3 is null and admin4 is null and fcode IN ('PPL', 'PPLA', 'PPLA2', 'PPLA3', 'PPLA4', 'PPLC', 'PPLF', 'PPLG', 'PPLL', 'PPLR', 'PPLS', 'PCLI');

--  M |   --   |   --   |   xx   |   xx   | => No validos => Cambiar admin3 a admin1 y admin4 a admin2 y fcode a ADM2
-- select * from geoname where admin1 is null and admin2 is null and admin3 is not null and admin4 is not null;
-- Tuplas: 0
update geoname set admin1=admin3, admin2=admin4, admin3=null, admin4=null, fcode='ADM2' where admin1 is null and admin2 is null and admin3 is not null and admin4 is not null and fcode like 'ADM%';
update geoname set admin1=admin3, admin2=admin4, admin3=null, admin4=null               where admin1 is null and admin2 is null and admin3 is not null and admin4 is not null and fcode IN ('PPL', 'PPLA', 'PPLA2', 'PPLA3', 'PPLA4', 'PPLC', 'PPLF', 'PPLG', 'PPLL', 'PPLR', 'PPLS', 'PCLI');

--  N |   --   |   --   |   xx   |   --   | => No validos => Cambiar admin3 a admin1 y fcode a ADM1
-- select *               from geoname where admin1 is null and admin2 is null and admin3 is not null and admin4 is null;
-- select fcode, count(*) from geoname where admin1 is null and admin2 is null and admin3 is not null and admin4 is null group by fcode;
-- Tuplas: 3 ADM3
-- select * from geoname where admin1 is null and admin2 is null and admin3 is not null and admin4 is null and fcode='ADM3';
update geoname set admin1=admin3, admin3=null, fcode='ADM1' where admin1 is null and admin2 is null and admin3 is not null and admin4 is null and fcode like 'ADM%';
update geoname set admin1=admin3, admin3=null               where admin1 is null and admin2 is null and admin3 is not null and admin4 is null and fcode IN ('PPL', 'PPLA', 'PPLA2', 'PPLA3', 'PPLA4', 'PPLC', 'PPLF', 'PPLG', 'PPLL', 'PPLR', 'PPLS', 'PCLI');

--  O |   --   |   --   |   --   |   xx   | => No validos => Cambiar admin4 a admin1 y fcode a ADM1
-- select fcode, count(*) from geoname where admin1 is null and admin2 is null and admin3 is null and admin4 is not null group by fcode;
-- Tuplas: 9 ADM4
-- select * from geoname where admin1 is null and admin2 is null and admin3 is null and admin4 is not null and fcode = 'ADM4';
update geoname set admin1=admin4, admin4=null, fcode='ADM1' where admin1 is null and admin2 is null and admin3 is null and admin4 is not null and fcode like 'ADM%';
update geoname set admin1=admin4, admin4=null, fcode='ADM1' where admin1 is null and admin2 is null and admin3 is null and admin4 is not null and fcode IN ('PPL', 'PPLA', 'PPLA2', 'PPLA3', 'PPLA4', 'PPLC', 'PPLF', 'PPLG', 'PPLL', 'PPLR', 'PPLS', 'PCLI');

--  P |   --   |   --   |   --   |   --   | => No validos (Poblaciones a nivel de pais) => Borrar
-- select fcode, count(*) from geoname where admin1 is null and admin2 is null and admin3 is null and admin4 is null group by fcode;
-- PPL: 1276
-- select * from geoname where admin1 is null and admin2 is null and admin3 is null and admin4 is null and fcode like 'PPL';
-- Primero borramos alternate name
DELETE FROM alternatename a WHERE a.geonameid IN (
	SELECT g.geonameid from geoname g where g.admin1 is null and g.admin2 is null and g.admin3 is null and g.admin4 is null and fcode IN ('PPL', 'PPLA', 'PPLA2', 'PPLA3', 'PPLA4', 'PPLC', 'PPLF', 'PPLG', 'PPLL', 'PPLR', 'PPLS', 'PCLI')
); -- 594
-- A continuacion borramos geoname
DELETE FROM geoname g where g.admin1 is null and g.admin2 is null and g.admin3 is null and g.admin4 is null; -- 1276

-- Hay casos en los que los valores de varias columnas adminX son iguales!
-- select count(*) from geoname g where g.admin1 = g.admin2 and g.admin1 is not null and g.admin2 is not null; -- 1891
-- select count(*) from geoname g where g.admin2 = g.admin3 and g.admin2 is not null and g.admin3 is not null; -- 1109
-- select count(*) from geoname g where g.admin3 = g.admin4 and g.admin3 is not null and g.admin4 is not null; -- 46
-- Sin embargo estos pueden ser validos: Ejemplo:
-- geonameid: 2783474
-- select * from geoname g where g.admin1 like 'BRU' and fcode like 'ADM1';
-- select * from geoname g where g.admin2 like 'BRU' and fcode like 'ADM2';
-- select * from geoname g where g.admin3 like '23'  and fcode like 'ADM3';
-- select * from geoname g where g.admin4 like '21019' and fcode like 'ADM4';

-- Optimizar tablas
\echo INFO: ====================================================================
\echo INFO: Optimizing tables
\echo INFO: ====================================================================

VACUUM VERBOSE geoname;
VACUUM VERBOSE alternatename


\echo INFO: ====================================================================
\echo INFO: ======================== Import finished! ==========================
\echo INFO: ====================================================================

/*% } %*/
