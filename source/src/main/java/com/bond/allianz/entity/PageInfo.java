package com.bond.allianz.entity;

import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 * 分页对象
 * @param <T>
 */
public class PageInfo<T> implements Serializable {

    /**
     * 当前页
     */
    @Autowired
    public int CurrentPage;

    /**
     * 每页数量
     */
    @Autowired
    public  int ItemsPerPage;

    /**
     * 总数量
     */
    @Autowired
    public  long TotalItems;

    /**
     * 总页数
     */
    @Autowired
    public int TotalPages;

    /**
     * 分页数据对象
     */
    @Autowired
    public List<T> Items;

    /**
     * 其他对象
     */
    @Autowired
    public  Object Context;

    /**
     * 时间
     */
    @Autowired
    public  double Time;

    public PageInfo(){

    }
    public PageInfo(List<T> list){
        if (list instanceof Page) {
            Page page = (Page) list;
            this.CurrentPage = page.getPageNum();
            this.ItemsPerPage = page.getPageSize();

            this.TotalPages = page.getPages();
            this.Items = page;
            this.TotalItems = page.getTotal();
        } else if (list instanceof Collection) {
            this.CurrentPage = 1;
            this.ItemsPerPage = list.size();

            this.TotalPages = 1;
            this.Items = list;
            this.TotalItems = list.size();
        }

    }

    /**
     * 自定义pageinfo对象
     * @param list
     * @param pageindex
     * @param pagesize
     * @param total
     */
    public PageInfo(List<T> list,int pageindex,int pagesize,long total){
        if (list instanceof Collection) {
            this.CurrentPage = pageindex;
            this.ItemsPerPage = pagesize;

            this.Items = list;
            this.TotalItems = total;
            this.TotalPages=(int)(total / (long)pagesize + (long)(total % (long)pagesize == 0L ? 0 : 1));
            //this.TotalPages = 1;
        }

    }
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PageInfo{");
        sb.append("pageNum=").append(CurrentPage);
        sb.append(", pageSize=").append(ItemsPerPage);
        sb.append(", total=").append(TotalItems);
        sb.append(", pages=").append(TotalPages);
        sb.append(", list=").append(Items);
        //sb.append(", isFirstPage=").append(isFirstPage);
        //sb.append(", isLastPage=").append(isLastPage);
        //sb.append(", navigatepageNums=");
        sb.append('}');
        return sb.toString();
    }

}
