/*%@
  if (!feature.SensorViewer) return [];
  return data.dataWarehouse.sensors
    .map(function(sen) {
      return {
          fileName: normalize(sen.id, true) + 'RepositoryImpl.java',
          context: sen
      };
    });
%*/
/*%
  const dimensions = [];
  data.dataWarehouse.sensors
    .filter(sen => sen.id === context.id)
    .forEach(function(sensor) {
      const dims = sensor.dimensions;
      dims
        .filter(dim => dim.type === "CATEGORICAL")
        .forEach(dim => {
          dimensions.push(dim);
        });
  });
  var hasCategoricalDims = dimensions.length > 0;
%*/
package es.udc.lbd.gema.lps.model.repository.sensor;

/*% if (hasCategoricalDims) { %*/
import es.udc.lbd.gema.lps.model.service.dto.sensor.DataDTO;
import java.util.Collections;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
/*% } else { %*/
import es.udc.lbd.gema.lps.model.service.dto.DataDTO;
/*% } %*/
import es.udc.lbd.gema.lps.model.service.dto.MinMaxDTO;
import es.udc.lbd.gema.lps.model.domain.sensor.CalcAggregation;
import es.udc.lbd.gema.lps.model.domain.sensor.TemporalAggregation;
import es.udc.lbd.gema.lps.model.domain.sensor./*%= normalize(context.id, true) %*/SpatialAggregation;
import es.udc.lbd.gema.lps.model.domain.sensor./*%= normalize(context.id, true) %*/SpatialFilter;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.time.ZoneId;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.SingleBucketAggregation;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.IncludeExclude;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.aggregations.metrics.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class /*%= normalize(context.id, true) %*/RepositoryImpl implements /*%= normalize(context.id, true) %*/Repository {

  private ElasticsearchOperations elasticsearchOperations;

  @Autowired
  public /*%= normalize(context.id, true) %*/RepositoryImpl(final ElasticsearchOperations elasticsearchOperations) {
    super();
    this.elasticsearchOperations = elasticsearchOperations;
  }

  @Override
  public List<DataDTO> getData(
       LocalDateTime start,
      LocalDateTime end,
      TemporalAggregation temporalAggregation,
      CalcAggregation calc,
      String field,
      Map<String, Object> filters,
      /*%= normalize(context.id, true) %*/SpatialAggregation spatialAggregation,
      /*%= normalize(context.id, true) %*/SpatialFilter spatialFilterType,
      /*% if (hasCategoricalDims) { %*/
      String categoryAggregation,
      String categoryFilter,
      String categoryFrom,
      String categoryTo,
      /*% } %*/
      Integer spatialFilter) {

      Boolean hasAggregation = spatialAggregation != null || temporalAggregation != null;
      Boolean hasFilters =
          start != null
              || end != null
              || (filters != null && !filters.isEmpty())
              /*% if (hasCategoricalDims) { %*/
              || categoryFilter != null
              /*% } %*/
              || spatialFilter != null;
      /*
      * Build SQL query string
      */
      NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

      // WHERE (filters)
      if (hasFilters) {
        nativeSearchQueryBuilder.withQuery(
          getQueryFilter(
            spatialFilterType,
            spatialFilter,
            /*% if (hasCategoricalDims) { %*/
            categoryAggregation,
            categoryFilter,
            categoryFrom,
            categoryTo,
            /*% } %*/
            calc,
            field,
            start,
            end));
      }

      // GROUP BY (aggregations)
      List<CalcAggregation> calcAggregation = new ArrayList<>();
      calcAggregation.add(calc);

      nativeSearchQueryBuilder.withMaxResults(0);
      nativeSearchQueryBuilder.addAggregation(
          getQueryAggregation(
              spatialAggregation,
              temporalAggregation,
              calcAggregation,
              /*% if (hasCategoricalDims) { %*/
              null, field, null));
              /*% } else { %*/
              field));
              /*% } %*/

      // Get Index Regex depending on the level of temporal aggregation
      String indexRegex = getIndexRegexByTemporalAggregation(temporalAggregation, start, end);

      // Build
      Query searchQuery = nativeSearchQueryBuilder.build();

      // If there is an aggregation, we dont need searchhits, only aggregation data
      searchQuery.setTrackTotalHits(!hasAggregation);

      // Execute query
      SearchHits<Object> hits = null;
      try {
        hits =
            elasticsearchOperations.search(
                searchQuery, Object.class, IndexCoordinates.of(indexRegex));
      } catch (Exception e) {
        return null;
      }

      /*% if (hasCategoricalDims) { %*/
      List calcs = new ArrayList<>();
      calcs.add(calc.toString());
      return getResponseWithAggregation(hits, spatialAggregation, calcs, null, false);
      /*% } else { %*/
      return getResponseWithAggregation(hits, spatialAggregation, calc.toString(), false);
      /*% } %*/
  }

  @Override
  /*% if (hasCategoricalDims) { %*/
  public List<DataDTO> getDataBySensorId(
  /*% } else { %*/
  public DataDTO getDataBySensorId(
  /*% } %*/
      Long sensorId,
      LocalDateTime start,
      LocalDateTime end,
      Map<String, Object> filters,
      /*%= normalize(context.id, true) %*/SpatialAggregation spatialAggregation,
      TemporalAggregation temporalAggregation,
      List<CalcAggregation> calcAggregations,
      /*% if (hasCategoricalDims) { %*/
      String categoryAggregation,
      String categoryFrom,
      String categoryTo,
      List<String> propertyAggregation) {
      /*% } else { %*/
      String propertyAggregation) {
      /*% } %*/

    Boolean hasAggregation = spatialAggregation != null || temporalAggregation != null;
    List<DataDTO> result = new ArrayList<>();

    /*
      * Build SQL query string
      */
    // WHERE (filters)
    NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

    // Add filters
    BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

    // WHERE (filters)
    if (spatialAggregation != null) {
      String spatialAggParsed = spatialAggregation.toString().toLowerCase();
      boolBuilder.must(
          QueryBuilders.termQuery(spatialAggParsed + "_id", String.valueOf(sensorId)));
    } else {
      boolBuilder.must(QueryBuilders.termQuery("sensor_id", sensorId));
    }

    /*% if (!hasCategoricalDims) { %*/
    boolBuilder.must(QueryBuilders.existsQuery(propertyAggregation.toLowerCase()));
    /*% } %*/

    boolBuilder.must(QueryBuilders.rangeQuery("@timestamp").from(start).to(end));

    nativeSearchQueryBuilder.withQuery(boolBuilder);

    // GROUP BY (aggregations)
    nativeSearchQueryBuilder.withMaxResults(0);

    nativeSearchQueryBuilder.addAggregation(
        getQueryAggregation(
            spatialAggregation,
            temporalAggregation,
            calcAggregations,
            /*% if (hasCategoricalDims) { %*/
            categoryAggregation,
            null,
            /*% } %*/
            propertyAggregation));

    // Get Index Regex depending on the level of temporal aggregation
    String indexRegex = getIndexRegexByTemporalAggregation(temporalAggregation, start, end);

    // Build
    Query searchQuery = nativeSearchQueryBuilder.build();

    // If there is an aggregation, we dont need searchhits, only aggregation data
    searchQuery.setTrackTotalHits(!hasAggregation);

    // Execute query
    SearchHits<Object> hits = null;
    try {
      hits =
          elasticsearchOperations.search(searchQuery, Object.class, IndexCoordinates.of(indexRegex));
    } catch (Exception e) {
      return null;
    }
    // Get results and parse to List<DataDTO>
    List<DataDTO> response =
        getResponseWithAggregation(
            hits,
            spatialAggregation,
            propertyAggregation,
            /*% if (hasCategoricalDims) { %*/
            categoryAggregation,
            /*% } %*/
            true);
    result.addAll(response);

    if (result.isEmpty()) {
      /*% if (hasCategoricalDims) { %*/
      return new ArrayList<>();
      /*% } else { %*/
      return new DataDTO(sensorId, new HashMap<>());
      /*% } %*/
    }

    /*% if (hasCategoricalDims) { %*/
    return result;
    /*% } else { %*/
    return result.get(0);
    /*% } %*/
  }

  @Override
  /*% if (hasCategoricalDims) { %*/
  public List<DataDTO> getHistogramDataBySensorId(
  /*% } else { %*/
  public DataDTO getHistogramDataBySensorId(
  /*% } %*/
      Long sensorId,
      LocalDateTime start,
      LocalDateTime end,
      Map<String, Object> filters,
      /*%= normalize(context.id, true) %*/SpatialAggregation spatialAggregation,
      TemporalAggregation temporalAggregation,
      List<CalcAggregation> calcAggregation,
      /*% if (hasCategoricalDims) { %*/
      String categoryAggregation,
      /*% } %*/
      List<String> propertyAggregation) {

    /*
     * Build SQL query string
     */
    NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

    // Add filters
    BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

    // WHERE (filters)
    if (spatialAggregation != null) {
      String spatialAggParsed = spatialAggregation.toString().toLowerCase();
      boolBuilder.must(QueryBuilders.termQuery(spatialAggParsed + "_id", String.valueOf(sensorId)));
    } else if (sensorId != null) {
      boolBuilder.must(QueryBuilders.termQuery("sensor_id", sensorId));
    } else {
      boolBuilder.must(QueryBuilders.matchAllQuery());
    }

    boolBuilder.must(QueryBuilders.rangeQuery("@timestamp").from(start).to(end));

    nativeSearchQueryBuilder.withQuery(boolBuilder);
    nativeSearchQueryBuilder.withMaxResults(0);

   // GROUP BY (aggregations)
   AbstractAggregationBuilder<?> aggregation = null;
  /*% if (hasCategoricalDims) { %*/
  if (propertyAggregation != null) {
    aggregation =
        AggregationBuilders.terms("category_agg")
            .field(categoryAggregation.toLowerCase())
            .size(65540);
  } else if (spatialAggregation != null) {
    String spatialAggParsed = spatialAggregation.toString().toLowerCase();
    aggregation =
        AggregationBuilders.terms(spatialAggParsed + "_agg")
            .field(spatialAggParsed + "_id")
            .size(1);
  } else {
    aggregation = AggregationBuilders.terms("sensor_agg").field("sensor_id").size(1);
  }
  /*% } else { %*/
  if (spatialAggregation != null) {
    String spatialAggParsed = spatialAggregation.toString().toLowerCase();
    aggregation =
        AggregationBuilders.terms(spatialAggParsed + "_agg")
            .field(spatialAggParsed + "_id")
            .size(1);
  } else {
    aggregation = AggregationBuilders.terms("sensor_agg").field("sensor_id").size(1);
  }
  /*% } %*/

   // Make histogram aggregation by date

   DateHistogramAggregationBuilder histogramAggregation =
       AggregationBuilders.dateHistogram("temporal_agg")
           .field("@timestamp")
           .calendarInterval(getDateHistogramInterval(temporalAggregation))
           .format("yyyy-MM-dd'T'HH:mm:ss");

   // Calculate all the avgs/min/max of all the properties (aggregations)
   /*% if (hasCategoricalDims) { %*/
    for (Iterator<String> iterator = propertyAggregation.iterator(); iterator.hasNext(); ) {
      String propertyParsed = iterator.next().toLowerCase();
      if (categoryAggregation != null && propertyParsed.equals(categoryAggregation.toLowerCase())) {
        continue;
      }
      histogramAggregation.subAggregation(
          AggregationBuilders.filter(
                  propertyParsed + "_filter_agg", QueryBuilders.rangeQuery(propertyParsed).gte(0))
              .subAggregation(
                  AggregationBuilders.avg(propertyParsed + "_avg").field(propertyParsed)));
    }
    /*% } else { %*/
    propertyAggregation.forEach(
        property -> {
          AbstractAggregationBuilder<?> filter = null;
          String propertyParsed = property.toLowerCase();

          filter =
              AggregationBuilders.filter(
                  propertyParsed + "_filter_agg", QueryBuilders.rangeQuery(propertyParsed).gte(0));
          filter.subAggregation(
              AggregationBuilders.avg(propertyParsed + "_avg").field(propertyParsed));
          filter.subAggregation(
              AggregationBuilders.min(propertyParsed + "_min").field(propertyParsed));
          filter.subAggregation(
              AggregationBuilders.max(propertyParsed + "_max").field(propertyParsed));
          histogramAggregation.subAggregation(filter);
        });
    /*% } %*/

   aggregation.subAggregation(histogramAggregation);

   nativeSearchQueryBuilder.addAggregation(aggregation);

    // Calculate regex index by start and end dates
    String indexName = "";
    if (temporalAggregation != null) {
      indexName = getIndexRegexByTemporalAggregation(temporalAggregation, start, end);
    } else {
      indexName = getIndexRegexByTemporalAggregation(TemporalAggregation.DAY, start, end);
    }

    // Build query
    Query searchQuery = nativeSearchQueryBuilder.build();

    // Execute query
    SearchHits<Object> hits = null;
    try {
      hits = elasticsearchOperations.search(searchQuery, Object.class, IndexCoordinates.of(indexName));
    } catch (Exception e) {
      return null;
    }

    // Get results and parse to DataDTO
    List<DataDTO> data = new ArrayList<>();
    /*% if (hasCategoricalDims) { %*/
    if (categoryAggregation != null) {
      data =
          getHistogramResponseWithAggregation(hits, null, propertyAggregation, categoryAggregation);
    } else if (spatialAggregation != null) {
      data =
          getHistogramResponseWithAggregation(hits, spatialAggregation, propertyAggregation, null);
    } else {
      data =
          getHistogramResponseWithAggregation(hits, null, propertyAggregation, categoryAggregation);
    }
    /*% } else { %*/
    if (spatialAggregation != null) {
      data = getHistogramResponseWithAggregation(hits, spatialAggregation, propertyAggregation);
    } else {
      data = getHistogramResponseWithAggregation(hits, null, propertyAggregation);
    }
    /*% } %*/

    if (data.size() > 0) {
      /*% if (hasCategoricalDims) { %*/
      return data;
      /*% } else { %*/
      return data.get(0);
      /*% } %*/
    }

    Map<String, Object> histogramData = new HashMap<>();
    histogramData.put("histogram", new ArrayList<>());
    /*% if (hasCategoricalDims) { %*/
    return new ArrayList<>();
    /*% } else { %*/
    return new DataDTO(sensorId, histogramData);
    /*% } %*/
  }

  // ------- PRIVATE FUNCTIONS ------- //

 private BoolQueryBuilder getQueryFilter(
      /*%= normalize(context.id, true) %*/SpatialFilter spatialFilterType,
      Integer spatialFilter,
      /*% if (hasCategoricalDims) { %*/
      String categoryAggregation,
      String categoryFilter,
      String categoryFrom,
      String categoryTo,
      /*% } %*/
      CalcAggregation calc,
      String field,
      LocalDateTime start,
      LocalDateTime end) {

    BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
    QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
    QueryBuilder rangeQuery = null;

    rangeQuery = QueryBuilders.rangeQuery("@timestamp").from(start).to(end);
    boolBuilder.must(rangeQuery);

    // Filter gte 0 values when doing an aggregation
    if (calc != null) {
      boolBuilder.must(QueryBuilders.rangeQuery(field.toLowerCase()).from(0));
    }

    // Spatial Filter
    if (spatialFilterType != null && spatialFilter != null) {
      String spatialFilterParsed = spatialFilterType.toString().toLowerCase() + "_id";
      queryBuilder = QueryBuilders.matchQuery(spatialFilterParsed, spatialFilter.toString());
      boolBuilder.must(queryBuilder);
    }

    /*% if (hasCategoricalDims) { %*/
    if (categoryAggregation != null && categoryFilter != null) {
        // Check if categoryFilter equals to "custom.ranged"
      if (categoryFilter.equals("custom.ranged")) {
        // Use ScriptQueryBuilder to convert field values to numbers within the query
        Script lowerBoundScript =
            new Script(
                ScriptType.INLINE,
                "painless",
                "Double.parseDouble(doc['"
                    + categoryAggregation
                    + "'].value.replace(',', '.')) >= "
                    + Double.parseDouble(categoryFrom),
                Collections.emptyMap());

        Script upperBoundScript =
            new Script(
                ScriptType.INLINE,
                "painless",
                "Double.parseDouble(doc['"
                    + categoryAggregation
                    + "'].value.replace(',', '.')) <= "
                    + Double.parseDouble(categoryTo),
                Collections.emptyMap());

        boolBuilder.must(QueryBuilders.scriptQuery(lowerBoundScript));
        boolBuilder.must(QueryBuilders.scriptQuery(upperBoundScript));
      } else {
        String categoryFilterParsed = categoryAggregation.toLowerCase();
        queryBuilder = QueryBuilders.matchQuery(categoryFilterParsed, categoryFilter.toString());
        boolBuilder.must(queryBuilder);
      }
    }
    /*% } %*/

    return boolBuilder;
  }

  /**
   * Calculates the aggregations needed for the query
   *
   * @param spatialAggregation
   * @param temporalAggregation
   * @param calc
   * @return AbstractAggregationBuilder with the aggregations
   */
  private AbstractAggregationBuilder<?> getQueryAggregation(
      /*%= normalize(context.id, true) %*/SpatialAggregation spatialAggregation,
      TemporalAggregation temporalAggregation,
      List<CalcAggregation> calc,
      /*% if (hasCategoricalDims) { %*/
      String categoryAggregation,
      String property,
      List<String> properties) {
      /*% } else { %*/
      String field) {
      /*% } %*/
    AbstractAggregationBuilder<?> aggregationBuilder = null;

    /*% if (hasCategoricalDims) { %*/
    if (categoryAggregation != null) {
        aggregationBuilder =
            AggregationBuilders.terms("category_agg")
                .field(categoryAggregation.toLowerCase())
                .size(65540);
     } else if (spatialAggregation != null) {
      String spatialAggParsed = spatialAggregation.toString().toLowerCase();
      aggregationBuilder =
          AggregationBuilders.terms(spatialAggParsed + "_agg")
              .field(spatialAggParsed + "_id")
              .size(65540)
              .order(BucketOrder.key(true));
    } else {
      aggregationBuilder = AggregationBuilders.terms("sensor_agg").field("sensor_id").size(65540);
    }
    /*% } else { %*/
    if (spatialAggregation != null) {
        String spatialAggParsed = spatialAggregation.toString().toLowerCase();
        aggregationBuilder =
            AggregationBuilders.terms(spatialAggParsed + "_agg")
                .field(spatialAggParsed + "_id")
                .size(65540)
                .order(BucketOrder.key(true));
    } else {
      aggregationBuilder = AggregationBuilders.terms("sensor_agg").field("sensor_id").size(65540);
    }
    /*% } %*/


    /*% if (hasCategoricalDims) { %*/
    if (properties != null) {
      for (Iterator<String> iterator = properties.iterator(); iterator.hasNext(); ) {
        String propertyParsed = iterator.next().toLowerCase();
        if (categoryAggregation != null
            && propertyParsed.equals(categoryAggregation.toLowerCase())) {
          continue;
        }
        aggregationBuilder.subAggregation(
            AggregationBuilders.filter(
                    propertyParsed + "_filter_agg", QueryBuilders.rangeQuery(propertyParsed).gte(0))
                .subAggregation(
                    AggregationBuilders.avg(propertyParsed + "_avg").field(propertyParsed)));
      }
    } else {
      for (Iterator<CalcAggregation> iterator2 = calc.iterator(); iterator2.hasNext(); ) {
        CalcAggregation calcAggregation = iterator2.next();
        String calcAggregationParsed = calcAggregation.toString().toLowerCase();
        if (calcAggregation == CalcAggregation.AVERAGE) {
          aggregationBuilder.subAggregation(
              AggregationBuilders.filter(
                      property.toLowerCase() + "_" + calcAggregationParsed + "_filter_agg",
                      QueryBuilders.rangeQuery(property.toLowerCase()).gte(0))
                  .subAggregation(
                      AggregationBuilders.avg(property.toLowerCase() + "_" + calcAggregationParsed)
                          .field(property.toLowerCase())));
        } else if (calcAggregation == CalcAggregation.MIN) {
          aggregationBuilder.subAggregation(
              AggregationBuilders.filter(
                      property.toLowerCase() + "_" + calcAggregationParsed + "_filter_agg",
                      QueryBuilders.rangeQuery(property.toLowerCase()).gte(0))
                  .subAggregation(
                      AggregationBuilders.min(property.toLowerCase() + "_" + calcAggregationParsed)
                          .field(property.toLowerCase())));
        } else if (calcAggregation == CalcAggregation.MAX) {
          aggregationBuilder.subAggregation(
              AggregationBuilders.filter(
                      property.toLowerCase() + "_" + calcAggregationParsed + "_filter_agg",
                      QueryBuilders.rangeQuery(property.toLowerCase()).gte(0))
                  .subAggregation(
                      AggregationBuilders.max(property.toLowerCase() + "_" + calcAggregationParsed)
                          .field(property.toLowerCase())));
        }
      }
    }
    /*% } else { %*/
    for (Iterator<CalcAggregation> iterator2 = calc.iterator(); iterator2.hasNext(); ) {
      CalcAggregation calcAggregation = iterator2.next();
      String calcAggregationParsed = calcAggregation.toString().toLowerCase();
      if (calcAggregation == CalcAggregation.AVERAGE) {
        aggregationBuilder.subAggregation(
            AggregationBuilders.filter(
                    field.toLowerCase() + "_" + calcAggregationParsed + "_filter_agg",
                    QueryBuilders.rangeQuery(field.toLowerCase()).gte(0))
                .subAggregation(
                    AggregationBuilders.avg(field.toLowerCase() + "_" + calcAggregationParsed)
                        .field(field.toLowerCase())));
      } else if (calcAggregation == CalcAggregation.MIN) {
        aggregationBuilder.subAggregation(
            AggregationBuilders.filter(
                    field.toLowerCase() + "_" + calcAggregationParsed + "_filter_agg",
                    QueryBuilders.rangeQuery(field.toLowerCase()).gte(0))
                .subAggregation(
                    AggregationBuilders.min(field.toLowerCase() + "_" + calcAggregationParsed)
                        .field(field.toLowerCase())));
      } else if (calcAggregation == CalcAggregation.MAX) {
        aggregationBuilder.subAggregation(
            AggregationBuilders.filter(
                    field.toLowerCase() + "_" + calcAggregationParsed + "_filter_agg",
                    QueryBuilders.rangeQuery(field.toLowerCase()).gte(0))
                .subAggregation(
                    AggregationBuilders.max(field.toLowerCase() + "_" + calcAggregationParsed)
                        .field(field.toLowerCase())));
      }
    }
    /*% } %*/

    // Add to final query
    return aggregationBuilder;
  }

  /**
   * Returns a regex string with the level of aggregation specified
   *
   * @return String containing the regex
   */
  private String getIndexRegexByTemporalAggregation(
      TemporalAggregation temporalAggregation, LocalDateTime start, LocalDateTime end) {
    String indexName = "/*%= normalize(context.id, false).toLowerCase() %*/";
    if (temporalAggregation != null) {
      if (temporalAggregation == TemporalAggregation.YEAR) {
        indexName += String.valueOf(start.getYear());
      } else if (temporalAggregation == TemporalAggregation.MONTH) {
        Month month = start.getMonth();
        String yearString = String.valueOf(start.getYear());
        String monthString = month.getValue() < 10 ? "0" + month.getValue() : month.getValue() + "";
        indexName += yearString + monthString;
      } else {
        String yearString = String.valueOf(start.getYear());
        Month month = start.getMonth();
        String monthString = month.getValue() < 10 ? "0" + month.getValue() : month.getValue() + "";
        String dayString =
            start.getDayOfMonth() < 10 ? "0" + start.getDayOfMonth() : start.getDayOfMonth() + "";
        indexName += yearString + monthString + dayString;
      }
      // IF THERE IS NO TEMPORAL AGGREGATION, SEARCH ON A CONCRETE DAY
    } else {
      String yearString = String.valueOf(start.getYear());
      Month month = start.getMonth();
      String monthString = month.getValue() < 10 ? "0" + month.getValue() : month.getValue() + "";
      String dayString =
          start.getDayOfMonth() < 10 ? "0" + start.getDayOfMonth() : start.getDayOfMonth() + "";
      indexName += yearString + monthString + dayString;
    }
    indexName += "*";
    return indexName;
  }

  /**
   * Returns the data query with aggregation If the aggregation is a date histogram, the data is
   * grouped by date If not, the aggregation will be by spatial, by sensor or by tramo
   *
   * @return A list of DataDTO with the results
   */
  private List<DataDTO> getResponseWithAggregation(
      SearchHits<Object> hits,
      /*%= normalize(context.id, true) %*/SpatialAggregation spatialAggregation,
      /*% if (hasCategoricalDims) { %*/
      List<String> property,
      String categoryAggregation,
      Boolean isDetail) {
      /*% } else { %*/
      String property,
      Boolean isDetail) {
      /*% } %*/
    List<DataDTO> features = new ArrayList<>();
    Aggregations aggs = hits.getAggregations();
    String termsAggName = "";

    // GET HOW THE AGGREGATION WAS DONE
    /*% if (hasCategoricalDims) { %*/
    if (categoryAggregation != null) {
      termsAggName = "category_agg";
    } else if (spatialAggregation != null) {
      termsAggName = spatialAggregation.toString().toLowerCase() + "_agg";
    } else {
      termsAggName = "sensor_agg";
    }
    /*% } else { %*/
    if (spatialAggregation != null) {
      termsAggName = spatialAggregation.toString().toLowerCase() + "_agg";
    } else {
      termsAggName = "sensor_agg";
    }
    /*% } %*/

    // If there are no aggregations, return empty list
    if (aggs == null) {
      /*% if (hasCategoricalDims) { %*/
      return new ArrayList<DataDTO>();
      /*% } else { %*/
      return features;
      /*% } %*/
    }

    // Get Bucket DateHistogram Aggregation
    Terms byId = aggs.get(termsAggName);

    // ITERATE THROUGH AGGREGATION BUCKETS
    for (Terms.Bucket bucket : byId.getBuckets()) {

      DataDTO feature = new DataDTO();
      Map<String, Object> properties = new HashMap<>();

      /*% if (hasCategoricalDims) { %*/
      feature.setId(bucket.getKeyAsString());
      /*% } else { %*/
      feature.setId(Long.parseLong(bucket.getKeyAsString()));
      /*% } %*/
      bucket
          .getAggregations()
          .asList()
          .forEach(
              agg -> {
                try {
                  Aggregation singleBucketAggregation =
                      ((SingleBucketAggregation) agg).getAggregations().asList().get(0);
                  String valueProp = PropertyUtils.getProperty(singleBucketAggregation, "value").toString();

                  // if the singleBucketAggregation has more than one "_", cut the last one
                  String name = "";
                  String[] split = singleBucketAggregation.getName().split("_");
                  if (split.length > 1) {
                    // get all the string except the last one
                    name =
                        singleBucketAggregation.getName().substring(0, singleBucketAggregation.getName().lastIndexOf("_"));
                  } else {
                    if (isDetail) {
                      name = singleBucketAggregation.getName();
                    } else {
                      name = singleBucketAggregation.getName().split("_")[0];
                    }
                  }

                  properties.put(
                      name,
                      valueProp == "Infinity" || valueProp == "-Infinity" ? null : valueProp);
                } catch (IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException e) {
                  e.printStackTrace();
                }
              });
      feature.setData(properties);
      features.add(feature);
    }
    return features;
  }

  private List<DataDTO> getHistogramResponseWithAggregation(
      SearchHits<Object> medidas,
      /*%= normalize(context.id, true) %*/SpatialAggregation spatialAggregation,
      /*% if (hasCategoricalDims) { %*/
      List<String> propertyAggregations,
      String categoryAggregation) {
      /*% } else { %*/
      List<String> propertyAggregations) {
      /*% } %*/
    // If we want to retrieve with a subaggregation of type dateHistogram

    List<DataDTO> features = new ArrayList<>();
    Aggregations aggs = medidas.getAggregations();
    String termsAggName = "";

    // GET HOW THE AGGREGATION WAS DONE
    if (spatialAggregation != null) {
      termsAggName = spatialAggregation.toString().toLowerCase() + "_agg";
    /*% if (hasCategoricalDims) { %*/
    } else if (categoryAggregation != null) {
      termsAggName = "category_agg";
    /*% } %*/
    } else {
      termsAggName = "sensor_agg";
    }

    // If there are no aggregations, return empty list
    if (aggs == null) {
      return features;
    }

    // Get Bucket DateHistogram Aggregation
    Terms byId = aggs.get(termsAggName);

    /*% if (hasCategoricalDims) { %*/
      byId.getBuckets()
      .forEach(
          catBucket -> {
            // Set ID of the feature (sensor_id or tramo_id or geometry_agg_id)
            DataDTO feature = new DataDTO();

            feature.setId(catBucket.getKeyAsString());

            Aggregations temporalAgg = catBucket.getAggregations();
            Histogram byDate = temporalAgg.get("temporal_agg");
            List<Object> histogramProperties = new ArrayList<>();

            for (Histogram.Bucket bucket : byDate.getBuckets()) {
              // Properties containing the MIN, MAX and AVG of all the metrics
              List<Object> properties = new ArrayList<>();

              Aggregations ocupacionAgg = bucket.getAggregations();
              propertyAggregations.removeIf(
                  property -> property.equals(categoryAggregation.toLowerCase()));

              // FILTER BUCKET
              propertyAggregations.forEach(
                  property -> {
                    String propertyParsed = property.toLowerCase();
                    Filter filterBucket = ocupacionAgg.get(propertyParsed + "_filter_agg");

                    // Variable to store Aggregation of min/max/avg values
                    List<Object> aggregationValues = new ArrayList<>();
                    filterBucket
                        .getAggregations()
                        .forEach(
                            agg -> {
                              // Terms byProperty = (Terms) agg;
                              try {
                                Map<String, Object> aggValue = new HashMap<>();
                                String valueProp =
                                    PropertyUtils.getProperty(agg, "value").toString();

                                aggValue.put(
                                    agg.getName(),
                                    valueProp == "Infinity" || valueProp == "-Infinity"
                                        ? null
                                        : valueProp);

                                aggregationValues.add(aggValue);
                                // aggValue.put(bucket.getKeyAsString(), finalAggs);
                              } catch (IllegalAccessException
                                  | InvocationTargetException
                                  | NoSuchMethodException e) {
                                e.printStackTrace();
                              }
                            });
                    // Add aggregation values
                    properties.add(aggregationValues);
                  });
              Map<String, Object> histValue = new HashMap<>();
              histValue.put(bucket.getKeyAsString(), properties);
              histogramProperties.add(histValue);
            }
            Map<String, Object> featureVal = new HashMap<>();
            featureVal.put("histogram", histogramProperties);
            feature.setData(featureVal);
            features.add(feature);
          });
    /*% } else { %*/
    DataDTO feature = new DataDTO();

    if (byId.getBuckets().size() == 0) {
      return features;
    }

    feature.setId(Long.parseLong(byId.getBuckets().get(0).getKeyAsString()));

    Aggregations temporalAgg = byId.getBuckets().get(0).getAggregations();
    Histogram byDate = temporalAgg.get("temporal_agg");
    List<Object> histogramProperties = new ArrayList<>();

    for (Histogram.Bucket bucket : byDate.getBuckets()) {
      // Properties containing the MIN, MAX and AVG of all the metrics
      List<Object> properties = new ArrayList<>();

      Aggregations ocupacionAgg = bucket.getAggregations();
      // FILTER BUCKET
      propertyAggregations.forEach(
          property -> {
            String propertyParsed = property.toLowerCase();
            Filter filterBucket = ocupacionAgg.get(propertyParsed + "_filter_agg");

            // Variable to store Aggregation of min/max/avg values
            List<Object> aggregationValues = new ArrayList<>();
            filterBucket
                .getAggregations()
                .forEach(
                    agg -> {
                      // Terms byProperty = (Terms) agg;
                      try {
                        Map<String, Object> aggValue = new HashMap<>();
                        String valueProp = PropertyUtils.getProperty(agg, "value").toString();

                        aggValue.put(
                            agg.getName(),
                            valueProp == "Infinity" || valueProp == "-Infinity" ? null : valueProp);

                        aggregationValues.add(aggValue);
                        // aggValue.put(bucket.getKeyAsString(), finalAggs);
                      } catch (IllegalAccessException
                          | InvocationTargetException
                          | NoSuchMethodException e) {
                        e.printStackTrace();
                      }
                    });
            // Add aggregation values
            properties.add(aggregationValues);
          });
      Map<String, Object> histValue = new HashMap<>();
      histValue.put(bucket.getKeyAsString(), properties);
      histogramProperties.add(histValue);
    }
    Map<String, Object> featureVal = new HashMap<>();
    featureVal.put("histogram", histogramProperties);
    feature.setData(featureVal);
    features.add(feature);
    /*% } %*/
    return features;
  }

  private DateHistogramInterval getDateHistogramInterval(TemporalAggregation temporalAggregation) {
    DateHistogramInterval interval = null;
    switch (temporalAggregation) {
      case YEAR:
        interval = DateHistogramInterval.MONTH;
        break;
      case MONTH:
        interval = DateHistogramInterval.DAY;
        break;
      case WEEK:
        interval = DateHistogramInterval.DAY;
        break;
      case DAY:
        interval = DateHistogramInterval.HOUR;
        break;
      case HOUR:
        interval = DateHistogramInterval.MINUTE;
        break;
      case MINUTE:
        interval = DateHistogramInterval.SECOND;
        break;
      case SECOND:
        interval = DateHistogramInterval.SECOND;
        break;
      default:
        interval = DateHistogramInterval.HOUR;
        break;
    }
    return interval;
  }

}
