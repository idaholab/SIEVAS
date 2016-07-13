/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author monejh
 */
public class Utility
{
    private static String NormalizeGetter(String field)
    {
        return "get"+ field.substring(0,1).toUpperCase() + field.substring(1);
    }
    
    public static List<Order> ProcessOrders(String sortField, int sortOrder,CriteriaBuilder cb, Root<?> root, String defaultSortColumn, ObjectMapper objMapper)
    {
        List<Order> orderList = new ArrayList<>();
        if (!sortField.trim().isEmpty())
        {
            
            if (sortOrder>0)
                orderList.add(cb.asc(root.get(sortField.trim())));
            else
                orderList.add(cb.desc(root.get(sortField.trim())));
        }
        if (orderList.isEmpty())
            orderList.add(cb.asc(root.get(defaultSortColumn)));
        
        return orderList;
    }
    
    public static List<Predicate> ProcessFilters(String filters, CriteriaBuilder cb, Root<?> root, Class<?> cls, ObjectMapper objMapper)
    {
        List<Predicate> predicateList = new ArrayList<>();
        if (!filters.isEmpty())
        {
            JsonNode node = null;
            try
            {
                 node = objMapper.readTree(filters);
            }
            catch (IOException ex)
            {
                Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            if (node != null)
            {
                String fieldName;
                
                Iterator<String> iter = node.fieldNames();
                while(iter.hasNext() && (fieldName = iter.next()) != null )
                {
                    JsonNode fieldNode = node.get(fieldName);
                    if (fieldNode==null)
                        continue;
                    FilterData filterData = null;
                    System.out.println("Field:" + fieldName + ", Data:" + fieldNode.toString());
                    try
                    {
                        filterData = objMapper.readValue(fieldNode.toString(), FilterData.class);
                    }
                    catch(IOException e)
                    {
                        Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, e);
                        continue;
                    }
                    
                    if ((filterData!=null) && (!filterData.getValue().isEmpty()))
                    {
                        try
                        {
                            if (cls.getMethod(NormalizeGetter(fieldName)).getReturnType().isAssignableFrom(Double.class))
                                predicateList.add(cb.equal(root.get(fieldName), Double.parseDouble(filterData.getValue())));
                            else if (cls.getMethod(NormalizeGetter(fieldName)).getReturnType().isAssignableFrom(Float.class))
                                predicateList.add(cb.equal(root.get(fieldName), Float.parseFloat(filterData.getValue())));
                            else if (cls.getMethod(NormalizeGetter(fieldName)).getReturnType().isAssignableFrom(Long.class))
                                predicateList.add(cb.equal(root.get(fieldName), Long.parseLong(filterData.getValue())));
                            else if (cls.getMethod(NormalizeGetter(fieldName)).getReturnType().isAssignableFrom(Integer.class))
                                predicateList.add(cb.equal(root.get(fieldName), Integer.parseInt(filterData.getValue())));
                            else if (cls.getMethod(NormalizeGetter(fieldName)).getReturnType().isAssignableFrom(Short.class))
                                predicateList.add(cb.equal(root.get(fieldName), Short.parseShort(filterData.getValue())));
                            else if (cls.getMethod(NormalizeGetter(fieldName)).getReturnType().isAssignableFrom(Byte.class))
                                predicateList.add(cb.equal(root.get(fieldName), Byte.parseByte(filterData.getValue())));
                            else if (cls.getMethod(NormalizeGetter(fieldName)).getReturnType().isAssignableFrom(Boolean.class))
                                predicateList.add(cb.equal(root.get(fieldName), Boolean.parseBoolean(filterData.getValue())));
                            else if (cls.getMethod(NormalizeGetter(fieldName)).getReturnType().isAssignableFrom(String.class))
                            {
                                if (filterData.getMatchMode().equals("contains"))
                                    predicateList.add(cb.like(root.get(fieldName), "%" + filterData.getValue() + "%"));
                                else if (filterData.getMatchMode().equals("startsWith"))
                                    predicateList.add(cb.like(root.get(fieldName), filterData.getValue() + "%"));
                                else if (filterData.getMatchMode().equals("endsWith"))
                                    predicateList.add(cb.like(root.get(fieldName), "%" + filterData.getValue()));
                            }
                        }
                        catch(NumberFormatException e)
                        {
                            Logger.getLogger(Utility.class.getName()).log(Level.INFO, null, e);
                        }
                        catch (Exception ex)
                        {
                            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                
            }
        }
        return predicateList;
    }
}
