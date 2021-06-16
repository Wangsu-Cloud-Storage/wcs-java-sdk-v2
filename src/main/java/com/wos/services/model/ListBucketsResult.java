package com.wos.services.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Response to a request for listing buckets
 *
 */
public class ListBucketsResult extends HeaderResponse {
    private List<WosBucket> buckets;

    private Owner owner;

    public ListBucketsResult(List<WosBucket> buckets, Owner owner) {
        this.buckets = buckets;
        this.owner = owner;
    }

    public List<WosBucket> getBuckets() {
        if (buckets == null) {
            buckets = new ArrayList<WosBucket>();
        }
        return buckets;
    }

    public Owner getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "ListBucketsResult [buckets=" + buckets + ", owner=" + owner + "]";
    }

}
