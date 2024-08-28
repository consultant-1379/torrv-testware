package com.ericsson.nms.rv.taf.test.networkexplorer.datasource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.DataSource;
import com.ericsson.cifwk.taf.utils.FileFinder;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.DataSourceBuilder;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto.Criteria;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto.QueryObject;

/**
 * Created by ewandaf on 01/07/14.
 */
public class QueryBuilderDataSource {

    public static final String CRITERIA_CSV = "Criteria.csv";
    Logger logger = LoggerFactory.getLogger(QueryBuilderDataSource.class);

    @DataSource
    public List<Map<String, QueryObject>> queryBuilder() {
        final List<Map<String, QueryObject>> toReturn = new ArrayList<>();
        final File file = new File(FileFinder.findFile(CRITERIA_CSV).get(0));
        boolean firstLine = true;
        QueryObject firstQueryObject = null;
        QueryObject queryObject = null;
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            while (scanner.hasNext()) {
                final String line = scanner.nextLine();
                // ensure the line is not blank
                if (!line.isEmpty()) {
                    // build the toppest query object in the query tree
                    if (firstLine) {
                        firstQueryObject = buildQueryObject(null, line);
                        queryObject = firstQueryObject;
                        firstLine = false;
                    } else {
                        // build its children
                        queryObject = buildQueryObject(queryObject, line);
                    }
                } else {
                    // this block exist because there maybe new query object tree. In property file, multiple query objects are separated by blank line.
                    firstLine = true;
                    final Map<String, QueryObject> map = new HashMap<>();
                    map.put("queryObject", firstQueryObject);
                    toReturn.add(map);
                }
            }
            if (!firstLine) {
                final Map<String, QueryObject> map = new HashMap<>();
                map.put("queryObject", firstQueryObject);
                toReturn.add(map);
            }
        } catch (final FileNotFoundException e) {
            logger.info("Did not find file " + CRITERIA_CSV);
            return toReturn;
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return toReturn;
    }

    public static QueryObject buildQueryObject(QueryObject parent, String line) {
        line = DataSourceBuilder.parseLine(line);
        final String[] strings = line.split(":");
        final String name = strings[0];
        final List<Criteria> criteriaList = new ArrayList<Criteria>();
        if (strings.length == 2) {
            final String[] criterias = strings[1].split(",");

            for (final String c : criterias) {
                criteriaList.add(new Criteria(c));
            }
        }
        final QueryObject queryObject = new QueryObject(name, null,
                criteriaList);
        if (parent != null) {
            parent.setChild(queryObject);
        }
        return queryObject;
    }
}
