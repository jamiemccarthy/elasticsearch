/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.query;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanFirstQuery;
import org.elasticsearch.common.ParsingException;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.spanTermQuery;
import static org.hamcrest.CoreMatchers.instanceOf;

public class SpanFirstQueryBuilderTests extends AbstractQueryTestCase<SpanFirstQueryBuilder> {
    @Override
    protected SpanFirstQueryBuilder doCreateTestQueryBuilder() {
        SpanTermQueryBuilder[] spanTermQueries = new SpanTermQueryBuilderTests().createSpanTermQueryBuilders(1);
        return new SpanFirstQueryBuilder(spanTermQueries[0], randomIntBetween(0, 1000));
    }

    @Override
    protected void doAssertLuceneQuery(SpanFirstQueryBuilder queryBuilder, Query query, QueryShardContext context) throws IOException {
        assertThat(query, instanceOf(SpanFirstQuery.class));
    }

    /**
     * test exception on missing `end` and `match` parameter in parser
     */
    public void testParseEnd() throws IOException {
        {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            builder.startObject(SpanFirstQueryBuilder.NAME);
            builder.field("match");
            spanTermQuery("description", "jumped").toXContent(builder, null);
            builder.endObject();
            builder.endObject();

            try {
                parseQuery(builder.string());
                fail("missing [end] parameter should raise exception");
            } catch (ParsingException e) {
                assertTrue(e.getMessage().contains("spanFirst must have [end] set"));
            }
        }
        {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            builder.startObject(SpanFirstQueryBuilder.NAME);
            builder.field("end", 10);
            builder.endObject();
            builder.endObject();

            try {
                parseQuery(builder.string());
                fail("missing [match] parameter should raise exception");
            } catch (ParsingException e) {
                assertTrue(e.getMessage().contains("spanFirst must have [match] span query clause"));
            }
        }
    }
}
