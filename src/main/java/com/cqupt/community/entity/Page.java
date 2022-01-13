package com.cqupt.community.entity;

public class Page {
    private int currentPage = 1;
    private int pageSize = 10;
    private int totals;
    private String path = "/index";

    @Override
    public String toString() {
        return "Page{" +
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totals=" + totals +
                ", path='" + path + '\'' +
                '}';
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotals() {
        return totals;
    }

    public void setTotals(int totals) {
        this.totals = totals;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOffSet () {
        return (currentPage - 1) * pageSize;
    }

    public int getPageFrom() {
        int from = currentPage - 2;
        int pageFrom = from <= 0 ? 1 : from;
        return pageFrom;
    }

    public int getPageTo(){
        int to = currentPage + 2;
        int pageTo = to > getTotalPage() ? getTotalPage() : to;
        return pageTo;
    }

    public int getTotalPage(){
        int totalPages = 0;
        if (totals % pageSize == 0) {
            totalPages = totals / pageSize;
        } else {
            totalPages = totals / pageSize + 1;
        }

        return totalPages;
    }
}
