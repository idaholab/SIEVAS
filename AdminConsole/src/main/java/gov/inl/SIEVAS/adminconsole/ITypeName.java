/* 
 * Copyright 2017 Idaho National Laboratory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.inl.SIEVAS.adminconsole;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Simple type name, copied from backend.
 * @author monejh
 */
public abstract class ITypeName
{
    @JsonIgnore
    public String getTypeName()
    {
        return this.getClass().getSimpleName();
    }
}
