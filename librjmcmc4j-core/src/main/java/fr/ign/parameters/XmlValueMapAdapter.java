package fr.ign.parameters;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * XML Value map adapter.
 *
 */
public class XmlValueMapAdapter extends XmlAdapter<String, Object> {
  
  public XmlValueMapAdapter() {
    super();
  }
  
  @Override
  public String marshal(Object parameter) throws Exception {
    return parameter.toString();
  }

  /**
   * 
   */
  @Override
  public Object unmarshal(String arg0) throws Exception {
    return arg0;
  }

}
