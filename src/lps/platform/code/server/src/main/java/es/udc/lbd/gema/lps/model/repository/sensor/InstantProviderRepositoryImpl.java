/*% if (feature.SensorViewer) { %*/
package es.udc.lbd.gema.lps.model.repository.sensor;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class InstantProviderRepositoryImpl implements InstantProviderRepository {

  private final ElasticsearchOperations elasticsearchOperations;

  @Autowired
  public InstantProviderRepositoryImpl(
      final ElasticsearchOperations elasticsearchOperations) {
    super();
    this.elasticsearchOperations = elasticsearchOperations;
  }

  @Override
  public Page<LocalDateTime> getInstantsByDate(String sensorId, LocalDateTime dateInit, LocalDateTime dateEnd,
      Pageable pageable) {

    // Build query
    Query searchQuery = buildNativeSearchQuery(
        QueryBuilders.matchAllQuery(),
        0,
        AggregationBuilders.terms("date_agg")
            .field("@timestamp")
            .order(BucketOrder.key(false))
            .size(20000));

    // Calculate regex index with day precision and t_medida indexes
    String indexName = getIndexRegex(dateInit, "day", sensorId);

    // Execute query
    SearchHits<Object> medidas = null;
    try {
      medidas = elasticsearchOperations.search(searchQuery, Object.class, IndexCoordinates.of(indexName));
    } catch (Exception e) {
      return Page.empty();
    }

    // Return result
    Aggregations aggs = medidas.getAggregations();

    if (aggs != null) {
      Terms byId = aggs.get("date_agg");

      // Format dates and apply pagination
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSXXX");
      List<LocalDateTime> allDateTimes = byId.getBuckets().stream()
          .map(bucket -> LocalDateTime.parse(bucket.getKeyAsString(), formatter))
          .collect(Collectors.toList());

      // Create a pageable list
      PagedListHolder<LocalDateTime> page = new PagedListHolder<>(allDateTimes);
      page.setPageSize(pageable.getPageSize()); // number of items per page
      page.setPage(pageable.getPageNumber()); // current page

      // Return a new PageImpl from the PagedListHolder
      return new PageImpl<>(page.getPageList(), pageable, page.getNrOfElements());
    }
    return Page.empty();
  }

  @Override
  public Page<Timestamp> getDayInstantsByDate(String sensorId, LocalDateTime dateInit, LocalDateTime dateEnd,
      Pageable pageable) {
    List<Timestamp> lista = getDateHistogramAggregationQuery(
        dateInit,
        dateEnd,
        "@timestamp",
        DateHistogramInterval.DAY,
        "yyyy-MM-dd'T'hh:mm:ss",
        "month",
        false,
        sensorId);
    return transformTimestampListToPage(lista, pageable);
  }

  @Override
  public Page<Timestamp> getMonthInstantsByDate(String sensorId, LocalDateTime dateInit, LocalDateTime dateEnd,
      Pageable pageable) {
    List<Timestamp> lista = getDateHistogramAggregationQuery(
        dateInit,
        dateEnd,
        "@timestamp",
        DateHistogramInterval.MONTH,
        "yyyy-MM-dd'T'hh:mm:ss",
        "year",
        false,
        sensorId);

    return transformTimestampListToPage(lista, pageable);
  }

  @Override
  public Page<Timestamp> getYearInstantsByDate(String sensorId, Pageable pageable) {
    List<Timestamp> lista = getDateHistogramAggregationQuery(
        null,
        null,
        "@timestamp",
        DateHistogramInterval.YEAR,
        "yyyy-MM-dd'T'hh:mm:ss",
        "all",
        false,
        sensorId);

    return transformTimestampListToPage(lista, pageable);
  }

  @Override
  public LocalDateTime getLastInstant(String sensorId) {
    // Build query
    Query searchQuery = buildNativeSearchQuery(
        QueryBuilders.matchAllQuery(),
        0,
        AggregationBuilders.min("min_date_agg").field("@timestamp"));

    // Calculate regex index with day precision and t_medida indexes
    String indexName = getIndexRegex(LocalDateTime.now(), "all", sensorId);

    // Execute query
    SearchHits<Object> medidas = null;
    try {
      medidas = elasticsearchOperations.search(searchQuery, Object.class, IndexCoordinates.of(indexName));
    } catch (Exception e) {
      return null;
    }

    // Return result
    Aggregations aggs = medidas.getAggregations();
    if (aggs != null) {
      Min byId = aggs.get("min_date_agg");
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSXXX");

      return byId.getValueAsString() != null
          ? LocalDateTime.parse(byId.getValueAsString(), formatter)
          : null;
    }
    return null;

  }

  // ------------ PRIVATE METHODS ------------

  private String getIndexRegex(LocalDateTime date, String indexLevel, String startIndexName) {
    String finalIndexName = startIndexName;
    if (Objects.equals(indexLevel, "day")) {
      Month month = date.getMonth();
      String monthString = month.getValue() < 10 ? "0" + month.getValue() : month.getValue() + "";
      String dayString = date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : date.getDayOfMonth() + "";
      finalIndexName += date.getYear() + monthString + dayString;
    } else if (Objects.equals(indexLevel, "month")) {
      Month month = date.getMonth();
      String monthString = month.getValue() < 10 ? "0" + month.getValue() : month.getValue() + "";
      finalIndexName += date.getYear() + monthString;
    } else if (Objects.equals(indexLevel, "year")) {
      finalIndexName += String.valueOf(date.getYear());
    }
    finalIndexName += "*";
    return finalIndexName;
  }

  private Query buildNativeSearchQuery(
      QueryBuilder filterQuery, Integer maxResults, AbstractAggregationBuilder<?> aggregation) {
    NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

    // Add filter
    if (filterQuery != null) {
      nativeSearchQueryBuilder.withFilter(filterQuery);
    }

    if (maxResults != null) {
      nativeSearchQueryBuilder.withMaxResults(maxResults);
    }

    // Add aggregation
    if (aggregation != null) {
      nativeSearchQueryBuilder.addAggregation(aggregation);
    }

    // Build query
    return nativeSearchQueryBuilder.build();
  }

  private List<Timestamp> getDateHistogramAggregationQuery(
      LocalDateTime dateInit,
      LocalDateTime dateEnd,
      String aggField,
      DateHistogramInterval calendarInterval,
      String format,
      String indexLevel,
      Boolean isAsc,
      String index) {

    // Filter query
    QueryBuilder filterQuery = null;
    if (dateInit == null && dateEnd == null) {
      filterQuery = QueryBuilders.matchAllQuery();
    } else {
      filterQuery = QueryBuilders.rangeQuery(aggField).from(dateInit).to(dateEnd);
    }

    // Final Query
    Query query = buildNativeSearchQuery(
        filterQuery,
        0,
        AggregationBuilders.dateHistogram("date_agg")
            .field(aggField)
            .calendarInterval(calendarInterval)
            .order(BucketOrder.key(isAsc))
            .format(format));

    // Calculate regex index with day precision and t_medida or t_aq_medida indexes
    String indexName = getIndexRegex(dateInit, indexLevel, index);

    // Execute query
    SearchHits<Object> medidas = null;
    try {
      medidas = elasticsearchOperations.search(query, Object.class, IndexCoordinates.of(indexName));
    } catch (Exception e) {
      return new ArrayList<>();
    }

    // Return result
    Aggregations aggs = medidas.getAggregations();
    if (aggs != null) {
      ParsedDateHistogram byId = aggs.get("date_agg");
      SimpleDateFormat dateFormat = new SimpleDateFormat(format);

      return byId.getBuckets().stream()
          .map(
              bucket -> {
                try {
                  return new Timestamp(dateFormat.parse(bucket.getKeyAsString()).getTime());
                } catch (ParseException e) {
                  return null;
                }
              })
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  private Page<Timestamp> transformTimestampListToPage(List<Timestamp> list, Pageable pageable) {
    if (pageable.isPaged()) {
      int start = (int) pageable.getOffset();
      int end = Math.min((start + pageable.getPageSize()), list.size());
      return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }
    return new PageImpl<>(list);
  }
}
/*% } %*/
