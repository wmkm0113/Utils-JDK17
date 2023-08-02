package org.nervousync.test.utils;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervousync.annotations.beans.BeanProperty;
import org.nervousync.annotations.beans.BeanProperties;
import org.nervousync.annotations.beans.OutputConfig;
import org.nervousync.beans.converter.impl.basic.BigIntegerStringAdapter;
import org.nervousync.beans.converter.impl.basic.BooleanStringAdapter;
import org.nervousync.beans.converter.impl.beans.BeanStringAdapter;
import org.nervousync.beans.converter.impl.beans.JsonStringAdapter;
import org.nervousync.beans.converter.impl.beans.XmlStringAdapter;
import org.nervousync.beans.converter.impl.beans.YamlStringAdapter;
import org.nervousync.beans.converter.impl.blob.Base32Adapter;
import org.nervousync.beans.converter.impl.blob.Base64Adapter;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.enumerations.beans.DataFlow;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.BeanUtils;
import org.nervousync.utils.StringUtils;

import java.io.Serial;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public final class BeanTest extends BaseTest {

    @Test
    @Order(0)
    public void copyTest() {
        GenericBean genericBean = new GenericBean();
        BeanUtils.copyTo(generateGeneric(), genericBean);
        this.logger.info("Bean_Copy_Result", "generic",
                StringUtils.objectToString(genericBean, StringUtils.StringType.JSON, Boolean.TRUE));
        WrapperBean wrapperBean = new WrapperBean();
        BeanUtils.copyTo(generateWrapper(), wrapperBean);
        this.logger.info("Bean_Copy_Result", "wrapper",
                StringUtils.objectToString(wrapperBean, StringUtils.StringType.JSON, Boolean.TRUE));
    }

    @Test
    @Order(10)
    public void beanCopyToTest() {
        BeanTwo beanTwo = new BeanTwo();
        BeanThree beanThree = new BeanThree();
        BeanFour beanFour = new BeanFour();
        BeanFive beanFive = new BeanFive();
        BeanUtils.copyTo(generateBeanOne(), beanTwo, beanThree, beanFour, beanFive);
        this.logger.info("Bean_Copy_Result", "bean two",
                StringUtils.objectToString(beanTwo, StringUtils.StringType.JSON, Boolean.TRUE));
        this.logger.info("Bean_Copy_Result", "bean three",
                StringUtils.objectToString(beanThree, StringUtils.StringType.JSON, Boolean.TRUE));
        this.logger.info("Bean_Copy_Result", "bean four",
                StringUtils.objectToString(beanFour, StringUtils.StringType.JSON, Boolean.TRUE));
        this.logger.info("Bean_Copy_Result", "bean five",
                StringUtils.objectToString(beanFive, StringUtils.StringType.JSON, Boolean.TRUE));
    }

    @Test
    @Order(20)
    public void beanCopyFromTest() {
        BeanOne beanOne = new BeanOne();
        BeanUtils.copyFrom(beanOne, generateBeanTwo(), generateBeanThree(), generateBeanFour(), generateBeanFive());
        this.logger.info("Bean_Copy_Result", "",
                StringUtils.objectToString(beanOne, StringUtils.StringType.JSON, Boolean.TRUE));
        this.logger.info("Bean_Result", "Base64", new String(beanOne.getBeanOneBytes()));
        this.logger.info("Bean_Result", "Base32", new String(beanOne.getBase32Bytes()));
    }

    @Test
    @Order(30)
    public void beanCopyPropertiesTest() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("innerName", "Map name");
        dataMap.put("innerCode", 227);
        InnerBean innerBean = new InnerBean();
        BeanUtils.copyProperties(dataMap, innerBean);
        this.logger.info("Bean_Copy_Result", "",
                StringUtils.objectToString(innerBean, StringUtils.StringType.JSON, Boolean.TRUE));
    }

    @Test
    @Order(40)
    public void removeConfig() {
        BeanUtils.removeBeanConfig(BeanOne.class, BeanTwo.class, BeanThree.class, BeanFour.class, BeanFive.class);
    }

    private static GenericBean generateGeneric() {
        GenericBean origBean = new GenericBean();

        origBean.setTestBoolean(Boolean.TRUE);
        origBean.setTestByte((byte) 227);
        origBean.setTestChar('c');
        origBean.setTestDouble(2.27d);
        origBean.setTestFloat(2.27f);
        origBean.setTestLong(227L);
        origBean.setTestInt(227);
        origBean.setTestShort((short) 27);

        return origBean;
    }

    private static WrapperBean generateWrapper() {
        WrapperBean origBean = new WrapperBean();

        origBean.setTestBoolean(Boolean.TRUE);
        origBean.setTestByte((byte) 227);
        origBean.setTestCharacter('c');
        origBean.setTestDouble(2.27d);
        origBean.setTestFloat(2.27f);
        origBean.setTestLong(227L);
        origBean.setTestInteger(227);
        origBean.setTestShort((short) 27);
        origBean.setTestString("Wrapper String");

        return origBean;
    }

    private static BeanOne generateBeanOne() {
        BeanOne beanOne = new BeanOne();

        beanOne.setBigDecimal(new BigInteger("118780004724419996989429615970374578883474102254846312217089877064957697359398223816415470410537952768340788639333520018858322611268697324924284122922861084721701382204388754353490531964939619229367235555092986351934218308741070678024241774816537500600817420694474429968745794894382877413238661224377537419983"));
        beanOne.setBeanOneString("Bean One String");
        beanOne.setInnerBean(generateInnerBean());
        beanOne.setBeanOneBytes("Bean One Bytes".getBytes());
        beanOne.setBeanOneBoolean(Boolean.TRUE);
        beanOne.setDataBean(generateDataBean());

        return beanOne;
    }

    private static InnerBean generateInnerBean() {
        InnerBean innerBean = new InnerBean();

        innerBean.setInnerCode(227);
        innerBean.setInnerName("Inner name");

        return innerBean;
    }

    private static DataBean generateDataBean() {
        DataBean dataBean = new DataBean();

        dataBean.setDataInteger(227);
        dataBean.setDataString("Data String");

        return dataBean;
    }

    private static BeanTwo generateBeanTwo() {
        BeanTwo beanTwo = new BeanTwo();

        beanTwo.setBigDecimal(new BigInteger("118780004724419996989429615970374578883474102254846312217089877064957697359398223816415470410537952768340788639333520018858322611268697324924284122922861084721701382204388754353490531964939619229367235555092986351934218308741070678024241774816537500600817420694474429968745794894382877413238661224377537419983"));
        beanTwo.setBeanString("Bean Two String");
        beanTwo.setBeanInner(generateInnerBean());

        return beanTwo;
    }

    private static BeanThree generateBeanThree() {
        BeanThree beanThree = new BeanThree();

        beanThree.setBase64Data(StringUtils.base64Encode("Bean Three Base64".getBytes()));
        beanThree.setBooleanString("True");
        beanThree.setJsonString("{\"dataString\":\"Data String\",\"dataInteger\":227}");

        return beanThree;
    }

    private static BeanFour generateBeanFour() {
        BeanFour beanFour = new BeanFour();

        beanFour.setDecimalString("118780004724419996989429615970374578883474102254846312217089877064957697359398223816415470410537952768340788639333520018858322611268697324924284122922861084721701382204388754353490531964939619229367235555092986351934218308741070678024241774816537500600817420694474429968745794894382877413238661224377537419983");
        beanFour.setBeanFourBoolean(Boolean.TRUE);
        beanFour.setXmlString("<?xml version=\"1.0\" encoding=\"UTF-8\"?><data_bean><data_string>Data String</data_string><data_integer>227</data_integer></data_bean>");
        beanFour.setBase32Data(StringUtils.base32Encode("Bean Four Base32".getBytes()));

        return beanFour;
    }

    private static BeanFive generateBeanFive() {
        BeanFive beanFive = new BeanFive();

        beanFive.setBeanFiveBoolean(Boolean.TRUE);
        beanFive.setYamlString("dataString: \"Data String\"\ndataInteger: 227\n");
        beanFive.setOrigBean(generateDataBean());

        return beanFive;
    }

    @OutputConfig(type = StringUtils.StringType.JSON)
    public static final class GenericBean extends BeanObject {

	    @Serial
        private static final long serialVersionUID = -8250897818064674830L;
        private int testInt;
        private short testShort;
        private float testFloat;
        private long testLong;
        private double testDouble;
        private char testChar;
        private boolean testBoolean;
        private byte testByte;

        public int getTestInt() {
            return testInt;
        }

        public void setTestInt(int testInt) {
            this.testInt = testInt;
        }

        public short getTestShort() {
            return testShort;
        }

        public void setTestShort(short testShort) {
            this.testShort = testShort;
        }

        public float getTestFloat() {
            return testFloat;
        }

        public void setTestFloat(float testFloat) {
            this.testFloat = testFloat;
        }

        public long getTestLong() {
            return testLong;
        }

        public void setTestLong(long testLong) {
            this.testLong = testLong;
        }

        public double getTestDouble() {
            return testDouble;
        }

        public void setTestDouble(double testDouble) {
            this.testDouble = testDouble;
        }

        public char getTestChar() {
            return testChar;
        }

        public void setTestChar(char testChar) {
            this.testChar = testChar;
        }

        public boolean isTestBoolean() {
            return testBoolean;
        }

        public void setTestBoolean(boolean testBoolean) {
            this.testBoolean = testBoolean;
        }

        public byte getTestByte() {
            return testByte;
        }

        public void setTestByte(byte testByte) {
            this.testByte = testByte;
        }
    }

    @OutputConfig(type = StringUtils.StringType.JSON)
    public static final class WrapperBean extends BeanObject {

	    @Serial
        private static final long serialVersionUID = 8469520795055346340L;
        private Short testShort;
        private Integer testInteger;
        private Long testLong;
        private Double testDouble;
        private Float testFloat;
        private Byte testByte;
        private Boolean testBoolean;
        private Character testCharacter;
        private String testString;

        public Short getTestShort() {
            return testShort;
        }

        public void setTestShort(Short testShort) {
            this.testShort = testShort;
        }

        public Integer getTestInteger() {
            return testInteger;
        }

        public void setTestInteger(Integer testInteger) {
            this.testInteger = testInteger;
        }

        public Long getTestLong() {
            return testLong;
        }

        public void setTestLong(Long testLong) {
            this.testLong = testLong;
        }

        public Double getTestDouble() {
            return testDouble;
        }

        public void setTestDouble(Double testDouble) {
            this.testDouble = testDouble;
        }

        public Float getTestFloat() {
            return testFloat;
        }

        public void setTestFloat(Float testFloat) {
            this.testFloat = testFloat;
        }

        public Byte getTestByte() {
            return testByte;
        }

        public void setTestByte(Byte testByte) {
            this.testByte = testByte;
        }

        public Boolean getTestBoolean() {
            return testBoolean;
        }

        public void setTestBoolean(Boolean testBoolean) {
            this.testBoolean = testBoolean;
        }

        public Character getTestCharacter() {
            return testCharacter;
        }

        public void setTestCharacter(Character testCharacter) {
            this.testCharacter = testCharacter;
        }

        public String getTestString() {
            return testString;
        }

        public void setTestString(String testString) {
            this.testString = testString;
        }
    }

    @OutputConfig(type = StringUtils.StringType.JSON)
    public static final class InnerBean extends BeanObject {
	    @Serial
        private static final long serialVersionUID = 2456743666460180276L;
        private String innerName;
        private int innerCode;

        public String getInnerName() {
            return innerName;
        }

        public void setInnerName(String innerName) {
            this.innerName = innerName;
        }

        public int getInnerCode() {
            return innerCode;
        }

        public void setInnerCode(int innerCode) {
            this.innerCode = innerCode;
        }
    }

    @OutputConfig(type = StringUtils.StringType.JSON)
    public static final class BeanOne extends BeanObject {

	    @Serial
        private static final long serialVersionUID = 2148709510427702608L;
        @BeanProperty(dataFlow = DataFlow.IN, beanClass = BeanFour.class, targetField = "decimalString", converter = BigIntegerStringAdapter.class)
        @BeanProperty(dataFlow = DataFlow.OUT, beanClass = BeanTwo.class, targetField = "bigDecimal")
        @BeanProperty(dataFlow = DataFlow.OUT, beanClass = BeanFour.class, targetField = "decimalString", converter = BigIntegerStringAdapter.class)
        private BigInteger bigDecimal;
        @BeanProperty(dataFlow = DataFlow.IN, beanClass = BeanTwo.class, targetField = "beanString")
        @BeanProperties(@BeanProperty(dataFlow = DataFlow.OUT, beanClass = BeanTwo.class, targetField = "beanString"))
        private String beanOneString;
        @BeanProperty(dataFlow = DataFlow.IN, beanClass = BeanTwo.class, targetField = "beanInner")
        @BeanProperties(@BeanProperty(dataFlow = DataFlow.OUT, beanClass = BeanTwo.class, targetField = "beanInner"))
        private InnerBean innerBean;
        @BeanProperty(dataFlow = DataFlow.IN, beanClass = BeanThree.class, targetField = "base64Data", converter = Base64Adapter.class)
        @BeanProperty(dataFlow = DataFlow.OUT, beanClass = BeanThree.class, targetField = "base64Data", converter = Base64Adapter.class)
        @BeanProperty(dataFlow = DataFlow.OUT, beanClass = BeanFour.class, targetField = "base32Data", converter = Base32Adapter.class)
        private byte[] beanOneBytes;
        @BeanProperty(dataFlow = DataFlow.IN, beanClass = BeanFour.class, targetField = "base32Data", converter = Base32Adapter.class)
        private byte[] base32Bytes;
        @BeanProperty(dataFlow = DataFlow.IN, beanClass = BeanThree.class, targetField = "booleanString", converter = BooleanStringAdapter.class)
        @BeanProperty(dataFlow = DataFlow.OUT, beanClass = BeanThree.class, targetField = "booleanString", converter = BooleanStringAdapter.class)
        @BeanProperty(dataFlow = DataFlow.OUT, beanClass = BeanFour.class, targetField = "beanFourBoolean")
        @BeanProperty(dataFlow = DataFlow.OUT, beanClass = BeanFive.class, targetField = "beanFiveBoolean")
        private boolean beanOneBoolean;
        @BeanProperty(dataFlow = DataFlow.IN, beanClass = BeanThree.class, targetField = "jsonString", converter = BeanStringAdapter.class)
        @BeanProperty(dataFlow = DataFlow.OUT, beanClass = BeanThree.class, targetField = "jsonString", converter = JsonStringAdapter.class)
        @BeanProperty(dataFlow = DataFlow.OUT, beanClass = BeanFour.class, targetField = "xmlString", converter = XmlStringAdapter.class)
        @BeanProperty(dataFlow = DataFlow.OUT, beanClass = BeanFive.class, targetField = "yamlString", converter = YamlStringAdapter.class)
        private DataBean dataBean;
        @BeanProperty(dataFlow = DataFlow.IN, beanClass = BeanFive.class, targetField = "origBean")
        private DataBean fromBean;

        public BigInteger getBigDecimal() {
            return bigDecimal;
        }

        public void setBigDecimal(BigInteger bigDecimal) {
            this.bigDecimal = bigDecimal;
        }

        public String getBeanOneString() {
            return beanOneString;
        }

        public void setBeanOneString(String beanOneString) {
            this.beanOneString = beanOneString;
        }

        public InnerBean getInnerBean() {
            return innerBean;
        }

        public void setInnerBean(InnerBean innerBean) {
            this.innerBean = innerBean;
        }

        public byte[] getBeanOneBytes() {
            return beanOneBytes;
        }

        public void setBeanOneBytes(byte[] beanOneBytes) {
            this.beanOneBytes = beanOneBytes;
        }

        public byte[] getBase32Bytes() {
            return base32Bytes;
        }

        public void setBase32Bytes(byte[] base32Bytes) {
            this.base32Bytes = base32Bytes;
        }

        public boolean isBeanOneBoolean() {
            return beanOneBoolean;
        }

        public void setBeanOneBoolean(boolean beanOneBoolean) {
            this.beanOneBoolean = beanOneBoolean;
        }

        public DataBean getDataBean() {
            return dataBean;
        }

        public void setDataBean(DataBean dataBean) {
            this.dataBean = dataBean;
        }

        public DataBean getFromBean() {
            return fromBean;
        }

        public void setFromBean(DataBean fromBean) {
            this.fromBean = fromBean;
        }
    }

    @OutputConfig(type = StringUtils.StringType.JSON)
    public static final class BeanTwo extends BeanObject {
	    @Serial
        private static final long serialVersionUID = -3903310914229238786L;
        private BigInteger bigDecimal;
        private String beanString;
        private InnerBean beanInner;

        public BigInteger getBigDecimal() {
            return bigDecimal;
        }

        public void setBigDecimal(BigInteger bigDecimal) {
            this.bigDecimal = bigDecimal;
        }

        public String getBeanString() {
            return beanString;
        }

        public void setBeanString(String beanString) {
            this.beanString = beanString;
        }

        public InnerBean getBeanInner() {
            return beanInner;
        }

        public void setBeanInner(InnerBean beanInner) {
            this.beanInner = beanInner;
        }
    }

    @OutputConfig(type = StringUtils.StringType.JSON)
    public static final class BeanThree extends BeanObject {

	    @Serial
        private static final long serialVersionUID = 2676597737207266268L;
        private String base64Data;
        private String booleanString;
        private String jsonString;

        public String getBase64Data() {
            return base64Data;
        }

        public void setBase64Data(String base64Data) {
            this.base64Data = base64Data;
        }

        public String getBooleanString() {
            return booleanString;
        }

        public void setBooleanString(String booleanString) {
            this.booleanString = booleanString;
        }

        public String getJsonString() {
            return jsonString;
        }

        public void setJsonString(String jsonString) {
            this.jsonString = jsonString;
        }
    }

    @OutputConfig(type = StringUtils.StringType.JSON)
    public static final class BeanFour extends BeanObject {

	    @Serial
        private static final long serialVersionUID = 2131533619703353105L;
        private String decimalString;
        private String base32Data;
        private boolean beanFourBoolean;
        private String xmlString;

        public String getDecimalString() {
            return decimalString;
        }

        public void setDecimalString(String decimalString) {
            this.decimalString = decimalString;
        }

        public String getBase32Data() {
            return base32Data;
        }

        public void setBase32Data(String base32Data) {
            this.base32Data = base32Data;
        }

        public boolean isBeanFourBoolean() {
            return beanFourBoolean;
        }

        public void setBeanFourBoolean(boolean beanFourBoolean) {
            this.beanFourBoolean = beanFourBoolean;
        }

        public String getXmlString() {
            return xmlString;
        }

        public void setXmlString(String xmlString) {
            this.xmlString = xmlString;
        }
    }

    @OutputConfig(type = StringUtils.StringType.JSON)
    public static final class BeanFive extends BeanObject {

	    @Serial
        private static final long serialVersionUID = -2793808469846338003L;
        private boolean beanFiveBoolean;
        private String yamlString;
        private DataBean origBean;

        public boolean isBeanFiveBoolean() {
            return beanFiveBoolean;
        }

        public void setBeanFiveBoolean(boolean beanFiveBoolean) {
            this.beanFiveBoolean = beanFiveBoolean;
        }

        public String getYamlString() {
            return yamlString;
        }

        public void setYamlString(String yamlString) {
            this.yamlString = yamlString;
        }

        public DataBean getOrigBean() {
            return origBean;
        }

        public void setOrigBean(DataBean origBean) {
            this.origBean = origBean;
        }
    }

    @XmlRootElement(name = "data_bean")
    @XmlAccessorType(XmlAccessType.NONE)
    @OutputConfig(type = StringUtils.StringType.JSON)
    public static final class DataBean extends BeanObject {

	    @Serial
        private static final long serialVersionUID = -9087272100087754448L;
        @XmlElement(name = "data_string")
        private String dataString;
        @XmlElement(name = "data_integer")
        private Integer dataInteger;

        public String getDataString() {
            return dataString;
        }

        public void setDataString(String dataString) {
            this.dataString = dataString;
        }

        public Integer getDataInteger() {
            return dataInteger;
        }

        public void setDataInteger(Integer dataInteger) {
            this.dataInteger = dataInteger;
        }
    }
}
