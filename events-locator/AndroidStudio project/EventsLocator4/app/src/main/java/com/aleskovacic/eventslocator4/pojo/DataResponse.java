package com.aleskovacic.eventslocator4.pojo;

import java.util.List;

public class DataResponse {
    private boolean success;
    private List<DataObject> data;

    public DataResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public List<DataObject> getData() {
        return data;
    }

    public void setData(List<DataObject> data) {
        this.data = data;
    }
}
