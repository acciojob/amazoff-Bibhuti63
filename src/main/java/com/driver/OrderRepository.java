package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {

    //database//Hashmap
    HashMap<String,Order> orderDB;
    HashMap<String,DeliveryPartner> partnerDB;
    HashMap<String, List<String>> pairDB;
    HashSet<String> isOrderNotAssigned;

    public OrderRepository() {
        orderDB=new HashMap<>();
        partnerDB=new HashMap<>();
        pairDB=new HashMap<>();
        isOrderNotAssigned =new HashSet<>();

    }

    public void addOrder(Order order) {
        orderDB.put(order.getId(),order);
        isOrderNotAssigned.add(order.getId());
    }

    public void addPartner(String partnerId) {
        DeliveryPartner d1=new DeliveryPartner(partnerId);
        partnerDB.put(partnerId,d1);
    }


    public void addOrderPartnerPair(String orderId, String partnerId) {
        List<String> list=pairDB.getOrDefault(partnerId,new ArrayList<>());
        list.add(orderId);
        pairDB.put(partnerId,list);
        partnerDB.get(partnerId).setNumberOfOrders(partnerDB.get(partnerId).getNumberOfOrders()+1);

        isOrderNotAssigned.remove(orderId);
    }

    public Order getOrderById(String orderId) {
        return orderDB.get(orderId);
    }


    public DeliveryPartner getPartnerById(String partnerId) {
        return partnerDB.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
//        return partnerDB.get(partnerId).getNumberOfOrders();
        return pairDB.get(partnerId).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        List<String> list=pairDB.getOrDefault(partnerId,new ArrayList<>());
        return list;
    }

    public List<String> getAllOrders() {
        List<String> list=new ArrayList<>();
        for(String s: orderDB.keySet()){
            list.add(s);
        }
        return list;
    }

    public Integer getCountOfUnassignedOrders() {
       return isOrderNotAssigned.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        Integer count=0;
        //converting given string time to integer
        String arr[]=time.split(":"); //12:45
        int hr=Integer.parseInt(arr[0]);
        int min=Integer.parseInt(arr[1]);

        int total=(hr*60+min);

        List<String> list=pairDB.getOrDefault(partnerId,new ArrayList<>());
        if(list.size()==0)return 0; //no order assigned to partnerId

        for(String s: list){
            Order currentOrder=orderDB.get(s);
            if(currentOrder.getDeliveryTime()>total){
                count++;
            }
        }

        return count;

    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        //return in HH:MM format
        String str="00:00";
        int max=0;

        List<String>list=pairDB.getOrDefault(partnerId,new ArrayList<>());
        if(list.size()==0)return str;
        for(String s: list){
            Order currentOrder=orderDB.get(s);
            max=Math.max(max,currentOrder.getDeliveryTime());
        }
        //convert int to string (140-> 02:20)
        int hr=max/60;
        int min=max%60;

        if(hr<10){
            str="0"+hr+":";
        }else{
            str=hr+":";
        }

        if(min<10){
            str+="0"+min;
        }
        else{
            str+=min;
        }
        return str;


    }

    public void deletePartnerById(String partnerId) {
        if(!pairDB.isEmpty()){
            isOrderNotAssigned.addAll(pairDB.get(partnerId));
        }
//        if(!partnerDB.containsKey(partnerId)){
//            return;
//        }
        //removing form partnerDB
        partnerDB.remove(partnerId);

//        List<String>list=pairDB.getOrDefault(partnerId,new ArrayList<>());
//        if(list.size()==0)return;
//        isOrderAssigned.addAll(pairDB.get(partnerId));

        //remove form the pairDB
        pairDB.remove(partnerId);

    }

    public void deleteOrderById(String orderId) {
        //Delete an order and the corresponding partner should be unassigned
        if(orderDB.containsKey(orderId)){
            if(isOrderNotAssigned.contains(orderId)){
                isOrderNotAssigned.remove(orderId);
            }
            else{

                for(String str : pairDB.keySet()){
                    List<String> list=pairDB.get(str);
                    list.remove(orderId);
                }
            }
        }


    }
}
