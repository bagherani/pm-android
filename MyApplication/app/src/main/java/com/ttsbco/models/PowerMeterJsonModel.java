package com.ttsbco.models;

public class PowerMeterJsonModel
{
    private boolean hasError;
    private String message;

    public boolean isPowerMeter()
    {
        return isPowerMeter;
    }

    public void setPowerMeter(boolean powerMeter)
    {
        isPowerMeter = powerMeter;
    }

    public double getMin()
    {
        return min;
    }

    public void setMin(double min)
    {
        this.min = min;
    }

    private double min;


    public double getMax()
    {
        return max;
    }

    public void setMax(double max)
    {
        this.max = max;
    }

    private double max;

    public double getVal()
    {
        return val;
    }

    public void setVal(double val)
    {
        this.val = val;
    }

    private double val;

    private boolean isPowerMeter;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    private String name;


    public String getPowerMeterName()
    {
        return powerMeterName;
    }

    public void setPowerMeterName(String powerMeterName)
    {
        this.powerMeterName = powerMeterName;
    }

    private String powerMeterName;


    private int address;

    public void setAddresss(int address)
    {
        this.address = address;
    }


    public int getAddress()
    {
        return address;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    private int id;


    public void setHasError(boolean hasError)
    {
        this.hasError = hasError;
    }

    public boolean hasError()
    {
        return hasError;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return this.message;
    }
}
