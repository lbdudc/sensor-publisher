/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service.dto;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.FeedInfo;
import es.udc.lbd.gema.lps.model.domain.*;
import java.time.LocalDate;

public class FeedInfoDTO {

  private Long id;
  private String feedPublisherName;
  private String feedPublisherUrl;
  private String feedLang;
  private String defaultLang;
  private LocalDate feedStartDate;
  private LocalDate feedEndDate;
  private String feedVersion;
  private String feedContactEmail;
  private String feedContactUrl;

  public FeedInfoDTO() {}

  public FeedInfoDTO(FeedInfo feedInfo) {
    this.id = feedInfo.getId();
    this.feedPublisherName = feedInfo.getFeedPublisherName();
    this.feedPublisherUrl = feedInfo.getFeedPublisherUrl();
    this.feedLang = feedInfo.getFeedLang();
    this.defaultLang = feedInfo.getDefaultLang();
    this.feedStartDate = feedInfo.getFeedStartDate();
    this.feedEndDate = feedInfo.getFeedEndDate();
    this.feedVersion = feedInfo.getFeedVersion();
    this.feedContactEmail = feedInfo.getFeedContactEmail();
    this.feedContactUrl = feedInfo.getFeedContactUrl();
  }

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

  public FeedInfo toFeedInfo() {
    FeedInfo feedInfo = new FeedInfo();
    feedInfo.setId(this.getId());
    feedInfo.setFeedPublisherName(this.getFeedPublisherName());
    feedInfo.setFeedPublisherUrl(this.getFeedPublisherUrl());
    feedInfo.setFeedLang(this.getFeedLang());
    feedInfo.setDefaultLang(this.getDefaultLang());
    feedInfo.setFeedStartDate(this.getFeedStartDate());
    feedInfo.setFeedEndDate(this.getFeedEndDate());
    feedInfo.setFeedVersion(this.getFeedVersion());
    feedInfo.setFeedContactEmail(this.getFeedContactEmail());
    feedInfo.setFeedContactUrl(this.getFeedContactUrl());
    return feedInfo;
  }
}
/*% } %*/
