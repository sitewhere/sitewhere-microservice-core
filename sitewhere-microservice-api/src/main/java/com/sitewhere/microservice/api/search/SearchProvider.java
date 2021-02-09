/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.microservice.api.search;

/**
 * Implementation of {@link ISearchProvider} used for marshaling.
 */
public class SearchProvider {

    /** Provider id */
    private String id;

    /** Provider name */
    private String name;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.search.external.ISearchProvider#getId()
     */
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.search.external.ISearchProvider#getName()
     */
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /**
     * Create copy of an {@link ISearchProvider} for marshaling.
     * 
     * @param source
     * @return
     */
    public static SearchProvider copy(ISearchProvider source) {
	SearchProvider provider = new SearchProvider();
	provider.setId(source.getId());
	provider.setName(source.getName());
	return provider;
    }
}