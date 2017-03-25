package com.zenkun.fundatest.utilities;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.zenkun.fundatest.model.ModelHouse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zen zenyagami@gmail.com on 25/03/2017.
 */

public class SortUtil {
    /**
     * Sort the Hasmap in Descending order
     * @param unsorted : Unsorted Hashmap
     * @return
     */
    public static List<ModelHouse> sortMapDescending(Map<String,ModelHouse> unsorted)
    {
        //limit is 11, 1 is the top1 and then  the rest is top 10

        // new java 8 API to sort a stream is faster and easier, im using pre 24 api to support more devices
        List<ModelHouse> sorted = Stream.of(unsorted)
                .limit(11)
                .sorted(new Comparator<Map.Entry<String, ModelHouse>>() {
                    @Override
                    public int compare(Map.Entry<String, ModelHouse> o1, Map.Entry<String, ModelHouse> o2) {
                        int x =o2.getValue().amountOfSales;
                        int y=o1.getValue().amountOfSales;
                        return (x < y) ? -1 : ((x == y) ? 0 : 1);

                    }
                })
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        return sorted;
    }
}
