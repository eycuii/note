package demo.protocol;

public class MyProtocol {
	/** 
     * 消息的开头的信息标志 
     */  
    private final int headData = 0x11;
    /** 
     * 消息的长度 
     */  
    private int contentLength;  
    /** 
     * 消息的内容 
     */  
    private byte[] content;  
  
    /** 
     * 初始化
     *  
     * @param contentLength 协议里面，消息数据的长度 
     * @param content 协议里面，消息的数据 
     */  
    public MyProtocol(int contentLength, byte[] content) {  
        this.contentLength = contentLength;  
        this.content = content;  
    }  
  
    public int getHeadData() {  
        return headData;  
    }  
  
    public int getContentLength() {  
        return contentLength;  
    }  
  
    public void setContentLength(int contentLength) {  
        this.contentLength = contentLength;  
    }  
  
    public byte[] getContent() {  
        return content;  
    }  
  
    public void setContent(byte[] content) {  
        this.content = content;  
    }  
  
    @Override  
    public String toString() {  
        return "SmartCarProtocol [head_data=" + headData + ", contentLength="  
                + contentLength + ", content=" + new String(content) + "]";  
    }
}
