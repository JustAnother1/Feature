package de.nomagic.puzzler.Environment;

import org.jdom2.Element;

public class EnvironmentStub implements Environment
{
    private String[] PlatformParts;
    private String BuildSystemType = null;

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

    @Override
    public String getRootApi()
    {
        return null;
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
