package com.SpringBootMVC.ExpensesTracker.DTO;

public class FilterDTO {
    private Integer categoryId;
    private int from;
    private int to;
    private String month;
    private String year;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "categoryId=" + categoryId +
                ", from=" + from +
                ", to=" + to +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                '}';
    }
}
