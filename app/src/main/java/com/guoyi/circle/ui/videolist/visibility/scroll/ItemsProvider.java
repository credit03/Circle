package com.guoyi.circle.ui.videolist.visibility.scroll;


import com.guoyi.circle.ui.videolist.visibility.items.ListItem;

/**
 * @author Wayne
 */
public interface ItemsProvider {

    ListItem getListItem(int position);

    int listItemSize();

}
