package org.lgangloff.web.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import lombok.Data;

@Data
public class ApiPagination {
    
    private Collection<?> paginateItems;

    private int itemsPerPage;
    private int startIndex;

    private int currentItemCount;
    private int totalItems;
    private int pageIndex;
    private int totalPages;

    public void paginate(List<?> items){
        if(startIndex > items.size())
            startIndex = 0;
        int lastIndex = (startIndex+itemsPerPage) > items.size()+1 ? items.size()+1 : startIndex+itemsPerPage;
        
        paginateItems = items.subList(startIndex-1, lastIndex-1);
        pageIndex = (int) (Math.floor(startIndex / (double)itemsPerPage) + 1);
        currentItemCount = paginateItems.size();
        totalItems = items.size();
        totalPages = (int) Math.ceil(totalItems / (double)itemsPerPage);
    }
}
