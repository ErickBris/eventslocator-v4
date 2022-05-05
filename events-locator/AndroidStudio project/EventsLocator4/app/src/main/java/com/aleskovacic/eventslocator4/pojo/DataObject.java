package com.aleskovacic.eventslocator4.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;


public class DataObject implements ClusterItem, Parcelable {
    public static final String EXTRA_LIST_ARRAY = "extralist";
    public static final String EXTRA_OBJECT = "extraobject";
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DataObject> CREATOR = new Parcelable.Creator<DataObject>() {
        @Override
        public DataObject createFromParcel(Parcel in) {
            return new DataObject(in);
        }

        @Override
        public DataObject[] newArray(int size) {
            return new DataObject[size];
        }
    };
    private static SimpleDateFormat inputFormat;
    private static SimpleDateFormat outputFormat;
    private long eventId;
    private long pageId;
    private long placeId;
    private String title;
    private String category;
    private String startTime;
    private String endTime;
    private String description;
    private String photo;
    private String coverPhoto;
    private int attendeesCount;
    private String placeName;
    private String street;
    private String city;
    private String zip;
    private String country;
    private LatLng position;
    private Double latitude;
    private Double longitude;
    private String pageName;

    public DataObject() {
        this.inputFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:ss");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        outputFormat = new SimpleDateFormat("MMM d, HH:ss", Locale.getDefault());
    }

    protected DataObject(Parcel in) {
        eventId = in.readLong();
        pageId = in.readLong();
        placeId = in.readLong();
        title = in.readString();
        category = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        description = in.readString();
        photo = in.readString();
        coverPhoto = in.readString();
        attendeesCount = in.readInt();
        placeName = in.readString();
        street = in.readString();
        city = in.readString();
        zip = in.readString();
        country = in.readString();
        position = (LatLng) in.readValue(LatLng.class.getClassLoader());
        latitude = in.readByte() == 0x00 ? null : in.readDouble();
        longitude = in.readByte() == 0x00 ? null : in.readDouble();
        pageName = in.readString();
    }

    @Override
    public LatLng getPosition() {
        if (position != null) {
            return position;
        }
        return position = new LatLng(latitude, longitude);
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getPageId() {
        return pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public int getAttendeesCount() {
        return attendeesCount;
    }

    public void setAttendeesCount(int attendeesCount) {
        this.attendeesCount = attendeesCount;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartDate() {
        try {
            return outputFormat.format(inputFormat.parse(startTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(eventId);
        dest.writeLong(pageId);
        dest.writeLong(placeId);
        dest.writeString(title);
        dest.writeString(category);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(description);
        dest.writeString(photo);
        dest.writeString(coverPhoto);
        dest.writeInt(attendeesCount);
        dest.writeString(placeName);
        dest.writeString(street);
        dest.writeString(city);
        dest.writeString(zip);
        dest.writeString(country);
        dest.writeValue(position);
        if (latitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(longitude);
        }
        dest.writeString(pageName);
    }
}