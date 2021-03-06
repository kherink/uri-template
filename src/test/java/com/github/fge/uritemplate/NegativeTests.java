/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.uritemplate;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.uritemplate.vars.VariableMap;
import com.github.fge.uritemplate.vars.VariableMapBuilder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public final class NegativeTests
{
    private JsonNode data;
    private VariableMap vars;

    @BeforeClass
    public void initData()
        throws IOException
    {
        data = JsonLoader.fromResource("/negative-tests.json");
        final JsonNode node = data.get("Failure Tests").get("variables");

        final VariableMapBuilder builder = VariableMap.newBuilder();
        final Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
        Map.Entry<String, JsonNode> entry;

        while (iterator.hasNext()) {
            entry = iterator.next();
            builder.addValue(entry.getKey(), Util.fromJson(entry.getValue()));
        }

        vars = builder.freeze();
    }

    @DataProvider
    public Iterator<Object[]> getData()
    {
        final JsonNode node = data.get("Failure Tests").get("testcases");
        final List<Object[]> list = Lists.newArrayList();

        for (final JsonNode element: node)
            list.add(new Object[] { element.get(0).textValue() });

        return list.iterator();
    }

    @Test(dataProvider = "getData")
    public void illegalTemplatesAreMarkedAsSuch(final String input)
    {
        try {
            new URITemplate(input).toString(vars);
            fail("No exception thrown!!");
        } catch (URITemplateParseException ignored) {
        } catch (URITemplateException ignored) {
        }
    }
}

