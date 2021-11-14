package com.eidotab.smartab.Models;

import java.util.Comparator;


public class MesaSorter implements Comparator<Mensaje>
{

    public int compare(Mensaje one, Mensaje another){
        return one.getRemitente().compareTo(another.getRemitente());
    }

}
