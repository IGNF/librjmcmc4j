package fr.ign.parameters;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "parameters")
public class XmlParameters extends XmlParameterComponent {

	@XmlElementRef(name = "parameter")
	public List<XmlParameterComponent> entry = new ArrayList<XmlParameterComponent>();

	@XmlAttribute(name = "description")
	public String description;

	/**
	 * Constructor.
	 */
	public XmlParameters() {
		this.description = "";
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void add(XmlParameterComponent p) {
		entry.add(p);
	}

	public boolean set(String key, Object c) {
		
		boolean modificationDone = false;
		if (entry != null) {
			int nbEntry = entry.size();
			for (int i=0;i<nbEntry;i++) {
				XmlParameterComponent paramComp  = entry.get(i);
				if (paramComp instanceof XmlParameter) {
					// System.out.println("parameter");
					if (((XmlParameter) paramComp).getKey().equals(key)) {
						 entry.remove(paramComp);
						 i--;
						 nbEntry--;
					} else {
						// System.out.println(((Parameter)paramComp).getKey());
					}
				} else if (paramComp instanceof XmlParameters) {
					// System.out.println("parameters");
						((XmlParameters) paramComp).set(key,c);
						modificationDone= true;
				}
			}
		}
	
		if(! modificationDone){
			XmlParameter p = new XmlParameter(key, c);
			entry.add(p);
		}
	
		
		return modificationDone;
	}

	public Object get(String key) {
		// System.out.println("key = " + key);
		if (entry != null) {
			for (XmlParameterComponent paramComp : entry) {
				if (paramComp instanceof XmlParameter) {
					// System.out.println("parameter");
					if (((XmlParameter) paramComp).getKey().equals(key)) {
						// System.out.println("--" +
						// ((Parameter)paramComp).getValue());
						return ((XmlParameter) paramComp).getValue();
					} else {
						// System.out.println(((Parameter)paramComp).getKey());
					}
				} else if (paramComp instanceof XmlParameters) {
					// System.out.println("parameters");
					Object retour = ((XmlParameters) paramComp).get(key);
					if (retour != null) {
						return retour;
					}
					// return ((Parameters) paramComp).get(key);
				}
			}
		}
		return null;
	}

	/**
	 * On cherche dans les param de parametres qui a la description, celui qui a
	 * key
	 * 
	 * @param description
	 * @param key
	 * @return
	 */
	public Object get(String desc, String key) {
		// System.out.println("key = " + key);
		if (entry != null) {
			for (XmlParameterComponent paramComp : entry) {
				if (paramComp instanceof XmlParameters) {
					if (((XmlParameters) paramComp).description.equals(desc)) {
						return ((XmlParameters) paramComp).get(key);
					}
				}
			}
		}
		return null;
	}

	public String getString(String name) {
		Object value = this.get(name);
		if (value == null) {
			return "";
		}
		return value.toString();
	}

	public String getString(String desc, String name) {
		Object value = this.get(desc, name);
		if (value == null) {
			return "";
		}
		return value.toString();
	}

	public boolean getBoolean(String name) {
		Object value = this.get(name);
		if (value == null) {
			return false;
		}
		return Boolean.parseBoolean(value.toString());
	}

	public boolean getBoolean(String desc, String name) {
		Object value = this.get(desc, name);
		if (value == null) {
			return false;
		}
		return Boolean.parseBoolean(value.toString());
	}

	public double getDouble(String name) {
		Object value = this.get(name);
		if (value == null) {
			return 0;
		}
		return Double.parseDouble(value.toString());
	}

	public int getInteger(String name) {
		Object value = this.get(name);
		if (value == null) {
			return 0;
		}
		return Integer.parseInt(value.toString());
	}

	public long getLong(String name) {
    Object value = this.get(name);
    if (value == null) {
      return 0;
    }
    return Long.parseLong(value.toString());
  }

	public float getFloat(String name) {
		Object value = this.get(name);
		if (value == null) {
			return 0;
		}
		return Float.parseFloat(value.toString());
	}

	public List<Integer> getIntegerList(String name) {
		Object value = this.get(name);
		if (value == null) {
			return new ArrayList<>();
		}
		String[] values = value.toString().split(",");
		List<Integer> result = new ArrayList<>(values.length);
		for (int i = 0; i < values.length; i++) {
			result.add(Integer.parseInt(values[i]));
		}
		return result;
	}
	public List<Double> getDoubleList(String name) {
		Object value = this.get(name);
		if (value == null) {
			return new ArrayList<>();
		}
		String[] values = value.toString().split(",");
		List<Double> result = new ArrayList<>(values.length);
		for (int i = 0; i < values.length; i++) {
			result.add(Double.parseDouble(values[i]));
		}
		return result;
	}
}
