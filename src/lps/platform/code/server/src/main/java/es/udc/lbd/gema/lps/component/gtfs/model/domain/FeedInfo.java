/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.domain;

import com.fasterxml.jackson.annotation.JsonView;
import es.udc.lbd.gema.lps.model.views.Views;
import java.time.LocalDate;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.SequenceGenerator;

@Entity(name = "t_feed_info")
@Table(name = "t_feed_info")
public class FeedInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feedInfoid")
  @SequenceGenerator(
      name = "feedInfoid",
      sequenceName = "t_feed_info_id_seq",
      initialValue = 1,
      allocationSize = 1)
  @JsonView(Views.General.class)
  @Column(name = "id", unique = true)
  private Long id;

  @JsonView(Views.General.class)
  @Column(name = "feed_publisher_name")
  private String feedPublisherName;

  @JsonView(Views.General.class)
  @Column(name = "feed_publisher_url")
  private String feedPublisherUrl;

  @JsonView(Views.General.class)
  @Column(name = "feed_lang")
  private String feedLang;

  @JsonView(Views.General.class)
  @Column(name = "default_lang")
  private String defaultLang;

  @JsonView(Views.General.class)
  @Column(name = "feed_start_date")
  private LocalDate feedStartDate;

  @JsonView(Views.General.class)
  @Column(name = "feed_end_date")
  private LocalDate feedEndDate;

  @JsonView(Views.General.class)
  @Column(name = "feed_version")
  private String feedVersion;

  @JsonView(Views.General.class)
  @Column(name = "feed_contact_email")
  private String feedContactEmail;

  @JsonView(Views.General.class)
  @Column(name = "feed_contact_url")
  private String feedContactUrl;

  public FeedInfo() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFeedPublisherName() {
    return feedPublisherName;
  }

  public void setFeedPublisherName(String feedPublisherName) {
    this.feedPublisherName = feedPublisherName;
  }

  public String getFeedPublisherUrl() {
    return feedPublisherUrl;
  }

  public void setFeedPublisherUrl(String feedPublisherUrl) {
    this.feedPublisherUrl = feedPublisherUrl;
  }

  public String getFeedLang() {
    return feedLang;
  }

  public void setFeedLang(String feedLang) {
    this.feedLang = feedLang;
  }

  public String getDefaultLang() {
    return defaultLang;
  }

  public void setDefaultLang(String defaultLang) {
    this.defaultLang = defaultLang;
  }

  public LocalDate getFeedStartDate() {
    return feedStartDate;
  }

  public void setFeedStartDate(LocalDate feedStartDate) {
    this.feedStartDate = feedStartDate;
  }

  public LocalDate getFeedEndDate() {
    return feedEndDate;
  }

  public void setFeedEndDate(LocalDate feedEndDate) {
    this.feedEndDate = feedEndDate;
  }

  public String getFeedVersion() {
    return feedVersion;
  }

  public void setFeedVersion(String feedVersion) {
    this.feedVersion = feedVersion;
  }

  public String getFeedContactEmail() {
    return feedContactEmail;
  }

  public void setFeedContactEmail(String feedContactEmail) {
    this.feedContactEmail = feedContactEmail;
  }

  public String getFeedContactUrl() {
    return feedContactUrl;
  }

  public void setFeedContactUrl(String feedContactUrl) {
    this.feedContactUrl = feedContactUrl;
  }
}
/*% } %*/
