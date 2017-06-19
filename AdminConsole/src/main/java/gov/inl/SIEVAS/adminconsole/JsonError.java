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

/**
 * Class for errors that include and error string as JSON.
 * @author monejh
 */
public class JsonError extends ITypeName
{
    private String error;

    /***
     * Default constructor. Does nothing.
     */
    public JsonError()
    {
    }

    /**
     * Constructor that takes the error string.
     * @param error The error.
     */
    public JsonError(String error)
    {
        this.error = error;
    }

    
    /***
     * Gets the error string
     * @return the error
     */
    public String getError()
    {
        return error;
    }

    /***
     * Sets the error string.
     * @param error The error message.
     */
    public void setError(String error)
    {
        this.error = error;
    }
    
    
}
