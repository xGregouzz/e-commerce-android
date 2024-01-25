package edu.esiea.ecommerce.utils;

public class ProductListUrlBuilder {

    private static final String BASE_URL = "https://api.escuelajs.co/api/v1/products?";
    private StringBuilder urlBuilder;

    public ProductListUrlBuilder() {
        this.urlBuilder = new StringBuilder(BASE_URL);
    }

    public ProductListUrlBuilder limit(Integer limit) {
        appendQueryParam("limit", limit);
        return this;
    }

    public ProductListUrlBuilder offset(Integer offset) {
        appendQueryParam("offset", offset);
        return this;
    }

    public ProductListUrlBuilder title(String title) {
        appendQueryParam("title", title);
        return this;
    }

    public ProductListUrlBuilder categoryId(Integer categoryId) {
        appendQueryParam("categoryId", categoryId);
        return this;
    }

    public ProductListUrlBuilder price(Double price) {
        appendQueryParam("price", price);
        return this;
    }

    public ProductListUrlBuilder priceMin(Double min) {
        appendQueryParam("price_min", min);
        return this;
    }

    public ProductListUrlBuilder priceMax(Double max) {
        appendQueryParam("price_max", max);
        return this;
    }

    public ProductListUrlBuilder priceRange(Double min, Double max) {
        if (min != null && max != null) {
            appendQueryParam("price_min", min);
            appendQueryParam("price_max", max);
        }
        return this;
    }

    public String getUrl() {
        return urlBuilder.toString();
    }

    public String build() {
        String finalUrl = getUrl();
        urlBuilder = new StringBuilder(BASE_URL);
        return finalUrl;
    }

    private void appendQueryParam(String param, Object value) {
        if (value != null) {
            urlBuilder.append(String.format("%s=%s&", param, value));
        }
    }
}
