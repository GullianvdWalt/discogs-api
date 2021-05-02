package com.gvdw.discogsapimaster.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Gullian Van Der Walt
 * Created at 12:23 on May, 2021
 */
//@Entity
//@Table(name = "release")
public class Release {
    @Id
    private String id;
    private String title;
    private String country;
    private String releaseType;
    private int releaseYear;
    private String formatType;
    private String formatDesc;
    private String label;
    private String thumbnailPath;
    private int popularity;

    public Release() {
    }

    public Release(String id, String title, String country, String releaseType, int releaseYear, String formatType, String formatDesc, String label, String thumbnailPath, int popularity) {
        this.id = id;
        this.title = title;
        this.country = country;
        this.releaseType = releaseType;
        this.releaseYear = releaseYear;
        this.formatType = formatType;
        this.formatDesc = formatDesc;
        this.label = label;
        this.thumbnailPath = thumbnailPath;
        this.popularity = popularity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public String getFormatDesc() {
        return formatDesc;
    }

    public void setFormatDesc(String formatDesc) {
        this.formatDesc = formatDesc;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    @Override
    public String toString() {
        return "Release{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", country='" + country + '\'' +
                ", releaseType='" + releaseType + '\'' +
                ", releaseYear=" + releaseYear +
                ", formatType='" + formatType + '\'' +
                ", formatDesc='" + formatDesc + '\'' +
                ", label='" + label + '\'' +
                ", thumbnailPath='" + thumbnailPath + '\'' +
                ", popularity=" + popularity +
                '}';
    }
}
