
import java.io.*;

public class ByteNode {
    private int label;
    private int freq;
    private BooleanList code;
    private boolean haveFather;
    private ByteNode father;
    private ByteNode left;
    private ByteNode right;
    
    public ByteNode(int lb,int fr){
        label=lb;
        freq=fr;
        code=new BooleanList(null);
        haveFather=false;
        father=null;
        left=null;
        right=null;
    }
    
    public int getLabel(){
        return label;
    }
    
    public int getFreq(){
        return freq;
    }
    
    public boolean isHaveFather(){
        return haveFather;
    }
    
    public void setFather(ByteNode f){
        father=f;
        haveFather=true;
    }
    
    public ByteNode getFather(){
        return father;
    }
    
    public void setLeft(ByteNode l){
        left=l;
        left.setFather(this);
    }
    
    public ByteNode getLeft(){
        return left;
    }
    
    public void setRihgt(ByteNode r){
        right=r;
        right.setFather(this);
    }
    
    public ByteNode getRight(){
        return right;
    }
    
    public BooleanList getCode(){
        return code;
    }
    
    public void setCode(BooleanList bl){
        code=bl;
    }
    
    public void writeCode(BufferedWriter wr){
        if(code==null){
            throw new NullPointerException();
        }
        
        try{
            wr.write("%");
            wr.write(String.valueOf(label));
            wr.write(":");
            BooleanNode cr=code.getRoot();
            if(cr.getValue()) wr.write("1");
            else wr.write("0");
            
            while(cr.getNext()!=null){
                cr=cr.getNext();
                if(cr.getValue()) wr.write("1");
                else wr.write("0");
            }
            
            wr.write("%\n");
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}
