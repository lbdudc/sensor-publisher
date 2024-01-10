/*% if (feature.DM_DS_Address || feature.DM_A_G_Batch) { %*/
## Geonames DB Creation Procedure
To work with geolocation modules a GIS database must be created.

1. **Download the GeoNames dumps**  
   Necessary files: `allCountries.zip`, `alternateNames.zip`  
   Fields explanation: http://www.geonames.org/export/codes.html

   Dumps location <a href="http://download.geonames.org/export/dump/">http://download.geonames.org/export/dump/</a>

   The files must be unzipped to allow the import statements to find the `.txt` files  

   NOTES:
    - Dumps are updated daily.  
    - There are many data that we do not consider valid
    - New invalid cases may appear with different dumps
    - If you want to import the geonames of certain countries, you must use the XX.zip files instead of `allCountries.zip`,
   for each of them an import equivalent to the one of 'geoname' must be done. (Review importGeonames.sql)  

2. **Connect to DB where geonames schema will be created**  
   ```psql -U <user> -d <dbName>``` 

3. **Go to the folder where geonames dumps were downloaded.**  
   The import script assumes that the import files are in the directory where the script is running.  
   The easiest thing is to copy the script to the directory where you have the dumps and launch it from there

4. **Execute import script**  
   ```\i importGeonames.sql```

NOTE: If indexes are corrupted at some point, do `REINDEX <table>`
/*% } %*/
