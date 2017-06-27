package de.nomagic.puzzler.solution;

public enum DataType {
    DT_void(0), DT_bool(1), DT_int(2);

    private int value;

    private DataType(int idx)
    {
        this.value = idx;
    }

    public static DataType decode(String type)
    {
        if(null == type)
        {
            return DataType.DT_void;
        }
        switch(type)
        {
        case "void" : return DataType.DT_void;
        case "bool" : return DataType.DT_bool;
        case "int" : return DataType.DT_int;
        default: return DataType.DT_void;
        }
    }

    public String toString()
    {
        switch(value)
        {
        case 0: return "void"; // DT_void
        case 1: return "bool"; // DT_bool
        case 2: return "int"; // DT_int
        default: return "void";
        }
    }

    public int getValue()
    {
        return value;
    }
}
