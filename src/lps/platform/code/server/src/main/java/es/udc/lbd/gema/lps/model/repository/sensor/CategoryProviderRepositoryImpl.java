/*% if (feature.SensorViewer) { %*/
/*%
  const dimensions = [];
  data.dataWarehouse.sensors.forEach(function(sensor) {
    const dims = sensor.dimensions;
    dims
      .filter(dim => dim.type === "CATEGORICAL")
      .forEach(dim => {
        dimensions.push(dim);
      });
  });
  var hasCategoricalDims = dimensions.length > 0;
%*/
/*% if (hasCategoricalDims) { %*/
package es.udc.lbd.gema.lps.model.repository.sensor;

import es.udc.lbd.gema.lps.model.domain.sensor.TemporalAggregation;
import es.udc.lbd.gema.lps.model.service.dto.sensor.CategoryDTO;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryProviderRepositoryImpl implements CategoryProviderRepository {

  private final ElasticsearchOperations elasticsearchOperations;

  @Autowired
  public CategoryProviderRepositoryImpl(ElasticsearchOperations elasticsearchOperations) {
    super();
    this.elasticsearchOperations = elasticsearchOperations;
  }

  @Override
  public List<CategoryDTO> findAllByCategory(
      String repo_url,
      String category,
      LocalDateTime date,
      TemporalAggregation tempAgg,
      String sensorId) {

    NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
    nativeSearchQueryBuilder.withMaxResults(0);

    // Create the query
    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
    QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
    QueryBuilder rangeQuery = null;

    // Filter by date
    if (Objects.nonNull(date)) {

      // Calculate the local Timezone from the system

      // Convert to GMT
      LocalDateTime dateStart =
          date.atZone(ZoneId.systemDefault())
              .withZoneSameInstant(ZoneId.of("GMT+2"))
              .toLocalDateTime();
      LocalDateTime dateEnd =
          date.atZone(ZoneId.systemDefault())
              .withZoneSameInstant(ZoneId.of("GMT+2"))
              .toLocalDateTime();

      // Depending on the temporal aggregation, we calculate the start and end date
      if (tempAgg != null) {
        if (tempAgg.equals(TemporalAggregation.DAY)) {
          // From 00:00 to 23:59
          dateStart = dateStart.withHour(0).withMinute(0).withSecond(0);
          dateEnd = dateEnd.withHour(23).withMinute(59).withSecond(59);
        } else if (tempAgg.equals(TemporalAggregation.MONTH)) {
          // From 00:00 of first day of the month to 23:59 of last day of the month
          dateStart = dateStart.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
          dateEnd =
              dateEnd
                  .withDayOfMonth(date.getMonth().maxLength())
                  .withHour(23)
                  .withMinute(59)
                  .withSecond(59);
        } else if (tempAgg.equals(TemporalAggregation.YEAR)) {
          // From 00:00 of first day of the year to 23:59 of last day of the year
          dateStart =
              dateStart.withDayOfYear(1).withMonth(0).withHour(0).withMinute(0).withSecond(0);
          dateEnd =
              dateEnd
                  .withDayOfYear(date.getMonth().maxLength())
                  .withMonth(12)
                  .withHour(23)
                  .withMinute(59)
                  .withSecond(59);
        }
      }

      if (tempAgg == null) {
        rangeQuery = QueryBuilders.matchQuery("@timestamp", dateStart);
      } else {
        rangeQuery = QueryBuilders.rangeQuery("@timestamp").from(dateStart).to(dateEnd);
      }
      boolQueryBuilder.must(rangeQuery);
    }

    // Filter by sensorId
    if (Objects.nonNull(sensorId)) {
      queryBuilder = QueryBuilders.matchQuery("sensor_id", sensorId);
      boolQueryBuilder.must(queryBuilder);
    }

    nativeSearchQueryBuilder.withQuery(boolQueryBuilder);

    // Create the aggregation
    nativeSearchQueryBuilder.addAggregation(
        AggregationBuilders.terms("category")
            .field(category)
            .size(65540)
            .order(BucketOrder.key(false)));

    // Get index name depending on the temporal aggregation
    String indexName = getIndexRegex(date, tempAgg, repo_url);

    // Execute the query
    SearchHits<Object> hits = null;
    try {
      hits =
          elasticsearchOperations.search(
              nativeSearchQueryBuilder.build(), Object.class, IndexCoordinates.of(indexName));
    } catch (Exception e) {
      return new ArrayList<CategoryDTO>();
    }

    // Get the aggregation
    List<CategoryDTO> result = new ArrayList<CategoryDTO>();
    Terms aggregation = hits.getAggregations().get("category");

    // Create the result
    aggregation.getBuckets().stream()
        // Sort ascending by key if the category is a number
        .sorted(
            (bucket1, bucket2) -> {
              if (bucket1.getKeyAsString().matches("-?\\d+(\\.\\d+)?")
                  && bucket2.getKeyAsString().matches("-?\\d+(\\.\\d+)?")) {
                return Double.compare(
                    Double.parseDouble(bucket1.getKeyAsString()),
                    Double.parseDouble(bucket2.getKeyAsString()));
              } else {
                return bucket1.getKeyAsString().compareTo(bucket2.getKeyAsString());
              }
            })
        .forEach(
            bucket -> {
              CategoryDTO categoryDTO = new CategoryDTO();
              categoryDTO.setLabel(bucket.getKeyAsString());
              categoryDTO.setValue(bucket.getKeyAsString());
              result.add(categoryDTO);
            });

    return result;
  }

  /**
   * Get the index name depending on the temporal aggregation
   *
   * @param date
   * @param tempAgg
   * @param baseIndexName
   * @return
   */
  private String getIndexRegex(
      LocalDateTime date, TemporalAggregation tempAgg, String baseIndexName) {

    date =
        date.atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneId.of("GMT+2"))
            .toLocalDateTime();

    String finalIndexName = baseIndexName;

    if (tempAgg == null) {
      return finalIndexName + "*";
    }

    if (tempAgg.equals(TemporalAggregation.DAY)) {
      Month month = date.getMonth();
      String monthString = month.getValue() < 10 ? "0" + month.getValue() : month.getValue() + "";
      String dayString =
          date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : date.getDayOfMonth() + "";
      finalIndexName += date.getYear() + monthString + dayString;

    } else if (tempAgg.equals(TemporalAggregation.MONTH)) {
      Month month = date.getMonth();
      String monthString = month.getValue() < 10 ? "0" + month.getValue() : month.getValue() + "";
      finalIndexName += date.getYear() + monthString;

    } else if (tempAgg.equals(TemporalAggregation.YEAR)) {
      finalIndexName += String.valueOf(date.getYear());
    }

    finalIndexName += "*";
    return finalIndexName;
  }
}
/*% } %*/
/*% } %*/
