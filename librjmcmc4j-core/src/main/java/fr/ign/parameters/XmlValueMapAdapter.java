package fr.ign.parameters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * 
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
