package de.nomagic.puzzler.Environment;

import org.jdom2.Element;

public class EnvironmentStub implements Environment
{
    private String[] PlatformParts;
    private String BuildSystemType = null;
    private String rootApi = null;

    public EnvironmentStub()
    {
    }

    @Override
    public boolean loadFromElement(Element environmentRoot)
    {
        return false;
    }

    @Override
    public String[] getPlatformParts()
    {
        return PlatformParts;
    }

    public void setBuildSystemType(String val)
    {
        BuildSystemType = val;
    }

    @Override
    public String getBuldSystemType()
    {
        return BuildSystemType;
    }

    public void setRootApi(String nameApi)
    {
    	rootApi = nameApi;
    }
    
    @Override
    public String getRootApi()
    {
        return rootApi;
    }

    @Override
    public boolean provides(String name)
    {
        return false;
    }

    @Override
    public Element getAlgorithmCfg(String algoName)
    {
        return null;
    }

    @Override
    public Element[] getConfigFile(String postfix, String RootElementName)
    {
        return null;
    }

    public void SetPlatformParts(String[] val)
    {
        PlatformParts = val;
    }

}
