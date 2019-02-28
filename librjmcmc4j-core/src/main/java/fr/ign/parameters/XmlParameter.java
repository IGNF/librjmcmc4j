package fr.ign.parameters;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "param")
public class XmlParameter extends XmlParameterComponent {
  
  @XmlAttribute(name = "key")
  private String key;
  
  @XmlJavaTypeAdapter(XmlValueMapAdapter.class)
  @XmlAttribute(name = "value")
  private Object value;
  
  public XmlParameter() {
  }
  
  public XmlParameter(String k, Object v) {
    this.key = k;
    this.value = v;
  }
  
  public String getKey() {
    return key;
  }
  
  public Object getValue() {
    return value;
  }
  
}
