/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.inl.LIVE.DAO.CriteriaBuilderCriteriaQueryRootTriple;
import gov.inl.LIVE.DAO.UserInfoDAO;
import gov.inl.LIVE.entity.UserInfo;
import gov.inl.LIVE.entity.UserInfo_;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
    
    
    public static class CompareClass<T> implements Comparator<T>
    {
        private String fieldName;
        private int sortOrder;
        private Method method;

        public CompareClass(Class<T> cls, String fieldName, int sortOrder) throws NoSuchMethodException
        {
            this.fieldName = fieldName;
            this.sortOrder = sortOrder;
            method = cls.getMethod(NormalizeGetter(this.fieldName));
            
        }
        

        @Override
        public int compare(T o1, T o2)
        {
            try
            {
                if (method.getReturnType().isAssignableFrom(Comparable.class))
                {
                    Comparable obj1 = (Comparable) method.invoke(o1);
                    Comparable obj2 = (Comparable) method.invoke(o2);
                    if (sortOrder>0)
                        return obj1.compareTo(obj2);
                    else
                        return obj2.compareTo(obj1);
                }
                else
                    return -1;
                
            }
            catch (Exception ex)
            {
                Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
            
        }
        
    }
    
    
    public static <T> void ProcessOrders(T[] list, Class<T> cls, String sortField, int sortOrder, String defaultSortColumn, ObjectMapper objMapper)
    {
        List<Order> orderList = new ArrayList<>();
        if (!sortField.trim().isEmpty())
        {
            CompareClass<T> compareClass = null;
            try
            {
                compareClass = new CompareClass<T>(cls, sortField, sortOrder);
            }
            catch(NoSuchMethodException e)
            {
            }
            if (compareClass != null)
                Arrays.sort(list,compareClass);
        }
        else
        {
            CompareClass<T> compareClass = null;
            try
            {
                compareClass = new CompareClass<T>(cls, defaultSortColumn, sortOrder);
            }
            catch(NoSuchMethodException e)
            {
            }
            if (compareClass != null)
                Arrays.sort(list,compareClass);
        }
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
    
    public static <T> List<T> ProcessFilters(Collection<T> list, String filters, Class<T> cls, ObjectMapper objMapper)
    {
        List<T> returnList = new ArrayList<>();
        for(T item: list)
        {
            boolean match = true;
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
                            match = false;
                            try
                            {
                                Method method = cls.getMethod(NormalizeGetter(fieldName));
                                if (method.getReturnType().isAssignableFrom(String.class))
                                {
                                    if (filterData.getMatchMode().equals("contains"))
                                    {    
                                        String value = (String)method.invoke(item);
                                        if (value.contains(filterData.getValue()))
                                            match = true;
                                        
                                    }
                                    else if (filterData.getMatchMode().equals("startsWith"))
                                    {
                                        String value = (String)method.invoke(item);
                                        if (value.startsWith(filterData.getValue()))
                                            match = true;
                                        
                                    }
                                    else if (filterData.getMatchMode().equals("endsWith"))
                                    {
                                        String value = (String)method.invoke(item);
                                        if (value.endsWith(filterData.getValue()))
                                            match = true;
                                        
                                    }
                                }
                                else if (method.getReturnType().isAssignableFrom(Long.class))
                                    if (Objects.equals(method.invoke(item),Long.parseLong(filterData.getValue())))
                                        match = true;
                                else if (method.getReturnType().isAssignableFrom(Integer.class))
                                    if (Objects.equals(method.invoke(item),Integer.parseInt(filterData.getValue())))
                                        match = true;
                                else if (method.getReturnType().isAssignableFrom(Short.class))
                                    if (Objects.equals(method.invoke(item),Short.parseShort(filterData.getValue())))
                                        match = true;
                                else if (method.getReturnType().isAssignableFrom(Byte.class))
                                    if (Objects.equals(method.invoke(item),Byte.parseByte(filterData.getValue())))
                                        match = true;
                                else if (method.getReturnType().isAssignableFrom(Boolean.class))
                                    if (Objects.equals(method.invoke(item),Boolean.parseBoolean(filterData.getValue())))
                                        match = true;
                                else if (method.getReturnType().isAssignableFrom(Double.class))
                                    if (Objects.equals(method.invoke(item),Double.parseDouble(filterData.getValue())))
                                        match = true;
                                else if (method.getReturnType().isAssignableFrom(Float.class))
                                    if (Objects.equals(method.invoke(item),Float.parseFloat(filterData.getValue())))
                                        match = true;
                                
                                
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
                        else
                            match = true;
                        
                        if (!match)
                            break;
                    }

                }
            }
            else
                match = true;
            if (match)
                returnList.add(item);
        }
        return returnList;
    }
    
    
    public static UserInfo getUserByUsername(String username, UserInfoDAO userInfoDAO)
    {
        CriteriaBuilderCriteriaQueryRootTriple<UserInfo,UserInfo> triple = userInfoDAO.getCriteriaTriple();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Root<UserInfo> root = triple.getRoot();
        
        Predicate pred = cb.equal(root.get(UserInfo_.username), username);
        
        List<UserInfo> list = userInfoDAO.findByCriteria(triple, null, 0, -1, pred);
        if (list.isEmpty())
            return null;
        else
            return list.get(0);
    }
    
    public static void cleanUserInfo(UserInfo user)
    {
        user.setPassword("");
        user.setPassword2("");
    }
}
