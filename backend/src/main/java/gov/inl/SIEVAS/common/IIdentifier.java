/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.common;

/**
 * Interface for identifiers
 * @author monejh
 */


public interface IIdentifier<T>
{
	public T getId();
	public void setId(T id);

}
