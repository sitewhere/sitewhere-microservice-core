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
package com.sitewhere.microservice.api.device;

import com.sitewhere.rest.model.customer.Customer;

/**
 * Used to show broken link if referenced customer is deleted.
 */
public class InvalidCustomer extends Customer {

    /** Serial version UID */
    private static final long serialVersionUID = 4008685314250546242L;

    public InvalidCustomer() {
	setName("Missing Customer");
	setImageUrl("https://s3.amazonaws.com/sitewhere-demo/broken-link.png");
    }
}
